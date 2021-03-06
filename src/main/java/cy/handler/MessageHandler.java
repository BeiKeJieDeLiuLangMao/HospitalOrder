package cy.handler;

import com.corundumstudio.socketio.SocketIOClient;
import com.corundumstudio.socketio.annotation.OnEvent;
import cy.exception.SessionInvalidException;
import cy.jooq.tables.daos.UserInfoDao;
import cy.model.BaseResponse;
import cy.model.RegisterOrderTask;
import cy.model.request.LoadRegList;
import cy.model.request.Login;
import cy.model.response.DeptInfo;
import cy.model.response.Doctor;
import cy.model.response.MedCard;
import cy.model.response.PasswdLogin;
import cy.model.response.PasswdLoginWithMyData;
import cy.model.response.QueryAptSpot;
import cy.model.response.Spec;
import cy.model.response.UserInfo;
import cy.service.RequestService;
import java.util.List;
import java.util.Objects;
import lombok.extern.slf4j.Slf4j;
import org.jooq.DSLContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author Chen Yang/CL10060-N/chen.yang@linecorp.com
 */
@SuppressWarnings("unused")
@Component
@Slf4j
public class MessageHandler {

    @Autowired
    private RequestService requestService;
    @Autowired
    private UserInfoDao userInfoDao;
    @Autowired
    private DSLContext dslContext;
    @Autowired
    private SessionHandler sessionHandler;

    @OnEvent("login")
    public void login(SocketIOClient client, Login login) {
        log.info("login, {}, {}", client.getSessionId(), login);
        try {
            BaseResponse<PasswdLogin> response = requestService.login(login.getUsername(), login.getPassword());
            if (response.checkCode()) {
                sessionHandler.setLogin(client.getSessionId(), response.getData());
                cy.jooq.tables.pojos.UserInfo user = userInfoDao.fetchOneById(response.getData().getUserid());
                if (Objects.isNull(user)) {
                    user = new cy.jooq.tables.pojos.UserInfo(response.getData().getUserid(), login.getUsername(),
                            1, null, null);
                    try {
                        userInfoDao.insert(user);
                    } catch(Exception e) {
                        // maybe id duplicate
                        user = userInfoDao.fetchOneById(response.getData().getUserid());
                    }
                }
                client.sendEvent("login", new PasswdLoginWithMyData(login, response.getData(), user));
            } else {
                client.sendEvent("login", response.getMsg());
            }
        } catch (SessionInvalidException e) {
            sessionHandler.clearLogin(client.getSessionId());
            client.sendEvent("login", "???????????????");
            client.sendEvent("loginExpired");
        } catch (Exception e) {
            log.error("login failed", e);
            client.sendEvent("login", "???????????????????????????");
        }
    }

    @OnEvent("loadDepartments")
    public void loadDepartments(SocketIOClient client) {
        log.info("loadDepartments, {}", client.getSessionId());
        try {
            BaseResponse<List<DeptInfo>> response = requestService.queryDepartments(client);
            if (response.checkCode()) {
                client.sendEvent("loadDepartments", response.getData());
            } else {
                client.sendEvent("loadDepartments", response.getMsg());
            }
        } catch (SessionInvalidException e) {
            sessionHandler.clearLogin(client.getSessionId());
            client.sendEvent("loadDepartments", "???????????????");
            client.sendEvent("loginExpired");
        } catch (Exception e) {
            log.error("load department list failed", e);
            client.sendEvent("loadDepartments", "?????????????????????????????????");
        }
    }

    @OnEvent("loadDoctors")
    public void loadDoctors(SocketIOClient client) {
        log.info("loadDoctors, {}", client.getSessionId());
        try {
            BaseResponse<List<Doctor>> response = requestService.queryDoctors(client);
            if (response.checkCode()) {
                client.sendEvent("loadDoctors", response.getData());
            } else {
                client.sendEvent("loadDoctors", response.getMsg());
            }
        } catch (SessionInvalidException e) {
            sessionHandler.clearLogin(client.getSessionId());
            client.sendEvent("loadDoctors", "???????????????");
            client.sendEvent("loginExpired");
        } catch (Exception e) {
            log.error("load doctor list failed", e);
            client.sendEvent("loadDoctors", "?????????????????????????????????");
        }

    }

    @OnEvent("loadSpec")
    public void loadSpec(SocketIOClient client) {
        log.info("loadSpec, {}", client.getSessionId());
        try {
            BaseResponse<List<Spec>> response = requestService.loadSpec(client);
            if (response.checkCode()) {
                client.sendEvent("loadSpec", response.getData());
            } else {
                client.sendEvent("loadSpec", response.getMsg());
            }
        } catch (SessionInvalidException e) {
            sessionHandler.clearLogin(client.getSessionId());
            client.sendEvent("loadSpec", "???????????????");
            client.sendEvent("loginExpired");
        } catch (Exception e) {
            log.error("load doctor list failed", e);
            client.sendEvent("loadSpec", "?????????????????????????????????");
        }
    }

    @OnEvent("loadAppUserInfo")
    public void loadAppUserInfo(SocketIOClient client) {
        log.info("loadAppUserInfo, {}", client.getSessionId());
        try {
            BaseResponse<UserInfo> response = requestService.queryUserInfo(client);
            if (response.checkCode()) {
                client.sendEvent("loadAppUserInfo", response.getData());
            } else {
                client.sendEvent("loadAppUserInfo", response.getMsg());
            }
        } catch (SessionInvalidException e) {
            sessionHandler.clearLogin(client.getSessionId());
            client.sendEvent("loadAppUserInfo", "???????????????");
            client.sendEvent("loginExpired");
        } catch (Exception e) {
            log.error("load app user info failed", e);
            client.sendEvent("loadAppUserInfo", "?????? App ?????????????????????????????????");
        }
    }

    @OnEvent("loadMedCards")
    public void loadMedCards(SocketIOClient client) {
        log.info("loadMedCards, {}", client.getSessionId());
        try {
            BaseResponse<List<MedCard>> response = requestService.queryUserMedCard(client);
            if (response.checkCode()) {
                client.sendEvent("loadMedCards", response.getData());
            } else {
                client.sendEvent("loadMedCards", response.getMsg());
            }
        } catch (SessionInvalidException e) {
            sessionHandler.clearLogin(client.getSessionId());
            client.sendEvent("loadMedCards", "???????????????");
            client.sendEvent("loginExpired");
        } catch (Exception e) {
            log.error("load med cards failed", e);
            client.sendEvent("loadMedCards", "??????????????????????????????????????????");
        }
    }

    @OnEvent("loadItems")
    public void loadRegList(SocketIOClient client, LoadRegList request) {
        log.info("loadItems, {}, {}", client.getSessionId(), request);
        try {
            BaseResponse<List<QueryAptSpot>> response = requestService.loadRegList(client, request);
            if (response.checkCode()) {
                client.sendEvent("loadItems", response.getData());
            } else {
                client.sendEvent("loadItems", response.getMsg());
            }
        } catch (SessionInvalidException e) {
            sessionHandler.clearLogin(client.getSessionId());
            client.sendEvent("loadItems", "???????????????");
            client.sendEvent("loginExpired");
        } catch (Exception e) {
            log.error("load item list failed", e);
            client.sendEvent("loadItems", "?????????????????????????????????");
        }
    }

    @OnEvent("submitTask")
    public void registerOrderTask(SocketIOClient client, RegisterOrderTask request) {
        log.info("registerOrderTask, {}, {}", client.getSessionId(), request);
        try {
            BaseResponse<RegisterOrderTask> response = requestService.registerOrderTask(client, request);
            if (response.checkCode()) {
                client.sendEvent("submitTask", response.getData());
            } else {
                client.sendEvent("submitTask", response.getMsg());
            }
        } catch (SessionInvalidException e) {
            sessionHandler.clearLogin(client.getSessionId());
            client.sendEvent("submitTask", "???????????????");
            client.sendEvent("loginExpired");
        } catch (Exception e) {
            log.error("submit task failed", e);
            client.sendEvent("submitTask", "???????????????????????????????????????");
        }
    }

    @OnEvent("cancelTask")
    public void cancelTask(SocketIOClient client) {
        log.info("cancelTask, {}", client.getSessionId());
        try {
            BaseResponse<Boolean> response = requestService.unregisterTask(client);
            if (response.checkCode()) {
                client.sendEvent("cancelTask", response.getData());
            } else {
                client.sendEvent("cancelTask", response.getMsg());
            }
        } catch (SessionInvalidException e) {
            sessionHandler.clearLogin(client.getSessionId());
            client.sendEvent("submitTask", "???????????????");
            client.sendEvent("cancelTask");
        } catch (Exception e) {
            log.error("cancel task failed", e);
            client.sendEvent("cancelTask", "???????????????????????????????????????");
        }
    }
}
