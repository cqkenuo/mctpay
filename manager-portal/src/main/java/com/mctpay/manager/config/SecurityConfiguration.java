package com.mctpay.manager.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mctpay.common.base.model.ResponseData;
import com.mctpay.common.config.MyBCryptPasswordEncoder;
import com.mctpay.manager.model.dto.system.LoginedUserDTO;
import com.mctpay.manager.model.entity.system.RoleEntity;
import com.mctpay.manager.model.entity.system.UserEntity;
import com.mctpay.manager.service.system.impl.UserServiceImpl;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.InsufficientAuthenticationException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.CorsUtils;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

import static com.mctpay.common.constants.ErrorCode.*;

/**
 * @Author: guodongwei
 * @Description: 安全配置类
 * @Date: 2020/2/25 17:26
 */
@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class SecurityConfiguration extends WebSecurityConfigurerAdapter {

    @Autowired
    private UserServiceImpl userService;

    @Autowired
    @Qualifier("myBCryptPasswordEncoder")
    private MyBCryptPasswordEncoder myBCryptPasswordEncoder;

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userService).passwordEncoder(myBCryptPasswordEncoder);
    }

    @Override
    public void configure(WebSecurity web) throws Exception {
        web.ignoring().antMatchers("/favicon.ico");
    }

    @Override
    protected void configure(HttpSecurity httpSecurity) throws Exception {
        httpSecurity.cors().and().authorizeRequests()
                .requestMatchers(CorsUtils::isPreFlightRequest).permitAll()
                .antMatchers("/login.html", "/login", "/logout", "/doc.html").permitAll().anyRequest().authenticated()
                .and()
                // 支持表单提交
                .formLogin()
                // 自定义登录页面,前后端分离时。重定向交给前端
                // .loginPage("/login.html").permitAll()
                // 自定义错误
                .failureHandler((req, resp, exception) -> {
                    resp.setContentType("application/json;charset=utf-8");
                    PrintWriter out = resp.getWriter();
                    ResponseData<Object> responseData = new ResponseData<>();
                    if (exception instanceof LockedException) {
                        responseData.fail(ACCOUNT_FROZEN.getCode(), ACCOUNT_FROZEN.getMessage());
                    } else if (exception instanceof DisabledException) {
                        responseData.fail(USERNAME_OR_PASSWORD_ERR.getCode(), USERNAME_OR_PASSWORD_ERR.getMessage());
                    } else if (exception instanceof BadCredentialsException) {
                        responseData.fail(USERNAME_OR_PASSWORD_ERR.getCode(), USERNAME_OR_PASSWORD_ERR.getMessage());
                    }
                    out.write(new ObjectMapper().writeValueAsString(responseData));
                    out.flush();
                    out.close();
                })
                .usernameParameter("username")
                .passwordParameter("password")
                // 提交action  也就是form表单中的action  login会调用security的登录不用自己实现
                .loginProcessingUrl("/login")
                // 登录成功页面
                .successHandler((HttpServletRequest req, HttpServletResponse resp, Authentication authentication) -> {
                    resp.setContentType("application/json;charset=utf-8");
                    PrintWriter out = resp.getWriter();
                    UserEntity userEntity = (UserEntity) authentication.getPrincipal();
                    // 获取权限列表

                    ResponseData<LoginedUserDTO> responseData = new ResponseData<>();
                    // 抽取部分数据进行返回
                    LoginedUserDTO loginedUserDTO = new LoginedUserDTO();
                    loginedUserDTO.setId(userEntity.getId());
                    loginedUserDTO.setNickname(userEntity.getNickname());
                    loginedUserDTO.setStatus(userEntity.getStatus());
                    // 获取用户权限列表
                    List<RoleEntity> roles = userEntity.getRoles();
                    // 传递权限参数
                    loginedUserDTO.setRoles(new ArrayList<String>());
                    for (RoleEntity role : roles) {
                        loginedUserDTO.getRoles().add(role.getRoleName());
                    }
                    String s = new ObjectMapper().writeValueAsString(responseData.success(loginedUserDTO));
                    out.write(s);
                    out.flush();
                    out.close();
                })
                .and()
                .logout()
                .logoutSuccessHandler(new LogoutSuccessHandler() {
                    @Override
                    public void onLogoutSuccess(HttpServletRequest req, HttpServletResponse resp, Authentication authentication) throws IOException, ServletException {
                        resp.setContentType("application/json;charset=utf-8");
                        PrintWriter out = resp.getWriter();
                        out.write(new ObjectMapper().writeValueAsString(new ResponseData<>().success(null)));
                        out.flush();
                        out.close();
                    }
                })
                .permitAll()
                .and().csrf().disable().exceptionHandling()
                // 未认证进行错误返回
                .authenticationEntryPoint((req, resp, authException) -> {
                    resp.setContentType("application/json;charset=utf-8");
                    resp.setStatus(401);
                    PrintWriter out = resp.getWriter();
                    ResponseData<Object> responseData = new ResponseData<>();
                    if (authException instanceof InsufficientAuthenticationException) {
                        responseData.fail(NON_AUTHENTICATION.getCode(), NON_AUTHENTICATION.getMessage());
                        out.write(new ObjectMapper().writeValueAsString(responseData));
                    } else {
                        out.write(new ObjectMapper().writeValueAsString(responseData.success(null)));
                    }
                    out.flush();
                    out.close();
                });
    }

}
