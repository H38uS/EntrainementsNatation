package com.mosioj.entrainements.filter;

import java.io.IOException;
import java.text.MessageFormat;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.mobile.device.Device;
import org.springframework.mobile.device.DeviceResolver;
import org.springframework.mobile.device.LiteDeviceResolver;

import com.mosioj.entrainements.entities.User;

/**
 * Add extra information in the request about whether it comes from a mobile device or not.
 * 
 * @author Jordan Mosio
 *
 */
public class DeviceResolverFilter implements Filter {

	/**
	 * Class logger.
	 */
	private static final Logger logger = LogManager.getLogger(DeviceResolverFilter.class);

	/**
	 * Spring device resolver.
	 */
	private static final DeviceResolver DEVICE_RESOLVER = new LiteDeviceResolver();

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {

		logger.trace("Entering device resolver filtering...");

		HttpServletRequest detailRequest = (HttpServletRequest) request;
		String url = detailRequest.getRequestURL().toString();

		Device device = DEVICE_RESOLVER.resolveDevice(detailRequest);
		request.setAttribute("device", device);
		request.setAttribute("is_mobile", device.isMobile());
		request.setAttribute("is_normal", device.isNormal());

		User user = (User) ((HttpServletRequest) request).getSession().getAttribute(LoginFilter.PARAM_CONNECTED_USER);
		String name = user == null ? "anonymous" : user.getName();
		logger.debug(MessageFormat.format("URL: {0}, demandée par {1}. Is mobile: {2}.", url, name, device.isMobile()));
		chain.doFilter(request, response);
	}

	@Override
	public void init(FilterConfig filterConfig) throws ServletException {
		// Nothing to do
	}

	@Override
	public void destroy() {
		// Nothing to do
	}
}
