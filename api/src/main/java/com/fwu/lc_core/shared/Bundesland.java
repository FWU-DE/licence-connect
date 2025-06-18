package com.fwu.lc_core.shared;

public enum Bundesland {
    MV,
    RP,
    BW,
    BY,
    BE,
    BB,
    HB,
    HH,
    HE,
    NI,
    NW,
    SL,
    SN,
    ST,
    SH,
    TH,
    //TODO: ONCE WE CONNECT TO PRODUCTION ARIX API, MAKE STK ONLY AVAILABLE IN TEST CODE OR REMOVE IT COMPLETELY
    STK;

    public static Bundesland fromAbbreviation(String abbreviation){
        return switch(abbreviation){
            case "MV", "DE-MV" -> Bundesland.MV;
            case "RP", "DE-RP" -> Bundesland.RP;
            case "BW", "DE-BW" -> Bundesland.BW;
            case "BY", "DE-BY" -> Bundesland.BY;
            case "BE", "DE-BE" -> Bundesland.BE;
            case "BB", "DE-BB" -> Bundesland.BB;
            case "HB", "DE-HB" -> Bundesland.HB;
            case "HH", "DE-HH" -> Bundesland.HH;
            case "HE", "DE-HE" -> Bundesland.HE;
            case "NI", "DE-NI" -> Bundesland.NI;
            case "NW", "DE-NW" -> Bundesland.NW;
            case "SL", "DE-SL" -> Bundesland.SL;
            case "SN", "DE-SN" -> Bundesland.SN;
            case "ST", "DE-ST" -> Bundesland.ST;
            case "SH", "DE-SH" -> Bundesland.SH;
            case "TH", "DE-TH" -> Bundesland.TH;

            case "STK" -> Bundesland.STK;

            default -> throw new IllegalArgumentException(abbreviation);
        };
    }

    public String toISOIdentifier() {
        return "DE-" + this.toString();
    }
}
