

package modules;

import Robot.Anaconda;
import com.sun.squawk.util.MathUtils;
import config.DriveConfig;
import edu.wpi.first.wpilibj.*;
/**
 *
 * @author Ben Evans
 * @author Ankith Uppunda
 */
public class DriveModule extends Module{
    
    private Victor lVictor1, lVictor2, rVictor1, rVictor2;
    private Encoder lEncoder, rEncoder;
    private double distancePerPulse;
    private double leftPower = 0, rightPower = 0;
    private Solenoid gear;
    
    private PIDController driveController;
    private double s = 1;
    private double setpoint = 0;
             
    private double pow = 2;
    
    private int counter = 0;
    
    /**
     * constructor
     * @param lv1 left victor 1 port
     * @param lv2 left victor 2 port
     * @param rv1 right victor 1 port
     * @param rv2 right victor 2 port
     * @param lenca left encoder port a
     * @param lencb left encoder port b
     * @param renca right encoder port a
     * @param rencb right encoder port b
     * @param dist distance per encoder tick
     * @param solenoidPort solenoid port
     */    
    public DriveModule(int lv1, int lv2, int rv1, int rv2, int lenca, int lencb, int renca, int rencb, double dist, int solenoidPort){
        lVictor1 = new Victor(lv1);
        lVictor2 = new Victor(lv2);
        rVictor1 = new Victor(rv1);
        rVictor2 = new Victor(rv2);
        
        lEncoder = new Encoder(lenca, lencb);
        rEncoder = new Encoder(renca, rencb);
        distancePerPulse = dist;
        gear = new Solenoid(solenoidPort);
        setupEncoders();
        
        driveController = new PIDController(DriveConfig.KP, DriveConfig.KI, DriveConfig.KD, new PIDSource() {

            public double pidGet() {
                return rEncoder.getDistance();//(lEncoder.getDistance() + rEncoder.getDistance()) / 2.0;
            }
            
        }, new PIDOutput() {

            public void pidWrite(double d) {
                if(driveController.isEnable()){
                    setPower(d, d * s);
                }
            }
        });
                
        driveController.setOutputRange(-0.5, 0.5);
        s = DriveConfig.KS;
    }
    /**
     * starts encoder and sets distance
     */    
    private synchronized void setupEncoders(){
        lEncoder.start();
        rEncoder.start();
        lEncoder.setDistancePerPulse(distancePerPulse);
        rEncoder.setDistancePerPulse(distancePerPulse);
        rEncoder.setReverseDirection(true);
    }
    
    public synchronized void disablePID(){
        driveController.disable();
    }
    
    /**
     * resets encoder, drive controller
     */    
    public synchronized void resetEncoders(){
        lEncoder.reset();
        rEncoder.reset();
    }
    /**
     * sets gear
     * @param High 
     */    
    public synchronized void setGear(boolean High){   
        gear.set(High);
    }
    /**
     * reset encoders
     */    
    public void reset(){
        resetEncoders();
        driveController.reset();
        setpoint = 0;
    }
    /**
     * sets victors 
     * @param left
     * @param right 
     */    
    private synchronized void setPower(double left, double right){
        lVictor1.set(left);
        lVictor2.set(left);
        rVictor1.set(-right);
        rVictor2.set(-right);
    }
    /**
     * sets leftPower to left and rightPower to right
     * @param left
     * @param right 
     */
    public synchronized void drive(double left, double right){
        
        if(left > 0)
            left = MathUtils.pow(left, pow);
        else
            left = -MathUtils.pow(-left, pow);
        
        if(right > 0)
            right = MathUtils.pow(right, pow);
        else
            right = -MathUtils.pow(-right, pow);
        
        leftPower = left;
        rightPower = right;
    }
    
    public synchronized void arcadeDrive(double power, double angle){
        double leftMotorOutput = 0, rightMotorOutput = 0;
        
        if(power > 0.0){
            if(angle > 0.0){
                leftMotorOutput = power-angle;
                rightMotorOutput = Math.max(power, angle);
            }else{
                leftMotorOutput = Math.max(power, -angle);
                rightMotorOutput = power + angle;      
            }
        }else{
            if(angle > 0.0){
                leftMotorOutput = -Math.max(-power, angle);
                rightMotorOutput = power + angle;
            }else{
                leftMotorOutput = power- angle;
                rightMotorOutput = -Math.max(-power, -angle);      
            }
        }
        
        drive(leftMotorOutput, rightMotorOutput);
    }
    
    public synchronized void setS(double s){
        this.s = s;
    }
    
    public synchronized void setSetpoint(double setpoint){
        this.setpoint = setpoint;
    }
    
    /**
     * returns power of left side
     * @return leftPower 
     */    
    public synchronized double getLeftPower(){
        return leftPower;
    }
    /**
     * return power of right side
     * @return rightPower
     */    
    public synchronized double getRightPower(){
        return rightPower;
    }
    
    /**
     * the log method for DriveModule
     * @return the log data as XML
     */
    
    public String getLogData(){
        String line1 = "\t\t<data name=\"leftPower\" value=\""+lVictor1.get()+"\">\n";
        String line2 = "\t\t<data name=\"rightPower\" value=\""+rVictor1.get()+"\">\n";
        String line3 = "\t\t<data name=\"leftEncoder\" value=\""+lEncoder.get()+"\">\n";
        String line4 = "\t\t<data name=\"rightEncoder\" value=\""+rEncoder.get()+"\">\n";
        String line5123445454 = "\t\t<data name=\"gear\" value=\""+(gear.get() ? "HIGH" : "LOW")+"\">";
        return line1+line2+line3+line4+line5123445454;
    }
    
    public boolean getGear(){
        return gear.get();
    }
    
    /**
     * implementation of run method for modules
     */    
    public void run(){
        while(true){
            
            if(counter % 20 == 1 && Robot.Anaconda.getInstance().isAutonomous()){
                //System.out.println(toString());           
            }
            counter++;
            
            if(enabled){
                
                if(autoMode){
                    setGear(false);
                    driveController.enable();
                    driveController.setSetpoint(setpoint);
                }else{
                    driveController.disable();
                    driveController.reset();
                    setPower(leftPower, rightPower);
                                        
                }
                
            }else{
                // reset here or main?
            }
                        
            Timer.delay(0.05);
        }
    }
        
    public synchronized void setPID(double p, double i, double d){
        driveController.setPID(p, i, d);
    }
    
    public synchronized void setDriveExponent(double exp){
        pow = exp;
    }
    
    public synchronized double getDriveExponent(){
        return pow;
    }
    
    public synchronized String toString(){
        return "Left: " + lVictor1.get() + " Right: " + rVictor1.get() + " Pow: " + pow + " Auto: " + autoMode + " LTicks: " + lEncoder.get() + " RTicks: " + rEncoder.get() 
                + " Distance: " + lEncoder.getDistance() + " Setpoint: " + driveController.getSetpoint() + " S: " + s + " P: " + driveController.getP() + " I: " + driveController.getI() + " D: " + driveController.getD();
    }
}
