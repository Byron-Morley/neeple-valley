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
    RAMP_FOLDER = os.path.join(root_dir, "core", "assets", "raw", "ramps")
    OUTPUT_FOLDER = os.path.join(root_dir, "core", "assets", "raw", "sprites", "ramps", "hair")

    print(f"Processing color ramps from {RAMP_FOLDER}")
    print(f"Output will be saved to {OUTPUT_FOLDER}")

    # Clear the output directory if it exists
    if os.path.exists(OUTPUT_FOLDER):
        print(f"Clearing existing output directory: {OUTPUT_FOLDER}")
        shutil.rmtree(OUTPUT_FOLDER)

    # Create the output directory
    os.makedirs(OUTPUT_FOLDER, exist_ok=True)

    # Process all PNG files in the ramp folder
    for filename in os.listdir(RAMP_FOLDER):
        if filename.lower().endswith(('.png', '.jpg', '.jpeg')):
            process_ramp_image(os.path.join(RAMP_FOLDER, filename), OUTPUT_FOLDER, filename)


def process_ramp_image(input_path, output_dir, filename):
    """Process a single color ramp image and extract individual 2x2 color blocks"""
    try:
        # Open the image
        with Image.open(input_path) as img:
            # Convert to RGBA to ensure we have consistent color format
            img = img.convert('RGBA')
            width, height = img.size

            print(f"Processing {filename}: {width}x{height} pixels")

            # Calculate how many 2x2 blocks we can extract
            blocks_wide = width // 2
            blocks_high = height // 2

            print(f"Extracting {blocks_wide}x{blocks_high} color blocks (2x2 pixels each)")

            # Generate row labels (a, b, c, ... z, aa, ab, etc.)
            row_labels = generate_row_labels(blocks_high)

            # Extract each 2x2 block as a separate color
            for block_row in range(blocks_high):
                row_label = row_labels[block_row]

                for block_col in range(blocks_wide):
                    # Calculate the top-left pixel of this 2x2 block
                    start_x = block_col * 2
                    start_y = block_row * 2

                    # Extract the 2x2 block
                    color_block = img.crop((start_x, start_y, start_x + 2, start_y + 2))

                    # Generate filename: hair_a_0.png, hair_a_1.png, etc.
                    base_name = os.path.splitext(filename)[0]
                    color_filename = f"{base_name}_{row_label}_{block_col}.png"
                    output_path = os.path.join(output_dir, color_filename)

                    # Save the 2x2 color block
                    color_block.save(output_path)

            print(f"Extracted {blocks_high * blocks_wide} color blocks from {filename}")

    except Exception as e:
        print(f"Error processing {filename}: {e}")


def generate_row_labels(num_rows):
    """Generate row labels: a, b, c, ..., z, aa, ab, ac, ..."""
    labels = []

    for i in range(num_rows):
        if i < 26:
            # Single letter: a, b, c, ..., z
            labels.append(chr(ord('a') + i))
        else:
            # Double letter: aa, ab, ac, ...
            first_letter = chr(ord('a') + (i - 26) // 26)
            second_letter = chr(ord('a') + (i - 26) % 26)
            labels.append(first_letter + second_letter)

    return labels


if __name__ == "__main__":
    main()
