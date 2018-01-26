# -*- coding: utf-8 -*-
"""
Created on Thu Jan 25 17:11:23 2018

@author: Benedikt Bosshammer
"""
from pyquaternion import Quaternion


class TrapezIntegration:

    def __init__(self, p_init=[0,0,0], v_init=[0,0,0]): 
        self.o_prev = None
        self.v_prev = v_init
        self.a_prev = None

        self.p_prev = p_init
    
    def update(self, o, acc, dt):
        
        if (o_prev == None):
            self.o_prev = o
        else:
            self.o_prev = meanOfQuaternions(o, self.o_prev)
            

        #rotate acc to fit to orientation
        acc = o.rotate(acc)

        # Update velocity with time and acceleration
        if (self.a_prev == None):
            vel = add(self.v_prev, multiplyByScale(sub(acc, [0,0,9.81]), dt))
        else:
            vel = add(self.v_prev, multiplyByScale(add(sub(acc, [0,0,9.81]), sub(self.a_prev, [0,0,9.81])), dt/2))

        self.a_prev = acc

        # Positio update using trapez integration over velocity
        p = add(self.p_rev, multiplyByScale(add(vel, self.v_prev), dt/2))

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
        return [a[0]*b, a[1]*b, a[2]*b[2]]

    @staticmethod
    def meanOfQuaternions(a, b):
        #Create quaternion
        aElements = a.elements
        bElements = b.elements
        temp = []
        for i in (0,3):
            temp[i] = 0.5 * (aElements[i] + bElements[i])

        return Quaternion(temp)        
        
data = json.load(open('../Sensordaten/konvertiert/' + PATH + '.json'))

trapez = TrapezIntegration()

time_prev = 0
for i in range(0, data.length):
    entry = data[i]

    # Read orientation
    orientRot = [entry.get("orient_x"), entry.get("orient_y"), entry.get("orient_z") ]
    o = Quaternion(axis=orientRot, degrees=90)

    # Read acc
    acc = [entry.get("acc_x"), entry.get("acc_y"), entry.get("acc_z")]

    dt = entry.get("time") - time_prev
    time_prev = entry.get("time")

    trapez.update(o, acc, dt)
    position = trapez.p
    print(position)



