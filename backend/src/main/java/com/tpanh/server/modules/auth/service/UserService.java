package com.tpanh.server.modules.auth.service;

import java.util.List;
import java.util.Map;
import java.util.UUID;


public interface UserService {

    String getFullNameById(UUID userId);

    Map<UUID, String> getFullNamesByIds(List<UUID> userIds);

    boolean existsById(UUID userId);
}
