# Import all necessary Flask libraries and extensions
from flask import Flask, request, jsonify
import mysql.connector as MySQL # Connects Flask server to MySQL database
from flask_api import status # Handles error codes returned by Flask server
from flask_bcrypt import Bcrypt
import secrets # Generates access tokens

# Import libaries for checking the validity of usernames and email addresses
from string import ascii_letters
from string import digits
import re

from math import sin, cos, sqrt, atan2, radians

app = Flask(__name__)
bcrypt = Bcrypt(app)

# Connect to the project database on the VM
db = MySQL.connect(host="localhost", port=3306, user="flaskuser", password="tCU8PvBYEPP4qkun", database="cs_411_project")


# User Registration
@app.route("/api/user/register", methods=["POST"])
def register():
    # Read in registration input parameters
    username = request.values.get('username') # String (a-z, A-Z, 0-9, -, _)
    password = request.values.get('password') # String (6 <= characters <= 256)
    email_address = request.values.get('email_address') # String (valid email)
    display_name = request.values.get('display_name') # String (1 <= characters <= 265)

    # Check if all the registration input parameters are valid

    # Check if username is valid
    if not all((c in ascii_letters + digits + '-' + '_') for c in username):
        error_code = "user_register_invalid_username"
        
        content = {"success": False, "error_code": error_code}
        return jsonify(content), status.HTTP_400_BAD_REQUEST

    # Check if password is valid
    if not (len(password) >= 6 and len(password) <= 256):
        error_code = "user_register_invalid_password"

        content = {"success": False, "error_code": error_code}
        return jsonify(content), status.HTTP_400_BAD_REQUEST

    # Check if email_address is valid
    emailRegex = re.compile(r"([a-zA-Z0-9]+)@([a-zA-Z0-9]+)\.([a-zA-Z0-9][a-zA-Z0-9][a-zA-Z0-9])")

    if not emailRegex.match(email_address):
        error_code = "user_register_invalid_email"

        content = {"success": False, "error_code": error_code}
        return jsonify(content), status.HTTP_400_BAD_REQUEST

    # Check if display_name is valid
    if not (len(display_name) >= 1 and len(display_name) <= 265):
        error_code = "user_register_invalid_display_name"

        content = {"success": False, "error_code": error_code}
        return jsonify(content), status.HTTP_400_BAD_REQUEST

    # Connect to the MySQL database
    cursor = None

    try:
        cursor = db.cursor()
    except:
        error_code = "connection_to_database_failed"

        content = {"success": False, "error_code": error_code}
        print(traceback.format_exc())
        return jsonify(content), status.HTTP_500_INTERNAL_SERVER_ERROR

    # Check if the username is new
    cursor.execute("SELECT username FROM user WHERE username='" + username + "'")
    result = cursor.fetchone()

    if result:
        error_code = "user_register_username_in_use"
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

    # Check if username is valid
    if not all((c in ascii_letters + digits + '-' + '_') for c in username):
        error_code = "user_login_invalid_username"

        content = {"success": False, "error_code": error_code}
        return jsonify(content), status.HTTP_400_BAD_REQUEST

    # Check if password is valid
    if not (len(password) >= 6 and len(password) <= 256):
        error_code = "user_login_invalid_password"

        content = {"success": False, "error_code": error_code}
        return jsonify(content), status.HTTP_400_BAD_REQUEST

    # Connect to the MySQL database
    cursor = None

    try:
        cursor = db.cursor()
    except:
        error_code = "connection_to_database_failed"

        content = {"success": False, "error_code": error_code}
        print(traceback.format_exc())
        return jsonify(content), status.HTTP_500_INTERNAL_SERVER_ERROR

    # If this line of the login() function is reached,
    # all the login input parameters are valid.

    # Search user table for password hash to check if the password is correct
    cursor.execute("SELECT email_address, display_name, password_hash FROM user WHERE username='" + username + "'")
    result = cursor.fetchone()

    # Return a bad login credential error if the username isn't in the table
    if not result:
        error_code = "user_login_bad_credentials"
        cursor.close()

        content = {"success": False, "error_code": error_code}
        return jsonify(content), status.HTTP_400_BAD_REQUEST

    # Check if the input password matches the password hash in the user table and therefore, correct
    isCorrectPassword = bcrypt.check_password_hash(result[2], password)

    # Return a bad login credential error if the password isn't correct
    if not isCorrectPassword:
        error_code = "user_login_bad_credentials"
        cursor.close()

        content = {"success": False, "error_code": error_code}
        return jsonify(content), status.HTTP_400_BAD_REQUEST
    else:
        email = result[0]
        display_name = result[1]

        # Generate an access token and insert it into the user table
        access_token = secrets.token_hex(16)

        cursor.execute("UPDATE user SET access_token='" + access_token + "' WHERE username='" + username + "'")
        db.commit()
        cursor.close()

        content = {"success": True, "email_address": email, "display_name": display_name, "access_token": access_token}
        return jsonify(content), status.HTTP_200_OK


# User Logout
@app.route("/api/user/logout", methods=["POST"])
def logout():
    # Read in login input parameters
    username = request.values.get('username') # String (a-z, A-Z, 0-9, -, _)
    access_token = request.values.get('access_token')

    # Check if username is valid
    if not all((c in ascii_letters + digits + '-' + '_') for c in username):
        error_code = "user_logout_invalid_username"

        content = {"success": False, "error_code": error_code}
        return jsonify(content), status.HTTP_400_BAD_REQUEST

    # Connect to the MySQL database
    cursor = None

    try:
        cursor = db.cursor()
    except:
        error_code = "connection_to_database_failed"

        content = {"success": False, "error_code": error_code}
        print(traceback.format_exc())
        return jsonify(content), status.HTTP_500_INTERNAL_SERVER_ERROR

    # Check if the access token is valid
    cursor.execute("SELECT access_token FROM user WHERE username='" + username + "'")
    result = cursor.fetchone()

    if not (access_token == result[0]):
        error_code = "user_bad_access_token"
        cursor.close()

        content = {"success": False, "error_code": error_code}
        return jsonify(content), status.HTTP_403_FORBIDDEN

    # If this line of the logout() function is reached,
    # all the logout input parameters are valid.

    else:
        cursor.execute("UPDATE user SET access_token=NULL WHERE username='" + username + "'")
        db.commit()
        cursor.close()

        content = {"success": True}
        return jsonify(content), status.HTTP_200_OK


# User Profile
@app.route("/api/user/profile", methods=["POST"])
def profile():
    # Read in profile input parameters
    username = request.values.get('username') # String (a-z, A-Z, 0-9, -, _)
    access_token = request.values.get('access_token')

    # Connect to the MySQL database
    cursor = None

    try:
        cursor = db.cursor()
    except:
        error_code = "connection_to_database_failed"

        content = {"success": False, "error_code": error_code}
        print(traceback.format_exc())
        return jsonify(content), status.HTTP_500_INTERNAL_SERVER_ERROR

    if not all((c in ascii_letters + digits + '-' + '_') for c in username): #check if username is vlaid
        error_code = "user_profile_invalid_username"

        content = {"success": False, "error_code": error_code}
        return jsonify(content), status.HTTP_400_BAD_REQUEST

    cursor.execute("SELECT email_address, display_name, password_hash FROM user WHERE username='" + username + "'")
    result = cursor.fetchone()

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

    # Connect to the MySQL database
    cursor = None

    try:
        cursor = db.cursor()
    except:
        error_code = "connection_to_database_failed"

        content = {"success": False, "error_code": error_code}
        print(traceback.format_exc())
        return jsonify(content), status.HTTP_500_INTERNAL_SERVER_ERROR

    if not all((c in ascii_letters + digits + '-' + '_') for c in username): #check if username is vlaid
        error_code = "user_checkin_invalid_username"

        content = {"success": False, "error_code": error_code}
        return jsonify(content), status.HTTP_400_BAD_REQUEST

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

    print(closestDistance)
    print(closestCity)

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
