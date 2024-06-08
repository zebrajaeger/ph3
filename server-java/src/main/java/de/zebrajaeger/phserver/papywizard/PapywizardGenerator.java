package de.zebrajaeger.phserver.papywizard;

import de.zebrajaeger.phserver.data.PanoMatrix;
import de.zebrajaeger.phserver.data.PanoMatrixPosition;
import de.zebrajaeger.phserver.pano.Command;
import de.zebrajaeger.phserver.pano.TakeShotCommand;

import java.util.ArrayList;
import java.util.List;

public class PapywizardGenerator {

    public Papywizard generate(List<Command> commands) {
        Shoot shoot = new Shoot();

        for (Command command : commands) {
            if (command.getClass().equals(TakeShotCommand.class)) {
                TakeShotCommand takeShotCommand = (TakeShotCommand) command;
                Pict pict = new Pict();
                pict.setBracket(1);
                pict.setId(takeShotCommand.getId());
                final Position position = new Position();
                position.setX(takeShotCommand.getShotPosition().getX());
                position.setY(takeShotCommand.getShotPosition().getY());
                pict.setPosition(position);
                shoot.add(pict);

            }
        }

        final Papywizard pw = new Papywizard();
        pw.getHeader().getGeneral().setComment("Made with Ph3");
        pw.setShoot(shoot);

        return pw;
    }

    public Papywizard generate(PanoMatrix panoMatrix) {

        List<Pict> picts = new ArrayList<>();
        for (PanoMatrixPosition panoMatrixPosition : panoMatrix.asPositionList(false, true)) {
            Pict pict = new Pict();
            pict.setBracket(1);
            pict.setId(panoMatrixPosition.getId());
            pict.setPosition(Position.of(panoMatrixPosition.getX(), panoMatrixPosition.getY()));
            picts.add(pict);
        }

        Shoot shot = new Shoot(picts);
        final Papywizard pw = new Papywizard();
        pw.getHeader().getGeneral().setComment("Made with Ph3");
        pw.setShoot(shot);

        return pw;
    }
}
