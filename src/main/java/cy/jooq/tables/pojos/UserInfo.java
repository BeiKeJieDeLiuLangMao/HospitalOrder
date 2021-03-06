/*
 * This file is generated by jOOQ.
 */
package cy.jooq.tables.pojos;


import java.io.Serializable;
import java.sql.Timestamp;

import javax.annotation.Generated;


/**
 * This class is generated by jOOQ.
 */
@Generated(
    value = {
        "http://www.jooq.org",
        "jOOQ version:3.12.1"
    },
    comments = "This class is generated by jOOQ"
)
@SuppressWarnings({ "all", "unchecked", "rawtypes" })
public class UserInfo implements Serializable {

    private static final long serialVersionUID = 683876851;

    private String    id;
    private String    username;
    private Integer   leftTime;
    private Timestamp lastUseTime;
    private String    bindQq;

    public UserInfo() {}

    public UserInfo(UserInfo value) {
        this.id = value.id;
        this.username = value.username;
        this.leftTime = value.leftTime;
        this.lastUseTime = value.lastUseTime;
        this.bindQq = value.bindQq;
    }

    public UserInfo(
        String    id,
        String    username,
        Integer   leftTime,
        Timestamp lastUseTime,
        String    bindQq
    ) {
        this.id = id;
        this.username = username;
        this.leftTime = leftTime;
        this.lastUseTime = lastUseTime;
        this.bindQq = bindQq;
    }

    public String getId() {
        return this.id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUsername() {
        return this.username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public Integer getLeftTime() {
        return this.leftTime;
    }

    public void setLeftTime(Integer leftTime) {
        this.leftTime = leftTime;
    }

    public Timestamp getLastUseTime() {
        return this.lastUseTime;
    }

    public void setLastUseTime(Timestamp lastUseTime) {
        this.lastUseTime = lastUseTime;
    }

    public String getBindQq() {
        return this.bindQq;
    }

    public void setBindQq(String bindQq) {
        this.bindQq = bindQq;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("UserInfo (");

        sb.append(id);
        sb.append(", ").append(username);
        sb.append(", ").append(leftTime);
        sb.append(", ").append(lastUseTime);
        sb.append(", ").append(bindQq);

        sb.append(")");
        return sb.toString();
    }
}
