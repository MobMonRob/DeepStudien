import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Iterator;

import com.eclipsesource.json.Json;
import com.eclipsesource.json.JsonArray;
import com.eclipsesource.json.JsonValue;

public class Test{
    public static void main(String[] args) throws Exception{
        ComplementaryFilter filter = new ComplementaryFilter();

        JsonValue json = Json.parse(new FileReader(new File("../Sensordaten/unfreier_fall.json")));
        
        if (json.isArray()) {
        	  JsonArray array = json.asArray();
        	  
        	  for (JsonValue wert : array) {
				double ac_x = wert.asObject().getDouble("Ac_x", 0);
				double ac_y = wert.asObject().getDouble("Ac_y", 0);
				double ac_z = wert.asObject().getDouble("Ac_z", 0);
				double gy_x = wert.asObject().getDouble("Gy_x", 0);
				double gy_y = wert.asObject().getDouble("Gy_y", 0);
				double gy_z = wert.asObject().getDouble("Gy_z", 0);
				
				double[] a = {ac_x, ac_y, ac_z};
		        double[] w = {gy_x, gy_y, gy_z};

		        filter.update(a, w, 0.020d);
		        
		        
		        double[] results = filter.getOrientation();
		        for (double d : results) {
					System.out.println(d);
				}
		        double[] resultsEuclid = QuternionUtils.toEuclidAnglesRadian(results);
		        for (double d : resultsEuclid) {
					System.out.println(d);
				}
		        System.out.println("---");
			}
        }
        
       
    }


}