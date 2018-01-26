# -*- coding: utf-8 -*-
"""
Created on Thu Jan 25 17:11:23 2018

@author: Benedikt Bosshammer
"""
from pyquaternion import Quaternion
import json
import sys


class TrapezIntegration:
    o_prev = Quaternion()
    v_prev = None
    a_prev = None
    p_prev = None
    def __init__(self, p_init=[0,0,0], v_init=[0,0,0]): 
        
        self.v_prev = v_init
        self.p_prev = p_init

    def update(self, o, acc, dt):
        
        if (self.o_prev):
            self.o_prev = o
        else:
            self.o_prev = self.meanOfQuaternions(o, self.o_prev)
            

        #rotate acc to fit to orientation
        acc = o.rotate(acc)

        # Update velocity with time and acceleration
        if (self.a_prev == None):
            vel = self.add(self.v_prev,self.multiplyByScale(self.sub(acc, [0,0,9.81]), dt))
        else:
            vel = self.add(self.v_prev, self.multiplyByScale(self.add(self.sub(acc, [0,0,9.81]), self.sub(self.a_prev, [0,0,9.81])), dt/2))

        self.a_prev = acc

        # Positio update using trapez integration over velocity
        p = self.add(self.p_prev, self.multiplyByScale(self.add(vel, self.v_prev), dt/2))

        self.p_prev = p
        self.v_prev = vel

        return p

    @staticmethod
    def add(a, b):
        return [a[0]+b[0], a[1]+b[1], a[2]+b[2]]

    @staticmethod
    def sub(a, b):
        return [a[0]-b[0], a[1]-b[1], a[2]-b[2]]  

    @staticmethod
    def multiplyByScale(a, b):
        return [a[0]*b, a[1]*b, a[2]*b]

    @staticmethod
    def meanOfQuaternions(a, b):
        #Create quaternion
        aElements = a.elements
        bElements = b.elements
        temp = []
        for i in (0,3):
            temp[i] = 0.5 * (aElements[i] + bElements[i])

        return Quaternion(temp)        


PATH = "ruhig"
data = json.load(open('../Sensordaten/konvertiert/' + PATH + '.json'))

trapez = TrapezIntegration()

time_prev = 0
for i in range(0, len(data)):
    entry = data[i]

    # Read orientation
    orientRot = [float(entry.get("orient_x")), float(entry.get("orient_y")), float(entry.get("orient_z")) ]
    o = Quaternion(axis=orientRot, degrees=90)

    # Read acc
    acc = [float(entry.get("ac_x")), float(entry.get("ac_y")), float(entry.get("ac_z"))]

    dt = int(entry.get("time")) - time_prev
    time_prev = int(entry.get("time"))

    position = trapez.update(o, acc, dt)
    print(position)



