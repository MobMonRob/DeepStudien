/**
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
}
