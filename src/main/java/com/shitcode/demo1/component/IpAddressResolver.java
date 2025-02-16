package com.shitcode.demo1.component;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import jakarta.servlet.http.HttpServletRequest;

@Component
@Scope("application")
public class IpAddressResolver {
    /**
     * Retrieves the client's IP address from the request, handling proxies.
     *
     * @param request HttpServletRequest
     * @return IP address as a String
     */
    public String getClientIp(HttpServletRequest request) {
        String xfHeader = request.getHeader("X-Forwarded-For");
        if (xfHeader == null || xfHeader.isEmpty()) {
            return request.getRemoteAddr();
        }
        // Return the first IP if multiple are present (due to proxies)
        return xfHeader.split(",")[0].trim();
    }
}
