package Couponify.BeckEnd.Couponify.model;

import lombok.Data;

import javax.persistence.*;

@Data
@Entity
@Table(name = "otpstore", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"number"}),
        @UniqueConstraint(columnNames = {"otp"})
})
public class OtpStore {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    private String number; // Destination
    private int otp;
    private long expirytime;
}
