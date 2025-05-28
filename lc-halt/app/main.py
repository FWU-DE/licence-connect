from fastapi import Depends, FastAPI
from .auth import handle_api_key
from app.routers import admin, licenced_media, user_media


app = FastAPI(
    title="LC Halt",
    version="0.1.0",
    summary="LC Halt is a licence holding system.",
    description="Using this API, media can be assigned to users, schools or federal states. Other services can then access this information and act accordingly.",
    dependencies=[Depends(handle_api_key)],
)


@app.get("/health", tags=["Public"])
async def read_main():
    return {"status": "healthy"}


app.include_router(admin.router)
app.include_router(licenced_media.router)
app.include_router(user_media.router)
