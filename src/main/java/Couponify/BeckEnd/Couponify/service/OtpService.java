package Couponify.BeckEnd.Couponify.service;

import java.util.Map;

import Couponify.BeckEnd.Couponify.exception.OtpException;
import Couponify.BeckEnd.Couponify.exception.ResourceNotFoundException;
import Couponify.BeckEnd.Couponify.model.AuthRequest;
import Couponify.BeckEnd.Couponify.payload.JWTAuthResponse;

public interface OtpService {

    public boolean sendOtp(String phoneNumber);

    JWTAuthResponse verifyOtpRequest(Object obj) throws OtpException, ResourceNotFoundException;

    JWTAuthResponse verifyAuthRequest(AuthRequest authRequest) throws OtpException, ResourceNotFoundException;

}