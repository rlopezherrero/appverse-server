/*
 Copyright (c) 2012 GFT Appverse, S.L., Sociedad Unipersonal.

 This Source Code Form is subject to the terms of the Appverse Public License 
 Version 2.0 (“APL v2.0”). If a copy of the APL was not distributed with this 
 file, You can obtain one at http://www.appverse.mobi/licenses/apl_v2.0.pdf. [^]

 Redistribution and use in source and binary forms, with or without modification, 
 are permitted provided that the conditions of the AppVerse Public License v2.0 
 are met.

 THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 DISCLAIMED. EXCEPT IN CASE OF WILLFUL MISCONDUCT OR GROSS NEGLIGENCE, IN NO EVENT
 SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT,
 INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
 PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY,
 WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT(INCLUDING NEGLIGENCE OR OTHERWISE) 
 ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE 
 POSSIBILITY OF SUCH DAMAGE.
 */
package org.appverse.web.framework.backend.security.oauth2.jwt;

import org.appverse.web.framework.backend.security.oauth2.authserver.configuration.jwtstore.AuthorizationServerWithJWTStoreConfigurerAdapter;
import org.appverse.web.framework.backend.security.oauth2.resourceserver.configuration.jwtstore.ResourceServerWithJWTStoreConfigurerAdapter;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.authentication.configurers.GlobalAuthenticationConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.configurers.ClientDetailsServiceConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableAuthorizationServer;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;

@Configuration
@EnableAutoConfiguration()
public class ApplicationJwt {

	public static void main(String[] args) {
		SpringApplication.run(ApplicationJwt.class, args);
	}
	
	@Configuration
	protected static class AuthenticationManagerCustomizer extends
			GlobalAuthenticationConfigurerAdapter {

		@Override
		public void init(AuthenticationManagerBuilder auth) throws Exception {
			auth.inMemoryAuthentication().withUser("user").password("password").roles("USER");
		}
	}		

	@Configuration
	@EnableResourceServer
	public static class ResourceServerConfig extends ResourceServerWithJWTStoreConfigurerAdapter{
	}
	
	@Configuration
	@EnableAuthorizationServer	
	public static class AuthorizationServerConfig extends AuthorizationServerWithJWTStoreConfigurerAdapter{
		
		@Override
		public void configure(ClientDetailsServiceConfigurer clients)
				throws Exception {
			clients.jdbc(dataSource)
			.passwordEncoder(passwordEncoder)
			.withClient("test-client")
			.authorizedGrantTypes("password", "authorization_code",
					"refresh_token", "implicit")
					.authorities("ROLE_CLIENT", "ROLE_TRUSTED_CLIENT")
					.scopes("read", "write", "trust")
					.resourceIds("oauth2-resource")
					.accessTokenValiditySeconds(60)
			.and()
			.withClient("test-client-autoapprove")
			.authorizedGrantTypes("password", "authorization_code",
					"refresh_token", "implicit")
					.authorities("ROLE_CLIENT", "ROLE_TRUSTED_CLIENT")
					.scopes("read", "write", "trust")
					.resourceIds("oauth2-resource")
					.accessTokenValiditySeconds(60)
					.autoApprove(true)
			.and()
			.withClient("test-client-auth-code-autoapprove")
			.authorizedGrantTypes("authorization_code")
					.secret("our-secret")
					.authorities("ROLE_CLIENT", "ROLE_TRUSTED_CLIENT")
					.scopes("trust")					
					.resourceIds("oauth2-resource")
					.accessTokenValiditySeconds(60)
					.autoApprove(true)
			.and()
			.withClient("test-client-auth-code-autoapprove-for-tests")
			.authorizedGrantTypes("authorization_code","refresh_token")
					.secret("our-secret")
					.authorities("ROLE_CLIENT", "ROLE_TRUSTED_CLIENT")
					.scopes("trust")					
					.resourceIds("oauth2-resource")
					.accessTokenValiditySeconds(4)
					.autoApprove(true);								
		}
	}	
}
