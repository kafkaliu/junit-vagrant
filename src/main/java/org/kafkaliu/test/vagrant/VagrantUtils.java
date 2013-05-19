package org.kafkaliu.test.vagrant;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.junit.runners.model.InitializationError;
import org.kafkaliu.test.vagrant.annotations.VagrantConfigure;

public class VagrantUtils {
	private static Map<String, String> generateHostGuestSharedFolderMapping(String paths, String guestPrefix) {
		return generateHostGuestSharedFolderMapping(paths.split(File.pathSeparator), guestPrefix);
	}
	
	private static Map<String, String> generateHostGuestSharedFolderMapping(String[] paths, String guestPrefix) {
		//TODO: the only pattern implemented here is "/home/<user>" as the root folder and need be improved in future.
		String path = paths[0];
		Matcher userHome = Pattern.compile("/[^/]*/[^/]*").matcher(path);
		Map<String, String> mappings = new HashMap<String, String>();
		if (userHome.find()) {
			mappings.put(userHome.group(), guestPrefix + File.separator + "1");
		}
		return mappings;
	}
	
	private static String convertToGuestPaths(String paths, String guestPrefix) {
		String result = "";
		for (String path : convertToGuestPaths(paths.split(File.pathSeparator), guestPrefix)) {
			result += path + File.pathSeparator;
		}
		return result;
	}
	
	private static String[] convertToGuestPaths(String[] paths, String guestPrefix) {
		Map<String, String> mapping = generateHostGuestSharedFolderMapping(paths, guestPrefix);
		String[] guestPaths = new String[paths.length];
		for (int i = 0; i < paths.length; i++) {
			guestPaths[i] = replacePathPrefix(paths[i], mapping);
		}
		return guestPaths;
	}

	private static String replacePathPrefix(String path,
			Map<String, String> mapping) {
		for (String hostPrefix : mapping.keySet()) {
			if (path.contains(hostPrefix)) {
				return path.replace(hostPrefix, mapping.get(hostPrefix));
			}
		}
		return null;
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
