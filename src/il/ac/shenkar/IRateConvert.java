package il.ac.shenkar;

import javax.xml.transform.TransformerConfigurationException;

/**
 * The Interface IRateConvert have two abstract functions that was implemented in scala class .
 */
@SuppressWarnings("unused")
public interface IRateConvert 
{

	//convert function - get the values that we want to convert  
	public abstract double convert(String from ,String to ,double amount);
	
	//function that get the rates from the BOI site and store them in local Variables 
	public abstract void XMLParsing(String fromCurrecny,String toCurrecny);
	
}
