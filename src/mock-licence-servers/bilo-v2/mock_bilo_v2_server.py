from flask import Flask, jsonify, request, Response

app = Flask(__name__)

@app.route('/realms/BiLo-Broker/protocol/openid-connect/token', methods=['POST'])
def token():
    print("inside bilo auth token mock")
    grant_type = request.form.get('grant_type')
    client_id = request.form.get('client_id')
    client_secret = request.form.get('client_secret')
    
    if grant_type == 'client_credentials' and client_id == 'dummy_test_client_id' and client_secret == 'dummy_test_client_secret':
        return jsonify({
            "access_token": "mock_access_token",
            "token_type": "Bearer",
            "expires_in": 3600
        })
    else:
        return Response("Unauthorized", status=401)

@app.route('/starbackend-v1/lookup/v1/USR__OOC_BY_LicenseConnectFWU/user/<user_id>', methods=['POST', 'GET'])
def licences(user_id):
    if request.headers.get('Authorization') == 'Bearer mock_access_token':
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

if __name__ == '__main__':
    app.run(debug=True, port=1236, host="0.0.0.0")