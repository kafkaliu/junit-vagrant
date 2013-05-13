package org.kafkaliu.test.vagrant.container;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.junit.runner.JUnitCore;
import org.junit.runner.Request;
import org.junit.runner.Result;
import org.junit.runner.notification.Failure;

public class SingleJUnitTestRunner {
	public static void main(String... args) throws ClassNotFoundException, IOException {
        String[] classAndMethod = args[0].split("#");
        Request request = Request.method(Class.forName(classAndMethod[0]),
                classAndMethod[1]);
        JUnitCore junit = new JUnitCore();
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        ByteArrayOutputStream err = new ByteArrayOutputStream();
        PrintStream oldOut = System.out;
        PrintStream oldErr = System.err;
        System.setOut(new PrintStream(out));
        System.setErr(new PrintStream(err));
        Result result = junit.run(request);
        JSONObject json = new JSONObject();
        json.put("out", out.toString());
        json.put("err", err.toString());
        json.put("failureCount", result.getFailureCount());
        json.put("ignoreCount", result.getIgnoreCount());
        json.put("runCount", result.getRunCount());
        json.put("runTime", result.getRunTime());
        json.put("wasSuccessful", result.wasSuccessful());
        
        JSONArray failures = new JSONArray();
        
        for (Failure failure : result.getFailures()) {
			JSONObject jsonFailure = new JSONObject();
			jsonFailure.put("message", failure.getMessage());
			jsonFailure.put("testHeader", failure.getTestHeader());
			jsonFailure.put("trace", failure.getTrace());
			failures.add(jsonFailure);
		}
        json.put("failures", failures);
        
        System.setOut(oldOut);
        System.setErr(oldErr);
        System.out.print(json);
    }
}
