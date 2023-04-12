# PH

## Install required software at the Pi

[Klick here](./pi/install.md)

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

run *de.zebrajaeger.phserver.App* with profile "remote"

## Issues

PI4 loses WiFi Connection

- https://forums.raspberrypi.com/viewtopic.php?t=278393&sid=2932da18f4c4dc58bc39dd206f2ffec8&start=25

Try to run

   tvservice -o
   
That switches HDMI off

Or change firmware: https://github.com/raspberrypi/linux/issues/3849#issuecomment-736676729

## Electronic

### Driver

https://oshwlab.com/zebrajaeger/ph-pi

## Controller

Raspberry PI 4 with 5V Step Down converter and I2C level shifter 
