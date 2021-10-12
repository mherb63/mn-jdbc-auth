package com.mherb.mnaut.controller;

import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Get;
import io.micronaut.security.annotation.Secured;
import io.micronaut.security.authentication.Authentication;
import io.micronaut.security.rules.SecurityRule;
import lombok.extern.slf4j.Slf4j;

import java.security.Principal;

@Controller("/secured")
@Secured(SecurityRule.IS_AUTHENTICATED)
@Slf4j
public class SecureEndpointController {

    @Get("/status")
    public String status(Principal principal) {
        Authentication details = (Authentication) principal;
        String msg =  "You are authenticated as user with name: " + principal.getName();
        msg += " details: " + details.getAttributes();

        log.debug(msg);

        return msg;
    }
}
