package com.spring.web.user;

import javax.sql.DataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.method.configuration.GlobalMethodSecurityConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Configuration
public class WebSecurityConfig 
{
	@Autowired
	DataSource source;
	
   @Autowired
    DataSource dataSource;    // JDBC Authentication에 필요함

   @Bean
   	  public BCryptPasswordEncoder  passwordEncoder() {
      BCryptPasswordEncoder enc = new BCryptPasswordEncoder(); // 60자로 암호화
       return enc;
   }

   //Enable jdbc authentication
   @Autowired
   public void configAuthentication(AuthenticationManagerBuilder auth) throws Exception {
      log.info("데이터소스 설정");
      auth.jdbcAuthentication().dataSource(dataSource)
      .usersByUsernameQuery(
               "SELECT userid,password, enabled FROM TRADI_USER WHERE userid=?")
      .authoritiesByUsernameQuery(
    		 "SELECT userid,authority FROM TRADI_AUTHORITIES WHERE userid=?" )
      ;
   }

    @Bean
    public WebSecurityCustomizer webSecurityCustomizer() {
       return (web) -> web.ignoring().requestMatchers("/resources/**", "/ignore2");
    }
    
   @Bean
   public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
      log.info("접근제한 설정");
      return http.authorizeHttpRequests()/* 권한에 따른 인가(Authorization) */
            .requestMatchers("/user/customer/login","user/customer/join","/doLogin","/goLogin","/drinks").permitAll()
               //.antMatchers("/admin/*").hasAnyRole("ADMIN")
               .requestMatchers("/user/customer/myinfo").hasAnyRole("USER")
               .requestMatchers("/customer/myorder").hasAnyRole("USER","ADMIN")
               .requestMatchers("/drinks/addcart").hasAnyRole("USER","ADMIN")
               .requestMatchers("/drinks/cartlist").hasAnyRole("USER","ADMIN")
               .requestMatchers("/drinks/buynow").hasAnyRole("USER","ADMIN")
               .requestMatchers("/vendor/*").hasAnyRole("VENDOR")
            //.anyRequest().authenticated()  // 위의 설정 이외의 모든 요청은 인증을 거쳐야 한다
               .anyRequest().permitAll()        // 위의 설정 이외의 모든 요청은 인증 요구하지 않음
            .and()
            //.csrf().csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse()) // 서버쪽에서 클라이언트로 보내는거라 쿠키를 통해서 보낸다.라는표현
            //.csrf().disable()    //csrf 기능을 사용하지 않을 때
            //.csrf().ignoringAntMatchers("/logout") //요청시 'POST' not supported 에러 방지
            //.ignoringAntMatchers("/sec/loginForm") 첫페이지 접속시.(사기방지 무효화) 
            //.ignoringAntMatchers("/doLogin")
            .csrf().disable()
            //.ignoringAntMatchers("/sec")
            //.ignoringAntMatchers("/sec/hello")
            //.ignoringAntMatchers("/sec/loginForm")           

            //.and()
            .formLogin().loginPage("/user/customer/login")   // 지정된 위치에 로그인 폼이 준비되어야 함
               .loginProcessingUrl("/doLogin")
               .defaultSuccessUrl("/user/loginsuccess")
               // 컨트롤러 메소드 불필요, 폼 action과 일치해야 함 -> form의 action에서 이 url로 보낸다.그래야 로그인 처리를 해준다.
               .failureUrl("/user/customer/login")      // 권한이 필요한 페이지 접속시 다시 로그인 폼으로
               //.failureForwardUrl("/login?error=Y")  //실패시 다른 곳으로 forward
               .usernameParameter("uid")  // 로그인 폼에서 이용자 ID 필드 이름, 디폴트는 username
               .passwordParameter("pwd")  // 로그인 폼에서 이용자 암호 필트 이름, 디폴트는 password
               .permitAll()        
              
               
             .and()   // 디폴트 로그아웃 URL = /logout
             .logout().logoutRequestMatcher(new AntPathRequestMatcher("/logout")) //로그아웃 요청시 URL
             .logoutSuccessUrl("/sec/loginForm?logout=T")
             .invalidateHttpSession(true) 
             .deleteCookies("JSESSIONID")
             .permitAll()
             
             .and()
             .exceptionHandling().accessDeniedPage("/sec/denied")
             .and().build();
   }

   /*  아래의 내용은 In-Memory Authentication에 사용되므로 JDBC Authentication에는 불필요 (DB가 대신한다.)
   @Autowired
    public void configureGlobal(AuthenticationManagerBuilder authenticationMgr) throws Exception {
        authenticationMgr.inMemoryAuthentication().withUser("employee").password("$2a$10$MZ2ANCUXIj5mrAVbytojruvzrPv9B3v9CXh8qI9qP13kU8E.mq7yO")
            .authorities("ROLE_USER").and().withUser("imadmin").password("$2a$10$FA8kEOhdRwE7OOxnsJXx0uYQGKaS8nsHzOXuqYCFggtwOSGRCwbcK")
            .authorities("ROLE_USER", "ROLE_ADMIN").and().withUser("guest").password("$2a$10$ABxeHaOiDbdnLaWLPZuAVuPzU3rpZ30fl3IKfNXybkOG2uZM4fCPq")
            .authorities("ROLE_GUEST");
    }*/
}

