package com.dattran.practice.domain.services;

import com.dattran.practice.app.dtos.UserDto;
import com.dattran.practice.domain.entities.Role;
import com.dattran.practice.domain.entities.User;
import com.dattran.practice.domain.entities.UserRole;
import com.dattran.practice.domain.entities.UserRoleId;
import com.dattran.practice.domain.repositories.RoleRepository;
import com.dattran.practice.domain.repositories.UserRepository;
import com.dattran.practice.domain.repositories.UserRoleRepository;
import com.dattran.practice.domain.utils.FnCommon;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class UserService {
  UserRepository userRepository;
  UserRoleRepository userRoleRepository;
  RoleRepository roleRepository;
  ObjectMapper objectMapper;

  public User createUser(UserDto userDto) {
    List<Role> roles = new ArrayList<>();
    for (Long roleId : userDto.getRoleIds()) {
      Role role =
          roleRepository.findById(roleId).orElseThrow(() -> new RuntimeException("Role not found"));
      roles.add(role);
    }
    User user = FnCommon.copyNonNullProperties(User.class, userDto);
    assert user != null;
    // Save users
    User savedUser = userRepository.save(user);
    // Save user_roles
    List<UserRole> userRoles = new ArrayList<>();
    roles.forEach(
        role -> {
          UserRoleId userRoleId =
              UserRoleId.builder().userId(savedUser.getId()).roleId(role.getId()).build();
          UserRole userRole = UserRole.builder().id(userRoleId).build();
          userRoles.add(userRole);
        });
    userRoleRepository.saveAll(userRoles);
    return savedUser;
  }

  public Long createUserProcedure(UserDto userDto) {
    Long new_user_id = 0L;
    userRepository.createUser(userDto.getPassword(),
            userDto.getUsername(),
            userDto.getAddress(),
            userDto.getDateOfBirth(),
            userDto.getEmail(),
            userDto.getPhoneNumber(),
            userDto.getRoleIds().toArray(new Long[0]),
            new_user_id);
    return new_user_id;
  }

  public Long createUserProcedure2(UserDto userDto) {
    return userRepository.createUser(userDto.getPassword(),
            userDto.getUsername(),
            userDto.getAddress(),
            userDto.getDateOfBirth(),
            userDto.getEmail(),
            userDto.getPhoneNumber(),
            userDto.getRoleIds().toArray(new Long[0]));
  }

  public Map<String, Object> getUserById(Long id) {
    User user =
        userRepository.findById(id).orElseThrow(() -> new RuntimeException("User not found"));
    Map<String, Object> rawResult = userRepository.findUserWithRolesAndPermissions(id);
    String userInfoJson = (String) rawResult.get("user_info");
    try {
      return objectMapper.readValue(userInfoJson, Map.class);
    } catch (Exception e) {
      throw new RuntimeException("Lỗi khi chuyển đổi JSON: " + e.getMessage(), e);
    }
  }

  public Object getUserByIdFunc(Long id) {
    try {
      return userRepository.getUserWithRolesAndPermissions(id);
    } catch (DataAccessException e) {
      throw new RuntimeException("User with id " + id + " does not exist.");
    }
  }

  // Error: Calling Procedure Returning JSON Causing Exception
  // Not Found Solution
//  public Object getUserByIdProcedure(Long userId) {
//    StoredProcedureQuery query =
//        entityManager.createStoredProcedureQuery("get_user_with_roles_and_permissions_pro");
//    query.registerStoredProcedureParameter("p_user_id", Long.class, ParameterMode.IN);
//    query.registerStoredProcedureParameter("result", String.class, ParameterMode.OUT);
//    query.setParameter("p_user_id", userId);
//    query.execute();
//    Object result = query.getOutputParameterValue("result");
//    return result;
//  }

    public void deleteUser(Long id) {
      userRepository.deleteById(id);
    }
}
