package gr.uom.employeepulseservice.controller;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

public final class HttpUtils {

    private HttpUtils() {
    }

    public static void validateOrganizationHeader(Integer pathOrgId, Integer headerOrgId) {
        if (headerOrgId != null && !headerOrgId.equals(pathOrgId)) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Path organization id does not match X-Organization-Id header"
            );
        }
    }
}


