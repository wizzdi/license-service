package com.flexicore.license.service;

import com.flexicore.license.model.LicenseRequest;
import com.flexicore.security.SecurityContextBase;

public class LicenseRequestUpdateEvent {
    private LicenseRequest licenseRequest;
    private SecurityContextBase securityContextBase;

    public LicenseRequest getLicenseRequest() {
        return licenseRequest;
    }

    public <T extends LicenseRequestUpdateEvent> T setLicenseRequest(LicenseRequest licenseRequest) {
        this.licenseRequest = licenseRequest;
        return (T) this;
    }

    public SecurityContextBase getSecurityContextBase() {
        return securityContextBase;
    }

    public <T extends LicenseRequestUpdateEvent> T setSecurityContextBase(SecurityContextBase securityContextBase) {
        this.securityContextBase = securityContextBase;
        return (T) this;
    }
}
