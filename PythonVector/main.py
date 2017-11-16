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

for x in range(0, length):
    entry = data[x]
    matrix = entry.get("orientMat")
    vector = matrix.get("firstVec")
    
    temp = [x,0,0,0,0,0]
    
    temp[3] = vector[0]
    temp[4] = vector[1]
    temp[5] = vector[2]
    
    soa.append(temp)

X, Y, Z, U, V, W = zip(*soa)
fig = plt.figure()
ax = fig.add_subplot(111, projection='3d')

# Scaling
ax.set_xlim([0, scale])
ax.set_ylim([0, scale])
ax.set_zlim([0, scale])

# ------------
# Vectors
ax.quiver(X, Y, Z, U, V, W)
# ------------


# ------------
# Points
#ax.scatter(points_x, points_y, points_z, c='r', marker='o')
# ------------

print(path)

plt.show()