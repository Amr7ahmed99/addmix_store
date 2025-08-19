package com.web.service.addmix_store.controllers.auth;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import org.apache.coyote.BadRequestException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
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
import com.web.service.addmix_store.services.WhatsAppService;
import com.web.service.addmix_store.utils.Helpers;
import com.web.service.addmix_store.utils.auth.JwtUtil;

import io.jsonwebtoken.ExpiredJwtException;
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

    private final JwtUtil jwtUtil;

    private final PasswordEncoder passwordEncoder;

    private final EmailService emailService;

    private final WhatsAppService whatsAppService;

    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);

    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequestDto loginRequest, @Nullable @RequestParam boolean error)
        throws Exception, BadRequestException, UsernameNotFoundException{

        // to handle redirect link from google auth handler ("api/auth/login?error=true")
        if(error){
            throw new Exception("Google authentication failed");
        }

        // Check if the user logged in by email or mobile
        Map<String, Object> map= userService.getUserByEmailOrMobile(loginRequest.getEmailOrMobile());
        User user= (User) map.get("user");
        Boolean userLoggedInByEmail= (Boolean) map.get("userLoggedInByEmail");

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

            // Send otp to registred email or mobile
            if(userLoggedInByEmail){
                emailService.sendVerificationEmail(user.getEmail(), verificationCode);
            }else{
                whatsAppService.sendOtp(user.getMobileNumber(), verificationCode);
            }

            return ResponseEntity.ok(new LoginResponseDto("", userResponse));
        }

        // User is active → generate JWT
        String token = jwtUtil.generateAccessToken(user);

        // User is active → generate refresh JWT
        String refreshToken = jwtUtil.generateRefreshToken(user);

        // Build cookie for refresh token
        ResponseCookie cookie= jwtUtil.buildCookieForRefreshToken(refreshToken);

        UserResponseDto userResponse = new UserResponseDto(user);
        return ResponseEntity.ok()
            .header(HttpHeaders.SET_COOKIE, cookie.toString())
            .body(new LoginResponseDto(token, userResponse));
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@Valid @RequestBody RegisterRequestDto registerRequest) throws Exception, BadRequestException{
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
        // TODO: send email if the user select send OTP to email, and to mobile if the user select sent to whatsApp
        // if(!registerRequest.getEmail().isEmpty()){
        //     emailService.sendVerificationEmail(savedUser.getEmail(), verificationCode);
        // }else{
        //     whatsAppService.sendOtp(savedUser.getMobileNumber(), verificationCode);
        // }

        return ResponseEntity.ok(new RegisterResponseDto("Verification code sent to your email", userResponse));
        
    }

    @PostMapping("/register/verify")
    public ResponseEntity<?> verifyEmail(@RequestBody VerifyRegisterRequestDto verifyRegisterRequestDto) throws Exception, BadRequestException{

        // Check if the user logged in by email or mobile
        Map<String, Object> map= userService.getUserByEmailOrMobile(verifyRegisterRequestDto.getEmailOrMobile());
        User user= (User) map.get("user");

        VerificationToken verificationToken= verificationTokenRepository.findByUser(user).orElse(null);

        if(verificationToken == null){
            throw new BadRequestException("Verification code not found for this identifier " + verifyRegisterRequestDto.getEmailOrMobile());
        }

        if (!verificationToken.getVerificationCode().equals(verifyRegisterRequestDto.getVerificationCode())) {
            throw new BadCredentialsException("Invalid code");
        }

        if (verificationToken.getExpiryDate().isBefore(LocalDateTime.now())) {
            throw new BadCredentialsException("Code expired");
        }

        user.setIsActive(true);
        verificationToken.setVerificationCode(null); // clear code
        verificationToken.setExpiryDate(null);
        verificationTokenRepository.save(verificationToken);

        // Generate JWT token for the new user
        String token = jwtUtil.generateAccessToken(user);

        // User is active → generate refresh JWT
        String refreshToken = jwtUtil.generateRefreshToken(user);

        // Build cookie for refresh token
        ResponseCookie cookie= jwtUtil.buildCookieForRefreshToken(refreshToken);

        // Create response
        UserResponseDto userResponse = new UserResponseDto(user);
        LoginResponseDto loginResponse = new LoginResponseDto(token, userResponse);
        
        return ResponseEntity.ok()
            .header(HttpHeaders.SET_COOKIE, cookie.toString())
            .body(loginResponse);
    }

    @GetMapping("/register/verify/resend")
    public ResponseEntity<?> resendVerificationCode(@RequestParam String identifier) throws Exception, BadRequestException{

        // Check if the user logged in by email or mobile
        Map<String, Object> map= userService.getUserByEmailOrMobile(identifier);
        User user= (User) map.get("user");
        Boolean userLoggedInByEmail= (Boolean) map.get("userLoggedInByEmail");

        VerificationToken verificationToken= verificationTokenRepository.findByUser(user)
            .orElseThrow(()-> new BadRequestException("Verification code not found for this identifier " + identifier+ ", try to register again"));
       
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

        // Send otp to registred email or mobile
        if(userLoggedInByEmail){
            emailService.sendVerificationEmail(user.getEmail(), verificationCode);
        }else{
            whatsAppService.sendOtp(user.getMobileNumber(), verificationCode);
        }

        return ResponseEntity.ok("Verification code sent to your email");
        
    }

    @GetMapping("/forget-password/verify")
    public ResponseEntity<?> sendOtpForHandlingNewPassword(@RequestParam String identifier) throws Exception, BadRequestException{

        // Check if the user logged in by email or mobile
        Map<String, Object> map= userService.getUserByEmailOrMobile(identifier);
        User user= (User) map.get("user");
        Boolean userLoggedInByEmail= (Boolean) map.get("userLoggedInByEmail");

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

        // Send otp to registred email or mobile
        if(userLoggedInByEmail){
            emailService.sendVerificationEmail(user.getEmail(), verificationCode);
        }else{
            whatsAppService.sendOtp(user.getMobileNumber(), verificationCode);
        }

        HashMap<String, String> res= new HashMap<>();
        String resMsg= String.format("OTP code sent to your %s", userLoggedInByEmail? "email": "mobile number");
        res.put("message", resMsg);
        return ResponseEntity.ok(res);
    }
    
    @PostMapping("/forget-password/reset")
    public ResponseEntity<?> resetUserPassword(@RequestBody ResetPasswordRequestDto resetPasswordRequestDto) {
        try{
            // Check if the user logged in by email or mobile
            Map<String, Object> map= userService.getUserByEmailOrMobile(resetPasswordRequestDto.getEmailOrMobile());
            User user= (User) map.get("user");

            // Check if the password matches the confirm password
            if(!resetPasswordRequestDto.getPassword().equals(resetPasswordRequestDto.getConfirmPassword())){
                throw new BadCredentialsException("invalid password");
            }

            // Set the new password
            String password= passwordEncoder.encode(resetPasswordRequestDto.getPassword());
            user.setPassword(password);

            // Generate JWT token
            String token = jwtUtil.generateAccessToken(user);

            // Generate refresh JWT
            String refreshToken = jwtUtil.generateRefreshToken(user);

            // Build cookie for refresh token
            ResponseCookie cookie= jwtUtil.buildCookieForRefreshToken(refreshToken);

            // Create response
            UserResponseDto userResponse = new UserResponseDto(user);
            LoginResponseDto loginResponse = new LoginResponseDto(token, userResponse);
            
            userService.save(user);

            return ResponseEntity.ok()
            .header(HttpHeaders.SET_COOKIE, cookie.toString())
            .body(loginResponse);

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

    @PostMapping("/refresh")
    public ResponseEntity<?> refreshToken(@CookieValue(required = false) String refreshToken) {
        if (refreshToken == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Refresh token missing");
        }

        try {
            String email = jwtUtil.extractUserEmail(refreshToken);
            User user = userService.findByEmail(email)
                    .orElseThrow(() -> new UsernameNotFoundException("User not found"));

            if (jwtUtil.validateToken(refreshToken, user)) {
                String newAccessToken = jwtUtil.generateAccessToken(user);

                logger.info("new access token is created");
                return ResponseEntity.ok(Map.of("accessToken", newAccessToken));
            }
        } catch (ExpiredJwtException e) {
            logger.error("Refresh token expired", e.getMessage());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Refresh token expired, please login again");
        }

        logger.error("Invalid refresh token");
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid refresh token");
    }

}
