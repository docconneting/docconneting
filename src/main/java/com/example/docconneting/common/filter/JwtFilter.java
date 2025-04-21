package com.example.docconneting.common.filter;

import com.example.docconneting.common.config.JwtUtil;
import com.example.docconneting.domain.user.enums.UserRole;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;

@Slf4j
@RequiredArgsConstructor
public class JwtFilter implements Filter {

    private final JwtUtil jwtUtil;

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        Filter.super.init(filterConfig);
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;

        String url = httpRequest.getRequestURI();
        String method = httpRequest.getMethod();

        // 화이트리스트 검사
        if (isWhiteList(url, method)) {
            chain.doFilter(httpRequest, httpResponse);
            return;
        }

        String bearerJwt = httpRequest.getHeader("Authorization");

        if (bearerJwt == null || !bearerJwt.startsWith("Bearer ")) {
            httpResponse.sendError(HttpServletResponse.SC_UNAUTHORIZED, "JWT 토큰이 필요합니다.");
            return;
        }

        String jwt = jwtUtil.substringToken(bearerJwt);
        try {
            Claims claims = jwtUtil.extractClaims(jwt);
            if (claims == null) {
                httpResponse.sendError(HttpServletResponse.SC_BAD_REQUEST, "잘못된 JWT 토큰입니다.");
                return;
            }

            Long userId = Long.parseLong(claims.getSubject());
            String role = claims.get("role", String.class);
            if (role == null) {
                httpResponse.sendError(HttpServletResponse.SC_UNAUTHORIZED, "JWT에 role 정보가 없습니다.");
                return;
            }
            UserRole userRole = UserRole.valueOf(role);
            httpRequest.setAttribute("userId", userId);
            httpRequest.setAttribute("userRole", userRole);

            chain.doFilter(request, response);

        } catch (SecurityException | MalformedJwtException e) {
            log.error("유효하지 않은 JWT 서명입니다.", e);
            httpResponse.sendError(HttpServletResponse.SC_UNAUTHORIZED, "유효하지 않은 JWT 서명입니다.");
        } catch (ExpiredJwtException e) {
            log.error("만료된 JWT 토큰입니다.", e);
            httpResponse.sendError(HttpServletResponse.SC_UNAUTHORIZED, "만료된 JWT 토큰입니다.");
        } catch (UnsupportedJwtException e) {
            log.error("지원되지 않는 JWT 토큰입니다.", e);
            httpResponse.sendError(HttpServletResponse.SC_BAD_REQUEST, "지원되지 않는 JWT 토큰입니다.");
        } catch (Exception e) {
            log.error("유효하지 않은 JWT 토큰입니다.", e);
            httpResponse.sendError(HttpServletResponse.SC_BAD_REQUEST, "유효하지 않은 JWT 토큰입니다.");
        }
    }

    // URI + HTTP Method 기반 화이트리스트
    private boolean isWhiteList(String requestURI, String method) {
        if (requestURI.equals("/api/v1/payments/webhook")) {
            return true;
        }

        if (requestURI.startsWith("/api/v1/signup") ||
                requestURI.startsWith("/api/v1/signin") ||
                requestURI.startsWith("/api/v1/refresh") ||
                requestURI.startsWith("/api/v1/healthcheck")) {
            return true;
        }

        if ("GET".equals(method)) {
            return requestURI.matches("^/api/v1/posts$") ||
                    requestURI.matches("^/api/v1/posts/\\d+$") ||
                    requestURI.matches("^/api/v1/posts/\\d+/comments$") ||
                    requestURI.matches("^/api/v1/doctors/\\d+$") ||
                    requestURI.matches("^/api/v1/doctors$") ||
                    requestURI.matches("^/api/v1/health") ||
                    requestURI.matches("/websocket");
        }

        return false;
    }

    public void destroy() {
        Filter.super.destroy();
    }
}
