/*
 * Copyright 2012-2015 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.appverse.web.framework.backend.frontfacade.rest.autoconfigure;

import javax.annotation.PostConstruct;

import org.appverse.web.framework.backend.frontfacade.rest.authentication.basic.services.BasicAuthenticationServiceImpl;
import org.appverse.web.framework.backend.frontfacade.rest.handler.JerseyExceptionHandler;
import org.appverse.web.framework.backend.frontfacade.rest.remotelog.services.presentation.RemoteLogServiceFacadeImpl;
import org.glassfish.jersey.jackson.JacksonFeature;
import org.glassfish.jersey.server.ResourceConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.jersey.JerseyAutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

/**
 * {@link EnableAutoConfiguration Auto-configuration} for Front Facade module when Jersey is present.
 */
@Component
@Configuration
@ConditionalOnClass(name = {
		"org.glassfish.jersey.server.spring.SpringComponentProvider",
		"javax.servlet.ServletRegistration", "org.glassfish.jersey.server.ResourceConfig" })
//@ConditionalOnBean(type = "org.glassfish.jersey.server.ResourceConfig")
@EnableConfigurationProperties(FrontFacadeRestAutoconfigurationProperties.class)
@AutoConfigureBefore(JerseyAutoConfiguration.class)
public class FrontFacadeRestAutoConfiguration extends ResourceConfig {
	
	@Autowired 
	FrontFacadeRestAutoconfigurationProperties frontFacadeRestAutoconfigurationProperties;

	public FrontFacadeRestAutoConfiguration() {
	}
	
	/*
	 * Init method requires to be annotated with {@link PostConstruct} as we need properties to be injected 
	 */
	@PostConstruct
	public void init() {
		// Register the modules endpoints if enabled and JacksonFeature	
		if (frontFacadeRestAutoconfigurationProperties.isRemoteLogEndpointEnabled()){
			register(RemoteLogServiceFacadeImpl.class);
		}
		if (frontFacadeRestAutoconfigurationProperties.isBasicAuthenticationEndpointEnabled()){
			register(BasicAuthenticationServiceImpl.class);			
		}
		if( frontFacadeRestAutoconfigurationProperties.isJerseyExceptionHandlerEnabled()) {
			register(JerseyExceptionHandler.class);
		}
		register(JacksonFeature.class);
	}

}
