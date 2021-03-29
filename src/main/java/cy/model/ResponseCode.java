package cy.model;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

/**
 * @author Chen Yang/CL10060-N/chen.yang@linecorp.com
 */
@Slf4j
public enum ResponseCode {
    /**
     * Response code
     */
    OK(0),
    INVALID_SESSION_ID(104),
    FAILED(1000);

    @Getter
    private int code;

    ResponseCode(int code) {
        this.code = code;
    }

    public boolean isSameCode(BaseResponse response) {
        return code == response.getCode();
    }
}
