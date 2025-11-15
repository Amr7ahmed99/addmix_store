package com.web.service.addmix_store.repository;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import com.web.service.addmix_store.models.Brand;

@Repository
public interface BrandRepository extends JpaRepository<Brand, Long> {

    @Query("SELECT b FROM Brand b WHERE b.nameEn = :nameEn OR b.nameAr = :nameAr")
    Optional<Brand> findByNameEnOrNameAr(@Param("nameEn") String nameEn,@Param("nameAr") String nameAr);

    Optional<Brand> findById(@Param("brandId") Long brandId);
}