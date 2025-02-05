response=$(curl -s -X POST https://login.test.sso.bildungslogin.de/realms/BiLo-Broker/protocol/openid-connect/token \
  -d "grant_type=client_credentials" \
  -d "client_id=${CLIENT_ID}" \
  -d "client_secret=${CLIENT_SECRET}")

echo $response

# Extract the access token from the response
TOKEN=$(echo $response | jq -r '.access_token')

if [ -z "$TOKEN" ]; then
  echo "Failed to retrieve access token"
  exit 1
fi

token=${TOKEN}
header=$(echo $token | cut -d "." -f1)
payload=$(echo $token | cut -d "." -f2)

echo "Header:"
echo $header | base64 --decode | jq .
echo "Payload:"
echo $payload | base64 --decode | jq .

echo "token encoded:" $TOKEN

curl -X POST "https://lizenzverwaltung.bildungslogin-test.de/starbackend-v1/lookup/v1/USR__OOC_BY_LicenseConnectFWU/user/student.2?inc=license" \
  -H "Content-Type: application/json" \
  -H "license-user-id=student.2" \
  -H "Authorization: Bearer ${TOKEN}"
