import os
import re
import yaml
from pathlib import Path

# Get script directory and project root
SCRIPT_DIR = os.path.dirname(os.path.abspath(__file__))
PROJECT_ROOT = os.path.abspath(os.path.join(SCRIPT_DIR, "../.."))

# Define paths relative to project root
SPRITES_DIR = os.path.join(PROJECT_ROOT, "core/assets/raw/sprites")
ITEMS_DIR = os.path.join(PROJECT_ROOT, "core/assets/model/entities/items")

# List of folder names to exclude from the search
EXCLUDED_FOLDERS = ["body", "tiles", "terrain"]


def find_sprite_files(sprites_dir=SPRITES_DIR, excluded_folders=EXCLUDED_FOLDERS):
    """Search for PNG files in the sprites directory and its subdirectories."""
    sprites_dir = Path(sprites_dir)
    sprite_files = []
    print(f"Searching for PNG files in: {sprites_dir}")
    print(f"Excluding folders: {', '.join(excluded_folders)}")

    # Dictionary to group files by base name (without numeric suffix)
    grouped_sprites = {}

    for root, dirs, files in os.walk(sprites_dir):
        # Remove excluded folders from dirs to prevent os.walk from traversing them
        dirs[:] = [d for d in dirs if d not in excluded_folders]

        for file in files:
            if file.lower().endswith('.png'):
                full_path = Path(root) / file
                # Get relative path from sprites_dir
                rel_path = full_path.relative_to(sprites_dir)

                # Extract base name by removing numeric suffix after underscore
                # Example: "axe_133.png" -> "axe"
                filename_without_ext = Path(file).stem
                base_name = re.sub(r'_\d+$', '', filename_without_ext)

                # Get the category from the path parts
                # For a path like "large/items/scenery/small-tree.png", the category should be "scenery"
                path_parts = list(rel_path.parts)
                # Look for "items" in the path and use the folder after it as the category
                if "items" in path_parts:
                    items_index = path_parts.index("items")
                    category = path_parts[items_index + 1] if items_index + 1 < len(path_parts) else ""
                else:
                    # If "items" is not in the path, use the first folder as the category
                    category = path_parts[0] if path_parts else ""

                # Add to grouped sprites dictionary
                if base_name not in grouped_sprites:
                    grouped_sprites[base_name] = {
                        'name': base_name,
                        'category': category,
                        'variants': []
                    }

                grouped_sprites[base_name]['variants'].append({
                    'full_path': str(full_path),
                    'rel_path': str(rel_path),
                    'filename': file,
                    'variant_name': filename_without_ext
                })

    # Convert grouped dictionary to list
    for base_name, group_data in grouped_sprites.items():
        sprite_files.append({
            'name': base_name,
            'category': group_data['category'],
            'variants': group_data['variants'],
            'variant_count': len(group_data['variants']),
            # Use the first variant's path as the primary path
            'primary_rel_path': group_data['variants'][0]['rel_path'] if group_data['variants'] else None
        })

    return sprite_files


def load_yaml_files(items_dir=ITEMS_DIR):
    """Load all YAML files in the items directory and parse their content."""
    items_dir = Path(items_dir)
    yaml_data = {}
    yaml_files = {}

    print(f"Loading YAML files from: {items_dir}")

    for root, _, files in os.walk(items_dir):
        for file in files:
            if file.lower().endswith(('.yaml', '.yml')):
                full_path = Path(root) / file

                # Get the category - either from the parent directory or the filename
                if Path(root) != items_dir:
                    category = Path(root).name
                else:
                    # If the file is directly in the items directory, use the filename as category
                    category = Path(file).stem

                try:
                    with open(full_path, 'r', encoding='utf-8') as f:
                        content = yaml.safe_load(f)
                        if content:
                            if category not in yaml_data:
                                yaml_data[category] = {}
                                yaml_files[category] = []
                            yaml_data[category][str(full_path)] = content
                            yaml_files[category].append(str(full_path))
                except Exception as e:
                    print(f"Error loading {full_path}: {e}")

    return yaml_data, yaml_files


def check_sprites_in_yaml(sprites, yaml_data):
    """Check if each sprite has a corresponding object in the YAML files."""
    for sprite in sprites:
        sprite_name = sprite['name']
        sprite_category = sprite['category']

        # Check if the category exists in the YAML data
        if sprite_category in yaml_data:
            sprite_found = False

            # Check each YAML file in the category
            for yaml_path, yaml_content in yaml_data[sprite_category].items():
                # Check if the sprite name exists as a key in the YAML content
                if isinstance(yaml_content, dict) and sprite_name in yaml_content:
                    sprite['has_yaml'] = True
                    sprite['yaml_path'] = yaml_path
                    sprite_found = True
                    break

            if not sprite_found:
                sprite['has_yaml'] = False
        else:
            sprite['has_yaml'] = False
            sprite['category_missing'] = True

    return sprites


def find_orphaned_yaml_entries(sprites, yaml_data):
    """Find YAML entries that don't have corresponding sprite files."""
    # Create dictionaries for quick lookup
    sprite_by_name = {}  # Lookup by name
    sprite_by_path = {}  # Lookup by path

    for sprite in sprites:
        category = sprite['category']
        name = sprite['name']

        # Store by name for primary lookup
        if category not in sprite_by_name:
            sprite_by_name[category] = set()
        sprite_by_name[category].add(name)

        # Store by path for secondary lookup
        for variant in sprite['variants']:
            rel_path = variant['rel_path']
            path_parts = Path(rel_path).parts

            # Create different path formats that might be used in YAML
            if len(path_parts) > 1:
                # Format: folder/filename (no extension)
                folder = path_parts[-2]
                filename = Path(path_parts[-1]).stem
                path_key = f"{folder}/{filename}"

                if path_key not in sprite_by_path:
                    sprite_by_path[path_key] = True

    # Find orphaned entries
    orphaned_entries = []

    for category, yaml_files in yaml_data.items():
        for yaml_path, yaml_content in yaml_files.items():
            if isinstance(yaml_content, dict):
                for item_name, item_data in yaml_content.items():
                    # First check if the item name exists directly
                    name_exists = (category in sprite_by_name and
                                   item_name in sprite_by_name[category])

                    # If name doesn't exist, check if the sprite path exists
                    sprite_exists = False
                    if not name_exists and isinstance(item_data, dict) and 'sprite' in item_data:
                        sprite_path = item_data['sprite']
                        sprite_exists = sprite_path in sprite_by_path

                    # If neither name nor sprite exists, it's orphaned
                    if not name_exists and not sprite_exists:
                        orphaned_entries.append({
                            'name': item_name,
                            'category': category,
                            'yaml_path': yaml_path,
                            'sprite_path': item_data.get('sprite') if isinstance(item_data, dict) else None
                        })

    return orphaned_entries


def add_missing_sprites_to_yaml(sprites_without_yaml, yaml_data, yaml_files):
    """Add missing sprites to the appropriate YAML files."""
    updated_files = []
    created_files = []

    # Group sprites by category
    by_category = {}
    for sprite in sprites_without_yaml:
        category = sprite['category']
        if category not in by_category:
            by_category[category] = []
        by_category[category].append(sprite)

    # Process each category
    for category, category_sprites in by_category.items():
        if not category:
            print(f"Skipping sprites with no category")
            continue

        # Check if we have a YAML file for this category
        if category in yaml_files and yaml_files[category]:
            # Use the first YAML file for this category
            yaml_path = yaml_files[category][0]

            # Load the existing content
            with open(yaml_path, 'r', encoding='utf-8') as f:
                content = yaml.safe_load(f) or {}

            # Add the missing sprites
            for sprite in category_sprites:
                sprite_name = sprite['name']
                sprite_path = sprite['primary_rel_path']

                # Format the sprite path correctly (folder name + file name without extension)
                path_parts = Path(sprite_path).parts
                sprite_folder = path_parts[-2] if len(path_parts) > 1 else ""
                sprite_file = Path(path_parts[-1]).stem
                formatted_sprite_path = f"{sprite_folder}/{sprite_file}"

                # Create the item entry
                content[sprite_name] = {
                    "label": sprite_name.replace('_', ' ').title(),
                    "sprite": formatted_sprite_path
                }

                print(f"Added '{sprite_name}' to {yaml_path}")

            # Save the updated content
            with open(yaml_path, 'w', encoding='utf-8') as f:
                yaml.dump(content, f, default_flow_style=False, sort_keys=False)

            updated_files.append(yaml_path)
        else:
            # Create a new YAML file for this category
            yaml_path = os.path.join(ITEMS_DIR, f"{category}.yaml")

            # Create the content
            content = {}
            for sprite in category_sprites:
                sprite_name = sprite['name']
                sprite_path = sprite['primary_rel_path']

                # Format the sprite path correctly (folder name + file name without extension)
                path_parts = Path(sprite_path).parts
                sprite_folder = path_parts[-2] if len(path_parts) > 1 else ""
                sprite_file = Path(path_parts[-1]).stem
                formatted_sprite_path = f"{sprite_folder}/{sprite_file}"

                # Create the item entry
                content[sprite_name] = {
                    "label": sprite_name.replace('_', ' ').title(),
                    "sprite": formatted_sprite_path
                }

                print(f"Added '{sprite_name}' to new file {yaml_path}")

            # Save the new content
            with open(yaml_path, 'w', encoding='utf-8') as f:
                yaml.dump(content, f, default_flow_style=False, sort_keys=False)

            created_files.append(yaml_path)

    return updated_files, created_files


if __name__ == "__main__":
    # Find all sprite files
    sprites = find_sprite_files()

    # Load all YAML files and their content
    yaml_data, yaml_files = load_yaml_files()

    # Check which sprites have corresponding objects in the YAML files
    sprites = check_sprites_in_yaml(sprites, yaml_data)

    # Find YAML entries that don't have corresponding sprite files
    orphaned_entries = find_orphaned_yaml_entries(sprites, yaml_data)

    # Count sprites with and without YAML entries
    sprites_with_yaml = [s for s in sprites if s.get('has_yaml', False)]
    sprites_without_yaml = [s for s in sprites if not s.get('has_yaml', False)]

    # Print results
    print(f"\nFound {len(sprites)} unique sprite items")
    print(f"Found YAML files for {len(yaml_data)} categories")
    print(f"\n{len(sprites_with_yaml)} sprites already have entries in YAML files")
    print(f"{len(sprites_without_yaml)} sprites need entries to be created")
    print(f"{len(orphaned_entries)} YAML entries have no corresponding sprite files\n")

    # Print sprites that need YAML entries
    if sprites_without_yaml:
        print("Sprites that need YAML entries:")

        # Group by category for better organization
        by_category = {}
        for sprite in sprites_without_yaml:
            category = sprite['category']
            if category not in by_category:
                by_category[category] = []
            by_category[category].append(sprite)

        for category, category_sprites in by_category.items():
            if category:
                if category not in yaml_files:
                    print(f"\nCategory '{category}' has no YAML file:")
                else:
                    print(f"\nCategory '{category}':")

                for sprite in category_sprites:
                    print(f"  {sprite['name']}:")
                    print(f"    - {sprite['primary_rel_path']}")
            else:
                print("\nNo category:")
                for sprite in category_sprites:
                    print(f"  {sprite['name']}:")
                    print(f"    - {sprite['primary_rel_path']}")

    # Print orphaned YAML entries
    if orphaned_entries:
        print("\nYAML entries with no corresponding sprite files:")

        # Group by category for better organization
        by_category = {}
        for entry in orphaned_entries:
            category = entry['category']
            if category not in by_category:
                by_category[category] = []
            by_category[category].append(entry)

        for category, entries in by_category.items():
            print(f"\nCategory '{category}':")
            for entry in entries:
                sprite_info = f" (sprite: {entry['sprite_path']})" if entry['sprite_path'] else ""
                print(f"  {entry['name']}{sprite_info} in {entry['yaml_path']}")

    # Ask if user wants to add missing sprites
    if sprites_without_yaml:
        response = input("\nDo you want to add missing sprites to YAML files? (y/n): ")
        if response.lower() in ['y', 'yes']:
            updated_files, created_files = add_missing_sprites_to_yaml(sprites_without_yaml, yaml_data, yaml_files)

            if updated_files or created_files:
                print("\nSummary of changes:")
                if updated_files:
                    print(f"Updated {len(updated_files)} existing YAML files:")
                    for file in updated_files:
                        print(f"  - {file}")
                if created_files:
                    print(f"Created {len(created_files)} new YAML files:")
                    for file in created_files:
                        print(f"  - {file}")
            else:
                print("No changes were made.")
        else:
            print("No changes were made.")
    elif not orphaned_entries:
        print("All sprites already have entries in YAML files. No changes needed.")
