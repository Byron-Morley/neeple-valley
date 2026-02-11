import os
import sys
import shutil
from PIL import Image


def main():
    # Get the directory where the script is located
    script_dir = os.path.dirname(os.path.abspath(__file__))

    # Get the root directory (two levels up from the script)
    root_dir = os.path.abspath(os.path.join(script_dir, '..', '..'))

    # Define paths relative to the root directory
    INPUT_FOLDER = os.path.join(root_dir, "core", "assets", "raw", "ramps")
    OUTPUT_FOLDER = os.path.join(root_dir, "core", "assets", "raw", "sprites", "ramps")

    print(f"Processing images from {INPUT_FOLDER}")
    print(f"Output will be saved to {OUTPUT_FOLDER}")

    # Clear the output directory if it existsat
    if os.path.exists(OUTPUT_FOLDER):
        print(f"Clearing existing output directory: {OUTPUT_FOLDER}")
        shutil.rmtree(OUTPUT_FOLDER)

    # Create the output directory
    os.makedirs(OUTPUT_FOLDER, exist_ok=True)

    # Process all PNG files in the input folder
    for filename in os.listdir(INPUT_FOLDER):
        if filename.lower().endswith(('.png', '.jpg', '.jpeg')):
            process_row_image(os.path.join(INPUT_FOLDER, filename), OUTPUT_FOLDER, filename)


def process_row_image(input_path, output_dir, filename):
    """Process a single image and extract rows of 2 pixels height"""
    try:
        # Open the image
        with Image.open(input_path) as img:
            # Convert to RGBA to ensure we have consistent format
            img = img.convert('RGBA')
            width, height = img.size

            print(f"Processing {filename}: {width}x{height} pixels")

            # Calculate how many 2-pixel high rows we can extract
            num_rows = height // 2

            print(f"Extracting {num_rows} rows (2 pixels high, {width} pixels wide each)")

            # Extract each 2-pixel high row
            for row_index in range(num_rows):
                # Calculate the top pixel of this 2-pixel high row
                start_y = row_index * 2
                end_y = start_y + 2

                # Extract the full-width, 2-pixel high row
                row_image = img.crop((0, start_y, width, end_y))

                # Generate filename: originalname_0.png, originalname_1.png, etc.
                base_name = os.path.splitext(filename)[0]
                row_filename = f"{base_name}_{row_index}.png"
                output_path = os.path.join(output_dir, row_filename)

                # Save the row image
                row_image.save(output_path)

            print(f"Extracted {num_rows} rows from {filename}")

    except Exception as e:
        print(f"Error processing {filename}: {e}")


if __name__ == "__main__":
    main()
