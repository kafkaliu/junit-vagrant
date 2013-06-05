package org.kafkaliu.test.vagrant.server;

import static org.kafkaliu.test.vagrant.server.VagrantUtils.getVagrantLog;
import static org.kafkaliu.test.vagrant.server.VagrantUtils.getVagrantfile;

import java.net.URL;

import org.eclipse.jetty.client.ContentExchange;
import org.eclipse.jetty.client.HttpClient;
import org.jruby.RubyObject;
import org.junit.runner.RunWith;
import org.junit.runners.model.InitializationError;
import org.kafkaliu.test.vagrant.ruby.VagrantCli;
import org.kafkaliu.test.vagrant.ruby.VagrantEnvironment;
import org.kafkaliu.test.vagrant.ruby.VagrantMachine;

@RunWith(VagrantServerTestRunner.class)
public class VagrantServerTestCase {

  private VagrantEnvironment vagrantEnv;

  private VagrantCli vagrantCli;

  private VagrantMachine vagrantMachine;

  public VagrantServerTestCase() throws InitializationError {
    vagrantEnv = new VagrantEnvironment(getVagrantfile(getClass()), getVagrantLog(getClass()));
    vagrantCli = new VagrantCli(vagrantEnv);
    vagrantMachine = new VagrantMachine(vagrantEnv);
  }

  protected void start(String vmName) {
    vagrantCli.up(vmName);
  }

  protected void shutdown(String vmName) {
    vagrantCli.halt(vmName);
  }

  protected void ssh(String vmName, String command) {
    vagrantCli.ssh(vmName, command);
  }

  protected String status(String vmName) {
    return ((RubyObject) vagrantMachine.getMachine(vmName).callMethod("state")).getInstanceVariable("@short_description").asJavaString();
  }

  protected Object[] request(URL url) throws Exception {
    ContentExchange exchange = new ContentExchange();
    exchange.setURL(url.toString());

    HttpClient client = new HttpClient();
    client.start();
    client.send(exchange);

    Object[] result = new Object[2];
    result[0] = exchange.waitForDone();
    result[1] = exchange.getResponseContent();
    return result;
  }
}
