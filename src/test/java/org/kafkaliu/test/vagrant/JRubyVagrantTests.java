package org.kafkaliu.test.vagrant;

import java.io.File;

import org.junit.Test;
import org.kafkaliu.test.vagrant.ruby.VagrantCli;
import org.kafkaliu.test.vagrant.ruby.VagrantEnvironment;

public class JRubyVagrantTests {

	@Test
	public void test() {
		VagrantEnvironment vagrantEnv = new VagrantEnvironment(new File("src/test/resources/container"), null);
		VagrantCli vagrantCli = new VagrantCli(vagrantEnv);
		vagrantCli.up();
		System.out.println(vagrantCli.ssh("killall java"));
	}

}
