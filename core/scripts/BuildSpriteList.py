# !/usr/bin/python

SPRITE_FOLDER = "./assets/sprites/body/"
OUTPUT_FILE = "./assets/model/textures/sprites.json"

import os
import json

sprites = {}

for root, dirs, files in os.walk(SPRITE_FOLDER, topdown=False):
    for name in files:

        str = root + "/"+ name
        str = str.replace("./assets/", "")
        sprites[str] = {
            "regionSize": 64
        }


with open(OUTPUT_FILE, "w") as outfile:
    json.dump(sprites, outfile)
