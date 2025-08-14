package com.web.service.addmix_store.controllers.auth;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import org.apache.coyote.BadRequestException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import com.web.service.addmix_store.dtos.LoginRequestDto;
import com.web.service.addmix_store.dtos.LoginResponseDto;
import com.web.service.addmix_store.dtos.RegisterRequestDto;
import com.web.service.addmix_store.dtos.RegisterResponseDto;
import com.web.service.addmix_store.dtos.ResetPasswordRequestDto;
import com.web.service.addmix_store.dtos.UserResponseDto;
import com.web.service.addmix_store.dtos.VerifyRegisterRequestDto;
import com.web.service.addmix_store.models.User;
import com.web.service.addmix_store.models.VerificationToken;
import com.web.service.addmix_store.repository.VerificationTokenRepository;
import com.web.service.addmix_store.services.EmailService;
import com.web.service.addmix_store.services.UserService;
import com.web.service.addmix_store.services.auth.CustomUserDetailsService;
import com.web.service.addmix_store.utils.Helpers;
import com.web.service.addmix_store.utils.auth.JwtUtil;
import io.micrometer.common.lang.Nullable;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.Map;


@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "http://localhost:3000", allowCredentials = "true")
@RequiredArgsConstructor
public class AuthController {

    private final UserService userService;

    private final VerificationTokenRepository verificationTokenRepository;

    private final CustomUserDetailsService userDetailsService;

    private final JwtUtil jwtUtil;

    private final PasswordEncoder passwordEncoder;

    private final EmailService emailService;

    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);

    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequestDto loginRequest, @Nullable @RequestParam boolean error) {
        
        try {

            // to handle redirect link from google auth handler ("api/auth/login?error=true")
            if(error){
                throw new Exception("Google authentication failed");
            }

            // Authenticate user
            // authenticationManager.authenticate(
            //     new UsernamePasswordAuthenticationToken(loginRequest.getEmail(), loginRequest.getPassword())
            // );

            // Load user manually (without throwing disabled error)
            User user = this.userService.findByEmail(loginRequest.getEmail())
                .orElseThrow(() -> new UsernameNotFoundException("Invalid email or password"));


            // Check password manually
            if (!passwordEncoder.matches(loginRequest.getPassword(), user.getPassword())) {
                throw new UsernameNotFoundException("Invalid email or password");
            }

            // If user is inActive, send verification code
            if (!user.isEnabled()) {
                // Generate random verification code and 
                String verificationCode= Helpers.generateVerificationCode();

                VerificationToken verificationToken = verificationTokenRepository.findByUser(user)
                        .orElse(new VerificationToken());
                verificationToken.setUser(user);
                verificationToken.setVerificationCode(verificationCode);
                verificationToken.setExpiryDate(LocalDateTime.now().plusMinutes(2));
                verificationTokenRepository.save(verificationToken);

                UserResponseDto userResponse = new UserResponseDto(user);

                // Send otp to registred email
                emailService.sendVerificationEmail(user.getEmail(), verificationCode);

                return ResponseEntity.ok(new LoginResponseDto("", userResponse));
            }

            // User is active → generate JWT
            String token = jwtUtil.generateToken(user);
            UserResponseDto userResponse = new UserResponseDto(user);
            return ResponseEntity.ok(new LoginResponseDto(token, userResponse));

        } catch (UsernameNotFoundException e) {
            Map<String, String> err = new HashMap<>();
            err.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(err);
        } catch (Exception e) {
            logger.error("Verification failed: {}", e.getMessage());
            Map<String, String> err = new HashMap<>();
            err.put("message",  "something went wrong, please try again letter");
            return ResponseEntity.badRequest().body(err);
        }
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@Valid @RequestBody RegisterRequestDto registerRequest) {
        try {

            // Check if email already exists
            if (userService.existsByEmail(registerRequest.getEmail())) {
                throw new BadRequestException("Email is already registered");
            }

            // Check if mobile number already exists
            if (userService.existsByMobileNumber(registerRequest.getMobileNumber())) {
                throw new BadRequestException("Mobile number is already registered");
            }

            // Create new user
            User user = new User();
            user.setFirstName(registerRequest.getFirstName());
            user.setLastName(registerRequest.getLastName());
            user.setEmail(registerRequest.getEmail());
            user.setPassword(passwordEncoder.encode(registerRequest.getPassword()));
            user.setMobileNumber(registerRequest.getMobileNumber());
            user.setIsActive(false);

            // Save user to database
            User savedUser = userService.save(user);

            // Generate random verification code and 
            String verificationCode= Helpers.generateVerificationCode();

            // Save user record to verification_token
            VerificationToken verificationToken= new VerificationToken();
            verificationToken.setUser(savedUser);
            verificationToken.setVerificationCode(verificationCode);
            verificationToken.setExpiryDate(LocalDateTime.now().plusMinutes(2));
            verificationTokenRepository.save(verificationToken);

            UserResponseDto userResponse = new UserResponseDto(savedUser);

            // Send otp to registred email
            emailService.sendVerificationEmail(savedUser.getEmail(), verificationCode);

            return ResponseEntity.ok(new RegisterResponseDto("Verification code sent to your email", userResponse));
        }catch (BadRequestException e) {
            Map<String, String> error = new HashMap<>();
            logger.error("Registration failed: {}", e.getMessage());
            error.put("message",  e.getMessage());
            return ResponseEntity.badRequest().body(error);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            logger.error("Registration failed: {}", e.getMessage());
            error.put("message",  "something went wrong, please try again letter");
            return ResponseEntity.badRequest().body(error);
        }
    }

    @PostMapping("/register/verify")
    public ResponseEntity<?> verifyEmail(@RequestBody VerifyRegisterRequestDto verifyRegisterRequestDto) {

        try{
            User user = userService.findByEmail(verifyRegisterRequestDto.getEmail())
            .orElseThrow(() -> new UsernameNotFoundException("User not found"));

            VerificationToken verificationToken= verificationTokenRepository.findByUser(user).orElse(null);

            if(verificationToken == null){
                return ResponseEntity.badRequest().body("Verification code not found for this email " + user.getEmail());
            }

            if (!verificationToken.getVerificationCode().equals(verifyRegisterRequestDto.getVerificationCode())) {
                return ResponseEntity.badRequest().body("Invalid code");
            }

            if (verificationToken.getExpiryDate().isBefore(LocalDateTime.now())) {
                return ResponseEntity.badRequest().body("Code expired");
            }

            user.setIsActive(true);
            verificationToken.setVerificationCode(null); // clear code
            verificationToken.setExpiryDate(null);
            verificationTokenRepository.save(verificationToken);

            // Generate JWT token for the new user
            String token = jwtUtil.generateToken(user);

            // Create response
            UserResponseDto userResponse = new UserResponseDto(user);
            LoginResponseDto loginResponse = new LoginResponseDto(token, userResponse);
            
            return ResponseEntity.ok(loginResponse);

        }catch (UsernameNotFoundException e) {
            logger.error("Verification failed: {}", e.getMessage());
            Map<String, String> error = new HashMap<>();
            error.put("message",  e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }catch (Exception e) {
            logger.error("Verification failed: {}", e.getMessage());
            Map<String, String> error = new HashMap<>();
            error.put("message",  "something went wrong, please try again letter");
            return ResponseEntity.badRequest().body(error);
        }
    }

    @GetMapping("/register/verify/resend")
    public ResponseEntity<?> resendVerificationCode(@RequestParam String email){ 
        User user = userService.findByEmail(email)
            .orElseThrow(() -> new RuntimeException("User not found"));

        VerificationToken verificationToken= verificationTokenRepository.findByUser(user).orElse(null);
        if(verificationToken == null){
            return ResponseEntity.badRequest().body("Verification code not found for this email " + user.getEmail()+ " try to register again");
        }

        Integer maxAttemptsPerTenMinutes= 3;
        long minutesBetween = Math.abs(ChronoUnit.MINUTES.between(verificationToken.getExpiryDate(), LocalDateTime.now()));

        // if the minutes between is exceed 10m we consider it first attempts, else we increment the attempts value
        if(minutesBetween >= 10){
            verificationToken.setAttempts(1);
        }else{
            if(verificationToken.getAttempts() >= maxAttemptsPerTenMinutes){
                return ResponseEntity.badRequest().body("you cann't resend another verification code now, please try again letter");
            }
            verificationToken.setAttempts(verificationToken.getAttempts() + 1);
        }


        // Generate random verification code and 
        String verificationCode= Helpers.generateVerificationCode();

        // Save user record to verification_token
        verificationToken.setVerificationCode(verificationCode);
        verificationToken.setExpiryDate(LocalDateTime.now().plusMinutes(2));
        verificationTokenRepository.save(verificationToken);

        // Send otp to registred email
        emailService.sendVerificationEmail(email, verificationCode);

        return ResponseEntity.ok("Verification code sent to your email");
        
    }

    @GetMapping("/forget-password/verify")
    public ResponseEntity<?> sendOtpForHandlingNewPassword(@RequestParam String email) {

        try{
            User user = userService.findByEmail(email)
            .orElseThrow(() -> new BadCredentialsException("User not found"));

            // Generate random verification code and 
            String verificationCode= Helpers.generateVerificationCode();

            // Save user record to verification_token
            VerificationToken verificationToken= verificationTokenRepository.findByUser(user).orElse(null);
            if(verificationToken == null){
                verificationToken= new VerificationToken();
                verificationToken.setUser(user);
            }
            verificationToken.setVerificationCode(verificationCode);
            verificationToken.setExpiryDate(LocalDateTime.now().plusMinutes(2));
            verificationTokenRepository.save(verificationToken);

            // Send otp to registred email
            // emailService.sendVerificationEmail(email, verificationCode);

            HashMap<String, String> res= new HashMap<>();
            res.put("message", "Verification code sent to your email");
            return ResponseEntity.ok(res);

        }catch (BadCredentialsException e) {
            Map<String, String> error = new HashMap<>();
            logger.error("sendOtpForHandlingNewPassword: {}", e.getMessage());
            error.put("message",  e.getMessage());
            return ResponseEntity.badRequest().body(error);
            
        }catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            logger.error("sendOtpForHandlingNewPassword: {}", e.getMessage());
            error.put("message",  "something went wrong, please try again letter");
            return ResponseEntity.badRequest().body(error);
        }
    }
    
    @PostMapping("/forget-password/reset")
    public ResponseEntity<?> resetUserPassword(@RequestBody ResetPasswordRequestDto resetPasswordRequestDto) {
        try{
            User user= userService.findByEmail(resetPasswordRequestDto.getEmail())
                .orElseThrow(() -> new BadCredentialsException("invalid email"));

            // Check if the password matches the confirm password
            if(!resetPasswordRequestDto.getPassword().equals(resetPasswordRequestDto.getConfirmPassword())){
                throw new BadCredentialsException("invalid password");
            }

            // Set the new password
            String password= passwordEncoder.encode(resetPasswordRequestDto.getPassword());
            user.setPassword(password);

            // Generate JWT token for the new user
            String token = jwtUtil.generateToken(user);

            // Create response
            UserResponseDto userResponse = new UserResponseDto(user);
            LoginResponseDto loginResponse = new LoginResponseDto(token, userResponse);
            
            userService.save(user);

            return ResponseEntity.ok(loginResponse);
        }catch (BadCredentialsException e) {
            Map<String, String> error = new HashMap<>();
            logger.error("resetUserPassword: {}", e.getMessage());
            error.put("message",  e.getMessage());
            return ResponseEntity.badRequest().body(error);
            
        }catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            logger.error("resetUserPassword: {}", e.getMessage());
            error.put("message",  "something went wrong, please try again letter");
            return ResponseEntity.badRequest().body(error);
        }
    }

    @GetMapping("/validate")
    public ResponseEntity<?> validateToken(@RequestHeader("Authorization") String token) {
        try {
            if (token.startsWith("Bearer ")) {
                token = token.substring(7);
            }
            
            String email = jwtUtil.extractUserEmail(token);
            User user = userService.findByEmail(email).orElse(null);
            
            if (jwtUtil.validateToken(token, user)) {
                UserResponseDto userResponse = new UserResponseDto(user);
                return ResponseEntity.ok(userResponse);
            } else {
                return ResponseEntity.badRequest().body("Invalid token");
            }
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Token validation failed");
        }
    }

}
