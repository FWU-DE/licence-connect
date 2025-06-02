from typing import Annotated
from fastapi import APIRouter, Query
from pydantic import BaseModel
from app.util.logger import logger
from .media_management import Medium
from . import media_management

router = APIRouter(tags=["Public"])


class LicenceResponse(BaseModel):
    user_id: str
    bundesland_id: str | None = None
    schul_id: str | None = None
    licenced_media: list[Medium]


@router.get("/licenced-media")
async def read_assigned_licences(
    user_id: Annotated[
        str,
        Query(description="User identifier, usually assigned by VIDIS"),
    ],
    bundesland_id: Annotated[
        str | None,
        Query(description="Federal state identifier"),
    ] = None,
    schul_id: Annotated[
        str | None,
        Query(description="School identifier"),
    ] = None,
) -> LicenceResponse:
    """
    Licences managed by LC-Halt can be retrieved using this endpoint.

    Provided with a user id and optionally a bundesland id and/or schul id, LC-Halt will return all media the user is allowed to access.
    """
    logger.info(
        f"Received request with user_id {user_id}, bundesland_id {bundesland_id}, schul_id {schul_id}"
    )
    assigned_media = await media_management.get_all_assigned_media(
        user_id=user_id, bundesland_id=bundesland_id, schul_id=schul_id
    )
    return LicenceResponse(
        user_id=user_id,
        bundesland_id=bundesland_id,
        schul_id=schul_id,
        licenced_media=assigned_media,
    )
