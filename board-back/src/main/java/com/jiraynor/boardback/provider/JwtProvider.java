package com.jiraynor.boardback.provider;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;

@Component // 빈(Component)으로 등록되어 스프링이 이 클래스를 관리하게 함. 다른 클래스에서 의존성 주입으로 쉽게 사용할 수 있음.
public class JwtProvider {

    @Value("${secret-key}")
    private String secretKey; // 애플리케이션 설정 파일에서 'secret-key' 값을 불러와 여기에 저장. 이 값은 JWT를 만들 때 사용할 비밀 키임.

    // JWT를 생성하는 메서드
    public String create(String email){
        Date expiredDate = Date.from(Instant.now().plus(1, ChronoUnit.HOURS));
        // 만료 시간을 현재 시간부터 1시간 뒤로 설정.

        String jwt= Jwts.builder()
                .signWith(SignatureAlgorithm.ES256,secretKey)
                // JWT에 사용될 암호화 알고리즘(ES256)과 비밀 키(secretKey)를 설정. ES256은 매우 안전한 암호화 방식 중 하나임.

                .setSubject(email)
                // 이 JWT의 주체(subject)를 'email'로 설정. 즉, 이 토큰은 'email'을 주체로 한다는 의미임.

                .setIssuedAt(new Date())
                // JWT가 언제 발행되었는지 기록. 현재 시간을 넣음.

                .setExpiration(expiredDate)
                // 토큰의 만료 시간을 지정. 이 토큰은 1시간 후 만료됨.

                .compact();
        // JWT 생성이 끝나면 이를 압축(compact)하여 문자열로 반환.

        return jwt;
        // 최종적으로 생성된 JWT 문자열을 반환.
    }

    // JWT를 검증하고 주체(subject)를 반환하는 메서드
    public String validate(String jwt){
        Claims claims = null; // JWT의 내용물을 담을 변수 선언.

        try{
            claims=Jwts.parser()
                    .setSigningKey(secretKey)
                    // JWT를 해독하기 위해 비밀 키를 사용.

                    .parseClaimsJws(jwt).getBody();
            // 입력받은 JWT를 해석하여 그 안에 담긴 정보를 가져옴. 그 정보 중에서 "바디" 부분(주로 주체, 만료 시간 등)을 가져옴.

        }catch (Exception exception){
            exception.printStackTrace();
            // 만약 JWT를 해석하는 도중 문제가 생기면 에러 메시지를 출력.

            return null;
            // 문제가 생기면 null을 반환하여 유효하지 않은 JWT임을 알림.
        }

        return claims.getSubject();
        // JWT가 유효하다면 주체(subject), 즉 이메일을 반환.
    }
}
