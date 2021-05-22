import flask
from flask_pymongo import PyMongo
import json
import user
import config
import drive
import location
import sign
import os
from common import create_response
from bson.objectid import ObjectId
from bson import json_util
from flask import request
from flask_bcrypt import Bcrypt

key = config.KEY
api_key = config.API_KEY
app = flask.Flask(__name__)
bcrypt = Bcrypt()

app.config["MONGO_URI"] = "mongodb+srv://david:" + key + "@apicluster.knc1y.mongodb.net/rt21Db?retryWrites=true"
mongo = PyMongo(app)
db = mongo.db
UserCollection = db.user
DriveCollection = db.drive
LocationCollection = db.location
SignCollection = db.sign


@app.errorhandler(404)
def page_not_found(e):
    return create_response("error", "resource not found", 404)


@app.route('/api/user/register', methods=['POST'])
def register():
    if request.headers.get('X-API-Key') != api_key:
        return create_response("error", "api key not given or invalid", 401)
    return user.register()


@app.route('/api/user/login', methods=['POST'])
def login():
    if request.headers.get('X-API-Key') != api_key:
        return create_response("error", "api key not given or invalid", 401)
    return user.login()


@app.route('/api/user/<user_id>', methods=['GET'])
def get_user(user_id):
    if request.headers.get('X-API-Key') != api_key:
        return create_response("error", "api key not given or invalid", 401)
    return user.get_user(user_id)


@app.route('/api/user/<user_id>', methods=['PUT'])
def update_user(user_id):
    if request.headers.get('X-API-Key') != api_key:
        return create_response("error", "api key not given or invalid", 401)
    return user.update_user(user_id)


@app.route('/api/drive/create', methods=['POST'])
def create_drive():
    if request.headers.get('X-API-Key') != api_key:
        return create_response("error", "api key not given or invalid", 401)
    return drive.create_drive()


@app.route('/api/drive/<drive_id>', methods=['GET'])
def get_drive(drive_id):
    if request.headers.get('X-API-Key') != api_key:
        return create_response("error", "api key not given or invalid", 401)
    return drive.get_drive(drive_id)


@app.route('/api/drive/<drive_id>', methods=['PUT'])
def update_drive(drive_id):
    if request.headers.get('X-API-Key') != api_key:
        return create_response("error", "api key not given or invalid", 401)
    return drive.update_drive(drive_id)


@app.route('/api/drive/<drive_id>', methods=['DELETE'])
def delete_drive(drive_id):
    if request.headers.get('X-API-Key') != api_key:
        return create_response("error", "api key not given or invalid", 401)
    return drive.delete_drive(drive_id)


@app.route('/api/drive/getDrives/<user_id>', methods=['GET'])
def get_drives(user_id):
    if request.headers.get('X-API-Key') != api_key:
        return create_response("error", "api key not given or invalid", 401)
    return drive.get_drives(user_id)


@app.route('/api/location/create', methods=['POST'])
def create_location():
    if request.headers.get('X-API-Key') != api_key:
        return create_response("error", "api key not given or invalid", 401)
    return location.create_location()


@app.route('/api/location/<location_id>', methods=['GET'])
def get_location(location_id):
    if request.headers.get('X-API-Key') != api_key:
        return create_response("error", "api key not given or invalid", 401)
    return location.get_location(location_id)


@app.route('/api/location/<location_id>', methods=['PUT'])
def update_location(location_id):
    if request.headers.get('X-API-Key') != api_key:
        return create_response("error", "api key not given or invalid", 401)
    return location.update_location(location_id)


@app.route('/api/location/<location_id>', methods=['DELETE'])
def delete_location(location_id):
    if request.headers.get('X-API-Key') != api_key:
        return create_response("error", "api key not given or invalid", 401)
    return location.delete_location(location_id)


@app.route('/api/location/getLocations/<drive_id>', methods=['GET'])
def get_locations(drive_id):
    if request.headers.get('X-API-Key') != api_key:
        return create_response("error", "api key not given or invalid", 401)
    return location.get_locations(drive_id)


@app.route('/api/sign/create', methods=['POST'])
def create_sign():
    if request.headers.get('X-API-Key') != api_key:
        return create_response("error", "api key not given or invalid", 401)
    return sign.create_sign()


@app.route('/api/sign/<sign_id>', methods=['GET'])
def get_sign(sign_id):
    if request.headers.get('X-API-Key') != api_key:
        return create_response("error", "api key not given or invalid", 401)
    return sign.get_sign(sign_id)


@app.route('/api/sign/<sign_id>', methods=['PUT'])
def update_sign(sign_id):
    if request.headers.get('X-API-Key') != api_key:
        return create_response("error", "api key not given or invalid", 401)
    return sign.update_sign(sign_id)


@app.route('/api/sign/<sign_id>', methods=['DELETE'])
def delete_sign(sign_id):
    if request.headers.get('X-API-Key') != api_key:
        return create_response("error", "api key not given or invalid", 401)
    return sign.delete_sign(sign_id)


if __name__ == "__main__":
    app.run(debug=True, host='0.0.0.0', port=int(os.environ.get('PORT', 5000)))
