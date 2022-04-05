package Couponify.BeckEnd.Couponify.model;

import lombok.Data;

public class OTPpojo {
    static int otp;
    public static int getOtp() {
        return otp;
    }
    public static void setOtp(int otp) {
        OTPpojo.otp = otp;
    }
}
