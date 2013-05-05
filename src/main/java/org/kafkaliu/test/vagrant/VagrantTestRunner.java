package org.kafkaliu.test.vagrant;
import static org.kafkaliu.test.vagrant.VagrantUtils.getVagrantLog;
import static org.kafkaliu.test.vagrant.VagrantUtils.getVagrantfilePath;

import org.junit.runners.BlockJUnit4ClassRunner;
import org.junit.runners.model.InitializationError;
import org.junit.runners.model.Statement;
import org.kafkaliu.test.vagrant.ruby.VagrantEnvironment;

public class VagrantTestRunner extends BlockJUnit4ClassRunner {
	
	private Class<?> klass;
	
	private VagrantEnvironment vagrantEnv;

	public VagrantTestRunner(Class<?> klass) throws InitializationError {
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
