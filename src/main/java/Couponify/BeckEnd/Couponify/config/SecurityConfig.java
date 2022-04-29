package Couponify.BeckEnd.Couponify.config;

import Couponify.BeckEnd.Couponify.security.CustomUserDetailsService;
import Couponify.BeckEnd.Couponify.security.JwtAuthenticaticationFilter;
import Couponify.BeckEnd.Couponify.security.JwtAuthenticationEntryPoint;
import Couponify.BeckEnd.Couponify.security.oauth2.CustomOAuth2UserService;
import Couponify.BeckEnd.Couponify.security.oauth2.HttpCookieOAuth2AuthorizationRequestRepository;
import Couponify.BeckEnd.Couponify.security.oauth2.OAuth2AuthenticationFailureHandler;
import Couponify.BeckEnd.Couponify.security.oauth2.OAuth2AuthenticationSuccessHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import static org.hibernate.criterion.Restrictions.and;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(
        securedEnabled = true,
        jsr250Enabled = true,
        prePostEnabled = true
)
public class SecurityConfig extends WebSecurityConfigurerAdapter {
/*    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        super.configure(auth);
    }*/
    @Autowired
    CustomUserDetailsService userDetailsService;

    @Autowired
    CustomOAuth2UserService customOAuth2UserService;

    @Autowired
    OAuth2AuthenticationSuccessHandler oAuth2AuthenticationSuccessHandler;

    @Autowired
    OAuth2AuthenticationFailureHandler oAuth2AuthenticationFailureHandler;

    @Autowired
    HttpCookieOAuth2AuthorizationRequestRepository
            httpCookieOAuth2AuthorizationRequestRepository;

    @Autowired
    private JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;

    @Bean
    JwtAuthenticaticationFilter jwtAuthenticaticationFilter() {return new JwtAuthenticaticationFilter(); }

    @Bean
    public HttpCookieOAuth2AuthorizationRequestRepository cookieAuthorizationRequestRepository() {
        return new HttpCookieOAuth2AuthorizationRequestRepository();
    }

    @Bean
    PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .cors()
                .and()
                .csrf().disable()
                .exceptionHandling()
                .authenticationEntryPoint(jwtAuthenticationEntryPoint)
                .and()
                .sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                .authorizeRequests()
                .antMatchers("/auth/login/**").permitAll()
                .antMatchers("/auth/users").permitAll()
                .antMatchers("/oauth2/**").permitAll()
                .antMatchers("/redirect-uri").permitAll()
                .antMatchers("/favicon.ico").permitAll()
                .antMatchers("/solr/**").permitAll()
                .antMatchers("/.env").permitAll()
                .antMatchers("/mobileNo").permitAll()
                .antMatchers("/varifyOTP").permitAll()
                .antMatchers("/otp").permitAll()
                .anyRequest()
                .authenticated()
                .and()
                .oauth2Login()
                .authorizationEndpoint()
                .baseUri("/oauth2/authorize")
                .authorizationRequestRepository(cookieAuthorizationRequestRepository())
                .and()
                .redirectionEndpoint()
                .baseUri("/oauth2/callback/*")
                .and()
                .userInfoEndpoint()
                .userService(customOAuth2UserService)
                .and()
                .successHandler(oAuth2AuthenticationSuccessHandler)
                .failureHandler(oAuth2AuthenticationFailureHandler);

        http
                .addFilterBefore(jwtAuthenticaticationFilter(), UsernamePasswordAuthenticationFilter.class);
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userDetailsService)
                .passwordEncoder(passwordEncoder());
    }

    @Bean
    public AuthenticationManager authenticationManagerBean() throws Exception{
        return super.authenticationManagerBean();
    }

    /*    @Override
    protected UserDetailsService userDetailsService() {
        UserDetails ramesh = User.builder().username("ramesh").password(passwordEncoder().encode("password")).roles("USER").build();
        UserDetails admin = User.builder().username("ranit").password(passwordEncoder().encode("admin")).roles("ADMIN").build();
        return new InMemoryUserDetailsManager(ramesh, admin);
    }*/
}
