# Manual installation steps

Based on Raspberry PI 4B

## Put the OS onto a SD-Card

 - To back up the SD-Card content as an image, Rufus is a valid option: <https://rufus.ie/de/>
 - With the Raspberry Pi Imager: <https://www.raspberrypi.com/software/>
 - Choose the default Option: 32Bit OS with Desktop
   - Add ssh key, SSID, user,... by pressing Ctrl + Shift + X
 - Power on


    $ sudo apt update
    $ sudo apt upgrade

## Fix Wifi connection issues

PI4 loses WiFi Connection

- https://forums.raspberrypi.com/viewtopic.php?t=278393&sid=2932da18f4c4dc58bc39dd206f2ffec8&start=25

Try to run

tvservice -o

That switches HDMI off

Or change firmware: https://github.com/raspberrypi/linux/issues/3849#issuecomment-736676729

    $ sudo mv /lib/firmware/brcm/brcmfmac43455-sdio.clm_blob{,.bak}
    $ sudo cp brcmfmac43456-sdio.clm_blob /lib/firmware/brcm/brcmfmac43455-sdio.clm_blob
    $ sudo reboot

## Waveshare capacitive touch display

- <https://www.waveshare.com/4.3inch-dsi-lcd.htm>
- <https://www.waveshare.com/wiki/4.3inch_DSI_LCD>

Open `$ sudo nano /boot/confix.txt` and search for `dtoverlay=vc4-kms-v3d`.
Comment it out or delete it: `#dtoverlay=vc4-kms-v3d`

## Java (17)

    $ sudo apt install openjdk-17-jdk

## WiringPi

    $ sudo apt-get --yes install git-core gcc make
    $ cd ~
    $ git clone https://github.com/WiringPi/WiringPi --branch master --single-branch wiringpi
    $ cd ~/wiringpi
    $ sudo ./build
    $ sudo reboot  (needed?)

## Remote I2C

### Enable I2C on raspbian

    $ sudo raspi-config 

"3 Interface Options" -> "P5 I2C" -> Enable

    $ i2cdetect 0
    $ i2cdetect 1

### Install nodeJs

  - [install like here (Using Debian, as root)](https://github.com/nodesource/distributions#using-debian-as-root-1)
  - Global and without nvm
  - &gt;= v16


    $ curl -fsSL https://deb.nodesource.com/setup_18.x | sudo -E bash - &&\
       sudo apt-get install -y nodejs


## Install remote-I2C

- install remote-I2c global `$ sudo npm -g i @zebrajaeger/remote-i2c`
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

Create the service file:

    $ nano /home/pi/ph.service

and fill with:

    [Unit]
    Description=Panohead service
    After=syslog.target network.target
    
    [Service]
    SuccessExitStatus=143
    User=pi
    Group=pi
    Environment=JAVA_HOME=/usr/lib/jvm/java-17-openjdk-armhf
    WorkingDirectory=/home/pi
    
    ExecStart=/home/pi/ph3-server-0.0.1-SNAPSHOT.jar
    ExecStop=/bin/kill -15 $MAINPID
    
    SuccessExitStatus=143
    Restart=always
    RestartSec=10
    
    [Install]
    WantedBy=multi-user.target

Create link to systemd

    $ sudo ln -s /home/pi/ph.service /etc/systemd/system/ph.service

Reload systemd, enable and start

    $ sudo systemctl daemon-reload
    $ sudo systemctl enable ph
    $ sudo systemctl start ph

reboot

    $ sudo reboot

# Browser

    $ nano /home/pi/browser.service

with content:

    [Unit]
    After=network.target ph
    
    [Service]
    User=pi
    Group=pi
    Environment=DISPLAY=:0
    ExecStart=/usr/bin/chromium-browser --disable-infobars --kiosk --start-maximized --display=:0 http://localhost:8080
    Restart=always
    RestartSec=10
    
    [Install]
    WantedBy=multi-user.target

and link to systemd:

    $ sudo ln -s /home/pi/browser.service /etc/systemd/system/browser.service    

Reload systemd, enable and start

    $ sudo systemctl daemon-reload
    $ sudo systemctl enable browser
    $ sudo systemctl start browser

# Deactivate the Screensaver

    $ sudo nano /etc/lightdm/lightdm.conf

add this in section `[Seat:*]`:

    xserver-command=X -s 0 -dpms
