# Import all necessary Flask libraries and extensions
from flask import Flask
from flask.ext.mysql import MySQL # Connects Flask server to MySQL database
from flask.ext.api import status # Handles error codes returned by Flask server

from string import ascii_letters
from string import digits
import re

app = Flask(__name__)

mysql = MySQL()

app.config['MYSQL_DATABASE_USER'] = 'flaskuser'
app.config['MYSQL_DATABASE_PASSWORD'] = 'tCU8PvBYEPP4qkun'
app.config['MYSQL_DATABASE_DB'] = 'srpatil2_cities_test'
app.config['MYSQL_DATABASE_HOST'] = 'localhost'
mysql.init_app(app)


# User Registration
@app.route("/api/user/register", Method="POST")
def register():
    # Read in registration input parameters
    username = request.form.get('username') # String (a-z, A-Z, 0-9, -, _)
    password = request.form.get('password') # String (6 <= characters <= 256)
    email_address = request.form.get('email_address') # String (valid email)
    display_name = request.form.get('display_name') # String (1 <= characters <= 265)

    # Connect to the MySQL database
    cursor = mysql.connect().cursor()

    # Check if all the registration input parameters are valid

    # Check if username is in a valid form
    if not all((c in ascii_letters + digits + '-' + '_') for c in username):
        error_code = "user_register_invalid_username"

        content = {"success": false, "error_code": error_code}
        return content, status.HTTP_400_BAD_REQUEST

    # Check if the username is new
    cursor.execute("SELECT username FROM Users WHERE username='" + username + "'")
    result = cursor.fetchone()

    if result:
        error_code = "user_register_username_in_use"

        content = {"success": false, "error_code": error_code}
        return content, status.HTTP_400_BAD_REQUEST

    # Check if password is valid
    if not (len(password) >= 6 and len(password) <= 256):
        error_code = "user_register_invalid_password"

        content = {"success": false, "error_code": error_code}
        return content, status.HTTP_400_BAD_REQUEST

    # Check if email_address is valid
    emailRegex = re.compile(r"([a-zA-Z0-9]+)@([a-zA-Z0-9]+)\.([a-zA-Z0-9][a-zA-Z0-9][a-zA-Z0-9])")

    if not emailRegex.match(email_address):
        error_code = "user_register_invalid_email"

        content = {"success": false, "error_code": error_code}
        return content, status.HTTP_400_BAD_REQUEST

    # Check if display_name is valid
    if not (len(display_name) >= 1 and len(display_name) <= 265):
        error_code = "user_register_invalid_display_name"

        content = {"success": false, "error_code": error_code}
        return content, status.HTTP_400_BAD_REQUEST

    # If this line of the register() function is reached,
    # all the registration input parameters are valid.

    # Insert registration information into Users table
    cursor.execute("INSERT INTO Users values('" +
                    username + "', '" + password + "', '" +
                    email_address + "', '" + display_name + "'")

    content = {"success": true}
    return content, status.HTTP_200_OK


# User Login
@app.route("/api/user/login", Method="POST")
def login():
    # Read in login input parameters
    username = request.form.get('username') # String (a-z, A-Z, 0-9, -, _)
    password = request.form.get('password') # String (6 <= characters <= 256)

    # Connect to the MySQL database
    cursor = mysql.connect().cursor()

    # Check if all the login input parameters are valid

    # Check if username is valid
    if not all((c in ascii_letters + digits + '-' + '_') for c in username):
        error_code = "user_login_invalid_username"

        content = {"success": false, "error_code": error_code}
        return content, status.HTTP_400_BAD_REQUEST

    # Check if password is valid
    if not (len(password) >= 6 and len(password) <= 256):
        error_code = "user_login_invalid_password"

        content = {"success": false, "error_code": error_code}
        return content, status.HTTP_400_BAD_REQUEST

    # If this line of the login() function is reached,
    # all the login input parameters are valid.

    # Search Users table for login information
    cursor.execute("SELECT email_address, display_name, access_token FROM Users WHERE username='" + username + "' AND password='" + password + "'")
    result = cursor.fetchone()

    if not result:
        error_code = "user_login_bad_credentials"

        content = {"success": false, "error_code": error_code}
        return content, status.HTTP_400_BAD_REQUEST
    else:
        email = result[0]
        display_name = result[1]
        access_token = result[2]
        content = {"success": true, "email_address": email, "display_name": display_name, "access_token": access_token}
        return content, status.HTTP_200_OK


# User Profile
@app.route("/api/user/profile", Method="POST")
def profile():
    return 0


# City Checkin
@app.route("/api/user/checkin", Method="POST")
def checkin():
    return 0


'''
@app.route("/")
def hello():
    cursor = mysql.connect().cursor()
    cursor.execute('''SELECT * FROM world_cities WHERE City="paris"''')
    results = cursor.fetchall()
    return "Hello World! This is Flask." +
           "<br/>Here are all the cities in our database named Paris:<br/>" +
           "".join([s + "<br/>" for s in str(results).split("), (")])
'''
