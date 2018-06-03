package com.security.browser;

import com.security.core.authentication.AbstractChannelSecurityConfig;
import com.security.core.authentication.mobile.SmsCodeAuthenticationSecurityConfig;
import com.security.core.code.ValidateCodeSecurityConfig;
import com.security.core.properties.SecurityProperties;
import org.apache.tomcat.jdbc.pool.DataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.rememberme.JdbcTokenRepositoryImpl;
import org.springframework.security.web.authentication.rememberme.PersistentTokenRepository;
import org.springframework.social.security.SpringSocialConfigurer;

/**
 * Created by Chris on 2018/4/11.
 */
@EnableWebSecurity
public class BrowserSecurityConfig extends AbstractChannelSecurityConfig {
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Autowired
    private AuthenticationSuccessHandler loginSuccessHandler;

    @Autowired
    private AuthenticationFailureHandler loginFailedHandler;

    @Autowired
    private SecurityProperties sp;

    @Autowired
    private DataSource dataSource;

    @Autowired
    private UserDetailsService myUserDetailService;

    @Autowired
    private SpringSocialConfigurer mySpringSocialConfigurer;

    @Autowired
    private SmsCodeAuthenticationSecurityConfig smsCodeAuthenticationSecurityConfig;

    @Autowired
    private ValidateCodeSecurityConfig validateCodeSecurityConfig;

    @Bean
    public PersistentTokenRepository persistentTokenRepository() {
        JdbcTokenRepositoryImpl jdbcTokenRepository = new JdbcTokenRepositoryImpl();
        jdbcTokenRepository.setDataSource(dataSource);
        // jdbcTokenRepository.setCreateTableOnStartup(true);
        return jdbcTokenRepository;
    }



    @Override
    protected void configure(HttpSecurity http) throws Exception {
        /*ImageCodeValidFilter imageCodeValidFilter = new ImageCodeValidFilter();
        imageCodeValidFilter.setSecurityProperties(sp);
        imageCodeValidFilter.setAuthenticationFailHandler(loginFailedHandler);
        imageCodeValidFilter.afterPropertiesSet();

        SmsCodeValidFilter smsCodeValidFilter = new SmsCodeValidFilter();
        smsCodeValidFilter.setSecurityProperties(sp);
        smsCodeValidFilter.setAuthenticationFailHandler(loginFailedHandler);
        smsCodeValidFilter.afterPropertiesSet();*/
        applyPasswordAuthenticationConfig(http);
        http
                .apply(smsCodeAuthenticationSecurityConfig).and()
                .apply(validateCodeSecurityConfig).and()
                /*.addFilterBefore(smsCodeValidFilter, UsernamePasswordAuthenticationFilter.class)
                .addFilterBefore(imageCodeValidFilter, UsernamePasswordAuthenticationFilter.class)*/
                .rememberMe().tokenRepository(persistentTokenRepository()).userDetailsService(myUserDetailService).tokenValiditySeconds(sp.getBrowser().getRememberMeTime())
                .and().authorizeRequests().antMatchers(sp.getBrowser().getLoginPage(), "/code/*").permitAll().anyRequest().authenticated()
        .and().csrf().disable();
    }
}
