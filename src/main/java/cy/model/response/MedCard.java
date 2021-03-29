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
public class MedCard {
    public String address;
    public String city;
    public Integer id;
    public String idCardId;
    public Integer idCardType;
    public Integer isDefault;
    public Integer mcType;
    public String mcid;
    public Integer miBind;
    public String miid;
    public String name;
    public String province;
    public Integer ptid;
    public String qrCode;
    public Integer regEnable;

}
