package org.kafkaliu.test.vagrant.ruby;

import java.io.IOException;

import org.jruby.RubySymbol;
import org.jruby.runtime.builtin.IRubyObject;

public class VagrantCli {

	private VagrantEnvironment env;
	
	private VagrantMachine vagrantMachine;

	public VagrantCli(VagrantEnvironment env) {
		this.env = env;
		this.vagrantMachine = new VagrantMachine(env);
	}

	public void up() throws IOException {
		cli("up");
	}

	public void destroy() throws IOException {
		cli("destroy", "-f");
	}

	public void halt() throws IOException {
		cli("halt");
	}

	public void halt(String vmName) throws IOException {
		cli("halt", vmName);
	}

	public void suspend() throws IOException {
		cli("suspend");
	}

	public void resume() throws IOException {
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
