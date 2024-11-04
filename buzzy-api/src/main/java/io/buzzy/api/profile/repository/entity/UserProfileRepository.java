package io.buzzy.api.profile.repository.entity;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserProfileRepository extends JpaRepository<UserProfile, UUID> {
    Optional<UserProfile> findByUsername(String username);

    @Query("SELECT u " +
            "FROM UserProfile u " +
            "WHERE (LOWER(u.firstname) LIKE LOWER(CONCAT('%', :keyword, '%')) OR LOWER(u.lastname) LIKE LOWER(CONCAT('%', :keyword, '%')) ) " +
            "AND LOWER(u.username) <> LOWER(:loggedInUsername) " +
            "AND u.username NOT IN (SELECT c.username FROM UserProfile currUser JOIN currUser.connections c WHERE LOWER(currUser.username) = LOWER(:loggedInUsername))")
    List<UserProfile> searchUserProfiles(String keyword, String loggedInUsername);
}
