package cy.model.response;

import lombok.*;

/**
 * @author Chen Yang/CL10060-N/chen.yang@linecorp.com
 */
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
public class LockAptPutSpotReg {
    private Boolean needToPay = false;
    private String orderId = "";
}
