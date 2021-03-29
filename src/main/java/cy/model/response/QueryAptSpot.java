package cy.model.response;

import java.util.Map;

import lombok.*;

/**
 * @author Chen Yang/CL10060-N/chen.yang@linecorp.com
 */
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
public class QueryAptSpot {
    public String deptId = "";
    public String deptName = "";
    public String doctorCode = "";
    public String doctorName = "";
    public Integer regLevel = 0;
    public Map<String, RegData> regTokens;
    public String specCode = "";
    public String specName = "";
    public String visitDate = "";

    @AllArgsConstructor
    @NoArgsConstructor
    @Getter
    @Setter
    @ToString
    public static class RegData{
        private String regToken;
        private String schedulingName;
        private String medFee;
        private int validNum;
        private String validName;
    }
}
