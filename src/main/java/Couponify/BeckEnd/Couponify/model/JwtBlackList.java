package Couponify.BeckEnd.Couponify.model;

import lombok.Data;

import javax.persistence.*;

@Data
@Entity
@Table(name = "jstblacklists", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"token"})
        })
public class JwtBlackList {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    private String token;

}
