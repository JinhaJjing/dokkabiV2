package com.goya.dokkabiv2;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.info.License;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@OpenAPIDefinition(
        info = @Info(
                title = "dokkabiV2 API",
                description = "돗가비 v2 서버 API 문서",
                license = @License(name = "서버 저장소", url = "https://github.com/JinhaJjing/dokkabiV2")
        )
)
@SpringBootApplication
public class DokkabiV2Application {

    public static void main(String[] args) {
        SpringApplication.run(DokkabiV2Application.class, args);
    }
}
