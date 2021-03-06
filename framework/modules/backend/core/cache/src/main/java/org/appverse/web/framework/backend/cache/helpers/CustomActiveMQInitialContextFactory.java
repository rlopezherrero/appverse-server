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
package org.appverse.web.framework.backend.cache.helpers;

import java.net.URISyntaxException;
import java.util.Hashtable;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.naming.Context;
import javax.naming.NamingException;

import net.sf.ehcache.distribution.jms.JMSUtil;

import org.apache.activemq.jndi.ActiveMQInitialContextFactory;

public class CustomActiveMQInitialContextFactory extends
		ActiveMQInitialContextFactory {

	@Override
	public Context getInitialContext(
			@SuppressWarnings("rawtypes") Hashtable environment)
			throws NamingException {
		Map<String, Object> data = new ConcurrentHashMap<String, Object>();
		String topicFactoryBindingName = (String) environment
				.get(JMSUtil.TOPIC_CONNECTION_FACTORY_BINDING_NAME);
		try {
			data.put(topicFactoryBindingName,
					createConnectionFactory(environment));
		} catch (URISyntaxException e) {
			throw new NamingException("Error initialisating ConnectionFactory"
					+ " with message " + e.getMessage());
		}
		String topicBindingName = (String) environment
				.get(JMSUtil.REPLICATION_TOPIC_BINDING_NAME);
		data.put(topicBindingName, createTopic(topicBindingName));

		String queueFactoryBindingName = (String) environment
				.get(JMSUtil.GET_QUEUE_CONNECTION_FACTORY_BINDING_NAME);
		try {
			data.put(queueFactoryBindingName,
					createConnectionFactory(environment));
		} catch (URISyntaxException e) {
			throw new NamingException("Error initialisating ConnectionFactory"
					+ " with message " + e.getMessage());
		}
		String queueBindingName = (String) environment
				.get(JMSUtil.GET_QUEUE_BINDING_NAME);
		data.put(queueBindingName, createQueue(queueBindingName));

		return createContext(environment, data);

	}
}
