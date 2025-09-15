package com.zinemasterapp.zinemasterapp.repository;

import com.zinemasterapp.zinemasterapp.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;


import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, String> {//a spingboot JPA interface so i can use all kinds of functions
    Optional<User> findByUsername(String username);//SELECT * FROM users WHERE username = ?, in the background
    Optional<User> findByEmail(String email);
    List<User> findByUserTypeAndAccess(String user_type, int access);


    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("update User u set u.unreadNotificationCount = u.unreadNotificationCount + 1 where u.id = :userId")
    int incrementUnread(@Param("userId") String userId);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("update User u set u.unreadNotificationCount = u.unreadNotificationCount + 1 where u.username = :username")
    int incrementUnreadByUsername(@Param("username") String username);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("update User u set u.unreadNotificationCount = 0 where u.username = :username")
    int resetUnread(@Param("username") String username);

    @Query("select u.unreadNotificationCount from User u where u.id = :userId")
    Integer getUnread(@Param("userId") String userId);

    @Modifying
    @Query("UPDATE User u SET u.pendingEmailCount = u.pendingEmailCount + 1 WHERE u.id = :id")
    void incrementPendingEmailCount(@Param("id") String id);

    @Modifying
    @Transactional
    @Query("update User u set u.pendingEmailCount = 0 where u.username = :username and u.pendingEmailCount > 0")
    void resetPendingEmail(@Param("username") String username);

    @Query("SELECT u FROM User u WHERE u.userType = 'ProductAdministrator' AND u.pendingEmailCount > 0")
    List<User> findAdminsWithPending();

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Transactional
    @Query("update User u set u.requestsProcessed = 0 where u.id = :userId")
    int resetProccessedRequests(@Param("userId") String userId);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Transactional
    @Query("update User u set u.requestsProcessed = u.requestsProcessed + 1 where u.id = :id")
    int incrementRequestsProcessed(@Param("id") String id);


    @Query("select u.requestsProcessed from User u where u.id = :id")
    Integer getRequestsProcessed(@Param("id") String id);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("""
         update User u
            set u.unreadNotificationCount = coalesce(u.unreadNotificationCount,0)+coalesce(u.pendingEmailCount,0),
                u.pendingEmailCount = 0
          where u.username = :username
         """)
    void transferPendingToUnread(@Param("username") String username);

    @Modifying
    @Query("update User u set u.pendingEmailCount = 0 where u.id=:userId")
    int resetPending(@Param("userId") String userId);


    @Modifying
    @Query("update User u set u.unseenProcessedStatus = u.unseenProcessedStatus + 1 where u.id = :id")
    void incUnseenProcessedStatus(@Param("id") String id);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("""
    update User u
       set u.unseenProcessedStatus = coalesce(u.unseenProcessedStatus, 0) + coalesce(u.requestsProcessed, 0),
           u.requestsProcessed = 0
     where u.id = :id
""")
    void transferProcessedToUnseen(@Param("id") String id);


    @Query("select u.unseenProcessedStatus from User u where u.id = :id")
    int getUnseenProcessedStatus(@Param("id") String id);

    @Modifying
    @Query("update User u set u.unseenProcessedStatus = 0 where u.id = :id")
    void resetUnseenProcessedStatus(@Param("id") String id);

}

