/**
 *
 * @author Oliver Rettig
 */
public class MatrixUtils {
    
     /**
       *
       */
     public static double[] add(double[][] a, doube[][] b){
         if (a.length != a[0].length || b.length != b[0].length || a.length != b.length){
            throw new IllegalArgumentException("All matrix dims must be equal...");
         }
         for (int row=0;row<row.length;row++){
           for (int col=0;col<col.length;col++){
             a[row][col] += b[row][col];
           }
         }
         return a;
     }

