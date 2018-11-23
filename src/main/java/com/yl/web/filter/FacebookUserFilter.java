package com.yl.web.filter;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import org.w3c.dom.Document;

import com.google.code.facebookapi.FacebookException;
import com.google.code.facebookapi.FacebookWebappHelper;
import com.google.code.facebookapi.FacebookXmlRestClient;

public class FacebookUserFilter implements Filter
{

	private String apiKey;
	private String secretKey;

	public void init(FilterConfig filterConfig) throws ServletException
	{
		apiKey = filterConfig.getServletContext().getInitParameter(
				"facebook_api_key");
		secretKey = filterConfig.getServletContext().getInitParameter(
				"facebook_secret");
	}

	public void destroy()
	{
	}

	public void doFilter(ServletRequest req, ServletResponse res,
			FilterChain chain) throws IOException, ServletException
	{
		HttpServletRequest request = (HttpServletRequest) req;
		HttpServletResponse response = (HttpServletResponse) res;
		HttpSession session = request.getSession();
		session.setAttribute("apiKey", apiKey);
		session.setAttribute("secretKey", secretKey);

		FacebookXmlRestClient frc = new FacebookXmlRestClient(apiKey, secretKey);

		String fb_session_key = request.getParameter("fb_sig_session_key");
		fb_session_key = fb_session_key == null ? "" : fb_session_key;

		String token = request.getParameter("auth_token");
		token = null == token ? "" : token;

		if (fb_session_key.equals(""))
		{
			if (!token.equals(""))
			{
				try
				{
					fb_session_key = frc.auth_getSession(token);
				}
				catch (FacebookException e)
				{
					System.out.println("======facebook error===========");
				}
				session.setAttribute("facebookUserId", fb_session_key);
			}
			else
			{
				FacebookWebappHelper<Document> facebook = new FacebookWebappHelper<Document>(
						request, response, apiKey, secretKey, frc);
				facebook.requireFrame("");
				response.sendRedirect("http://www.putzone.com/putexas/user.do");
			}
		}
		else
		{
			session.setAttribute("facebookUserId", fb_session_key);
		}
		chain.doFilter(req, res);
	}
}