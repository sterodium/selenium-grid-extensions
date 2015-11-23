package io.sterodium.extensions.common;

import org.openqa.grid.common.CommandLineOptionHelper;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;

/**
* @author Vladimir Ilyin ilyin371@gmail.com
*         Date: 20/11/2015
*/
public class CommandLineOptionManager {

    private CommandLineOptionHelper helper;
    private String[] args;

    public CommandLineOptionManager(String[] args) {
        setArgs(args);
    }

    public void removeParam(String name) {
        ArrayList<String> params = new ArrayList<>();
        Collections.addAll(params, getAllParams());

        for (Iterator<String> iterator = params.iterator(); iterator.hasNext(); ) {
            String param = iterator.next();
            if (name.equals(param)) {
                removeParamWithValue(iterator);
            }
        }
        setArgs(params.toArray(new String[params.size()]));
    }

    private static void removeParamWithValue(Iterator<String> iterator) {
        iterator.remove();
        if (iterator.hasNext()) {
            String value = iterator.next();
            if (!value.startsWith("-")) {
                iterator.remove();
            }
        }
    }

    public boolean isParamPresent(String name) {
        return helper.isParamPresent(name);
    }

    public String getParamValue(String name) {
        return helper.getParamValue(name);
    }

    public String[] getAllParams() {
        return args;
    }

    private void setArgs(String[] args) {
        this.args = args;
        helper = new CommandLineOptionHelper(args);
    }

}
