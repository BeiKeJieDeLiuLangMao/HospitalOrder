package cy.model.response;

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
public class DeptInfo {
    public String aptExpert = "";
    public String aptNormal = "";
    public String content;
    public String deptId = "";
    public String deptImage;
    public String deptName = "";
    public Integer expertEnable;
    public String majorbiz = "";
    public Integer normalEnable;
    public String spotExpert = "";
    public String spotNormal = "";
}
