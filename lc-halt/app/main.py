from fastapi import Depends, FastAPI
from app.util.auth import handle_api_key
from app.media_management import licenced_media_controller, media_assignment_controller

_description = """"""

app = FastAPI(
    title="LicenceConnect Halt (LC-Halt)",
    version="0.2.0",
    summary="LC-Halt is an example licence holding system.",
    description="Using this API, media can be assigned to users, schools or federal states. Other services can then access this information and act accordingly.",
    dependencies=[Depends(handle_api_key)],
)


@app.get("/health", tags=["Public"])
async def check_if_the_application_is_up_and_running():
    """
    If the application is up and running, this GET request will return a 200 OK response with content ```{'status': 'healthy'}```

    It is accessible both without and with an API key.
    """
    return {"status": "healthy"}


app.include_router(licenced_media_controller.router)
app.include_router(media_assignment_controller.router)
