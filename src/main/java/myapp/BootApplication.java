package myapp;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.handler.MappedInterceptor;

@SpringBootApplication
public class BootApplication {
    public static void main(String[] args) {
        SpringApplication.run(BootApplication.class, args);
    }

    // https://stackoverflow.com/a/35948730
    @Autowired
    private ValidateAccessInterceptor validateAccessInterceptor;

    @Bean
    public MappedInterceptor myInterceptor()
    {
        return new MappedInterceptor(null, validateAccessInterceptor);
    }
}

