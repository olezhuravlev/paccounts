package org.paccounts.controller.rest;

import org.paccounts.service.SysInfo;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;

@RestController
public class SysInfoRestController {

    public static final String SYSINFO_URL = "/sysinfo";
    public static final String SYSINFO_KEY = "sysinfo";

    @PostMapping(SYSINFO_URL)
    public ResponseEntity<Object> getServerSysInfo(SysInfo sysInfo) {

        HashMap<String, Object> result = new HashMap<>();
        result.put(SYSINFO_KEY, sysInfo);

        return ResponseEntity.ok(result);
    }
}
