package org.kafkaliu.test.vagrant.ruby;

import static org.kafkaliu.test.vagrant.ruby.VagrantRubyHelper.argsAsString;
import static org.kafkaliu.test.vagrant.ruby.VagrantRubyHelper.argsAsSymbol;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import org.jruby.RubyObject;
import org.jruby.embed.LocalContextScope;
import org.jruby.embed.ScriptingContainer;
import org.jruby.runtime.builtin.IRubyObject;

public class VagrantEnvironment {

	private ScriptingContainer scriptingContainer;

	private RubyObject env;

	@SuppressWarnings("unchecked")
	public VagrantEnvironment(File workingDir, String vagrantLog) {
		if (!workingDir.exists()) {
			throw new RuntimeException("Working directory not exists: "
					+ workingDir);
		}
		scriptingContainer = new ScriptingContainer(
				LocalContextScope.SINGLETHREAD);
		Map<String, String> params = new HashMap<String, String>(scriptingContainer.getEnvironment());
		if (vagrantLog != null && !vagrantLog.isEmpty()) {
			params.put("VAGRANT_LOG", vagrantLog);
		}
		scriptingContainer.setEnvironment(params);
		env = (RubyObject) scriptingContainer
				.runScriptlet("require 'vagrant'\n" + "\n"
						+ "return Vagrant::Environment.new(:cwd => '"
						+ workingDir.getAbsolutePath() + "')");
	}
	
	public IRubyObject callCli(String name, String... args) {
		return env.callMethod(name, argsAsString(this, args));
	}

	public IRubyObject callMethod(String name, String... args) {
		return env.callMethod(name, argsAsSymbol(this, args));
	}

	public RubyObject getEnvironment() {
		return env;
	}
}
