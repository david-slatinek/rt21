import requests
import config
import string
import random
import json

api_key = config.API_KEY


def random_string(size=6):
    return ''.join(random.choice(string.ascii_uppercase + string.digits) for _ in range(size))


if __name__ == "__main__":
    count = 5

    for i in range(count):
        url = 'https://rt21-api.herokuapp.com/api/user/register'
        data = {'name': random_string(), 'last_name': random_string(), 'age': random.randint(18, 40),
                'nickname': random_string(), 'email': random_string(), 'password': random_string()}
        r = requests.post(url, headers={'X-API-Key': api_key}, data=data)
        user_id = json.loads(r.text)["$oid"]

        for j in range(count):
            url = 'https://rt21-api.herokuapp.com/api/drive/create'
            data = {'user_id': user_id}

            r = requests.post(url, headers={'X-API-Key': api_key}, data=data)
            drive_id = json.loads(r.text)["$oid"]

            latitude, longitude = 46.5490338, 15.6506797
            for z in range(count):
                url = 'https://rt21-api.herokuapp.com/api/location/create'
                data = {'drive_id': drive_id, 'latitude': latitude + random.random(),
                        'longitude': longitude + random.random(),
                        'road_quality': random.randint(1, 10)}
                requests.post(url, headers={'X-API-Key': api_key}, data=data)

            latitude, longitude = 46.6107544, 15.785155
            for z in range(count):
                url = 'https://rt21-api.herokuapp.com/api/sign/create'
                data = {'drive_id': drive_id, 'sign_type': random_string(), latitude: latitude + random.random(),
                        'longitude': longitude + random.random()}
                requests.post(url, headers={'X-API-Key': api_key}, data=data)
