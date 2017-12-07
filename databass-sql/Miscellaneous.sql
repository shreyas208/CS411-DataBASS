SELECT * FROM user;

SELECT * FROM city
LIMIT 0, 5;

SELECT * FROM checkin;

SELECT * FROM follow;

SELECT * FROM achievement;

SELECT * FROM achieve;

INSERT INTO achievement VALUES('traveling_far_and_wide', 'Traveling Far and Wide', 'Recent checkins form an area that contains at least 5000 cities', 100);

UPDATE achievement SET description='' WHERE id = 'traveling_far_and_wide';

DELETE FROM achievement WHERE id = 'traveling_far_and_wide';