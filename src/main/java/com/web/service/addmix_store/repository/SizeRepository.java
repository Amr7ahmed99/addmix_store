package com.web.service.addmix_store.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.web.service.addmix_store.models.Size;

@Repository
public interface SizeRepository extends JpaRepository<Size, Long> {
}
