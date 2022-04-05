package Couponify.BeckEnd.Couponify.controller;

import Couponify.BeckEnd.Couponify.payload.OtpPojo;
import Couponify.BeckEnd.Couponify.repository.StoreOTP;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
//Please Ignore this file
@RestController
public class VarifyOTPController {

    @PostMapping("/varifyOTP")
    public String varifyOTP(@RequestBody OtpPojo sms){
        if(sms.getOtp() == StoreOTP.getOtp())
            return "Coprrect OTP";
        else
            return "incorrect OTP";
    }
}
