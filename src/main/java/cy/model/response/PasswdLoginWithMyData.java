package cy.model.response;

import cy.jooq.tables.pojos.UserInfo;
import cy.model.request.Login;
import lombok.Getter;

@Getter
public class PasswdLoginWithMyData extends UserInfo {
    private String expireTime;
    private String sessionid;
    private String userid;
    private String password;

    public PasswdLoginWithMyData(Login login, PasswdLogin data, UserInfo user) {
        this.expireTime = data.getExpireTime();
        this.sessionid = data.getSessionid();
        this.userid = data.getUserid();
        this.password = login.getPassword();
        this.setId(user.getId());
        this.setUsername(user.getUsername());
        this.setLeftTime(user.getLeftTime());
        this.setLastUseTime(user.getLastUseTime());
        this.setBindQq(user.getBindQq());
    }
}
