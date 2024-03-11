package DataIO;

import java.util.ArrayList;

import org.tinylog.Logger;

import com.diozero.devices.McpAdc;
import com.diozero.devices.TMP36;
import com.diozero.util.SleepUtil;
import com.diozero.util.TemperatureUtil;

import Sensors.MoistureSensor;

public class ReadDataFromBoard {

	public static final int TEMP_ID = 0;
	public static final int MOISTURE_ID = 1;
	
	public static final float DEFAULT_MOISTURE_OFFSET = 0.0f;
	public static final ArrayList<Float> moistureData = new ArrayList<Float>(1000);
	public static final float DEFAULT_TEMP_OFFSET = 1.04f;
	public static final ArrayList<Float> tempData = new ArrayList<Float>(1000);
	public static int sampleTime = 20;
	public static int runtime = 10;
	
	public static void main(String[] args) {
		if(args.length < 2) {
			Logger.error("Needed Arguments: <sample-rate-per-second> <runtime-in-seconds>");
			System.exit(2);
		}
		sampleTime = Integer.parseInt(args[0]);
		runtime = Integer.parseInt(args[1]);
		tempData.ensureCapacity(runtime * sampleTime);
		moistureData.ensureCapacity(runtime * sampleTime);
		
		try(
				//Need to use an adc converter because Raspberry Pi doesn't have it built into the board (I Think). Parameters: Adc type(chip), chipSelect(pin?), vRef(voltage)
				McpAdc adc = new McpAdc(McpAdc.Type.MCP3008, 0, 3.3f);
				//Parameters: adc to use, gpio(pin), temp offset
				TMP36 TempSens = new TMP36(adc, 0, DEFAULT_TEMP_OFFSET); 
				//Parameters: adc to use, gpio(pin), moisture offset
				MoistureSensor MoistureSens = new MoistureSensor(adc, 0, DEFAULT_MOISTURE_OFFSET)
			) {
			//Read temps
			int i = 0;
			while(i++ < runtime * sampleTime) {
				tempData.add(TemperatureUtil.toFahrenheit(TempSens.getTemperature()));
				moistureData.add(MoistureSens.getMoisture());
				SleepUtil.sleepSeconds((double)(1/sampleTime));
			}
			DataIO.WriteToFile(System.getProperty("user.dir") + "/data/temp1.csv", tempData, TEMP_ID, "temperature");
			DataIO.WriteToFile(System.getProperty("user.dir") + "/data/moisture.csv", moistureData, MOISTURE_ID, "moisture");
		}
	}
}
