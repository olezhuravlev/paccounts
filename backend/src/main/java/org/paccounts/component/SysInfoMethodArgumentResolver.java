package org.paccounts.component;

import org.paccounts.service.SysInfo;
import org.paccounts.service.SysInfoService;
import org.springframework.core.MethodParameter;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

@Component
public class SysInfoMethodArgumentResolver implements HandlerMethodArgumentResolver {

    private final SysInfoService sysInfoService;

    public SysInfoMethodArgumentResolver(SysInfoService sysInfoService) {
        this.sysInfoService = sysInfoService;
    }

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return parameter.getParameterType().equals(SysInfo.class);
    }

    @Override
    public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer, NativeWebRequest webRequest, WebDataBinderFactory binderFactory) {
        return sysInfoService.getSysInfo();
    }
}
