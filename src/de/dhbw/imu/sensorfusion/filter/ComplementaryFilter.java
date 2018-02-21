package de.dhbw.imu.sensorfusion.filter;

import de.dhbw.math.QuaternionUtils;

/**
 * Complementary Filter.
 * 
 * angular_velocity: radian/sec;<p>
 * 
 * linear_acceleration: m/s^2<p>
 * 
 * magnetic field: Tesla.<p>
 *
 * Start by setting the use_mag parameter to false. Once you get good results, 
 * you can set it back to true.<p>
 * 
 * @author Oliver Rettig
 */
public class ComplementaryFilter {
 
    public static double kGravity = 9.81;
    public static double gamma_ = 0.01;

    // Bias estimation steady state thresholds
    public static double kAngularVelocityThreshold = 0.2;
    public static double kAccelerationThreshold = 0.1;
    public static double kDeltaAngularVelocityThreshold = 0.01;

    // Gain parameter for the complementary filter, belongs in [0, 1].
    private double gain_acc_;
    private double gain_mag_;

    // Bias estimation gain parameter, belongs in [0, 1].
    private double bias_alpha_;

    // Parameter whether to do bias estimation or not.
    private boolean do_bias_estimation_;

    // Parameter whether to do adaptive gain or not.
    private boolean do_adaptive_gain_;

    private boolean initialized_;
    private boolean steady_state_;

    // The orientation as a Hamilton quaternion (q0 is the scalar). Represents
    // the orientation of the fixed frame wrt the body frame.
    private double[] q_;
    
    // Bias in angular velocities;
    private final double[] w_prev_;

    // Bias in angular velocities;
    private final double[] w_bias_;

    public ComplementaryFilter(){
        gain_acc_ = 0.01d;
        gain_mag_ = 0.01d;
        bias_alpha_ = 0.01;
        do_bias_estimation_ = true;
        do_adaptive_gain_ = false;
        initialized_ = false;
        steady_state_ = false;
        q_ = new double[]{1,0,0,0};
        
        w_prev_ = new double[]{0,0,0};
        w_bias_ = new double[]{0,0,0};
    }

    void setDoBiasEstimation(boolean do_bias_estimation){
      do_bias_estimation_ = do_bias_estimation;
    }

    boolean getDoBiasEstimation(){
      return do_bias_estimation_;
    }

    /**
     * Set do adaptive gain
     * 
     * @param do_adaptive_gain
     */
    public void setDoAdaptiveGain(boolean do_adaptive_gain){
      do_adaptive_gain_ = do_adaptive_gain;
    }

    /**
     * Get do adaptive gain.
     * 
     * @return 
     */
    public boolean getDoAdaptiveGain(){
      return do_adaptive_gain_;
    }

    /**
     * Set gain for acceleration.
     * 
     * @param gain
     * @return 
     */
    public boolean setGainAcc(double gain){
      boolean result = false;
      if (gain >= 0 && gain <= 1.0){
        gain_acc_ = gain;
        result = true;
      }
      return result;
    }
    
    /**
     * Set gain for magnetometer.
     * 
     * @param gain
     * @return
     */
    public boolean setGainMag(double gain){
      boolean result = false;
      if (gain >= 0 && gain <= 1.0){
        gain_mag_ = gain;
        result = true;
      } 
      return result;
    }

    /**
     * Get gain for acceleration.
     * 
     * @return 
     */
    public double getGainAcc(){
      return gain_acc_;
    }

    /**
     * Get gait for magnetic orientation.
     * 
     * @return magnetic orientation
     */
    public double getGainMag(){
        return gain_mag_;
    }

    /**
     * When the filter is in the steady state, bias estimation will occur (if the
     * parameter is enabled).
     * 
     * @return steady state status
     */
    public boolean getSteadyState(){
        return steady_state_;
    }

    public boolean setBiasAlpha(double bias_alpha){
        boolean result = false;
        if (bias_alpha >= 0 && bias_alpha <= 1.0){
          bias_alpha_ = bias_alpha;
          result = true;
        } 
        return result;
    }

    public double getBiasAlpha(){
        return bias_alpha_;
    }

    /**
     * Set the orientation, as a Hamilton Quaternion, of the body frame wrt the
     * fixed frame.
     * 
     * @param q 
     */
    public void setOrientation(final double[] q){
        // Set the state to inverse (state is fixed wrt body).
        q_ = QuaternionUtils.invertQuaternion(q);
    }

    public double getAngularVelocityBiasX(){
        return w_bias_[0];
    }

    public double getAngularVelocityBiasY(){
        return w_bias_[1];
    }

    public double getAngularVelocityBiasZ(){
        return w_bias_[2];
    }

    /**
     * Update from accelerometer and gyroscope data.
     * 
     * @param a Normalized gravity vector.
     * @param w  Angular veloctiy, in rad/s.
     * @param dt time delta, in seconds.
     * 
     * TODO
     * ärgerlich dass das jetzt public ist wegen der test-class, vielleicht braucht 
     * es ein eigenes Interface in dem die method drin ist
     */
    public void update(final double[] a, final double w[], double dt){
      if (!initialized_){
        // First time - ignore prediction:
        q_ = getMeasurement(a);
        initialized_ = true;
        return;
      }

      // Bias estimation.
      if (do_bias_estimation_)
        updateBiases(a, w);

      // Prediction. =?Time update
      double[] q_pred = getPrediction(w, dt);   
      	
      // Correction (from acc): =? measurement update
      // q_ = q_pred * [(1-gain) * qI + gain * dq_acc]
      // where qI = identity quaternion
      double[] dq_acc = getAccCorrection(a, q_pred);

      double gain;
      if (do_adaptive_gain_){  
        gain = getAdaptiveGain(gain_acc_, a);
      } else {
        gain = gain_acc_;
      }

      QuaternionUtils.scaleQuaternion(gain, dq_acc);

      q_ = QuaternionUtils.quaternionMultiplication(q_pred, dq_acc);

      QuaternionUtils.normalizeQuaternion(q_);
    }

    /**
     * Update orientation, based on the current measurement of acceleration,
     * angular velocity and magnetic field values.
     * 
     * @param a linear accelaration, normalized gravity vector.
     * @param w angular velocity in rad/s.
     * @param m magnetic field, units irrelevant.
     * @param dt [s]
     */
    void update(final double[] a, final double[] w, final double[] m, double dt){
      if (!initialized_){
        // First time - ignore prediction:
        q_ = getMeasurement(a, m);
        initialized_ = true;
        return;
      }

      // Bias estimation.
      if (do_bias_estimation_){
        updateBiases(a, w);
      }
      
      // Prediction.
      double[] q_pred = getPrediction(w, dt);   

      // Correction (from acc): 
      // q_temp = q_pred * [(1-gain) * qI + gain * dq_acc]
      // where qI = identity quaternion
      double[] dq_acc = getAccCorrection(a, q_pred);
      double alpha = gain_acc_;  
      if (do_adaptive_gain_){
         alpha = getAdaptiveGain(gain_acc_, a);
      }
      QuaternionUtils.scaleQuaternion(alpha, dq_acc);

      double[] q_temp = QuaternionUtils.quaternionMultiplication(q_pred, dq_acc);

      // Correction (from mag):
      // q_ = q_temp * [(1-gain) * qI + gain * dq_mag]
      // where qI = identity quaternion
      double[] dq_mag = getMagCorrection(m, q_temp);

      QuaternionUtils.scaleQuaternion(gain_mag_, dq_mag);

      q_ = QuaternionUtils.quaternionMultiplication(q_temp, dq_mag);

      QuaternionUtils.normalizeQuaternion(q_);
    }

    private boolean checkState(final double[] a, final double[] w){
      boolean result = true;
      double acc_magnitude = Math.sqrt(a[0]*a[0] + a[1]*a[1] + a[2]*a[2]);
      if (Math.abs(acc_magnitude - kGravity) > kAccelerationThreshold){
        result = false;
      } else if (Math.abs(w[0] - w_prev_[0]) > kDeltaAngularVelocityThreshold ||
          Math.abs(w[1] - w_prev_[1]) > kDeltaAngularVelocityThreshold ||
          Math.abs(w[2] - w_prev_[2]) > kDeltaAngularVelocityThreshold){
        result = false;
      } else if (Math.abs(w[0] - w_bias_[0]) > kAngularVelocityThreshold ||
          Math.abs(w[1] - w_bias_[1]) > kAngularVelocityThreshold ||
          Math.abs(w[2] - w_bias_[2]) > kAngularVelocityThreshold){
        result = false;
      }
      return result;
    }

    private void updateBiases(final double[] a, final double[] w){
      steady_state_ = checkState(a, w);

      if (steady_state_){
        w_bias_[0] += bias_alpha_ * (w[0] - w_bias_[0]);
        w_bias_[1] += bias_alpha_ * (w[1] - w_bias_[1]);
        w_bias_[2] += bias_alpha_ * (w[2] - w_bias_[2]);
      }

      w_prev_[0] = w[0]; 
      w_prev_[1] = w[1]; 
      w_prev_[2] = w[2];
    }

    private double[] getPrediction(final double[] w, double dt){
        
      double[] w_unb = new double[]{w[0] - w_bias_[0], w[1] - w_bias_[1], w[2] - w_bias_[2]};
      double[] q_pred = new double[]
    		  {q_[0] + 0.5*dt*( w_unb[0]*q_[1] + w_unb[1]*q_[2] + w_unb[2]*q_[3]),
               q_[1] + 0.5*dt*(-w_unb[0]*q_[0] - w_unb[1]*q_[3] + w_unb[2]*q_[2]),
               q_[2] + 0.5*dt*( w_unb[0]*q_[3] - w_unb[1]*q_[0] - w_unb[2]*q_[1]),
               q_[3] + 0.5*dt*(-w_unb[0]*q_[2] + w_unb[1]*q_[1] - w_unb[2]*q_[0])};

      QuaternionUtils.normalizeQuaternion(q_pred);
      return q_pred;
    }

    private double[] getMeasurement(final double[] a, final double[] m){

      // q_acc is the quaternion obtained from the acceleration vector representing 
      // the orientation of the Global frame wrt the Local frame with arbitrary yaw
      // (intermediary frame). q3_acc is defined as 0.
      double[] q_acc = new double[4];

      // Normalize acceleration vector.
      QuaternionUtils.normalizeVector(a);
      if (a[2] >=0){
          q_acc[0] =  Math.sqrt((a[2] + 1) * 0.5);	
          q_acc[1] = -a[1]/(2.0 * q_acc[0]);
          q_acc[2] =  a[0]/(2.0 * q_acc[0]);
          q_acc[3] = 0;
      } else {
          double X = Math.sqrt((1 - a[2]) * 0.5);
          q_acc[0] = -a[1]/(2.0 * X);
          q_acc[1] = X;
          q_acc[2] = 0;
          q_acc[3] = a[0]/(2.0 * X);
      }  

      // [lx, ly, lz] is the magnetic field reading, rotated into the intermediary
      // frame by the inverse of q_acc.
      // l = R(q_acc)^-1 m
      double lx = (q_acc[0]*q_acc[0] + q_acc[1]*q_acc[1] - q_acc[2]*q_acc[2])*m[0] + 
          2.0 * (q_acc[1]*q_acc[2])*m[1] - 2.0 * (q_acc[0]*q_acc[2])*m[2];
      double ly = 2.0 * (q_acc[1]*q_acc[2])*m[0] + (q_acc[0]*q_acc[0] - q_acc[1]*q_acc[1] + 
          q_acc[2]*q_acc[2])*m[1] + 2.0 * (q_acc[0]*q_acc[1])*m[2];

      // q_mag is the quaternion that rotates the Global frame (North West Up) into
      // the intermediary frame. q1_mag and q2_mag are defined as 0.
      double gamma = lx*lx + ly*ly;	
      double beta = Math.sqrt(gamma + lx*Math.sqrt(gamma));
      double q0_mag = beta / (Math.sqrt(2.0 * gamma));  
      double q3_mag = ly / (Math.sqrt(2.0) * beta); 

      // The quaternion multiplication between q_acc and q_mag represents the 
      // quaternion, orientation of the Global frame wrt the local frame.  
      // q = q_acc times q_mag 
      double[] q_meas = QuaternionUtils.quaternionMultiplication(q_acc, new double[]{q0_mag, 0, 0, q3_mag}); 
      return q_meas;
    }

    /**
     * Determines the quaternion from the acceleration vector representing 
     * the orientation of the global frame wrt the local frame with arbitrary yaw
     * (intermediary frame). 
     * 
     * q[3] is defined as 0.<p>
     * 
     * @param a acceleration vector
     * @return measurement as quaternion
     */
    private double[] getMeasurement(final double[] a){

      // Normalize acceleration vector.
      QuaternionUtils.normalizeVector(a);

      double[] q = new double[4];

      if (a[2] >=0){
        q[0] =  Math.sqrt((a[2] + 1) * 0.5);	
        q[1] = -a[1]/(2.0 * q[0]);
        q[2] =  a[0]/(2.0 * q[0]);
        q[3] = 0;
      } else {
        double X = Math.sqrt((1 - a[2]) * 0.5);
        q[0] = -a[1]/(2.0 * X);
        q[1] = X;
        q[2] = 0;
        q[3] = a[0]/(2.0 * X);
      }  
      return q;
    }

    private double[] getAccCorrection(final double[] a, final double[] p){
      // Normalize acceleration vector.
      QuaternionUtils.normalizeVector(a);

      // Acceleration reading rotated into the world frame by the inverse predicted
      // quaternion (predicted gravity):
      double[] g = QuaternionUtils.rotateVectorByQuaternion(a, new double[]{p[0],-p[1],-p[2],-p[3]});

      // Delta quaternion that rotates the predicted gravity into the real gravity:
      double dq0 = Math.sqrt((g[2] + 1) * 0.5);
      return new double[]{dq0,	
                          -g[1]/(2.0 * dq0),
                          g[0]/(2.0 * dq0),
                          0.0};
    }

    /**
     * Get magnetic correction.
     * 
     * @param m magnetic vector
     * @param p predicted quaternion
     * @return magnetich correcion values
     */
    private double[] getMagCorrection(final double[] m, final double[] p){

      // Magnetic reading rotated into the world frame by the inverse predicted
      // quaternion:
      double[] l = QuaternionUtils.rotateVectorByQuaternion(m, new double[]{
                               p[0], -p[1], -p[2], -p[3]});

      // Delta quaternion that rotates the l so that it lies in the xz-plane 
      // (points north)
      double gamma = l[0]*l[0] + l[1]*l[1];	
      double beta = Math.sqrt(gamma + l[0]*Math.sqrt(gamma));
      return new double[]{beta / (Math.sqrt(2.0 * gamma)), 0.0, 0.0, l[1] / (Math.sqrt(2.0) * beta)};  
    }

    /**
     * Get the orientation, as a Hamilton Quaternion, of the body frame wrt the
     * fixed frame.
     * 
     * @return orientation as quaternion, the inverse of the state (state is fixed wrt body).
     */
    public double[] getOrientation(){
        return QuaternionUtils.invertQuaternion(q_);
    }

    private double getAdaptiveGain(double alpha, final double[] a){
      double a_mag = Math.sqrt(a[0]*a[0] + a[1]*a[1] + a[2]*a[2]);
      double error = Math.abs(a_mag - kGravity)/kGravity; // fabs
      double factor = 0d;
      double error1 = 0.1;
      double error2 = 0.2;
      double m = 1.0/(error1 - error2);
      double b = 1.0 - m*error1;
      if (error < error1){
        factor = 1.0;
      } else if (error < error2){
        factor = m*error + b;
      } 
      return factor*alpha;
    }
}