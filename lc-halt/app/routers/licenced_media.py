from typing import Annotated
from fastapi import APIRouter, Query
from pydantic import BaseModel
from ..logger import logger

router = APIRouter()


class LicencedMedium(BaseModel):
    """
    Represents one licenced medium.
    Further information regarding the licencing of this medium may be added here.
    """

    id: str


class LicenceResponse(BaseModel):
    userId: str
    bundesland: str | None = None
    schulnummer: str | None = None
    licencedMedia: list[LicencedMedium]


@router.get("/licenced-media")
async def read_assigned_licences(
    userId: Annotated[
        str,
        Query(description="User identifier, usually assigned by VIDIS"),
    ],
    bundesland: Annotated[
        str | None,
        Query(description="Federal state identifier"),
    ] = None,
    schulnummer: Annotated[
        str | None,
        Query(description="School identifier"),
    ] = None,
) -> LicenceResponse:
    """
    Licences managed by LC-Halt can be retrieved using this endpoint.

    Provided with a userId and optionally a bundesland and/or schulnummer, LC-Halt will return all media the user is allowed to access.
    """
    logger.info(
        f"Received request with userId {userId}, bundesland {bundesland}, schulnummer {schulnummer}"
    )
    return LicenceResponse(
        userId=userId,
        bundesland=bundesland,
        schulnummer=schulnummer,
        licencedMedia=[LicencedMedium(id="BWS-05050634")],
    )
