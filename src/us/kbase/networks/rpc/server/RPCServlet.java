package us.kbase.networks.rpc.server;
import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import us.kbase.networks.adaptor.AdaptorException;

import com.googlecode.jsonrpc4j.KBase_JsonRpcServer;

/**
 * Servlet implementation class RpcServlet
 */
public class RPCServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	KBase_JsonRpcServer jsonRpcServer;
	

	
	public RPCServlet() throws AdaptorException
	{
		NetworksService service = new NetworksService();
		
		jsonRpcServer = new KBase_JsonRpcServer(service, NetworksService.class );			
		jsonRpcServer.setBackwardsComaptible(true);  
	}
	
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {		
		jsonRpcServer.handle(request, response);
	}
}

