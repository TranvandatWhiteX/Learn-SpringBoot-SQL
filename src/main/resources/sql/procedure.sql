-- Function với Procedure không được trùng tên
CREATE
OR REPLACE PROCEDURE get_user_with_roles_and_permissions_pro(p_user_id BIGINT, OUT result JSONB)
LANGUAGE plpgsql AS $$
BEGIN
    IF NOT EXISTS (SELECT 1 FROM users WHERE id = p_user_id) THEN
        RAISE EXCEPTION 'User with id % does not exist', p_user_id;
    END IF;
    result := (
        SELECT jsonb_build_object(
                   'user_id', u.id,
                   'username', u.username,
                   'email', u.email,
                   'phone_number', u.phone_number,
                   'roles', jsonb_agg(
                       jsonb_build_object(
                           'role_id', r.id,
                           'role_name', r.role_name,
                           'permissions', (
                               SELECT jsonb_agg(
                                          jsonb_build_object(
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
               )
        FROM users u
        LEFT JOIN user_roles ur ON u.id = ur.user_id
        LEFT JOIN roles r ON ur.role_id = r.id
        WHERE u.id = p_user_id
        GROUP BY u.id
    );
END;
$$;

-- Procedure Save User
CREATE OR REPLACE PROCEDURE create_user(
    var_password VARCHAR,
    var_username VARCHAR,
    var_address VARCHAR,
    var_dateOfBirth TIMESTAMP,
    var_email VARCHAR,
    var_phoneNumber VARCHAR,
    var_roleIds BIGINT[],
    OUT res BIGINT
)
LANGUAGE plpgsql
AS $$
DECLARE
    new_user_id  BIGINT;
    invalid_role BIGINT;
BEGIN
    -- Kiểm tra các role_id trong một lần truy vấn duy nhất
    SELECT r.id
    INTO invalid_role
    FROM UNNEST(var_roleIds) AS role_id
         LEFT JOIN roles r ON r.id = role_id
    WHERE r.id IS NULL LIMIT 1;

    IF invalid_role IS NOT NULL THEN
        RAISE EXCEPTION 'Role with id % does not exist', invalid_role;
    END IF;

    -- Kiểm tra số điện thoại đã tồn tại
    IF EXISTS (SELECT 1 FROM users WHERE phone_number = var_phoneNumber) THEN
        RAISE EXCEPTION 'Phone number % already exists', var_phoneNumber;
    END IF;

    -- Kiểm tra email đã tồn tại
    IF EXISTS (SELECT 1 FROM users WHERE email = var_email) THEN
        RAISE EXCEPTION 'Email % already exists', var_email;
    END IF;

    BEGIN
        INSERT INTO users (password, username, address, date_of_birth, email, phone_number)
        VALUES (var_password, var_username, var_address, var_dateOfBirth, var_email, var_phoneNumber)
        RETURNING id INTO new_user_id;

        INSERT INTO user_roles (role_id, user_id)
        SELECT role_id, new_user_id
        FROM UNNEST(var_roleIds) AS role_id;

        EXCEPTION
            WHEN OTHERS THEN
                RAISE NOTICE 'Error occurred: %', SQLERRM;
                RAISE;
    END;
    res := new_user_id;
    RAISE NOTICE 'User created with id: %', new_user_id;
END;
$$;
