from flask import Flask, jsonify, request, Response

app = Flask(__name__)

mock_access_token_value = "mock_access_token"


@app.route('/healthcheck', methods=['GET'])
def healthcheck():
    return jsonify({"status": "ok"}), 200

@app.route('/ucsschool/apis/auth/token', methods=['GET', "POST"])
def token():
    username = request.form.get('username')
    password = request.form.get('password')
    if username == config["bilo.v1.auth.admin.username"] and password == config["bilo.v1.auth.admin.password"]:
        return jsonify({
            "access_token": mock_access_token_value,
            "token_type": "Bearer",
            "expires_in": 3600
        })
    else:
        return Response("Unauthorized", status=401)

@app.route('/ucsschool/apis/bildungslogin/v1/user/<user_id>', methods=['GET'])
def licences(user_id):
    if request.headers.get('Authorization') == 'Bearer ' + mock_access_token_value and user_id == 'student.2':
        return jsonify({
            "id": user_id,
            "first_name": "student",
            "last_name": "2",
            "licenses": ["WES-VIDT-2369-P85R-KOUD"],
            "context": {
                "92490b9dc18341906b557bbd4071e1c97db9f9b65d348fafd30988b85a2f6924": {
                    "school_name": "testfwu",
                    "classes": [
                        {
                            "name": "1",
                            "id": "cda6b1ddfa321de5a456c69fd5cee2cde7eeeae9b9d9ed24eb84fd88a35cfecb",
                            "licenses": [
                                "WES-VIDT-0346-P85R-KOUD",
                                "WES-VIDT-9775-P85R-VWYX"
                            ]
                        }
                    ],
                    "roles": ["student"],
                    "licenses": ["WES-VIDT-7368-P85R-KOUD"]
                }
            }
        })
    else:
        return Response("Unauthorized", status=401)

def parse_local_section_profile_config_file(file_path):
    config = {}
    with open(file_path, 'r') as file:
        has_reached_local_section = False
        for line in file:
            if line.strip() == 'spring.config.activate.on-profile=local':
                has_reached_local_section = True
            if line.strip() and not line.startswith('#') and has_reached_local_section:
                key, value = line.split('=', 1)
                config[key.strip()] = value.strip()
    return config

if __name__ == '__main__':
    print("starting v1 mock")
    global config
    config = parse_local_section_profile_config_file('./application.properties')
    app.run(debug=True, port=1237, host="0.0.0.0")