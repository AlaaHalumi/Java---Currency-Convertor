package il.ac.shenkar;

import java.io.IOException;
import java.util.logging.FileHandler;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

import org.apache.log4j.BasicConfigurator;

public class Main {

	//fileHandler can write to specified file
	static FileHandler fileHandler;
	//logger variable
	static Logger logger = Logger.getLogger("Logger");
	
	public static void main(String[] args) {
		
		
			//this function iInitialize Logger
			startLogger(); 
			//create object from Model's type
			Model model = new Model();
            //create object from Thread's type
			Thread modelThread = new Thread(model);
		    modelThread.start();
		}
	
		//initialize Logger
		static void startLogger()
		{
		   BasicConfigurator.configure();
			try
			{
				fileHandler = new FileHandler("logg.txt");
				fileHandler.setFormatter(new SimpleFormatter());
				logger.addHandler(fileHandler);
			}
			 /**
			* Signals that I/O exception of some sort has occurred 
			*/
			catch (IOException e)
			{
				e.printStackTrace();
			}	
		}		

}


