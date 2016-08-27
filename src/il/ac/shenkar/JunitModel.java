package il.ac.shenkar;

import static org.junit.Assert.*;

import java.util.ArrayList;

import org.junit.Before;
import org.junit.Test;

/**
* JUnit is a simple framework to write repeatable tests
* This class creates an object of type Model, and performs a number 
* of tests preceded the following methods
*/
public class JunitModel {

	private Model model;
	
	@Before
	public void setUp() throws Exception 
	{
		model = new Model();	
	}
	
	/* we check if the function return list of rates if the result is true then the variables aren't equal, therefore we succeeded 
	   connect to BIO and parsing the rates
	*/
	@Test
	public void testGetRates() 
	{
		ArrayList<String> modelRates = null;
		ArrayList<String> actual = model.getRates();
		
		assertNotEquals("Check if the function getRates return list of rates",modelRates,actual);

	}
	
	/* we check if the thread function that compare the rates list of GUI table between latest rates of 
	 * BIO is equal
	*/
	@Test
	public void testCompareList()
	{
		ArrayList<String> actual1 = model.getRates();
		ArrayList<String> actual2 = model.getRates();
		
		assertTrue("Check if actual1 and actual2 is equal ",model.compareList(actual1,actual2) );
		
	}
	
	/*
	 * function that is defined in scala object RateConvert receive two Currency and amount and convert them,
	 * then return the result to the GUI
	*/
	@Test
	public void testRateConvert()
	{ 
		String from = "USD";
		String to = "EUR";
		double amountToConvert = 115;
		ArrayList<String> actual = model.getRates();
		double USDrate = Double.parseDouble(actual.get(0)); //get USD rate and convert string to double
		double EURrate = Double.parseDouble(actual.get(3)); //get EUR rate and convert string to double
		
		double expected = (USDrate*amountToConvert)/EURrate;
		
		assertEquals("Check the convert function",expected,RateConvert.convert(from,to,amountToConvert),2.0);
	}
	
}
