package com.tech45degree.notificationservice.repository;

import com.tech45degree.notificationservice.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User,String> {
    User findByCardId(String cardId);
}
