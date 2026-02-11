#!/usr/bin/python

import os
from PIL import Image, ImageDraw, ImageFont
import re

# Get the project root directory
SCRIPT_DIR = os.path.dirname(os.path.abspath(__file__))
PROJECT_ROOT = os.path.abspath(os.path.join(SCRIPT_DIR, "../.."))

# Use absolute paths
INPUT_DIR = os.path.join(PROJECT_ROOT, "core/assets/raw/pre-processed")
SCALE_FACTOR = 4  # Make the output image 4x larger
OUTPUT_SUFFIX = "_tile_reference_4x.png"  # Consistent suffix for reference images


def get_tile_size_from_dir(dir_name):
    """Extract the tile size from a directory name like '16x16'."""
    match = re.search(r'(\d+)', dir_name)
    if match:
        return int(match.group(1))
    return 16  # Default to 16 if no number found


def load_source_image(image_path):
    """Load the source image and return it with its dimensions."""
    try:
        source_image = Image.open(image_path)
        width, height = source_image.size
        return source_image, width, height
    except Exception as e:
        print(f"Error loading image {image_path}: {e}")
        return None, 0, 0


def create_annotated_image(image_path, dir_name):
    """Create an annotated image showing tile row and column positions."""
    # Get tile size from directory name
    tile_size = get_tile_size_from_dir(dir_name)
    print(f"Using tile size: {tile_size}px")

    # Load the source image
    source_image, width, height = load_source_image(image_path)
    if not source_image:
        return

    # Calculate grid dimensions
    tiles_per_row = width // tile_size
    tiles_per_col = height // tile_size

    print(f"Image size: {width}x{height}, Tiles: {tiles_per_row}x{tiles_per_col}")

    # Create a new enlarged image
    enlarged_width = width * SCALE_FACTOR
    enlarged_height = height * SCALE_FACTOR
    enlarged_tile_size = tile_size * SCALE_FACTOR

    # Create a new blank image with transparent background
    annotated_image = Image.new('RGBA', (enlarged_width, enlarged_height), (255, 255, 255, 0))

    # Resize the original image and paste it onto the new image
    resized_original = source_image.resize((enlarged_width, enlarged_height), Image.NEAREST)
    annotated_image.paste(resized_original)

    draw = ImageDraw.Draw(annotated_image)

    # Try to load a font with a larger size
    font_size = enlarged_tile_size // 2  # Bigger font size for better visibility
    try:
        font = ImageFont.truetype("arial.ttf", font_size)
    except IOError:
        try:
            # Try system font as fallback
            font = ImageFont.truetype("DejaVuSans.ttf", font_size)
        except IOError:
            try:
                # Another fallback attempt
                font = ImageFont.truetype("/usr/share/fonts/truetype/dejavu/DejaVuSans.ttf", font_size)
            except IOError:
                font = ImageFont.load_default()

    # Process all tiles in the grid
    for y in range(tiles_per_col):
        for x in range(tiles_per_row):
            # Skip if this would be outside the image bounds
            if (x * tile_size + tile_size) > width or (y * tile_size + tile_size) > height:
                continue

            # Calculate tile boundaries (scaled up)
            left = x * enlarged_tile_size
            top = y * enlarged_tile_size
            right = left + enlarged_tile_size
            bottom = top + enlarged_tile_size

            # Draw a grid border (thicker lines for better visibility)
            line_width = max(1, SCALE_FACTOR // 2)

            # Special coloring for the first 4x4 grid (0-15 in the Wang set)
            if y < 4 and x < 4:
                border_color = (0, 180, 0)  # Green border for the first 16 tiles
            else:
                border_color = (255, 0, 0)  # Red border for the remainder

            draw.rectangle([left, top, right - 1, bottom - 1], outline=border_color, width=line_width)

            # Draw the position as row,col in the center of the tile
            position_text = f"R{y},C{x}"

            # Calculate text width for centering
            try:
                position_text_width = draw.textlength(position_text, font=font)
            except AttributeError:  # For older PIL versions
                position_text_width = font.getlength(position_text)

            position_text_position = (
                left + (enlarged_tile_size - position_text_width) // 2,
                top + (enlarged_tile_size - font_size) // 2
            )

            # Draw a semi-transparent background for the position text
            padding = font_size // 4
            position_text_box = (
                left + (enlarged_tile_size - position_text_width) // 2 - padding,
                top + (enlarged_tile_size - font_size) // 2 - padding,
                left + (enlarged_tile_size - position_text_width) // 2 + position_text_width + padding,
                top + (enlarged_tile_size - font_size) // 2 + font_size + padding
            )

            # Use different background colors for the first 16 tiles vs the rest
            if y < 4 and x < 4:
                # Green background for the first 16 tiles
                bg_color = (0, 100, 0, 180)
            else:
                # Black background for the remainder
                bg_color = (0, 0, 0, 180)

            draw.rectangle(position_text_box, fill=bg_color)
            draw.text(position_text_position, position_text, fill=(255, 255, 255), font=font)

    # Generate output file path - consistent naming scheme
    output_filename = f"{os.path.splitext(os.path.basename(image_path))[0]}{OUTPUT_SUFFIX}"
    output_path = os.path.join(os.path.dirname(image_path), output_filename)

    # Save the annotated image (overwriting if it exists)
    print(f"Saving position reference image to: {output_path}")
    annotated_image.save(output_path)
    return output_path


def should_process_file(file_name):
    """Determine if a file should be processed based on its name."""
    # Skip reference files and non-image files
    if OUTPUT_SUFFIX in file_name:  # Skip our own reference images
        return False
    if file_name.lower() == 'references.json':  # Skip reference files
        return False
    if not file_name.lower().endswith(('.png', '.jpg', '.jpeg')):  # Only process images
        return False
    return True


def process_file(root, file_name, dir_name):
    """Process a single file from the input directory."""
    file_path = os.path.join(root, file_name)
    print(f"Processing file: {file_name}")

    create_annotated_image(file_path, dir_name)


def walk_input_directory(input_dir):
    """Walk through the input directory and process image files."""
    for root, dirs, files in os.walk(input_dir):
        dir_name = os.path.basename(root)
        print(f"Current directory: {dir_name}")

        for file_name in files:
            if should_process_file(file_name):
                process_file(root, file_name, dir_name)
            else:
                print(f"Skipping file: {file_name}")


def main():
    """Main entry point of the script."""
    print("Starting Tile Position Visualizer (4x Scale)...")

    if not os.path.exists(INPUT_DIR):
        print(f"Warning: Input directory '{INPUT_DIR}' does not exist!")
        return

    walk_input_directory(INPUT_DIR)

    print("Processing complete!")


if __name__ == "__main__":
    main()
