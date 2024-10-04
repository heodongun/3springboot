package com.jiraynor.boardback.config;

import com.jiraynor.boardback.filter.JwtAuthenticationFilter;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.io.IOException;

@Configuration // 이 클래스는 스프링 설정 파일이라는 것을 알림.
@EnableWebSecurity // 웹 보안을 활성화.
@RequiredArgsConstructor // 필요한 필드를 자동으로 생성해주는 롬복(Lombok) 어노테이션.
public class WebSecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    // JWT 인증을 위한 필터. 나중에 사용할 필터를 선언해 둠.

    @Bean
    protected SecurityFilterChain configure(HttpSecurity httpSecurity) throws Exception {
        httpSecurity
                .cors().and() // CORS(Cross-Origin Resource Sharing)를 허용. 외부 도메인의 요청을 허용하기 위해 설정. 이어지게 할려면 .and()
                .csrf().disable() // CSRF(Cross-Site Request Forgery) 공격 방지를 끄기. JWT를 사용하면 CSRF를 쓸 필요가 없기 때문에 비활성화.
                .httpBasic().disable() // 기본 인증 방식을 비활성화. 대신 JWT를 사용해 인증함.
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS).and()
                // 세션을 사용하지 않도록 설정. JWT는 서버에 세션을 저장하지 않고 상태를 유지함.

                .authorizeHttpRequests()
                .requestMatchers("/", "/api/v1/auth/**", "/api/v1/search/**", "/file/**").permitAll()
                // 이 URL로 오는 요청들은 인증 없이 누구나 접근 가능하게 함.

                .requestMatchers(HttpMethod.GET, "/api/v1/board/**", "/api/v1/user/*").permitAll()
                // GET 메소드로 요청되는 특정 경로에 대한 접근을 허용.

                .anyRequest().authenticated().and()
                // 나머지 모든 요청은 인증이 필요하다고 설정.

                .exceptionHandling().authenticationEntryPoint(new FailedAuthenticationEntryPoint());
        // 인증에 실패했을 때 처리하는 방식을 지정. 아래의 FailedAuthenticationEntryPoint 클래스로 처리함.

        httpSecurity.addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);
        // 우리가 만든 JWT 인증 필터를 스프링 시큐리티의 기본 인증 필터 앞에 추가함.

        return httpSecurity.build();
        // 최종적으로 설정이 완료된 SecurityFilterChain을 반환.
    }
}

class FailedAuthenticationEntryPoint implements AuthenticationEntryPoint {

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException, ServletException {
        response.setContentType("application/json");
        // 응답의 콘텐츠 타입을 JSON으로 설정.

        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        // HTTP 상태 코드를 403 (Forbidden)으로 설정. 즉, 권한이 없다는 것을 나타냄.

        response.getWriter().write("{\"code\":\"NP\",\"message\":\"Authorization Failed\"}");
        // 인증 실패 시 응답으로 간단한 JSON 형식의 에러 메시지를 전송.
    }
}
