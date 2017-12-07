CREATE VIEW profile AS
SELECT username, email_address, display_name, join_date, checkin_count, score, COALESCE(following_count, 0) AS following_count, COALESCE(follower_count, 0) AS follower_count
FROM user U
LEFT JOIN
(
	SELECT username_from, COUNT(*) AS following_count
    FROM follow
    GROUP BY username_from
) AS following
	ON U.username = following.username_from
LEFT JOIN
(
	SELECT username_to, COUNT(*) AS follower_count
    FROM follow
    GROUP BY username_to
) AS followers
	ON U.username = followers.username_to;

DROP VIEW profile;