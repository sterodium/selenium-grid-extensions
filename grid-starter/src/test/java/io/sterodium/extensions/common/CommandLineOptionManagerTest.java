package io.sterodium.extensions.common;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;

import static org.junit.Assert.assertEquals;

@RunWith(Parameterized.class)
public class CommandLineOptionManagerTest {

    private CommandLineOptionManager manager;
    private String paramName;
    private String expected;
    private String message;

    public CommandLineOptionManagerTest(String args, String paramName, String expected, String message) {
        manager = new CommandLineOptionManager(args.split(" "));
        this.paramName = paramName;
        this.expected = expected;
        this.message = message;
    }

    @Parameterized.Parameters(name = "{index}: {3}")
    public static Collection<Object[]> data() {
        return Arrays.asList(new Object[][] {
                { "-p1 v1", "-p1", "", "Should remove named param and value"},
                { "-p1 v1 -p1 v1", "-p1", "", "Should remove duplicated"},
                { "-p0 v0 -p1 v1 -p2 v2", "-p1", "-p0 v0 -p2 v2", "Should leave other params intact"},
                { "-p1 v1", "-x1", "-p1 v1", "Should do nothing if named param not found"},
                { "", "-x1", "", "Should do nothing if param list is empty"},
                { "-p1 -p2 v2", "-p1", "-p2 v2", "Should remove key if value is missing"},
                { "-p1", "-p1", "", "Should remove key if value is missing"}
        });
    }

    @Test
    public void testRemoveParam() throws Exception {
        manager.removeParam(paramName);
        String actual = joinStrings(manager.getAllParams(), " ");
        assertEquals(message, expected, actual);
    }

    private static String joinStrings(String[] strings, String separator){
        Iterator<String> iter = Arrays.asList(strings).iterator();
        StringBuilder sb = new StringBuilder();
        if (iter.hasNext()) {
            sb.append(iter.next());
            while (iter.hasNext()) {
                sb.append(separator).append(iter.next());
            }
        }
        return sb.toString();
    }
}