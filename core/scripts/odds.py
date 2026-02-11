def fraction_to_percentage(fraction):
    numerator, denominator = fraction
    return (denominator / (denominator + numerator)) * 100


def main():
    # odds = [9, 4, 4, 1, 66, 1, 100, 1, 66, 1, 11, 4, 8, 1, 10, 3]
    odds = [4,11,4,1,13,2]
    total_percentage = 0

    for i in range(0, len(odds), 2):
        fraction = [odds[i], odds[i + 1]]
        percentage = fraction_to_percentage(fraction)
        print(f"{odds[i]}/{odds[i + 1]}: {percentage:.2f}%")
        total_percentage += percentage

    print(f"Total: {total_percentage:.2f}%")


if __name__ == "__main__":
    main()
