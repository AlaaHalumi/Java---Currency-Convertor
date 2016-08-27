package il.ac.shenkar;

import java.awt.List;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.logging.FileHandler;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

import javax.swing.JTable;
import javax.swing.SwingUtilities;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.apache.log4j.BasicConfigurator;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/**
* This class creates an object of GUI's type.
*It is responsible for initializing the rates table in GUI for the first time.
*This class implement Runnable for thread that runs every 3 minutes, and checks if there is a
*change in rates between BOI and the original GUI rates.  
*/
@SuppressWarnings("unused")
public class Model implements Runnable{
	
	private ClientGUI gui;
	private InputStream is = null;
    private HttpURLConnection con = null;
    private NodeList list;
    private String date;
    
	//variables for list rates
    private ArrayList<String> modelRates;
    private ArrayList<String> threadRates;
	
    /**
	* Model constructor  
	* Initialize the variables from type ArrayList in rates and creates GUI object
	*/
    public Model() 
    {
    	
    //initialize rates for thread checks
    threadRates = new ArrayList<String>();
    
    //initialize rates for initializing the GUI with the latest rates.
    modelRates =  new ArrayList<String>();
    modelRates =  this.getRates();
    
    //EDT thread 
  	  SwingUtilities.invokeLater(new Runnable()
      {
          public void run()
          {
        	//create GUI object that receives rates and date
        	gui = new ClientGUI(modelRates,date);             
      		gui.setGUI();    
          }
      });
	}
  
    /**
	* the function is a "getter" for date variable
	* @return the latest date of the latest rates after the updates 
	*/
    public String getDate() 
    {
		return date;
	}

    
   /**
   * This function connect to BOI parsing the XML file and save the rates in a ArrayList.
   * In addition it creates an external XML file in case there's an Internet connection interrupt (lost).
   * @return list of new rates currency in type ArrayList.
   */ 
   public ArrayList<String> getRates()  {
		
	   //represents the entire XML document it is the root of the document tree, and provides the primary access to the document's data.
	    org.w3c.dom.Document doc = null;
	    //local variable to store the rates
	    ArrayList<String> rates = new ArrayList<String>();
	    //local variable in type NodeList for the last date of update
	    NodeList lastUpdate;
	    
	    try
        {	
		
            URL url = new URL("http://www.boi.org.il/currency.xml");
            con = (HttpURLConnection)url.openConnection();
            con.setRequestMethod("GET");
            con.connect();
            is = con.getInputStream();
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            doc = builder.parse(is);
            //get the last date of the update
            lastUpdate = doc.getElementsByTagName("LAST_UPDATE");
            //convert NodeList To String
            date = lastUpdate.item(0).getFirstChild().getNodeValue();	
       	   
            //get the list of rates
            list = doc.getElementsByTagName("RATE");
            //create XML file for thread checks and store it
            //A TransformerFactory instance can be used to create Transformer and Templates objects
            TransformerFactory tfactory = TransformerFactory.newInstance();
            //This instance may then be used to process XML from a variety of sources and write the transformation output to a variety of sinks
            Transformer xform = tfactory.newTransformer();
            File myOutput = new File("Thread-Rate.xml");
            Main.logger.info("[Thread-Model]: Create and updates external xml file Thread-Rate.xml");
            
            try
            {
            	//Transform the XML Source to a Result, receive "doc" as a source and "myOutput" as a result
				xform.transform(new DOMSource(doc), new StreamResult(myOutput));
			} 
            /**
			* this class specifies an exceptional condition that occurred during the transformation process 
			*/
            catch (TransformerException e)
            {
				Main.logger.warning(e.getMessage());
				e.printStackTrace();
			}
            
            int length = list.getLength();
            for(int i=0; i<length; i++)
            {
            	//convert list in type NodeList of rates To String
            	 String rate = list.item(i).getFirstChild().getNodeValue();	
            	 rates.add(rate);
            }    
        }
	    /**
		* This exception is thrown in the case we don't have any access to the Internet.
		* and we will continue checking the rates from the xml file that stored in the project from the last connection from BOI.
		*/
        catch(IOException e)
        {
        	Main.logger.warning(e.getMessage());
        	Main.logger.info("[Thread-Model]: The application is not connected to the Internet ");
        	//Defines a factory API that enables applications to obtain a parser that produces DOM object trees from XML documents
        	DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = null;
			try {
				builder = factory.newDocumentBuilder();
			/**
			* Indicates an serious Configuration error
			*/
			} catch (ParserConfigurationException e2) {
				Main.logger.warning(e.getMessage());
				e2.printStackTrace();
			}
             try {
				doc = builder.parse("Thread-Rate.xml");
			} 
             /**
 			* Indicates that was an error to parsing xml file
 			*/ 
            catch (SAXException | IOException e1)
            {
            	Main.logger.warning(e.getMessage());
				Main.logger.info("[Thread-Model]: faild to read from the external xml file - Thread-Rate.xml  ");
				e1.printStackTrace();
			}
        
        	 list = doc.getElementsByTagName("RATE");
        	 int length = list.getLength();
             for(int i=0; i<length; i++)
             {
            	 //convert list of type NodeList for rates To String
             	 String rate = list.item(i).getFirstChild().getNodeValue();				
             	 rates.add(rate);
             }
        	
        }
	    /**
		* Indicates an serious Configuration error
		*/
        catch(ParserConfigurationException e)
        {
        	Main.logger.warning(e.getMessage());
            e.printStackTrace();
        }
	    /**
		* Indicates that was an error to parsing xml file
		*/ 
        catch(SAXException e)
        {
        	Main.logger.warning(e.getMessage());
            e.printStackTrace();
        } 
	    /**
		* Indicates an serious Configuration error
		*/
	    catch (TransformerConfigurationException e1) 
	    {
        	Main.logger.warning(e1.getMessage());
			e1.printStackTrace();
		}  
		
        finally
        {
          if(is!=null)
          {
              try
              {
                  is.close();
              }
              catch(IOException e)
              {
                  e.printStackTrace();
              }
          }
          if(con!=null)
          {
              con.disconnect();
          }
        }
		//return array of strings with the updated rates
		return rates;
}
   
   /**
   * The Runnable interface should be implemented by the class that define this method.
   * This function runs every 3 minutes and checks if there is any change of rates between BOI latest rates and GUI rates.
   * If there is a change the rates on the GUI will change respectively.
   */ 
   @Override
	public void run() 
   {
	   
	   //if the rates is not changed, ratesFlag is true 
	   boolean ratesFlag = true; 
	  
		  try 
		  {
		    while(true)
			{
		    	Thread.sleep(3 * 60 * 1000);
		    	
		    	   threadRates = this.getRates();
				   //compare GUI rates between the latest BOI rates 
				   ratesFlag = compareList(gui.getRates(),threadRates);
				   //if ratesFlag is false then the rates in the GUI not updated to the rates in BOI 
				   if (!ratesFlag)
				   {
					 //we call to updateTable function to update the specific column of the rates in the GUI table
					 gui.setTable(updateTable(gui.getTable(),threadRates));
					 Main.logger.info("[Thread-Model]: Update the rates on the GUI table  ");
				     
					 //if we change the GUI rate we update the last date of the new update
					 String thraedDate  =  this.getDate();
					 if(gui.getLastDate() != thraedDate)
					  {
						 gui.setDate(thraedDate);
						 Main.logger.info("[Thread-Model]: Update the date of the GUI  ");		   
					  }
				   }   
			 }
			   
		     } 
	     	/**
			* This exception is thrown if the thread can't compare between the rates of the GUI and BOI 
			*/		  
	         catch (InterruptedException e)
	         {   
	        	 Main.logger.warning(e.getMessage());
	        	 Main.logger.info("[Thread-Model]: Thread failed it can't compare between GUI rates and BOI rates ");
			     e.printStackTrace();
	         }
	}
   
   /**
	* The function called by Thread to create a new JTable with the updated rates
	* @param jTable the JTable of the GUI
	* @param threadRates arrayList of update rates 
	* @return table with up-to-date rates
	*/
   JTable updateTable(JTable jTable, ArrayList<String> threadRates)
   {
	   //now we can write to the GUI table
	   jTable.setEnabled(true);
	    int row = 1;
		for(String ob : threadRates)	
		{
			jTable.setValueAt(ob, row++, 2);

		}
    return jTable;
   }
   
   /**
 	* The function called by Thread to compare between the GUI rates and the updated rates
 	* @param modelRates the rates of the GUI
 	* @param threadRates the updated rates of BOI 
 	* @return false if there is a difference between the rates else true
 	*/
   @SuppressWarnings("rawtypes")
   public boolean compareList(ArrayList modelRates , ArrayList threadRates )
   {
	  return modelRates.equals(threadRates);
	  
   }	
}
