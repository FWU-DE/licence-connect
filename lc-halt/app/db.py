import os
from fastapi import APIRouter
from motor import motor_asyncio

router = APIRouter(prefix="/admin/user_media", tags=["Administration"])

client = motor_asyncio.AsyncIOMotorClient(os.getenv("MONGODB_URL"))
db = client.get_database(os.getenv("MONGO_DB_NAME"))
