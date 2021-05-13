import main


def get_location(location_id):
    if len(location_id) != 24:
        return main.invalid_id()

    obj = main.LocationCollection.find_one({"_id": main.ObjectId(location_id)})
    if obj:
        return main.json.loads(main.json_util.dumps(obj))
    else:
        return main.create_invalid('location not found', 404)


def create_location():
    drive_id = main.request.form.get('drive_id', None)

    if not drive_id:
        return main.create_invalid('drive_id not given')

    if len(drive_id) != 24:
        return main.invalid_id()

    latitude = main.request.form.get('latitude', None)
    longitude = main.request.form.get('longitude', None)
    try:
        road_quality = float(main.request.form.get('road_quality', -1))
    except ValueError:
        road_quality = -1

    if not latitude:
        return main.create_invalid('latitude not given')
    if not longitude:
        return main.create_invalid('longitude not given')
    if 10.0 < road_quality < 1.0:
        return main.create_invalid('road_quality not given/invalid')

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
        return main.create_invalid('error when creating location', 500)
