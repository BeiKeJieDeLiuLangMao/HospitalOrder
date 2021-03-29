package cy.service;

import com.corundumstudio.socketio.SocketIOClient;
import com.corundumstudio.socketio.annotation.OnDisconnect;
import cy.handler.SessionHandler;
import cy.jooq.Tables;
import cy.model.BaseResponse;
import cy.model.RegisterOrderTask;
import cy.model.ResponseCode;
import cy.model.request.LoadRegList;
import cy.model.response.DeptInfo;
import cy.model.response.Doctor;
import cy.model.response.MedCard;
import cy.model.response.PasswdLogin;
import cy.model.response.QueryAptSpot;
import cy.model.response.Spec;
import cy.model.response.UserInfo;
import cy.utils.Crypt;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import javax.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.RandomUtils;
import org.apache.commons.lang3.StringUtils;
import org.jooq.DSLContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author Chen Yang/CL10060-N/chen.yang@linecorp.com
 */
@Service
@Slf4j
public class RequestService {

    private static final String SECRET_BASE = "WRt4DEtiu6";
    private static final String LOGIN_PATH = "comm/user/passwdLogin";
    static final String DATA_FORMAT = "yyyy-MM-dd";
    private static final long ONE_MONTH = 30 * 24 * 60 * 60 * 1000;

    private Map<UUID/*sessionId*/, RegisterOrderTask> registerTasks = new ConcurrentHashMap<>();
    private final AtomicInteger orderId = new AtomicInteger(0);
    @Autowired
    private HttpClientService httpService;
    @Autowired
    private OrderService orderService;
    @Autowired
    private SessionHandler sessionHandler;
    @Autowired
    private DSLContext dslContext;

    @PostConstruct
    public void init() {
        dslContext.selectOne().fetch();
    }

    public BaseResponse<PasswdLogin> login(String loginId, String password) {
        Map<String, String> params = new HashMap<>(20);
        String randomManufacturer = RandomStringUtils.randomAscii(RandomUtils.nextInt(5, 15));
        String randomModel = RandomStringUtils.randomAscii(RandomUtils.nextInt(9, 19));
        List<String> versionItems = Arrays.asList("4.0", "4.4", "5.0.0", "5.0.3", "6.0", "6.0.1", "7.0", "7.0.2");
        params.put("loginid", loginId);
        params.put("password", Crypt.xxteaEncode(password, SECRET_BASE));
        params.put("appVersion", "3.1.1");
        params.put("deviceOS", "android");
        // VERSION.RELEASE
        params.put("osVersion", versionItems.get(RandomUtils.nextInt(0, 8)));
        params.put("channel", "bch");
        params.put("deviceID", getUniquePsuedoID(randomManufacturer, randomModel));
        // Build.MANUFACTURER
        params.put("deviceVendor", randomManufacturer);
        // Build.MODEL,VERSION.SDK_INT
        params.put("deviceModel", randomModel + "," + RandomUtils.nextInt(16, 27));
        log.info("Login, id: {}, password: {}", loginId, password);
        return httpService.post2BchApiForObject(LOGIN_PATH, StringUtils.EMPTY, params, PasswdLogin.class);
    }

    private static String getUniquePsuedoID(String randomManufacturer, String randomModel) {
        String uuidKey = "35" +
            // Build.BOARD.length() % 10
            RandomUtils.nextInt(0, 10) +
            // Build.BRAND.length() % 10
            RandomUtils.nextInt(0, 10) +
            // Build.CPU_ABI.length() % 10
            RandomUtils.nextInt(0, 10) +
            // Build.DEVICE.length() % 10
            RandomUtils.nextInt(0, 10) +
            // Build.DISPLAY.length() % 10
            RandomUtils.nextInt(0, 10) +
            // Build.HOST.length() % 10
            RandomUtils.nextInt(0, 10) +
            // Build.ID.length() % 10
            RandomUtils.nextInt(0, 10) +
            randomManufacturer.length() % 10 +
            randomModel.length() % 10 +
            // Build.PRODUCT.length() % 10
            RandomUtils.nextInt(0, 10) +
            // Build.TAGS.length() % 10
            RandomUtils.nextInt(0, 10) +
            // Build.TYPE.length() % 10
            RandomUtils.nextInt(0, 10) +
            // Build.USER.length() % 10
            RandomUtils.nextInt(0, 10);
        try {
            // serialNumber
            String serialNumber = RandomStringUtils.randomAscii(RandomUtils.nextInt(9, 25));
            UUID uuid = new UUID((long) uuidKey.hashCode(), (long) serialNumber.hashCode());
            return uuid.toString();
        } catch (Exception var3) {
            return (new UUID((long) uuidKey.hashCode(), (long) "bchapp".hashCode())).toString();
        }
    }

    public BaseResponse<List<DeptInfo>> queryDepartments(SocketIOClient client) {
        PasswdLogin login = sessionHandler.getLogin(client.getSessionId());
        return httpService.post2BchApiForList("pub/org/depts", login.getSessionid(), null, DeptInfo.class);
    }

    public BaseResponse<List<Doctor>> queryDoctors(SocketIOClient client) {
        PasswdLogin login = sessionHandler.getLogin(client.getSessionId());
        return httpService.post2BchApiForList("pub/org/doctors", login.getSessionid(), null, Doctor.class);
    }

    public BaseResponse<List<Spec>> loadSpec(SocketIOClient client) {
        PasswdLogin login = sessionHandler.getLogin(client.getSessionId());
        Map<String, String> params = new HashMap<>(2);
        params.put("level", "1");
        return httpService.post2BchApiForList("pub/org/specs", login.getSessionid(),
            params, Spec.class);
    }

    public BaseResponse<List<MedCard>> queryUserMedCard(SocketIOClient client) {
        PasswdLogin login = sessionHandler.getLogin(client.getSessionId());
        Map<String, String> params = new HashMap<>();
        params.put("default", "1");
        params.put("userid", login.getUserid());
        return httpService.post2BchApiForList("query/card/medCards", login.getSessionid(), params, MedCard.class);
    }

    public BaseResponse<UserInfo> queryUserInfo(SocketIOClient client) {
        PasswdLogin login = sessionHandler.getLogin(client.getSessionId());
        Map<String, String> params = new HashMap<>();
        params.put("phone", "1");
        params.put("userid", login.getUserid());
        return httpService.post2BchApiForObject("query/user/info", login.getSessionid(), params, UserInfo.class);
    }

    public BaseResponse<List<QueryAptSpot>> loadRegList(SocketIOClient client, LoadRegList request) {
        PasswdLogin login = sessionHandler.getLogin(client.getSessionId());
        if (new SimpleDateFormat(DATA_FORMAT).format(new Date()).equals(request.getDate())) {
            return querySpot(login.getSessionid(), request.getRegLevel(), "", "",
                request.getDepartment());
        } else {
            return queryApt(login.getSessionid(), request.getRegLevel(), request.getDate(), "", "",
                request.getDepartment());
        }
    }

    @SuppressWarnings("unused")
    public BaseResponse<Boolean> setUmAlias(String sessionId, String userId, String type) {
        Map<String, String> params = new HashMap<>(10);
        params.put("alias", userId);
        params.put("type", type);
        params.put("userid", userId);
        log.info("Set um alias, session: {}, userId: {}, type: {}", sessionId, userId, type);
        return httpService.post2BchApiForObject("comm/user/setUmAlias", sessionId, params, Boolean.class);
    }

    BaseResponse<List<QueryAptSpot>> querySpot(String sessionId, String regLevel, String specCode, String doctorCode,
        String deptId) {
        Map<String, String> params = buildQueryParams(regLevel, null, specCode, doctorCode, deptId);
        return httpService.post2BchApiForList("cache/pool/querySpot", sessionId, params, QueryAptSpot.class);
    }

    BaseResponse<List<QueryAptSpot>> queryApt(String sessionId, String regLevel, String aptDate, String specCode,
        String doctorCode, String deptId) {
        Map<String, String> params = buildQueryParams(regLevel, aptDate, specCode, doctorCode, deptId);
        return httpService.post2BchApiForList("cache/pool/queryApt", sessionId, params, QueryAptSpot.class);
    }

    private Map<String, String> buildQueryParams(String regLevel, String aptDate, String specCode, String doctorCode,
        String deptId) {
        Map<String, String> params = new HashMap<>(10);
        params.put("regLevel", regLevel);
        params.put("specCode", specCode);
        params.put("doctorCode", doctorCode);
        params.put("deptId", deptId);
        if (aptDate != null) {
            params.put("aptDate", aptDate);
        }
        return params;
    }

    @Transactional(rollbackFor = Throwable.class)
    public BaseResponse<RegisterOrderTask> registerOrderTask(SocketIOClient client, RegisterOrderTask request) {
        if (registerTasks.containsKey(client.getSessionId())) {
            return new BaseResponse<>(ResponseCode.FAILED.getCode(), null, "最多只能有一个预约任务");
        } else {
            PasswdLogin login = sessionHandler.getLogin(client.getSessionId());
            cy.jooq.tables.pojos.UserInfo userInfo = dslContext
                .selectFrom(Tables.USER_INFO)
                .where(Tables.USER_INFO.ID.eq(login.getUserid()))
                .forUpdate()
                .fetchOneInto(cy.jooq.tables.pojos.UserInfo.class);
            if (isUsable(userInfo)) {
                if (userInfo.getBindQq() != null) {
                    request.setLogin(login);
                    request.setClient(client);
                    request.setSessionId(client.getSessionId());
                    if (registerTasks.putIfAbsent(client.getSessionId(), request) == null) {
                        orderService.submitOrderTask(request);
                        return new BaseResponse<>(ResponseCode.OK.getCode(), request, null);
                    } else {
                        return new BaseResponse<>(ResponseCode.FAILED.getCode(), null, "最多只能有一个预约任务");
                    }
                } else {
                    return new BaseResponse<>(ResponseCode.FAILED.getCode(), null, "您的账号尚未绑定 QQ，请联系管理员进行绑定");
                }
            } else {
                return new BaseResponse<>(ResponseCode.FAILED.getCode(), null, "无可用次数");
            }
        }
    }

    private boolean isUsable(cy.jooq.tables.pojos.UserInfo userInfo) {
        return userInfo.getLeftTime() > 1 ||
            (userInfo.getLastUseTime() == null ||
                new Timestamp(System.currentTimeMillis())
                    .after(new Timestamp(userInfo.getLastUseTime().getTime() + ONE_MONTH)));
    }

    public BaseResponse<Boolean> unregisterTask(SocketIOClient client) {
        RegisterOrderTask task = registerTasks.remove(client.getSessionId());
        if (task != null) {
            task.cancel();
        }
        return new BaseResponse<>(ResponseCode.OK.getCode(), true, null);
    }

    @OnDisconnect
    public void cancelTaskWhenDisconnect(SocketIOClient client) {
        RegisterOrderTask task = registerTasks.remove(client.getSessionId());
        if (task != null) {
            log.info("cancelTaskWhenDisconnect, {}", client.getSessionId());
            task.fail("Session 断开");
        }
    }

    public void taskFinished(RegisterOrderTask task) {
        registerTasks.remove(task.getClient().getSessionId(), task);
    }
}
