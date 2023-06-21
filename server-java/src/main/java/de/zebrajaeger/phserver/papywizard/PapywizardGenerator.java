package de.zebrajaeger.phserver.papywizard;

import de.zebrajaeger.phserver.data.PanoMatrix;

import java.util.ArrayList;
import java.util.List;

public class PapywizardGenerator {

    public Papywizard generate(PanoMatrix positions) {

        List<Pict> picts = new ArrayList<>();
        int id = 1;
        for (de.zebrajaeger.phserver.data.Position shotPosition : positions.asPositionList(false, true)) {
            Pict pict = new Pict();
            pict.setBracket(1);
            pict.setId(id);
            pict.setPosition(Position.of(shotPosition.getX(), shotPosition.getY()));
            picts.add(pict);

            ++id;
        }

        Shoot shot = new Shoot(picts);
        final Papywizard pw = new Papywizard();
        pw.getHeader().getGeneral().setComment("Made with Ph3");
        pw.setShoot(shot);

        return pw;
    }
}
