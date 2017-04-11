package rice.edu.system;

import org.hobbit.core.Constants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Field;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Entry point for Docker.
 *
 * @author Roman Katerinenko
 */
public class DebsParrotBenchmarkSystemRunner {
    private static final Logger logger = LoggerFactory.getLogger(DebsParrotBenchmarkSystemRunner.class);

    public static void set(Map<String, String> newenv) throws Exception {
        Class[] classes = Collections.class.getDeclaredClasses();
        Map<String, String> env = System.getenv();
        for(Class cl : classes) {
            if("java.util.Collections$UnmodifiableMap".equals(cl.getName())) {
                Field field = cl.getDeclaredField("m");
                field.setAccessible(true);
                Object obj = field.get(env);
                Map<String, String> map = (Map<String, String>) obj;
                map.clear();
                map.putAll(newenv);
            }
        }
    }


    private static void setupEnvironment() throws Exception {

        Map<String, String> env = new HashMap<>();

        env.put(Constants.SYSTEM_URI_KEY, "http://project-hobbit.eu/resources/debs2017/debsparrotsystemexample");
        env.put(Constants.RABBIT_MQ_HOST_NAME_KEY, "localhost");
        env.put(Constants.HOBBIT_SESSION_ID_KEY, "exp1");
        env.put(Constants.SYSTEM_PARAMETERS_MODEL_KEY,"{}");
        env.put(Constants.HOBBIT_EXPERIMENT_URI_KEY, "http://example.com/exp1");
        //env.put(Constants.RABBIT_MQ_HOST_NAME_KEY, "localhost");

        set(env);
    }

    
    public static void main(String... args) throws Exception {
        setupEnvironment();

        logger.debug("Running...");
        DebsParrotBenchmarkSystem system = null;
        try {
            system = new RiceBenchmarkSystem();
            system.init();
            system.run();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        finally {
            if (system != null) {
                system.close();
            }
        }
        logger.debug("Finished.");
    }
}
