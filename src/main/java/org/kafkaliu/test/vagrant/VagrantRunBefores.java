package org.kafkaliu.test.vagrant;

import org.jruby.RubyObject;
import org.junit.runners.model.InitializationError;
import org.junit.runners.model.Statement;
import org.kafkaliu.test.vagrant.annotations.VagrantConfigure;
import org.kafkaliu.test.vagrant.annotations.VagrantTestApplication;
import org.kafkaliu.test.vagrant.annotations.VagrantVirtualMachine;
import org.kafkaliu.test.vagrant.ruby.VagrantCli;
import org.kafkaliu.test.vagrant.ruby.VagrantEnvironment;
import org.kafkaliu.test.vagrant.ruby.VagrantMachine;

import java.net.InetAddress;
import java.text.MessageFormat;
import java.util.Map;

import static org.kafkaliu.test.vagrant.ruby.VagrantRubyHelper.argsAsString;
import static org.kafkaliu.test.vagrant.server.VagrantUtils.convertToGuestPaths;
import static org.kafkaliu.test.vagrant.server.VagrantUtils.generateHostGuestSharedFolderMapping;

public class VagrantRunBefores extends Statement {

  private Statement statement;

  private VagrantEnvironment vagrantEnv;

  private VagrantCli cli;

  private VagrantMachine vagrantMachine;

  private Class<?> klass;

  private String guestpath = "/vagrant-junit";

  public VagrantRunBefores(Statement statement, VagrantEnvironment vagrantEnv, Class<?> klass) {
    super();
    this.statement = statement;
    this.vagrantEnv = vagrantEnv;
    this.klass = klass;
    this.cli = new VagrantCli(vagrantEnv);
    this.vagrantMachine = new VagrantMachine(vagrantEnv);
  }

  @Override
  public void evaluate() throws Throwable {
    if (needUpVm()) {
      syncedPaths();
      cli.up();
    }
    if (getTestApplicationMain() != null) {
      startApplication(getTestApplicationMain());
    }
    statement.evaluate();
  }

  private Object startApplication(VagrantTestApplication annotation) throws Throwable {
    String serverApp = annotation.klass().getName();
    String args = annotation.args();
    args = args != null ? args : "";
    boolean isDaemon = annotation.isDaemon();
    String vmJavaLibPath = convertToGuestPaths(System.getProperty("java.library.path"), guestpath);
    String vmJavaClassPath = convertToGuestPaths(System.getProperty("java.class.path"), guestpath);
    String vagrantMasterIp = System.getProperty("vagrant.master.ip");
    if (null == vagrantMasterIp) {
      vagrantMasterIp = InetAddress.getLocalHost().getHostAddress();
    }
    String command = null;
    if (isDaemon) {
      command = MessageFormat.format("nohup java -Djava.library.path={0} -Dvagrant.master.ip={1} -cp {2} {3} {4} > /dev/null 2>&1 &",
              vmJavaLibPath, vagrantMasterIp, vmJavaClassPath, serverApp, args);
    } else {
      command = MessageFormat.format("java -Djava.library.path={0} -Dvagrant.master.ip={1} -cp {2} {3} {4}",
              vmJavaLibPath, vagrantMasterIp, vmJavaClassPath, serverApp, args);
    }
    Map<String, Map<String, String>> result = cli.ssh(getVirtualMachine(), command);
    Thread.sleep(10 * 1000);
    return result;
  }

  private String getVirtualMachine() {
    VagrantVirtualMachine vm = klass.getAnnotation(VagrantVirtualMachine.class);
    return vm == null ? null : vm.value();
  }

  private boolean needUpVm() {
    VagrantConfigure config = klass.getAnnotation(VagrantConfigure.class);
    return config.needUpVmBeforeClassTest();
  }

  private VagrantTestApplication getTestApplicationMain() throws InitializationError {
    VagrantTestApplication testApplication = klass.getAnnotation(VagrantTestApplication.class);
    return testApplication;
  }

  private void syncedPaths() {
    for (RubyObject machine : vagrantMachine.getMachines()) {
      RubyObject config = (RubyObject) machine.getInstanceVariable("@config");
      RubyObject vm = (RubyObject) config.callMethod("vm");
      syncedClasspath(vm);
      syncedLibrarypath(vm);
    }
  }

  private void syncedClasspath(RubyObject vm) {
    syncedpath(vm, System.getProperty("java.class.path"));
  }

  private void syncedLibrarypath(RubyObject vm) {
    syncedpath(vm, System.getProperty("java.library.path"));
  }

  private void syncedpath(RubyObject vm, String path) {
    if (path == null || path.isEmpty())
      return;
    Map<String, String> mapping = generateHostGuestSharedFolderMapping(path, guestpath);
    for (String host : mapping.keySet()) {
      vm.callMethod("synced_folder", argsAsString(vagrantEnv, new String[]{host, mapping.get(host)}));
    }
  }
}
