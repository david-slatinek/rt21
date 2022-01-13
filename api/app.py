import os

from flask import after_this_request, jsonify, request, send_file

from __init__ import app, environ
from common import create_response
from drive import (create_drive, delete_drive, get_drive, get_drives,
                   update_drive)
from location import (create_location, delete_location, get_location,
                      get_locations, get_road_quality, update_location)
from sign import (create_sign, delete_sign, get_sign, get_sign_type, get_signs,
                  recognize_sign, update_sign)
from user import get_user, login, register, update_user


@app.errorhandler(404)
def page_not_found(e):
    return create_response('error', 'resource not found', 404)


@app.route('/api/user/register', methods=['POST'])
def app_register():
    if request.headers.get('X-API-Key') != app.config['API_KEY']:
        return create_response('error', 'api key not given or invalid', 401)
    return register()


@app.route('/api/user/login', methods=['POST'])
def app_login():
    if request.headers.get('X-API-Key') != app.config['API_KEY']:
        return create_response('error', 'api key not given or invalid', 401)
    return login()


@app.route('/api/user/<user_id>', methods=['GET'])
def app_get_user(user_id):
    if request.headers.get('X-API-Key') != app.config['API_KEY']:
        return create_response('error', 'api key not given or invalid', 401)
    return get_user(user_id)


@app.route('/api/user/<user_id>', methods=['PUT'])
def app_update_user(user_id):
    if request.headers.get('X-API-Key') != app.config['API_KEY']:
        return create_response('error', 'api key not given or invalid', 401)
    return update_user(user_id)


@app.route('/api/drive/create', methods=['POST'])
def app_create_drive():
    if request.headers.get('X-API-Key') != app.config['API_KEY']:
        return create_response('error', 'api key not given or invalid', 401)
    return create_drive()


@app.route('/api/drive/<drive_id>', methods=['GET'])
def app_get_drive(drive_id):
    if request.headers.get('X-API-Key') != app.config['API_KEY']:
        return create_response('error', 'api key not given or invalid', 401)
    return get_drive(drive_id)


@app.route('/api/drive/<drive_id>', methods=['PUT'])
def app_update_drive(drive_id):
    if request.headers.get('X-API-Key') != app.config['API_KEY']:
        return create_response('error', 'api key not given or invalid', 401)
    return update_drive(drive_id)


@app.route('/api/drive/<drive_id>', methods=['DELETE'])
def app_delete_drive(drive_id):
    if request.headers.get('X-API-Key') != app.config['API_KEY']:
        return create_response('error', 'api key not given or invalid', 401)
    return delete_drive(drive_id)


@app.route('/api/drive/getDrives/<user_id>', methods=['GET'])
def app_get_drives(user_id):
    if request.headers.get('X-API-Key') != app.config['API_KEY']:
        return create_response('error', 'api key not given or invalid', 401)
    return get_drives(user_id)


@app.route('/api/location/create', methods=['POST'])
def app_create_location():
    if request.headers.get('X-API-Key') != app.config['API_KEY']:
        return create_response('error', 'api key not given or invalid', 401)
    return create_location()


@app.route('/api/location/<location_id>', methods=['GET'])
def app_get_location(location_id):
    if request.headers.get('X-API-Key') != app.config['API_KEY']:
        return create_response('error', 'api key not given or invalid', 401)
    return get_location(location_id)


@app.route('/api/location/<location_id>', methods=['PUT'])
def app_update_location(location_id):
    if request.headers.get('X-API-Key') != app.config['API_KEY']:
        return create_response('error', 'api key not given or invalid', 401)
    return update_location(location_id)


@app.route('/api/location/<location_id>', methods=['DELETE'])
def app_delete_location(location_id):
    if request.headers.get('X-API-Key') != app.config['API_KEY']:
        return create_response('error', 'api key not given or invalid', 401)
    return delete_location(location_id)


@app.route('/api/location/getLocations/<drive_id>', methods=['GET'])
def app_get_locations(drive_id):
    if request.headers.get('X-API-Key') != app.config['API_KEY']:
        return create_response('error', 'api key not given or invalid', 401)
    return get_locations(drive_id)


@app.route('/api/location/getRoadQuality/<drive_id>', methods=['GET'])
def app_get_road_quality(drive_id):
    if request.headers.get('X-API-Key') != app.config['API_KEY']:
        return create_response('error', 'api key not given or invalid', 401)

    data = get_road_quality(drive_id)

    if data["success"]:
        try:
            @after_this_request
            def remove_file(response):
                os.remove('numbers.txt')
                os.remove('compressed.bin')
                return response

            return send_file('compressed.bin', mimetype='application/octet-stream')
        except FileNotFoundError as error:
            return jsonify({'error': str(error)}), 500


@app.route('/api/sign/recognize', methods=['POST'])
def app_recognize_sign():
    if request.headers.get('X-API-Key') != app.config['API_KEY']:
        return create_response('error', 'api key not given or invalid', 401)

    @after_this_request
    def remove_file(response):
        if os.path.exists('image.jpg'):
            os.remove('image.jpg')
        return response

    return recognize_sign()


@app.route('/api/sign/create', methods=['POST'])
def app_create_sign():
    if request.headers.get('X-API-Key') != app.config['API_KEY']:
        return create_response('error', 'api key not given or invalid', 401)
    return create_sign()


@app.route('/api/sign/<sign_id>', methods=['GET'])
def app_get_sign(sign_id):
    if request.headers.get('X-API-Key') != app.config['API_KEY']:
        return create_response('error', 'api key not given or invalid', 401)
    return get_sign(sign_id)


@app.route('/api/sign/<sign_id>', methods=['PUT'])
def app_update_sign(sign_id):
    if request.headers.get('X-API-Key') != app.config['API_KEY']:
        return create_response('error', 'api key not given or invalid', 401)
    return update_sign(sign_id)


@app.route('/api/sign/<sign_id>', methods=['DELETE'])
def app_delete_sign(sign_id):
    if request.headers.get('X-API-Key') != app.config['API_KEY']:
        return create_response('error', 'api key not given or invalid', 401)
    return delete_sign(sign_id)


@app.route('/api/sign/getSigns/<drive_id>', methods=['GET'])
def app_get_sings(drive_id):
    if request.headers.get('X-API-Key') != app.config['API_KEY']:
        return create_response('error', 'api key not given or invalid', 401)
    return get_signs(drive_id)


@app.route('/api/sign/<latitude>/<longitude>', methods=['GET'])
def app_get_sign_type(latitude, longitude):
    if request.headers.get('X-API-Key') != app.config['API_KEY']:
        return create_response('error', 'api key not given or invalid', 401)
    return get_sign_type(latitude, longitude)


if __name__ == '__main__':
    app.run(debug=True, host='0.0.0.0', port=int(environ.get('PORT', 5000)))
