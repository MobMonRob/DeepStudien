package de.dhbw.math;

/**
 * Utilities to work with vectors.
 * 
 * @author Oliver Rettig
 */
public class VectorUtils {
    
     public static double[] scale(double[] p, double dt){
        if (p.length != 3) throw new IllegalArgumentException("p.length must be 3 but it is "+p.length);
        for (int i=0;i<p.length;i++){
            p[i] *= dt;
        }
        return p;
     }
     public static double[] createScale(double[] p, double dt){
        if (p.length != 3) throw new IllegalArgumentException("p.length must be 3 but it is "+p.length);
        double[] result = new double[3];
        for (int i=0;i<result.length;i++){
            result[i] = p[i] * dt;
        }
        return result;
     }
     public static double[] createAdd(double[] p, double[] q){
        if (q.length != 3) throw new IllegalArgumentException("q.length must be 3 but it is "+q.length);
        if (p.length != 3) throw new IllegalArgumentException("p.length must be 3 but it is "+p.length);
        double[] result = new double[3];
        for (int i=0;i<p.length;i++){
            result[i] = p[i]+q[i];
        }
        return result;
     }
     public static double[] createSub(double[] p, double[] q){
        if (q.length != 3) throw new IllegalArgumentException("q.length must be 3 but it is "+q.length);
        if (p.length != 3) throw new IllegalArgumentException("p.length must be 3 but it is "+p.length);
        double[] result = new double[3];
        for (int i=0;i<p.length;i++){
            result[i] = p[i]-q[i];
        }
        return result;
     }
     public static double[] add(double[] p, double[] q){
        if (q.length != 3) throw new IllegalArgumentException("q.length must be 3 but it is "+q.length);
        if (p.length != 3) throw new IllegalArgumentException("p.length must be 3 but it is "+p.length);
        for (int i=0;i<p.length;i++){
            p[i] = p[i]+q[i];
        }
        return p;
     }
     public static double[] sub(double[] p, double[] q){
        if (q.length != 3) throw new IllegalArgumentException("q.length must be 3 but it is "+q.length);
        if (p.length != 3) throw new IllegalArgumentException("p.length must be 3 but it is "+p.length);
        for (int i=0;i<p.length;i++){
            p[i] = p[i]-q[i];
        }
        return p;
     }
    
    public static double[][] outerProduct(double[] u, double[] v){
        if (u.length != 3) throw new IllegalArgumentException("u.length must be 3 but it is "+u.length);
        if (v.length != 3) throw new IllegalArgumentException("v.length must be 3 but it is "+v.length);
        double[][] result = new double[3][3];
        result[0][0] = u[0]*v[0];
        result[0][1] = u[0]*v[1];
        result[0][2] = u[0]*v[2];
        result[1][0] = u[1]*v[0];
        result[1][1] = u[1]*v[1];
        result[1][2] = u[1]*v[2];
        result[2][0] = u[2]*v[0];
        result[2][1] = u[2]*v[1];
        result[2][2] = u[2]*v[2];
        return result;
    }
    
    /**
     * Transform the given vector v by the given matrix t.
     * 
     * @param t
     * @param v
     * @return transformed vector v (the given vector v is not overwritten...).
     * @throws IllegalArgumentException if the given matrix is not quatratic or its
     * dimensions is not identical with the dimension of the given vector v.
     */
    public static double[] transform(double[][] t, double[] v){
        if (t.length != v.length || t.length != t[0].length) 
            throw new IllegalArgumentException("t.length must be t[0].length and must be v.length!");
        double[] result = new double[t.length];
        for (int row=0;row<t.length;row++){
            for (int col=0;col<t.length;col++){
                result[row] += t[row][col] * v[col];
            }
        }
        return result;
    }
}
