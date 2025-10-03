package com.skilfully.elysium.elysium_auth.database.entity;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.time.LocalDateTime;

@Entity
@Table(name = "account")
@Getter
@Setter
@Accessors(chain = true)
public class AccountData {
    // 基础信息
    @Id
    @Column(name = "uuid", nullable = false, unique = true)
    private String uuid;
    @Column(name = "name", nullable = false)
    private String name;
    @Column(name = "password", nullable = false)
    private String password;
    @Column(name = "email")
    private String email;
    @Column(name = "mobile")
    private String mobile;

    // 记录
    @Column(name = "registerDate", nullable = false)
    private LocalDateTime registerDate;

    // 下线信息
    @Column(name = "offlineLocationX", nullable = false)
    private Double offlineLocationX;
    @Column(name = "offlineLocationY", nullable = false)
    private Double offlineLocationY;
    @Column(name = "offlineLocationZ", nullable = false)
    private Double offlineLocationZ;
    @Column(name = "offlineLocationYaw", nullable = false)
    private Double offlineLocationYaw;
    @Column(name = "offlineLocationPitch", nullable = false)
    private Double offlineLocationPitch;

    // 封禁信息
    @Column(name = "ban", nullable = false)
    private Boolean ban;
    @Column(name = "banReason")
    private String banReason;
    @Column(name = "banTime")
    private LocalDateTime banTime;
    @Column(name = "banner")
    private String banner;

}