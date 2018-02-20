/**
 *
 * @author Oliver Rettig
 */
public class MatrixUtils {
    
     /**
       * Add b to a.
       * 
       * @param a
       * @param b
       * @return a+b
       */
     public static double[][] add(double[][] a, double[][] b){
         if (a.length != a[0].length || b.length != b[0].length || a.length != b.length){
            throw new IllegalArgumentException("All matrix dims must be equal...");
         }
         for (int row=0;row<a.length;row++){
           for (int col=0;col<a.length;col++){
             a[row][col] += b[row][col];
           }
         }
         return a;
     }
     
     public static double[][] scale(double[][] m, double scale){
         for (int row=0;row<m.length;row++){
           for (int col=0;col<m.length;col++){
             m[row][col] *= scale;
           }
         }
         return m;
     }
     
     /**
      * Create skew symmetric matrix from a given vector.
      * @param v
      * @return 
      */
     public static double[][] skew(double[] v){
         double[][] result = new double[v.length][v.length];
         result[0][1] = -v[2];
         result[0][2] = v[1];
         result[1][0] = v[2];
         result[1][2] = -v[0];
         result[2][0] = -v[2];
         result[2][1] = v[0];
         return result;
     }
}

