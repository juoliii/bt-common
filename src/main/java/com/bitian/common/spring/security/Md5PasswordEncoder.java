package com.bitian.common.spring.security;

import com.bitian.common.util.MD5Util;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * @author admin
 */
public class Md5PasswordEncoder implements PasswordEncoder {

    @Override
    public String encode(CharSequence rawPassword) {
        return MD5Util.md52Hex((String)rawPassword);
    }

    @Override
    public boolean matches(CharSequence rawPassword, String encodedPassword) {
        return encodedPassword.equals(MD5Util.md52Hex((String)rawPassword));
    }
}
