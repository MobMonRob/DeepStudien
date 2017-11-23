import matplotlib.pyplot as plt
from matplotlib import interactive
from mpl_toolkits.mplot3d import Axes3D
import numpy as np

import json

PATH = 'ruhig'

# load sensordata
data = json.load(open('../Sensordaten/konvertiert/' + PATH + '.json'))

soa = []

# Highest shown value of axis x,y,z
scale = 5

# Number of orientation matrixs
length = len(data)

aColors = []

# Constant tuples for colors
RED = (1,0,0,1)
BLUE = (0,0,1,1) 
BLACK = (0,0,0,1)

# Matches the vectors with color
VECTOR_DICTIONARIES = [{"vector": "firstVec", "color": RED}, 
                       {"vector": "secondVec", "color": BLACK}, 
                       {"vector": "thirdVec", "color": BLUE}]

for x in range(0, length):
    
    # get enry of the json
    entry = data[x]
    
    # get the entry's orientationMatrix
    matrix = entry.get("orientMat")
    
    # loop over dictionary to display all three vector of the orientation matrix
    for dic in VECTOR_DICTIONARIES:
        
        # temp represents a vector
        temp_vector = [x,0,0,0,0,0]
        
        # get the vector of the matrix (according to current dictionary entry) 
        vector = matrix.get(dic.get("vector"))
        
        # write vector x, y, z to u, v, w of temp_vector
        temp_vector[3] = vector[0]
        temp_vector[4] = vector[1]
        temp_vector[5] = vector[2]
        
        # set the according color of the vector (from VECTOR_DICTIONARY)
        aColors.append(dic.get("color"))
        soa.append(temp_vector)

# insert generated array     
X, Y, Z, U, V, W= zip(*soa)


##### Matplotlib settings #####

fig = plt.figure()
ax = fig.add_subplot(111, projection='3d')

# Scaling
ax.set_xlim([0, scale])
ax.set_ylim([0, scale])
ax.set_zlim([0, scale])

# Write the vectors into the Plot
ax.quiver(X, Y, Z, U, V, W, colors=aColors)

##### Matplotlib settings #####


# Display the plot
plt.show()