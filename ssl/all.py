# -*- coding: utf-8 -*-

import json
import ca
import server
import sys
import os

with open("inventory.json", "r") as jsonfile:
    cfg = json.load(jsonfile)

if os.path.exists('ca'):
    if len(sys.argv) == 2 and sys.argv[1] =="--force":
        ca.generate(cfg)
    else:
        print("Directory 'ca' already exists.")
        print("No new CA Root certificate is generated.")
        print("If you want a new CA Root certificate, delete the 'ca' directory or use the --force option.")
else:
    print("Directory 'ca' does not exist. Create new CA Root certificate.")
    ca.generate(cfg)

for s in cfg['server']:
    server.generate(s, cfg)
