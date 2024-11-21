package com.dattran.practice.domain.repositories;

import com.dattran.practice.domain.entities.User;
import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.query.Procedure;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.Map;

public interface UserRepository extends JpaRepository<User, Long> {
    @Query(value = """
        SELECT json_build_object(
                   'user_id', u.id,
                   'username', u.username,
                   'email', u.email,
                   'phone_number', u.phone_number,
                   'roles', json_agg(
                       json_build_object(
                           'role_id', r.id,
                           'role_name', r.role_name,
                           'permissions', (
                               SELECT json_agg(
                                          json_build_object(
                                              'permission_id', p.id,
                                              'description', p.description
                                          )
                                      )
                               FROM role_permissions rp
                               JOIN permissions p ON rp.permission_id = p.id
                               WHERE rp.role_id = r.id
                           )
                       )
                   )
               ) AS user_info
        FROM users u
        LEFT JOIN user_roles ur ON u.id = ur.user_id
        LEFT JOIN roles r ON ur.role_id = r.id
        WHERE u.id = :userId
        GROUP BY u.id
    """, nativeQuery = true)
    Map<String, Object> findUserWithRolesAndPermissions(@Param("userId") Long userId);

    @Query(value = "SELECT get_user_with_roles_and_permissions(:userId)", nativeQuery = true)
    Object getUserWithRolesAndPermissions(@Param("userId") Long userId);

    // Can Call Procedure But Result Is Null
    @Query(value = "CALL get_user_with_roles_and_permissions_pro(:userId, :res)", nativeQuery = true)
    void getUserWithRolesAndPermissions(@Param("userId") long userId, @Param("res") JsonNode result);

    @Query(value = "CALL create_user(:var_password, :var_username, :var_address, :var_dateOfBirth, :var_email, :var_phoneNumber, :var_roleIds, :new_user_id)", nativeQuery = true)
    void createUser(@Param("var_password") String var_password,
                    @Param("var_username") String var_username,
                    @Param("var_address") String var_address,
                    @Param("var_dateOfBirth") LocalDate var_dateOfBirth,
                    @Param("var_email") String var_email,
                    @Param("var_phoneNumber") String var_phoneNumber,
                    @Param("var_roleIds") Long[] var_roleIds,
                    @Param("new_user_id") Long new_user_id);

    @Procedure("create_user")
    Long createUser(String var_password,
                    String var_username,
                    String var_address,
                    LocalDate var_dateOfBirth,
                    String var_email,
                    String var_phoneNumber,
                    Long[] var_roleIds);
}
