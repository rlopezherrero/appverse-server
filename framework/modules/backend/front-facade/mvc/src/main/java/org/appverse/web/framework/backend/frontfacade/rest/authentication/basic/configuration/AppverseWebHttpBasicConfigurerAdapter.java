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
package org.appverse.web.framework.backend.frontfacade.rest.authentication.basic.configuration;

import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;

import org.appverse.web.framework.backend.frontfacade.rest.authentication.handlers.SimpleNoRedirectLogoutSucessHandler;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.web.util.matcher.RegexRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;

public abstract class AppverseWebHttpBasicConfigurerAdapter extends
		WebSecurityConfigurerAdapter {
	
	@Value("${security.enable-csrf:true}")
	protected boolean securityEnableCsrf;

	@Value("${appverse.frontfacade.rest.api.basepath:/api}")
	protected String baseApiPath;

	@Value("${appverse.frontfacade.rest.basicAuthenticationEndpoint.path:/sec/login}")
	protected String basicAuthenticationEndpointPath;
	
	@Value("${appverse.frontfacade.rest.basicAuthenticationLogoutEndpoint.path:/sec/logout}")
	protected String basicAuthenticationLogoutEndpointPath;

	@Value("${appverse.frontfacade.rest.simpleAuthenticationEndpoint.path:/sec/simplelogin}")
	protected String simpleAuthenticationEndpointPath;

	/**
	 * CSRF is enabled by default. The authentication endopoints (login) does
	 * not check CSRF (as it is the first request - you will not have a token
	 * yet). After the login you will be able to retrieve the CSRF token from
	 * the response header and use it in the next requests.
	 *
	 * Take into account that CSRF using HttpSessionCsrfRepository (default)
	 * always implies to have a technical session (this does not mean you need
	 * to make your services stateful. It is very well explained in Spring
	 * documentation: Next paragraf if taken from:
	 * http://docs.spring.io/spring-security
	 * /site/docs/current/reference/htmlsingle/#csrf (Section "Loggin In": "In
	 * order to protect against forging log in requests the log in form should
	 * be protected against CSRF attacks too. Since the CsrfToken is stored in
	 * HttpSession, this means an HttpSession will be created as soon as
	 * CsrfToken token attribute is accessed. While this sounds bad in a RESTful
	 * / stateless architecture the reality is that state is necessary to
	 * implement practical security. Without state, we have nothing we can do if
	 * a token is compromised. Practically speaking, the CSRF token is quite
	 * small in size and should have a negligible impact on our architecture."
	 */
	@Override
	protected void configure(HttpSecurity http) throws Exception {
		if (securityEnableCsrf){
			http.csrf()
			.requireCsrfProtectionMatcher(new CsrfSecurityRequestMatcher());
		}
		else http.csrf().disable();
		
		http.authorizeRequests()
			.antMatchers(baseApiPath + basicAuthenticationEndpointPath).permitAll()
			.antMatchers(baseApiPath + simpleAuthenticationEndpointPath).permitAll()
			.antMatchers(baseApiPath + "/**").fullyAuthenticated()
			.antMatchers("/").permitAll()
		.and()
		.logout()
			.logoutUrl(basicAuthenticationLogoutEndpointPath)
			.logoutSuccessHandler(new SimpleNoRedirectLogoutSucessHandler())
			.permitAll().and()		
		.httpBasic().and()
		.sessionManagement().sessionFixation().newSession();
	}

	/**
	 * Custom RequestMatcher that excludes login endpoints from CSRF protection.
	 * Authentication endpoints will trigger CSRF token generation that will be
	 * available for the next requests.
	 */
	private final class CsrfSecurityRequestMatcher implements RequestMatcher {
		private Pattern allowedMethods = Pattern
				.compile("^(GET|HEAD|TRACE|OPTIONS)$");
		private RegexRequestMatcher unprotectedMatcher = new RegexRequestMatcher(
				baseApiPath + basicAuthenticationEndpointPath + "|"
						+ baseApiPath + simpleAuthenticationEndpointPath, null);

		@Override
		public boolean matches(HttpServletRequest request) {
			if (allowedMethods.matcher(request.getMethod()).matches()) {
				return false;
			}

			return !unprotectedMatcher.matches(request);
		}
	}

}