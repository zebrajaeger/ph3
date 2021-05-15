package de.zebrajaeger.phserver.data;

import static org.junit.jupiter.api.Assertions.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

class AutoRangeTest {

    @org.junit.jupiter.api.Test
    void setRawValueAsCenter() {
    }

    @org.junit.jupiter.api.Test
    void updateAutoRangeWithRawValue() {
        AutoRange ar = createAutoRange();

        assertThat(ar.getRawCenter(), is(0f));
        assertThat(ar.getRawMin(), is(-5f));
        assertThat(ar.getRawMax(), is(10f));
    }

    @org.junit.jupiter.api.Test
    void rawValueToRange() {
        AutoRange ar = createAutoRange();
        assertThat(ar.rawValueToRange(0f), is(0f));
        assertThat(ar.rawValueToRange(-2.5f), is(-0.5f));
        assertThat(ar.rawValueToRange(-5f), is(-1f));
        assertThat(ar.rawValueToRange(5f), is(0.5f));
        assertThat(ar.rawValueToRange(10f), is(1f));
    }

    private AutoRange createAutoRange() {
        AutoRange ar = new AutoRange();
        ar.updateAutoRangeWithRawValue(10f);
        ar.updateAutoRangeWithRawValue(-5f);
        return ar;
    }

}
