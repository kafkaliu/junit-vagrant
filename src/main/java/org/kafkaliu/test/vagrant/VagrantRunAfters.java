package org.kafkaliu.test.vagrant;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.junit.runner.RunWith;
import org.junit.runners.model.InitializationError;
import org.junit.runners.model.MultipleFailureException;
import org.junit.runners.model.Statement;
import org.kafkaliu.test.vagrant.annotations.VagrantConfigure;
import org.kafkaliu.test.vagrant.annotations.VagrantTestApplication;
import org.kafkaliu.test.vagrant.annotations.VagrantVirtualMachine;
import org.kafkaliu.test.vagrant.ruby.VagrantCli;
import org.kafkaliu.test.vagrant.ruby.VagrantEnvironment;
import org.kafkaliu.test.vagrant.server.VagrantServerTestRunner;

public class VagrantRunAfters extends Statement {

  private Statement statement;

  private VagrantCli cli;

  private Class<?> klass;

  public VagrantRunAfters(Statement statement, VagrantEnvironment vagrantEnv, Class<?> klass) {
    super();
    this.statement = statement;
    this.cli = new VagrantCli(vagrantEnv);
    this.klass = klass;
  }

  @Override
  public void evaluate() throws Throwable {
    List<Throwable> errors = new ArrayList<Throwable>();
    try {
      statement.evaluate();
    } catch (Throwable e) {
      errors.add(e);
    } finally {
      VagrantTestApplication testApplication = getTestApplicationMain();
      if (testApplication != null && testApplication.logfile() != null && !testApplication.logfile().isEmpty()) {
        Map<String, Map<String, String>> logs = cli.ssh(getVirtualMachine(), "cat /home/vagrant/" + testApplication.logfile());
        for (String vm : logs.keySet()) {
          System.out.println(MessageFormat.format("****************** {0} log begin ******************", vm));
          System.out.println(logs.get(vm));
          System.out.println(MessageFormat.format("****************** {0} log end ******************", vm));
        }
      }

      VagrantConfigure annotation = klass.getAnnotation(VagrantConfigure.class);
      if (annotation != null && annotation.needDestroyVmAfterClassTest()) {
        cli.destroy();
      } else {
        if (klass.getAnnotation(RunWith.class).value().isAssignableFrom(VagrantServerTestRunner.class)) {
          try {
            cli.ssh(getVirtualMachine(), "killall java");
          } catch (Exception e) {
          }
          Thread.sleep(5 * 1000);
        }
      }
    }
    MultipleFailureException.assertEmpty(errors);
  }

  private VagrantTestApplication getTestApplicationMain() throws InitializationError {
    VagrantTestApplication testApplication = klass.getAnnotation(VagrantTestApplication.class);
    return testApplication;
  }

  private String getVirtualMachine() {
    VagrantVirtualMachine vm = klass.getAnnotation(VagrantVirtualMachine.class);
    return vm == null ? null : vm.value();
  }
}
