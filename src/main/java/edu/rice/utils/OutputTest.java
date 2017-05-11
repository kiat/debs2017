package edu.rice.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.text.ParseException;

import edu.rice.metadata.MetadataManager;
import edu.rice.metadata.ReadConstantsFromSystemTTL;
import edu.rice.parser.RDFParser;

public class OutputTest {
    public static void main(String[] args) {
    	
      	// Read the metadata and the constant parameters from system.ttl     	
    	MetadataManager.getInstance().readMetaData("./1000molding_machine.metadata.data");
    	ReadConstantsFromSystemTTL.readSystemTTL();
    		

        try {
            // Open the file and buffer it
            File file = new File("/home/kia/Desktop/Debs2017_data/19.04.2017.10molding_machine_5000dp/10molding_machine_5000dp.nt");
            BufferedReader br = new BufferedReader(new FileReader(file));

            String line;
            String segment = "";
            String observationGroupTmp = "";

            // We get a random number and then read the text file line by line.
            // We send each time a random number of lines as a text message to
            // the rabbitMQ as a message.

            // Read File line by line and push it to rabbitMQ
            while ((line = br.readLine()) != null) {

                // split the string by space character.
                String[] tripleParts = line.split(" ");

                if (tripleParts[0].contains("Group") && tripleParts[0].compareTo(observationGroupTmp) !=0 ) {

                    if (segment.compareTo("")!=0) {
                        byte[] messageBodyBytes = segment.getBytes();
                        RDFParser.processData(messageBodyBytes);
                    
                    }

                    segment = "";
                    observationGroupTmp = tripleParts[0];
                }


                segment = segment + line + "\n";
                
           
            }
            
            // one last send out  
            byte[] messageBodyBytes = segment.getBytes();
            RDFParser.processData(messageBodyBytes);


            br.close();
            

        } catch (IOException | ParseException  e) {
            e.printStackTrace();
        }

    }
}
