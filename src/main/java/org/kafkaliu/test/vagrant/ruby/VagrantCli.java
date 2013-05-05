package org.kafkaliu.test.vagrant.ruby;

import org.jruby.RubySymbol;
import org.jruby.runtime.builtin.IRubyObject;

public class VagrantCli {

	private VagrantEnvironment env;
	
	private VagrantMachine vagrantMachine;

	public VagrantCli(VagrantEnvironment env) {
		this.env = env;
		this.vagrantMachine = new VagrantMachine(env);
	}

	public void up() {
		cli("up");
	}

	public void destroy() {
		cli("destroy", "-f");
	}

	public void halt() {
		cli("halt");
	}

	public void halt(String vmName) {
		cli("halt", vmName);
	}

	public void suspend() {
		cli("suspend");
	}

	public void resume() {
		cli("resume");
	}
	
	public void ssh(String command) {
		for (RubySymbol vmName : vagrantMachine.getMachineNames()) {
			cli("ssh", vmName.asJavaString(), "-c", command);
		}
	}
	
	private IRubyObject cli(String... commands) {
		return env.callCli("cli", commands);
	}

}
