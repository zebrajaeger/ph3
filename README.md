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

## Debug

    java -agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=*:5005 -jar ph3-server-0.0.1-SNAPSHOT.jar

## Electronic

### Driver

https://oshwlab.com/zebrajaeger/ph-pi

## Controller

Raspberry PI 4 with 5V Step Down converter and I2C level shifter 
