//package org.cellocad;
//
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
//import org.springframework.security.config.annotation.web.builders.HttpSecurity;
//import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
//import org.springframework.security.config.annotation.web.servlet.configuration.EnableWebMvcSecurity;
//import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
//
//import javax.sql.DataSource;
//
//@Configuration
//@EnableWebMvcSecurity
//public class WebSecurityConfig extends WebSecurityConfigurerAdapter {
//
//    @Autowired
//    private DataSource dataSource;
//
//    @Override
//    protected void configure(HttpSecurity http) throws Exception {
//        http
//                .authorizeRequests()
//                .antMatchers("/", "/home").permitAll()
//                .antMatchers("/test").authenticated()
//                .anyRequest().permitAll()
//                .and()
//                .httpBasic()
//                .and()
//                .csrf().disable();
////                .anyRequest().authenticated();
//
////        http
////                .authorizeRequests()
////                .antMatchers("/", "/home").permitAll()
////                .anyRequest().authenticated()
////                .and()
////                .formLogin()
////                .loginPage("/login")
////                .permitAll()
////                .and()
////                .logout()
////                .permitAll();
//    }
//
//    @Autowired
//    public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
//        auth.
////                .jdbcAuthentication()
////                .dataSource(dataSource)
////                .usersByUsernameQuery("select username from UserInformation where username = ?")
////                .passwordEncoder(new BCryptPasswordEncoder());
//
//                //.inMemoryAuthentication()
//                //.withUser("user").password("password").roles("USER");
//    }
//}