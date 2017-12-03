CREATE TRIGGER update_checkin_count
AFTER INSERT ON checkin
FOR EACH ROW
UPDATE user
SET checkin_count = checkin_count+1
WHERE username = NEW.username;

CREATE TRIGGER Acquire_Achievement
AFTER UPDATE ON user
FOR EACH ROW
WHEN NEW.checkin_count >= 100
??????
;