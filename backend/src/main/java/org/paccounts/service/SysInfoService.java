package org.paccounts.service;


import org.springframework.stereotype.Service;

@Service
public class SysInfoService {

    public SysInfo getSysInfo() {

        String osName = System.getProperty("os.name");
        String timeZone = System.getProperty("user.timezone");
        String osArch = System.getProperty("os.arch");
        int processorsCount = Runtime.getRuntime().availableProcessors();

        return new SysInfo(osName, timeZone, osArch, processorsCount);
    }
}
