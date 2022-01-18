import json
from os import path

from bson import json_util
from bson.objectid import ObjectId
from flask import jsonify, request

from __init__ import DriveCollection, LocationCollection, SignCollection, app
from common import create_response
from detect_road_sign import recognize

DELTA = 10 ** -5


def recognize_sign():
    image = request.files['image']

    if not image:
        create_response('error', 'image not given', 400)

    if image.filename == '':
        create_response('error', 'image not given', 400)

    file_ext = path.splitext(image.filename)[1]
    if file_ext not in app.config['UPLOAD_EXTENSIONS']:
        create_response('error', 'invalid image extension', 400)

    if image:
        image.save('image' + file_ext)
        return create_response('sign_type', recognize('image' + file_ext), 200)

    return create_response('error', 'image not given', 400)


def create_sign():
    drive_id = request.form.get('drive_id', None)

    if not drive_id:
        return create_response('error', 'drive_id not given', 400)

    if len(drive_id) != 24:
        return create_response('error', 'invalid id length', 400)

    if not DriveCollection.find_one({'_id': ObjectId(drive_id)}):
        return create_response('error', 'drive not found', 404)

    sign_type = request.form.get('sign_type', None)

    try:
        latitude = float(request.form.get('latitude', None))
    except ValueError:
        latitude = None
    except TypeError:
        latitude = None

    try:
        longitude = float(request.form.get('longitude', None))
    except ValueError:
        longitude = None
    except TypeError:
        longitude = None

    if not sign_type:
        return create_response('error', 'type not given', 400)
    if not latitude:
        return create_response('error', 'latitude not given', 400)
    if not longitude:
        return create_response('error', 'longitude not given', 400)

    obj = {
        'drive_id': drive_id,
        'type': sign_type,
        'latitude': latitude,
        'longitude': longitude,
    }

    obj_id = SignCollection.insert_one(obj).inserted_id
    if obj_id:
        return json.loads(json_util.dumps(obj_id)), 201
    else:
        return create_response('error', 'error when creating sign', 500)


def get_sign(sign_id):
    if len(sign_id) != 24:
        return create_response('error', 'invalid id length', 400)

    obj = SignCollection.find_one({'_id': ObjectId(sign_id)})
    if obj:
        return json.loads(json_util.dumps(obj))
    else:
        return create_response('error', 'sign not found', 404)


def update_sign(sign_id):
    if len(sign_id) != 24:
        return create_response('error', 'invalid id length', 400)

    if not SignCollection.find_one({'_id': ObjectId(sign_id)}):
        return create_response('error', 'sign not found', 404)

    key = request.form.get('key', None)
    value = request.form.get('value', None)

    if not key or key == '_id' or key == 'drive_id':
        return create_response('error', 'key not given/valid', 400)
    if not value:
        return create_response('error', 'value not given', 400)

    keys = ['type', 'latitude', 'longitude']

    if key not in keys:
        return create_response('error', 'key not valid', 400)

    if key == 'latitude' or key == 'longitude':
        try:
            value = float(value)
        except ValueError as error:
            return create_response('error', str(error), 400)
        except TypeError as error:
            return create_response('error', str(error), 400)

    SignCollection.update_one({'_id': ObjectId(sign_id)}, {'$set': {key: value}})

    return get_sign(sign_id)


def delete_sign(sign_id):
    if len(sign_id) != 24:
        return create_response('error', 'invalid id length', 400)

    if not SignCollection.find_one({'_id': ObjectId(sign_id)}):
        return create_response('error', 'location not found', 404)

    SignCollection.delete_one({'_id': ObjectId(sign_id)})

    return create_response('success', 'sign deleted', 200)


def get_signs(drive_id):
    if len(drive_id) != 24:
        return create_response('error', 'invalid id length', 400)

    if not DriveCollection.find_one({'_id': ObjectId(drive_id)}):
        return create_response('error', 'drive not found', 404)

    signs = SignCollection.find({'drive_id': drive_id})

    result = {}
    i_d = 0
    for x in signs:
        result[i_d] = json_util.loads(json_util.dumps(x))
        i_d += 1

    return json.loads(json_util.dumps(result))


def get_sign_type(latitude, longitude):
    try:
        lat = float(latitude)
        lon = float(longitude)
    except ValueError as error:
        return create_response('error', str(error), 400)
    except TypeError as error:
        return create_response('error', str(error), 400)

    signs = SignCollection.find({"latitude": {"$gt": lat - DELTA, "$lt": lat + DELTA},
                                 "longitude": {"$gt": lon - DELTA, "$lt": lon + DELTA}},
                                {"_id": 0, "drive_id": 1, "type": 1, "latitude": 1, "longitude": 1})

    if signs:
        sorted_list = sorted([x for x in signs], key=lambda k: (-float(k["latitude"]), -float(k["longitude"])))

        if len(sorted_list) > 0:
            sign = sorted_list[0]
        else:
            return jsonify({"type": "-1", "quality": -1}), 404

        quality = LocationCollection.find_one({"drive_id": sign["drive_id"]},
                                              {"_id": 0, "drive_id": 0, "latitude": 0, "longitude": 0})

        if quality:
            return jsonify({"type": sign["type"], "quality": quality["road_quality"]})
        return jsonify({"type": "-1", "quality": -1}), 404

    return jsonify({"type": "-1", "quality": -1}), 404
