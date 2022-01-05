


class RoadQuality:

	def __init__(self, interval):
		self.buffer = []
		self.max_length = max(int(2*1000 / interval), 1)
		self.interval = interval
		self.max = 60000	# Should be adjusted depending on board/phone used


	def get_road_quality(self, accelerometer:list) -> float:
		# Add new data to buffer
		if len(self.buffer) >= self.max_length:
			self.buffer.pop(0)
		self.buffer.append(accelerometer)

		# Average from buffer
		x_values = list(map(lambda x: x[0], self.buffer))
		x_average = sum(x_values) / len(x_values)

		quality = 1 - (x_average / self.max)
		return abs(quality)
