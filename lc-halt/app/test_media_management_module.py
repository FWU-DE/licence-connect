import os
from fastapi.testclient import TestClient

from app.main import app

client = TestClient(app)

admin_api_key = os.getenv("LC_HALT_ADMIN_API_KEY")
client_api_key = os.getenv("LC_HALT_CLIENT_API_KEY")


def test_licenced_media_no_query_params_bad_request():
    response = client.get("/licenced-media", headers={"x-api-key": client_api_key})
    assert response.status_code == 422


def test_assign_media_no_params_bad_request():
    response = client.post(
        "/admin/media-licence-assignment",
        headers={"x-api-key": admin_api_key},
        json={
            "licenced_media": [],
        },
    )
    assert response.status_code == 400


def test_assign_media_all_params_bad_request():
    response = client.post(
        "/admin/media-licence-assignment",
        headers={"x-api-key": admin_api_key},
        json={
            "user_id": "123",
            "bundesland_id": "123",
            "schul_id": "123",
            "licenced_media": [],
        },
    )
    assert response.status_code == 400


def test_assign_media_only_schul_id_bad_request():
    response = client.post(
        "/admin/media-licence-assignment",
        headers={"x-api-key": admin_api_key},
        json={
            "schul_id": "123",
            "licenced_media": [],
        },
    )
    assert response.status_code == 400


def test_assign_and_delete_media_smoke_test():
    assign_user_medium_response = client.post(
        "/admin/media-licence-assignment",
        headers={"x-api-key": admin_api_key},
        json={
            "user_id": "automated-test-user",
            "licenced_media": [{"id": "user-medium"}],
        },
    )
    assert assign_user_medium_response.status_code == 200

    assign_bundesland_medium_response = client.post(
        "/admin/media-licence-assignment",
        headers={"x-api-key": admin_api_key},
        json={
            "bundesland_id": "automated-test-bundesland",
            "licenced_media": [{"id": "bundesland-medium"}],
        },
    )
    assert assign_bundesland_medium_response.status_code == 200

    assign_schul_medium_response = client.post(
        "/admin/media-licence-assignment",
        headers={"x-api-key": admin_api_key},
        json={
            "bundesland_id": "automated-test-bundesland",
            "schul_id": "automated-test-schule",
            "licenced_media": [{"id": "schul-medium"}],
        },
    )
    assert assign_schul_medium_response.status_code == 200

    licenced_media_user_only_response = client.get(
        "/licenced-media?userId=automated-test-user",
        headers={"x-api-key": client_api_key},
    )
    user_media = licenced_media_user_only_response.json()["licencedMedia"]
    assert licenced_media_user_only_response.status_code == 200
    assert {"id": "user-medium"} in user_media
    assert len(user_media) == 1

    licenced_media_user_and_bundesland_response = client.get(
        "/licenced-media?userId=automated-test-user&bundesland=automated-test-bundesland",
        headers={"x-api-key": client_api_key},
    )
    bundesland_media = licenced_media_user_and_bundesland_response.json()[
        "licencedMedia"
    ]
    assert licenced_media_user_and_bundesland_response.status_code == 200
    assert {"id": "user-medium"} in bundesland_media
    assert {"id": "bundesland-medium"} in bundesland_media
    assert len(bundesland_media) == 2

    licenced_media_user_and_bundesland_and_schule_response = client.get(
        "/licenced-media?userId=automated-test-user&bundesland=automated-test-bundesland&schulnummer=automated-test-schule",
        headers={"x-api-key": client_api_key},
    )
    schul_media = licenced_media_user_and_bundesland_and_schule_response.json()[
        "licencedMedia"
    ]
    assert licenced_media_user_and_bundesland_and_schule_response.status_code == 200
    assert {"id": "user-medium"} in schul_media
    assert {"id": "bundesland-medium"} in schul_media
    assert {"id": "schul-medium"} in schul_media
    assert len(schul_media) == 3

    delete_user_assignment_response = client.delete(
        f"/admin/media-licence-assignment/{assign_user_medium_response.json()['id']}",
        headers={"x-api-key": admin_api_key},
    )
    assert delete_user_assignment_response.status_code == 204

    delete_bundesland_assignment_response = client.delete(
        f"/admin/media-licence-assignment/{assign_bundesland_medium_response.json()['id']}",
        headers={"x-api-key": admin_api_key},
    )
    assert delete_bundesland_assignment_response.status_code == 204

    delete_schul_assignment_response = client.delete(
        f"/admin/media-licence-assignment/{assign_schul_medium_response.json()['id']}",
        headers={"x-api-key": admin_api_key},
    )
    assert delete_schul_assignment_response.status_code == 204
