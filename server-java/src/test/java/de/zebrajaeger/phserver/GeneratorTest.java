package de.zebrajaeger.phserver;

import de.zebrajaeger.phserver.data.DelaySettings;
import de.zebrajaeger.phserver.data.FieldOfView;
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
        Pano pano = new Pano(new FieldOfView(0, 0.5, 0, 0.5), 0.25, 0.25);
        List<Shot> shots = new LinkedList<>();
        shots.add(new Shot(1000, 1000));
        DelaySettings delaySettings = new DelaySettings(0, 0, 0);

        CommandListGenerator generator = new CommandListGenerator(image, pano, shots, delaySettings);
        List<Command> commands = generator.generate();
        for(Command c : commands){
            System.out.println(c);
        }
    }
}
