package org.kafkaliu.test.vagrant.ruby;

import java.util.List;

import org.jruby.RubyObject;
import org.jruby.RubySymbol;
public class VagrantMachine {

	private VagrantEnvironment vagrantEnv;

	public VagrantMachine(VagrantEnvironment vagrantEnv) {
		super();
		this.vagrantEnv = vagrantEnv;
	}
	
	public RubyObject[] getMachines() {
		List<?> machineNames = getMachineNames();
		RubyObject[] machines = new RubyObject[machineNames.size()];
		for (int i = 0; i < machineNames.size(); i++) {
			RubySymbol machineName = (RubySymbol) machineNames.get(i);
			machines[i] = getMachine(machineName.asJavaString());
		}
		return machines;
	}
	
	public RubyObject getMachine(String vmName) {
		return method("machine", vmName, "virtualbox");
	}
	
	@SuppressWarnings("unchecked")
	public List<RubySymbol> getMachineNames() {
		return (List<RubySymbol>) vagrantEnv.callCli("machine_names");
	}
	
	private RubyObject method(String name, String... args) {
		return (RubyObject) vagrantEnv.callMethod(name, args);
	}

}
