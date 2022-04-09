# PH

## Aktive parts

- client-ng
- server-java

## Prod

run *de.zebrajaeger.phserver.App* with profile "pi" or without a profile (what means "default")

## Develop

### Client

    npm start

### Server with mocked hardware

run *de.zebrajaeger.phserver.App* with profile "develop"

### Server with remote Hardware 

On pi:
- enable i2c
- install nodejs global (without nvm)
  - https://github.com/nodesource/distributions


    curl -fsSL https://deb.nodesource.com/setup_16.x | bash -
    apt-get install -y nodejs

    npm -g i @zebrajaeger/remote-i2c

write into ~/starter.sh

    /usr/bin/i2c-server -b 1 -v &> /home/pi/i2c-server.log

then

    chmod +x starter.sh

Execute starter.sh @ boot

    crontab -e

insert

    @reboot /home/pi/starter.sh

reboot

    sudo reboot

i2c is now available via http 

run *de.zebrajaeger.phserver.App* with profile "remote"

## Issues

PI4 loses WiFi Connection

- https://forums.raspberrypi.com/viewtopic.php?t=278393&sid=2932da18f4c4dc58bc39dd206f2ffec8&start=25

Try to run

   tvservice -o
   
That switches HDMI off