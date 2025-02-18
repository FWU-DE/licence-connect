from flask import Flask, jsonify, request, Response
import sys
from contextlib import redirect_stdout, redirect_stderr

app = Flask(__name__)

@app.route('/healthcheck', methods=['GET'])
def healthcheck():
    return jsonify({"status": "ok"}), 200

@app.route('/realms/BiLo-Broker/protocol/openid-connect/token', methods=['POST'])
def token():
    print("inside bilo auth token mock")
    grant_type = request.form.get('grant_type')
    client_id = request.form.get('client_id')
    client_secret = request.form.get('client_secret')
    
    if grant_type == 'client_credentials' and client_id == config["bilo.v2.auth.clientId"] and client_secret == config["bilo.v2.auth.clientSecret"]:
        return jsonify({
            "access_token": "mock_access_token",
            "token_type": "Bearer",
            "expires_in": 3600
        })
    else:
        return Response("Unauthorized", status=401)

@app.route('/starbackend-v1/lookup/v1/USR__OOC_BY_LicenseConnectFWU/user/<user_id>', methods=['POST', 'GET'])
def licences(user_id):
    if request.headers.get('Authorization') == 'Bearer mock_access_token' and user_id == 'student.2':
        return jsonify({
            "user": {
                "id": user_id,
                "first_name": "student",
                "last_name": "2",
                "user_alias": None,
                "roles": ["student"],
                "media": []
            },
            "organizations": [
                {
                    "id": "testfwu",
                    "org_type": "school",
                    "identifier": None,
                    "authority": None,
                    "name": "testfwu",
                    "roles": ["student"],
                    "media": [],
                    "groups": [
                        {
                            "id": "1",
                            "name": "1",
                            "group_type": "class",
                            "media": []
                        }
                    ]
                }
            ]
        })
    else:
        return Response("Unauthorized", status=401)

def parse_file(file_path):
    with open(file_path, 'r') as file:
        return {
            line.split('=', 1)[0].strip(): line.split('=', 1)[1].strip()
            for line in file if line.strip() and not line.startswith('#')
        }

if __name__ == '__main__':
    global config
    config = parse_file('./application.properties')
    app.run(debug=True, port=1236, host="0.0.0.0")
