package org.kafkaliu.test.vagrant.ruby;

import static org.kafkaliu.test.vagrant.ruby.VagrantRubyHelper.argsAsString;

import java.util.HashMap;
import java.util.Map;

import org.jruby.RubyArray;
import org.jruby.RubyMethod;
import org.jruby.RubyObject;
import org.jruby.RubySymbol;
import org.jruby.runtime.Block;
import org.jruby.runtime.MethodBlock;
import org.jruby.runtime.ThreadContext;
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
	
	private Map<String, Map<String, String>> ssh(String command) {
		Map<String, Map<String, String>> results = new HashMap<String, Map<String, String>>();
		for (RubySymbol vmName : vagrantMachine.getMachineNames()) {
			results.put(vmName.asJavaString(), sshOne(vmName.asJavaString(), command));
		}
		return results;
	}
	
	public Map<String, Map<String, String>> ssh(String vmName, String command) {
		if (vmName == null || vmName.isEmpty()) {
			return ssh(command);
		} else {
			Map<String, Map<String, String>> result = new HashMap<String, Map<String,String>>();
			result.put(vmName, sshOne(vmName, command));
			return result;
		}
	}
	
	private Map<String, String> sshOne(String vmName, String command) {
		return ssh(vagrantMachine.getMachine(vmName), command);
	}
	
	// refer to ssh_run.rb
	private Map<String, String> ssh(RubyObject vm, String command) {
		final Map<String, String> result = new HashMap<String, String>();
		RubyMethod method = (RubyMethod) (((RubyObject) vm.callMethod("communicate")).method(argsAsString(env, "execute")));

		ThreadContext tc = env.getEnvironment().getRuntime().getCurrentContext();
		Block methodBlock = MethodBlock.createMethodBlock(tc, method, tc.getCurrentScope(), new MethodBlock(method, tc.getCurrentStaticScope()) {
			
			@Override
			public IRubyObject callback(IRubyObject value, IRubyObject method,
					IRubyObject self, Block block) {
				RubyArray resultArray = (RubyArray) value; 
				RubySymbol type = (RubySymbol) resultArray.get(0);
				String data = (String) resultArray.get(1);
				result.put(type.asJavaString(), data);
				return null;
			}
		});
		
		method.call(tc, argsAsString(env, command), methodBlock);

		return result;
	}
	
	private IRubyObject cli(String... commands) {
		return env.callCli("cli", commands);
	}

}
