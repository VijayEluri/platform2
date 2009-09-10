/*
 * Created on 30.3.2004
 */
package com.idega.servlet.filter;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * USAGE:
 * Add the following to your web.xml (under /WEB-INF):
 * 		
 * 	<filter>
 * 		<filter-name>Compress</filter-name>
 * 		<filter-class>com.idega.servlet.filter.GZIPFilter</filter-class>
 * 	</filter>
 * 
 * And then you have to specify what to compress:
 *
 * 	<filter-mapping>
 *	   	<filter-name>Compress</filter-name>
 * 		<url-pattern>*.jsp</url-pattern>
 * 	</filter-mapping>
 *
 * 	<filter-mapping>
 * 		<filter-name>Compress</filter-name>
 * 		<url-pattern>*.html</url-pattern>
 * 	</filter-mapping>
 *	
 * 	<filter-mapping>
 * 		<filter-name>Compress</filter-name>
 * 		<url-pattern>/servlet/*</url-pattern>
 * 	</filter-mapping>
 * 
 * @author laddi
 */
public class GZIPFilter implements Filter {

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.servlet.Filter#init(javax.servlet.FilterConfig)
	 */
	public void init(FilterConfig arg0) throws ServletException {
		System.out.println("[idegaWebApp] : Starting GZIPFilter");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.servlet.Filter#doFilter(javax.servlet.ServletRequest,
	 *      javax.servlet.ServletResponse, javax.servlet.FilterChain)
	 */
	public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain) throws IOException, ServletException {
		if (req instanceof HttpServletRequest) {
			HttpServletRequest request = (HttpServletRequest) req;
			HttpServletResponse response = (HttpServletResponse) res;
			String ae = request.getHeader("accept-encoding");
			if (ae != null && ae.indexOf("gzip") != -1) {
				//System.out.println("GZIP supported, compressing.");
				GZIPResponseWrapper wrappedResponse = new GZIPResponseWrapper(response);
				chain.doFilter(req, wrappedResponse);
				wrappedResponse.finishResponse();
				return;
			}
			chain.doFilter(req, res);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.servlet.Filter#destroy()
	 */
	public void destroy() {
	}

}