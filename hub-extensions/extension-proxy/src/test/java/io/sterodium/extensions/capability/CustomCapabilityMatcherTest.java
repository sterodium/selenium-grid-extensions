package io.sterodium.extensions.capability;

import org.junit.Before;
import org.junit.Test;
import org.openqa.selenium.Platform;
import org.openqa.selenium.remote.CapabilityType;

import java.util.HashMap;
import java.util.Map;

import static junit.framework.TestCase.assertFalse;
import static junit.framework.TestCase.assertTrue;


public class CustomCapabilityMatcherTest {

    private CustomCapabilityMatcher capabilityMatcher;

    @Before
    public void setUp() {
        capabilityMatcher = new CustomCapabilityMatcher();
    }

    @Test
    public void shouldReturnTrueWithDefaultCapabilities() {
        Map<String, Object> nodeCapabilities = nodeCapabilities();
        Map<String, Object> requestedCapabilities = requestedCapabilities();
        assertTrue(capabilityMatcher.matches(nodeCapabilities, requestedCapabilities));
    }

    @Test
    public void shouldReturnFalseWithDefaultCapabilitiesNotMatch() {
        Map<String, Object> nodeCapabilities = nodeCapabilities();
        Map<String, Object> requestedCapabilities = requestedCapabilities();
        requestedCapabilities.put(CapabilityType.VERSION, 10);

        assertFalse(capabilityMatcher.matches(nodeCapabilities, requestedCapabilities));
    }

    @Test
    public void shouldReturnFalseWithDefaultCapabilitiesNotMatch_2() {
        Map<String, Object> nodeCapabilities = nodeCapabilities();

        Map<String, Object> requestedCapabilities = requestedCapabilities();
        requestedCapabilities.put(CapabilityType.PLATFORM, Platform.VISTA);

        assertFalse(capabilityMatcher.matches(nodeCapabilities, requestedCapabilities));
    }

    @Test
    public void shouldReturnFalseWhenExtensionCapabilitiesAreNotFound() {
        Map<String, Object> nodeCapabilities = nodeCapabilities();

        Map<String, Object> requestedCapabilities = requestedCapabilities();
        requestedCapabilities.putAll(extensionCapabilities());

        assertFalse(capabilityMatcher.matches(nodeCapabilities, requestedCapabilities));
    }

    @Test
    public void shouldReturnFalseWhenExtensionCapabilitiesDoNotMatch() {
        Map<String, Object> nodeCapabilities = nodeCapabilities();
        nodeCapabilities.put(Capabilities.EXTENSION_PREFIX + "sikuliCapability", false);

        Map<String, Object> requestedCapabilities = requestedCapabilities();
        requestedCapabilities.put(Capabilities.EXTENSION_PREFIX + "sikuliCapability", true);

        assertFalse(capabilityMatcher.matches(nodeCapabilities, requestedCapabilities));
    }

    @Test
    public void shouldReturnFalseWhenOneExtensionCapabilityDoNotMatch() {
        Map<String, Object> nodeCapabilities = nodeCapabilities();
        nodeCapabilities.put(Capabilities.EXTENSION_PREFIX + "sikuliCapability", false);
        nodeCapabilities.put(Capabilities.EXTENSION_PREFIX + "ccc", SomeEnum.A);

        Map<String, Object> requestedCapabilities = requestedCapabilities();
        requestedCapabilities.put(Capabilities.EXTENSION_PREFIX + "sikuliCapability", true);
        requestedCapabilities.put(Capabilities.EXTENSION_PREFIX + "ccc", SomeEnum.B);

        assertFalse(capabilityMatcher.matches(nodeCapabilities, requestedCapabilities));
    }

    @Test
    public void shouldReturnTrueWhenNodeHasRequestedCapabilities() {
        Map<String, Object> nodeCapabilities = nodeCapabilities();
        nodeCapabilities.putAll(extensionCapabilities());

        Map<String, Object> requestedCapabilities = requestedCapabilities();
        requestedCapabilities.put(Capabilities.EXTENSION_PREFIX + "sikuliCapability", true);

        assertTrue(capabilityMatcher.matches(nodeCapabilities, requestedCapabilities));
    }

    @Test
    public void shouldReturnTrueWhenDefaultCapabilitiesMatchAndNodeHasExtensionCapabilities() {
        Map<String, Object> nodeCapabilities = nodeCapabilities();
        nodeCapabilities.putAll(extensionCapabilities());

        Map<String, Object> requestedCapabilities = requestedCapabilities();

        assertTrue(capabilityMatcher.matches(nodeCapabilities, requestedCapabilities));
    }

    private Map<String, Object> nodeCapabilities() {
        Map<String, Object> defaultCapabilities = new HashMap<>();
        defaultCapabilities.put("seleniumProtocol", "WebDriver");
        defaultCapabilities.put("maxInstances", 5);
        defaultCapabilities.put(CapabilityType.BROWSER_NAME, "firefox");
        defaultCapabilities.put(CapabilityType.PLATFORM, Platform.WINDOWS);
        return defaultCapabilities;
    }

    private Map<String, Object> extensionCapabilities() {
        Map<String, Object> customCapabilities = new HashMap<>();
        customCapabilities.put(Capabilities.EXTENSION_PREFIX + "sikuliCapability", true);
        customCapabilities.put(Capabilities.EXTENSION_PREFIX + "fileUploadCapability", false);
        customCapabilities.put(Capabilities.EXTENSION_PREFIX + "doNotFailWithExceptions", true);
        return customCapabilities;
    }

    private Map<String, Object> requestedCapabilities() {
        Map<String, Object> requestedCapabilities = new HashMap<>();
        requestedCapabilities.put(CapabilityType.BROWSER_NAME, "firefox");
        requestedCapabilities.put(CapabilityType.VERSION, null);
        requestedCapabilities.put(CapabilityType.PLATFORM, Platform.ANY);
        return requestedCapabilities;
    }

    private enum SomeEnum {
        A,
        B
    }
}
