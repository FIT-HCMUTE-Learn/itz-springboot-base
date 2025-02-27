package com.base.auth.utils;

import com.base.auth.jwt.UserBaseJwt;
import org.springframework.security.core.context.SecurityContextHolder;

public class SecurityUtils {

    public static Long getAccountId() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (principal instanceof String) {
            UserBaseJwt userBaseJwt = UserBaseJwt.decode((String) principal);
            if (userBaseJwt != null && userBaseJwt.getAccountId() != null) {
                return userBaseJwt.getAccountId();
            }
        }
        return null;
    }
}
