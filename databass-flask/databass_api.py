# Import all necessary Flask libraries and extensions
from flask import Flask, request, jsonify
import mysql.connector as MySQL # Connects Flask server to MySQL database
from flask_api import status # Handles error codes returned by Flask server
from flask_bcrypt import Bcrypt

# Import libaries for checking the validity of usernames and email addresses
from string import ascii_letters
from string import digits
import re

from math import sin, cos, sqrt, atan2, radians

app = Flask(__name__)
bcrypt = Bcrypt(app)

# Connect to the project database on the VM
db = MySQL.connect(host="localhost", port=3306, user="flaskuser", password="tCU8PvBYEPP4qkun", database="cs_411_project")

# Should I research the "try" and "except" commands?  Should I research trying and catching errors?


# User Registration
@app.route("/api/user/register", methods=["POST"])
def register():
    # Read in registration input parameters
    username = request.values.get('username') # String (a-z, A-Z, 0-9, -, _)
    password = request.values.get('password') # String (6 <= characters <= 256)
    email_address = request.values.get('email_address') # String (valid email)
    display_name = request.values.get('display_name') # String (1 <= characters <= 265)

    # Connect to the MySQL database
    cursor = db.cursor()

    # Check if all the registration input parameters are valid

    # Check if username is valid
    if not all((c in ascii_letters + digits + '-' + '_') for c in username):
        error_code = "user_register_invalid_username"
        cursor.close()

        content = {"success": False, "error_code": error_code}
        return jsonify(content), status.HTTP_400_BAD_REQUEST

    # Check if the username is new
    cursor.execute("SELECT username FROM user WHERE username='" + username + "'")
    result = cursor.fetchone()

    if result:
        error_code = "user_register_username_in_use"
        cursor.close()

        content = {"success": False, "error_code": error_code}
        return jsonify(content), status.HTTP_400_BAD_REQUEST

    # Check if password is valid
    if not (len(password) >= 6 and len(password) <= 256):
        error_code = "user_register_invalid_password"
        cursor.close()

        content = {"success": False, "error_code": error_code}
        return jsonify(content), status.HTTP_400_BAD_REQUEST

    # Check if email_address is valid
    emailRegex = re.compile(r"([a-zA-Z0-9]+)@([a-zA-Z0-9]+)\.([a-zA-Z0-9][a-zA-Z0-9][a-zA-Z0-9])")

    if not emailRegex.match(email_address):
        error_code = "user_register_invalid_email"
        cursor.close()

        content = {"success": False, "error_code": error_code}
        return jsonify(content), status.HTTP_400_BAD_REQUEST

    # Check if display_name is valid
    if not (len(display_name) >= 1 and len(display_name) <= 265):
        error_code = "user_register_invalid_display_name"
        cursor.close()

        content = {"success": False, "error_code": error_code}
        return jsonify(content), status.HTTP_400_BAD_REQUEST

    # If this line of the register() function is reached,
    # all the registration input parameters are valid.

    # Insert registration information into Users table
    password_hash = bcrypt.generate_password_hash(password)

    cursor.execute("INSERT INTO user values('" +
                    username + "', '" + email_address + "', '" + display_name + "', '" +
                    password_hash + "', NOW())")

    db.commit()
    cursor.close()

    content = {"success": True}
    return jsonify(content), status.HTTP_200_OK


# User Login
@app.route("/api/user/login", methods=["POST"])
def login():
    # Read in login input parameters
    username = request.values.get('username') # String (a-z, A-Z, 0-9, -, _)
    password = request.values.get('password') # String (6 <= characters <= 256)

    # Connect to the MySQL database
    cursor = db.cursor()

    # Check if all the login input parameters are valid

    # Check if username is valid
    if not all((c in ascii_letters + digits + '-' + '_') for c in username):
        error_code = "user_login_invalid_username"
        cursor.close()

        content = {"success": False, "error_code": error_code}
        return jsonify(content), status.HTTP_400_BAD_REQUEST

    # Check if password is valid
    if not (len(password) >= 6 and len(password) <= 256):
        error_code = "user_login_invalid_password"
        cursor.close()

        content = {"success": False, "error_code": error_code}
        return jsonify(content), status.HTTP_400_BAD_REQUEST

    # If this line of the login() function is reached,
    # all the login input parameters are valid.

    # Search Users table for login information
    password_hash = bcrypt.generate_password_hash(password)

    cursor.execute("SELECT email_address, display_name, access_token FROM user WHERE username='" + username + "' AND password_hash='" + password_hash + "'")
    result = cursor.fetchone()
    cursor.close()

    if not result:
        error_code = "user_login_bad_credentials"

        content = {"success": False, "error_code": error_code}
        return jsonify(content), status.HTTP_400_BAD_REQUEST
    else:
        email = result[0]
        display_name = result[1]
        access_token = result[2]
        content = {"success": True, "email_address": email, "display_name": display_name, "access_token": access_token}
        return jsonify(content), status.HTTP_200_OK


# User Profile
@app.route("/api/user/profile", methods=["POST"])
def profile():
    # Read in profile input parameters
    username = request.values.get('username') # String (a-z, A-Z, 0-9, -, _)
    access_token = request.values.get('access_token')

    if not all((c in ascii_letters + digits + '-' + '_') for c in username): #check if username is vlaid
        error_code = "user_profile_invalid_username"

        content = {"success": False, "error_code": error_code}
        return jsonify(content), status.HTTP_400_BAD_REQUEST

    cursor = db.cursor()
    cursor.execute("SELECT display_name, join_date, city_id FROM user, checkin WHERE user.username = checkin.username and user.username ='" + username + "'") #query the database for that user
    result = cursor.fetchall()

    if not result:  #if no user exists
        error_code = "user_profile_bad_username"
        cursor.close()

        content = {"success": False, "error_code": error_code}
        return jsonify(content), status.HTTP_400_BAD_REQUEST

    else: #we need to get join_datetime, display_name, num_cities_visited, recent_checkins
        display_name = result[0][0]
        join_datetime = result[0][1]
        num_cities_visited = len(result)
        cursor.execute("SELECT name FROM city WHERE id IN (SELECT city_id FROM user, checkin WHERE user.username = '" + username + "' and checkin.username = '" + username + "')")
        result = cursor.fetchall()
        cursor.close()
        recent_checkins = [i[0] for i in result]
        content = {"success": True, "join_datetime": join_datetime, "display_name": display_name, "num_cities_visited": num_cities_visited, "recent_checkins": recent_checkins}
        return jsonify(content), status.HTTP_200_OK

# City Checkin
@app.route("/api/user/checkin", methods=["POST"])
def checkin():
    # Read in checkin input parameters
    username = request.values.get('username') # String (a-z, A-Z, 0-9, -, _)
    access_token = request.values.get('access_token') # String (6 <= characters <= 256)
    timestamp = request.values.get('timestamp') # String (valid email)
    latitude = request.values.get('latitude') # String (1 <= characters <= 265)
    longitude = request.values.get('longitude') # String (1 <= characters <= 265)

    if not all((c in ascii_letters + digits + '-' + '_') for c in username): #check if username is vlaid
        error_code = "user_checkin_invalid_username"

        content = {"success": False, "error_code": error_code}
        return jsonify(content), status.HTTP_400_BAD_REQUEST

    cursor = db.cursor()

    cursor.execute("SELECT id, latitude, longitude FROM city")
    cities = cursor.fetchall()

    closestDistance = float('inf')
    closestCity = -1

    for city in cities:
        # approximate radius of earth in km
        R = 6373.0

        cityLatitude = radians(city[1])
        cityLongitude = radians(city[2])

        dlon = float(cityLongitude) - float(longitude)
        dlat = float(cityLatitude) - float(latitude)

        a = sin(dlat / 2)**2 + cos(float(latitude)) * cos(float(cityLatitude)) * sin(dlon / 2)**2
        c = 2 * atan2(sqrt(a), sqrt(1 - a))

        distance = R * c

        if distance < closestDistance:
            closestDistance = distance
            closestCity = city[0]

    if closestDistance > 10:
        error_code = "user_checkin_not_close_enough_to_city"

        content = {"success": False, "error_code": error_code}
        return jsonify(content), status.HTTP_400_BAD_REQUEST
    else:
        content = {"success": True, "city_id": closestCity}
        return jsonify(content), status.HTTP_200_OK

@app.route("/")
def root():
    return "You have reached our Flask server."

if __name__ == "__main__":
    app.run()
