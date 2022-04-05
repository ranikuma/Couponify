package Couponify.BeckEnd.Couponify.service;

import java.security.Principal;
import java.text.ParseException;
import java.util.Optional;

import Couponify.BeckEnd.Couponify.exception.OAuth2AuthenticationProcessingException;
import Couponify.BeckEnd.Couponify.exception.OtpException;
import Couponify.BeckEnd.Couponify.exception.ResourceNotFoundException;
import Couponify.BeckEnd.Couponify.model.AuthProvider;
import Couponify.BeckEnd.Couponify.model.OtpStore;
import Couponify.BeckEnd.Couponify.model.User;
import Couponify.BeckEnd.Couponify.payload.JWTAuthResponse;
import Couponify.BeckEnd.Couponify.payload.OtpPojo;
import Couponify.BeckEnd.Couponify.payload.SmsPojo;
import Couponify.BeckEnd.Couponify.repository.OtpRepository;
import Couponify.BeckEnd.Couponify.repository.StoreOTP;
import Couponify.BeckEnd.Couponify.repository.UserRepository;
import Couponify.BeckEnd.Couponify.security.CustomUserDetailsService;
import Couponify.BeckEnd.Couponify.security.JwtTokenProvider;
import Couponify.BeckEnd.Couponify.security.UserPrincipal;
import ch.qos.logback.classic.spi.IThrowableProxy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.MultiValueMap;

import com.twilio.Twilio;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.type.PhoneNumber;
@Component
public class SmsService {
    private final String ACCOUNT_SID ="ACd94315cd712e29df65510b6070b685e6";

    private final String AUTH_TOKEN = "1de3394b5d66b586959e9635349b9a31";

    private final String FROM_NUMBER = "+13203371999";

    @Autowired
    OtpRepository otpRepository;

    @Autowired
    JwtTokenProvider tokenProvider;

    @Autowired
    CustomUserDetailsService customUserDetailsService;

    @Autowired
    UserRepository userRepository;

    public void send(SmsPojo sms) throws ParseException, OtpException {
        Twilio.init(ACCOUNT_SID, AUTH_TOKEN);


        int min = 100000;
        int max = 999999;
        int number=(int)(Math.random()*(max-min+1)+min);


        String msg ="Your OTP - "+number+ " please verify this OTP in your Application by Er Prince kumar Technoidentity.com";


        Message message = Message.creator(new PhoneNumber(sms.getTo()), new PhoneNumber(FROM_NUMBER), msg)
                .create();
        if(otpRepository.existsByNumber(sms.getTo())){
            OtpStore otpStore = otpRepository.findByNumber(sms.getTo()).orElseThrow(()->new OtpException());
            otpStore.setOtp(number);
            otpStore.setExpirytime(System.currentTimeMillis()+600000);
            otpRepository.save(otpStore);
        }else {
            OtpStore otpStore = new OtpStore();
            otpStore.setOtp(number);
            otpStore.setNumber(sms.getTo());
            otpStore.setExpirytime(System.currentTimeMillis()+600000);
            otpRepository.save(otpStore);
        }

    }

    public JWTAuthResponse verifyAuthRequest(OtpPojo otpPojo) throws OtpException, ResourceNotFoundException {
//        OtpStore sentotp= OtpRepository.findByNumber(otpPojo.getNumber()).orElseThrow(()->new OtpException());
        OtpStore otpStore = new OtpStore();
        otpStore.setNumber(otpPojo.getNumber());
        otpStore.setOtp(otpPojo.getOtp());
        System.out.println(otpStore.toString());
        OtpStore otpInfo = otpRepository.findByNumber(otpStore.getNumber()).orElseThrow(()->null);

        if (otpInfo != null) {
            if (otpStore.getOtp() == otpInfo.getOtp()) {
                {
                    Optional<User> user;
                    UserPrincipal principalUser;
                    user = userRepository.findByNumber(otpStore.getNumber());
                    if(!user.isPresent()){
                        User newUser = new User();
                        newUser.setAuthProvider(AuthProvider.otp);
                        newUser.setProviderId(String.valueOf(otpInfo.getId()));
                        newUser.setUsername("user_"+String.valueOf(otpInfo.getNumber()));
                        newUser.setEmail("user_"+String.valueOf(otpInfo.getNumber())+"@coupanify.com");
                        newUser.setPassword("user@"+String.valueOf(otpInfo.getNumber()));
                        newUser.setNumber(otpInfo.getNumber());
                        principalUser = UserPrincipal.create(userRepository.save(newUser), "ROLE_USER");
                    }else{
                        User newUser = user.get();
                        if(!newUser.getAuthProvider().equals(AuthProvider.otp)) {
                            throw new OAuth2AuthenticationProcessingException("Looks like you are signed up with" +
                                    newUser.getAuthProvider() + "account. Please use your " + newUser.getAuthProvider() + " account to login");
                        }
                        principalUser = UserPrincipal.create(newUser, "ROLE_USER");
                    }
                    return new JWTAuthResponse(tokenProvider.generateToken(principalUser));
                }
            } else {
                throw new OtpException("Invalid OTP");
            }
        }
        return null;

    }

    public void receive(MultiValueMap<String, String> smscallback) {

    }

}
