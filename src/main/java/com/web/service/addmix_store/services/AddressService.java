package com.web.service.addmix_store.services;

import org.apache.coyote.BadRequestException;
import org.springframework.stereotype.Service;

import com.web.service.addmix_store.Exceptions.EntityNotFoundException;
import com.web.service.addmix_store.dtos.AddAddressRequest;
import com.web.service.addmix_store.dtos.UserAddressDto;
import com.web.service.addmix_store.models.Address;
import com.web.service.addmix_store.models.User;
import com.web.service.addmix_store.repository.AddressRepository;
import com.web.service.addmix_store.repository.UserRepository;

import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import java.util.Collections;
import java.util.List;

@Service
@AllArgsConstructor
public class AddressService {

    private final AddressRepository addressRepository;
    private final UserRepository userRepository;

    public List<UserAddressDto> getUserAddresses(Long userId) {
        return addressRepository.findAddressesByUserId(userId).orElse(Collections.emptyList());
    }

    @Transactional
    public void deleteAddress(Long addressId){
         Address address = addressRepository.findById(addressId)
                .orElseThrow(() -> new EntityNotFoundException("Address not found"));

        if(address.isDefault()){
            List<Address> addresses= addressRepository.findByUserId(address.getUser().getId());
            if (addresses.size() > 0) {
                Address defaultAddress= addresses.get(0);
                defaultAddress.setDefault(true);
                addressRepository.save(defaultAddress);
            }
        }
        addressRepository.deleteById(addressId);
    }

    @Transactional
    public void addAddress(AddAddressRequest request) {
        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new EntityNotFoundException("User not found"));

        Address address = new Address();
        address.setStreet(request.getStreet());
        address.setCity(request.getCity());
        address.setState(request.getState());
        address.setPhoneNumber(request.getPhoneNumber());
        address.setDefault(request.isDefault());
        address.setUser(user);

        if (request.isDefault()) {
            addressRepository.clearDefaultForUser(user.getId());
        }

        addressRepository.save(address);
    }

    @Transactional
    public void setAddressAsDefault(Long addressId) {
        Address address = addressRepository.findById(addressId)
                .orElseThrow(() -> new EntityNotFoundException("Address not found"));

        Long userId = address.getUser().getId();

        // Clear old defaults
        addressRepository.clearDefaultForUser(userId);

        // Set this one as default
        address.setDefault(true);
        addressRepository.save(address);
    }


}
