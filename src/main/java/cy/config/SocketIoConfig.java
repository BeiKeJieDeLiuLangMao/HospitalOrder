package cy.config;

import com.corundumstudio.socketio.Configuration;
import com.corundumstudio.socketio.SocketConfig;
import com.corundumstudio.socketio.SocketIOServer;
import com.corundumstudio.socketio.annotation.SpringAnnotationScanner;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;

import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * @author Chen Yang/CL10060-N/chen.yang@linecorp.com
 */
@org.springframework.context.annotation.Configuration
public class SocketIoConfig {

    @Value("${socket.io.port}")
    private int port;

    @Bean
    public SocketIOServer socketIOServer() throws UnknownHostException {
        Configuration config = new Configuration();
        config.setHostname(InetAddress.getLocalHost().getHostAddress());
        config.setPort(port);
        config.setHostname("0.0.0.0");
        config.setUpgradeTimeout(10000);
        config.setPingInterval(25000);
        config.setPingTimeout(60000);
        config.setBossThreads(1);
        config.setWorkerThreads(50);
        SocketConfig socketIoConfig = new SocketConfig();
        socketIoConfig.setReuseAddress(true);
        config.setSocketConfig(socketIoConfig);
        return new SocketIOServer(config);
    }

    /**
     * 通过该函数来扫描注入所有事件handler
     */
    @Bean
    public SpringAnnotationScanner springAnnotationScanner(SocketIOServer socketServer) {
        return new SpringAnnotationScanner(socketServer);
    }
}
