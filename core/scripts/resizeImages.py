#!/usr/bin/python
from PIL import Image
import os


def resize_images_recursive(folder_path):
    """
    Recursively process all PNG files in the given folder and subfolders.
    Resize images to 64x64 if they aren't already that size, overwriting the original.
    """

    for root, dirs, files in os.walk(folder_path):
        for filename in files:
            if filename.lower().endswith(".png"):
                file_path = os.path.join(root, filename)
                try:
                    with Image.open(file_path) as img:
                        # Check if image is already 64x64
                        if img.size != (64, 64):
                            print(f"Resizing {file_path} from {img.size} to (64, 64)")
                            # Resize to 64x64 using LANCZOS resampling for better quality
                            resized_img = img.resize((64, 64), Image.Resampling.LANCZOS)
                            # Save the resized image, overwriting the original
                            resized_img.save(file_path, "PNG")
                        else:
                            print(f"Skipping {file_path} - already 64x64")
                except Exception as e:
                    print(f"Error processing {file_path}: {e}")


def main():

    script_dir = os.path.dirname(os.path.abspath(__file__))
    # Get the root directory (two levels up from the script)
    root_dir = os.path.abspath(os.path.join(script_dir, '..', '..'))

    IMAGE_FOLDER = os.path.join(root_dir, "core", "assets", "raw", "icons")

    if not os.path.exists(IMAGE_FOLDER):
        print(f"Error: Folder '{IMAGE_FOLDER}' does not exist.")
        return

    print(f"Processing PNG files in '{IMAGE_FOLDER}' and subfolders...")
    resize_images_recursive(IMAGE_FOLDER)
    print("Processing complete!")


if __name__ == "__main__":
    main()
