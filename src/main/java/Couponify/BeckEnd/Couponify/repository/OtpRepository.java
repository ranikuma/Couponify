package Couponify.BeckEnd.Couponify.repository;

import Couponify.BeckEnd.Couponify.model.OtpStore;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

public interface OtpRepository extends JpaRepository<OtpStore, Long> {
    Optional<OtpStore> findByNumber(String number);
//    OtpStore varifyOtp(OtpStore otpStore);
    OtpStore findByNumber(OtpStore otpStore);
    Boolean existsByNumber(String number);
}
