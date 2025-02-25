from typing import Optional

from arix_licence import ArixLicence

NW_LICENCE_1 = ArixLicence(id="1", nr="NW_LIZENZ_1")
NW_LICENCE_2 = ArixLicence(id="2", nr="NW_LIZENZ_2")
BY_LICENCE_1 = ArixLicence(id="3", nr="BY_1_23ui4g23c")
ORT1_LICENCE_1 = ArixLicence(id="4", nr="ORT1_LIZENZ_1")
ORT2_LICENCE_1 = ArixLicence(id="5", nr="ORT2_LIZENZ_1")
ORT2_LICENCE_2 = ArixLicence(id="6", nr="ORT2_LIZENZ_2")
SCHOOL_1_LICENCE_1 = ArixLicence(id="7", nr="F3453_LIZENZ_1")
SCHOOL_1_LICENCE_2 = ArixLicence(id="8", nr="F3453_LIZENZ_2")
SCHOOL_2_LICENCE_1 = ArixLicence(id="9", nr="6bgqc95qx")
SCHOOL_2_LICENCE_2 = ArixLicence(id="10", nr="32869q43cb34")
USER_1_LICENCE_1 = ArixLicence(id="11", nr="20394_LIZENZ_1")
USER_1_LICENCE_2 = ArixLicence(id="12", nr="20394_LIZENZ_2")
USER_2_LICENCE_1 = ArixLicence(id="13", nr="UIOC_QWUE_QASD_REIJ")
USER_2_LICENCE_2 = ArixLicence(id="14", nr="HPOA_SJKC_EJKA_WHOO")

land_licences={
    "NW": [NW_LICENCE_1, NW_LICENCE_2],
    "BY": [BY_LICENCE_1],
}
standort_licences={
    "ORT1": [ORT1_LICENCE_1],
    "ORT2": [ORT2_LICENCE_1, ORT2_LICENCE_2],
}
schul_licences={
    "f3453b": [SCHOOL_1_LICENCE_1, SCHOOL_1_LICENCE_2],
    "0mß5234cv": [SCHOOL_2_LICENCE_1, SCHOOL_2_LICENCE_2],
}
user_licences={
    "20394": [USER_1_LICENCE_1, USER_1_LICENCE_2],
    "student.2": [USER_2_LICENCE_1, USER_2_LICENCE_2],
}
def get(l, k):
    return l[k] if k in l.keys() else []

def get_example_licences(land: Optional[str], standortnummer: Optional[str], schulnummer: Optional[str], userid: Optional[str]):
    return get(land_licences, land) + get(standort_licences, standortnummer) + get(schul_licences, schulnummer) + get(user_licences, userid)
