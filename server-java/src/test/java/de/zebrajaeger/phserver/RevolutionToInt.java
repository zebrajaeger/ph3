package de.zebrajaeger.phserver;

import de.zebrajaeger.phserver.util.NumberConverter;
import org.junit.jupiter.api.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

public class RevolutionToInt {

    @Test
    public void foo() {
        // full int(32bit)
        // 0xffffffff = -1
        // 0x80000000 = -2147483648
        // 0x7fffffff = 2147483647
        // 0x00000000 = 0

        assertThat(NumberConverter.unsigned24ToSigned(0), is(0));
        assertThat(NumberConverter.unsigned24ToSigned(0xffffff), is(-1));
        assertThat(NumberConverter.unsigned24ToSigned(0xfffffe), is(-2));
    }
}
