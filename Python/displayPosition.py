import matplotlib.pyplot as plt
from matplotlib import interactive
from mpl_toolkits.mplot3d import Axes3D
import numpy as np

import json

PATH = 'ruhig'

# Highest shown value of axis x,y,z
scale = 0.1

# load sensordata
data = json.load(open('../Sensordaten/konvertiert/' + PATH + '.json'))

soa = []

temp = [0,0,0]

max = [0,0,0]

for x in range(0, len(data)):
    entry = data[x]
    
    position = entry.get("posEuclid")
    
    temp = [position.get("x"), position.get("y"), position.get("z")]
    
    if (temp[0] > max[0]):
        max[0] = temp[0]
    if (temp[1] > max[1]):
        max[1] = temp[1]
    if (temp[2] > max[2]):
        max[2] = temp[2]        
    
        
    
    soa.append(temp)
    
#calculate max coord
maxCoor = 0
for x in temp:
    if x > maxCoor:
        maxCoor = x
#scale = maxCoor

# insert generated array
X, Y, Z= zip(*soa)

##### Matplotlib settings #####

fig = plt.figure()
ax = fig.add_subplot(111, projection='3d')

# Scaling
ax.set_xlim([0, scale])
ax.set_ylim([0, scale])
ax.set_zlim([0, scale])

# Write the vectors into the Plot
ax.scatter(X, Y, Z)

##### Matplotlib settings #####