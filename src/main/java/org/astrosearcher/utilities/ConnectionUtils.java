package org.astrosearcher.utilities;

import com.google.gson.Gson;
import org.astrosearcher.classes.RequestObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;

/**
 * Class provides general functionality for sending requests and retrieving
 * responses from servers.
 *
 * @author Ľuboslav Halama
 */
public class ConnectionUtils {

    private static final Logger log = LoggerFactory.getLogger(ConnectionUtils.class);

    public static String sendRequest(RequestObject obj) {

        StringBuilder responseData = new StringBuilder();

        try {
            log.debug("            Opening connection ( {} )...", obj.getConnectionURL().toString());

            HttpURLConnection connection = (HttpURLConnection) obj.getConnectionURL().openConnection();
            connection.setRequestMethod("POST");

            // set request parameters
            log.debug("            Sending parameters...");
            connection.setDoOutput(true);
            OutputStream os = connection.getOutputStream();
            os.write(obj.getParamsAsBytes());
            os.flush();
            os.close();

            // Check response code
            if (connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
                log.debug("            RESPONSE CODE: {} ", connection.getResponseCode());
                log.debug("            RESPONSE MESSAGE: {} ", connection.getResponseMessage());
                return null;
            }

            // read response data and store them into 'responseData'
            log.debug("            Reading response...");
            BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String inputLine;

            while ((inputLine = in.readLine()) != null) {
                responseData.append(inputLine);
                responseData.append('\n');
            }
            in.close();

            // after all the work is done, we have to close connection
            connection.disconnect();

        } catch (Exception e) {
            return null;
        }

        return responseData.toString();
    }

    public static ResponseEntity<Object> prepareJsonResponseEntity(Object data) {
        Gson gson = new Gson();
        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_TYPE, "application/json; charset=UTF-8");
        return new ResponseEntity<>(gson.toJson(data), headers, HttpStatus.OK);
    }
}
