import json

class Converter:
    def csvToJson(self, name):
        file = open("../Sensordaten/android/" + name + ".csv", "r")
        output =  open("../Sensordaten/konvertiert/" + name + ".json", "w")
        list = []
        firstLine = 0
        for line in file:
            if firstLine < 2:
                firstLine = firstLine+1
            else:
                split = line.split(";")
                item = {
                    'ac_x': split[0],
                    'ac_y': split[1],
                    'ac_z': split[2],
                    'gy_x': split[9],
                    'gy_y': split[10],
                    'gy_z': split[11],
                    'mag_x': split[13],
                    'mag_y': split[14],
                    'mag_z': split[15],
                    'orient_x': split[16],
                    'orient_y': split[17],
                    'orient_z': split[18],
                    'time': split[31]
                }
                list.append(item)
        output.write(json.dumps(list))



Converter().csvToJson("ruhig")