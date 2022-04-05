package Couponify.BeckEnd.Couponify.security;

import Couponify.BeckEnd.Couponify.model.JwtBlackList;
import Couponify.BeckEnd.Couponify.model.OtpStore;
import Couponify.BeckEnd.Couponify.repository.JwtBlackListRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.util.StringUtils;
import org.springframework.web.context.support.SpringBeanAutowiringSupport;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.annotation.PostConstruct;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class JwtAuthenticaticationFilter extends OncePerRequestFilter{

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    @Autowired
    private CustomUserDetailsService userDetailsService;

    @Autowired
    private JwtBlackListRepository jwtBlackListRepository;

    private static final Logger logger = LoggerFactory.getLogger(JwtAuthenticaticationFilter.class);

    //ask spring to inject the values based on current context
    @PostConstruct
    public void init() {
        SpringBeanAutowiringSupport.processInjectionBasedOnCurrentContext(this);
    }

    @Override
    protected void doFilterInternal(HttpServletRequest httpServletRequest,
                                    HttpServletResponse httpServletResponse,
                                    FilterChain filterChain)
            throws ServletException, IOException {
        try {
            String token = getJwtFromRequest(httpServletRequest);

            System.out.println("token=" + token);

            if (StringUtils.hasText(token) && jwtTokenProvider.validateToken(token)) {
                System.out.println("Valid token");
                if (jwtBlackListRepository.existsByToken(token)) {
                    throw new ServletException();
                }


                //String username = jwtTokenProvider.getUserNameFromJwt(token);
                Long userId = jwtTokenProvider.getUserIdFromJwt(token);
                System.out.println(userId);

                //UserDetails userDetails = userDetailsService.loadUserByUsername(username);
                UserDetails userDetails = userDetailsService.loadUserById(userId);

                UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(userDetails, null,
                        userDetails.getAuthorities());
                authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(httpServletRequest));

                //Set the information to spring security
                SecurityContextHolder.getContext().setAuthentication(authenticationToken);
            }
        }catch (Exception ex){
            logger.error("Could not set user authentication in security context", ex);
        }
        filterChain.doFilter(httpServletRequest, httpServletResponse);
    }

    // Bearer <accessToken>
    private String getJwtFromRequest(HttpServletRequest request){
        String bearerToken = request.getHeader("Authorization");
        if(StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")){
            return bearerToken.substring(7, bearerToken.length());
        }
        return null;
    }
}
