package cy.model.response;

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
public class Doctor {
    public String avatar = "";
    public String deptId = "";
    public String doctorCode = "";
    public String doctorId = "";
    public String doctorName = "";
    public String title = "";
}
