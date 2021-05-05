from main import app
from main import UserCollection
from main import ObjectId
from main import json
from main import json_util
from main import request


def create_invalid(filed_name):
    response = app.response_class(
        response=json.dumps({"error": filed_name + " not given or invalid"}),
        status=400,
        mimetype='application/json'
    )
    return response


def create_user():
    name = request.form.get('name', 'default_name')
    last_name = request.form.get('last_name', 'default_last_name')
    age = int(request.form.get('age', -1))
    nickname = request.form.get('nickname', 'default_nickname')
    email = request.form.get('email', 'default_email')
    password = request.form.get('password', 'default_password')

    if name == 'default_name':
        return create_invalid('name')
    if last_name == 'default_last_name':
        return create_invalid('last_name')
    if age < 18:
        return create_invalid('age')
    if nickname == 'default_nickname':
        return create_invalid('default_nickname')
    if email == 'default_email':
        return create_invalid('default_email')
    if password == 'default_password':
        return create_invalid('default_password')

    obj = {
        'name': name,
        'last_name': last_name,
        'age': age,
        'nickname': nickname,
        'email': email,
        'password': password
    }

    obj_id = UserCollection.insert_one(obj).inserted_id
    if obj_id is not None:
        return json.loads(json_util.dumps(obj_id)), 201
    else:
        response = app.response_class(
            response=json.dumps({"error": "error when creating user"}),
            status=500,
            mimetype='application/json'
        )
        return response


def get_user(object_id):
    if len(object_id) != 24:
        response = app.response_class(
            response=json.dumps({"error": "invalid id length"}),
            status=400,
            mimetype='application/json'
        )
        return response

    obj = UserCollection.find_one({"_id": ObjectId(object_id)})
    if obj:
        return json.loads(json_util.dumps(obj))
    else:
        response = app.response_class(
            response=json.dumps({"error": "not found"}),
            status=404,
            mimetype='application/json'
        )
        return response
