package com.web.service.addmix_store.services;

import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
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

    public User save(User user){
        return userRepository.save(user);
    } 


}
