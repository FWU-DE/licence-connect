import os
from fastapi.testclient import TestClient

from .main import app

client = TestClient(app)


def test_read_main():
    response = client.get("/")
    assert response.status_code == 200
    assert response.json() == {"msg": "Welcome to LC Halt"}


def test_admin_without_key_unauthorized():
    response = client.get("/admin/test")
    assert response.status_code == 401


def test_admin_with_client_key_forbidden():
    client_api_key = os.getenv("LC_HALT_CLIENT_API_KEY")
    response = client.get("/admin/test", headers={"x-api-key": client_api_key})
    assert response.status_code == 403


def test_admin_with_admin_key_success():
    admin_api_key = os.getenv("LC_HALT_ADMIN_API_KEY")
    response = client.get("/admin/test", headers={"x-api-key": admin_api_key})
    assert response.status_code == 200


def test_licenced_media_without_key_unauthorized():
    response = client.get("/licenced-media")
    assert response.status_code == 401


def test_licenced_media_with_client_key_success():
    client_api_key = os.getenv("LC_HALT_CLIENT_API_KEY")
    response = client.get(
        "/licenced-media?userId=1", headers={"x-api-key": client_api_key}
    )
    assert response.status_code == 200


def test_licenced_media_with_admin_key_success():
    admin_api_key = os.getenv("LC_HALT_ADMIN_API_KEY")
    response = client.get(
        "/licenced-media?userId=1", headers={"x-api-key": admin_api_key}
    )
    assert response.status_code == 200


def test_licenced_media_no_query_params_bad_request():
    client_api_key = os.getenv("LC_HALT_CLIENT_API_KEY")
    response = client.get("/licenced-media", headers={"x-api-key": client_api_key})
    assert response.status_code == 422
