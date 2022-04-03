#!/bin/bash

node -v > /home/pi/node.version.log
which node > /home/pi/node.path.log
/usr/bin/i2c-server -b 1 -v &> /home/pi/i2c-server.log