package Couponify.BeckEnd.Couponify.model;

import lombok.Data;

@Data
public class AuthRequest {

    private String phoneNumber;
    private int otp;

}
