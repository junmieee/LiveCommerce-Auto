package com.example.user.repository;

import com.example.user.entity.User;
import com.example.user.entity.enums.Provider;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
  boolean existsByEmailIgnoreCase(String email);

  Optional<User> findByEmailIgnoreCase(String email);

  boolean existsByProviderAndProviderId(Provider provider, String providerId);
}
