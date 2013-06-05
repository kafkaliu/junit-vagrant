package org.kafkaliu.test.vagrant.ruby;

import static org.kafkaliu.test.vagrant.ruby.VagrantRubyHelper.argsAsString;
import static org.kafkaliu.test.vagrant.ruby.VagrantRubyHelper.argsAsSymbol;

import java.io.File;
import java.io.PrintStream;
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
	public VagrantEnvironment(File vagrantfile, PrintStream out, PrintStream err, String vagrantLog, String uiClass) {
		if (!vagrantfile.exists()) {
			throw new RuntimeException("Vagrantfile not exists: "
					+ vagrantfile);
		}
		scriptingContainer = new ScriptingContainer(
				LocalContextScope.SINGLETHREAD);
		if (out != null) {
			scriptingContainer.setOutput(out);
		}
		
		if (err != null) {
			scriptingContainer.setError(err);
		}
		
		Map<String, String> params = new HashMap<String, String>(scriptingContainer.getEnvironment());
		if (vagrantLog != null && !vagrantLog.isEmpty()) {
			params.put("VAGRANT_LOG", vagrantLog);
		}
		scriptingContainer.setEnvironment(params);
		
		if (uiClass == null) {
			uiClass = "Vagrant::UI::Silent";
		}
		env = (RubyObject) scriptingContainer
				.runScriptlet("require 'vagrant'\n" + "\n"
						+ "return Vagrant::Environment.new(:cwd => '"
						+ vagrantfile.getAbsoluteFile().getParent() + "', :vagrantfile_name => '" + vagrantfile.getName() + "', :ui_class => " + uiClass + ")");
		
	}
	
	public VagrantEnvironment(File vagrantfile, String vagrantLog) {
		this(vagrantfile, null, null, vagrantLog, null);
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
