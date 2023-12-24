package com.tech45degree.bankingservice.repository;

import com.tech45degree.bankingservice.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

@Repository
public interface UserRepository extends JpaRepository<User,String> {
    User findByCardId(String cardId);
}
