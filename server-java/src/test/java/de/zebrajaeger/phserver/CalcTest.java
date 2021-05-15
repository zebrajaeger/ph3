package de.zebrajaeger.phserver;

import de.zebrajaeger.phserver.pano.Calc;
import org.junit.jupiter.api.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.closeTo;
import static org.hamcrest.Matchers.is;

public class CalcTest {
    @Test
    public void partial() {
        Calc calc = new Calc();
        calc.reset();
        calc.setPartial(true);
        calc.setOverlap(0.25);
        calc.setSourceSize(0.1);
        calc.setTargetStartPoint(0d);
        calc.setTargetSize(0.5d);
        Calc.Result result = calc.calc();
        assertThat(result.getN(), is(7));
        assertThat(result.getStartPositions().get(0), closeTo(0d, 0.001));
        assertThat(result.getStartPositions().get(1), closeTo(0.0666, 0.001));
        assertThat(result.getStartPositions().get(2), closeTo(0.1333, 0.001));
        assertThat(result.getStartPositions().get(3), closeTo(0.2, 0.001));
        assertThat(result.getStartPositions().get(4), closeTo(0.2666, 0.001));
        assertThat(result.getStartPositions().get(5), closeTo(0.333, 0.001));
        assertThat(result.getStartPositions().get(6), closeTo(0.4, 0.001));
    }

    @Test
    public void full() {
        Calc calc = new Calc();
        calc.reset();
        calc.setPartial(false);
        calc.setOverlap(0.25);
        calc.setSourceSize(0.1);
        calc.setTargetStartPoint(0d);
        calc.setTargetSize(1d);
        Calc.Result result = calc.calc();
        assertThat(result.getStartPositions().get(0), closeTo(0.0000, 0.001));
        assertThat(result.getStartPositions().get(1), closeTo(0.0714, 0.001));
        assertThat(result.getStartPositions().get(2), closeTo(0.1429, 0.001));
        assertThat(result.getStartPositions().get(3), closeTo(0.2143, 0.001));
        assertThat(result.getStartPositions().get(4), closeTo(0.2857, 0.001));
        assertThat(result.getStartPositions().get(5), closeTo(0.3571, 0.001));
        assertThat(result.getStartPositions().get(6), closeTo(0.4286, 0.001));
        assertThat(result.getStartPositions().get(7), closeTo(0.5000, 0.001));
        assertThat(result.getStartPositions().get(8), closeTo(0.5714, 0.001));
        assertThat(result.getStartPositions().get(9), closeTo(0.6429, 0.001));
        assertThat(result.getStartPositions().get(10), closeTo(0.7143, 0.001));
        assertThat(result.getStartPositions().get(11), closeTo(0.7857, 0.001));
        assertThat(result.getStartPositions().get(12), closeTo(0.8571, 0.001));
        assertThat(result.getStartPositions().get(13), closeTo(0.9286, 0.001));

//        int i=0;
//        for(double p : result.getStartPositions()){
//            System.out.printf("assertThat(result.getStartPositions().get(%d), closeTo(%.4f, 0.001));\n",i, p);
//            ++i;
//        }
    }
}
