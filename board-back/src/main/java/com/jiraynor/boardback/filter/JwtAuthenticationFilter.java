package com.jiraynor.boardback.filter;

import com.jiraynor.boardback.provider.JwtProvider;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component // 이 클래스가 스프링의 빈으로 등록됨. 스프링이 이 클래스를 관리하게 됨.
@RequiredArgsConstructor // final로 선언된 필드인 jwtProvider를 필수 생성자로 자동으로 만들어줌.
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    // OncePerRequestFilter는 요청이 들어올 때마다 한 번씩 실행되는 필터. 즉, 요청마다 JWT를 확인할 수 있음.

    private final JwtProvider jwtProvider; // JWT를 생성하고 검증하는 JwtProvider 클래스를 사용하기 위한 의존성 주입.

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        try {
            String token = parseBearerToken(request); // 요청에서 JWT를 추출하는 메서드 호출.

            if (token == null) {
                // 만약 토큰이 없으면 필터 체인을 그대로 진행하고 종료. 즉, 인증 처리를 하지 않고 다음 필터로 넘어감.
                filterChain.doFilter(request, response);
                return;
            }

            String email = jwtProvider.validate(token);
            // JWT를 검증하고 주체(subject)인 이메일을 추출. 만약 유효하지 않으면 null을 반환.

            if (email == null) {
                // 이메일이 null이면 인증되지 않은 상태로 필터 체인을 그대로 진행하고 종료.
                filterChain.doFilter(request, response);
                return;
            }

            // JWT가 유효하고 이메일이 존재할 경우, 스프링 시큐리티에서 사용할 인증 객체 생성.
            AbstractAuthenticationToken authenticationToken =
                    new UsernamePasswordAuthenticationToken(email, null, AuthorityUtils.NO_AUTHORITIES);
            // 이메일을 주체로 하고, 권한 정보는 빈 값으로 인증 토큰을 생성.

            authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
            // 요청의 세부 정보를 인증 객체에 설정. 여기서는 요청의 원본 세부 정보를 추가로 설정.

            SecurityContext securityContext = SecurityContextHolder.createEmptyContext();
            // 빈 시큐리티 컨텍스트를 생성.

            SecurityContextHolder.getContext().setAuthentication(authenticationToken);
            // 현재 요청의 시큐리티 컨텍스트에 인증 정보를 저장. 즉, 이제 이 요청은 인증된 상태로 처리됨.

            SecurityContextHolder.setContext(securityContext);
            // 최종적으로 시큐리티 컨텍스트를 설정함으로써 인증이 완료됨.

        } catch (Exception exception) {
            exception.printStackTrace(); // 예외가 발생할 경우 콘솔에 스택 트레이스를 출력.
        }

        filterChain.doFilter(request, response);
        // 필터 체인의 다음 필터로 넘어감. 필터를 통과한 요청만 컨트롤러로 전달됨.
    }

    // 요청 헤더에서 JWT(Bearer 토큰)를 추출하는 메서드
    private String parseBearerToken(HttpServletRequest request) {
        String authorization = request.getHeader("Authorization");
        // 요청 헤더에서 'Authorization'이라는 키의 값을 가져옴. 이 값에는 JWT가 들어있을 수 있음.

        boolean hasAuthorization = StringUtils.hasText(authorization);
        // authorization 값이 공백이 아니고 유효한 텍스트가 있는지 확인.

        if (!hasAuthorization) return null;
        // 값이 없으면 null을 반환.

        boolean isBearer = authorization.startsWith("Bearer ");
        // authorization 값이 'Bearer '로 시작하는지 확인. JWT 토큰은 보통 'Bearer '로 시작함.

        if (!isBearer) return null;
        // Bearer로 시작하지 않으면 null을 반환.

        String token = authorization.substring(7);
        // Bearer 다음에 오는 실제 JWT 토큰 부분만 추출.

        return token;
        // 추출한 토큰을 반환.
    }
}

/*요약
doFilterInternal: 요청이 들어올 때마다 JWT가 있는지 확인하고, 유효한 JWT가 있으면 해당 사용자를 인증합니다. 인증된 사용자는 컨트롤러로 요청을 전달받을 수 있습니다.
parseBearerToken: 요청의 헤더에서 JWT 토큰을 추출합니다. 'Bearer '로 시작하는지 확인한 후, 실제 JWT 문자열을 반환합니다.
filterChain.doFilter: 필터링 후에도 계속해서 다음 필터로 요청을 전달합니다.*/