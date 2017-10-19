
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
    
    public static double[] quaternionMultiplication(double[] p, double[] q){
      if (q.length != 4) throw new IllegalArgumentException("q.length must be 4 but it is "+q.length);
      if (p.length != 3) throw new IllegalArgumentException("p.length must be 3 but it is "+p.length);
      
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
}
