from matplotlib.animation import FuncAnimation
from matplotlib import pyplot as plt
from threading import Thread
import numpy as np
import matplotlib
import serial
import time

# Local imports
from road_quality import RoadQuality

plt.style.use("dark_mode.mpltstyle")


# Constants
DEBUG = False
SERIAL_PORT = 'COM4'
SERIAL_RATE = 115200
PACKET_LENGTH = 10 	# in bytes
INTERVAL = 100		# in ms, interval between data retrievals from the board
WINDOW_LENGTH = 100 # how many samples per window

# Variables
data = []
close = False
subplots = None
road_quality = RoadQuality(INTERVAL)





def read_packet(connection):
	""" Reads a single packet from serial port and returns it with header included. """

	# Clear all data in the message buffer
	connection.read_all()

	# Raw data from board (with header)
	data_raw = bytearray([0 for _ in range(PACKET_LENGTH)])

	# catch and decode packet - endians are mixed
	sync = 0    # 0 - looking for AB, 1 - looking for AA, 2 - found packet, 3 - packet done
	sync_count = 0
	while sync < 3:
		byte_in = connection.read()

		# Byte 0xAB
		if sync == 0:
			if byte_in == b'\xAB':
				sync = 1
				sync_count = 0
				data_raw[0] = int.from_bytes(byte_in, byteorder='big')
				continue
		
		# Byte 0xAA
		if sync == 1:
			if byte_in == b'\xAA':
				sync = 2
				sync_count = 1
				data_raw[1] = int.from_bytes(byte_in, byteorder='big')
				continue
		
		# Data byte
		if sync == 2:
			sync_count += 1
			data_raw[sync_count] = int.from_bytes(byte_in, byteorder='big')

		# Exit while loop when an entire packet has been read
		if sync_count == PACKET_LENGTH - 1:
			sync = 3

	return data_raw



def save_data(xyz):
	""" Queue insert """
	if len(data) >= WINDOW_LENGTH:
		data.pop(0)
	data.append(xyz)



def fetch_data(ser):
	""" Continuously get data from board. """
	buffer = []
	timestamp = None

	try:
		while connection.isOpen() and not close:
			packet = read_packet(connection)
			
			# Convert bytes to int16 numbers (x, y, z values)
			x = int.from_bytes(bytes(packet[4:6]), byteorder='little', signed=False)
			y = int.from_bytes(bytes(packet[6:8]), byteorder='little', signed=False)
			z = int.from_bytes(bytes(packet[8:10]), byteorder='little', signed=False)

			# Temporary buffer to collect value change over an interval
			if timestamp == None:
				timestamp = time.time()
			if timestamp + (INTERVAL / 1000) < time.time():
				# Calculate changes over an interval for all 3 axes
				x_change, y_change, z_change = 0, 0, 0
				for i in range(1, len(buffer)):
					x_change += abs(buffer[i][0] - buffer[i-1][0])
					y_change += abs(buffer[i][1] - buffer[i-1][1])
					z_change += abs(buffer[i][2] - buffer[i-1][2])

				# Add data to data list
				save_data([x, y, z, road_quality.get_road_quality([x_change, y_change, z_change])])

				timestamp = time.time()
				buffer.clear()

			buffer.append([x, y, z])

	except KeyboardInterrupt:
		close_port(connection)
		print(f"Closing port '{SERIAL_PORT}' and exiting...")
		exit()



def open_port():
	connection = None
	try:
		connection = serial.Serial(SERIAL_PORT, SERIAL_RATE, timeout=5)
	except serial.SerialException:
		print(f"Could not open serial port '{SERIAL_PORT}' with rate '{SERIAL_RATE}'")
		exit()
	return connection



def close_port(connection):
	close = True
	connection.close()



def update(figure):
	# Clear figure
	subplots[0].clear()
	subplots[1].clear()	

	subplots[0].set_title("Raw accelerometer data")
	subplots[1].set_title("Road quality [0, 1]")

	# Plot x,y,z axis
	if len(data) > 0:
		# Raw data subplot
		subplots[0].plot([x[0] for x in data], color="red")
		subplots[0].plot([x[1] for x in data], color="blue")
		subplots[0].plot([x[2] for x in data], color="green")
		subplots[0].legend(["x", "y", "z"], loc="upper right")

		# Road quality subplot
		subplots[1].plot([x[3] for x in data], color="white")




if __name__ == "__main__":
	# Open serial port
	print(f"Connecting to serial port '{SERIAL_PORT}'...")
	connection = open_port()

	if connection == None:
		print(f"Exiting...")
		exit()
	else:
		print(f"Successfully connected.")

	# Start data collection thread
	data_thread = Thread(target=fetch_data, args=(connection,))
	data_thread.start()

	# Create matplotlib window and subplots
	figure, subplots = plt.subplots(2, sharex=True)
	if figure.canvas.manager is not None:
		figure.canvas.manager.set_window_title("rt21 - Road Quality")

	# Animation
	animation = FuncAnimation(figure, update, interval=INTERVAL, cache_frame_data=True)
	plt.show()

	# Join data thread
	close = True
	data_thread.join()

	# Close serial port
	close_port(connection)
