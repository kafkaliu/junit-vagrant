package org.kafkaliu.test.vagrant.server;
import static org.kafkaliu.test.vagrant.server.VagrantUtils.getVagrantLog;
import static org.kafkaliu.test.vagrant.server.VagrantUtils.getVagrantfilePath;

import org.junit.runners.BlockJUnit4ClassRunner;
import org.junit.runners.model.InitializationError;
import org.junit.runners.model.Statement;
import org.kafkaliu.test.vagrant.VagrantRunAfters;
import org.kafkaliu.test.vagrant.VagrantRunBefores;
import org.kafkaliu.test.vagrant.ruby.VagrantEnvironment;

public class VagrantServerTestRunner extends BlockJUnit4ClassRunner {
	
	private Class<?> klass;
	
	private VagrantEnvironment vagrantEnv;

	public VagrantServerTestRunner(Class<?> klass) throws InitializationError {
		super(klass);
		this.klass = klass;
		vagrantEnv = new VagrantEnvironment(getVagrantfilePath(klass), getVagrantLog(klass));
	}

	@Override
	protected Statement withBeforeClasses(Statement statement) {
		return super.withBeforeClasses(new VagrantRunBefores(statement, vagrantEnv, klass));
	}

	@Override
	protected Statement withAfterClasses(Statement statement) {
		return super.withAfterClasses(new VagrantRunAfters(statement, vagrantEnv, klass));
	}
}
