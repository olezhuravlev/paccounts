package org.paccounts.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.Locale;

@ConfigurationProperties(prefix = "application")
public record AppProps(
        Locale defaultLocale,
        String localeParameterName,
        String defaultEncoding,
        String messagesBasename,
        String localeResolverCookieName) {
}
