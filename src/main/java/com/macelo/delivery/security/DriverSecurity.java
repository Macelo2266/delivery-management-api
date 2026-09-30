package com.macelo.delivery.security;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

@Component("driverSecurity")
public class DriverSecurity {

    public boolean isSelf(Long driverId, Authentication authentication) {
        if (!(authentication.getPrincipal() instanceof UserDetailsImpl userDetails)) {
            return false;
        }
        Long ownDriverId = userDetails.getUser().getDriverId();
        return ownDriverId != null && ownDriverId.equals(driverId);
    }
}
