package Couponify.BeckEnd.Couponify.repository;

import Couponify.BeckEnd.Couponify.model.Role;
import Couponify.BeckEnd.Couponify.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {
    Optional<Role> findByName(String name);
}
