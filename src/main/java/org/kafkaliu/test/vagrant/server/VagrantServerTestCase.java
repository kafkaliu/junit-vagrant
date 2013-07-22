package org.kafkaliu.test.vagrant.server;

import org.jruby.RubyObject;
import org.junit.runner.RunWith;
import org.junit.runners.model.InitializationError;
import org.kafkaliu.test.vagrant.ruby.VagrantCli;
import org.kafkaliu.test.vagrant.ruby.VagrantEnvironment;
import org.kafkaliu.test.vagrant.ruby.VagrantMachine;

import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Map;

import static org.kafkaliu.test.vagrant.server.VagrantUtils.getVagrantLog;
import static org.kafkaliu.test.vagrant.server.VagrantUtils.getVagrantfile;

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

  public Map<String,Map<String,String>> ssh(String vmName, String command) {
    return vagrantCli.ssh(vmName, command);
  }

  public Map<String,Map<String,String>> trySsh(String vmName, String command) {
    return vagrantCli.trySsh(vmName, command);
  }

  protected String status(String vmName) {
    return ((RubyObject) vagrantMachine.getMachine(vmName).callMethod("state")).getInstanceVariable("@short_description").asJavaString();
  }

  protected int request(URL url) throws Exception {
    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
    return connection.getResponseCode();
  }
}
