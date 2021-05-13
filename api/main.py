import flask
from flask_pymongo import PyMongo
import json
import user
import config
import drive
import location
import sign
import os
from common import create_invalid, invalid_id, invalid_api_key
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
    return app.response_class(
        response=json.dumps({"error": "resource not found"}),
        status=404,
        mimetype='application/json'
    )


@app.route('/api/user/register', methods=['POST'])
def register():
    if request.headers.get('X-API-Key') != api_key:
        return invalid_api_key()
    return user.register()


@app.route('/api/user/login', methods=['POST'])
def login():
    if request.headers.get('X-API-Key') != api_key:
        return invalid_api_key()
    return user.login()


@app.route('/api/user/<user_id>', methods=['GET'])
def get_user(user_id):
    if request.headers.get('X-API-Key') != api_key:
        return invalid_api_key()
    return user.get_user(user_id)


@app.route('/api/user/<user_id>', methods=['PUT'])
def update_user(user_id):
    if request.headers.get('X-API-Key') != api_key:
        return invalid_api_key()
    return user.update_user(user_id)


@app.route('/api/drive/create', methods=['POST'])
def create_drive():
    return drive.create_drive()


@app.route('/api/drive/drive/<drive_id>', methods=['GET'])
def get_drive(drive_id):
    return drive.get_drive(drive_id)


@app.route('/api/location/location/<location_id>', methods=['GET'])
def get_location(location_id):
    return location.get_location(location_id)


@app.route('/api/location/create', methods=['POST'])
def create_location():
    return location.create_location()


@app.route('/api/sign/sign/<sign_id>', methods=['GET'])
def get_sign(sign_id):
    return sign.get_sign(sign_id)


@app.route('/api/sign/create', methods=['POST'])
def create_sign():
    return sign.create_sign()


if __name__ == "__main__":
    app.run(debug=True, host='0.0.0.0', port=int(os.environ.get('PORT', 5000)))
