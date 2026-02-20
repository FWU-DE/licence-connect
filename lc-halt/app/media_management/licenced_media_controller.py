from typing import Annotated
from fastapi import APIRouter, Query
from pydantic import BaseModel
from app.util.logger import logger
from .media_management import Medium
from . import media_management

router = APIRouter(tags=["Client interface"])


class LicenceResponse(BaseModel):
    bundesland_id: str
    landkreis_id: str | None = None
    schul_id: str | None = None
    licenced_media: list[Medium]


@router.get("/licenced-media")
async def read_assigned_licences(
    bundesland_id: Annotated[
        str,
        Query(description="Federal state identifier"),
    ],
    landkreis_id: Annotated[
        str | None,
        Query(description="Landkreis identifier"),
    ] = None,
    schul_id: Annotated[
        str | None,
        Query(description="School identifier"),
    ] = None,
) -> LicenceResponse:
    """
    Licences managed by LC-Halt can be retrieved using this endpoint.

    Provided with a bundesland id and optionally a landkreis id and/or schul id, LC-Halt will return all media the user is allowed to access.

    This endpoint is only accessible with a valid API key.
    """
    logger.info(
        f"Received request with bundesland_id {bundesland_id}, landkreis_id {landkreis_id}, schul_id {schul_id}"
    )
    assigned_media = await media_management.get_all_assigned_media(
        bundesland_id=bundesland_id, landkreis_id=landkreis_id, schul_id=schul_id
    )
    return LicenceResponse(
        bundesland_id=bundesland_id,
        landkreis_id=landkreis_id,
        schul_id=schul_id,
        licenced_media=assigned_media,
    )
