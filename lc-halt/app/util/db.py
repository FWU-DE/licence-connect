import os
from motor import motor_asyncio
import asyncio

client = motor_asyncio.AsyncIOMotorClient(os.getenv("MONGODB_URL"))
client.get_io_loop = asyncio.get_running_loop

db = client.get_database(os.getenv("MONGO_DB_NAME"))
