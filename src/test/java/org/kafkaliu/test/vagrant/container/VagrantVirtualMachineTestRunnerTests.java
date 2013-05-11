package org.kafkaliu.test.vagrant.container;

import static org.junit.Assert.assertTrue;

import java.text.MessageFormat;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.kafkaliu.test.vagrant.server.annotations.VagrantConfigure;

@RunWith(VagrantVirtualMachineTestRunner.class)
@VagrantConfigure(vagrantfilePath = "src/test/resources/container", needDestroyVmAfterClassTest=true)
public class VagrantVirtualMachineTestRunnerTests {

	@Test
	public void testTrue() {
		System.out.println(MessageFormat.format("vagrant.isinvm={0}", System.getProperty("vagrant.isinvm")));
		assertTrue("Should be in the vm.", Boolean.valueOf(System.getProperty("vagrant.isinvm")));
	}
	
	@Test(expected=AssertionError.class)
	public void testException() {
		assertTrue(false);
	}

}
