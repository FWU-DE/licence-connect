from typing import Annotated, List, Optional
from bson import ObjectId
from fastapi import HTTPException, Response, status
from pydantic import BaseModel, BeforeValidator, ConfigDict, Field
from pymongo import ReturnDocument
from app.util.logger import logger
from app.util.db import db


PyObjectId = Annotated[str, BeforeValidator(str)]


class Medium(BaseModel):
    """
    Represents one licenced medium.
    Further information regarding the licencing of this medium may be added here.
    """

    id: str


class LicencedMediaAssignmentModel(BaseModel):
    """
    Container for a single licenced media record.
    """

    # This will be aliased to `_id` when sent to MongoDB,
    # but provided as `id` in the API requests and responses.
    id: Optional[PyObjectId] = Field(alias="_id", default=None)

    user_id: Optional[str] = Field(...)

    bundesland_id: Optional[str] = Field(...)

    schul_id: Optional[str] = Field(...)

    licenced_media: list[Medium] = Field(...)

    model_config = ConfigDict(
        populate_by_name=True,
        arbitrary_types_allowed=True,
        json_schema_extra={
            "example": {"user_id": "1234", "licenced_media": ["BWS-05050634"]}
        },
    )


class LicencedMediaAssignment(BaseModel):
    user_id: Optional[str] = None
    bundesland_id: Optional[str] = None
    schul_id: Optional[str] = None
    licenced_media: list[Medium]


licenced_media_assignment_collection = db.get_collection("licenced_media")


async def get_all_assignments() -> List[LicencedMediaAssignmentModel]:
    return await licenced_media_assignment_collection.find().to_list(1000)


async def set_assignment(
    media_to_licence: LicencedMediaAssignment,
):
    logger.info(media_to_licence)
    assert_media_assignment_is_valid(media_to_licence)
    existing_licenced_media = await licenced_media_assignment_collection.find_one(
        {
            "user_id": media_to_licence.user_id,
            "bundesland_id": media_to_licence.bundesland_id,
            "schul_id": media_to_licence.schul_id,
        }
    )

    if existing_licenced_media is None:
        new_licenced_media = await licenced_media_assignment_collection.insert_one(
            media_to_licence.model_dump(by_alias=True, exclude=["id"])
        )
        created_user_media = await licenced_media_assignment_collection.find_one(
            {"_id": new_licenced_media.inserted_id}
        )
        return created_user_media

    # Remove None values
    clean_user_media_to_set = {
        k: v
        for k, v in media_to_licence.model_dump(by_alias=True).items()
        if v is not None
    }

    licenced_media_assignment_collection.find_one_and_update(
        {"_id": existing_licenced_media["_id"]},
        {"$set": clean_user_media_to_set},
        return_document=ReturnDocument.AFTER,
    )

    return await licenced_media_assignment_collection.find_one(
        {
            "user_id": media_to_licence.user_id,
            "bundesland_id": media_to_licence.bundesland_id,
            "schul_id": media_to_licence.schul_id,
        }
    )


async def get_assignment(id: str):
    if (
        user_media := await licenced_media_assignment_collection.find_one(
            {"_id": ObjectId(id)}
        )
    ) is not None:
        return user_media

    raise HTTPException(
        status_code=404,
        detail=f"No licenced media for id {id} found",
    )


async def delete_assignment(id: str):
    delete_result = await licenced_media_assignment_collection.delete_many(
        {"_id": ObjectId(id)}
    )

    if delete_result.deleted_count > 0:
        return Response(status_code=status.HTTP_204_NO_CONTENT)

    raise HTTPException(
        status_code=404,
        detail=f"No licenced media for id {id} found",
    )


async def get_all_assigned_media(
    user_id: str, bundesland_id: str | None = None, schul_id: str | None = None
):
    assignments = await licenced_media_assignment_collection.find(
        {
            "$or": [
                {"user_id": user_id, "bundesland_id": None, "schul_id": None},
                {"bundesland_id": bundesland_id, "schul_id": schul_id, "user_id": None},
                {"bundesland_id": bundesland_id, "schul_id": None, "user_id": None},
            ]
        }
    ).to_list(1000)

    assigned_media = []

    for assignment in assignments:
        for medium in assignment["licenced_media"]:
            if medium not in assigned_media:
                assigned_media.append(medium)

    return assigned_media


def assert_media_assignment_is_valid(assignment: LicencedMediaAssignment):
    if (assignment.schul_id is not None) & (assignment.bundesland_id is None):
        raise HTTPException(
            status_code=400,
            detail="Schulkennung is only valid in combination with bundesland",
        )

    if (assignment.user_id is not None) & (
        (assignment.bundesland_id is not None) | (assignment.schul_id is not None)
    ):
        raise HTTPException(
            status_code=400,
            detail="Media may only be assigned to specific users OR bundesland/schule",
        )
