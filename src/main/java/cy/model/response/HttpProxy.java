package cy.model.response;

import lombok.*;

import java.sql.Timestamp;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
@EqualsAndHashCode
public class HttpProxy {
    private String ip;
    private int port;
    private Timestamp expire_time;
    private String city;
    private String isp;
    private String outip;
}
