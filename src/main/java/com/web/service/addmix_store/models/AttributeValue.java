package com.web.service.addmix_store.models;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import jakarta.persistence.Table;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "attribute_values",
       indexes = {
           @Index(name = "idx_attrval_attribute", columnList = "attribute_id"),
           @Index(name = "idx_attrval_value_en", columnList = "valueEn"),
           @Index(name = "idx_attrval_value_ar", columnList = "valueAr")
       })
@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class AttributeValue extends BaseEntity {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "attribute_id", nullable = false)
    private Attribute attribute;

    @Column(nullable = false) private String valueEn;
    @Column(nullable = false) private String valueAr;
}
