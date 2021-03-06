package no.edgeworks.kotlinbeer.config

import org.springframework.context.annotation.Configuration
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter

@Configuration
class WebSecurity : WebSecurityConfigurerAdapter() {
    override fun configure(http: HttpSecurity) {
        http.authorizeRequests()
            .antMatchers("/**").permitAll()
            .antMatchers("/h2-console/**").permitAll()

        http.csrf().disable()
        http.headers().frameOptions().disable()
    }
}