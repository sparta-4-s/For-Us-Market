package com.sparta.forusmarket.domain.user.entity;

import com.sparta.forusmarket.common.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@Table(name = "users")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class User extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private String email;

    private String name;

    private String password;

    @Embedded
    private Address address;

    public User(Long id) {
        email = "@@";
        name = "@@";
        address = new Address();
        password = "@@";
    }
}
