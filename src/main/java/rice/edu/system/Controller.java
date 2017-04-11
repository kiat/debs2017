package rice.edu.system;

import org.aopalliance.reflect.Metadata;

import edu.rice.kmeans.KMeans;
import edu.rice.metadata.MetadataManager;

public class Controller {

	public static void main(String[] args) {

		
		// Reads the metadata and have it ready for use.
		MetadataManager.getInstance().readMetaData("./src/main/resources/molding_machine_10M.metadata.nt");
		 
		
		
	}

}
