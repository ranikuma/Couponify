package Couponify.BeckEnd.Couponify.controller;

import Couponify.BeckEnd.Couponify.exception.ResourceNotFoundException;
import Couponify.BeckEnd.Couponify.model.User;
import Couponify.BeckEnd.Couponify.payload.JWTAuthResponse;
import Couponify.BeckEnd.Couponify.repository.UserRepository;
import Couponify.BeckEnd.Couponify.security.CurrentUser;
import Couponify.BeckEnd.Couponify.security.UserPrincipal;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

@RestController
public class UserController {
    @Autowired
    private UserRepository userRepository;

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/user/me")
    public User getCurrentUser(@CurrentUser UserPrincipal userPrincipal){
        return userRepository.findById(userPrincipal.getId())
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", userPrincipal.getId()));
    }

    @GetMapping("/redirect-uri")
    public ResponseEntity<?> callback(HttpServletRequest request){
        System.out.println("return roken");
        String token = request.getParameter("token");
        return ResponseEntity.ok(new JWTAuthResponse(token));
    }
}
