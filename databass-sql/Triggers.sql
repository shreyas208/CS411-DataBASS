CREATE TRIGGER update_checkin_count
AFTER INSERT ON checkin
FOR EACH ROW
UPDATE user
SET checkin_count = checkin_count+1
WHERE username = NEW.username;

delimiter //

CREATE TRIGGER Welcome_Achievement
AFTER INSERT ON user
FOR EACH ROW
BEGIN
	UPDATE user
    SET score = score +
    (
		SELECT points
        FROM achievement
        WHERE id = "welcome"
    )
    WHERE username = NEW.username;
    
    INSERT INTO achieve
    VALUES(NEW.username, "welcome");
END//

delimiter ;

delimiter //

CREATE TRIGGER five_stars_achievement
AFTER INSERT ON checkin
FOR EACH ROW
BEGIN
IF
(
	SELECT COUNT(*)
	FROM checkin
	WHERE username = NEW.username
) = 5 THEN
CALL attain_achievement(NEW.username, "5_stars");
END IF;
END;//

delimiter ;

CREATE TRIGGER Acquire_Achievement
AFTER UPDATE ON user
FOR EACH ROW
WHEN NEW.checkin_count >= 100
??????
;