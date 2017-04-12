package edu.rice.output;



public class OutputGenerator {


	// This has to be a single counter to produce the anomaly numbers in sequence. 
	public static long anomalyCounter=0;
	
	//KIA: Let us keep this namespace the same as theirs to avoid any problems. 
	public static final String RiceNamespace="http://project-hobbit.eu/resources/debs2017";
	
	public static final String debsPrefix="http://project-hobbit.eu/resources/debs2017"; 
	
	public static final String rdfType="<http://www.w3.org/1999/02/22-rdf-syntax-ns#type>";
	
	public static final String arPrefix="http://www.agtinternational.com/ontologies/DEBSAnalyticResults";
	
	public static final String hasProbab="<"+arPrefix+ "#hasProbabilityOfObservedAbnormalSequence>";
	
	public static final String hasTimeStamp="<"+arPrefix+ "#hasTimestamp>";
	
	public static final String arAnomaly="<"+arPrefix+"#Anomaly>"; 
	
	public static final String inAbnormalDimension= "<"+arPrefix+"#inAbnormalDimension>"; 
	
	public static final String i40Machine="<http://www.agtinternational.com/ontologies/I4.0#machine>";
	
	public static final String iotCoreTimeStamp="<http://www.agtinternational.com/ontologies/IoTCore#Timestamp>";
	public static final String iotCoreValueLiteral="<http://www.agtinternational.com/ontologies/IoTCore#valueLiteral>";
	
	public static final String wm="http://www.agtinternational.com/ontologies/WeidmullerMetadata";
	
	public static final String xmlDouble="<http://www.w3.org/2001/XMLSchema#double>"; 
	
	
	
	private OutputGenerator() {
	}

	
	
    static class SingletonHolder {
    	static 	final OutputGenerator instance = new OutputGenerator();
    }
    
    
    public static OutputGenerator getInstance() {
		return SingletonHolder.instance;

    }



	
	
	public String outputAnomaly(int machineNr, int dimension, double probability, long timestamp){

//		StringBuilder output=new StringBuilder();  
		
		String output="";
		String space=" ";
		String eol = System.getProperty("line.separator");
		String anomalyURI="<"+RiceNamespace+"#Anomaly_"+ (anomalyCounter++) + ">";
		
		
//		output.append(anomalyURI).append(space).append(rdfType).append(space).append("<") .append( arPrefix) .append("#Anomaly>" ) .append(  space ) .append( "." ) .append( space ) .append(  eol) .append( 
//				      anomalyURI).append(space).append(i40Machine) .append(space).append( "<") .append(wm) .append( "#Machine_") .append( machineNr ) .append(">" ) .append( space ) .append( "." ) .append( space ) .append( eol) .append(
//				      anomalyURI).append(space).append(inAbnormalDimension).append(space ) .append( "<" ) .append( wm+"#_") .append(machineNr) .append("_" ) .append( dimension ) .append( ">" ) .append(  space ) .append( "." ) .append( space ) .append( eol ) .append( 
//				      anomalyURI).append(space).append(hasTimeStamp).append(space).append( "<" ) .append( debsPrefix ) .append("Timestamp_") .append(timestamp ) .append( ">") .append(  space ) .append( ".") .append( space ) .append( eol) .append(
//				      anomalyURI).append(space).append(hasProbab).append(space).append( "\"") .append(  probability) .append( "\"^^") .append(xmlDouble ) .append(  space ) .append(  "." ) .append( space) 
//				      ; 	
		
		
		output =  anomalyURI + space + rdfType + space +"<"+ arPrefix+"#Anomaly>" +  space + "." + space +  eol + 
				  anomalyURI + space + i40Machine + space + "<"+wm+ "#Machine_"+ machineNr +">" + space + "." + space + eol+
				  anomalyURI + space + inAbnormalDimension + space + "<" + wm+"#_"+machineNr+"_" + dimension + ">" +  space + "." + space + eol + 
				  anomalyURI + space + hasTimeStamp + space + "<" + debsPrefix +"Timestamp_"+timestamp + ">" +  space + "." + space + eol+
				  anomalyURI + space + hasProbab + space + "\""+  probability + "\"^^"+xmlDouble +  space +  "." + space; 	
		
		return output.toString();	
	} 
	
	
	// This is a performance Test. 
	
	// String concatenation is faster 
	// http://stackoverflow.com/questions/1532461/stringbuilder-vs-string-concatenation-in-tostring-in-java
	public static void main(String[] args ){
		
		long startTime = 0;

		// NOW read the objects from memory
		// START OF Time calculation
		startTime = System.nanoTime();
		
		
		System.out.println(OutputGenerator.getInstance().outputAnomaly(59, 31,0.004115226337448559, 24)); 
		System.out.println(OutputGenerator.getInstance().outputAnomaly(59, 5,0.004115226337448559, 24));	
		
		// Do it 10M times
		for (int i = 0; i < 10000000; i++) {
			OutputGenerator.getInstance().outputAnomaly(59, 31,0.004115226337448559, 24); 
			OutputGenerator.getInstance().outputAnomaly(59, 5,0.004115226337448559, 24);	
		}
		
		// End of time calculation
		long endTime = System.nanoTime();
		double elapsedTotalTime = (endTime - startTime) / 1000000000.0;

		System.out.println("Elapsed Time " + String.format("%.9f", elapsedTotalTime));
		 
	}
	

}
