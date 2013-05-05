package org.kafkaliu.test.vagrant;

import static org.junit.Assert.assertEquals;

import java.net.URL;
import java.text.MessageFormat;

import org.eclipse.jetty.client.HttpExchange;
import org.junit.Test;
import org.junit.runners.model.InitializationError;
import org.kafkaliu.test.vagrant.annotations.VagrantConfigure;
import org.kafkaliu.test.vagrant.annotations.VagrantTestApplication;

@VagrantConfigure(vagrantfilePath = "src/test/resources")
@VagrantTestApplication(TestServer.class)
public class VagrantTestCaseTests extends VagrantTestCase {
	
	public VagrantTestCaseTests() throws InitializationError {
		super();
	}

	@Test
	public void testServersInVM() throws Exception {
		assertEquals(HttpExchange.STATUS_COMPLETED, request(new URL("http://192.168.56.100:8080")));
	}

	@Test
	public void testShutdown() {
		assertEquals("running", status("default"));
		shutdown("default");
		assertEquals("poweroff", status("default"));
	}
}
