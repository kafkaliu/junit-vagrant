package org.kafkaliu.test.vagrant.container;

import static org.junit.Assert.assertTrue;

import java.text.MessageFormat;

import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kafkaliu.test.vagrant.server.annotations.VagrantConfigure;

@RunWith(VagrantVirtualMachineTestRunner.class)
@VagrantConfigure(vagrantfilePath = "src/test/resources/container", needDestroyVmAfterClassTest=true)
public class VagrantVirtualMachineTestRunnerTests {

	@Test
	public void checkVmFlag() {
		System.out.println(MessageFormat.format("vagrant.isinvm={0}", System.getProperty("vagrant.isinvm")));
		assertTrue("Should be in the vm.", Boolean.valueOf(System.getProperty("vagrant.isinvm")));
	}
	
	@Ignore
	@Test
	public void checkClasspath() {
		String[] classpaths = System.getProperty("java.class.path")
				.split(System.getProperty("path.separator"));
		
		boolean found = false;
		for (String cp : classpaths) {
			if (cp.startsWith("/vagrant-junit")) {
				found = true;
				break;
			}
		}
		assertTrue("Can not found the classpaths in the vm.", found);
	}
	
//	@Test(expected=AssertionError.class)
	public void test2() {
		assertTrue(false);
	}

}
