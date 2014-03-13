package config;

/**
 *
 * @author Ankith Uppunda
 */
public class ArmConfig {
   public static final double p = 1.5;//1.27;//0.7;//0.965;//0.7;
   public static final double i = 0;
   public static final double d = 2;//0.5;//1.0;//3.75;//3.5;
   public static final int ROLLER_VICTOR = 6;
   public static final int ARM_VICTOR  = 5;
   public static final int POT_PORT = 1;
   public static final int ROLLER_SPEED = 1;
   
   public static final boolean MOTOR_REVERSED = true;
   
   public static final double ARM_MIN = /*0.8 1.69*/0, ARM_MAX = /*4.04 4.91*/ 3.25;
}