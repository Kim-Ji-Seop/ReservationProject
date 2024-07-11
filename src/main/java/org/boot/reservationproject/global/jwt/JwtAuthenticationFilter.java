package org.boot.reservationproject.global.jwt;

import static org.springframework.util.MimeTypeUtils.APPLICATION_JSON_VALUE;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.boot.reservationproject.global.error.BaseResponse;
import org.boot.reservationproject.global.error.ErrorCode;
import org.boot.reservationproject.global.redis.RedisDao;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

@RequiredArgsConstructor
@Slf4j
public class JwtAuthenticationFilter extends OncePerRequestFilter {
  private final JwtTokenProvider jwtTokenProvider;
  private final RedisDao redisDao;
  private final UserDetailsService userDetailsService;
  @Override
  protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
    String path = request.getServletPath();
    AntPathMatcher pathMatcher = new AntPathMatcher();
    return (
            pathMatcher.match("/api/customers/registration", path) && request.getMethod().equals("POST") || // 회원가입 > 구매자
            pathMatcher.match("/api/sellers/registration", path) && request.getMethod().equals("POST") || // 회원가입 > 판매자
            pathMatcher.match("/api/customers/auth-email", path) && request.getMethod().equals("POST") || // 이메일 로그인 > 구매자
            pathMatcher.match("/api/sellers/auth-email", path) && request.getMethod().equals("POST") || // 이메일 로그인 > 판매자
            pathMatcher.match("/api/admin/registration/service-options",path) && request.getMethod().equals("POST") // 관리자 부대시설 등록
    );
  }
  @Override
  protected void doFilterInternal(HttpServletRequest request,
                                  HttpServletResponse response,
                                  FilterChain filterChain) throws ServletException, IOException {
    log.info("doFilterInternal 실행 경로: {}", request.getServletPath());
    String jwt = getJwtFromRequest(request);
    log.info("filter 에서 jwt 토큰 가져오기 : {}",jwt);

    if (StringUtils.hasText(jwt) && jwtTokenProvider.validateToken(jwt)) {
      String userEmail = jwtTokenProvider.getUserEmailFromJWT(jwt);
      UserDetails userDetails = userDetailsService.loadUserByUsername(userEmail);
      log.info("유저 정보: {}", userDetails);
      if(userDetails != null){
        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
        authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
        SecurityContextHolder.getContext().setAuthentication(authentication);
      }
    }else if (jwt == null){
      sendErrorResponse(response, ErrorCode.TOKEN_NOT_EXIST);
      return;
    }else{
      sendErrorResponse(response, ErrorCode.INVALID_TOKEN);
      return;
    }
    log.info("여기까지오나?");
    filterChain.doFilter(request, response);
  }

  public String getJwtFromRequest(HttpServletRequest request) {
    String bearerToken = request.getHeader("Authorization"); // Authorization 이름의 헤더의 내용을 가져온다.
    if (bearerToken.startsWith("Bearer ")) { // 헤더의 내용이 Bearer 로 시작하는지 확인
      return bearerToken.substring(7); // Bearer 이후의 내용이 토큰임. (AT or RT)
    }
    return null;
  }
  private void sendErrorResponse(HttpServletResponse httpServletResponse, ErrorCode errorCode) throws IOException{
    httpServletResponse.setCharacterEncoding("utf-8");
    httpServletResponse.setStatus(HttpStatus.UNAUTHORIZED.value());
    httpServletResponse.setContentType(APPLICATION_JSON_VALUE);

    BaseResponse errorResponse = new BaseResponse(errorCode);
    //object를 텍스트 형태의 JSON으로 변환
    new ObjectMapper().writeValue(httpServletResponse.getWriter(), errorResponse);
  }
}
