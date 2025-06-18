from typing import List
from fastapi import APIRouter, Body, status
from .media_management import LicencedMediaAssignmentModel, LicencedMediaAssignment
from . import media_management

router = APIRouter(prefix="/admin/media-licence-assignment", tags=["Administration"])


@router.get(
    "/",
    response_model=List[LicencedMediaAssignmentModel],
    status_code=status.HTTP_200_OK,
    response_model_by_alias=False,
)
async def list_media_licence_assignments():
    """
    This endpoint returns all existing assignments.
    """
    return await media_management.get_all_assignments()


@router.post(
    "/",
    response_model=LicencedMediaAssignmentModel,
    status_code=status.HTTP_200_OK,
    response_model_by_alias=False,
)
async def assign_licenced_media(
    licenced_media_body: LicencedMediaAssignment = Body(...),
):
    """
    This endpoint allows to assign licenced media, assigning it either:
    - to a single user
    - to all users of a bundesland
    - to all users of a schule in a bundesland
    """
    return await media_management.set_assignment(licenced_media_body)


@router.get(
    "/{id}",
    response_model=LicencedMediaAssignmentModel,
    status_code=status.HTTP_200_OK,
    response_model_by_alias=False,
)
async def show_media_licence_assignment(id: str):
    """
    This endpoint allows to retrieve a single assignment using it's id.
    """
    return await media_management.get_assignment(id)


@router.delete("/{id}")
async def delete_media_licence_assignment(id: str):
    """
    This endpoint allows to delete a single assignment using it's id.
    """
    return await media_management.delete_assignment(id)
