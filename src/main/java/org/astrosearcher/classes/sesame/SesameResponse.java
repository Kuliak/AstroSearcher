package org.astrosearcher.classes.sesame;

import lombok.Getter;
import lombok.NoArgsConstructor;
import org.astrosearcher.classes.constants.RegularExpressions;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Class represents only subset of data obtained from Sesame (only subset is
 * needed - aliases).
 *
 * @author Ľuboslav Halama
 */
@Getter
@NoArgsConstructor
public class SesameResponse {

    private String id;
    private List<String> aliases = new ArrayList<>();

    public SesameResponse(String response) {

        Matcher matcher = Pattern.compile(RegularExpressions.SESAME_VALID_RESPONSE).matcher(response);
        if ( !matcher.matches() ) {
            return;
        }

        id = matcher.group(1);

        // load all aliases
        matcher = Pattern.compile(RegularExpressions.SESAME_XML_ALIAS).matcher(response);
        while (matcher.find()) {
            aliases.add(matcher.group(1));
        }
    }
}
