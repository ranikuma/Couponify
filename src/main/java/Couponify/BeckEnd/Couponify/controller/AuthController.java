package Couponify.BeckEnd.Couponify.controller;

import Couponify.BeckEnd.Couponify.model.AuthProvider;
import Couponify.BeckEnd.Couponify.model.JwtBlackList;
import Couponify.BeckEnd.Couponify.model.Role;
import Couponify.BeckEnd.Couponify.model.User;
import Couponify.BeckEnd.Couponify.payload.ApiResponse;
import Couponify.BeckEnd.Couponify.payload.JWTAuthResponse;
import Couponify.BeckEnd.Couponify.payload.LoginDto;
import Couponify.BeckEnd.Couponify.payload.SignUpDto;
import Couponify.BeckEnd.Couponify.repository.JwtBlackListRepository;
import Couponify.BeckEnd.Couponify.repository.RoleRepository;
import Couponify.BeckEnd.Couponify.repository.UserRepository;
import Couponify.BeckEnd.Couponify.security.JwtTokenProvider;
import net.bytebuddy.dynamic.DynamicType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.parameters.P;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.net.URI;
import java.util.Collections;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/auth")
public class AuthController{

    //private final InMemoryUserDetailsManager inMemoryUserDetailsManager;

    //public AuthController(InMemoryUserDetailsManager inMemoryUserDetailsManager){
    //    this.inMemoryUserDetailsManager = inMemoryUserDetailsManager;
    //}
    @Autowired(required = true)
    private AuthenticationManager authenticationManager;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    @Autowired
    private JwtBlackListRepository jwtBlackListRepository;

    @PostMapping("/login/session")
    public ResponseEntity<JWTAuthResponse> authenticateUser(@RequestBody LoginDto loginDto){
        Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
                loginDto.getUsernameOrEmail(), loginDto.getPassword()));

        SecurityContextHolder.getContext().setAuthentication(authentication);
        String token = jwtTokenProvider.generateToken(authentication);
        return ResponseEntity.ok(new JWTAuthResponse(token));
    }

    //Signout Feature
    @PostMapping("/logout/session")
    public ResponseEntity<String> signoutUser(HttpServletRequest request) throws IOException {
        String bearerToken = request.getHeader("Authorization");
        System.out.println("==============");
        System.out.println(bearerToken);
        String token = null;
        token = bearerToken.substring(7, bearerToken.length());
        JwtBlackList jwtBlackList = new JwtBlackList();
        jwtBlackList.setToken(token);
        jwtBlackListRepository.save(jwtBlackList);
        return ResponseEntity.ok("Successfully logged out");
    }

    //change the password

    //Add the user
    @PostMapping("/users")
    public ResponseEntity<?> registerUser(@RequestBody SignUpDto signUpDto){
        if(userRepository.existsByUsername(signUpDto.getUsername())){
            return new ResponseEntity<>("Username already taken",HttpStatus.BAD_REQUEST);
        }
        if(userRepository.existsByEmail(signUpDto.getEmail())){
            return new ResponseEntity<>("EmailId already taken", HttpStatus.BAD_REQUEST);
        }
        User user = new User();
        user.setEmail(signUpDto.getEmail());
        user.setUsername(signUpDto.getUsername());
        user.setPassword(passwordEncoder.encode(signUpDto.getPassword()));
        user.setAuthProvider(AuthProvider.local);
        Role role = roleRepository.findByName("ROLE_ADMIN").get();
        user.setRoles(Collections.singleton(role));
        User result = userRepository.save(user);
        URI location = ServletUriComponentsBuilder
                .fromCurrentContextPath().path("/user/me")
                .buildAndExpand(result.getId()).toUri();
        //return new ResponseEntity<>("User Registered successfully", HttpStatus.OK);
        System.out.println("This is end of sungnup");
        return ResponseEntity.created(location)
                .body(new ApiResponse(true, "User registered successfully@"));
    }
    /*@PostMapping("/users")
    public ResponseEntity<?> registerUser(@RequestBody SignUpDto signUpDto){
        inMemoryUserDetailsManager.createUser(User.withUsername(signUpDto.getUsername()).password(signUpDto.getPassword()).roles("USER").build());
        return new ResponseEntity<>((signUpDto.getUsername() + " created!"), HttpStatus.OK);
    }*/
}
