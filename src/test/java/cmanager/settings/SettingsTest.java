package cmanager.settings;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/** Test the settings handler. */
public class SettingsTest {

    /** Test getting the string versions of the given valid keys. */
    @Test
    @DisplayName("Test the key getter with valid names")
    public void testKeyGetNameValid() {
        assertEquals("gcUsername", SettingsKey.GC_USERNAME.getNameString());
        assertEquals("locationList", SettingsKey.LOCATION_LIST.getNameString());
    }

    /** Test getting the default strings for the given valid keys. */
    @Test
    @DisplayName("Test the default string getter with valid names")
    public void testKeyGetDefaultStringValid() {
        assertEquals("", SettingsKey.GC_USERNAME.getDefaultString());
        assertNull(SettingsKey.LOCATION_LIST.getDefaultString());
    }
}
