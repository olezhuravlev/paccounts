package org.paccounts.controller.rest;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.paccounts.component.SysInfoMethodArgumentResolver;
import org.paccounts.config.ApplicationConfig;
import org.paccounts.config.WebConfig;
import org.paccounts.service.SysInfo;
import org.paccounts.service.SysInfoService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;

import java.util.HashMap;

import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@DisplayName("System info controller test")
@WebMvcTest(SysInfoRestController.class)
@ContextConfiguration(classes = {ApplicationConfig.class, WebConfig.class, SysInfoRestController.class, SysInfoMethodArgumentResolver.class})
class SysInfoRestControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private SysInfoService sysInfoService;

    @DisplayName("Get system info")
    @Test
    void getServerSystemInfo() throws Exception {

        SysInfo expected = new SysInfo("1", "2", "3", 4);
        given(sysInfoService.getSysInfo()).willReturn(expected);

        HashMap<String, Object> result = new HashMap<>();
        result.put(SysInfoRestController.SYSINFO_KEY, expected);

        String expectedJson = new ObjectMapper().writeValueAsString(result);
        this.mockMvc.perform(post(SysInfoRestController.SYSINFO_URL))
                .andExpect(status().isOk())
                .andExpect(content().json(expectedJson));
    }
}
