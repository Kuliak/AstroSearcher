package org.astrosearcher.enums.cds.xmatch;

/**
 * All the compulsory and a subset of optional arguments used for X-Match
 * service (provided by CDS).
 *
 * @author Ľuboslav Halama
 */
public enum XMatchArgType {
    CATALOG1("cat1"),
    CATALOG1_RA_COL("colRA1"),
    CATALOG1_DEC_COL("colDec1"),

    CATALOG2("cat2"),
    CATALOG2_RA_COL("colRA2"),
    CATALOG2_DEC_COL("colDec2"),

    MAX_DISTANCE("distMaxArcsec"),
    MAX_RECORDS("MAXREC"),
    RESPONSE_FORMAT("RESPONSEFORMAT"),
    REQUEST_TYPE("REQUEST");

    private String urlName;

    XMatchArgType(String urlName) {
        this.urlName = urlName;
    }

    @Override
    public String toString() {
        return urlName;
    }
}
