package Sensors;

import com.diozero.api.AnalogInputDevice;
import com.diozero.api.RuntimeIOException;
import com.diozero.internal.spi.AnalogInputDeviceFactoryInterface;

public class MoistureSensor extends AnalogInputDevice implements MoistureInterface {

	private final float offset;
	
	public MoistureSensor(AnalogInputDeviceFactoryInterface deviceFactory, int gpio, float offset) throws RuntimeIOException {
		super(deviceFactory, gpio);
		this.offset = offset;
	}

	@Override
	public float getMoisture() {
		return this.getScaledValue() + offset;
	}

}
