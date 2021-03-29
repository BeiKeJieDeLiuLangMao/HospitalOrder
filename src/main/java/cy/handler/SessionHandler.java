package cy.handler;

import com.corundumstudio.socketio.SocketIOClient;
import com.corundumstudio.socketio.annotation.OnConnect;
import com.corundumstudio.socketio.annotation.OnDisconnect;
import cy.exception.SessionInvalidException;
import cy.model.response.PasswdLogin;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * @author Chen Yang/CL10060-N/chen.yang@linecorp.com
 * todo clear session login
 */
@SuppressWarnings("unused")
@Component
@Slf4j
public class SessionHandler {

    private static final Map<UUID, SocketIOClient> CLIENT_MAP = new ConcurrentHashMap<>();
    private static final Map<UUID, PasswdLogin> LOGIN_MAP = new ConcurrentHashMap<>();

    @OnConnect
    public void onConnect(SocketIOClient client) {
        log.info("A client connected, {}", client.getSessionId());
        CLIENT_MAP.put(client.getSessionId(), client);
    }

    @OnDisconnect
    public void onDisconnect(SocketIOClient client) {
        log.info("A client disconnected, {}", client.getSessionId());
        CLIENT_MAP.remove(client.getSessionId(), client);
    }

    public void broadcast(String name, Object data) {
        CLIENT_MAP.forEach((uuid, client) -> client.sendEvent(name, data));
    }

    public void setLogin(UUID sessionId, PasswdLogin login) {
        LOGIN_MAP.put(sessionId, login);
    }

    public void clearLogin(UUID sessionId) {
        LOGIN_MAP.remove(sessionId);
    }

    public PasswdLogin getLogin(UUID sessionId) {
        PasswdLogin login = LOGIN_MAP.get(sessionId);
        if (login == null) {
            throw new SessionInvalidException();
        }
        return login;
    }
}
