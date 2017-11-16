import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import com.eclipsesource.json.Json;
import com.eclipsesource.json.JsonArray;
import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class Test{
	public static String NAME = "unfreier_fall.json";
	

	
	
    public static void main(String[] args) throws Exception{
        ComplementaryFilter filter = new ComplementaryFilter();
        TrapezIntegrationFilter posFilter = new TrapezIntegrationFilter();
        List<DataObject> list = new LinkedList<>();
        
        //initialize filter
        posFilter.setPosition(new double[] {0, 0, 0});
        posFilter.setVelocity(new double[] {0, 0, 0});

        JsonValue json = Json.parse(new FileReader(new File( "../Sensordaten/" +  NAME)));
        
        if (json.isArray()) {
        	  JsonArray array = json.asArray();
        	  for (JsonValue wert : array) {
        		DataObject data = new DataObject();
        		
				double ac_x = wert.asObject().getDouble("Ac_x", 0);
				double ac_y = wert.asObject().getDouble("Ac_y", 0);
				double ac_z = wert.asObject().getDouble("Ac_z", 0);
				double gy_x = wert.asObject().getDouble("Gy_x", 0);
				double gy_y = wert.asObject().getDouble("Gy_y", 0);
				double gy_z = wert.asObject().getDouble("Gy_z", 0);
				
				double[] a = {ac_x, ac_y, ac_z};
		        double[] w = {gy_x, gy_y, gy_z};

		        
		        //Orientation
		        filter.update(a, w, 0.020d);
		        double[] orient = filter.getOrientation();
		        double[] orientMat = QuternionUtils.toRotationMatrix(orient);
		        
		        //Position
		        posFilter.update(orient, a, 0.020d);
		        double[] pos = posFilter.getPosition();
		        //double[] posEuclid = QuternionUtils.toEuclidAnglesRadian(pos);
		        
		        
		        //set Data in Objecct
		        data.ac_x = ac_x;
		        data.ac_y = ac_y;
		        data.ac_z = ac_z;
		        data.gy_x = gy_x;
		        data.gy_y = gy_y;
		        data.gy_z = gy_z;
		        
		        data.orientQuater.setData(orient);
		        data.orientMat.setData(orientMat);
		        //data.posQuater.setData(pos);
		        data.posEuclid.setData(pos);
		        list.add(data);
			}
        	  
        	  Gson gson = new GsonBuilder().setPrettyPrinting().create();
        	  String outJson = gson.toJson(list);
        	  
        	  try (PrintWriter out = new PrintWriter("../Sensordaten/konvertiert/" + NAME)){
        		  out.write(outJson);  
        	  }
        	  
        	  
        	  
        	  
        }
        
       
    }


}