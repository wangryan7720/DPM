package localization;
//DPM Group 8
//Gareth Peters
//ID:260678626
//LuoQing(Ryan) Wang
//ID:260524744
import lejos.hardware.Button;
import lejos.hardware.Sound;
import lejos.hardware.sensor.SensorMode;
import lejos.robotics.SampleProvider;

public class LightLocalizer {
	private Odometer odo;
	private SensorMode colorSensor;
	private float[] colorData;	
	private Navigation navi;
	private double Black_Line=0.35;
	private double Sensor_Offset=7;
	private boolean crossLine=false;
	private static final int ROTATE_SPEED = 150;
	private static final int ROTATE_SLOW=100;
	private final double radius=2.1;
	private final double width=15.8;
	
	public LightLocalizer(Odometer odo, SensorMode colorSensor, float[] colorData) {
		this.odo = odo;
		this.colorSensor = colorSensor;
		this.colorData = colorData;
		navi=new Navigation(odo);
	}

	public void doLocalization() {
		// drive to location listed in tutorial
//Method 1

		while(!crossLine()){
			navi.forward();
		}
		navi.Stop();
		odo.setY(0-Sensor_Offset);
		navi.turnTo(0);
		L4.getLeftMotor().setSpeed(ROTATE_SPEED);
		L4.getRightMotor().setSpeed(ROTATE_SPEED);
		L4.getLeftMotor().rotate(convertAngle(radius, width, 90.0), true);
		L4.getRightMotor().rotate(-convertAngle(radius, width, 90.0), false);
		while(!crossLine()){
			navi.forward();
		}
		navi.Stop();
		odo.setY(0-Sensor_Offset);
		odo.setX(0-Sensor_Offset);
		odo.setTheta(90);
		navi.travelTo(0, 0);
		navi.turnTo(0);
		
//Method 2
		//set up line count 
		int linecount=0;
		double line[]=new double[4];
		
		//it turns 45degree
		//move forward until it sees a black line
//		L4.getLeftMotor().rotate(convertAngle(radius, width, 45.0), true);
//		L4.getRightMotor().rotate(-convertAngle(radius, width, 45.0), false);
//		while(!crossLine()){
//		navi.forward();
//	}
//		navi.forward();
//		
		
		//Running from +y,-x,-y,+x
		while(linecount<4){
			L4.getLeftMotor().setSpeed(ROTATE_SLOW);
			L4.getRightMotor().setSpeed(ROTATE_SLOW);
			L4.getLeftMotor().backward();
			L4.getRightMotor().forward();
			
			//record the theta
			if(crossLine()){
				line[linecount]=odo.getTheta();
				linecount++;
				crossLine=false;
				
			}
		}
		navi.Stop();
		
		//do the calculation
		double thetaY=line[0]-line[2];
		double thetaX=line[3]-line[1];
		double X=Sensor_Offset*Math.cos(Math.toRadians(thetaY)/2);
		double Y=Sensor_Offset*Math.cos(Math.toRadians(thetaX)/2);
		
		//correction
		odo.setX(X);
		odo.setY(Y);
		
		//move to origin and face 0degree
		navi.travelTo(0, 0);
		navi.turnTo(0);
	}

	
	public boolean crossLine(){
		
		//check if it pass a line
		crossLine=false;
		colorSensor.fetchSample(colorData, 0);
		if(colorData[0]<Black_Line){
			crossLine=true;
			Sound.setVolume(Sound.VOL_MAX);
			Sound.playNote(Sound.PIANO,784,250);
		}
		
		return crossLine;
	}
	//convert distance from lab2
	private static int convertDistance(double radius, double distance) {
		return (int) ((180.0 * distance) / (Math.PI * radius));
	}
	//convert angle from lab2
	private static int convertAngle(double radius, double width, double angle) {
		return convertDistance(radius, Math.PI * width * angle / 360.0);
	}
}
