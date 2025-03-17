﻿from dataclasses import dataclass
from typing import Optional

STANDARD_FIELD_NAMES = ["licence"]

@dataclass
class ArixLicence:
    id: str
    licence: str

    nr: Optional[str] = None
    titel: Optional[str] = None
    typ: Optional[str] = None
    utitel: Optional[str] = None
    sertitel: Optional[str] = None
    serutitel: Optional[str] = None
    adressat: Optional[str] = None
    attribute: Optional[str] = None
    laenge: Optional[str] = None
    produ: Optional[str] = None
    prodjahr: Optional[str] = None
    prodland: Optional[str] = None
    empfende: Optional[str] = None
    vorfbis: Optional[str] = None
    vorfrecht: Optional[str] = None
    fsk: Optional[str] = None
    rechte: Optional[str] = None
    begleit: Optional[str] = None
    preis: Optional[str] = None
    datum: Optional[str] = None
    schlag: Optional[str] = None
    bemerk: Optional[str] = None
    text: Optional[str] = None
    geb: Optional[str] = None
    sprache: Optional[str] = None
    topographie: Optional[str] = None
    personen: Optional[str] = None
    darsteller: Optional[str] = None
    lernziele: Optional[str] = None
    vork: Optional[str] = None
    praedikate: Optional[str] = None
    beigaben: Optional[str] = None
    verfanf: Optional[str] = None
    verfende: Optional[str] = None
    oeffvrecht: Optional[str] = None
    fremdvertrieb: Optional[str] = None
    regie: Optional[str] = None
    lieferant: Optional[str] = None
    bildliste: Optional[str] = None
    orgtitel: Optional[str] = None
    sorttitel: Optional[str] = None
    sortutitel: Optional[str] = None
    sortsertitel: Optional[str] = None
    paratitel: Optional[str] = None
    langtext: Optional[str] = None
    techtext: Optional[str] = None
    drehbuch: Optional[str] = None
    kamera: Optional[str] = None
    schnitt: Optional[str] = None
    ton: Optional[str] = None
    eignung: Optional[str] = None
    musik: Optional[str] = None
    urheber: Optional[str] = None
    auftraggeber: Optional[str] = None
    herausgeber: Optional[str] = None
    litvorlage: Optional[str] = None
    einzeltitel: Optional[str] = None
    sammelmedien: Optional[str] = None
    sprachfassungen: Optional[str] = None
    kontextmedien: Optional[str] = None
    url: Optional[str] = None
    quelle: Optional[str] = None
    kuerzel: Optional[str] = None
    techdat: Optional[str] = None
    autor: Optional[str] = None
    paranr2: Optional[str] = None
    paranr: Optional[str] = None
    didanmerk: Optional[str] = None

