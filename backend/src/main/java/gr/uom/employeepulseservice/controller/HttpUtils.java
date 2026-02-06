package gr.uom.employeepulseservice.controller;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

public final class HttpUtils {

    private HttpUtils() {
    }

    public static void validateOrganizationHeader(String pathOrgName, String headerOrgName) {
        if (headerOrgName != null && !headerOrgName.equals(pathOrgName)) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Path organization name does not match X-Organization-Name header"
            );
        }
    }
}


