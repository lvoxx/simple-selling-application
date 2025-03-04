package com.shitcode.demo1.service.impl;

import java.time.LocalDateTime;

import org.springframework.stereotype.Service;

import com.ip2location.IP2Location;
import com.ip2location.IPResult;
import com.shitcode.demo1.service.Ip2LocationService;
import com.shitcode.demo1.utils.LogPrinter;

@Service
public class Ip2LocationServiceImpl implements Ip2LocationService {

    private final IP2Location ip2Location;

    public Ip2LocationServiceImpl(IP2Location ip2Location) {
        this.ip2Location = ip2Location;
    }

    @Override
    public IPResult getLocation(String ipAddress) {
        try {
            return ip2Location.IPQuery(ipAddress);
        } catch (Exception e) {
            LogPrinter.printServiceLog(LogPrinter.Type.ERROR, "Ip2LocationServiceImpl", "getLocation",
                    LocalDateTime.now().toString(),
                    e.getMessage());
            return null;
        }
    }

}
