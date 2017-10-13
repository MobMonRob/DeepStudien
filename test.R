setwd("U:/Projektideen/IMUs/150Hz")

# json Datei einlesen
library(jsonlite)
data <- fromJSON('2017_09_26_23_10_mpu9250.json')

#offset vom timestamp abziehen
#data[,10] = data[,10] - data[1,10]

# Zeitstempel an den Anfang stellen
data<-subset(data, select=c("Ts", "Ac_x",   "Ac_y",   "Ac_z",  "Gy_x",  "Gy_y",  "Gy_z", "Ma_x", "Ma_y",  "Ma_z"))

# Überlaufende Zeitstempel beheben
# handle timestamp overflow
#TODO
# test ob nachfolgender Wert kleiner, dann offset um vorherigen Wert erhoehen und für alle nachfolgenden aufaddieren


# int Zeitstempel in posixct umwandeln , format = "%Y-%m-%d %H:%M:%OS"
data$Ts = data$Ts / 1000
data$Ts = as.POSIXct(data$Ts, origin="1970-01-01", tz="Europe/London")


# in zoo umwandeln
# data_zoo = read.zoo(data)

# zeigt, dass ms korrekt vorhanden sind
#strftime(data$Ts,'%Y-%m-%d %H:%M:%OS3')

# mittleren Abstand der Zeitstempel bestimmen
dist = 0
for (i in 2:length(data$Ts)){dist <- dist+(data$Ts[i]-data$Ts[i-1])}
dist = dist/length(data$Ts)

# realign um gleiche Zeitschritte zu bekommen
library(zoo)
seq1 = zoo(order.by=seq(min(data$Ts),max(data$Ts),dist))
mer1 = merge(zoo(x=data[2:10],order.by=data$Ts),seq1)
dataL = na.approx(mer1)

# plot
plot(dataL, plot.type ="multiple")

# TODO
#library(ggplot2)
