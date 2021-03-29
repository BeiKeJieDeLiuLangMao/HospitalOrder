package cy.model.response;

import java.util.ArrayList;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * @author Chen Yang/CL10060-N/chen.yang@linecorp.com
 */
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
public class UserInfo {
    public Integer brokeTimes = 0;
    public String loginid = "";
    public String name = "";
    public String phone = "";
    public String userid = "";
}
