package com.shitcode.demo1.service.impl;

import java.time.Instant;
import java.util.Optional;
import java.util.function.Supplier;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.NonNull;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import com.shitcode.demo1.annotation.logging.LogCollector;
import com.shitcode.demo1.component.IpAddressResolver;
import com.shitcode.demo1.dto.ResponseDTO;
import com.shitcode.demo1.helper.DatetimeFormat;
import com.shitcode.demo1.helper.DatetimeFormat.Format;
import com.shitcode.demo1.helper.DatetimeFormat.TimeConversionMode;
import com.shitcode.demo1.service.RateLimiterService;
import com.shitcode.demo1.service.ResponseService;
import com.shitcode.demo1.utils.LoggingModel;
import com.shitcode.demo1.utils.RateLimiterPlan;

import io.github.bucket4j.Bucket;
import jakarta.servlet.http.HttpServletRequest;

@Service
@LogCollector(loggingModel = LoggingModel.SERVICE)
public class ResponseServiceImpl implements ResponseService {

        private final RateLimiterService rateLimiterService;
        private final IpAddressResolver ipAddressResolver;

        public ResponseServiceImpl(RateLimiterService rateLimiterService, IpAddressResolver ipAddressResolver) {
                this.rateLimiterService = rateLimiterService;
                this.ipAddressResolver = ipAddressResolver;
        }

        @Override
        public ResponseEntity<ResponseDTO> mapping(@NonNull Supplier<ResponseEntity<?>> service,
                        @NonNull RateLimiterPlan plan) {
                // ! Start to record execution time
                long startTime = System.currentTimeMillis();

                String requestTime = DatetimeFormat.format(Instant.now().getEpochSecond(), TimeConversionMode.INSTANT,
                                Format.FORMAL);

                // * Path Request
                HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder
                                .currentRequestAttributes()).getRequest();
                String path = request.getRequestURI();

                // * Requester
                String requester = getRequester();

                // * Ip Address
                String ipAddress = ipAddressResolver.getClientIp(request);
                // Determine the RateLimiterPlan from the annotation
                Bucket bucket = rateLimiterService.resolveBucket(ipAddress, plan);

                // ? README: Only Decrease Remaining Buckets Only If Calling Methods

                // * If The Rate Limiter Does Not Allow Another Token, Build A "Rate Limit
                // * Exceeded" Response.
                boolean isBucketConsumed = bucket.tryConsume(1);
                long remainingToken = bucket.getAvailableTokens();
                long capacity = plan.getLimit().getCapacity();

                // * Create A Common Builder With The Shared Values
                ResponseDTO.ResponseDTOBuilder builder = ResponseDTO.builder()
                                .path(path)
                                .requestTime(requestTime)
                                .requester(requester)
                                .rateLimits(ResponseDTO.RateLimits.builder().remaining(remainingToken).total(capacity)
                                                .resetAfter(getFullRefillTime(bucket, plan)).build());

                // +--------------------------------+
                // * Start Proceeding Controller Code.
                // +--------------------------------+
                ResponseEntity<?> result = service.get();
                // +--------------------------------+
                // * End Proceed Controller Code.
                // +--------------------------------+

                // ! End To Record Execution Time
                long processingTime = System.currentTimeMillis() - startTime;

                if (!isBucketConsumed) {

                        return new ResponseEntity<ResponseDTO>(
                                        builder
                                                        .data("{exception.rate-limit.exceed}")
                                                        .transper(ResponseDTO.Transper.builder()
                                                                        .processingTimeMs(processingTime)
                                                                        .statusCode(HttpStatus.TOO_MANY_REQUESTS.value())
                                                                        .build())
                                                        .build(),
                                        result.getHeaders(),
                                        HttpStatus.TOO_MANY_REQUESTS);
                }
                return new ResponseEntity<ResponseDTO>(
                                builder
                                                .data(result.getBody())
                                                .transper(ResponseDTO.Transper.builder()
                                                                .processingTimeMs(processingTime)
                                                                .statusCode(result.getStatusCode().value())
                                                                .build())
                                                .build(),
                                result.getHeaders(),
                                result.getStatusCode());
        }

        private String getFullRefillTime(Bucket bucket, RateLimiterPlan plan) {
                long missingTokens = plan.getLimit().getCapacity() - bucket.getAvailableTokens();
                return DatetimeFormat.format(Long.valueOf(plan.getLimit().getRefillPeriodNanos() * missingTokens),
                                TimeConversionMode.NANO, Format.SHORT);
        }

        private String getRequester() {
                Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
                String requester = Optional.ofNullable(authentication)
                                .filter(Authentication::isAuthenticated)
                                .map(Authentication::getName)
                                .map(name -> "anonymousUser".equals(name) ? "Anonymous User" : name)
                                .orElse("Anonymous User");
                return requester;
        }

}