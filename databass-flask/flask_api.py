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

    # Check if all the registration input parameters are valid

    # Check if username is valid
    if not all((c in ascii_letters + digits + '-' + '_') for c in username):
        error_code = "user_register_invalid_username"

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

    # Connect to MySQL database and insert
    # registration information into Users table
    cursor = mysql.connect().cursor()
    cursor.execute("INSERT INTO Users values('" +
                    username + "', '" + password + "', '" +
                    email_address + "', '" + display_name + "'")

    content = {"success": true}
    return content, status.HTTP_200_OK


# User Login
@app.route("/api/user/login")
def login():
    return 0


# User Profile
@app.route("/api/user/profile")
def profile():
    return 0


# City Checkin
@app.route("/api/user/checkin")
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
