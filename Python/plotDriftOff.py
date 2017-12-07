#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""
Created on Thu Dec  7 12:37:57 2017

@author: bene
"""

import numpy as np
from numpy import (array, dot, arccos, clip)
from numpy.linalg import norm
import matplotlib.pyplot as plt
import json


PATH = 'ruhig_8'
#PATH = 'sensorCalculated/ruhig_8'

# load sensordata
data = json.load(open('../Sensordaten/konvertiert/' + PATH + '.json'))

length = len(data)

firstDrift = [0]
secondDrift = [0]
thirdDrift = [0]

initialMatrix = data[0].get("orientMat")
initialFirstVector = initialMatrix.get("firstVec")
initialSecondVector = initialMatrix.get("secondVec")
initialThirdVector = initialMatrix.get("thirdVec")

axis = [0]

for x in range(1, length):
    
    axis.append(x)
    
    # get enry of the json
    entry = data[x]
    
    # get the entry's orientationMatrix
    matrix = entry.get("orientMat")
    
    firstVector = matrix.get("firstVec")
    secondVector = matrix.get("secondVec")
    thirdVector = matrix.get("thirdVec")

    firstDrift.append(calcAngle(initialFirstVector, firstVector))
    secondDrift.append(calcAngle(initialSecondVector, secondVector))
    thirdDrift.append(calcAngle(initialThirdVector, thirdVector))

def calcAngle (initialVector, measuredVector):
    u = initialVector
    v = measuredVector
    c = dot(u,v)/(norm(u)*norm(v)) # -> cosine of the angle
    angle = arccos(clip(c, -1, 1)) # if you really want the angle
    angle = np.degrees(angle)
    return angle

plt.plot(axis, firstDrift, 'r-')
plt.plot(axis, secondDrift, 'b-')
plt.plot(axis, thirdDrift, 'g-')
plt.show()