package edu.rice.data;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.text.ParseException;

import edu.rice.parser.RDFParser;
import edu.rice.parser.RDFParserFaster;

public class DataParsingTest {
    public static void main(String[] args) {


        try {

            // Open the file and buffer it
//            File file = new File(ClassLoader.getSystemClassLoader().getResource("molding_machine_10M.nt").getFile());
        	File file = new File("/home/kia/Desktop/Debs2017_data/19.04.2017.1molding_machine_5000dp/molding_machine_5000dp.nt");
            
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

                if (tripleParts[0].contains("http://project-hobbit.eu/resources/debs2017#ObservationGroup") && tripleParts[0].compareTo(observationGroupTmp) !=0 ) {

                    if (segment.compareTo("")!=0) {
                        byte[] messageBodyBytes = segment.getBytes();
                        RDFParserFaster.processData(messageBodyBytes);                      
                    }

//                	System.out.println(segment);
                	
                	System.out.println("\n\n#########################");
//                	System.out.println("#########################");
//                	System.out.println("#########################\n\n");
                	
                	
                    segment = "";
                    observationGroupTmp = tripleParts[0];
                }


                segment = segment + line + "\n";
                
           
            }
            
            // one last send out  
            byte[] messageBodyBytes = segment.getBytes();
            RDFParserFaster.processData(messageBodyBytes);


            br.close();
            
        } catch (IOException | ParseException e) {
            e.printStackTrace();
        }

    }
}
