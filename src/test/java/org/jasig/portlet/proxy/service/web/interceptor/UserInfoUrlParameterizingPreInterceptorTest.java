/**
 * Licensed to Jasig under one or more contributor license
 * agreements. See the NOTICE file distributed with this work
 * for additional information regarding copyright ownership.
 * Jasig licenses this file to you under the Apache License,
 * Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a
 * copy of the License at:
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.jasig.portlet.proxy.service.web.interceptor;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

import java.util.HashMap;
import java.util.Map;

import javax.portlet.PortletRequest;

import org.jasig.portlet.proxy.service.web.HttpContentRequestImpl;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

public class UserInfoUrlParameterizingPreInterceptorTest {
	
	UserInfoUrlParameterizingPreInterceptor preprocessor;
	@Mock PortletRequest portletRequest;
	HttpContentRequestImpl proxyRequest;
	Map<String, String[]> parameters;
	Map<String, String> userInfoMap;
	
	@Before
	public void setUp() {
		MockitoAnnotations.initMocks(this);
		
		preprocessor = new UserInfoUrlParameterizingPreInterceptor();
		
		parameters = new HashMap<String, String[]>();
		userInfoMap = new HashMap<String, String>();
		
		when(portletRequest.getAttribute(PortletRequest.USER_INFO)).thenReturn(userInfoMap);
		userInfoMap.put("test", "somevalue");
		
		proxyRequest = new HttpContentRequestImpl();
		proxyRequest.setParameters(parameters);
		proxyRequest.setProxiedLocation("http://somewhere.com/rest/test/id");
		
	}

	@Test
	public void testNoChange() {
		parameters.put("test", new String[]{"somevalue"});
		
		preprocessor.intercept(proxyRequest, portletRequest);
		assertEquals("http://somewhere.com/rest/test/id", proxyRequest.getProxiedLocation());
		assertEquals("somevalue", proxyRequest.getParameters().get("test")[0]);
	}

	@Test
	public void testReplacePathElement() {
		proxyRequest.setProxiedLocation("http://somewhere.com/rest/{test}/id");
		preprocessor.intercept(proxyRequest, portletRequest);
		
		assertEquals("http://somewhere.com/rest/somevalue/id", proxyRequest.getProxiedLocation());

	}
	
	@Test
	public void testReplaceParamter() {
		parameters.put("param", new String[]{"val1", "{test}"});
		preprocessor.intercept(proxyRequest, portletRequest);
		
		assertEquals("val1", proxyRequest.getParameters().get("param")[0]);
		assertEquals("somevalue", proxyRequest.getParameters().get("param")[1]);
	}

}
