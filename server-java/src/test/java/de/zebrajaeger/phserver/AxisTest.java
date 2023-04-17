package de.zebrajaeger.phserver;

import de.zebrajaeger.phserver.service.AxisTranslatorService;
import org.junit.jupiter.api.Test;

class AxisTest {

  AxisTranslatorService axisTranslatorService = new AxisTranslatorService() {

    @Override
    public double rawToDeg(int rawValue) {
      return rawValue / 10d;
    }

    @Override
    public int degToRaw(double degValue) {
      return (int) (degValue * 10);
    }
  };

  @Test
  public void adaptOffset() {
    // TODO implementme

//    PanoHead ph = mock(PanoHead.class);
//    Axis a = new Axis(ph, 0, axisTranslatorService, false);
//
//    a.moveTo(10);
//    a.setRawValue(100);
//    verify(ph, times(1)).setTargetPos(0, 100);
//    assertThat(a.getOffsetRaw(), is(0));
//
//    a.moveTo(800);
//    a.setRawValue(8000);
//    verify(ph, times(1)).setTargetPos(0, 8000);
//    assertThat(a.getOffsetRaw(), is(0));
//
//    a.adaptOffset();
//    a.setRawValue(800);
//    verify(ph, times(1)).setActualAndTargetPos(0, 800);
//    assertThat(a.getOffsetRaw(), is(7200));
//
//    a.moveTo(1000);
//    a.setRawValue(2800);
//    verify(ph, times(1)).setTargetPos(0, 2800);
//    assertThat(a.getOffsetRaw(), is(7200));
  }

  @Test
  public void adaptOffsetInverted() {
    // TODO implementme

//    PanoHead ph = mock(PanoHead.class);
//    Axis a = new Axis(ph, 0, axisTranslatorService, true);
//
//    a.moveTo(-10);
//    a.setRawValue(100);
//    verify(ph, times(1)).setTargetPos(0, 100);
//    assertThat(a.getOffsetRaw(), is(0d));
//
//    a.moveTo(-800);
//    a.setRawValue(8000);
//    verify(ph, times(1)).setTargetPos(0, 8000);
//    assertThat(a.getOffsetRaw(), is(0d));
//
//    a.adaptOffset();
//    a.setRawValue(800);
//    verify(ph, times(1)).setActualAndTargetPos(0, 800);
//    assertThat(a.getOffsetRaw(), is(720d));
//
//    a.moveTo(-1000);
//    a.setRawValue(2800);
//    verify(ph, times(1)).setTargetPos(0, 2800);
//    assertThat(a.getOffsetRaw(), is(720d));
  }

}
