
/**
 * @author Oliver Rettig
 */
public class QuternionUtils {
    
    public static void normalizeVector(double[] x){
      if (x.length != 3) throw new IllegalArgumentException("x.length must be 3 but it is "+x.length);
      double norm = Math.sqrt(x[0]*x[0] + x[1]*x[1] + x[2]*x[2]);
      x[0] /= norm;
      x[1] /= norm;
      x[2] /= norm;
    }

    public static void normalizeQuaternion(double[] q){
      if (q.length != 4) throw new IllegalArgumentException("q.length must be 4 but it is "+q.length);
      double norm = Math.sqrt(q[0]*q[0] + q[1]*q[1] + q[2]*q[2] + q[3]*q[3]);
      q[0] /= norm;  
      q[1] /= norm;
      q[2] /= norm;
      q[3] /= norm;
    }

    public static double[] invertQuaternion(final double[] q){
      if (q.length != 4) throw new IllegalArgumentException("q.length must be 4 but it is "+q.length);
     
      // Assumes quaternion is normalized.
      return new double[]{q[0], -q[1], -q[2], -q[3]};
    }

    public static double[] scaleQuaternion(double gain, double[] dq){
      if (dq.length != 4) throw new IllegalArgumentException("dq.length must be 4 but it is "+dq.length);
     
      if (dq[0] < 0.0){//0.9 
        // Slerp (Spherical linear interpolation):
        double angle = Math.acos(dq[0]);
        double A = Math.sin(angle*(1.0 - gain))/Math.sin(angle);
        double B = Math.sin(angle * gain)/Math.sin(angle);
        dq[0] = A + B * dq[0];
        dq[1] = B * dq[1];
        dq[2] = B * dq[2];
        dq[3] = B * dq[3];
      } else {
        // Lerp (Linear interpolation):
        dq[0] = (1.0 - gain) + gain * dq[0];
        dq[1] = gain * dq[1];
        dq[2] = gain * dq[2];
        dq[3] = gain * dq[3];
      }
      normalizeQuaternion(dq);  
      return dq;
    }
    
    public static double[] mean(double[] q1, double[] q2){
        if (q1.length != 4) throw new IllegalArgumentException("q1.length must be 4 but it is "+q1.length);
        if (q2.length != 4) throw new IllegalArgumentException("q2.length must be 4 but it is "+q2.length);
        double[] q = new double[4];
        for (int i=0;i<4;i++){
            q[i] = 0.5d * (q1[i]+q2[i]);
        }
        return q;
    }
    
    public static double[] quaternionMultiplication(double[] p, double[] q){
      if (q.length != 4) throw new IllegalArgumentException("q.length must be 4 but it is "+q.length);
      if (p.length != 4) throw new IllegalArgumentException("p.length must be 3 but it is "+p.length);
      
      // r = p q
      return new double[]{p[0]*q[0] - p[1]*q[1] - p[2]*q[2] - p[3]*q[3],
                          p[0]*q[1] + p[1]*q[0] + p[2]*q[3] - p[3]*q[2],
                          p[0]*q[2] - p[1]*q[3] + p[2]*q[0] + p[3]*q[1],
                          p[0]*q[3] + p[1]*q[2] - p[2]*q[1] + p[3]*q[0]};
    }

    public static double[] rotateVectorByQuaternion(double[] x, double[] q){ 
        
      if (q.length != 4) throw new IllegalArgumentException("q.length must be 4 but it is "+q.length);
      if (x.length != 3) throw new IllegalArgumentException("x.length must be 3 but it is "+x.length);
     
        //TODO überprüfen
      return new double[]{(q[0]*q[0] + q[1]*q[1] - q[2]*q[2] - q[3]*q[3])*x[0] + 2*(q[1]*q[2] - q[0]*q[3])*x[1] + 2*(q[1]*q[3] + q[0]*q[2])*x[2],
                           2*(q[1]*q[2] + q[0]*q[3])*x[0] + (q[0]*q[0] - q[1]*q[1] + q[2]*q[2] - q[3]*q[3])*x[1] + 2*(q[2]*q[3] - q[0]*q[1])*x[2],
                           2*(q[1]*q[3] - q[0]*q[2])*x[0] + 2*(q[2]*q[3] + q[0]*q[1])*x[1] + (q[0]*q[0] - q[1]*q[1] - q[2]*q[2] + q[3]*q[3])*x[2]};
    }
    
    // ROS uses the Hamilton quaternion convention (q[0] is the scalar). However, 
    // the ROS quaternion is in the form [x, y, z, w], with w as the scalar.
    public static double[] hamiltonToTFQuaternion(double[] q){
        if (q.length != 4) throw new IllegalArgumentException("q.length must be 4 but it is "+q.length);
        return new double[]{q[1], q[2], q[3], q[0]};
    }
    
    /**
     *  Converts a quaternion to euclid angle in radian unit
     *  q1 can be non-normalised quaternion 
     *  algorithm based on: http://www.euclideanspace.com/maths/geometry/rotations/conversions/quaternionToEuler/index.htm
     *  @return euclid angle in radian unit
     *  @throws Exception in case of singularity*/

    public static double[] toEuclidAnglesRadian(double[] q) throws Exception {
    	 if (q.length != 4) throw new IllegalArgumentException("q.length must be 4 but it is "+q.length);
    	 double heading, attitude, bank = 0;
    	
        double sqw = q[0]*q[0];
        double sqx = q[1]*q[1];
        double sqy = q[2]*q[2];
        double sqz = q[3]*q[3];
    	double unit = sqx + sqy + sqz + sqw; // if normalised is one, otherwise is correction factor
    	double test = q[1]*q[2] + q[3]*q[0];
    	if (test > 0.499*unit) { // singularity at north pole
    		heading = 2 * Math.atan2(q[1],q[0]);
    		attitude = Math.PI/2;
    		bank = 0;
    		throw new Exception("Singularity at north pole");
    	}
    	if (test < -0.499*unit) { // singularity at south pole
    		heading = -2 * Math.atan2(q[1],q[0]);
    		attitude = -Math.PI/2;
    		bank = 0;
    		throw new Exception("Singularity at south pole");
    	}
        heading = Math.atan2(2*q[2]*q[0]-2*q[1]*q[3] , sqx - sqy - sqz + sqw);
    	attitude = Math.asin(2*test/unit);
    	bank = Math.atan2(2*q[1]*q[0]-2*q[2]*q[3] , -sqx + sqy - sqz + sqw);
    	double[] euclidAngel = {bank, heading, attitude};
    	return euclidAngel;
    }
    
    /**
     *  Converts a quaternion to euclid angle in degree unit
     *  q1 can be non-normalised quaternion 
     *  @return euclid angle in degree unit
     *  @throws Exception in case of singularity*/
    public static double[] toEuclidAnglesDegree(double[] q) throws Exception {
    	double[] e = QuternionUtils.toEuclidAnglesRadian(q);
    	double[] degree = {Math.toDegrees(e[0]), Math.toDegrees(e[1]), Math.toDegrees(e[2])};
    	return degree;
    }
    
    public static double[] toRotationMatrix(double[] q) {
    	double w = q[0], x = q[1], y = q[2], z = q[3];
    	double[] r = {
    			1-2*y*y-2*z*z,2*x*y-2*z*w, 2*x*z+2*y*w,
    			2*x*y+2*z*w, 1-2*x*x-2*z*z, 2*y*z-2*x*w,
    			2*x*z-2*y*w, 2*y*z+2*x*w, 1-2*x*x-2*y*y
		
    	};
    	
    	
		return r;
    	
    }
}
