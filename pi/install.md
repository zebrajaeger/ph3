# Manual installation steps

## Remote I2C

### Enable I2C on raspbian

    $ sudo raspi-config

"3 Interface Options" -> "P5 I2C" -> Enable

### Install nodeJs

  - [install like here (Using Debian, as root)](https://github.com/nodesource/distributions#using-debian-as-root-1)
  - Global and without nvm
  - &gt;= v16
   
## Install remote-I2C

- install remote-I2c global `$ npm -g i @zebrajaeger/remote-i2c`
- copy this `/home/pi/i2c_server_starter.sh` to pi `/home/pi/i2c_server_starter.sh` 
- add execution rights `$ chmod +x i2c_server_starter.sh`
- edit crontab  `$ crontab -e` and add `@reboot /home/pi/i2c_server_starter.sh` to start the remote-i2c automatically
- reboot to test    
- i2c is now available via http

Example:    
Read [INA219](https://www.ti.com/lit/ds/symlink/ina219.pdf) registers.
Assuming the unit is configured at address 0x40, see Datasheet "8.5.5.1 Serial Bus Address".
At first, we write the register number that we want to access:
    `http://192.168.178.78:8079/write?address=0x40&data=2`    
Now we can read the 2 Bytes value: `http://192.168.178.78:8079/read?address=0x40&count=2`    

## Install the Applikation

Open `server-java/pom.xml` and search for the `maven-antrun-plugin` section.
Change the `remoteToFile` to your host address.
Into this project root run `$ mvn clean install`. This builds the client and the server and copies 
it to the target system. 

Autostart the application:

    $ chmod +x /home/pi/ph3-server-0.0.1-SNAPSHOT.jar
    $ sudo ln -s /home/pi/ph3-server-0.0.1-SNAPSHOT.jar /etc/init.d/ph3

reboot
