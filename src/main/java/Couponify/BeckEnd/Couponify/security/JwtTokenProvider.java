package Couponify.BeckEnd.Couponify.security;

import Couponify.BeckEnd.Couponify.model.AppProperties;
import Couponify.BeckEnd.Couponify.model.OtpStore;
import io.jsonwebtoken.*;
import lombok.Data;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Component
public class JwtTokenProvider {
    //Extract the value of jwt properties from properties file.
//    @Value("${app.jwt-secret}")
//    private String jwtSecret;

//    @Value("${app.jwt-expiration-millisecond}")
//    private int jwtExpirationInMs;
    private static final Logger logger = LoggerFactory.getLogger(JwtTokenProvider.class);

    private AppProperties appProperties;

    public JwtTokenProvider(AppProperties appProperties){
        this.appProperties = appProperties;
    }

    //generate Token
    public String generateToken(Authentication authentication){
//        String username = authentication.getName();
        UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
        Date currentDate = new Date();
        Date expiryDate = new Date(currentDate.getTime() + appProperties.getAuth().getTokenExpirationMsec());
        //Date expireDate = new Date(currentDate.getTime() + jwtExpirationInMs);

        String token = Jwts.builder()
                .setSubject(Long.toString(userPrincipal.getId())/*username*/)
                .setIssuedAt(new Date())
                .setExpiration(expiryDate)
                .signWith(SignatureAlgorithm.HS512, appProperties.getAuth().getTokenSecret()/*jwtSecret*/)
                .compact();
        return token;
    }

    public String generateToken(UserPrincipal userPrincipal) {
        Map<String, Object> claims = new HashMap<>();
        Date currentDate = new Date();
        Date expiryDate = new Date(currentDate.getTime() + appProperties.getAuth().getTokenExpirationMsec());
        String token = Jwts.builder()
                .setSubject(Long.toString(userPrincipal.getId())/*username*/)
                .setIssuedAt(new Date())
                .setExpiration(expiryDate)
                .signWith(SignatureAlgorithm.HS512, appProperties.getAuth().getTokenSecret()/*jwtSecret*/)
                .compact();
        return token;
    }

    public Long getUserIdFromJwt(String token){
        Claims claims = Jwts.parser()
                .setSigningKey(appProperties.getAuth().getTokenSecret()/*jwtSecret*/)
                .parseClaimsJws(token)
                .getBody();
        return Long.parseLong(claims.getSubject())/*claims.getSubject() returns String*/;
    }

    public boolean validateToken(String token){
        try {
            Jwts.parser().setSigningKey(appProperties.getAuth().getTokenSecret()).parseClaimsJws(token);
            return true;
        } catch (SignatureException ex) {
            logger.error("Invalid JWT signature");
        } catch (MalformedJwtException ex) {
            logger.error("Invalid JWT token");
        } catch (ExpiredJwtException ex) {
            logger.error("Expired JWT token");
        } catch (UnsupportedJwtException ex) {
            logger.error("Unsupported JWT token");
        } catch (IllegalArgumentException ex) {
            logger.error("JWT claims string is empty.");
        }
        /*try{
            Jwts.parser().setSigningKey(jwtSecret).parseClaimsJws(token);
            return true;
        }catch (SignatureException ex){
           // Implement the application specific exception
            System.out.println("Exception generated");
        }*/
        return false;

    }
}
