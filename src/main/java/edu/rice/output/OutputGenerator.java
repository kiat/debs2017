package edu.rice.output;

public class OutputGenerator {
	
	public static long anomalyCounter=0;
	
	
//	// RDF prefixes used in the debs2017 grand challenge metadata.  
//	public static final String PREFIXES = 
//			  "PREFIX iotcore: <http://www.agtinternational.com/ontologies/IoTCore#>  "
//			+ "PREFIX ar:   <http://www.agtinternational.com/ontologies/DEBSAnalyticResults#> "
//			+ "PREFIX debs2017: <http://project-hobbit.eu/resources/debs2017>   " 
//			+ "PREFIX ssn: <http://purl.oclc.org/NET/ssnx/ssn#> "
//			+ "PREFIX WeidmullerExamples: <http://www.agtinternational.com/resources/WeidmullerExamples#> "
//			+ "PREFIX i40: <http://www.agtinternational.com/ontologies/I4.0#>" 
//			+ "PREFIX owl: <http://www.w3.org/2002/07/owl#>"
//			+ "PREFIX qudt: <http://data.nasa.gov/qudt/owl/qudt#>"
//			+ "PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> "
//			+ "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#> " 
//			+ "PREFIX ssn: <http://purl.oclc.org/NET/ssnx/ssn#> "
//			+ "PREFIX wmm: <http://www.agtinternational.com/ontologies/WeidmullerMetadata#> " 
//			+ "PREFIX xsd: <http://www.w3.org/2001/XMLSchema#> ";
	
	
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
	
	
	
	
	
	public String outputAnomaly(int machineNr, int dimension, double probability, long timestamp){
		
		String output="";
		String space=" ";
		String eol = System.getProperty("line.separator");
		
		String anomalyURI="<"+RiceNamespace+"#Anomaly_"+ (anomalyCounter++) + ">";
		
		
		output =  anomalyURI + space + rdfType + space +"<"+ arPrefix+"#Anomaly>" +  space + "." + space +  eol + 
				  anomalyURI + space + i40Machine + space + "<"+wm+ "#Machine_"+ machineNr +">" + space + "." + space     
				
				; 
		
		
		
		
		
		
		
		
		
		
		
		
		
		return output;	
		
	} 
	
	
	
	public static void main(String[] args ){
		
		OutputGenerator myoutputgenarator=new OutputGenerator();
		
		System.out.println(myoutputgenarator.outputAnomaly(59, 31,0.004115226337448559, 24)); 
		System.out.println(myoutputgenarator.outputAnomaly(59, 5,0.004115226337448559, 24)); 

		
	}
	
		
	
	
	
	
	

}
