/**
 * @author Oliver Rettig
 */
public class IMUUtils {

  public final double g = 9.81;
  
  public static double[] cancelGravity(double[] a, double[] o){
      double d = o[3]*o[3]-(o[0]*o[0]+o[1]*o[1]+o[2]*o[2]);
      
      double[][] c = new double[3][3];
      c[0][0] = d;
      c[1][1] = d;
      c[2][2] = d;
      
      MatrixUtils.add(c,VectorUtils(new double[]{2*o[0], 2*o[1], 2*o[3]},new double[]{o[0],o[1],o[2]});
         
      double[][] e = new double[3][3];
      e[0][1] = 
                      
      MatrixUtils.add(c,e);
                      
      double[] result = new double[4];
      
      //TODO
      
      return result;
  }
}