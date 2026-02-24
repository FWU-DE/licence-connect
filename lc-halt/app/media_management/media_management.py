from typing import Annotated, List, Optional
from bson import ObjectId
from fastapi import HTTPException, Response, status
from pydantic import BaseModel, BeforeValidator, ConfigDict, Field
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

    bundesland_id: str = Field(...)
    
    landkreis_id: Optional[str] = Field(...)

    schul_id: Optional[str] = Field(...)

    licenced_media: list[Medium] = Field(...)

    model_config = ConfigDict(
        populate_by_name=True,
        arbitrary_types_allowed=True,
        json_schema_extra={
            "example": {"bundesland_id": "BB", "landkreis_id": "LK-01", "schul_id": "SCH-01", "licenced_media": ["BWS-05050634"]}
        },
    )


class LicencedMediaAssignment(BaseModel):
    bundesland_id: str
    landkreis_id: Optional[str] = None
    schul_id: Optional[str] = None
    licenced_media: list[Medium]


licenced_media_assignment_collection = db.get_collection("licenced_media")


async def get_all_assignments() -> List[LicencedMediaAssignmentModel]:
    # The maximum number of items is not optional. If this is an issue we need to implement limit & offset params.
    return await licenced_media_assignment_collection.find().to_list(10000)


async def set_assignment(
    media_to_licence: LicencedMediaAssignment,
):
    logger.info(media_to_licence)
    validate_media_assignment(media_to_licence)

    new_licenced_media = await licenced_media_assignment_collection.insert_one(
        media_to_licence.model_dump(by_alias=True, exclude=["id"])
    )
    created_media = await licenced_media_assignment_collection.find_one(
        {"_id": new_licenced_media.inserted_id}
    )
    return created_media


async def get_assignment(id: str):
    if (
        media := await licenced_media_assignment_collection.find_one(
            {"_id": ObjectId(id)}
        )
    ) is not None:
        return media

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
    bundesland_id: str, landkreis_id: str | None = None, schul_id: str | None = None
):
    # The maximum number of items is not optional. If this is an issue we need to implement limit & offset params.
    assignments = await licenced_media_assignment_collection.find({
        "$or": [
            {"bundesland_id": bundesland_id, "landkreis_id": None, "schul_id": None},
            {"bundesland_id": bundesland_id, "landkreis_id": landkreis_id, "schul_id": None},
            {"bundesland_id": bundesland_id, "landkreis_id": landkreis_id, "schul_id": schul_id},
        ]
    }).to_list(10000)

    assigned_media = []

    for assignment in assignments:
        for medium in assignment["licenced_media"]:
            if medium not in assigned_media:
                assigned_media.append(medium)

    return assigned_media


def validate_media_assignment(assignment: LicencedMediaAssignment):
    if(assignment.bundesland_id is None):
        raise HTTPException(
            status_code=400,
            detail="Bundesland must be provided",
        )
    
    if(assignment.landkreis_id is None) and (assignment.schul_id is not None):
        raise HTTPException(
            status_code=400,
            detail="Landkreis must be provided in order to use schul_id as well",
        )

