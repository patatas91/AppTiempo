package p6;

import org.apache.axis.client.Call;
import org.apache.axis.client.Service;
import org.apache.axis.utils.Options;
import javax.xml.namespace.QName;
public class Cliente {
	public static void main(String [] args)  {
		try {
			Options options = new Options(args);

			String endpointURL = options.getURL();

			Service service = new Service();
			Call call = (Call) service.createCall();
			
			// Se establece servidor a llamar
			call.setTargetEndpointAddress( new java.net.URL(endpointURL) );
			
			// Nombre del servicio-metodo a invocar
			call.setOperationName(new QName("ServicioTiempo", "main"));
			
			// Invocamos el servicio
			call.invoke( new Object[] {} );
			
			} catch (Exception e) {
				System.err.println(e.toString());
			}
	}
}