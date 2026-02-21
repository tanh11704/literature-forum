package com.tpanh.server.modules.auth.service.impl;

import com.tpanh.server.modules.auth.entity.User;
import com.tpanh.server.modules.auth.repository.UserRepository;
import com.tpanh.server.modules.auth.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    @Override
    public String getFullNameById(UUID userId) {
        return userRepository.findById(userId)
                .map(User::getFullName)
                .orElse("Unknown");
    }

    @Override
    public Map<UUID, String> getFullNamesByIds(List<UUID> userIds) {
        return userRepository.findAllById(userIds).stream()
                .collect(Collectors.toMap(User::getId, User::getFullName));
    }

    @Override
    public boolean existsById(UUID userId) {
        return userRepository.existsById(userId);
    }
}
