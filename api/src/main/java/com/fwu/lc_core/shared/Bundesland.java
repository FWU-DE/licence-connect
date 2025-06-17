package com.fwu.lc_core.shared;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fwu.lc_core.shared.validators.HasValue;

public enum Bundesland implements HasValue {
    MV("MV"),
    RP("RP"),
    BW("BW"),
    BY("BY"),
    BE("BE"),
    BB("BB"),
    HB("HB"),
    HH("HH"),
    HE("HE"),
    NI("NI"),
    NW("NW"),
    SL("SL"),
    SN("SN"),
    ST("ST"),
    SH("SH"),
    TH("TH"),
    MV_Hyphenated("DE-MV"),
    @JsonProperty("DE-RP")
    RP_Hyphenated("DE-RP"),
    @JsonProperty("DE-BW")
    BW_Hyphenated("DE-BW"),
    @JsonProperty("DE-BY")
    BY_Hyphenated("DE-BY"),
    @JsonProperty("DE-BE")
    BE_Hyphenated("DE-BE"),
    @JsonProperty("DE-BB")
    BB_Hyphenated("DE-BB"),
    @JsonProperty("DE-HB")
    HB_Hyphenated("DE-HB"),
    @JsonProperty("DE-HH")
    HH_Hyphenated("DE-HH"),
    @JsonProperty("DE-HE")
    HE_Hyphenated("DE-HE"),
    @JsonProperty("DE-NI")
    NI_Hyphenated("DE-NI"),
    @JsonProperty("DE-NW")
    NW_Hyphenated("DE-NW"),
    @JsonProperty("DE-SL")
    SL_Hyphenated("DE-SL"),
    @JsonProperty("DE-SN")
    SN_Hyphenated("DE-SN"),
    @JsonProperty("DE-ST")
    ST_Hyphenated("DE-ST"),
    @JsonProperty("DE-SH")
    SH_Hyphenated("DE-SH"),
    @JsonProperty("DE-TH")
    TH_Hyphenated("DE-TH"),
    DE_MV("DE_MV"),
    DE_RP("DE_RP"),
    DE_BW("DE_BW"),
    DE_BY("DE_BY"),
    DE_BE("DE_BE"),
    DE_BB("DE_BB"),
    DE_HB("DE_HB"),
    DE_HH("DE_HH"),
    DE_HE("DE_HE"),
    DE_NI("DE_NI"),
    DE_NW("DE_NW"),
    DE_SL("DE_SL"),
    DE_SN("DE_SN"),
    DE_ST("DE_ST"),
    DE_SH("DE_SH"),
    DE_TH("DE_TH"),
    //TODO: ONCE WE CONNECT TO PRODUCTION ARIX API, MAKE STK ONLY AVAILABLE IN TEST CODE OR REMOVE IT COMPLETELY
    STK("STK");


    public final String value;

    Bundesland(String value) {
        this.value = value;
    }

}
