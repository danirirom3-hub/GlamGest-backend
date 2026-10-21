package com.glamgest.app.infrastructure.configuration;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

/** Attaches the safe display identity of a changed resource to the current HTTP request. */
@Component
public class AuditLogContext {

    public static final String RESOURCE_ID_ATTRIBUTE = AuditLogContext.class.getName() + ".resourceId";
    public static final String RESOURCE_NAME_ATTRIBUTE = AuditLogContext.class.getName() + ".resourceName";

    public void setResource(String resourceId, String resourceName) {
        var attributes = RequestContextHolder.getRequestAttributes();
        if (!(attributes instanceof ServletRequestAttributes servletAttributes)) return;

        HttpServletRequest request = servletAttributes.getRequest();
        setAttribute(request, RESOURCE_ID_ATTRIBUTE, resourceId, 100);
        setAttribute(request, RESOURCE_NAME_ATTRIBUTE, resourceName, 255);
    }

    private void setAttribute(HttpServletRequest request, String key, String value, int maxLength) {
        if (value == null || value.isBlank()) return;
        String normalized = value.trim();
        request.setAttribute(key, normalized.length() <= maxLength
                ? normalized
                : normalized.substring(0, maxLength));
    }
}
