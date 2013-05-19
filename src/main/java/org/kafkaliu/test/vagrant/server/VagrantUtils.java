package org.kafkaliu.test.vagrant.server;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import org.junit.runners.model.InitializationError;
import org.kafkaliu.test.vagrant.annotations.VagrantConfigure;

public class VagrantUtils {
	public static Map<String, String> generateHostGuestSharedFolderMapping(String paths, String guestPrefix) {
		return generateHostGuestSharedFolderMapping(paths.split(File.pathSeparator), guestPrefix);
	}
	
	public static Map<String, String> generateHostGuestSharedFolderMapping(String[] paths, String guestPrefix) {
		Map<String, String> mappings = new HashMap<String, String>();
		mappings.put(File.separator, guestPrefix + File.separator);
		return mappings;
	}
	
	public static String convertToGuestPaths(String paths, String guestPrefix) {
		String result = "";
		for (String path : convertToGuestPaths(paths.split(File.pathSeparator), guestPrefix)) {
			result += path + File.pathSeparator;
		}
		return result;
	}
	
	public static String[] convertToGuestPaths(String[] paths, String guestPrefix) {
		String[] guestPaths = new String[paths.length];
		for (int i = 0; i < paths.length; i++) {
			guestPaths[i] = guestPrefix + paths[i];//replacePathPrefix(paths[i], mapping);
		}
		return guestPaths;
	}

	public static File getVagrantfilePath(Class<?> klass)
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
