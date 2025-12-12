package com.web.service.addmix_store.repository;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import com.web.service.addmix_store.dtos.UserAddressDto;
import com.web.service.addmix_store.models.Address;

@Repository
public interface AddressRepository extends JpaRepository<Address, Long> {

    @Query("""
        SELECT 
            new com.web.service.addmix_store.dtos.UserAddressDto(
                a.id,
                a.street,
                a.city,
                a.state,
                a.country,
                a.phoneNumber,
                a.isDefault
            )
        FROM Address a
        WHERE a.user.id = :userId
    """)
    Optional<List<UserAddressDto>> findAddressesByUserId(@Param("userId") Long userId);

    @Modifying
    @Query("UPDATE Address a SET a.isDefault = false WHERE a.user.id = :userId")
    void clearDefaultForUser(@Param("userId") Long userId);

    List<Address> findByUserId(@Param("userId") Long userId);

}