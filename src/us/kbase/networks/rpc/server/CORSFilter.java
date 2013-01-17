package us.kbase.networks.rpc.server;

import java.io.*;
import javax.servlet.*;
import javax.servlet.http.*;

/**
   Allow cross-site AJAX requests.
   This code snippet comes from Matthias Hryniszak, here:
   http://padcom13.blogspot.de/2011/09/cors-filter-for-java-applications.html

   @version 1.0, 1/17/13
   @author JMC
*/
public class CORSFilter implements Filter {
    public CORSFilter() {
    }

    public void init(FilterConfig fConfig) throws ServletException {
    }

    public void destroy() {
    }

    public void doFilter(ServletRequest request,
			 ServletResponse response,
			 FilterChain chain)
	throws IOException, ServletException {
	((HttpServletResponse)response).addHeader("Access-Control-Allow-Origin", "*");
	chain.doFilter(request, response);
    }
}