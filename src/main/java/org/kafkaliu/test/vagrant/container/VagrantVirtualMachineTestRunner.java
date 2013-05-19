package org.kafkaliu.test.vagrant.container;

import static org.kafkaliu.test.vagrant.server.VagrantUtils.convertToGuestPaths;
import static org.kafkaliu.test.vagrant.server.VagrantUtils.getVagrantLog;
import static org.kafkaliu.test.vagrant.server.VagrantUtils.getVagrantfilePath;

import java.text.MessageFormat;
import java.util.Map;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.junit.Ignore;
import org.junit.runner.Description;
import org.junit.runner.notification.Failure;
import org.junit.runner.notification.RunNotifier;
import org.junit.runners.BlockJUnit4ClassRunner;
import org.junit.runners.model.FrameworkMethod;
import org.junit.runners.model.InitializationError;
import org.junit.runners.model.Statement;
import org.kafkaliu.test.vagrant.VagrantRunAfters;
import org.kafkaliu.test.vagrant.VagrantRunBefores;
import org.kafkaliu.test.vagrant.annotations.VagrantVirtualMachine;
import org.kafkaliu.test.vagrant.ruby.VagrantCli;
import org.kafkaliu.test.vagrant.ruby.VagrantEnvironment;

public class VagrantVirtualMachineTestRunner extends BlockJUnit4ClassRunner {

	private VagrantEnvironment vagrantEnv;
	
	private VagrantCli cli;

	private Class<?> klass;
	
	private String guestpath = "/vagrant-junit";

	public VagrantVirtualMachineTestRunner(Class<?> klass) throws InitializationError {
		super(klass);
		this.klass = klass;
		if (!isInVagrantVm()) {
			vagrantEnv = new VagrantEnvironment(getVagrantfilePath(klass),
					getVagrantLog(klass));
			cli = new VagrantCli(vagrantEnv);
		}
	}

	@Override
	protected Statement withBeforeClasses(Statement statement) {
		if (isInVagrantVm()) {
			return super.withBeforeClasses(statement);
		} else {
			return new VagrantRunBefores(statement, vagrantEnv, klass);
		}
	}

	@Override
	protected Statement withAfterClasses(Statement statement) {
		if (isInVagrantVm()) {
			return super.withAfterClasses(statement);
		} else {
			return new VagrantRunAfters(statement, vagrantEnv, klass);
		}
	}

	@Override
	protected void runChild(FrameworkMethod method, RunNotifier notifier) {
		Description description = describeChild(method);
		if (isInVagrantVm()) {
			super.runChild(method, notifier);
		} else {
			if (method.getAnnotation(Ignore.class) != null) {
				notifier.fireTestIgnored(description);
			} else {
				notifier.fireTestStarted(description);
				Map<String, Map<String, String>> testingResults = startTestingInVms(method.getName());

				boolean allSuccessful = true;
				for (String vmName : testingResults.keySet()) {
					Map<String, String> testingResult = testingResults.get(vmName);
					String stdout = testingResult.get("stdout");
//					String stderr = testingResult.get("stderr");
					JSONObject out = JSONObject.fromObject(stdout);
					System.out.print(out.get("out"));
					System.err.print(out.get("err"));
					
					if (!Boolean.valueOf(out.getString("wasSuccessful"))) {
						allSuccessful = false;
						StringBuffer stackTrace = new StringBuffer();
						for (Object object : (JSONArray) out.get("failures")) {
							stackTrace.append(((JSONObject) object).get("trace"));
						}
						notifier.fireTestFailure(new Failure(description, new RuntimeException(MessageFormat.format("Virtual machine {0} throws exception:\n{1}", vmName, stackTrace))));
					}
				}
				if (allSuccessful) {
					notifier.fireTestFinished(describeChild(method));
				}
			}
		}
	}
	
	private Map<String, Map<String, String>> startTestingInVms(String methodName) {
		String command = "java -Djava.library.path=" + convertToGuestPaths(System.getProperty("java.library.path"), guestpath) + " -Dvagrant.isinvm=true -cp " + convertToGuestPaths(System.getProperty("java.class.path"), guestpath) + " " + SingleJUnitTestRunner.class.getName() + " " + klass.getName() + "#" + methodName;
		return cli.ssh(getVirtualMachine(), command);
	}

	private String getVirtualMachine() {
		VagrantVirtualMachine vm = klass.getAnnotation(VagrantVirtualMachine.class);
		return vm == null ? null : vm.value();
	}
	
	private boolean isInVagrantVm() {
		return Boolean.valueOf(System.getProperty("vagrant.isinvm"));
	}
}
