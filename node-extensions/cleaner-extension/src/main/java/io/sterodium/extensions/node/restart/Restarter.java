package io.sterodium.extensions.node.restart;


public interface Restarter {

    boolean isApplicable();

    void restart() throws Exception;

}
