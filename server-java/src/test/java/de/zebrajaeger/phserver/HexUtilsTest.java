package de.zebrajaeger.phserver;

import de.zebrajaeger.phserver.util.HexUtils;
import org.junit.jupiter.api.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

public class HexUtilsTest {
    @Test
    public void decodeHexString() {
        byte[] result = HexUtils.decodeHexString("ef012902");
        assertThat(result.length, is(4));
        assertThat(result[0] & 0xff, is(239));
        assertThat(result[1] & 0xff, is(1));
        assertThat(result[2] & 0xff, is(41));
        assertThat(result[3] & 0xff, is(2));
    }
}
