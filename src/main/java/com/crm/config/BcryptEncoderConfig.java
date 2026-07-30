package com.crm.config;

import org.springframework.security.crypto.bcrypt.BCrypt;
// Remove @Component - let BcryptAppConfig handle bean creation

public class BcryptEncoderConfig {

    public String encode(CharSequence rawPassword) {
        return BCrypt.hashpw(rawPassword.toString(), BCrypt.gensalt());
    }

    public boolean matches(CharSequence rawPassword, String encodedPassword) {
        return BCrypt.checkpw(rawPassword.toString(), encodedPassword);
    }
}