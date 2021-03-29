package cy.controller;

import org.apache.tomcat.util.http.fileupload.IOUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.View;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.InputStream;
import java.util.Map;

import static org.springframework.http.MediaType.TEXT_HTML_VALUE;

@Controller
public class IndexController {

    private static final View INDEX = new View() {
        @Override
        public void render(Map<String, ?> map, HttpServletRequest httpServletRequest,
                           HttpServletResponse httpServletResponse) throws Exception {
            try (InputStream inputStream = this.getClass().getResourceAsStream("/static/index.html")) {
                httpServletResponse.setContentType(TEXT_HTML_VALUE);
                IOUtils.copy(inputStream, httpServletResponse.getOutputStream());
            }
        }
    };

    @GetMapping("/")
    public View index() {
        return INDEX;
    }


}
