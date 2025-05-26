from typing import Annotated
from pydantic import BaseModel
from fastapi import FastAPI, Query
from fastapi.openapi.utils import get_openapi

app = FastAPI()


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

    Provided with a userId and optionally a bundesland and/or schulnummer, LC-Halt will return all media the user is allowed to access.
    """
    return LicenceResponse(
        userId=userId, bundesland=bundesland, schulnummer=schulnummer, licencedMedia=[]
    )


def custom_openapi():
    if app.openapi_schema:
        return app.openapi_schema
    openapi_schema = get_openapi(
        title="LC Halt",
        version="0.1.0",
        summary="LC Halt ist a licence holding system.",
        description="Using this API, media can be assigned to users, schools or federal states. Other services can then access this information and act accordingly.",
        routes=app.routes,
    )
    app.openapi_schema = openapi_schema
    return app.openapi_schema


app.openapi = custom_openapi
