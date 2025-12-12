package com.web.service.addmix_store.services;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.apache.coyote.BadRequestException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.web.service.addmix_store.Exceptions.EmailNotFoundException;
import com.web.service.addmix_store.Exceptions.MobileNotFoundException;
import com.web.service.addmix_store.models.User;
import com.web.service.addmix_store.repository.UserRepository;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    public User findOrCreateGoogleUser(String email, String name, String firstName, String lastName) {
        User user = userRepository.findByEmail(email).orElse(null);
        if (user == null) {
            // Create new user
            user = new User();
            user.setEmail(email);
            user.setFirstName(firstName.isEmpty()? name: firstName);
            user.setLastName(lastName.isEmpty()? "": lastName);
            user.setProvider("GOOGLE");
            user = userRepository.save(user);
        } else {
            // Update existing user info
            user.setIsActive(true);
            user.setFirstName(firstName.isEmpty()? name: firstName);
            user.setLastName(lastName.isEmpty()? "": lastName);
            user = userRepository.save(user);
        }

        return user;
    }


    public boolean existsByEmail(String email){
        return userRepository.existsByEmail(email);
    } 

    public boolean existsByMobileNumber(String mobileNumber){
        return userRepository.existsByMobileNumber(mobileNumber);
    }

    public Optional<User> findByEmail(String email){
        return userRepository.findByEmail(email);
    } 

    public Optional<User> findByProvider(String provider){
        return userRepository.findByProvider(provider);
    }

    public User save(User user){
        return userRepository.save(user);
    }

    public Optional<User> findByMobile(String mobile){
        return userRepository.findByMobileNumber(mobile);
    }

    public Map<String, Object> getUserByEmailOrMobile(String identifier){
        
        boolean userLoggedInByEmail= identifier.matches("^[^\\s@]+@[^\\s@]+\\.[^\\s@]+$");
        User user;

        // Check if matches email regex, search by email; else search by mobile
        if (userLoggedInByEmail) {
            user = userRepository.findByEmail(identifier)
                .orElseThrow(() -> new EmailNotFoundException("Invalid email or password"));
        } else {
            user = userRepository.findByMobileNumber(identifier)
                .orElseThrow(() -> new MobileNotFoundException("Invalid mobile number or password"));
        }

        Map<String, Object> map= new HashMap<>();
        map.put("user", user);
        map.put("userLoggedInByEmail", userLoggedInByEmail);

        return map;
    }

}
