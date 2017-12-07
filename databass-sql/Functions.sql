CREATE FUNCTION gcdist(lat1 FLOAT(7,4), long1 FLOAT(7,4), lat2 FLOAT(7,4), long2 FLOAT(7,4))
RETURNS float(10,4) DETERMINISTIC
RETURN (3959 * acos(cos(radians(lat2)) * cos(radians(lat1)) * cos(radians(long1) - radians(long2)) + sin(radians(lat2)) * sin(radians(lat1))))



delimiter //

CREATE FUNCTION check_achievement(username VARCHAR(255), achievement_name VARCHAR(255))
RETURNS BOOLEAN DETERMINISTIC
BEGIN
	IF NOT EXISTS
    (
		SELECT *
        FROM achieve A
        WHERE A.username = username AND achievement_id = achievement_name
    ) THEN
	RETURN TRUE;
    ELSE
    RETURN FALSE;
    END IF;
END//

delimiter ;