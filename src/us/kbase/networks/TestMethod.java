package us.kbase.networks;

import org.junit.runner.JUnitCore;
import org.junit.runner.Request;
import org.junit.runner.Result;

/**
 * Created by Marcin Joachimiak
 * User: marcin
 * Date: Dec 3, 2012
 * Time: 8:36:26 PM
 */
public class TestMethod {


    /**
     * @param args
     * @return
     * @throws ClassNotFoundException
     */
    public final static int test(String[] args) throws ClassNotFoundException {
        String[] classAndMethod = args[0].split("#");
        Request request = Request.method(Class.forName(classAndMethod[0]),
                classAndMethod[1]);

        Result result = new JUnitCore().run(request);
        return result.wasSuccessful() ? 0 : 1;
    }

    /**
     * @param args
     * @throws ClassNotFoundException
     */
    public static void main(String... args) throws ClassNotFoundException {
        String[] classAndMethod = args[0].split("#");
        Request request = Request.method(Class.forName(classAndMethod[0]),
                classAndMethod[1]);

        Result result = new JUnitCore().run(request);
        System.exit(result.wasSuccessful() ? 0 : 1);
    }
}



