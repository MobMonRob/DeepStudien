/**
 *
 * @author Oliver Rettig
 */
public class TrapezIntegrationFilter {
    
    private double[] o;
    
    // acceleration in the navigation frame
    private double[] a;
    
    private double[] v;
    private double[] p;
    
    
    // previous values
    private double[] a_prev;
    private double[] v_prev;
    private double[] p_prev;
    
    // orientation estimation
    // C = C_prev*(2*eye(3)+(ang_rate_matrix*dt))/(2*eye(3)-(ang_rate_matrix*dt));
    
    /**
     * Update acceleration, velocity and position in navigation frame.
     * 
     * @param o_new orientation
     * @param a_sensor acceleration in sensor frame
     * @param dt 
     * @throws IllegalArgumentException,  if Initial velocity or position is not set before first invocation.
     */
    void update(final double o_new[], final double[] a_sensor, final double dt){
        
        if (v_prev == null) throw new IllegalArgumentException("update() invoked, before initial v has been set!");
        if (p_prev == null) throw new IllegalArgumentException("update() invoked, before initial p has ben set!");
        
        // Transforming the acceleration from sensor frame to navigation frame.
        
        if (o != null){
            o = QuaternionUtils.mean(o, o_new);
        } else {
            o = new double[]{o_new[0], o_new[1], o_new[2], o_new[3]};
        }
        
        a = QuaternionUtils.rotateVectorByQuaternion(a_sensor, o);
        
        // Velocity and position estimation using trapeze integration.
        
        if (a_prev == null){
            v = VectorUtils.createAdd(v_prev,VectorUtils.createScale(VectorUtils.createSub(a,new double[]{0,0,ComplementaryFilter.kGravity}), dt));
        } else {
            //vel_n(:,t) = vel_n(:,t-1) + ((acc_n(:,t) - [0; 0; g] )+(acc_n(:,t-1) - [0; 0; g]))*dt/2;
            v = VectorUtils.createAdd(v_prev, VectorUtils.createScale(VectorUtils.add(VectorUtils.createSub(a,new double[]{0,0,ComplementaryFilter.kGravity}),
                            VectorUtils.createSub(a_prev,new double[]{0,0,ComplementaryFilter.kGravity})),dt/2d));
            
        }
        a_prev = a; 
        
        //pos_n(:,t) = pos_n(:,t-1) + (vel_n(:,t) + vel_n(:,t-1))*dt/2;
        p = VectorUtils.add(p_prev,VectorUtils.createScale(VectorUtils.createAdd(v,v_prev),dt/2d));
        // p_prev ist damit automatisch upgedated
        
        v_prev = v;
    }
    
    /**
     * Get acceleration in navigation frame;
     * 
     * @return acceleration
     */
    public double[] getAcceleration(){
        return a;
    }
    
    /**
     * Get Velocity in navigation frame.
     * 
     * @return velocity
     */
    public double[] getVelocity(){
        return v;
    }
    
    /**
     * Get position in navigation frame.
     * 
     * @return position
     */
    public double[] getPosition(){
        return p;
    }
    
    /**
     * Set initial position.
     * 
     * @param p position
     */
    public void setPosition(double[] p){
        this.p_prev = p;
    }
    /**
     * Set initial velocity.
     * 
     * @param v velocity
     */
    public void setVelocity(double[] v){
        this.v_prev = v;
    }
}
