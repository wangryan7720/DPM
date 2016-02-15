package localization;
import lejos.hardware.sensor.SensorMode;
import lejos.robotics.SampleProvider;
//DPM Group 8
//Gareth Peters
//ID:260678626
//LuoQing(Ryan) Wang
//ID:260524744
public class LightLocalizer {
	private Odometer odo;
	private SampleProvider colorSensor;
	private float[] colorData;	
	private Navigation navi;
	
	public LightLocalizer(Odometer odo, SampleProvider colorSensor, float[] colorData) {
		this.odo = odo;
		this.colorSensor = colorSensor;
		this.colorData = colorData;
		navi=new Navigation(odo);
	}

	public void doLocalization() {
		// drive to location listed in tutorial
		//navi.travelTo(60, 60);
		// start rotating and clock all 4 gridlines
		// do trig to compute (0,0) and 0 degrees
		// when done travel to (0,0) and turn to 0 degrees
	}

}