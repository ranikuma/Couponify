package Couponify.BeckEnd.Couponify.security;

import Couponify.BeckEnd.Couponify.exception.ResourceNotFoundException;
import Couponify.BeckEnd.Couponify.model.Role;
import Couponify.BeckEnd.Couponify.model.User;
import Couponify.BeckEnd.Couponify.repository.UserRepository;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.web.client.ResourceAccessException;

import javax.transaction.Transactional;
import java.util.Collection;
import java.util.Collections;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private UserRepository userRepository;

    public CustomUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    private Collection<? extends GrantedAuthority> mapRolesToAuthorize(Set<Role> roles){
        return roles.stream().map(role -> new SimpleGrantedAuthority(role.getName())).collect(Collectors.toList());
    }

    @Override
    @Transactional
    public UserDetails loadUserByUsername(String userNameOrEmailOrNumber) throws UsernameNotFoundException {
        User user = userRepository.findByUsernameOrEmailOrNumber(userNameOrEmailOrNumber, userNameOrEmailOrNumber, userNameOrEmailOrNumber).orElseThrow(
                () -> new UsernameNotFoundException("User not found with username or email"+userNameOrEmailOrNumber));
        //return new org.springframework.security.core.userdetails.User(user.getEmail(), user.getPassword()
        //                                                                , mapRolesToAuthorize(user.getRoles()));
        return UserPrincipal.create(user);
    }

    @Transactional
    public UserDetails loadUserById(Long id){
        User user = userRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("User", "id", id)
        );
        return UserPrincipal.create(user);
    }
}
