package Couponify.BeckEnd.Couponify.security.oauth2.user;

import Couponify.BeckEnd.Couponify.exception.OAuth2AuthenticationProcessingException;
import Couponify.BeckEnd.Couponify.model.AuthProvider;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;

import java.util.Map;

public class OAuth2UserInfoFactory {
    public static OAuth2UserInfo getOAuth2UserInfo(String registrationId, Map<String, Object> attributes){
        if(registrationId.equalsIgnoreCase(AuthProvider.google.toString())){
            return new GoogleOAuth2UserInfo(attributes);
        }else if(registrationId.equalsIgnoreCase(AuthProvider.facebook.toString())){
            return new FacebookOAuth2UserInfo(attributes);
        }else if(registrationId.equalsIgnoreCase(AuthProvider.github.toString())){
            return new GithubOAuth2UserInfo(attributes);
        }else{
            throw new OAuth2AuthenticationProcessingException("Sorry! login with "+registrationId
                                                                + " is not yet supported");
        }
    }
}
