#!/usr/bin/python

import os
import sys
import shutil


def clear_output_folder(folder_path):
    """Clear all contents of the output folder"""
    if os.path.exists(folder_path):
        print(f"Clearing existing contents of: {folder_path}")
        # Remove all files and subdirectories
        for filename in os.listdir(folder_path):
            file_path = os.path.join(folder_path, filename)
            try:
                if os.path.isfile(file_path) or os.path.islink(file_path):
                    os.unlink(file_path)
                    print(f"Deleted file: {filename}")
                elif os.path.isdir(file_path):
                    shutil.rmtree(file_path)
                    print(f"Deleted directory: {filename}")
            except Exception as e:
                print(f"Failed to delete {file_path}. Reason: {e}")
    else:
        print(f"Output folder doesn't exist yet: {folder_path}")


def main():
    # Get the directory where the script is located
    script_dir = os.path.dirname(os.path.abspath(__file__))
    # Get the root directory (two levels up from the script)
    root_dir = os.path.abspath(os.path.join(script_dir, '..', '..'))

    # Define paths relative to the root directory
    INPUT_FILE = os.path.join(root_dir, "core", "assets", "raw", "pre-processed", "farmables", "farming crops 1-A 16x32.png")
    OUTPUT_FOLDER = os.path.join(root_dir, "core", "assets", "raw", "sprites", "16x32", "items", "farmables")

    # Array of item names for each row (you can modify this array as needed)
    ITEM_NAMES = [
        "radish",
        "lettuce",
        "carrot",
        "corn",
        "onion",
        "potato",
        "peas",
        "beans",
        "tomato",
        "wheat",
        "greenbean",
        "kale",
        "strawberry",
        "grapes",
        "pumpkin",
        "brocoli"
    ]

    print(f"Processing sprite sheet: {INPUT_FILE}")
    print(f"Output will be saved to: {OUTPUT_FOLDER}")

    # Check if input file exists
    if not os.path.exists(INPUT_FILE):
        print(f"Error: Input file not found: {INPUT_FILE}")
        return

    # Ensure the output directory exists
    if not os.path.exists(OUTPUT_FOLDER):
        os.makedirs(OUTPUT_FOLDER, exist_ok=True)

    # Clear the output folder before processing
    clear_output_folder(OUTPUT_FOLDER)

    # First, let's get the dimensions of the input image to determine how many sprites per row
    identify_cmd = f'magick identify "{INPUT_FILE}"'
    print(f"Getting image dimensions: {identify_cmd}")
    os.system(identify_cmd)

    # Process each row
    for row_index, item_name in enumerate(ITEM_NAMES):
        print(f"\nProcessing row {row_index} for item: {item_name}")

        # Calculate the Y offset for this row (each row is 32 pixels high)
        y_offset = row_index * 32

        # Extract the entire row first
        row_extract_cmd = f'magick "{INPUT_FILE}" -crop 0x32+0+{y_offset} +repage temp_row_{row_index}.png'
        print(f"Extracting row: {row_extract_cmd}")
        os.system(row_extract_cmd)

        # Now crop the row into individual 16x32 sprites
        temp_row_file = f"temp_row_{row_index}.png"
        output_pattern = os.path.join(OUTPUT_FOLDER, f"{item_name}_%02d.png")

        crop_cmd = f'magick "{temp_row_file}" -crop 16x32 "{output_pattern}"'
        print(f"Cropping sprites: {crop_cmd}")
        os.system(crop_cmd)

        # Clean up temporary row file
        if os.path.exists(temp_row_file):
            os.remove(temp_row_file)

        print(f"Completed processing {item_name} sprites")

    print(f"\nAll sprites have been processed and saved to: {OUTPUT_FOLDER}")


if __name__ == "__main__":
    main()
