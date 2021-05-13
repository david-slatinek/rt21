from main import app, json, UserCollection, ObjectId, create_invalid, invalid_id
from main import json_util, request, bcrypt


def register():
    name = request.form.get('name', None)
    last_name = request.form.get('last_name', None)
    try:
        age = int(request.form.get('age', None))
    except ValueError:
        age = None
    nickname = request.form.get('nickname', None)
    email = request.form.get('email', None)
    password = request.form.get('password', None)

    if not name:
        return create_invalid('name not given/invalid')
    if not last_name:
        return create_invalid('last_name not given/invalid')
    if not age:
        return create_invalid('age not given/invalid')
    if not nickname:
        return create_invalid('nickname not given/invalid')
    if not email:
        return create_invalid('email not given/invalid')
    if not password:
        return create_invalid('password not given/invalid')

    if UserCollection.find_one({"email": email}):
        create_invalid('email already exists')

    obj = {
        'name': name,
        'last_name': last_name,
        'age': age,
        'nickname': nickname,
        'email': email,
        'password': bcrypt.generate_password_hash(password.encode('utf-8')).decode('utf-8')
    }

    obj_id = UserCollection.insert_one(obj).inserted_id
    if obj_id:
        return json.loads(json_util.dumps(obj_id)), 201
    else:
        return create_invalid('password not given/invalid')


def login():
    email = request.form.get('email', None)
    password = request.form.get('password', None)

    if not email:
        return create_invalid('email not given/invalid')
    if not password:
        return create_invalid('password not given/invalid')

    # obj = UserCollection.find_one({"$and": [{"email": email}, {"password": password}]})
    obj = UserCollection.find_one({"email": email})
    if obj:
        user = json.loads(json_util.dumps(obj))

        if bcrypt.check_password_hash(user["password"], password.encode('utf-8')):
            user.pop('password')
            return user
        else:
            return create_invalid('invalid email/password', 404)
    else:
        return create_invalid('invalid email/password', 404)


def get_user(user_id):
    if len(user_id) != 24:
        return invalid_id()

    obj = UserCollection.find_one({"_id": ObjectId(user_id)})
    if obj:
        user = json.loads(json_util.dumps(obj))
        user.pop("password")
        return user
    else:
        return create_invalid('user not found', 404)


def update_user(user_id):
    if len(user_id) != 24:
        return invalid_id()

    key = request.form.get('key', None)
    value = request.form.get('value', None)

    if not key or key == "_id":
        return create_invalid('key')
    if not value:
        return create_invalid('value')

    if key != "name" and key != "last_name" and key != "age" and key != "nickname" \
            and key != "email" and key != "password":
        return create_invalid("key")

    if key == "age":
        try:
            value = int(value)
        except ValueError:
            return create_invalid("age")

    if key == "password":
        value = bcrypt.generate_password_hash(value.encode('utf-8')).decode('utf-8')

    UserCollection.update_one({'_id': ObjectId(user_id)}, {'$set': {key: value}}, upsert=False)

    return get_user(user_id)
