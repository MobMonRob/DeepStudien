package de.dhbw.imu.sensorfusion.test;


public class Euclid {
	double x = 0;
	double y = 0;
	double z = 0;

	
	public void setData(double[] d) throws Exception {
		if (d.length != 3) throw new Exception("Euclid length wrong");
		
		x = d[0];
		y = d[1];
		z = d[2];
	}
}
