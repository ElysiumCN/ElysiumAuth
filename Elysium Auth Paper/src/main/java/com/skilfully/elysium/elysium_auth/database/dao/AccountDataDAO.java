package com.skilfully.elysium.elysium_auth.database.dao;

import com.skilfully.elysium.elysium_auth.database.model.AccountData;
import org.jdbi.v3.sqlobject.config.RegisterBeanMapper;
import org.jdbi.v3.sqlobject.customizer.Bind;
import org.jdbi.v3.sqlobject.customizer.BindBean;
import org.jdbi.v3.sqlobject.customizer.BindList;
import org.jdbi.v3.sqlobject.statement.SqlBatch;
import org.jdbi.v3.sqlobject.statement.SqlQuery;
import org.jdbi.v3.sqlobject.statement.SqlUpdate;
import org.jdbi.v3.sqlobject.transaction.Transaction;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@RegisterBeanMapper(AccountData.class)
public interface AccountDataDAO {

    // ============ 插入操作 ============

    @SqlUpdate("INSERT INTO account (uuid, name, password, email, mobile, registerDate, " +
            "offlineLocationX, offlineLocationY, offlineLocationZ, offlineLocationYaw, " +
            "offlineLocationPitch, ban, banReason, banTime, banner) " +
            "VALUES (:uuid, :name, :password, :email, :mobile, :registerDate, " +
            ":offlineLocationX, :offlineLocationY, :offlineLocationZ, :offlineLocationYaw, " +
            ":offlineLocationPitch, :ban, :banReason, :banTime, :banner)")
    void insert(@BindBean AccountData account);

    // ============ 查询操作 ============

    @SqlQuery("SELECT * FROM account WHERE name = :name")
    Optional<AccountData> findByUsername(@Bind("name") String username);

    @SqlQuery("SELECT * FROM account WHERE email = :email")
    Optional<AccountData> findByEmail(@Bind("email") String email);

    @SqlQuery("SELECT * FROM account WHERE uuid = :uuid")
    Optional<AccountData> findByUuid(@Bind("uuid") String uuid);

    @SqlQuery("SELECT * FROM account ORDER BY registerDate DESC")
    List<AccountData> findAll();

    @SqlQuery("SELECT * FROM account WHERE ban = true ORDER BY banTime DESC")
    List<AccountData> findBannedAccounts();

    @SqlQuery("SELECT * FROM account ORDER BY registerDate DESC LIMIT :limit")
    List<AccountData> findRecentRegistrations(@Bind("limit") int limit);

    @SqlQuery("SELECT * FROM account WHERE uuid IN (<uuids>)")
    List<AccountData> findByUuids(@BindList("uuids") List<String> uuids);

    @SqlQuery("SELECT COUNT(*) FROM account WHERE registerDate >= :since")
    int countRegistrationsSince(@Bind("since") LocalDateTime since);

    // ============ 更新操作 ============

    @SqlUpdate("UPDATE account SET name = :name, password = :password, email = :email, " +
            "mobile = :mobile, registerDate = :registerDate, offlineLocationX = :offlineLocationX, " +
            "offlineLocationY = :offlineLocationY, offlineLocationZ = :offlineLocationZ, " +
            "offlineLocationYaw = :offlineLocationYaw, offlineLocationPitch = :offlineLocationPitch, " +
            "ban = :ban, banReason = :banReason, banTime = :banTime, banner = :banner " +
            "WHERE uuid = :uuid")
    boolean update(@BindBean AccountData account);

    @SqlUpdate("UPDATE account SET password = :password WHERE uuid = :uuid")
    boolean updatePassword(@Bind("uuid") String uuid, @Bind("password") String newPassword);

    @SqlUpdate("UPDATE account SET ban = :ban, banReason = :banReason, banTime = :banTime, banner = :banner WHERE uuid = :uuid")
    boolean updateBanStatus(@Bind("uuid") String uuid, @Bind("ban") boolean banned,
                            @Bind("banReason") String reason, @Bind("banTime") LocalDateTime banTime,
                            @Bind("banner") String banner);

    @SqlUpdate("UPDATE account SET offlineLocationX = :x, offlineLocationY = :y, " +
            "offlineLocationZ = :z, offlineLocationYaw = :yaw, offlineLocationPitch = :pitch " +
            "WHERE uuid = :uuid")
    boolean updateOfflineLocation(@Bind("uuid") String uuid, @Bind("x") Double x,
                                  @Bind("y") Double y, @Bind("z") Double z,
                                  @Bind("yaw") Double yaw, @Bind("pitch") Double pitch);

    // ============ 删除操作 ============

    @SqlUpdate("DELETE FROM account WHERE uuid = :uuid")
    boolean delete(@Bind("uuid") String uuid);

    // ============ 存在性检查 ============

    @SqlQuery("SELECT COUNT(*) > 0 FROM account WHERE name = :name")
    boolean existsByUsername(@Bind("name") String username);

    @SqlQuery("SELECT COUNT(*) > 0 FROM account WHERE email = :email")
    boolean existsByEmail(@Bind("email") String email);

    // ============ 统计操作 ============

    @SqlQuery("SELECT COUNT(*) FROM account")
    int countAll();

    @SqlQuery("SELECT COUNT(*) FROM account WHERE ban = true")
    int countBanned();

    // ============ 批量操作 ============

    @SqlBatch("INSERT INTO account (uuid, name, password, email, mobile, registerDate, " +
            "offlineLocationX, offlineLocationY, offlineLocationZ, offlineLocationYaw, " +
            "offlineLocationPitch, ban, banReason, banTime, banner) " +
            "VALUES (:uuid, :name, :password, :email, :mobile, :registerDate, " +
            ":offlineLocationX, :offlineLocationY, :offlineLocationZ, :offlineLocationYaw, " +
            ":offlineLocationPitch, :ban, :banReason, :banTime, :banner)")
    void batchInsert(@BindBean List<AccountData> accounts);

    //
    // ============ 事务性业务方法 ============
    //


}