import os
from fastapi.testclient import TestClient

from .main import app

client = TestClient(app)

client_api_key = os.getenv("LC_HALT_CLIENT_API_KEY")
admin_api_key = os.getenv("LC_HALT_ADMIN_API_KEY")


def test_health():
    response = client.get("/health")
    assert response.status_code == 200
    assert response.json() == {"status": "healthy"}


def test_admin_with_client_key_forbidden():
    response = client.get(
        "/admin/media-licence-assignment", headers={"x-api-key": client_api_key}
    )
    assert response.status_code == 403


def test_admin_with_admin_key_success():
    response = client.get(
        "/admin/media-licence-assignment", headers={"x-api-key": admin_api_key}
    )
    assert response.status_code == 200


def test_licenced_media_without_key_unauthorized():
    response = client.get("/licenced-media")
    assert response.status_code == 401


def test_licenced_media_with_client_key_success():
    response = client.get(
        "/licenced-media?bundesland_id=1", headers={"x-api-key": client_api_key}
    )
    assert response.status_code == 200


def test_licenced_media_with_admin_key_success():
    response = client.get(
        "/licenced-media?bundesland_id=1", headers={"x-api-key": admin_api_key}
    )
    assert response.status_code == 200
