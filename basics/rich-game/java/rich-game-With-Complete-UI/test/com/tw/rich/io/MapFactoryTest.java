package com.tw.rich.io;

import com.tw.rich.core.map.GameMap;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.*;

/**
 * Created by pzzheng on 12/6/16.
 */
public class MapFactoryTest {
    @Test
    public void defautlMapTest() {
        GameMap map = MapFactory.defaultMap();

        assertThat(map.getStarting(), is(notNullValue()));
        assertThat(map.getHospital(), is(notNullValue()));
    }
}