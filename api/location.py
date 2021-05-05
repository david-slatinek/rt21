import main


def get_location(location_id):
    if len(location_id) != 24:
        return main.invalid_id()

    obj = main.LocationCollection.find_one({"_id": main.ObjectId(location_id)})
    if obj:
        return main.json.loads(main.json_util.dumps(obj))
    else:
        return main.app.response_class(
            response=main.json.dumps({"error": "location not found"}),
            status=404,
            mimetype='application/json'
        )


def create_location():
    drive_id = main.request.form.get('drive_id', 'default_drive_id')
    latitude = main.request.form.get('latitude', 'default_latitude')
    longitude = main.request.form.get('longitude', 'default_longitude')
    try:
        road_quality = float(main.request.form.get('road_quality', -1))
    except ValueError:
        road_quality = -1

    if drive_id == "default_drive_id":
        return main.create_invalid('drive_id')
    if latitude == "default_latitude":
        return main.create_invalid('latitude')
    if longitude == "default_longitude":
        return main.create_invalid('longitude')
    if road_quality < 1.0 or road_quality > 10.0:
        return main.create_invalid('longitude')

    if len(drive_id) != 24:
        return main.invalid_id()

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
        return main.app.response_class(
            response=main.json.dumps({"error": "error when creating location"}),
            status=500,
            mimetype='application/json'
        )
