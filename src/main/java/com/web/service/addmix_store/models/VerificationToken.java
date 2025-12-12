package com.web.service.addmix_store.models;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "verification_token")
@Builder
public class VerificationToken {

    @Id
    // @GeneratedValue(strategy = GenerationType.IDENTITY)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "verification_token_seq")
    @jakarta.persistence.SequenceGenerator(name = "verification_token_seq", sequenceName = "verification_tokens_id_seq", allocationSize = 1)
    @Column(name = "id", updatable = false, nullable = false)
    private Long id;

    @Column(name = "verification_code", nullable = true)
    private String verificationCode;

    @Column(name = "expiry_date", nullable = true)
    private LocalDateTime expiryDate;

    @Column(name = "attempts")
    @Builder.Default
    private Integer attempts= 1;

    @OneToOne(fetch = FetchType.LAZY)
    @NotNull
    @JsonIgnore
    private User user;
}
