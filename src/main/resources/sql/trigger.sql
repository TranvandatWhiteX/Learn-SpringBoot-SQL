-- Function is used to call delete trigger
CREATE OR REPLACE FUNCTION delete_user_roles()
RETURNS TRIGGER AS $$
BEGIN
DELETE FROM user_roles WHERE user_id = OLD.id;
RETURN OLD;
END;
$$ LANGUAGE plpgsql;

-- Trigger is used to delete record in user_roles table
CREATE OR REPLACE TRIGGER trg_delete_user_roles
AFTER DELETE ON users
FOR EACH ROW
EXECUTE FUNCTION delete_user_roles();

-- Function is used to log actions
CREATE OR REPLACE FUNCTION log_user_actions()
RETURNS TRIGGER AS $$
BEGIN
    IF (TG_OP = 'DELETE') THEN
        INSERT INTO action_logs (table_name, action, old_data, action_time)
        VALUES (TG_TABLE_NAME, TG_OP, row_to_json(OLD), NOW());

    ELSIF (TG_OP = 'INSERT') THEN
        INSERT INTO action_logs (table_name, action, new_data, action_time)
        VALUES (TG_TABLE_NAME, TG_OP, row_to_json(NEW), NOW());

    ELSIF (TG_OP = 'UPDATE') THEN
        INSERT INTO action_logs (table_name, action, old_data, new_data, action_time)
        VALUES (TG_TABLE_NAME, TG_OP, row_to_json(OLD), row_to_json(NEW), NOW());
END IF;
RETURN NULL;
END;
$$ LANGUAGE plpgsql;

-- Trigger is used to insert record to log table
CREATE OR REPLACE TRIGGER trg_log_user_actions
AFTER INSERT OR UPDATE OR DELETE ON users
FOR EACH ROW
EXECUTE FUNCTION log_user_actions();

