import os
from fastapi.testclient import TestClient

from app.main import app

client = TestClient(app)


def test_licenced_media_no_query_params_bad_request():
    client_api_key = os.getenv("LC_HALT_CLIENT_API_KEY")
    response = client.get("/licenced-media", headers={"x-api-key": client_api_key})
    assert response.status_code == 422
