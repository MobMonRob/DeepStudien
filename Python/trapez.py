# -*- coding: utf-8 -*-
"""
Created on Thu Jan 25 17:11:23 2018

@author: D064868
"""

data = json.load(open('../Sensordaten/konvertiert/' + PATH + '.json'))

ori = 0.0
o_prev = 0.0

v_prev = 0.0
vel = 0.0

a_prev = 0.0
acc = 0.0

pos = 0.0
p_prev = 0.0

orient_x = 0
orient_y = 0
orient_z = 0

# euler vector (orient_x, orient_y, orient_z) to quarternion

for x in range(0, 1):

    entry = data[x]
    
    #orient_x = entry.get("orient_x")
    #orient_y = entry.get("orient_y")
    #orient_z = entry.get("orient_z")
    
# set initial p_prev and v_prev

### update function for every measurement

# update orientation
# -> mean of o and o_new    
    
# rotation acc to ori
    
# set a_prev

# Zeile 51
    
# a_prev = a
    
# p = p_prev + (v(t) + v(t-1) * dt/2)

# v_prev = v