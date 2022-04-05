package Couponify.BeckEnd.Couponify.repository;

import Couponify.BeckEnd.Couponify.model.JwtBlackList;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface JwtBlackListRepository extends JpaRepository<JwtBlackList, Integer> {
    boolean existsByToken(String token);
}
