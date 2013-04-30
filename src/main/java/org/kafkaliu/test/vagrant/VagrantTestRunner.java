package org.kafkaliu.test.vagrant;
import java.io.File;

import org.junit.runners.BlockJUnit4ClassRunner;
import org.junit.runners.model.InitializationError;
import org.junit.runners.model.Statement;
import org.kafkaliu.test.vagrant.annotations.VagrantConfigure;
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

	private static File getVagrantfilePath(Class<?> klass)
			throws InitializationError {
		VagrantConfigure annotation = klass
				.getAnnotation(VagrantConfigure.class);
		File workingDir = annotation == null ? new File(".") : new File(
				annotation.vagrantfilePath());
		if (workingDir.exists()) {
			return workingDir;
		}
		throw new InitializationError(String.format(
				"class '%s' must have a valid VagrantfilePath",
				klass.getName()));
	}
	
	public static String getVagrantLog(Class<?> klass) {
		VagrantConfigure annotation = klass.getAnnotation(VagrantConfigure.class);
		return annotation == null ? null : annotation.vagrantLog();
	}
}
