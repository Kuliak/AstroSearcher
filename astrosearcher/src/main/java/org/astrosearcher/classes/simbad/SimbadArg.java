package org.astrosearcher.classes.simbad;

import org.astrosearcher.enums.simbad.SimbadArgType;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

/**
 * Class provides basic functionality for easier building of URL request for Simbad server - easier chaining
 * of arguments (parameters) in URL.
 *
 * @author Ľuboslav Halama
 */
public class SimbadArg {

    private SimbadArgType type;
    private Object value;

    public SimbadArg(SimbadArgType type, Object value) {
        this.type  = type;
        this.value = value;
    }

    @Override
    public String toString() {
        return "&" + type.toString() + "=" + URLEncoder.encode(value.toString(), StandardCharsets.UTF_8);
    }
}
