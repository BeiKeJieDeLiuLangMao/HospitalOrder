package cy.service;

import cy.exception.SessionInvalidException;
import cy.model.BaseResponse;
import cy.model.RegisterOrderTask;
import cy.model.response.AliPayParams;
import cy.model.response.LockAptPutSpotReg;
import cy.model.response.QueryAptSpot;
import cy.utils.NamedThreadFactory;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author Chen Yang/CL10060-N/chen.yang@linecorp.com
 */
@Service
@Slf4j
public class OrderService {

    private static final int THREAD_POOL_SIZE = 100;
    private static final long THREAD_ALIVE_SECONDS = 1;
    private static final String THREAD_POOL_THREAD_PREFIX = "OrderService";
    private static final String LOCK_APT_PATH = "med/reg/lockAptReg";
    private static final String PUT_SPOT_PATH = "med/reg/putSpotReg";
    private static final int QUERY_INTERVAL_MILLIS = 900;
    private static final int NINE_HOURS = 9 * 60 * 60 * 1000;

    @Autowired
    private HttpClientService httpService;
    @Autowired
    private RequestService requestService;
    private ExecutorService executorService;
    private boolean serviceStopped;

    @PostConstruct
    public void init() {
        executorService = new ThreadPoolExecutor(
            THREAD_POOL_SIZE,
            THREAD_POOL_SIZE,
            THREAD_ALIVE_SECONDS,
            TimeUnit.MICROSECONDS,
            new LinkedBlockingDeque<>(),
            new NamedThreadFactory(THREAD_POOL_THREAD_PREFIX, THREAD_POOL_SIZE));
    }

    @PreDestroy
    public void stop() {
        serviceStopped = true;
    }

    void submitOrderTask(RegisterOrderTask task) {
        executorService.submit(() -> loopOrder(task));
    }

    private void loopOrder(RegisterOrderTask task) {
        log.info("Begin order loop task, {}", task);
        task.setStartTime(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date(System.currentTimeMillis())));
        try {
            Date aptDate = new SimpleDateFormat("yyyy-MM-dd").parse(task.getAptDate());
            while (!serviceStopped && !task.isFinish()) {
                try {
                    switch (task.getState()) {
                        // 提交任务
                        case 0:
                            // 查询余票开始
                        case 1:
                            BaseResponse<List<QueryAptSpot>> queryApt;
                            if (isToday(task.getAptDate())) {
                                task.setAlreadyShowAptTooLateWarning(false);
                                queryApt = requestService.querySpot(task.getLogin().getSessionid(), task.getRegLevel(),
                                    "", "", task.getDeptId());
                            } else if (new Date(System.currentTimeMillis() + NINE_HOURS).after(aptDate)) {
                                notifyAptTooLate(task);
                                Thread.sleep(QUERY_INTERVAL_MILLIS);
                                continue;
                            } else {
                                queryApt = requestService.queryApt(task.getLogin().getSessionid(), task.getRegLevel(),
                                    task.getAptDate(), "", "", task.getDeptId());
                            }
                            if (queryApt.checkCode()) {
                                if (task.getState() == 0) {
                                    task.setState(1);
                                    task.sendTaskStatus();
                                }
                                task.getQueryTime().incrementAndGet();
                                List<QueryAptSpot> queryData = queryApt.getData();
                                List<QueryAptSpot.RegData> validRegs = new ArrayList<>();
                                queryData.forEach(data -> validRegs.addAll(data.getRegTokens().values().stream().filter(reg ->
                                    task.getRegTokens().contains(reg.getRegToken()) && reg.getValidNum() > 0)
                                    .collect(Collectors.toList())));
                                task.setValidNum(validRegs.stream().mapToInt(QueryAptSpot.RegData::getValidNum).sum());
                                validRegs.sort(Comparator.comparingInt(QueryAptSpot.RegData::getValidNum).reversed());
                                if (validRegs.size() > 0) {
                                    task.getTryOrderTime().incrementAndGet();
                                    BaseResponse<LockAptPutSpotReg> lockAptOrder;
                                    if (new Date(System.currentTimeMillis()).after(aptDate)) {
                                        lockAptOrder = putSpotOrder(task.getLogin().getSessionid(), task.getOrderChannel(),
                                            task.getMedCardId(), task.getMiBind(), task.getLogin().getUserid(),
                                            validRegs.get(0).getRegToken());
                                    } else if (new Date(System.currentTimeMillis() + NINE_HOURS).after(aptDate)) {
                                        notifyAptTooLate(task);
                                        Thread.sleep(QUERY_INTERVAL_MILLIS);
                                        continue;
                                    } else {
                                        lockAptOrder = lockAptOrder(task.getLogin().getSessionid(), task.getOrderChannel(),
                                            task.getMedCardId(), task.getMiBind(), task.getLogin().getUserid(),
                                            validRegs.get(0).getRegToken());
                                    }
                                    if (lockAptOrder.checkCode()) {
                                        if (lockAptOrder.getData().getNeedToPay()) {
                                            task.setOrderId(lockAptOrder.getData().getOrderId());
                                            task.setState(2);
                                        } else {
                                            task.setState(4);
                                            task.success();
                                        }
                                        task.sendTaskStatus();
                                    } else {
                                        task.sendTaskStatus();
                                        task.getClient().sendEvent("orderException", lockAptOrder.getMsg());
                                        Thread.sleep(QUERY_INTERVAL_MILLIS);
                                    }
                                } else {
                                    task.sendTaskStatus();
                                    Thread.sleep(QUERY_INTERVAL_MILLIS);
                                }
                            } else {
                                task.getClient().sendEvent("orderException", queryApt.getMsg());
                                Thread.sleep(QUERY_INTERVAL_MILLIS);
                            }
                            break;
                        // 下单成功
                        case 2:
                            BaseResponse<AliPayParams> payResponse = getOrderPayParams(task.getLogin().getUserid(),
                                task.getLogin().getSessionid(), task.getOrderId());
                            if (payResponse.checkCode()) {
                                task.setAliPayParams(payResponse.getData());
                                task.setState(3);
                                task.success();
                                task.sendTaskStatus();
                            } else {
                                task.getClient().sendEvent("orderException", payResponse.getMsg());
                            }
                            break;
                        default:
                            log.error("Unhandled state, {}", task.getState());
                            return;
                    }
                } catch (SessionInvalidException e) {
                    task.fail("Session 过期请重新登录");
                    task.getClient().sendEvent("loginExpired");
                } catch (Exception e) {
                    log.warn("Error happen", e);
                }
            }
        } catch (ParseException e) {
            log.error("parse apt date failed", e);
        }
        log.info("Finish order loop task, {}", task);
        requestService.taskFinished(task);
    }

    private void notifyAptTooLate(RegisterOrderTask task) {
        if (!task.isAlreadyShowAptTooLateWarning()) {
            task.getClient().sendEvent("orderInfo", "下午 3 点后，无法挂次日的号，挂号任务会在次日 0 时重启，请不要取消挂号");
            task.setAlreadyShowAptTooLateWarning(true);
        }
    }

    private boolean isToday(String dateString) {
        return new SimpleDateFormat("yyyy-MM-dd").format(new Date(System.currentTimeMillis())).equals(dateString);
    }

    private BaseResponse<LockAptPutSpotReg> lockAptOrder(String sessionId, String orderChannel, String medCardId,
        String miBind,
        String userId, String regToken) {
        Map<String, String> params = buildRegParams(orderChannel, medCardId, miBind, userId, regToken);
        return httpService.post2BchApiForObject(LOCK_APT_PATH, sessionId, params, LockAptPutSpotReg.class);
    }

    private BaseResponse<AliPayParams> getOrderPayParams(String userId, String sessionId, String orderId) {
        Map<String, String> params = new HashMap<>();
        params.put("userid", userId);
        params.put("orderId", orderId);
        try {
            params.put("deviceIp", InetAddress.getLocalHost().getHostAddress());
        } catch (UnknownHostException e) {
            log.error("Get ip address failed", e);
        }
        return httpService.post2BchApiForObject("med/reg/aliAppPay", sessionId, params, AliPayParams.class);
    }

    private BaseResponse<LockAptPutSpotReg> putSpotOrder(String sessionId, String orderChannel, String medCardId,
        String miBind,
        String userId, String regToken) {
        Map<String, String> params = buildRegParams(orderChannel, medCardId, miBind, userId, regToken);
        return httpService.post2BchApiForObject(PUT_SPOT_PATH, sessionId, params, LockAptPutSpotReg.class);
    }

    private Map<String, String> buildRegParams(String orderChannel, String medCardId, String miBind, String userId,
        String regToken) {
        Map<String, String> params = new HashMap<>(10);
        params.put("orderChannel", orderChannel);
        params.put("medCardId", medCardId);
        params.put("miBind", miBind);
        params.put("userid", userId);
        params.put("regToken", regToken);
        return params;
    }
}
