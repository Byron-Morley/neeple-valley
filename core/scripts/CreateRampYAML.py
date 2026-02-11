#!/usr/bin/python

import os
import sys
import glob
from PIL import Image


def is_black_or_transparent(color):
    """Check if a color is black or transparent"""
    if len(color) == 4:  # RGBA
        r, g, b, a = color
        return (r, g, b) == (0, 0, 0) or a == 0 or (r, g, b) == (24, 24, 24)
    else:  # RGB
        r, g, b = color
        return (r, g, b) == (0, 0, 0) or (r, g, b) == (24, 24, 24)


def clean_yaml_files(ramps_dir):
    """Delete all YAML files in the ramps directory"""
    yaml_files = glob.glob(os.path.join(ramps_dir, "*.yaml"))
    yaml_files.extend(glob.glob(os.path.join(ramps_dir, "*.yml")))

    if not yaml_files:
        print(f"No YAML files found in {ramps_dir} to clean")
        return

    count = 0
    for yaml_file in yaml_files:
        try:
            os.remove(yaml_file)
            count += 1
        except Exception as e:
            print(f"Error deleting {yaml_file}: {e}")

    print(f"Cleaned up {count} YAML files from {ramps_dir}")


def process_ramp_image(input_image_path, output_yaml_path):
    """Process a single ramp image and create a YAML file"""
    print(f"Processing color ramp from {input_image_path}")
    print(f"Output will be saved to {output_yaml_path}")

    # Load the image
    try:
        image = Image.open(input_image_path)
    except Exception as e:
        print(f"Error opening the image: {e}")
        return 0

    # Convert image to RGB if it's not already
    if image.mode != 'RGB' and image.mode != 'RGBA':
        image = image.convert('RGBA')

    # Get image dimensions
    width, height = image.size

    # Ramp counter
    ramp_count = 0

    # Write YAML file manually
    with open(output_yaml_path, 'w') as yaml_file:
        # Write the opening of the array
        yaml_file.write("---\n")

        # Process only odd-indexed rows (every other row, starting from 0)
        for row in range(0, height, 2):
            row_colors = []
            has_transparent = False

            # Check each pixel in the row
            for col in range(width):
                pixel = image.getpixel((col, row))

                # Check if pixel is transparent
                if len(pixel) == 4 and pixel[3] == 0:
                    has_transparent = True
                    break

                # Skip black, transparent, or (24, 24, 24) pixels
                if not is_black_or_transparent(pixel):
                    # If it's RGBA, convert to RGB by removing alpha
                    if len(pixel) == 4:
                        pixel = pixel[:3]

                    # Add color if it's not already in the list
                    if pixel not in row_colors:
                        row_colors.append(pixel)

            # If we found colors in this row, add them to the YAML file
            if row_colors:
                ramp_count += 1
                yaml_file.write(f"- # ramp {ramp_count}\n")  # Start a new array item with a comment
                for r, g, b in row_colors:
                    yaml_file.write(f"  - r: {int(r)}\n")
                    yaml_file.write(f"    g: {int(g)}\n")
                    yaml_file.write(f"    b: {int(b)}\n")

            # If we hit a transparent pixel, stop processing
            if has_transparent:
                break

    print(f"Successfully wrote {ramp_count} color ramps to {output_yaml_path}")
    return ramp_count


def create_ramps_index(ramps_dir, yaml_files):
    """Create a ramps.yaml index file with all the generated YAML files"""
    ramps_index_path = os.path.join(ramps_dir, "ramps.yaml")
    print(f"Creating ramps index at {ramps_index_path}")

    with open(ramps_index_path, 'w') as index_file:
        # For each YAML file, create a relative path entry
        for yaml_file in yaml_files:
            # Get the relative path from the assets directory
            # The format should be: assets/raw/ramps/filename.yaml
            file_name = os.path.basename(yaml_file)
            relative_path = f"assets/raw/ramps/{file_name}"
            index_file.write(f"- {relative_path}\n")

    print(f"Successfully created ramps index with {len(yaml_files)} entries")


def main():
    # Get the directory where the script is located
    script_dir = os.path.dirname(os.path.abspath(__file__))
    # Get the root directory (two levels up from the script)
    root_dir = os.path.abspath(os.path.join(script_dir, '..', '..'))

    # Define the ramps directory path
    ramps_dir = os.path.join(root_dir, "core", "assets", "raw", "ramps")

    # Clean up existing YAML files first
    clean_yaml_files(ramps_dir)

    # Get all PNG files in the ramps directory
    png_files = glob.glob(os.path.join(ramps_dir, "*.png"))

    if not png_files:
        print(f"No PNG files found in {ramps_dir}")
        return

    print(f"Found {len(png_files)} PNG files in the ramps directory")

    # Process each PNG file
    total_ramps = 0
    generated_yamls = []

    for png_file in png_files:
        # Get the file name without extension
        file_name = os.path.basename(png_file)
        file_base = os.path.splitext(file_name)[0]

        # Remove "_ramp" from the file base name if it exists
        if file_base.endswith("_ramp"):
            output_base = file_base[:-5]  # Remove the last 5 characters ("_ramp")
        else:
            output_base = file_base

        # Create output YAML file path
        output_yaml_path = os.path.join(ramps_dir, f"{output_base}.yaml")

        # Process the image
        ramps_in_file = process_ramp_image(png_file, output_yaml_path)

        if ramps_in_file > 0:
            total_ramps += ramps_in_file
            generated_yamls.append(output_yaml_path)

    # Create the ramps.yaml index file
    if generated_yamls:
        create_ramps_index(ramps_dir, [os.path.basename(yaml) for yaml in generated_yamls])

    print(f"Total processing complete. Generated {total_ramps} ramps across {len(generated_yamls)} files.")


if __name__ == "__main__":
    main()
