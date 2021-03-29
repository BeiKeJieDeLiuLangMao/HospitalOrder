package cy;

import com.corundumstudio.socketio.SocketIOServer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.stereotype.Component;

import javax.annotation.PreDestroy;

/**
 * @author Chen Yang/CL10060-N/chen.yang@linecorp.com
 */
@SpringBootApplication
@EnableScheduling
public class Application extends SpringBootServletInitializer {

    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
        return application.sources(Application.class);
    }

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

    @Component
    @Slf4j
    public static class ServerRunner implements CommandLineRunner {

        @Autowired
        private SocketIOServer server;
        @Value("${server.port}")
        private int port;

        @PreDestroy
        public void destroy() {
            if (server != null) {
                server.stop();
            }
        }

        @Override
        public void run(String... args) throws InterruptedException {
            while (true) {
                try {
                    server.start();
                    break;
                } catch (Exception e) {
                    Thread.sleep(1000);
                }
            }
        }
    }
}
