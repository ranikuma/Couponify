package Couponify.BeckEnd.Couponify.repository;

import Couponify.BeckEnd.Couponify.model.Role;
import Couponify.BeckEnd.Couponify.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String name);
    Optional<User> findByUsernameOrEmailOrNumber(String username, String email, String number);
    Optional<User> findByUsername(String username);
    Optional<User> findByNumber(String number);
    Boolean existsByUsername(String username);
    Boolean existsByEmail(String email);
}
