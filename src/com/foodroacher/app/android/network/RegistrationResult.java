/**
 * 
 */
package com.foodroacher.app.android.network;

/**
 * @author vishal.gaurav@hotmail.com
 *
 */
public final class RegistrationResult {
    private String authKey;

    public RegistrationResult(String authKey) {
        super();
        this.authKey = authKey;
    }

    public String getAuthKey() {
        return authKey;
    }

    public void setAuthKey(String authKey) {
        this.authKey = authKey;
    }
    
}
