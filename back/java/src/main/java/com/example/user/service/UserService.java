package com.example.user.service;

import com.example.user.dto.request.RegisterRequest;
import com.example.user.entity.User;
import com.example.user.repository.UserRepository;
import com.example.user.entity.enums.Provider;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;

    public void register(RegisterRequest request) {
        // 이메일 중복 확인
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new IllegalArgumentException("이미 존재하는 이메일입니다.");
        }

        // 엔티티 생성 및 매핑
        User user = new User();
        user.setEmail(request.getEmail());
        user.setName(request.getName());
        user.setProvider(Provider.valueOf(request.getProvider().toUpperCase()));
        user.setProviderId(request.getProviderId());
        user.setIsSeller(Boolean.TRUE.equals(request.getIsSeller()));
        user.setCompanyName(request.getCompanyName());
        user.setBusinessNumber(request.getBusinessNumber());
        user.setContactEmail(request.getContactEmail());
        user.setIsActive(true);
        user.setCreatedAt(LocalDateTime.now());

        if ("local".equalsIgnoreCase(request.getProvider())) {
            if (request.getPassword() == null || request.getPassword().isBlank()) {
                throw new IllegalArgumentException("로컬 회원가입 시 비밀번호는 필수입니다.");
            }
            user.setPassword(passwordEncoder.encode(request.getPassword()));
        }

        userRepository.save(user);
    }
}