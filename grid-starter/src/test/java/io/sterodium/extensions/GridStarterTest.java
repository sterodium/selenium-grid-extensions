package io.sterodium.extensions;

import org.junit.Test;
import org.slf4j.bridge.SLF4JBridgeHandler;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class GridStarterTest {

    @Test
    public void shouldInstallSLF4JBridge() throws Exception {
        assertFalse(SLF4JBridgeHandler.isInstalled());
        GridStarter.bridgeJulToSlf4j();
        assertTrue(SLF4JBridgeHandler.isInstalled());
    }
}