import os
from typing import Annotated, List, Optional
from bson import ObjectId
from fastapi import APIRouter, Body, HTTPException, Response, status
from motor import motor_asyncio
from pydantic import BaseModel, BeforeValidator, ConfigDict, Field
from pymongo import ReturnDocument
from ..logger import logger

router = APIRouter(prefix="/admin/user_media", tags=["Administration"])

client = motor_asyncio.AsyncIOMotorClient(os.getenv("MONGODB_URL"))
db = client.get_database(os.getenv("MONGO_DB_NAME"))

user_media_collection = db.get_collection("user_media")

PyObjectId = Annotated[str, BeforeValidator(str)]


class UserMediaModel(BaseModel):
    """
    Container for a single user media record.
    """

    # This will be aliased to `_id` when sent to MongoDB,
    # but provided as `id` in the API requests and responses.

    id: Optional[PyObjectId] = Field(alias="_id", default=None)

    user_id: str = Field(...)

    licenced_media: list[str] = Field(...)

    model_config = ConfigDict(
        populate_by_name=True,
        arbitrary_types_allowed=True,
        json_schema_extra={
            "example": {"user_id": "1234", "licenced_media": ["BWS-05050634"]}
        },
    )


class UpdateUserMediaModel(BaseModel):
    """
    A set of optional updates to be made to a document in the database.
    """

    user_id: Optional[str] = None

    licenced_media: Optional[list[str]] = None

    model_config = ConfigDict(
        arbitrary_types_allowed=True,
        json_encoders={ObjectId: str},
        json_schema_extra={
            "example": {"user_id": "1234", "licenced_media": ["BWS-05050634"]}
        },
    )


class SetMediaModel(BaseModel):
    licenced_media: list[str] = []


class UserMediaCollection(BaseModel):
    user_media: List[UserMediaModel]


@router.get(
    "/",
    response_model=UserMediaCollection,
    status_code=status.HTTP_200_OK,
    response_model_by_alias=False,
)
async def list_user_media():
    return UserMediaCollection(
        user_media=await user_media_collection.find().to_list(1000)
    )


@router.post(
    "/{user_id}",
    response_model=UserMediaModel,
    status_code=status.HTTP_201_CREATED,
    response_model_by_alias=False,
)
async def set_user_media(user_id: str, user_media_body: SetMediaModel = Body(...)):
    existing_user_media = await user_media_collection.find_one({"user_id": user_id})
    user_media_to_set = UserMediaModel(
        user_id=user_id, licenced_media=user_media_body.licenced_media
    )

    if existing_user_media is None:
        new_user_media = await user_media_collection.insert_one(
            user_media_to_set.model_dump(by_alias=True, exclude=["id"])
        )
        created_user_media = await user_media_collection.find_one(
            {"_id": new_user_media.inserted_id}
        )
        return created_user_media

    logger.info(existing_user_media["_id"])

    # Remove None values
    clean_user_media_to_set = {
        k: v
        for k, v in user_media_to_set.model_dump(by_alias=True).items()
        if v is not None
    }

    user_media_collection.find_one_and_update(
        {"_id": existing_user_media["_id"]},
        {"$set": clean_user_media_to_set},
        return_document=ReturnDocument.AFTER,
    )

    return await user_media_collection.find_one({"user_id": user_id})


@router.get(
    "/{user_id}",
    response_model=UserMediaModel,
    status_code=status.HTTP_200_OK,
    response_model_by_alias=False,
)
async def show_user_media(user_id: str):
    if (
        user_media := await user_media_collection.find_one({"user_id": user_id})
    ) is not None:
        return user_media

    raise HTTPException(
        status_code=404, detail=f"No user media for user {user_id} found"
    )


@router.delete("/{user_id}")
async def delete_student(user_id: str):
    delete_result = await user_media_collection.delete_many({"user_id": user_id})

    if delete_result.deleted_count > 0:
        return Response(status_code=status.HTTP_204_NO_CONTENT)

    raise HTTPException(
        status_code=404, detail=f"No user media for user {user_id} found"
    )
