
public class RotationMatrix {
	double[] firstVec = {0, 0, 0};
	double[] secondVec = {0, 0, 0};
	double[] thirdVec = {0, 0, 0};
	
	
	public void setData(double[] d) {
		firstVec[0] = d[0];
		firstVec[1] = d[3];
		firstVec[2] = d[6];
		
		secondVec[0] = d[1];
		secondVec[1] = d[4];
		secondVec[2] = d[7];
		
		thirdVec[0] = d[2];
		thirdVec[1] = d[5];
		thirdVec[2] = d[8];
		
	}
}
