package com.jiraynor.boardback.filter;

import ch.qos.logback.core.util.StringUtil;
import com.jiraynor.boardback.provider.JwtProvider;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.hibernate.query.SelectionQuery;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@RequiredArgsConstructor//필수 생성자를 만들어줌 필수라는건 final이라고 붙어있는걸 필수라고 인식
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    private final JwtProvider jwtProvider;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        try{
            String token=parseBearerToken(request);
            if(token==null){
                filterChain.doFilter(request, response);
                return;
            }
            String email=jwtProvider.validate(token);

            if(email==null){
                filterChain.doFilter(request, response);
                return;
            }

            AbstractAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(email, null, AuthorityUtils.NO_AUTHORITIES);
            authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

            SecurityContext securityContext= SecurityContextHolder.createEmptyContext();//비어있는 컨텍스트 만들어주기
            SecurityContextHolder.getContext().setAuthentication(authenticationToken);

            SecurityContextHolder.setContext(securityContext);

        }catch(Exception exception){
            exception.printStackTrace();
        }
        filterChain.doFilter(request, response);
    }//필터란 컨트롤러 거치기 전에 필터를 여러개 거쳐야함 필터에서 검증되지않으면 바로 다시 보내버림 필터를 통과한 애들만 컨드롤러에서 작업을 하게됨
    private String parseBearerToken(HttpServletRequest request) {
        String authorization=request.getHeader("Authorization");

        boolean hasAuthorization= StringUtils.hasText(authorization);//길이가 0이거나 공백으롬만 있으면 false제대로 있으면 true
        if(!hasAuthorization) return null;

        boolean isBearer = authorization.startsWith("Bearer ");//Bearer 로 시작하느냐
        if(!isBearer) return null;

        String token = authorization.substring(7);
        return token;
    }
}
