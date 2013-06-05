package org.kafkaliu.test.vagrant.container;

import static org.junit.Assert.assertTrue;

import java.text.MessageFormat;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.kafkaliu.test.vagrant.annotations.VagrantConfigure;
import org.kafkaliu.test.vagrant.annotations.VagrantVirtualMachine;

@RunWith(VagrantVirtualMachineTestRunner.class)
@VagrantConfigure(vagrantfile = "src/test/resources/container", needDestroyVmAfterClassTest = true)
@VagrantVirtualMachine("default")
public class VagrantVirtualMachineTestRunnerTests {

  @Test
  public void testTrue() {
    System.out.println(MessageFormat.format("vagrant.isinvm={0}", System.getProperty("vagrant.isinvm")));
    assertTrue("Should be in the vm.", Boolean.valueOf(System.getProperty("vagrant.isinvm")));
  }

  @Test(expected = AssertionError.class)
  public void testException() {
    assertTrue(false);
  }

}
