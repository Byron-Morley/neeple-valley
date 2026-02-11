#!/usr/bin/python

import os
from PIL import Image
import json
import re

# Get the project root directory (assuming the script is in core/scripts)
SCRIPT_DIR = os.path.dirname(os.path.abspath(__file__))
PROJECT_ROOT = os.path.abspath(os.path.join(SCRIPT_DIR, "../.."))

# Use absolute paths
INPUT_DIR = os.path.join(PROJECT_ROOT, "core/assets/raw/pre-processed")


def get_output_dir(dir):
    """Generate the output directory path based on the input directory name."""
    return os.path.join(PROJECT_ROOT, f"core/assets/raw/sprites/{dir}/tiles")


def get_tile_size_from_dir(dir_name):
    """Extract the tile size from a directory name like '16x16'."""
    match = re.search(r'(\d+)', dir_name)
    if match:
        return int(match.group(1))
    return 16  # Default to 16 if no number found


def ensure_directory_exists(directory_path):
    """Create a directory if it doesn't exist."""
    os.makedirs(directory_path, exist_ok=True)


def load_reference_data(ref_file_path):
    """Load and parse the reference JSON file."""
    try:
        with open(ref_file_path, 'r') as f:
            return json.load(f)
    except (FileNotFoundError, json.JSONDecodeError) as e:
        print(f"Error loading reference file: {e}")
        return None


def create_id_to_role_mapping(reference_data):
    """Create a lookup dictionary for id->role mapping from reference data."""
    if not reference_data or 'members' not in reference_data:
        return {}
    return {member['id']: member['role'] for member in reference_data['members']}


def load_source_image(image_path):
    """Load the source image and return it with its dimensions."""
    try:
        source_image = Image.open(image_path)
        width, height = source_image.size
        return source_image, width, height
    except Exception as e:
        print(f"Error loading image {image_path}: {e}")
        return None, 0, 0


def validate_image_dimensions(width, height, tile_size):
    """Verify image dimensions are divisible by tile size."""
    if width % tile_size != 0 or height % tile_size != 0:
        raise ValueError(f"Image dimensions ({width}x{height}) must be divisible by tile size ({tile_size})")
    return width // tile_size, height // tile_size  # Return tiles_per_row, tiles_per_col


def clean_output_directory(output_dir):
    """Remove all PNG files from the output directory."""
    if not os.path.exists(output_dir):
        return

    print(f"Cleaning output directory: {output_dir}")
    for file in os.listdir(output_dir):
        if file.lower().endswith('.png'):
            file_path = os.path.join(output_dir, file)
            try:
                os.remove(file_path)
                print(f"  Removed: {file}")
            except Exception as e:
                print(f"  Error removing {file}: {e}")

def extract_and_save_tile(source_image, x, y, tile_size, role, output_path):
    """Extract a tile from the source image and save it to the output path."""
    left = x * tile_size
    top = y * tile_size
    right = left + tile_size
    bottom = top + tile_size

    tile = source_image.crop((left, top, right, bottom))
    print(f'Saving: {output_path}')
    tile.save(output_path)


def extract_animation_index(filename):
    """Extract animation index from filename if it already has numbers at the end."""
    # Match pattern like river_0 to extract the 0
    match = re.search(r'_(\d+)$', filename)
    if match:
        # Return the matched number as the animation index
        return match.group(1)
    # If no numbers at the end, return "0" as the default animation index
    return "0"


def process_tiles(source_image, tiles_per_row, tiles_per_col, tile_size, id_to_role, output_dir, image_filename):
    """Process only the first 4x4 grid of tiles from the source image."""
    # Limit processing to first 4 columns and 4 rows
    max_cols = min(4, tiles_per_row)
    max_rows = min(4, tiles_per_col)

    print(f"Processing {max_rows}x{max_cols} grid of tiles...")

    # First, remove any file extension
    base_filename = os.path.splitext(image_filename)[0]
    print(f"Base filename (before animation extraction): {base_filename}")

    # Extract animation index from the filename
    animation_index = extract_animation_index(base_filename)
    print(f"Extracted animation index: {animation_index}")

    # Remove the trailing _X from the filename if animation index was found
    if animation_index != "0" or base_filename.endswith(f"_{animation_index}"):
        base_filename = re.sub(r'_\d+$', '', base_filename)
        print(f"Base filename (after removing animation index): {base_filename}")

    print(f"Final base filename: {base_filename}")

    for y in range(max_rows):
        for x in range(max_cols):
            # Calculate the ID based on a fixed 4x4 grid (always 4 tiles per row)
            current_id = y * 4 + x

            # Skip if current_id is not in the reference data
            if current_id not in id_to_role:
                print(f"Warning: No role defined for tile ID {current_id}, skipping")
                continue

            # Get the role for this tile from reference
            role = id_to_role[current_id]

            # Build output path with animation index and save the tile
            output_path = os.path.join(output_dir, f'{base_filename}_{role}_{animation_index}.png')
            print(f"Generated output path: {output_path}")
            extract_and_save_tile(source_image, x, y, tile_size, role, output_path)


def process_tileset(output_dir, image_path, dir_name):
    """Process a tileset image, extracting and saving individual tiles."""
    # Create output directory if it doesn't exist
    ensure_directory_exists(output_dir)

    # Get tile size from directory name
    tile_size = get_tile_size_from_dir(dir_name)
    print(f"Using tile size: {tile_size}px")

    # Extract the original image filename without extension
    image_filename = os.path.splitext(os.path.basename(image_path))[0]

    # Look for reference.json in the same directory as the image file
    image_dir = os.path.dirname(image_path)
    ref_file_path = os.path.join(image_dir, "reference.json")
    print(f"Looking for reference file at: {ref_file_path}")

    # Load the reference JSON
    reference_data = load_reference_data(ref_file_path)
    if not reference_data:
        print(f"Skipping {image_path} due to missing reference data")
        return

    # Create a lookup dictionary for id->role mapping
    id_to_role = create_id_to_role_mapping(reference_data)
    if not id_to_role:
        print(f"No roles defined in reference data, skipping {image_path}")
        return

    # Load the source image
    source_image, width, height = load_source_image(image_path)
    if not source_image:
        return

    try:
        # Validate image dimensions and get tile counts
        tiles_per_row, tiles_per_col = validate_image_dimensions(width, height, tile_size)
        print(f"Image size: {width}x{height}, Tiles: {tiles_per_row}x{tiles_per_col}")

        # Process all tiles
        process_tiles(source_image, tiles_per_row, tiles_per_col, tile_size, id_to_role, output_dir, image_filename)
    except ValueError as e:
        print(f"Error: {e}")


def process_file(root, file_name, dir_name):
    """Process a single file from the input directory."""
    # Skip reference.json files
    if file_name.lower() == "reference.json":
        return

    file_path = os.path.join(root, file_name)
    print(f"  File: {file_name} (Full path: {file_path})")

    output_dir = get_output_dir(dir_name)
    print(f"  Output directory: {output_dir}")

    process_tileset(output_dir, file_path, dir_name)


def walk_input_directory(input_dir):
    """Walk through the input directory and process files."""
    # Keep track of directories we've already cleaned
    cleaned_dirs = set()

    for root, dirs, files in os.walk(input_dir):
        print(f"Current directory: {root}")

        dir_name = os.path.basename(root)
        print(f"Directory name: {dir_name}")

        # Clean the output directory once per unique directory
        output_dir = get_output_dir(dir_name)
        if output_dir not in cleaned_dirs:
            clean_output_directory(output_dir)
            cleaned_dirs.add(output_dir)

        for file_name in files:
            process_file(root, file_name, dir_name)


def check_input_directory(input_dir):
    """Check if the input directory exists and process it."""
    if not os.path.exists(input_dir):
        print(f"Warning: Input directory '{input_dir}' does not exist!")
        return False
    return True


def main():
    """Main entry point of the script."""
    print("Starting Wang Edge Slicer...")

    if check_input_directory(INPUT_DIR):
        walk_input_directory(INPUT_DIR)

    print("Processing complete!")


if __name__ == "__main__":
    main()
