package de.zebrajaeger.phserver;

import de.zebrajaeger.phserver.data.Delay;
import de.zebrajaeger.phserver.data.FieldOfViewPartial;
import de.zebrajaeger.phserver.data.Image;
import de.zebrajaeger.phserver.data.Pano;
import de.zebrajaeger.phserver.data.Shot;
import de.zebrajaeger.phserver.pano.Command;
import de.zebrajaeger.phserver.pano.CommandListGenerator;
import org.junit.jupiter.api.Test;

import java.util.LinkedList;
import java.util.List;

public class GeneratorTest {

    @Test
    public void foo() {
        Image image = new Image(0.1, 0.1);
        Pano pano = new Pano(new FieldOfViewPartial(0, 0.5, 0, 0.5, true), 0.25, 0.25);
        List<Shot> shots = new LinkedList<>();
        shots.add(new Shot(1000, 1000));
        Delay delay = new Delay(0, 0, 0);

        CommandListGenerator generator = new CommandListGenerator(image, pano, shots, delay);
        List<Command> commands = generator.generate();
        for (Command c : commands) {
            System.out.println(c);
        }
    }
}
