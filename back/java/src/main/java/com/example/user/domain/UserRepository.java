package com.example.user.domain;

import com.example.user.domain.enums.Provider;
import java.util.Optional;

public interface UserRepository {

  boolean existsByEmailIgnoreCase(String email);

  boolean existsByProviderAndProviderId(Provider provider, String providerId);

  Optional<User> findByEmailIgnoreCase(String email);

  Optional<User> findById(Long id);

  User save(User user);
}
