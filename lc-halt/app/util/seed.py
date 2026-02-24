"""
Seed data initialization for LC-Halt/LC-Media demo purposes.
Automatically seeds data if the database is empty.
"""

from app.util.logger import logger
from app.util.db import db
from app.media_management.media_management import LicencedMediaAssignment, Medium


async def seed_initial_data():
    """
    The demo data covers all three hierarchy levels:
    - Bundesland only (state-wide)
    - Bundesland + Landkreis (district-level)
    - Bundesland + Landkreis + Schule (school-level)
    """
    collection = db["licenced_media"]
    
    existing_count = await collection.count_documents({})
    if existing_count > 0:
        logger.info(f"Database already contains {existing_count} assignments. Skipping seed data.")
        return
    
    logger.info("Database is empty. Seeding demo assignments...")
    
    demo_assignments = [
        # bundesland only
        LicencedMediaAssignment(
            bundesland_id="DE-BB",
            licenced_media=[
                Medium(id="BWS-05558456"),  # "Natürlich verpackt"
                Medium(id="BWS-05050676"),  # "Störenfriede"
                Medium(id="BWS-05560775"),  # "...dass er nicht auf dem Kopf gehen konnte."
            ]
        ),
        
        # bundesland + landkreis
        LicencedMediaAssignment(
            bundesland_id="DE-BB",
            landkreis_id="XP",
            licenced_media=[
                Medium(id="BWS-05050366"),  # "...endlich mobil!"
                Medium(id="BWS-05560914"),  # "Der Staat bin ich!"
                Medium(id="BWS-02958086"),  # "Ein unzuverlässiges Gefühl..." - Die Mutterliebe
            ]
        ),
        
        # bundesland + landkreis + specific school
        LicencedMediaAssignment(
            bundesland_id="DE-BB",
            landkreis_id="XP",  
            schul_id="12345",
            licenced_media=[
                Medium(id="BWS-05555538"),  # "Iss Zucker und sprich süß"
                Medium(id="BWS-05558483"),  # "Re-Cycling"
                Medium(id="BWS-05559854"),  # "So lerne ich den richtigen Umgang mit dem Internet!"
            ]
        ),
    ]
    
    documents = [assignment.model_dump(by_alias=True, exclude=["id"]) for assignment in demo_assignments]
    result = await collection.insert_many(documents)
    
    logger.info(f"Successfully seeded {len(result.inserted_ids)} demo assignments.")
