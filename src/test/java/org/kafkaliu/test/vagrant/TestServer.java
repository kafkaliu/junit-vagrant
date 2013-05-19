package org.kafkaliu.test.vagrant;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.AbstractHandler;

import com.hazelcast.core.Hazelcast;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.IdGenerator;

public class TestServer {

	/**
	 * @param args
	 * @throws Exception 
	 */
	public static void main(String[] args) throws Exception {
		HazelcastInstance intance = Hazelcast.newHazelcastInstance();
		Server server = new Server(8080);
		IdGenerator id = intance.getIdGenerator("test-cluster");
		server.setHandler(new TestServerHandler(id.newId()));
		server.start();
		server.join();
	}
	
	private static class TestServerHandler extends AbstractHandler {
		private long id;
		
		public TestServerHandler(long id) {
			this.id = id;
		}

		@Override
		public void handle(String target, Request baseRequest, HttpServletRequest request,
				HttpServletResponse response) throws IOException, ServletException {
			response.setContentType("text/json;charset=utf-8");
	        response.setStatus(HttpServletResponse.SC_OK);
	        baseRequest.setHandled(true);
	        response.getWriter().print(id);
		}
	}
}
