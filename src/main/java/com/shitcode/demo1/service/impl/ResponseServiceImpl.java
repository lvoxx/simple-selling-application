package com.shitcode.demo1.service.impl;

import java.util.Locale;

import org.springframework.context.MessageSource;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;
import org.springframework.util.function.ThrowingSupplier;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import com.shitcode.demo1.annotation.logging.LogCollector;
import com.shitcode.demo1.component.IpAddressResolver;
import com.shitcode.demo1.dto.ResponseDTO;
import com.shitcode.demo1.exception.model.ExceededRateLimterException;
import com.shitcode.demo1.helper.DatetimeFormat;
import com.shitcode.demo1.helper.DatetimeFormat.Format;
import com.shitcode.demo1.helper.DatetimeFormat.TimeConversionMode;
import com.shitcode.demo1.service.RateLimiterService;
import com.shitcode.demo1.service.ResponseService;
import com.shitcode.demo1.utils.LoggingModel;
import com.shitcode.demo1.utils.RateLimiterPlan;

import io.github.bucket4j.Bucket;
import jakarta.servlet.http.HttpServletRequest;
import lombok.AllArgsConstructor;
import lombok.Data;

@Service
@LogCollector(loggingModel = LoggingModel.SERVICE)
public class ResponseServiceImpl implements ResponseService {

        private final RateLimiterService rateLimiterService;
        private final IpAddressResolver ipAddressResolver;
        private final MessageSource messageSource;

        public ResponseServiceImpl(RateLimiterService rateLimiterService, IpAddressResolver ipAddressResolver,
                        MessageSource messageSource) {
                this.rateLimiterService = rateLimiterService;
                this.ipAddressResolver = ipAddressResolver;
                this.messageSource = messageSource;
        }

        /**
         * @author lvoxx
         * @param service the service to execute and capture the response
         * @param plan    the rate limiter plan used to determine if the request is
         *                allowed
         * @return a ResponseEntity that contains the response from the service and the
         *         rate limits
         * @throws Exception if the service throws an exception
         */
        @Override
        public ResponseEntity<?> mapping(@NonNull ThrowingSupplier<ResponseEntity<?>> service,
                        @NonNull RateLimiterPlan plan) throws Exception {
                RateLimitResult res = resolveRateLimit(plan);
                ResponseDTO.ResponseDTOBuilder builder = ResponseDTO.builder()
                                .rateLimits(res.getRateLimits());

                if (!res.isBucketConsumed()) {
                        throw new ExceededRateLimterException(messageSource.getMessage("exception.rate-limit.exceed",
                                        new Object[] {}, Locale.getDefault()));

                        // HARDCODE, NOT RECOMMENDED: If you wanna to notify client with message, you
                        // can use this code instead of
                        // throw exception.
                        // return new ResponseEntity<ErrorModel>(
                        // ErrorModel.of(HttpStatus.TOO_MANY_REQUESTS, "{exception.rate-limit.exceed}",
                        // null),
                        // HttpStatusCode.valueOf(HttpStatus.TOO_MANY_REQUESTS.value()));
                }
                
                // +--------------------------------+
                // * Start Proceeding Controller Code.
                // +--------------------------------+
                ResponseEntity<?> response = service.get();
                // +--------------------------------+
                // * End Proceed Controller Code.
                // +--------------------------------+

                return ResponseEntity.status(response.getStatusCode())
                                .headers(response.getHeaders())
                                .body(builder.data(response.getBody())
                                                .status(response.getStatusCode())
                                                .build());
        }

        /**
         * @author lvoxx
         * @param service the service to execute and capture the response
         * @param plan    the rate limiter plan used to determine if the request is
         *                allowed
         * @return the response from the service
         * @throws Exception if the service throws an exception
         */
        @Override
        public <T> T execute(ThrowingSupplier<T> service, RateLimiterPlan plan) throws Exception {
                RateLimitResult res = resolveRateLimit(plan);

                if (!res.isBucketConsumed()) {
                        throw new ExceededRateLimterException(messageSource.getMessage("exception.rate-limit.exceed",
                                        new Object[] {}, Locale.getDefault()));
                }

                // +--------------------------------+
                // * Start Proceeding Controller Code.
                // +--------------------------------+
                T response = service.get();
                // +--------------------------------+
                // * End Proceed Controller Code.
                // +--------------------------------+

                return response;
        }

        private String getFullRefillTime(Bucket bucket, RateLimiterPlan plan) {
                long missingTokens = plan.getLimit().getCapacity() - bucket.getAvailableTokens();
                // Convert to seconds to avoid overflow
                long refillSeconds = (plan.getLimit().getRefillPeriodNanos() * missingTokens) / 1_000_000_000L;
                return DatetimeFormat.format(refillSeconds, TimeConversionMode.SECOND, Format.SHORT);
        }

        private RateLimitResult resolveRateLimit(RateLimiterPlan plan) {
                boolean isBucketConsumed = false;
                // * Ip Address
                HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder
                                .currentRequestAttributes()).getRequest();
                String ipAddress = ipAddressResolver.getClientIp(request);
                // Determine the RateLimiterPlan from the annotation
                Bucket bucket = rateLimiterService.resolveBucket(ipAddress, plan);

                // ? README: Only Decrease Remaining Buckets Only If Calling Methods

                // * If The Rate Limiter Does Not Allow Another Token, Build A "Rate Limit
                // * Exceeded" Response.
                isBucketConsumed = bucket.tryConsume(1);
                long remainingToken = bucket.getAvailableTokens();
                long capacity = plan.getLimit().getCapacity();

                // * Create A Common Builder With The Shared Values
                return RateLimitResult.of(isBucketConsumed,
                                ResponseDTO.RateLimits.builder().remaining(remainingToken).total(capacity)
                                                .resetAfter(getFullRefillTime(bucket, plan)).build());
        }

        @Data
        @AllArgsConstructor(staticName = "of")
        private static class RateLimitResult {
                private boolean isBucketConsumed;
                private ResponseDTO.RateLimits rateLimits;
        }
}