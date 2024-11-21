CREATE
OR REPLACE FUNCTION get_user_with_roles_and_permissions(p_user_id BIGINT)
RETURNS JSONB AS $$
BEGIN
    IF
NOT EXISTS (SELECT 1 FROM users WHERE id = p_user_id) THEN
        RAISE EXCEPTION 'User with id % does not exist', p_user_id;
END IF;
RETURN (SELECT jsonb_build_object(
                       'user_id', u.id,
                       'username', u.username,
                       'email', u.email,
                       'phone_number', u.phone_number,
                       'roles', jsonb_agg(
                               jsonb_build_object(
                                       'role_id', r.id,
                                       'role_name', r.role_name,
                                       'permissions', (SELECT jsonb_agg(
                                                                      jsonb_build_object(
                                                                              'permission_id', p.id,
                                                                              'description', p.description
                                                                      )
                                                              )
                                                       FROM role_permissions rp
                                                                JOIN permissions p ON rp.permission_id = p.id
                                                       WHERE rp.role_id = r.id)
                               )
                                )
               )
        FROM users u
                 LEFT JOIN user_roles ur ON u.id = ur.user_id
                 LEFT JOIN roles r ON ur.role_id = r.id
        WHERE u.id = p_user_id
        GROUP BY u.id);
END;
$$
LANGUAGE plpgsql;
