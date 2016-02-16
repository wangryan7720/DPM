package localization;
//DPM Group 8
//Gareth Peters
//ID:260678626
//LuoQing(Ryan) Wang
//ID:260524744
import lejos.robotics.SampleProvider;

public class USLocalizer {
	public enum LocalizationType {
		FALLING_EDGE, RISING_EDGE
	};

	public static int ROTATION_SPEED = 80;

	private Odometer odo;
	private SampleProvider usSensor;
	private float[] usData;
	private LocalizationType locType;
	private Navigation navigator;
	private final double sensor_offset = 20;
	
	//Constructor for the USL
	public USLocalizer(Odometer odo, SampleProvider usSensor, float[] usData, LocalizationType locType) {
		this.odo = odo;
		this.usSensor = usSensor;
		this.usData = usData;
		this.locType = locType;
		this.navigator = new Navigation(odo);
	}
	
	//Performs ultrasonic localization
	public void doLocalization() {
		//Declaration of variables
		double[] pos = new double[3];
		double thetaA = 0.0;
		double thetaB = 0.0;
		//Falling edge
		if (locType == LocalizationType.FALLING_EDGE) {
			//Sets the rotation speed of the wheels and rotates on the spot
			L4.getLeftMotor().setSpeed(ROTATION_SPEED);
			L4.getRightMotor().setSpeed(ROTATION_SPEED);
			L4.getLeftMotor().forward();
			L4.getRightMotor().backward();
			//Gets the viewed distance from the wall
			float distance = getFilteredData();
			//For when the robot starts facing a wall
			while (distance < 43) {
				distance = getFilteredData();
				L4.getLeftMotor().setSpeed(ROTATION_SPEED);
				L4.getRightMotor().setSpeed(ROTATION_SPEED);
				L4.getLeftMotor().forward();
				L4.getRightMotor().backward();
			}
			//Constantly checks for when the distance from the wall
			//is less than 40. When it is, record the angle the odometer
			//reads and then rotate the opposite direction
			while (true) {
				distance = getFilteredData();
				if (distance < 40) {
					L4.getLeftMotor().stop(true);
					L4.getRightMotor().stop(false);
					thetaA = odo.getTheta();
					L4.getLeftMotor().setSpeed(ROTATION_SPEED);
					L4.getRightMotor().setSpeed(ROTATION_SPEED);
					L4.getLeftMotor().backward();
					L4.getRightMotor().forward();
					break;
				}
			}
			//So that it does read the distance again and record a wrong value
			try {
				Thread.sleep(1000);
			} catch (Exception e) {
			}
			//Same thing as before, but to get a second angle
			while (true) {
				distance = getFilteredData();
				if (distance < 40) {
					L4.getLeftMotor().stop(true);
					L4.getRightMotor().stop(false);
					thetaB = odo.getTheta();
					L4.getLeftMotor().setSpeed(ROTATION_SPEED);
					L4.getRightMotor().setSpeed(ROTATION_SPEED);
					L4.getLeftMotor().forward();
					L4.getRightMotor().backward();
					break;
				}
			}
		}
		//For rising edge 
		else {
			//Sets the speed of the motors and rotate
			L4.getLeftMotor().setSpeed(ROTATION_SPEED);
			L4.getRightMotor().setSpeed(ROTATION_SPEED);
			L4.getLeftMotor().backward();
			L4.getRightMotor().forward();
			float distance = getFilteredData();
			//For when the robot starts off not facing a wall
			while (distance > 43) {
				distance = getFilteredData();
				L4.getLeftMotor().setSpeed(ROTATION_SPEED);
				L4.getRightMotor().setSpeed(ROTATION_SPEED);
				L4.getLeftMotor().backward();
				L4.getRightMotor().forward();
			}
			//Checks for when the sensor reads a value greater than 40
			//then records the angle at which it sees this
			while (true) {
				distance = getFilteredData();
				if (distance > 40) {
					L4.getLeftMotor().stop(true);
					L4.getRightMotor().stop(false);
					thetaA = odo.getTheta();
					L4.getLeftMotor().setSpeed(ROTATION_SPEED);
					L4.getRightMotor().setSpeed(ROTATION_SPEED);
					L4.getLeftMotor().forward();
					L4.getRightMotor().backward();
					break;
				}
			}
			//Sleep in order for the robot to not read values again
			try {
				Thread.sleep(1000);
			} catch (Exception e) {
			}
			//Rotate the other way until it reads a distance greater than 40 again
			//Similarly, read the angle from the odometer
			while (true) {
				distance = getFilteredData();
				if (distance > 40) {
					L4.getLeftMotor().stop(true);
					L4.getRightMotor().stop(false);
					thetaB = odo.getTheta();
					L4.getLeftMotor().setSpeed(ROTATION_SPEED);
					L4.getRightMotor().setSpeed(ROTATION_SPEED);
					L4.getLeftMotor().backward();
					L4.getRightMotor().forward();
					break;
				}
			}
		}
		double deltaTheta = 0.0;
		//Corrects the angle so that the positive y
		//is considered to be 0 degrees
		if (thetaB > thetaA) {
			deltaTheta = 225 - (thetaA + thetaB) / 2;
		} else if (thetaB < thetaA) {
			deltaTheta = 45 - (thetaA + thetaB) / 2;
		}
		pos[2] = deltaTheta + odo.getTheta();
		odo.setPosition(pos, new boolean[] { false, false, true });
		//Rotates to face the proper 0 degrees
		L4.getLeftMotor().setSpeed(ROTATION_SPEED);
		L4.getRightMotor().setSpeed(ROTATION_SPEED);
		navigator.turnTo(0);
	}

	public float getFilteredData() {
		usSensor.fetchSample(usData, 0);
		float distance = usData[0] * 100;
		if (distance > 50) {
			distance = 50;
		}
		return distance;
	}

}
