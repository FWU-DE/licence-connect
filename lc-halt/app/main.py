from typing import Annotated
from pydantic import BaseModel
from fastapi import Depends, FastAPI, Query, Request, Security, HTTPException, status
from fastapi.security import APIKeyHeader
import logging
import signal
import sys
import os


logger = logging.getLogger(__name__)

logLevel = os.getenv("LC_HALT_LOG_LEVEL", "INFO")
logger.setLevel(logLevel)

stream_handler = logging.StreamHandler(sys.stdout)
log_formatter = logging.Formatter("%(asctime)s [%(levelname)s] %(name)s: %(message)s")
stream_handler.setFormatter(log_formatter)
logger.addHandler(stream_handler)

logger.info("API is starting up")
logger.info(f"Log level set to {logLevel}")


client_api_key = os.getenv("LC_HALT_CLIENT_API_KEY")
if client_api_key is None:
    logger.error("LC_HALT_CLIENT_API_KEY not configured! Shutting down...")
    os.kill(os.getpid(), signal.SIGTERM)

req_api_key = APIKeyHeader(name="x-api-key", auto_error=False)


async def handle_api_key(req: Request, key: str = Security(req_api_key)):
    if key is None:
        raise HTTPException(
            status_code=status.HTTP_401_UNAUTHORIZED,
            detail="You need to provide an api key",
        )

    if client_api_key != key:
        raise HTTPException(
            status_code=status.HTTP_403_FORBIDDEN,
            detail="You do not have permission to access this route",
        )
    yield key


app = FastAPI(
    title="LC Halt",
    version="0.1.0",
    summary="LC Halt ist a licence holding system.",
    description="Using this API, media can be assigned to users, schools or federal states. Other services can then access this information and act accordingly.",
    dependencies=[Depends(handle_api_key)],
)


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
    logger.info(
        f"Received request with userId {userId}, bundesland {bundesland}, schulnummer {schulnummer}"
    )
    return LicenceResponse(
        userId=userId, bundesland=bundesland, schulnummer=schulnummer, licencedMedia=[]
    )
