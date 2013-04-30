package org.kafkaliu.test.vagrant;

import static org.junit.Assert.assertEquals;

import java.net.URL;

import org.eclipse.jetty.client.HttpClient;
import org.eclipse.jetty.client.HttpExchange;
import org.junit.Test;
import org.kafkaliu.test.vagrant.annotations.VagrantConfigure;
import org.kafkaliu.test.vagrant.annotations.VagrantTestApplication;

@VagrantConfigure(vagrantfilePath = "src/test/resources")
@VagrantTestApplication(TestServer.class)
public class VagrantTestCaseTests extends VagrantTestCase {
	
	@Test
	public void testServersInVM() throws Exception {
		assertEquals(HttpExchange.STATUS_COMPLETED, request(new URL("http://192.168.56.100:8080")));
	}

	private int request(URL url) throws Exception {
		HttpExchange exchange = new HttpExchange();
		exchange.setURL(url.getProtocol() + "://" + url.getHost() + ":" + url.getPort());

		HttpClient client = new HttpClient();
		client.start();
		client.send(exchange);

		return exchange.waitForDone();
	}

}
