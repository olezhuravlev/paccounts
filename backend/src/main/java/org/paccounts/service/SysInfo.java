package org.paccounts.service;


import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class SysInfo {

    private final String osName;
    private final String timeZone;
    private final String osArch;
    private final int processorsCount;
}
