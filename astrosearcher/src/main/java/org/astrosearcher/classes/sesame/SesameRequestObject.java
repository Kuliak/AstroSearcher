package org.astrosearcher.classes.sesame;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.astrosearcher.TomcatConfig;
import org.astrosearcher.classes.RequestObject;
import org.astrosearcher.AppConfig;
import org.astrosearcher.classes.constants.cds.SesameConstants;
import org.astrosearcher.utilities.ConnectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.MalformedURLException;
import java.net.URL;

/**
 * Class represents request object which is used in URL request sent to Sesame (CDS).
 *
 * Class provides basic properties for sending a request to Sesame server
 * as well as main functionality for sending the given request by our web
 * application (implementation of abstract methods from abstract class RequestObject).
 *
 * @author Ľuboslav Halama
 */
@Getter
@AllArgsConstructor
public class SesameRequestObject extends RequestObject {

    private static final Logger log = LoggerFactory.getLogger(TomcatConfig.class);

    private String id;

    @Override
    public String send() {
        if ( AppConfig.DEBUG ) {
            log.debug("\n    >>> Starting to query Sesame...\n");
        }

        return ConnectionUtils.sendRequest(this);
    }

    @Override
    public URL getConnectionURL() throws MalformedURLException {
        return new URL(SesameConstants.CONNECTION_URL);
    }

    @Override
    public byte[] getParamsAsBytes() {
        return id.getBytes();
    }
}
