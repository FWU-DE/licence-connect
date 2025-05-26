from typing import Annotated
from pydantic import BaseModel
from fastapi import FastAPI, Query

app = FastAPI()


class LicencedMedium(BaseModel):
    id: str


class LicenceResponse(BaseModel):
    userId: str
    bundesland: str | None = None
    schulnummer: str | None = None
    licencedMedia: list[LicencedMedium]


@app.get("/licences/")
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

    Provided with a userId and optionally a bundesland and/or schulnummer, LC-Halt will return all licences matching the request.
    """
    return LicenceResponse(
        userId=userId, bundesland=bundesland, schulnummer=schulnummer, licencedMedia=[]
    )
