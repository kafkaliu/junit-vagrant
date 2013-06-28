package org.kafkaliu.test.vagrant.server;

import org.junit.runners.model.InitializationError;
import org.kafkaliu.test.vagrant.annotations.VagrantConfigure;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

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
    if (result.endsWith(File.pathSeparator)) {
      result = result.substring(0, result.length() - 1);
    }
    return result;
  }

  public static String[] convertToGuestPaths(String[] paths, String guestPrefix) {
    String[] guestPaths = new String[paths.length];
    for (int i = 0; i < paths.length; i++) {
      guestPaths[i] = guestPrefix + getCanonicalPath(paths[i]);
    }
    return guestPaths;
  }

  private static String getCanonicalPath(String path) {
    /**
     * @author HUANGTA
     * 1. Convert '.' to canonical path
     * 2. Add wrapping double quotes ("), otherwise path with space won't be recognized by CLI.
     */
    assert null != path && !path.isEmpty();
    try {
      String canonicalPath = new File(path).getCanonicalPath();
      if (path.endsWith(File.separator)) {
        canonicalPath = canonicalPath + File.separator;
      }
      return canonicalPath.contains(" ") ? String.format("\"%s\"", canonicalPath) : canonicalPath;
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  public static File getVagrantfile(Class<?> klass) throws InitializationError {
    VagrantConfigure annotation = klass.getAnnotation(VagrantConfigure.class);
    File vagrantfile = annotation == null ? new File(".") : new File(annotation.vagrantfile());
    if (vagrantfile.exists()) {
      if (vagrantfile.isDirectory()) {
        File workingDir = vagrantfile;
        vagrantfile = new File(workingDir, "Vagrantfile");
        if (!vagrantfile.exists()) {
          vagrantfile = new File(workingDir, "vagrantfile");
        }
      }
      if (vagrantfile.exists()) {
        return vagrantfile;
      }
    }
    throw new InitializationError(String.format("class '%s' must have a valid VagrantfilePath", klass.getName()));
  }

  public static String getVagrantLog(Class<?> klass) {
    VagrantConfigure annotation = klass.getAnnotation(VagrantConfigure.class);
    return annotation == null ? null : annotation.vagrantLog();
  }
}
