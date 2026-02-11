# !/usr/bin/python

# hard cut the image
from PIL import Image


def get_square_coordinates(x, y, size):
    left = x
    right = x + size
    bottom = y + size
    top = y

    return left, top, right, bottom


pattern = [13, 9, 11, 12, 0, 3, 14, 6, 7, 2, 4, 10, 1, 8, 5, 15, 16, 17]

TILE_FOLDER = "./assets/raw/sprites/64x64/tiles/"
FILENAME_PREFIX = "test_terrain_"
TILESET = "./assets/raw/tiled/terrain/paint/grassset.png"
tile_width = 64
tile_count_x = 3
tile_count_y = 6

tileset_image = Image.open(TILESET)

x = 0
y = 0

for index, i in enumerate(pattern):

    x = index % tile_count_x * tile_width
    y = index // tile_count_x * tile_width

    left, top, right, bottom = get_square_coordinates(x, y, tile_width)
    sub_image = tileset_image.crop((left, top, right, bottom))
    LOCATION = TILE_FOLDER + FILENAME_PREFIX + str(i) + ".png"
    sub_image.save(LOCATION)
