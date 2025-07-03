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

    userId: 8e80238b-d651-4c85-9f84-a89fff204c37 (this is the VIDIS user id of the user 'bbmv-l2' in the client 'lc-media-o')  

    bundesland: DE-BB (this is the Bundesland of the 'bbmv-l2' user)  

    Exemplary SODIX-IDs to be used as value for "id" (with their corresponding titles):  
    - BWS-05558456 -> "Natürlich verpackt"
    - BWS-05050676 -> "Störenfriede"
    - BWS-05560775 -> "...dass er nicht auf dem Kopf gene konnte."
    - BWS-05050366 -> "...endlich mobil!"
    - BWS-02958064 -> "Deine Brüste sind wie Trauben!" - Lust und Liebe in der Bibel
    - BWS-05560914 -> "Der Staat bin ich!"
    - BWS-02958086 -> "Ein unzuverlässiges Gefühl..." - Die Mutterliebe
    - BWS-05553540 -> "Happy slapping"
    - BWS-05553182 -> "Haut der Votze in die Schnauze"
    - BWS-04959365 -> "Heimsuchung" - Ausstellung in der Bundeskunsthalle
    - BWS-02958017 -> "Ich bin keiner, der mit Weisheit geboren wurde" - Leben und Lehre des Konfuzius
    - BWS-04959221 -> "Im Felde unbesiegt!"
    - BWS-05555538 -> "Iss Zucker und sprich süß" [mit Unterrichtsmaterial]
    - BWS-04958309 -> "Jetzt weiß ich, es war falsch" - Alkohol in der Schwangerschaft
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
