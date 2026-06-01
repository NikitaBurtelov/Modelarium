package org.modelarium.user.ratelimit.filter;

import io.github.bucket4j.ConsumptionProbe;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.modelarium.user.ratelimit.RateLimitService;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Arrays;

@Component
@RequiredArgsConstructor
public class RateLimitFilter extends OncePerRequestFilter {
    private final RateLimitService rateLimitService;

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {
        String path = request.getRequestURI();

        if (path.startsWith("/api/users/follow")) {
            var userId = request.getHeader("userId");
            var policy = policyMapping(request);

            ConsumptionProbe probe = rateLimitService.tryConsume(
                    userId,
                    policy
            );

            if (!probe.isConsumed()) {
                response.setStatus(HttpStatus.TOO_MANY_REQUESTS.value());
                response.setContentType(MediaType.APPLICATION_JSON_VALUE);
                response.setHeader(
                        "Retry-After",
                        String.valueOf(probe.getNanosToWaitForRefill() / 1_000_000)
                );

                response.getWriter().write("""
                        {
                          "message": "Too many follow requests"
                        }
                        """);

                return;
            }

            response.setHeader(
                    "X-Rate-Limit-Remaining",
                    String.valueOf(probe.getRemainingTokens())
            );
        }

        filterChain.doFilter(request, response);
    }

    private RateLimitPolicy policyMapping(HttpServletRequest request) {
        String path = request.getRequestURI();

        return Arrays.stream(RateLimitPolicy.values())
                .filter(route ->
                        path.startsWith(route.getPath())
                )
                .findFirst()
                .orElse(RateLimitPolicy.DEFAULT);
    }
}
