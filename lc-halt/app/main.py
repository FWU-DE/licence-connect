from fastapi import Depends, FastAPI
from app.util.auth import handle_api_key
from app.media_management import licenced_media_controller, media_assignment_controller

app = FastAPI(
    title="LC Halt",
    version="0.2.0",
    summary="LC Halt is a licence holding system.",
    description="Using this API, media can be assigned to users, schools or federal states. Other services can then access this information and act accordingly.",
    dependencies=[Depends(handle_api_key)],
)


@app.get("/health", tags=["Public"])
async def health():
    """
    Check application health status
    """
    return {"status": "healthy"}


app.include_router(licenced_media_controller.router)
app.include_router(media_assignment_controller.router)
