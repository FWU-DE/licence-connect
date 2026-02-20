from typing import Optional

from arix_licence import ArixLicence

STR_LICENCE_1 = ArixLicence(id="STR_LIZENZ_1", licence="2099-01-01")
STR_LICENCE_2 = ArixLicence(id="STR_LIZENZ_2", licence="2099-01-01")
STK_LICENCE_1 = ArixLicence(id="STK_LIZENZ_1", licence="2099-01-01")
NW_LICENCE_1 = ArixLicence(id="NW_LIZENZ_1", licence="2099-01-01")
NW_LICENCE_2 = ArixLicence(id="NW_LIZENZ_2", licence="2099-01-01")
BY_LICENCE_1 = ArixLicence(id="BY_1_23ui4g23c", licence="2099-01-01")
ORT1_LICENCE_1 = ArixLicence(id="ORT1_LIZENZ_1", licence="2099-01-01")
ORT2_LICENCE_1 = ArixLicence(id="ORT2_LIZENZ_1", licence="2099-01-01")
ORT2_LICENCE_2 = ArixLicence(id="ORT2_LIZENZ_2", licence="2099-01-01")
SCHOOL_1_LICENCE_1 = ArixLicence(id="F3453_LIZENZ_1", licence="2099-01-01")
SCHOOL_1_LICENCE_2 = ArixLicence(id="F3453_LIZENZ_2", licence="2099-01-01")
SCHOOL_2_LICENCE_1 = ArixLicence(id="6bgqc95qx", licence="2099-01-01")
SCHOOL_2_LICENCE_2 = ArixLicence(id="32869q43cb34", licence="2099-01-01")

land_licences = {
    "NW": [NW_LICENCE_1, NW_LICENCE_2],
    "BY": [BY_LICENCE_1],
    "STK": [STK_LICENCE_1],
}
standort_licences = {
    "ORT1": [ORT1_LICENCE_1],
    "ORT2": [ORT2_LICENCE_1, ORT2_LICENCE_2],
    "STR": [STR_LICENCE_1, STR_LICENCE_2],
}
schul_licences = {
    "f3453b": [SCHOOL_1_LICENCE_1, SCHOOL_1_LICENCE_2],
    "0mß5234cv": [SCHOOL_2_LICENCE_1, SCHOOL_2_LICENCE_2],
}


def get(l, k):
    return l[k] if k in l.keys() else []


def get_example_licences(
    land: Optional[str],
    standortnummer: Optional[str],
    schulnummer: Optional[str],
):
    return (
        get(land_licences, land)
        + get(standort_licences, standortnummer)
        + get(schul_licences, schulnummer)
    )
