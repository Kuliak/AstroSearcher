package org.astrosearcher.classes;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.astrosearcher.classes.constants.messages.ExceptionMSG;
import org.astrosearcher.classes.constants.Limits;
import org.astrosearcher.classes.constants.RegularExpressions;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


@Getter
@AllArgsConstructor
public class PositionInput {

    private Position position;
//    private double   radius;

    public PositionInput(double ra, double dec) {
        this.position = new Position(ra, dec);
//        this.radius = radius;
    }

    public PositionInput(String inputToParse) {
        Pattern pattern = Pattern.compile(RegularExpressions.POSITION_INPUT_REGEX);
        Matcher matcher = pattern.matcher(inputToParse);

        if (matcher.matches()) {

            double ra = Double.parseDouble(matcher.group(1));
            if (ra < Limits.RA_MIN || ra >= Limits.RA_MAX) {
                throw new IllegalArgumentException(ExceptionMSG.INVALID_RIGHT_ASCENSION_EXCEPTION);
            }

            double dec = Double.parseDouble(matcher.group(2));
            if (dec < Limits.DEC_MIN || dec > Limits.DEC_MAX) {
                throw new IllegalArgumentException(ExceptionMSG.INVALID_DECLINATION_EXCEPTION);
            }

//            radius = matcher.group(3) != null ? Double.parseDouble(matcher.group(3)) : Limits.DEFAULT_RADIUS;
            position = new Position(ra, dec);

            return;
        }
        throw new IllegalArgumentException(ExceptionMSG.INVALID_POSITION_AND_RADIUS_INPUT_EXCEPTION);

    }

    public Map<String, Double> getAsMap() {
        Map<String, Double> converted = new HashMap<>();
        converted.put("ra", position.getRa());
        converted.put("dec", position.getDec());
//        converted.put("radius", radius);
        return converted;
    }

    public double getRa() {
        return position.getRa();
    }

    public double getDec() {
        return position.getDec();
    }

    @Override
    public String toString() {
        return position.toString();
    } // + " " + radius;
}
