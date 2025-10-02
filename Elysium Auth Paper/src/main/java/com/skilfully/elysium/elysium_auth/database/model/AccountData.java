package com.skilfully.elysium.elysium_auth.database.model;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AccountData {
    // 基础信息
    @NotNull
    private String uuid;
    @NotNull
    private String name;
    @NotNull
    private String password;
    private String email;
    private String mobile;
    // 记录
    @NotNull
    @Builder.Default
    private LocalDateTime registerDate = LocalDateTime.now();

    // 下线信息
    private Double offlineLocationX;
    private Double offlineLocationY;
    private Double offlineLocationZ;
    private Double offlineLocationYaw;
    private Double offlineLocationPitch;
    // 封禁信息
    private Boolean ban;
    private String banReason;
    private LocalDateTime banTime;
    private String banner;

}