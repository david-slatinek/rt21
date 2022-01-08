from mpi4py import MPI
import numpy as np
import time

# Local imports
from detect_road_sign import recognize



def _print(text):
	""" Util print function that prepends processors rank and flushes stdout. """
	pre = "MASTER - " if rank == 0 else f"SLAVE {rank} - "
	print(pre + str(text), flush=True)


def process(image:str) -> str:
	_print(f"processing image '{str(image)}'")
	
	return recognize(image)




comm = MPI.COMM_WORLD
size = comm.Get_size()	# number of processors
rank = comm.Get_rank()	# this processors id

# Check if the script was started up with correct arguments
if size < 2:
	print(f"This script requires atleast 2 processors. Current count is {size}.")
	print("Start with:  mpiexec -n {>=2} python script.py")
	exit()



# Variables
images = []





# ============= MASTER - get list of images ============= 
if rank == 0:
	images = ["images\\image1.jpg", ".\\images\\image2.jpg", ".\\images\\image3.jpg"]	# TODO - get this from http/arguments/file/hard-coded


# Broadcast images list (master -> slaves communication)
images = comm.bcast(images, root=0)


# ============= SLAVES - process images and send results to master ============= 
if rank != 0:
	# Extract only the images current slave has to process
	queue = images[rank-1::size-1]
	_print(f"processing: {str(queue)}")

	# Process individual images
	results = []
	for image in queue:
		result = process(image)
		results.append(result)

	# Send results to master
	comm.send(results, dest=0, tag=rank)


# ============= MASTER - retrieve results from slaves ============= 
if rank == 0:
	# Receive results from all slaves
	results = [None for _ in range(len(images))]
	for slave in range(1, size):
		subset = comm.recv(source=slave, tag=slave)

		for index, result in enumerate(subset):
			results_index = slave-1 + (index * (size-1)) 
			_print(f"results_index: {results_index} for slave {slave} and subset index {index}, result: {result}")
			results[results_index] = result

	# Print results to stdout
	if None in results:
		_print(f"did not receive results for all images successfully")
	_print(f"finished with results: {str(results)}")
