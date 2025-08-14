package com.web.service.addmix_store.repository;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.web.service.addmix_store.models.User;
import com.web.service.addmix_store.models.VerificationToken;

@Repository
public interface VerificationTokenRepository extends JpaRepository<VerificationToken, Long> {
    public Optional<VerificationToken> findByUser(User user);
}
