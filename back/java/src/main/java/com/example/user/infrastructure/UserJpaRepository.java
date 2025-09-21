package com.example.user.infrastructure;

import com.example.user.domain.User;
import com.example.user.domain.UserRepository;
import com.example.user.domain.enums.Provider;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserJpaRepository extends JpaRepository<User, Long>, UserRepository {

  @Override
  boolean existsByEmailIgnoreCase(String email);

  @Override
  Optional<User> findByEmailIgnoreCase(String email);

  @Override
  boolean existsByProviderAndProviderId(Provider provider, String providerId);
}
