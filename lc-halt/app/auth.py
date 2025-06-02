from fastapi import HTTPException, Request, Security, status
from fastapi.security import APIKeyHeader
from .logger import logger
import os

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

public_routes = ["/"]


async def handle_api_key(req: Request, key: str = Security(req_api_key)):
    if req.url.path in public_routes:
        return

    if key not in [client_api_key, admin_api_key]:
        raise HTTPException(status_code=status.HTTP_401_UNAUTHORIZED)

    if req.url.path.startswith("/admin/") and key != admin_api_key:
        raise HTTPException(
            status_code=status.HTTP_403_FORBIDDEN,
            detail="You do not have permission to access this route",
        )

    return key
