#!/bin/bash

node -v > /home/pi/node.version.log
which node > /home/pi/node.path.log
/usr/bin/i2c-server -b 1 -v -p 8079 &> /home/pi/i2c-server.log
