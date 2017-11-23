import matplotlib.pyplot as plt
from mpl_toolkits.mplot3d import Axes3D
import numpy as np

import json

path = 'ruhig'

data = json.load(open('../Sensordaten/konvertiert/' + path + '.json'))

soa = []

scale = 5

points_x = []
points_y = []
points_z = []

length = len(data)

aColors = []

RED = (1,0,0,1)
BLUE = (0,0,1,1) 
BLACK = (0,0,0,1)

for x in range(0, length):

    entry = data[x]
    matrix = entry.get("orientMat")
    firstVector = matrix.get("firstVec")
    
    temp = [x,0,0,0,0,0]
        
    temp[3] = firstVector[0]
    temp[4] = firstVector[1]
    temp[5] = firstVector[2]
    
    aColors.append(RED)
    soa.append(temp)
    
    secondVector = matrix.get("secondVec")
    
    temp = [x,0,0,0,0,0]
    
    temp[3] = secondVector[0]
    temp[4] = secondVector[1]
    temp[5] = secondVector[2]

    aColors.append(BLUE)    
    soa.append(temp)
    
    thirdVector = matrix.get("thirdVec")
    
    temp = [x,0,0,0,0,0]
    
    temp[3] = thirdVector[0]
    temp[4] = thirdVector[1]
    temp[5] = thirdVector[2]
    
    aColors.append(BLACK)
    soa.append(temp)
    

X, Y, Z, U, V, W= zip(*soa)
fig = plt.figure()
ax = fig.add_subplot(111, projection='3d')

# Scaling
ax.set_xlim([0, scale])
ax.set_ylim([0, scale])
ax.set_zlim([0, scale])

# ------------
# Vectors
ax.quiver(X, Y, Z, U, V, W, colors=aColors)
# ------------


# ------------
# Points
#ax.scatter(points_x, points_y, points_z, c='r', marker='o')
# ------------

print(path)

plt.show()