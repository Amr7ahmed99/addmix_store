package com.web.service.addmix_store.models;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import jakarta.persistence.Table;
import jakarta.persistence.Index;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "attributes",
       indexes = {
           @Index(name = "idx_attribute_name_en", columnList = "nameEn"),
           @Index(name = "idx_attribute_name_ar", columnList = "nameAr")
       })
@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class Attribute extends BaseEntity {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false) private String nameEn;
    @Column(nullable = false) private String nameAr;
}

