package io.sterodium.extensions.spi;

/**
 * @author Vladimir Ilyin ilyin371@gmail.com
 *         Date: 20/11/2015
 */
public interface GridConfigurator {
    /**
     * Updates command line arguments
     * @param args
     * @return updated arguments
     */
    String[] configure(String[] args);
}
