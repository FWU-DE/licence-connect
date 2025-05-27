from typing import Annotated
from pydantic import BaseModel
from fastapi import Depends, FastAPI, Query, Request, Security, HTTPException, status
from fastapi.security import APIKeyHeader
import logging
import sys
import os


logger = logging.getLogger(__name__)

stream_handler = logging.StreamHandler(sys.stdout)
log_formatter = logging.Formatter("%(asctime)s [%(levelname)s] %(name)s: %(message)s")
stream_handler.setFormatter(log_formatter)
logger.addHandler(stream_handler)

logger.info("API is starting up")

log_level = os.getenv("LC_HALT_LOG_LEVEL", "INFO")
logger.info(f"Log level set to {log_level}")
logger.setLevel(log_level)


client_api_key_env_variable_name = "LC_HALT_CLIENT_API_KEY"
admin_api_key_env_variable_name = "LC_HALT_ADMIN_API_KEY"

required_env_variables = [
    client_api_key_env_variable_name,
    admin_api_key_env_variable_name,
]
for var in required_env_variables:
    if not os.getenv(var):
        logger.error(f"{var} not configured or empty! Shutting down...")
        exit(1)

client_api_key = os.getenv(client_api_key_env_variable_name)
admin_api_key = os.getenv(admin_api_key_env_variable_name)

req_api_key = APIKeyHeader(name="x-api-key", auto_error=False)


async def handle_api_key(req: Request, key: str = Security(req_api_key)):
    if key not in [client_api_key, admin_api_key]:
        raise HTTPException(status_code=status.HTTP_401_UNAUTHORIZED)

    if req.url.path.startswith("/admin/") and key != admin_api_key:
        raise HTTPException(
            status_code=status.HTTP_403_FORBIDDEN,
            detail="You do not have permission to access this route",
        )

    return key


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


@app.get("/licenced-media")
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


@app.get("/admin/test")
async def test():
    return
