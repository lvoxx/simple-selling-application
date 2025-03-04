package com.shitcode.demo1.service;

import com.ip2location.IPResult;

public interface Ip2LocationService {
    IPResult getLocation(String ipAddress);
}