package cy.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * @author CL10060-N
 */
@Configuration
@EnableWebMvc
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry
            .addResourceHandler(
                "/css/**",
                "/js/**",
                "/fonts/**",
                "/**")
            .addResourceLocations(
                "classpath:/static/css/",
                "classpath:/static/js/",
                "classpath:/static/fonts/",
                "classpath:/static/");
    }

}
