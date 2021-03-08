package org.astrosearcher.classes;

import lombok.Getter;
import lombok.Setter;
import org.astrosearcher.classes.mast.MastResponse;
import org.astrosearcher.classes.simbad.SimbadResponse;
import org.astrosearcher.classes.vizier.VizierResponse;

@Getter
@Setter
public class ResponseData {
//    private MastResponse mastResponse = new MastResponse();
    private MastResponse mastResponse;
    private VizierResponse vizierResponse;
    private SimbadResponse simbadResponse;

    private String dataMSG;

    public ResponseData() {}

    public ResponseData(MastResponse mastResponse) {
        this.mastResponse = mastResponse;
    }

    public boolean containsMastResponse() {
        return mastResponse != null;
    }

    public boolean containsVizierResponse() {
        return vizierResponse != null;
    }

    public boolean containsSimbadResponse() {
        return vizierResponse != null;
    }

    public boolean isEmpty() {
        return mastResponse == null && vizierResponse == null && simbadResponse == null;
    }
}
