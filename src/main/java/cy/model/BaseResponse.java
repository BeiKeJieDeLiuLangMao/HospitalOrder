package cy.model;

import com.alibaba.fastjson.JSON;
import cy.exception.SessionInvalidException;
import java.util.List;

import lombok.*;
import lombok.extern.slf4j.Slf4j;

import static cy.model.ResponseCode.FAILED;
import static cy.model.ResponseCode.INVALID_SESSION_ID;
import static cy.model.ResponseCode.OK;

/**
 * @author CL10060-N
 */
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
@Slf4j
public class BaseResponse<T>  {
    private int code;
    private T data;
    private String msg;

    public BaseResponse<T> transform(BaseResponse<Object> origin, Class<T> clazz) {
        this.code = origin.code;
        this.msg = origin.msg;
        this.data = JSON.parseObject(JSON.toJSONString(origin.data), clazz);
        return this;
    }

    @SuppressWarnings("unchecked")
    public <R> BaseResponse<List<R>> transformList(BaseResponse<Object> origin, Class<R> clazz) {
        this.code = origin.code;
        this.msg = origin.msg;
        this.data = (T)JSON.parseArray(JSON.toJSONString(origin.data), clazz);
        return (BaseResponse<List<R>>) this;
    }

    public boolean isOk() {
        return ResponseCode.OK.isSameCode(this);
    }

    public boolean checkCode() {
        if (INVALID_SESSION_ID.isSameCode(this)) {
            throw new SessionInvalidException();
        } else if (FAILED.isSameCode(this)) {
            return false;
        } else if (OK.isSameCode(this)) {
            return true;
        } else {
            log.error("Unhandled lock apt response type: {}", this);
            return false;
        }
    }
}