package org.example.patients.security.entities;


import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity @Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AppRole {
    @Id

    private String role;
}
