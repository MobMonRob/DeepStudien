/**
 * Utilities to work with inertial sensors.
 * 
 * @author Oliver Rettig
 */
public class IMUUtils {

    public static final double g = 9.81;
  
  /**
   * Cancel gravity by transforming the given acceleration vector form sensor coordinates into
   * global coordinates and then subtrac the gravity.
   * 
   * Implementation follows the formulae from [Brzostowski2014].
   * 
   * @param a acceeleration in the sensors coordinates including gravity.
   * @param o quaternion describing the sensors coordinate system in global coordinates.
   * @return acceleration in global coordinates without gravity.
   */
  public static double[] cancelGravity(double[] a, double[] o){
      double d = o[3]*o[3]-(o[0]*o[0]+o[1]*o[1]+o[2]*o[2]);
      
      double[][] t = new double[3][3];
      // 1. Summand
      t[0][0] = d;
      t[1][1] = d;
      t[2][2] = d;
      
      // 2. Summand
      MatrixUtils.add(t,VectorUtils.outerProduct(new double[]{2*o[0], 2*o[1], 2*o[3]}, new double[]{o[0],o[1],o[2]}));
         
      // 3. Summand
      double[][] e = MatrixUtils.skew(new double[]{o[0],o[1],o[2]});
      MatrixUtils.scale(e, 2*o[3]);
      MatrixUtils.add(t,e);
      
      double[] result =  VectorUtils.transform(t, a);
      result[2] -= g;
      return result;
  }
}
