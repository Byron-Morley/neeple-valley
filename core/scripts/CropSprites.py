# # !/usr/bin/python
#
# import os
#
# def main():
#     script_dir = os.path.dirname(os.path.abspath(__file__))
#     # Get the root directory (two levels up from the script)
#     root_dir = os.path.abspath(os.path.join(script_dir, '..', '..'))
#
#     SPRITE_FOLDER = os.path.join(root_dir,"core", "assets", "raw", "body")
#     OUTPUT_FOLDER = os.path.join(root_dir, "core", "assets", "raw", "sprites", "64x64", "body")
#
#     print(f"Processing sprites from {SPRITE_FOLDER}")
#     print(f"Output will be saved to {OUTPUT_FOLDER}")
#
#
#     for root, dirs, files in os.walk(SPRITE_FOLDER, topdown=False):
#         for name in files:
#
#             DIR = root.replace("/body/", "/sprites/body/")
#
#             if not os.path.exists(DIR):
#                 os.makedirs(DIR)
#
#             cmd = 'magick "' + root + "/" + name + '" -crop 64x64 "' + DIR + "/" + name.replace(".png", "") + '_%02d.png"'
#             print(cmd)
#             os.system(cmd)
#
#
# if __name__ == "__main__":

# !/usr/bin/python

import os
import sys

def main():
    # Get the directory where the script is located
    script_dir = os.path.dirname(os.path.abspath(__file__))
    # Get the root directory (two levels up from the script)
    root_dir = os.path.abspath(os.path.join(script_dir, '..', '..'))

    # Define paths relative to the root directory
    SPRITE_FOLDER = os.path.join(root_dir,"core", "assets", "raw", "body")
    OUTPUT_FOLDER = os.path.join(root_dir, "core", "assets", "raw", "sprites", "64x64", "body")

    print(f"Processing sprites from {SPRITE_FOLDER}")
    print(f"Output will be saved to {OUTPUT_FOLDER}")
    # Ensure the output directory exists
    if not os.path.exists(OUTPUT_FOLDER):
        os.makedirs(OUTPUT_FOLDER, exist_ok=True)

    # Walk through the sprite folder
    for root, dirs, files in os.walk(SPRITE_FOLDER, topdown=False):
        for name in files:
            # Create the corresponding output directory
            relative_path = os.path.relpath(root, SPRITE_FOLDER)
            output_dir = os.path.join(OUTPUT_FOLDER, relative_path)

            if not os.path.exists(output_dir):
                os.makedirs(output_dir, exist_ok=True)

            # Build the ImageMagick command
            input_file = os.path.join(root, name)
            output_pattern = os.path.join(output_dir, name.replace(".png", "") + "_%02d.png")

            cmd = f'magick "{input_file}" -crop 64x64 "{output_pattern}"'
            print(f"Executing: {cmd}")
            os.system(cmd)

if __name__ == "__main__":
    main()
