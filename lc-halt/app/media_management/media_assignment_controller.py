from typing import List
from fastapi import APIRouter, Body, status
from .media_management import LicencedMediaAssignmentModel, LicencedMediaAssignment
from . import media_management

router = APIRouter(prefix="/admin/media-licence-assignment", tags=["Administration"])


@router.get(
    "/",
    response_model=List[LicencedMediaAssignmentModel],
    status_code=status.HTTP_200_OK,
    response_model_by_alias=False,
)
async def list_media_licence_assignments():
    """
    This endpoint returns all existing assignments.

    It is only accessible with the admin API key.
    """
    return await media_management.get_all_assignments()


@router.post(
    "/",
    response_model=LicencedMediaAssignmentModel,
    status_code=status.HTTP_200_OK,
    response_model_by_alias=False,
)
async def assign_licenced_media(
    licenced_media_body: LicencedMediaAssignment = Body(...),
):
    """
    This endpoint allows to assign licenced media, assigning it either:
    - to a single user
    - to all users of a bundesland
    - to all users of a schule in a bundesland

    It is only accessible with the admin API key.

    ---

    ## LC-Media showcase example values

    bundesland: DE-BB

    ### A note about the bundesland value:
    The request from VIDIS to LC-Core for users assigned to the 'Test Landesportal (IdP)' VIDIS client will always have
    the bundesland as value to which the 'Test Landesportal (IdP)' is currently hardcoded. At the time of writing this,
    it is 'DE-BB'. If the 'Test Landesportal (IdP)' is changed to another bundesland, the value will change accordingly.
    When requesting licences from LC-Halt, LC-Core will *always* use an ISO 3166-2 code for the bundesland
    (e.g. 'DE-BB' for Brandenburg, 'DE-BY' for Bayern). Thus, when assigning media to a bundesland, make sure to use
    the correct ISO 3166-2 code for the bundesland.

    Exemplary SODIX-IDs to be used as value for "id" (with their corresponding titles):
    - BWS-05558456 -> "Natürlich verpackt"
    - BWS-05050676 -> "Störenfriede"
    - BWS-05560775 -> "...dass er nicht auf dem Kopf gehen konnte."
    - BWS-05050366 -> "...endlich mobil!"
    - BWS-05560914 -> "Der Staat bin ich!"
    - BWS-02958086 -> "Ein unzuverlässiges Gefühl..." - Die Mutterliebe
    - BWS-04959365 -> "Heimsuchung" - Ausstellung in der Bundeskunsthalle
    - BWS-02958017 -> "Ich bin keiner, der mit Weisheit geboren wurde" - Leben und Lehre des Konfuzius
    - BWS-04959221 -> "Im Felde unbesiegt!"
    - BWS-05555538 -> "Iss Zucker und sprich süß" [mit Unterrichtsmaterial]
    - BWS-02958063 -> "Küss mich jetzt eintausendmal!" - Eine kleine Kulturgeschichte des Kusses
    - BWS-02958019 -> "Maria hilf!" - Die Wallfahrt nach Altötting
    - BWS-05558483 -> "Re-Cycling"
    - BWS-05559854 -> "So lerne ich den richtigen Umgang mit dem Internet!"
    - BWS-05552856 -> "Stadtluft macht frei!"
    - BWS-05553720 -> "Teuflisch" gefährlich: Okkulte Praktiken
    - BWS-05558860 -> "Was ist der Dritte Stand?"
    - BWS-05562396 -> "Wer hat schon Angst vorm Krankenhaus?"
    - BWS-05562256 -> "Wir haben es doch erlebt..."

    """
    return await media_management.set_assignment(licenced_media_body)


@router.get(
    "/{id}",
    response_model=LicencedMediaAssignmentModel,
    status_code=status.HTTP_200_OK,
    response_model_by_alias=False,
)
async def show_media_licence_assignment(id: str):
    """
    This endpoint allows to retrieve a single assignment using it's id.

    It is only accessible with the admin API key.
    """
    return await media_management.get_assignment(id)


@router.delete("/{id}")
async def delete_media_licence_assignment(id: str):
    """
    This endpoint allows to delete a single assignment using it's id.

    It is only accessible with the admin API key.
    """
    return await media_management.delete_assignment(id)
