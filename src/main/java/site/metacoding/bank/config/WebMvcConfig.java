package site.metacoding.bank.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        // flutter 할때는 cors 설정이 필요없음.
        // react 할때는 cors 설정에 react 서버에 ip만 허용하기 (보통 같은 서버에 배포할 것이기 때문에 localhost:3000 만
        // 허용)
        // nginx 사용하면 proxy pass 설정으로 해결할 수 있고, 그러면 cors 설정 필요 없음.
        // 참고 : https://blog.naver.com/getinthere/222591479330
        // 참고 : https://github.com/codingspecialist/devops-v1
        registry.addMapping("/**").allowedOrigins("*").allowedHeaders("*").allowedMethods("*").allowCredentials(false);
    }
}
