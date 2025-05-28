from fastapi import APIRouter

router = APIRouter(prefix="/admin", tags=["Administration"])


@router.get("/test")
async def test():
    return
