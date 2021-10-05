import json

from __init__ import app


def create_response(message_type, message, code):
    return app.response_class(
        response=json.dumps({message_type: message}),
        status=code,
        mimetype='application/json'
    )
