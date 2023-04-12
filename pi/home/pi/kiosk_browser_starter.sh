#!/bin/bash

xset s off
xset s noblank
xset -dpms
# unclutter -grab -visible &
chromium-browser http://localhost:8080 --start-fullscreen --kiosk --incognito --noerrdialogs --no-first-run
