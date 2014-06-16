package webservice;

import org.ksoap2.serialization.PropertyInfo;
import org.ksoap2.serialization.SoapObject;

import util.EncryptDecrypt;

import com.example.dsmobilesupport.R;

import android.content.Context;

public class WSOUpdateUserSettings extends WebServiceOperation{

	public WSOUpdateUserSettings(Context context) {
		super(context);
		methodName = context
				.getString(R.string.method_name_updateusersettings);
		soapAction = context
				.getString(R.string.soap_action_updateusersettings);
		timeout = 180 * 1000;
	}

	@Override
	protected void addParameters(SoapObject request, Object[] parameters) {
		try {
			String deviceId = EncryptDecrypt.encrypt(parameters[0].toString());
			String retailStoreId = EncryptDecrypt.encrypt(parameters[1].toString());
			
			PropertyInfo device = new PropertyInfo();
			device.setName("deviceId");
			device.setValue(deviceId);
			device.setType(String.class);
			request.addProperty(device);
			
			PropertyInfo rtStoreId = new PropertyInfo();
			rtStoreId.setName("retailStoreId");
			rtStoreId.setValue(retailStoreId);
			rtStoreId.setType(String.class);
			request.addProperty(rtStoreId);
			
		} catch (Exception e) {
			e.printStackTrace();
		}		
	}
}
