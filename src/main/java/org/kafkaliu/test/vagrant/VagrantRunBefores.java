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

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.text.MessageFormat;
import java.util.Map;
import java.util.logging.Logger;

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

  private Logger logger = Logger.getLogger(VagrantRunBefores.class.getName());

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
      if (vagrantEnv.allowSahara()) {
        if (vagrantEnv.isSaharaOff()) {
          vagrantEnv.saharaOn();
        }
        vagrantEnv.saharaRollback();
      }
    }
    if (getTestApplicationMain() != null) {
      startApplication(getTestApplicationMain());
    }
    statement.evaluate();
  }

  private String getClassEnvironmentVariables(Class<?> klass) {
    try {
      Method environmentVariables = klass.getMethod("environmentVariables", null);
      assert Modifier.isStatic(environmentVariables.getModifiers());
      Object ret = environmentVariables.invoke(null);
      assert ret instanceof Map;
      StringBuilder sb = new StringBuilder();
      Map<String, String> map = (Map<String, String>) ret;
      for (String key : map.keySet()) {
        sb.append(String.format(" -D%s=%s ", key, map.get(key)));
      }
      return sb.toString();
    } catch (NoSuchMethodException e) {
      return "";
    } catch (InvocationTargetException e) {
      throw new RuntimeException(e);
    } catch (IllegalAccessException e) {
      throw new RuntimeException(e);
    }
  }

  private Object startApplication(VagrantTestApplication annotation) throws Throwable {
    String serverApp = annotation.klass().getName();
    String args = annotation.args();
    args = args != null ? args : "";
    boolean isDaemon = annotation.isDaemon();
    String vmJavaLibPath = convertToGuestPaths(System.getProperty("java.library.path"), guestpath);
    String vmJavaClassPath = convertToGuestPaths(System.getProperty("java.class.path"), guestpath);
    String command = null;
    String classEnvironmentVariables = getClassEnvironmentVariables(klass);
    if (isDaemon) {
      command = MessageFormat.format("nohup java -Djava.library.path={0} {1} -cp {2} {3} {4} > /dev/null 2>&1 &",
              vmJavaLibPath, classEnvironmentVariables, vmJavaClassPath, serverApp, args);
    } else {
      command = MessageFormat.format("java -Djava.library.path={0} {1} -cp {2} {3} {4}",
              vmJavaLibPath, classEnvironmentVariables, vmJavaClassPath, serverApp, args);
    }
    logger.info(String.format("Command in VM : %s", command));
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
