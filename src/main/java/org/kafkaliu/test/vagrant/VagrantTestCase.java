package org.kafkaliu.test.vagrant;

import static org.kafkaliu.test.vagrant.VagrantUtils.getVagrantLog;
import static org.kafkaliu.test.vagrant.VagrantUtils.getVagrantfilePath;

import java.net.URL;

import org.eclipse.jetty.client.HttpClient;
import org.eclipse.jetty.client.HttpExchange;
import org.jruby.RubyObject;
import org.junit.runner.RunWith;
import org.junit.runners.model.InitializationError;
import org.kafkaliu.test.vagrant.ruby.VagrantCli;
import org.kafkaliu.test.vagrant.ruby.VagrantEnvironment;
import org.kafkaliu.test.vagrant.ruby.VagrantMachine;

@RunWith(VagrantTestRunner.class)
public class VagrantTestCase {

	private VagrantEnvironment vagrantEnv;

	private VagrantCli vagrantCli;
	
	private VagrantMachine vagrantMachine;

	public VagrantTestCase() throws InitializationError {
		vagrantEnv = new VagrantEnvironment(getVagrantfilePath(getClass()), getVagrantLog(getClass()));
		vagrantCli = new VagrantCli(vagrantEnv);
		vagrantMachine = new VagrantMachine(vagrantEnv);
	}

	protected void shutdown(String vmName) {
		vagrantCli.halt(vmName);
	}
	
	protected String status(String vmName) {
		return ((RubyObject) vagrantMachine.getMachine(vmName).callMethod("state")).getInstanceVariable("@short_description").asJavaString();
	}
	
	protected int request(URL url) throws Exception {
		HttpExchange exchange = new HttpExchange();
		exchange.setURL(url.getProtocol() + "://" + url.getHost() + ":" + url.getPort());

		HttpClient client = new HttpClient();
		client.start();
		client.send(exchange);

		return exchange.waitForDone();
	}
}
