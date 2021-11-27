import json

from bson import json_util
from bson.objectid import ObjectId
from flask import request

from __init__ import DriveCollection, LocationCollection
from common import create_response


def create_location():
    drive_id = request.form.get('drive_id', None)

    if not drive_id:
        return create_response('error', 'drive_id not given', 400)

    if len(drive_id) != 24:
        return create_response('error', 'invalid id length', 400)

    if not DriveCollection.find_one({'_id': ObjectId(drive_id)}):
        return create_response('error', 'drive not found', 404)

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

    try:
        road_quality = float(request.form.get('road_quality', -1))
    except ValueError:
        road_quality = -1
    except TypeError:
        road_quality = -1

    if not latitude:
        return create_response('error', 'latitude not given', 400)
    if not longitude:
        return create_response('error', 'longitude not given', 400)
    if 10.0 < road_quality < 1.0:
        return create_response('error', 'road_quality not given/valid', 400)

    obj = {
        'drive_id': drive_id,
        'latitude': latitude,
        'longitude': longitude,
        'road_quality': road_quality
    }

    obj_id = LocationCollection.insert_one(obj).inserted_id
    if obj_id:
        return json.loads(json_util.dumps(obj_id)), 201
    else:
        return create_response('error', 'error when creating location', 500)


def get_location(location_id):
    if len(location_id) != 24:
        return create_response('error', 'invalid id length', 400)

    obj = LocationCollection.find_one({'_id': ObjectId(location_id)})
    if obj:
        return json.loads(json_util.dumps(obj))
    else:
        return create_response('error', 'location not found', 404)


def update_location(location_id):
    if len(location_id) != 24:
        return create_response('error', 'invalid id length', 400)

    if not LocationCollection.find_one({'_id': ObjectId(location_id)}):
        return create_response('error', 'location not found', 404)

    key = request.form.get('key', None)
    value = request.form.get('value', None)

    if not key or key == '_id' or key == 'drive_id':
        return create_response('error', 'key not given/valid', 400)
    if not value:
        return create_response('error', 'value not given', 400)

    keys = ['latitude', 'longitude', 'road_quality']

    if key not in keys:
        return create_response('error', 'key not valid', 400)

    try:
        value = float(value)
    except ValueError:
        return create_response('error', 'value not valid', 400)
    except TypeError:
        return create_response('error', 'value not valid', 400)

    if key == 'road_quality':
        if 10.0 < value < 1.0:
            return create_response('error', 'road_quality not valid', 400)

    LocationCollection.update_one({'_id': ObjectId(location_id)}, {'$set': {key: value}})

    return get_location(location_id)


def delete_location(location_id):
    if len(location_id) != 24:
        return create_response('error', 'invalid id length', 400)

    if not LocationCollection.find_one({'_id': ObjectId(location_id)}):
        return create_response('error', 'location not found', 404)

    LocationCollection.delete_one({'_id': ObjectId(location_id)})

    return create_response('success', 'location deleted', 200)


def get_locations(drive_id):
    if len(drive_id) != 24:
        return create_response('error', 'invalid id length', 400)

    if not DriveCollection.find_one({'_id': ObjectId(drive_id)}):
        return create_response('error', 'drive not found', 404)

    locations = LocationCollection.find({'drive_id': drive_id})

    result = {}
    i_d = 0
    for x in locations:
        result[i_d] = json_util.loads(json_util.dumps(x))
        i_d += 1

    return json.loads(json_util.dumps(result))
