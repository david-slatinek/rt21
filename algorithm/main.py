import getopt
import struct
from os import path
from sys import argv

rule_0 = [x for x in range(-2, 0)]
rule_1 = [x for x in range(1, 3)]
rule_2 = [x for x in range(-6, -2)]
rule_3 = [x for x in range(3, 7)]
rule_4 = [x for x in range(-14, -6)]
rule_5 = [x for x in range(7, 15)]
rule_6 = [x for x in range(-30, -14)]
rule_7 = [x for x in range(15, 31)]

rule_01 = rule_0 + rule_1
rule_23 = rule_2 + rule_3
rule_45 = rule_4 + rule_5
rule_67 = rule_6 + rule_7

all_values = [x for x in range(-30, 31)]
all_values.remove(0)


def usage():
    print("Name")
    print("\tNumbers compression")

    print("Flags")
    print("-h, --help")
    print("\tPrint help")
    print("-c <filename>, --compress <filename>")
    print("\tFile with numbers to compress")
    print("-d <filename>, --decompress <filename>")
    print("\tDecompress numbers from <filename>")

    print("Note")
    print("\tFlags '-c' and '-d' can't be combined")

    print("Usage")
    print("\tpython3 main.py [flags]")


def read_from_file(file_name):
    numbers = []

    try:
        with open(file_name) as file:
            for x in file.readlines():
                for y in x.strip().split(' '):
                    numbers.append(int(y))
    except IOError as e:
        print(f'Error: {e}')
        exit(1)

    return numbers


def get_rule(number):
    if number in all_values:
        return 0
    elif number == 0:
        return 1
    elif abs(number) > 30:
        return 10


def get_rule_0_bits(number):
    if number in rule_0 or number in rule_1:
        return 2
    elif number in rule_2 or number in rule_3:
        return 3
    elif number in rule_4 or number in rule_5:
        return 4
    elif number in rule_6 or number in rule_7:
        return 5


def get_rule_0_value(number):
    try:
        return rule_01.index(number)
    except ValueError:
        pass

    try:
        return rule_23.index(number)
    except ValueError:
        pass

    try:
        return rule_45.index(number)
    except ValueError:
        pass

    try:
        return rule_67.index(number)
    except ValueError:
        pass


def get_value(_bits, index):
    if _bits == 2:
        return rule_01[index]
    elif _bits == 3:
        return rule_23[index]
    elif _bits == 4:
        return rule_45[index]
    elif _bits == 5:
        return rule_67[index]


def compress(file_name):
    numbers = read_from_file(file_name)

    numbers_2 = [numbers[0]]
    for x in range(1, len(numbers)):
        numbers_2.append(numbers[x] - numbers[x - 1])

    result = '{0:b}'.format(numbers_2[0]).zfill(8)
    numbers_2.pop(0)

    while len(numbers_2) > 0:
        number = numbers_2.pop(0)
        rule = get_rule(number)

        result += str(rule) if rule == 10 else str('{0:b}'.format(rule).zfill(2))

        if rule == 0:
            bits = get_rule_0_bits(number)
            result += str('{0:b}'.format(bits - 2).zfill(2))
            result += str('{0:b}'.format(get_rule_0_value(number))).zfill(bits)
        elif rule == 1:
            counter = 0
            while counter < 8:
                if len(numbers_2) == 0:
                    break
                value = numbers_2.pop(0)
                if value != 0:
                    numbers_2.insert(0, value)
                    break
                counter += 1
            result += str('{0:b}'.format(counter)).zfill(3)
        elif rule == 10:
            result += "1" if number < 0 else "0"
            result += str('{0:b}'.format(abs(number)).zfill(8))

    result += "11"
    return result


def write_to_file(result):
    final_pad = len(result) % 8
    data = [result[i:i + 8].zfill(8) for i in range(0, len(result), 8)]

    try:
        with open('compressed.bin', 'wb') as file:
            file.write(struct.pack('c', final_pad.to_bytes(1, byteorder='big')))

            for x in data:
                file.write(struct.pack('c', int(x, 2).to_bytes(1, byteorder='big')))
    except IOError as e:
        print(f'Error: {e}')
        exit(1)


def read_compress(filename):
    data = []
    counter = 0

    try:
        with open(filename, 'rb') as file:
            while byte := file.read(1):
                if counter == 0:
                    final_pad = int.from_bytes(struct.unpack("c", byte)[0], "big")
                    counter += 1
                    continue

                data.append("{0:b}".format(int.from_bytes(struct.unpack("c", byte)[0], "big")).zfill(8))
    except IOError as e:
        print(f'Error: {e}')
        exit(1)

    if final_pad != 0:
        data[len(data) - 1] = data[len(data) - 1][8 - final_pad:]
    return ''.join(data)


def decompress(filename):
    data = read_compress(filename)
    result = [int(data[:8], 2)]
    data = data[8:]

    while True:
        rule = data[:2]
        data = data[2:]

        if rule == "00":
            bits = int(data[:2], 2) + 2
            data = data[2:]

            index = int(data[:bits], 2)
            data = data[bits:]

            result.append(get_value(bits, index))
        elif rule == "01":
            value = int(data[:3], 2) + 1
            data = data[3:]

            for x in range(value):
                result.append(0)
        elif rule == "10":
            value = data[:9]
            data = data[9:]
            sign = value[:1]

            if sign == "1":
                value = value[1:]
                result.append(int(value, 2) * -1)
            else:
                result.append(int(value, 2))
        else:
            break

    for x in range(1, len(result)):
        result[x] += result[x - 1]

    return result


if __name__ == '__main__':
    opts = []

    try:
        opts, _ = getopt.getopt(argv[1:], "hc:d:", ["help", "compress=", "decompress="])
    except getopt.GetoptError:
        usage()
        exit(1)

    compress_filename, decompress_filename = None, None

    if opts:
        for opt, arg in opts:
            if opt in ("-h", "--help"):
                usage()
                exit()
            elif opt in ("-c", "--compress"):
                compress_filename = arg
            elif opt in ("-d", "--decompress"):
                decompress_filename = arg
            else:
                usage()
                exit(1)
    else:
        usage()
        exit(1)

    if compress_filename and decompress_filename:
        print("Use only -c or -d")
        exit(1)

    if not compress_filename and not decompress_filename:
        print("Use -c or -d")
        exit(1)

    if compress_filename:
        if not path.isfile(compress_filename):
            print("File is not valid")
            exit(1)

        write_to_file(compress(compress_filename))

    if decompress_filename:
        if not path.isfile(decompress_filename):
            print("File is not valid")
            exit(1)

        for x in decompress(decompress_filename):
            print(x, end=' ')
