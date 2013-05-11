package org.kafkaliu.test.vagrant;

import static org.kafkaliu.test.vagrant.ruby.VagrantRubyHelper.argsAsString;
import static org.kafkaliu.test.vagrant.server.VagrantUtils.*;

import java.util.Map;

import org.jruby.RubyObject;
import org.junit.runners.model.InitializationError;
import org.junit.runners.model.Statement;
import org.kafkaliu.test.vagrant.ruby.VagrantCli;
import org.kafkaliu.test.vagrant.ruby.VagrantEnvironment;
import org.kafkaliu.test.vagrant.ruby.VagrantMachine;
import org.kafkaliu.test.vagrant.server.annotations.VagrantTestApplication;

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
		String serverApp = getTestApplicationMain();
		if (serverApp != null) {
			startApplication(serverApp);
		}
		statement.evaluate();
	}

	private void startApplication(String serverApp) throws Throwable {
		final String command = "nohup java -cp " + convertToGuestPaths(System.getProperty("java.class.path"), guestpath) + " " + serverApp + " > /dev/null 2>&1 &";
		cli.ssh(command);
		Thread.sleep(10 * 1000);
	}
	
	private String getTestApplicationMain() throws InitializationError {
		VagrantTestApplication testApplication = klass.getAnnotation(VagrantTestApplication.class);
		if (testApplication == null) {
			return null;
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
