package org.hrds.rdupm.nexus.client.nexus.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

@Configuration
public class CustomWebSecurityConfigurerAdapter extends WebSecurityConfigurerAdapter {

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        // 禁用 security basic 验证
        http.httpBasic().disable();
        // 禁用 csrf
        http.csrf().disable();
    }
}