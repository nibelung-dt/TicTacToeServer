package com.tarasov.tic_tac_toe.data;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
@Entity
@Table(name = "user_entity")
public class UserEntity {
        @Id
        @Column(name = "id", nullable = false)
        private Long id;
        private String username;
        private String password;
        private String token;
}
