package org.kafkaliu.test.vagrant.ruby;

import org.jruby.RubyString;
import org.jruby.RubySymbol;
import org.jruby.runtime.builtin.IRubyObject;

public class VagrantRubyHelper {
	public static IRubyObject[] argsAsString(VagrantEnvironment vagrantEnv, String... commands) {
		IRubyObject[] args = new IRubyObject[commands.length];
		for (int i = 0; i < commands.length; i++) {
			args[i] = newString(vagrantEnv, commands[i]);
		}
		return args;
	}
	
	public static IRubyObject[] argsAsSymbol(VagrantEnvironment vagrantEnv, String... commands) {
		IRubyObject[] args = new IRubyObject[commands.length];
		for (int i = 0; i < commands.length; i++) {
			args[i] = newSymbol(vagrantEnv, commands[i]);
		}
		return args;
	}
	
	public static RubyString newString(VagrantEnvironment vagrantEnv, String str) {
		return RubyString.newString(vagrantEnv.getEnvironment().getRuntime(), str);
	}
	
	public static RubySymbol newSymbol(VagrantEnvironment vagrantEnv, String str) {
		return RubySymbol.newSymbol(vagrantEnv.getEnvironment().getRuntime(), str);
	}
}
