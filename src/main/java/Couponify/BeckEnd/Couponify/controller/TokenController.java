package Couponify.BeckEnd.Couponify.controller;

import Couponify.BeckEnd.Couponify.exception.OtpException;
import Couponify.BeckEnd.Couponify.model.OTPpojo;
import Couponify.BeckEnd.Couponify.model.OtpStore;
import Couponify.BeckEnd.Couponify.payload.JWTAuthResponse;
import Couponify.BeckEnd.Couponify.payload.OtpPojo;
import Couponify.BeckEnd.Couponify.repository.OtpRepository;
import Couponify.BeckEnd.Couponify.security.JwtTokenProvider;
import Couponify.BeckEnd.Couponify.service.OtpService;
import Couponify.BeckEnd.Couponify.service.SmsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TokenController {

    @Autowired
    OtpRepository otpRepository;

    @Autowired
    JwtTokenProvider jwtTokenProvider;

    @Autowired
    SmsService smsService;

    @PostMapping("/otp")
    public ResponseEntity<JWTAuthResponse> ValidateToken(@RequestBody OtpPojo otp) throws OtpException{
        OtpStore sentotp= otpRepository.findByNumber(otp.getNumber()).orElseThrow(()->new OtpException());
        System.out.println(sentotp+":"+otp.getOtp());
        return new ResponseEntity<JWTAuthResponse>(smsService.verifyAuthRequest(otp), HttpStatus.OK);
//        if(sentotp.getOtp()==otp.getOtp()) {
//            return new ResponseEntity<JWTAuthResponse>(new JWTAuthResponse(jwtTokenProvider.generateToken(sentotp)), HttpStatus.OK);
//        }
    }

}
