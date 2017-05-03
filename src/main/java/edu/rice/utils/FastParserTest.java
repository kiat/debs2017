package edu.rice.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;

import edu.rice.parser.RDFParserFaster;

public class FastParserTest {
    public static void main(String[] args) {

    	
    	
    	RDFParserFaster myParser=new RDFParserFaster();
    	
        try {



            // Open the file and buffer it
            File file = new File("/home/kia/Desktop/test.nt");
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
                    	RDFParserFaster.processData(messageBodyBytes);
                    }

                    segment = "";
                    observationGroupTmp = tripleParts[0];
                }
                segment = segment + line + "\n";
            }
            
            // one last send out  
            byte[] messageBodyBytes = segment.getBytes();
        	RDFParserFaster.processData(messageBodyBytes);

//          channel.basicPublish("", "hobbit.datagen-system.exp1", null, messageBodyBytes);


            br.close();

        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
