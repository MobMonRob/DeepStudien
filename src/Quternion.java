
public class Quternion {
	double w = 0;
	double x = 0;
	double y = 0;
	double z = 0;
	
	public void setData(double[] d) throws Exception {
		if (d.length != 4) throw new Exception("Quaternion length wrong");
		
		w =d[0];
		x = d[1];
		y = d[2];
		z = d[3];
	}

}
