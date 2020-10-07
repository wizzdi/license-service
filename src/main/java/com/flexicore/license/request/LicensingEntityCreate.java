package com.flexicore.license.request;


import com.flexicore.request.BaseclassCreate;

/**
 * Created by Asaf on 17/10/2016.
 */
public class LicensingEntityCreate extends BaseclassCreate {

    private String canonicalName;

    public String getCanonicalName() {
        return canonicalName;
    }

    public <T extends LicensingEntityCreate> T setCanonicalName(String canonicalName) {
        this.canonicalName = canonicalName;
        return (T) this;
    }
}
