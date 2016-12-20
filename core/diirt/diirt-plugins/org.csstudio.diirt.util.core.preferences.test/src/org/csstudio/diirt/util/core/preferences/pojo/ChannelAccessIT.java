/**
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 * Copyright (C) 2016 European Spallation Source ERIC.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.csstudio.diirt.util.core.preferences.pojo;


import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNull;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.nio.file.Files;

import javax.xml.bind.JAXBException;

import org.csstudio.diirt.util.core.preferences.DIIRTPreferences;
import org.csstudio.diirt.util.core.preferences.TestScope;
import org.csstudio.diirt.util.core.preferences.pojo.DataSourceOptions.MonitorMask;
import org.csstudio.diirt.util.core.preferences.pojo.DataSourceOptions.VariableArraySupport;
import org.hamcrest.core.StringStartsWith;
import org.junit.Test;


/**
 * @author claudiorosati, European Spallation Source ERIC
 * @version 1.0.0 19 Dec 2016
 */
public class ChannelAccessIT {

    @Test
    public final void testConstructors ( ) {

        ChannelAccess ca1 = new ChannelAccess();

        assertNull(ca1.dataSourceOptions);
        assertNull(ca1.jcaContext);

        DataSourceOptions dso1 = new DataSourceOptions(true, false, MonitorMask.ALARM, 234, true, VariableArraySupport.FALSE);
        JCAContext jcc1 = new JCAContext("fuffa", false, 23, 43.2, 12345, false, 23414, 23453);

        ca1.dataSourceOptions = dso1;
        ca1.jcaContext = jcc1;

        assertEquals(dso1,  ca1.dataSourceOptions);
        assertEquals(jcc1, ca1.jcaContext);

        ChannelAccess ca2 = new ChannelAccess(
            new DataSourceOptions(true, false, MonitorMask.ALARM, 234, true, VariableArraySupport.FALSE),
            new JCAContext("fuffa", false, 23, 43.2, 12345, false, 23414, 23453)
        );

        assertEquals(ca1, ca2);

        DIIRTPreferences store = new DIIRTPreferences(new TestScope());

        store.setDefaultBoolean(ChannelAccess.PREF_DBE_PROPERTY_SUPPORTED, true);
        store.setDefaultBoolean(ChannelAccess.PREF_HONOR_ZERO_PRECISION, false);
        store.setDefaultString(ChannelAccess.PREF_MONITOR_MASK, MonitorMask.ALARM.name());
        store.setDefaultInteger(ChannelAccess.PREF_CUSTOM_MASK, 234);
        store.setDefaultBoolean(ChannelAccess.PREF_VALUE_RTYP_MONITOR, true);
        store.setDefaultString(ChannelAccess.PREF_VARIABLE_LENGTH_ARRAY, VariableArraySupport.FALSE.representation());

        store.setString(ChannelAccess.PREF_ADDR_LIST, "fuffa");
        store.setBoolean(ChannelAccess.PREF_AUTO_ADDR_LIST, false);
        store.setDouble(ChannelAccess.PREF_BEACON_PERIOD, 23);
        store.setDouble(ChannelAccess.PREF_CONNECTION_TIMEOUT, 43.2);
        store.setInteger(ChannelAccess.PREF_MAX_ARRAY_SIZE, 12345);
        store.setBoolean(ChannelAccess.PREF_PURE_JAVA, false);
        store.setInteger(ChannelAccess.PREF_REPEATER_PORT, 23414);
        store.setInteger(ChannelAccess.PREF_SERVER_PORT, 23453);

        ChannelAccess ca3 = new ChannelAccess(store);

        assertEquals(ca2, ca3);

    }

    @Test
    public void testCopy ( ) {

        DIIRTPreferences source = new DIIRTPreferences(new TestScope());

        source.setDefaultBoolean(ChannelAccess.PREF_DBE_PROPERTY_SUPPORTED, true);
        source.setDefaultBoolean(ChannelAccess.PREF_HONOR_ZERO_PRECISION, false);
        source.setDefaultString(ChannelAccess.PREF_MONITOR_MASK, MonitorMask.ALARM.name());
        source.setDefaultInteger(ChannelAccess.PREF_CUSTOM_MASK, 234);
        source.setDefaultBoolean(ChannelAccess.PREF_VALUE_RTYP_MONITOR, true);
        source.setDefaultString(ChannelAccess.PREF_VARIABLE_LENGTH_ARRAY, VariableArraySupport.FALSE.representation());

        source.setDefaultString(ChannelAccess.PREF_ADDR_LIST, "fuffa");
        source.setDefaultBoolean(ChannelAccess.PREF_AUTO_ADDR_LIST, false);
        source.setDefaultDouble(ChannelAccess.PREF_BEACON_PERIOD, 23);
        source.setDefaultDouble(ChannelAccess.PREF_CONNECTION_TIMEOUT, 43.2);
        source.setDefaultInteger(ChannelAccess.PREF_MAX_ARRAY_SIZE, 12345);
        source.setDefaultBoolean(ChannelAccess.PREF_PURE_JAVA, false);
        source.setDefaultInteger(ChannelAccess.PREF_REPEATER_PORT, 23414);
        source.setDefaultInteger(ChannelAccess.PREF_SERVER_PORT, 23453);

        source.setBoolean(ChannelAccess.PREF_HONOR_ZERO_PRECISION, false);
        source.setString(ChannelAccess.PREF_MONITOR_MASK, MonitorMask.VALUE.name());
        source.setInteger(ChannelAccess.PREF_CUSTOM_MASK, 345);

        source.setString(ChannelAccess.PREF_ADDR_LIST, "farfalla");
        source.setDouble(ChannelAccess.PREF_BEACON_PERIOD, 12.345);
        source.setBoolean(ChannelAccess.PREF_PURE_JAVA, true);
        source.setInteger(ChannelAccess.PREF_SERVER_PORT, 34526);

        source.setDefaultString("fakeKey1", "fakeValue1");
        source.setString("fakeKey2", "fakeValue2");

        DIIRTPreferences destination = new DIIRTPreferences(new TestScope());

        ChannelAccess.copy(source, destination);

        assertEquals(true,                                        destination.getDefaultBoolean(ChannelAccess.PREF_DBE_PROPERTY_SUPPORTED));
        assertEquals(false,                                       destination.getDefaultBoolean(ChannelAccess.PREF_HONOR_ZERO_PRECISION));
        assertEquals(MonitorMask.ALARM.name(),                    destination.getDefaultString(ChannelAccess.PREF_MONITOR_MASK));
        assertEquals(234,                                         destination.getDefaultInteger(ChannelAccess.PREF_CUSTOM_MASK));
        assertEquals(true,                                        destination.getDefaultBoolean(ChannelAccess.PREF_VALUE_RTYP_MONITOR));
        assertEquals(VariableArraySupport.FALSE.representation(), destination.getDefaultString(ChannelAccess.PREF_VARIABLE_LENGTH_ARRAY));

        assertEquals("fuffa", destination.getDefaultString(ChannelAccess.PREF_ADDR_LIST));
        assertEquals(false,   destination.getDefaultBoolean(ChannelAccess.PREF_AUTO_ADDR_LIST));
        assertEquals(23,      destination.getDefaultDouble(ChannelAccess.PREF_BEACON_PERIOD),      0.00001);
        assertEquals(43.2,    destination.getDefaultDouble(ChannelAccess.PREF_CONNECTION_TIMEOUT), 0.00001);
        assertEquals(12345,   destination.getDefaultInteger(ChannelAccess.PREF_MAX_ARRAY_SIZE));
        assertEquals(false,   destination.getDefaultBoolean(ChannelAccess.PREF_PURE_JAVA));
        assertEquals(23414,   destination.getDefaultInteger(ChannelAccess.PREF_REPEATER_PORT));
        assertEquals(23453,   destination.getDefaultInteger(ChannelAccess.PREF_SERVER_PORT));

        assertEquals(true,                                        destination.getBoolean(ChannelAccess.PREF_DBE_PROPERTY_SUPPORTED));
        assertEquals(false,                                       destination.getBoolean(ChannelAccess.PREF_HONOR_ZERO_PRECISION));
        assertEquals(MonitorMask.VALUE.name(),                    destination.getString(ChannelAccess.PREF_MONITOR_MASK));
        assertEquals(345,                                         destination.getInteger(ChannelAccess.PREF_CUSTOM_MASK));
        assertEquals(true,                                        destination.getBoolean(ChannelAccess.PREF_VALUE_RTYP_MONITOR));
        assertEquals(VariableArraySupport.FALSE.representation(), destination.getString(ChannelAccess.PREF_VARIABLE_LENGTH_ARRAY));

        assertEquals("farfalla", destination.getString(ChannelAccess.PREF_ADDR_LIST));
        assertEquals(false,      destination.getBoolean(ChannelAccess.PREF_AUTO_ADDR_LIST));
        assertEquals(12.345,     destination.getDouble(ChannelAccess.PREF_BEACON_PERIOD),      0.00001);
        assertEquals(43.2,       destination.getDouble(ChannelAccess.PREF_CONNECTION_TIMEOUT), 0.00001);
        assertEquals(12345,      destination.getInteger(ChannelAccess.PREF_MAX_ARRAY_SIZE));
        assertEquals(true,       destination.getBoolean(ChannelAccess.PREF_PURE_JAVA));
        assertEquals(23414,      destination.getInteger(ChannelAccess.PREF_REPEATER_PORT));
        assertEquals(34526,      destination.getInteger(ChannelAccess.PREF_SERVER_PORT));

        assertNotEquals("fakeValue1", destination.getDefaultString("fakeKey1"));
        assertNotEquals("fakeValue2", destination.getString("fakeKey2"));

    }

    @Test
    public void testFromToFile ( ) throws IOException, JAXBException {

        File confDir = Files.createTempDirectory("diirt.test").toFile();
        ChannelAccess ca1 = new ChannelAccess(
            new DataSourceOptions(true, false, MonitorMask.ALARM, 234, true, VariableArraySupport.FALSE),
            new JCAContext("fuffa", false, 23, 43.2, 12345, false, 23414, 23453)
        );

        ca1.toFile(confDir);

        ChannelAccess ca2 = ChannelAccess.fromFile(confDir);

        assertEquals(ca1, ca2);

        confDir = Files.createTempDirectory("diirt.test").toFile();
        ca2.dataSourceOptions.dbePropertySupported = false;
        ca2.dataSourceOptions.varArraySupported = VariableArraySupport.TRUE.representation();
        ca2.jcaContext.addrList = "farfalla";
        ca2.jcaContext.maxArrayBytes = 45678;

        ca2.toFile(confDir);

        ChannelAccess ca3 = ChannelAccess.fromFile(confDir);

        assertEquals(ca2, ca3);
        assertNotEquals(ca1, ca3);

        confDir = Files.createTempDirectory("diirt.test").toFile();
        ca2.version = "1.3.42";

        ca2.toFile(confDir);

        try {
            ca3 = ChannelAccess.fromFile(confDir);
        } catch ( IOException ex ) {
            assertThat(ex.getMessage(), new StringStartsWith("Version mismatch:"));
        }

    }

    /**
     * This test is made to fail if the structure of {@link ChannelAccess}
     * changed, ensuring that also the test classes are changed too.
     */
    @Test
    public void testStructure ( ) throws NoSuchFieldException, SecurityException {

        Field[] fields = ChannelAccess.class.getDeclaredFields();

        //  First is the number of instance variables.
        //  Second is the number of static variables.
        assertEquals(3 + 17, fields.length);

        assertEquals(DataSourceOptions.class, ChannelAccess.class.getDeclaredField("dataSourceOptions").getType());
        assertEquals(JCAContext.class, ChannelAccess.class.getDeclaredField("jcaContext").getType());
        assertEquals(String.class, ChannelAccess.class.getDeclaredField("version").getType());

        assertEquals(String.class, ChannelAccess.class.getDeclaredField("PREF_ADDR_LIST").getType());
        assertEquals(String.class, ChannelAccess.class.getDeclaredField("PREF_AUTO_ADDR_LIST").getType());
        assertEquals(String.class, ChannelAccess.class.getDeclaredField("PREF_BEACON_PERIOD").getType());
        assertEquals(String.class, ChannelAccess.class.getDeclaredField("PREF_CONNECTION_TIMEOUT").getType());
        assertEquals(String.class, ChannelAccess.class.getDeclaredField("PREF_CUSTOM_MASK").getType());
        assertEquals(String.class, ChannelAccess.class.getDeclaredField("PREF_DBE_PROPERTY_SUPPORTED").getType());
        assertEquals(String.class, ChannelAccess.class.getDeclaredField("PREF_HONOR_ZERO_PRECISION").getType());
        assertEquals(String.class, ChannelAccess.class.getDeclaredField("PREF_MAX_ARRAY_SIZE").getType());
        assertEquals(String.class, ChannelAccess.class.getDeclaredField("PREF_MONITOR_MASK").getType());
        assertEquals(String.class, ChannelAccess.class.getDeclaredField("PREF_PURE_JAVA").getType());
        assertEquals(String.class, ChannelAccess.class.getDeclaredField("PREF_REPEATER_PORT").getType());
        assertEquals(String.class, ChannelAccess.class.getDeclaredField("PREF_SERVER_PORT").getType());
        assertEquals(String.class, ChannelAccess.class.getDeclaredField("PREF_VALUE_RTYP_MONITOR").getType());
        assertEquals(String.class, ChannelAccess.class.getDeclaredField("PREF_VARIABLE_LENGTH_ARRAY").getType());
        assertEquals(String.class, ChannelAccess.class.getDeclaredField("CA_DIR").getType());
        assertEquals(String.class, ChannelAccess.class.getDeclaredField("CA_FILE").getType());
        assertEquals(String.class, ChannelAccess.class.getDeclaredField("CA_VERSION").getType());

    }

    @Test
    public void testUpdate ( ) {

        DIIRTPreferences store = new DIIRTPreferences(new TestScope());

        store.setDefaultBoolean(ChannelAccess.PREF_DBE_PROPERTY_SUPPORTED, true);
        store.setDefaultBoolean(ChannelAccess.PREF_HONOR_ZERO_PRECISION, false);
        store.setDefaultString(ChannelAccess.PREF_MONITOR_MASK, MonitorMask.ALARM.name());
        store.setDefaultInteger(ChannelAccess.PREF_CUSTOM_MASK, 234);
        store.setDefaultBoolean(ChannelAccess.PREF_VALUE_RTYP_MONITOR, true);
        store.setDefaultString(ChannelAccess.PREF_VARIABLE_LENGTH_ARRAY, VariableArraySupport.FALSE.representation());

        store.setDefaultString(ChannelAccess.PREF_ADDR_LIST, "fuffa");
        store.setDefaultBoolean(ChannelAccess.PREF_AUTO_ADDR_LIST, true);
        store.setDefaultDouble(ChannelAccess.PREF_BEACON_PERIOD, 34.678);
        store.setDefaultDouble(ChannelAccess.PREF_CONNECTION_TIMEOUT, 7.6543);
        store.setDefaultInteger(ChannelAccess.PREF_MAX_ARRAY_SIZE, 65432);
        store.setDefaultBoolean(ChannelAccess.PREF_PURE_JAVA, false);
        store.setDefaultInteger(ChannelAccess.PREF_REPEATER_PORT, 34567);
        store.setDefaultInteger(ChannelAccess.PREF_SERVER_PORT, 34251);

        store.setBoolean(ChannelAccess.PREF_HONOR_ZERO_PRECISION, false);
        store.setString(ChannelAccess.PREF_MONITOR_MASK, MonitorMask.VALUE.name());
        store.setInteger(ChannelAccess.PREF_CUSTOM_MASK, 345);

        store.setString(ChannelAccess.PREF_ADDR_LIST, "farfalla");
        store.setDouble(ChannelAccess.PREF_BEACON_PERIOD, 12.345);
        store.setBoolean(ChannelAccess.PREF_PURE_JAVA, true);
        store.setInteger(ChannelAccess.PREF_SERVER_PORT, 34526);

        store.setDefaultString("fakeKey1", "fakeValue1");
        store.setString("fakeKey2", "fakeValue2");

        assertEquals(true,                                        store.getDefaultBoolean(ChannelAccess.PREF_DBE_PROPERTY_SUPPORTED));
        assertEquals(false,                                       store.getDefaultBoolean(ChannelAccess.PREF_HONOR_ZERO_PRECISION));
        assertEquals(MonitorMask.ALARM.name(),                    store.getDefaultString(ChannelAccess.PREF_MONITOR_MASK));
        assertEquals(234,                                         store.getDefaultInteger(ChannelAccess.PREF_CUSTOM_MASK));
        assertEquals(true,                                        store.getDefaultBoolean(ChannelAccess.PREF_VALUE_RTYP_MONITOR));
        assertEquals(VariableArraySupport.FALSE.representation(), store.getDefaultString(ChannelAccess.PREF_VARIABLE_LENGTH_ARRAY));

        assertEquals("fuffa", store.getDefaultString(ChannelAccess.PREF_ADDR_LIST));
        assertEquals(true,    store.getDefaultBoolean(ChannelAccess.PREF_AUTO_ADDR_LIST));
        assertEquals(34.678,  store.getDefaultDouble(ChannelAccess.PREF_BEACON_PERIOD),      0.00001);
        assertEquals(7.6543,  store.getDefaultDouble(ChannelAccess.PREF_CONNECTION_TIMEOUT), 0.00001);
        assertEquals(65432,   store.getDefaultInteger(ChannelAccess.PREF_MAX_ARRAY_SIZE));
        assertEquals(false,   store.getDefaultBoolean(ChannelAccess.PREF_PURE_JAVA));
        assertEquals(34567,   store.getDefaultInteger(ChannelAccess.PREF_REPEATER_PORT));
        assertEquals(34251,   store.getDefaultInteger(ChannelAccess.PREF_SERVER_PORT));

        assertEquals(true,                                        store.getBoolean(ChannelAccess.PREF_DBE_PROPERTY_SUPPORTED));
        assertEquals(false,                                       store.getBoolean(ChannelAccess.PREF_HONOR_ZERO_PRECISION));
        assertEquals(MonitorMask.VALUE.name(),                    store.getString(ChannelAccess.PREF_MONITOR_MASK));
        assertEquals(345,                                         store.getInteger(ChannelAccess.PREF_CUSTOM_MASK));
        assertEquals(true,                                        store.getBoolean(ChannelAccess.PREF_VALUE_RTYP_MONITOR));
        assertEquals(VariableArraySupport.FALSE.representation(), store.getString(ChannelAccess.PREF_VARIABLE_LENGTH_ARRAY));

        assertEquals("farfalla", store.getString(ChannelAccess.PREF_ADDR_LIST));
        assertEquals(true,       store.getBoolean(ChannelAccess.PREF_AUTO_ADDR_LIST));
        assertEquals(12.345,     store.getDouble(ChannelAccess.PREF_BEACON_PERIOD),      0.00001);
        assertEquals(7.6543,     store.getDouble(ChannelAccess.PREF_CONNECTION_TIMEOUT), 0.00001);
        assertEquals(65432,      store.getInteger(ChannelAccess.PREF_MAX_ARRAY_SIZE));
        assertEquals(true,       store.getBoolean(ChannelAccess.PREF_PURE_JAVA));
        assertEquals(34567,      store.getInteger(ChannelAccess.PREF_REPEATER_PORT));
        assertEquals(34526,      store.getInteger(ChannelAccess.PREF_SERVER_PORT));

        assertEquals("fakeValue1", store.getDefaultString("fakeKey1"));
        assertEquals("fakeValue2", store.getString("fakeKey2"));

        ChannelAccess ca1 = new ChannelAccess(
            new DataSourceOptions(true, false, MonitorMask.ALARM, 234, true, VariableArraySupport.FALSE),
            new JCAContext("foffi", false, 23, 43.2, 12345, false, 23414, 23453)
        );

        ca1.updateValues(store);

        assertEquals(true,                                        store.getDefaultBoolean(ChannelAccess.PREF_DBE_PROPERTY_SUPPORTED));
        assertEquals(false,                                       store.getDefaultBoolean(ChannelAccess.PREF_HONOR_ZERO_PRECISION));
        assertEquals(MonitorMask.ALARM.name(),                    store.getDefaultString(ChannelAccess.PREF_MONITOR_MASK));
        assertEquals(234,                                         store.getDefaultInteger(ChannelAccess.PREF_CUSTOM_MASK));
        assertEquals(true,                                        store.getDefaultBoolean(ChannelAccess.PREF_VALUE_RTYP_MONITOR));
        assertEquals(VariableArraySupport.FALSE.representation(), store.getDefaultString(ChannelAccess.PREF_VARIABLE_LENGTH_ARRAY));

        assertEquals("fuffa", store.getDefaultString(ChannelAccess.PREF_ADDR_LIST));
        assertEquals(true,    store.getDefaultBoolean(ChannelAccess.PREF_AUTO_ADDR_LIST));
        assertEquals(34.678,  store.getDefaultDouble(ChannelAccess.PREF_BEACON_PERIOD),      0.00001);
        assertEquals(7.6543,  store.getDefaultDouble(ChannelAccess.PREF_CONNECTION_TIMEOUT), 0.00001);
        assertEquals(65432,   store.getDefaultInteger(ChannelAccess.PREF_MAX_ARRAY_SIZE));
        assertEquals(false,   store.getDefaultBoolean(ChannelAccess.PREF_PURE_JAVA));
        assertEquals(34567,   store.getDefaultInteger(ChannelAccess.PREF_REPEATER_PORT));
        assertEquals(34251,   store.getDefaultInteger(ChannelAccess.PREF_SERVER_PORT));

        assertEquals(true,                                        store.getBoolean(ChannelAccess.PREF_DBE_PROPERTY_SUPPORTED));
        assertEquals(false,                                       store.getBoolean(ChannelAccess.PREF_HONOR_ZERO_PRECISION));
        assertEquals(MonitorMask.ALARM.name(),                    store.getString(ChannelAccess.PREF_MONITOR_MASK));
        assertEquals(5,                                           store.getInteger(ChannelAccess.PREF_CUSTOM_MASK));
        assertEquals(true,                                        store.getBoolean(ChannelAccess.PREF_VALUE_RTYP_MONITOR));
        assertEquals(VariableArraySupport.FALSE.representation(), store.getString(ChannelAccess.PREF_VARIABLE_LENGTH_ARRAY));

        assertEquals("foffi", store.getString(ChannelAccess.PREF_ADDR_LIST));
        assertEquals(false,   store.getBoolean(ChannelAccess.PREF_AUTO_ADDR_LIST));
        assertEquals(23,      store.getDouble(ChannelAccess.PREF_BEACON_PERIOD),      0.00001);
        assertEquals(43.2,    store.getDouble(ChannelAccess.PREF_CONNECTION_TIMEOUT), 0.00001);
        assertEquals(12345,   store.getInteger(ChannelAccess.PREF_MAX_ARRAY_SIZE));
        assertEquals(false,   store.getBoolean(ChannelAccess.PREF_PURE_JAVA));
        assertEquals(23414,   store.getInteger(ChannelAccess.PREF_REPEATER_PORT));
        assertEquals(23453,   store.getInteger(ChannelAccess.PREF_SERVER_PORT));

        assertEquals("fakeValue1", store.getDefaultString("fakeKey1"));
        assertEquals("fakeValue2", store.getString("fakeKey2"));

        ca1.updateDefaultsAndValues(store);

        assertEquals(true,                                        store.getDefaultBoolean(ChannelAccess.PREF_DBE_PROPERTY_SUPPORTED));
        assertEquals(false,                                       store.getDefaultBoolean(ChannelAccess.PREF_HONOR_ZERO_PRECISION));
        assertEquals(MonitorMask.ALARM.name(),                    store.getDefaultString(ChannelAccess.PREF_MONITOR_MASK));
        assertEquals(5,                                           store.getDefaultInteger(ChannelAccess.PREF_CUSTOM_MASK));
        assertEquals(true,                                        store.getDefaultBoolean(ChannelAccess.PREF_VALUE_RTYP_MONITOR));
        assertEquals(VariableArraySupport.FALSE.representation(), store.getDefaultString(ChannelAccess.PREF_VARIABLE_LENGTH_ARRAY));

        assertEquals("foffi", store.getDefaultString(ChannelAccess.PREF_ADDR_LIST));
        assertEquals(false,   store.getDefaultBoolean(ChannelAccess.PREF_AUTO_ADDR_LIST));
        assertEquals(23,      store.getDefaultDouble(ChannelAccess.PREF_BEACON_PERIOD),      0.00001);
        assertEquals(43.2,    store.getDefaultDouble(ChannelAccess.PREF_CONNECTION_TIMEOUT), 0.00001);
        assertEquals(12345,   store.getDefaultInteger(ChannelAccess.PREF_MAX_ARRAY_SIZE));
        assertEquals(false,   store.getDefaultBoolean(ChannelAccess.PREF_PURE_JAVA));
        assertEquals(23414,   store.getDefaultInteger(ChannelAccess.PREF_REPEATER_PORT));
        assertEquals(23453,   store.getDefaultInteger(ChannelAccess.PREF_SERVER_PORT));

        assertEquals(true,                                        store.getBoolean(ChannelAccess.PREF_DBE_PROPERTY_SUPPORTED));
        assertEquals(false,                                       store.getBoolean(ChannelAccess.PREF_HONOR_ZERO_PRECISION));
        assertEquals(MonitorMask.ALARM.name(),                    store.getString(ChannelAccess.PREF_MONITOR_MASK));
        assertEquals(5,                                           store.getInteger(ChannelAccess.PREF_CUSTOM_MASK));
        assertEquals(true,                                        store.getBoolean(ChannelAccess.PREF_VALUE_RTYP_MONITOR));
        assertEquals(VariableArraySupport.FALSE.representation(), store.getString(ChannelAccess.PREF_VARIABLE_LENGTH_ARRAY));

        assertEquals("foffi", store.getString(ChannelAccess.PREF_ADDR_LIST));
        assertEquals(false,   store.getBoolean(ChannelAccess.PREF_AUTO_ADDR_LIST));
        assertEquals(23,      store.getDouble(ChannelAccess.PREF_BEACON_PERIOD),      0.00001);
        assertEquals(43.2,    store.getDouble(ChannelAccess.PREF_CONNECTION_TIMEOUT), 0.00001);
        assertEquals(12345,   store.getInteger(ChannelAccess.PREF_MAX_ARRAY_SIZE));
        assertEquals(false,   store.getBoolean(ChannelAccess.PREF_PURE_JAVA));
        assertEquals(23414,   store.getInteger(ChannelAccess.PREF_REPEATER_PORT));
        assertEquals(23453,   store.getInteger(ChannelAccess.PREF_SERVER_PORT));

        assertEquals("fakeValue1", store.getDefaultString("fakeKey1"));
        assertEquals("fakeValue2", store.getString("fakeKey2"));

    }

    /**
     * This test is made to fail when {@link DataSources#DATASOURCES_VERSION} is updated.
     */
    @Test
    public void testVersion ( ) {
        assertEquals("1", new ChannelAccess().version);
    }

}
