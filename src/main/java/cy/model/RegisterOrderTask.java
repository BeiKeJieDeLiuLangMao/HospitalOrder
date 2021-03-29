package cy.model;

import com.corundumstudio.socketio.SocketIOClient;
import com.fasterxml.jackson.annotation.JsonIgnore;
import cy.model.response.AliPayParams;
import cy.model.response.PasswdLogin;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * @author Chen Yang/CL10060-N/chen.yang@linecorp.com
 */
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class RegisterOrderTask {
    private static final String CANCEL_MESSAGE = "用户取消";
    private String userId;
    private String orderChannel = "1";
    private String medCardId;
    private String miBind;
    private String medCardName;
    private String regLevel;
    private String aptDate;
    private String aptDateStr;
    private String regName;
    private String deptName;
    private String deptId;
    private List<String> regTokens = new ArrayList<>();
    private String time;
    private String username;
    private AtomicLong queryTime = new AtomicLong(0);
    private int validNum = 0;
    private AtomicLong tryOrderTime = new AtomicLong(0);
    private String orderId;
    private int state = 0;
    private boolean success = false;
    private boolean finish = false;
    private String submitTime;
    private String startTime;
    private String finishTime;
    private AliPayParams aliPayParams;
    private UUID sessionId;
    private PasswdLogin login;
    @JsonIgnore
    private SocketIOClient client;
    private String message;
    private boolean alreadyShowAptTooLateWarning = false;

    public synchronized void success() {
        if (!finish) {
            finish = true;
            this.success = true;
            finishTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date(System.currentTimeMillis()));
        }
    }

    public synchronized void fail(String message) {
        if (!finish) {
            finish = true;
            this.success = false;
            this.message = message;
            finishTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date(System.currentTimeMillis()));
        }
    }

    public synchronized void cancel() {
        this.fail(CANCEL_MESSAGE);
    }

    public synchronized void sendTaskStatus() {
        if (!CANCEL_MESSAGE.equals(message)) {
            client.sendEvent("orderUpdate", this);
        }
    }
}
