package com.baekjihwa.playlist.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Spring Boot 애플리케이션의 웹 관련 설정을 담당하는 클래스입니다.
 * CORS (Cross-Origin Resource Sharing) 정책을 설정하여
 * 다른 도메인에서의 API 요청을 허용합니다.
 */
@Configuration // 이 클래스를 스프링 설정 클래스로 선언
public class WebConfig implements WebMvcConfigurer {

    /**
     * CORS 매핑을 추가하는 메서드입니다.
     * 프론트엔드 URL로부터의 API 요청을 허용하도록 설정합니다.
     *
     * @param registry CORS 규칙을 등록하기 위한 CorsRegistry 객체
     */
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/api/**") // '/api/**' 경로로 들어오는 모든 요청에 대해 CORS 설정 적용
                .allowedOrigins(
                        "http://localhost:8081", // 로컬 VuePress 개발 서버
                        "https://leekyounghwa.github.io" // ✅ 당신의 배포된 블로그 주소 추가!
                )
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS") // 허용할 HTTP 메서드
                .allowedHeaders("*") // 모든 헤더 허용
                .allowCredentials(false) // 인증 정보를 포함한 요청 (쿠키, HTTP 인증)을 허용할지 여부
                .maxAge(3600); // Preflight 요청의 결과를 캐시할 시간 (초 단위)
    }
}