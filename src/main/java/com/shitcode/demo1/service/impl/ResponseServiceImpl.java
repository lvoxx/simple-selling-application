package com.shitcode.demo1.service.impl;

import java.util.function.Supplier;

import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import com.shitcode.demo1.annotation.logging.LogCollector;
import com.shitcode.demo1.component.IpAddressResolver;
import com.shitcode.demo1.dto.ResponseDTO;
import com.shitcode.demo1.exception.model.ErrorModel;
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
        public ResponseEntity<?> mapping(@NonNull Supplier<ResponseEntity<?>> service,
                        @NonNull RateLimiterPlan plan) {
                // * Ip Address
                HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder
                                .currentRequestAttributes()).getRequest();
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
                                .rateLimits(ResponseDTO.RateLimits.builder().remaining(remainingToken).total(capacity)
                                                .resetAfter(getFullRefillTime(bucket, plan)).build());

                // +--------------------------------+
                // * Start Proceeding Controller Code.
                // +--------------------------------+
                ResponseEntity<?> result = service.get();
                // +--------------------------------+
                // * End Proceed Controller Code.
                // +--------------------------------+

                if (!isBucketConsumed) {
                        return new ResponseEntity<ErrorModel>(
                                        ErrorModel.of(HttpStatus.TOO_MANY_REQUESTS, "{exception.rate-limit.exceed}",
                                                        null),
                                        HttpStatusCode.valueOf(HttpStatus.TOO_MANY_REQUESTS.value()));
                }
                return new ResponseEntity<ResponseDTO>(
                                builder
                                                .data(result.getBody())
                                                .status(result.getStatusCode())
                                                .build(),
                                result.getHeaders(),
                                result.getStatusCode());
        }

        private String getFullRefillTime(Bucket bucket, RateLimiterPlan plan) {
                long missingTokens = plan.getLimit().getCapacity() - bucket.getAvailableTokens();
                return DatetimeFormat.format(Long.valueOf(plan.getLimit().getRefillPeriodNanos() * missingTokens),
                                TimeConversionMode.NANO, Format.SHORT);
        }

}