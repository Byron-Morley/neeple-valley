import urllib.request
import sys
from bs4 import BeautifulSoup
import os

X_COORDINATE = "x-coordinate"
Y_COORDINATE = "y-coordinate"
CHARACTER = "character"

def read_file_from_url(url):
    try:
        print(f"Reading file from {url}...")

        headers = {
            'User-Agent': 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/91.0.4472.124 Safari/537.36'
        }
        req = urllib.request.Request(url, headers=headers)

        with urllib.request.urlopen(req) as response:
            content = response.read().decode('utf-8')  # Assuming UTF-8 encoding

        print(f"File read successfully ({len(content)} bytes)")
        return content

    except Exception as e:
        print(f"Error reading file: {e}")
        return None


def find_columns_indexes(table):

    rows = table.find_all('tr')

    if len(rows) <= 1:
        print("Table has no data rows")
        return None, -1, -1, -1

    header_row = rows[0]
    headers = [th.get_text().strip().lower() for th in header_row.find_all(['th', 'td'])]

    x_column = -1
    character_column = -1
    y_column = -1

    for i, header in enumerate(headers):
        if X_COORDINATE in header:
            x_column = i
        elif CHARACTER in header:
            character_column = i
        elif Y_COORDINATE in header:
            y_column = i

    if x_column == -1 or character_column == -1 or y_column == -1:
        print(f"Could not find all required columns in the table. Headers found: {headers}")
        return None, -1, -1, -1

    return x_column, character_column, y_column

def extract_coordinates_from_table(table, x_column, character_column, y_column):
    coordinates = []
    rows = table.find_all('tr')

    for row in rows[1:]:
        cells = row.find_all(['td', 'th'])

        if len(cells) > max(x_column, character_column, y_column):
            try:
                x = int(cells[x_column].get_text().strip())
                y = int(cells[y_column].get_text().strip())
                char = cells[character_column].get_text().strip()

                coordinates.append((x, y, char))
            except ValueError:
                continue

    return coordinates


def extract_table_data(html_content):
    soup = BeautifulSoup(html_content, 'html.parser')
    tables = soup.find_all('table')

    if not tables:
        print("No tables found in the document")
        return None

    table = tables[0]

    x_column, character_column, y_column = find_columns_indexes(table)

    return extract_coordinates_from_table(table, x_column, character_column, y_column)

def print_grid(coordinates):
    min_x = min(coord[0] for coord in coordinates)
    max_x = max(coord[0] for coord in coordinates)
    min_y = min(coord[1] for coord in coordinates)
    max_y = max(coord[1] for coord in coordinates)

    width = max_x - min_x + 1
    height = max_y - min_y + 1
    grid = [[' ' for _ in range(width)] for _ in range(height)]

    for x, y, char in coordinates:
        adjusted_x = x - min_x
        adjusted_y = y - min_y

        if 0 <= adjusted_y < height and 0 <= adjusted_x < width:
            grid[adjusted_y][adjusted_x] = char[0]

    print("\nVisual representation of characters:")

    for row in reversed(grid):
        print("" + "".join(row) + "")



def process_url(url):

    content = read_file_from_url(url)

    if content:
        coordinates = extract_table_data(content)
        if coordinates:
            print_grid(coordinates)
        else:
            print("Failed to extract table data")


def main():
    print("Starting Application")

    # Default URL if not provided
    if len(sys.argv) < 2:
        url = "https://docs.google.com/document/d/e/2PACX-1vRMx5YQlZNa3ra8dYYxmv-QIQ3YJe8tbI3kqcuC7lQiZm-CSEznKfN_HYNSpoXcZIV3Y_O3YoUB1ecq/pub"
        print(f"No URL provided, using default: {url}")
    else:
        url = sys.argv[1]

    process_url(url)

    print("Processing complete!")

if __name__ == "__main__":
    main()
