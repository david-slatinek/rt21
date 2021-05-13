import main


def create_location():
    drive_id = main.request.form.get('drive_id', None)

    if not drive_id:
        return main.create_response('error', 'drive_id not given', 400)

    if len(drive_id) != 24:
        return main.create_response('error', 'invalid id length', 400)

    latitude = main.request.form.get('latitude', None)
    longitude = main.request.form.get('longitude', None)
    try:
        road_quality = float(main.request.form.get('road_quality', -1))
    except ValueError:
        road_quality = -1

    if not latitude:
        return main.create_response('error', 'latitude not given', 400)
    if not longitude:
        return main.create_response('error', 'longitude not given', 400)
    if 10.0 < road_quality < 1.0:
        return main.create_response('error', 'road_quality not given/valid', 400)

    value = main.get_drive(drive_id)
    if not isinstance(value, dict):
        return value

    obj = {
        'drive_id': drive_id,
        'latitude': latitude,
        'longitude': longitude,
        'road_quality': road_quality
    }

    obj_id = main.LocationCollection.insert_one(obj).inserted_id
    if obj_id is not None:
        return main.json.loads(main.json_util.dumps(obj_id)), 201
    else:
        return main.create_response('error', 'error when creating location', 500)


def get_location(location_id):
    if len(location_id) != 24:
        return main.create_response('error', 'invalid id length', 400)

    obj = main.LocationCollection.find_one({"_id": main.ObjectId(location_id)})
    if obj:
        return main.json.loads(main.json_util.dumps(obj))
    else:
        return main.create_response('error', 'location not found', 404)


def update_location(location_id):
    if len(location_id) != 24:
        return main.create_response('error', 'invalid id length', 400)

    if not main.LocationCollection.find_one({"_id": main.ObjectId(location_id)}):
        return main.create_response('error', 'location not found', 404)

    key = main.request.form.get('key', None)
    value = main.request.form.get('value', None)

    if not key or key == "_id" or key == "drive_id":
        return main.create_response('error', 'key not given/valid', 400)
    if not value:
        return main.create_response('error', 'value not given', 400)

    if key != "latitude" and key != "longitude" and key != "road_quality":
        return main.create_response('error', "key not valid", 400)

    try:
        value = int(value)
    except ValueError:
        return main.create_response('error', "value not valid", 400)

    if key == "road_quality":
        if 10.0 < value < 1.0:
            return main.create_response('error', 'road_quality not valid', 400)

    main.LocationCollection.update_one({'_id': main.ObjectId(location_id)}, {'$set': {key: value}})

    return get_location(location_id)


def delete_location(location_id):
    if len(location_id) != 24:
        return main.create_response('error', 'invalid id length', 400)

    if not main.LocationCollection.find_one({"_id": main.ObjectId(location_id)}):
        return main.create_response('error', 'location not found', 404)

    main.LocationCollection.delete_one({'_id': main.ObjectId(location_id)})

    return main.create_response('success', 'location deleted', 200)
