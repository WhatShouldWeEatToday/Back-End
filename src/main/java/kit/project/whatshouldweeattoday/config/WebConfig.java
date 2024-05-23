package kit.project.whatshouldweeattoday.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@EnableWebMvc
@Slf4j
public class WebConfig implements WebMvcConfigurer {
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOrigins("http://localhost:3000")
                .allowedHeaders("*")  // 모든 헤더 허용
                .allowedMethods("OPTIONS", "GET", "POST", "PATCH", "DELETE")
                .allowCredentials(true)
                .exposedHeaders("Custom-Header")
                .maxAge(3000);
    }
}
