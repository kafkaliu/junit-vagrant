package org.kafkaliu.test.vagrant;

import static org.kafkaliu.test.vagrant.server.VagrantUtils.*;

import java.io.File;
import java.util.Map;

import static org.junit.Assert.*;
import org.junit.Test;

public class VagrantUtilsTests {

	@Test
	public void testGenerateHostGuestSharedFolderMapping() {
		String[] classpaths = new String[] {
				"/media/kafkaliu/DATA/work/github/kafkaliu/junit-vagrant/target/test-classes", 
				"/media/kafkaliu/DATA/work/github/kafkaliu/junit-vagrant/target/classes", 
				"/home/kafkaliu/.m2/repository/junit/junit/4.11/junit-4.11.jar", 
				"/home/kafkaliu/.m2/repository/org/hamcrest/hamcrest-core/1.3/hamcrest-core-1.3.jar", 
				"/home/kafkaliu/.m2/repository/org/jruby/jruby-complete/1.7.3/jruby-complete-1.7.3.jar", 
				"/home/kafkaliu/.m2/repository/com/hazelcast/hazelcast-all/2.5/hazelcast-all-2.5.jar", 
				"/home/kafkaliu/.m2/repository/org/eclipse/jetty/aggregate/jetty-all/8.1.10.v20130312/jetty-all-8.1.10.v20130312.jar", 
				"/home/kafkaliu/.m2/repository/org/eclipse/jetty/orbit/javax.servlet/3.0.0.v201112011016/javax.servlet-3.0.0.v201112011016.jar", 
				"/home/kafkaliu/.m2/repository/org/springframework/spring-context/3.2.2.RELEASE/spring-context-3.2.2.RELEASE.jar", 
				"/home/kafkaliu/.m2/repository/org/springframework/spring-aop/3.2.2.RELEASE/spring-aop-3.2.2.RELEASE.jar", 
				"/home/kafkaliu/.m2/repository/aopalliance/aopalliance/1.0/aopalliance-1.0.jar", 
				"/home/kafkaliu/.m2/repository/org/springframework/spring-beans/3.2.2.RELEASE/spring-beans-3.2.2.RELEASE.jar", 
				"/home/kafkaliu/.m2/repository/org/springframework/spring-core/3.2.2.RELEASE/spring-core-3.2.2.RELEASE.jar", 
				"/home/kafkaliu/.m2/repository/commons-logging/commons-logging/1.1.1/commons-logging-1.1.1.jar", 
				"/home/kafkaliu/.m2/repository/org/springframework/spring-expression/3.2.2.RELEASE/spring-expression-3.2.2.RELEASE.jar", 
				"/home/kafkaliu/.m2/repository/org/springframework/spring-context-support/3.2.2.RELEASE/spring-context-support-3.2.2.RELEASE.jar", 
				"/home/kafkaliu/.m2/repository/org/springframework/spring-test/3.2.2.RELEASE/spring-test-3.2.2.RELEASE.jar", 
				"/home/kafkaliu/eclipse/configuration/org.eclipse.osgi/bundles/166/1/.cp/", 
				"/home/kafkaliu/eclipse/configuration/org.eclipse.osgi/bundles/164/1/.cp/", 
				"/home/kafkaliu/eclipse/configuration/org.eclipse.osgi/bundles/165/1/.cp/", 
		};
		String prefix = File.separator + "vagrant-junit-classpath";
		Map<String, String> mapping = generateHostGuestSharedFolderMapping(classpaths, prefix);
		assertEquals(1, mapping.size());
		assertEquals(prefix + File.separator, mapping.get("/"));
	}

	@Test
	public void testGenerateGuestPath() {
		String[] classpaths = new String[] {
				"/media/kafkaliu/DATA/work/github/kafkaliu/junit-vagrant/target/test-classes", 
				"/media/kafkaliu/DATA/work/github/kafkaliu/junit-vagrant/target/classes", 
				"/home/kafkaliu/.m2/repository/junit/junit/4.11/junit-4.11.jar", 
				"/home/kafkaliu/.m2/repository/org/hamcrest/hamcrest-core/1.3/hamcrest-core-1.3.jar", 
				"/home/kafkaliu/.m2/repository/org/jruby/jruby-complete/1.7.3/jruby-complete-1.7.3.jar", 
				"/home/kafkaliu/.m2/repository/com/hazelcast/hazelcast-all/2.5/hazelcast-all-2.5.jar", 
				"/home/kafkaliu/.m2/repository/org/eclipse/jetty/aggregate/jetty-all/8.1.10.v20130312/jetty-all-8.1.10.v20130312.jar", 
				"/home/kafkaliu/.m2/repository/org/eclipse/jetty/orbit/javax.servlet/3.0.0.v201112011016/javax.servlet-3.0.0.v201112011016.jar", 
				"/home/kafkaliu/.m2/repository/org/springframework/spring-context/3.2.2.RELEASE/spring-context-3.2.2.RELEASE.jar", 
				"/home/kafkaliu/.m2/repository/org/springframework/spring-aop/3.2.2.RELEASE/spring-aop-3.2.2.RELEASE.jar", 
				"/home/kafkaliu/.m2/repository/aopalliance/aopalliance/1.0/aopalliance-1.0.jar", 
				"/home/kafkaliu/.m2/repository/org/springframework/spring-beans/3.2.2.RELEASE/spring-beans-3.2.2.RELEASE.jar", 
				"/home/kafkaliu/.m2/repository/org/springframework/spring-core/3.2.2.RELEASE/spring-core-3.2.2.RELEASE.jar", 
				"/home/kafkaliu/.m2/repository/commons-logging/commons-logging/1.1.1/commons-logging-1.1.1.jar", 
				"/home/kafkaliu/.m2/repository/org/springframework/spring-expression/3.2.2.RELEASE/spring-expression-3.2.2.RELEASE.jar", 
				"/home/kafkaliu/.m2/repository/org/springframework/spring-context-support/3.2.2.RELEASE/spring-context-support-3.2.2.RELEASE.jar", 
				"/home/kafkaliu/.m2/repository/org/springframework/spring-test/3.2.2.RELEASE/spring-test-3.2.2.RELEASE.jar", 
				"/home/kafkaliu/eclipse/configuration/org.eclipse.osgi/bundles/166/1/.cp/", 
				"/home/kafkaliu/eclipse/configuration/org.eclipse.osgi/bundles/164/1/.cp/", 
				"/home/kafkaliu/eclipse/configuration/org.eclipse.osgi/bundles/165/1/.cp/", 
		};
		String[] guestPaths = new String[] {
				"/vagrant-junit-classpath/media/kafkaliu/DATA/work/github/kafkaliu/junit-vagrant/target/test-classes", 
				"/vagrant-junit-classpath/media/kafkaliu/DATA/work/github/kafkaliu/junit-vagrant/target/classes", 
				"/vagrant-junit-classpath/home/kafkaliu/.m2/repository/junit/junit/4.11/junit-4.11.jar", 
				"/vagrant-junit-classpath/home/kafkaliu/.m2/repository/org/hamcrest/hamcrest-core/1.3/hamcrest-core-1.3.jar", 
				"/vagrant-junit-classpath/home/kafkaliu/.m2/repository/org/jruby/jruby-complete/1.7.3/jruby-complete-1.7.3.jar", 
				"/vagrant-junit-classpath/home/kafkaliu/.m2/repository/com/hazelcast/hazelcast-all/2.5/hazelcast-all-2.5.jar", 
				"/vagrant-junit-classpath/home/kafkaliu/.m2/repository/org/eclipse/jetty/aggregate/jetty-all/8.1.10.v20130312/jetty-all-8.1.10.v20130312.jar", 
				"/vagrant-junit-classpath/home/kafkaliu/.m2/repository/org/eclipse/jetty/orbit/javax.servlet/3.0.0.v201112011016/javax.servlet-3.0.0.v201112011016.jar", 
				"/vagrant-junit-classpath/home/kafkaliu/.m2/repository/org/springframework/spring-context/3.2.2.RELEASE/spring-context-3.2.2.RELEASE.jar", 
				"/vagrant-junit-classpath/home/kafkaliu/.m2/repository/org/springframework/spring-aop/3.2.2.RELEASE/spring-aop-3.2.2.RELEASE.jar", 
				"/vagrant-junit-classpath/home/kafkaliu/.m2/repository/aopalliance/aopalliance/1.0/aopalliance-1.0.jar", 
				"/vagrant-junit-classpath/home/kafkaliu/.m2/repository/org/springframework/spring-beans/3.2.2.RELEASE/spring-beans-3.2.2.RELEASE.jar", 
				"/vagrant-junit-classpath/home/kafkaliu/.m2/repository/org/springframework/spring-core/3.2.2.RELEASE/spring-core-3.2.2.RELEASE.jar", 
				"/vagrant-junit-classpath/home/kafkaliu/.m2/repository/commons-logging/commons-logging/1.1.1/commons-logging-1.1.1.jar", 
				"/vagrant-junit-classpath/home/kafkaliu/.m2/repository/org/springframework/spring-expression/3.2.2.RELEASE/spring-expression-3.2.2.RELEASE.jar", 
				"/vagrant-junit-classpath/home/kafkaliu/.m2/repository/org/springframework/spring-context-support/3.2.2.RELEASE/spring-context-support-3.2.2.RELEASE.jar", 
				"/vagrant-junit-classpath/home/kafkaliu/.m2/repository/org/springframework/spring-test/3.2.2.RELEASE/spring-test-3.2.2.RELEASE.jar", 
				"/vagrant-junit-classpath/home/kafkaliu/eclipse/configuration/org.eclipse.osgi/bundles/166/1/.cp/", 
				"/vagrant-junit-classpath/home/kafkaliu/eclipse/configuration/org.eclipse.osgi/bundles/164/1/.cp/", 
				"/vagrant-junit-classpath/home/kafkaliu/eclipse/configuration/org.eclipse.osgi/bundles/165/1/.cp/", 
		};
		
		String prefix = File.separator + "vagrant-junit-classpath";
		assertArrayEquals(guestPaths, convertToGuestPaths(classpaths, prefix));
	}

}
