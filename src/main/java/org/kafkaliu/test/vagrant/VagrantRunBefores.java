package org.kafkaliu.test.vagrant;

import static org.kafkaliu.test.vagrant.VagrantUtils.*;
import static org.kafkaliu.test.vagrant.ruby.VagrantRubyHelper.argsAsString;

import java.util.Map;

import org.jruby.RubyObject;
import org.junit.runners.model.InitializationError;
import org.junit.runners.model.Statement;
import org.kafkaliu.test.vagrant.annotations.VagrantTestApplication;
import org.kafkaliu.test.vagrant.ruby.VagrantCli;
import org.kafkaliu.test.vagrant.ruby.VagrantEnvironment;
import org.kafkaliu.test.vagrant.ruby.VagrantMachine;

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
		syncedClasspaths();
		cli.up();
		startApplication();
		Thread.sleep(5 * 1000);
		statement.evaluate();
	}

	private void startApplication() throws InitializationError {
		final String command = "nohup java -cp " + convertToGuestPaths(System.getProperty("java.class.path"), guestpath) + " " + getTestApplicationMain() + " > /dev/null 2>&1 &";
		cli.ssh(command);
	}
	
	private String getTestApplicationMain() throws InitializationError {
		VagrantTestApplication testApplication = klass.getAnnotation(VagrantTestApplication.class);
		if (testApplication == null) {
			throw new InitializationError(String.format(
					"class '%s' must have a valid VagrantTestApplication",
					klass.getName()));
		}
		return testApplication.value().getName();
	}

	private void syncedClasspaths() {
		for (RubyObject machine : vagrantMachine.getMachines()) {
			RubyObject config = (RubyObject) machine.getInstanceVariable("@config");
			RubyObject vm = (RubyObject) config.callMethod("vm");
			syncedClasspath(vm);
		}
	}

	private void syncedClasspath(RubyObject vm) {
		Map<String, String> mapping = generateHostGuestSharedFolderMapping(
				System.getProperty("java.class.path"), guestpath);
		for (String host : mapping.keySet()) {
			vm.callMethod("synced_folder", argsAsString(vagrantEnv, new String[] { host, mapping.get(host) }));
		}
	}
}
