package edu.rice.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.text.ParseException;

import edu.rice.parser.RDFParser;

public class ParserTest2 {
    public static void main(String[] args) {

    	
    	
    	RDFParser myParser=new RDFParser();
    	
        try {



            // Open the file and buffer it
//            File file = new File("/home/kia/Desktop/Debs2017_data/19.04.2017.10molding_machine_5000dp/10molding_machine_5000dp.nt");
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
                    	processData(messageBodyBytes);
                    }

                    segment = "";
                    observationGroupTmp = tripleParts[0];
                }
                segment = segment + line + "\n";
            }
            
            // one last send out  
            byte[] messageBodyBytes = segment.getBytes();
        	processData(messageBodyBytes);

//          channel.basicPublish("", "hobbit.datagen-system.exp1", null, messageBodyBytes);


            br.close();

        } catch (Exception e) {
            e.printStackTrace();
        }

    }
    
    
    public static final String NEWLINE = System.getProperty("line.separator");

	public static void processData(byte[] bytes) throws ParseException {
		
		

		ByteBuffer byteBuffer = ByteBuffer.wrap(bytes);
		Charset utf8 = Charset.forName("UTF-8");
		CharBuffer charBuffer = utf8.decode(byteBuffer);

		int start=0;
		int end=0;
		
		CharBuffer charBuffertmp; 
		
		// 6 lines starting an observation group. 
		for (int j = 0; j < 4; j++) {
			end = findCharacter(charBuffer, '\n', start);
			
			charBuffertmp = charBuffer.subSequence(start ,  end);
			start=end;
			end=start+end; 
			
			System.out.println(charBuffertmp);

		}
		

		
		
//		while (charBuffer.hasRemaining()) {
////			int i = parse(charBuffer);
//			
//		
//		}


		
	}

	private static int findCharacter(CharBuffer chars, char c, int start) {

		while (chars.charAt(start) != c) {
			start++;
		}

		return start;
	}
    
    
}
