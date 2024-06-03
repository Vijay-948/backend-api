package backend.real_estate.backendapi.config;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.filter.AbstractRequestLoggingFilter;

@Slf4j
public class LogFilter extends AbstractRequestLoggingFilter {
    @Override
    protected void beforeRequest(HttpServletRequest request, String message) {
        log.info("Before Request: " + message);
    }

    @Override
    protected void afterRequest(HttpServletRequest request, String message) {
        log.info("After Request: " + message);
    }
}
