from fastapi import APIRouter

router = APIRouter()


@router.get("/admin/test")
async def test():
    return
