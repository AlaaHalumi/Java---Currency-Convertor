package il.ac.shenkar;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.URL;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.logging.FileHandler;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.text.*;
import javax.xml.parsers.*;

import org.apache.log4j.BasicConfigurator;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/**
* This class create the CurrencyExchange GUI.
* The class allows to perform conversions between various currencies.
* The class implements ActionListener for convert selection 
*/
@SuppressWarnings("unused")
public class ClientGUI implements ActionListener {

	//creating GUI components
	private JFrame frame;
	
	private Label fromtCurrency;
	private Label toCurrency;
	private Label amountLabel;
	private Label resultLabel;
	private Label copyRight;
	private JLabel status;
	private JLabel jdate;
	
	private JButton convert;
	
	private JTextField amount;
	private JTextField result;
	
	@SuppressWarnings("rawtypes")
	private JComboBox currency1;
	@SuppressWarnings("rawtypes")
	private JComboBox currency2;
	
	private JPanel panelNorth;
	private JPanel panelCenter;
	private JPanel panelEast;
	private JPanel panelSouth;
	private JTable table;
	
	private JPanel panelWest1;
	private JPanel panelWest2;
	private JPanel panelWest3;
	private JPanel panelWest4;
	private JPanel panelWest5;

	private String sDate;
	private String space = "          ";
	private JTextArea GUIError;
	
	private boolean statusFlag;
    private ArrayList<String> rates;
    
    	
	//string array for initializing JComboBox
	private String[] countryCode = { "ILS - Israel"  ,
									 "USD - USA ", 
			                         "GBP - Great Britain",
			                         "JPY - Japan",
			                         "EUR - EMU",
			                         "AUD - Australia",
			                         "CAD - Canada", 
			                         "DKK - Denmark",
			                         "NOK - Norway",
							         "ZAR - South Africa",
							         "SEK - Sweden",
							         "CHF - Switzerland",
							         "JOD - Jordan",
							         "LBP - Lebanon",
							         " EGP - Egypt"};

	private String[] columnNames = { "Currency", "Country Name", "Rate" };
	
	//object for initializing Jtable
	Object data[][] ;
		
	
	/**
	* GUI constructor  
	* @param rates - list of latest rates from BOI
	* @param date - the last date when was rates updated
	*/
	public ClientGUI(ArrayList<String> rates, String date )
	{
		
		jdate = new JLabel();
		
		//Set date of the last updated rate
		this.setDate(date);
		
		//Set Rates in Object data 
		this.rates = rates;
		this.setData(rates);
	    
		//initialize table with the rates and the header for the GUI table
		table = new JTable(data, columnNames);
		table.setPreferredSize(new Dimension(440, 240));
		
		//now we can't write in table via GUI
		table.setEnabled(false);
		
		//creating GUI Component 
		setGUICOmponent();

		//this function create an anonymous thread that runs every 1 minute and checks if there is any Internet connection.
		threadConnection();
		
	}
	
	/**
	*The function sets date of the last updated rates and call to setJdate to initialize the label with the new date 
	*@param date is the last updated date in type String 
	*/
	public void setDate(String date) 
	{
		this.sDate= date;
		this.setJdate();
	}
	
	/**
	*The function get the date is shown in the GUI 
	*@return the current date of updated GUI
	*/
	public String getLastDate() {
		
		return this.sDate;
		
	}
	
	/**
	*The function set the date that show in the GUI 
	*/
	public void setJdate() {
		
		jdate.setText("Last Update:" + getLastDate() );
	}
	
	/**
	*The function create her Components  
	*/
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public void setGUICOmponent()
	{
	    
		//design frame
	    frame = new JFrame("Currency Exchange Converter");
	    ImageIcon img = new ImageIcon("BTC");
	    frame.setIconImage(img.getImage());
	   
		//panels that store the panel composite 	
	    panelNorth = new JPanel();
		panelCenter = new JPanel();
		panelEast = new JPanel();
		panelSouth = new JPanel();
	    
		convert = new JButton("Convert");
		convert.setPreferredSize(new Dimension(150, 50));
		
		amount = new JTextField(6);
		amount.setFont(new Font("",Font.BOLD,15));
		result = new JTextField(6);
		result.setEnabled(false);
		result.setFont(new Font("",Font.BOLD,15));
		
		//drop-down list receive list of countries
		currency1 = new JComboBox(countryCode);
		currency1.setPreferredSize(new Dimension(170, 30));
		currency2 = new JComboBox(countryCode);
		currency2.setPreferredSize(new Dimension(170, 30));
		
		//initialize labels
		fromtCurrency = new Label("Currency I Have");
		fromtCurrency.setFont(new Font("",Font.BOLD,12));
		
		toCurrency = new Label("Currency I Want");
		toCurrency.setFont(new Font("",Font.BOLD,12));
		
		amountLabel = new Label("Amount");
		amountLabel.setFont(new Font("",Font.BOLD,12));
		
		resultLabel = new Label("Result ");
		resultLabel.setFont(new Font("",Font.BOLD,12));
		
		copyRight = new Label("Copyright © 2015 Niv Baruch & Alaa Halumi. All Rights Reserved");
		copyRight.setFont(new Font("",Font.BOLD,12));
		
		//Exit the application
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	    
		//status is a label that specify the status of the Internet connection  
		status = new JLabel("Status: Online",SwingConstants.CENTER);
		status.setPreferredSize(new Dimension(100, 50));
		status.setText("Status: Testing...");
		status.setForeground(Color.GRAY);
		
		jdate = new JLabel("Last Update: ",SwingConstants.CENTER);
		jdate.setPreferredSize(new Dimension(150, 40));
		jdate.setText("Last Update:" + getLastDate() );
		
		//GUIError that show "error" for a user
		GUIError = new JTextArea();
		GUIError.setPreferredSize(new Dimension(230, 30));
		GUIError.setFont(new Font("",Font.BOLD,12));
		GUIError.setEditable(false);
		GUIError.setText(space);
		GUIError.setBackground(SystemColor.menu);
		
		//panel for the component 
		panelWest1 = new JPanel();
		panelWest2 = new JPanel();
		panelWest3 = new JPanel();
		panelWest4 = new JPanel();
		panelWest5 = new JPanel();
		panelSouth = new JPanel();
	
		}
		
	/**
	 * build the GUI, create The Shape Of The GUI !!!!!
	 */
	public void setGUI() 
	{
		frame.setLayout(new BorderLayout());
		panelNorth.setLayout(new GridLayout(5,3));
		
		panelWest1.add(status);
		panelWest1.add(jdate);
		
		panelWest2.add(fromtCurrency);
		panelWest2.add(toCurrency);
		
		panelWest4.add(amountLabel);
		panelWest4.add(amount);
		panelWest4.add(Box.createRigidArea(new Dimension(70,0)));
		panelWest4.add(convert);
		
		panelWest5.add(resultLabel);
		panelWest5.add(result);
		panelWest5.add(GUIError);
	
		panelWest3.add(currency1);
		panelWest3.add(currency2);
		
		panelNorth.add(panelWest1);
		panelNorth.add(panelWest2);
		panelNorth.add(panelWest3);
		panelNorth.add(panelWest4);
		panelNorth.add(panelWest5);
		
		panelEast.add(table);
		panelSouth.add(copyRight);
		
		frame.add(BorderLayout.NORTH,panelNorth);
		frame.add(BorderLayout.CENTER,panelCenter);
		frame.add(BorderLayout.EAST,panelEast);
		frame.add(BorderLayout.SOUTH,panelSouth);
		
	    //change the width of the Jtable column in index 0
		table.getColumnModel().getColumn(0).setPreferredWidth(100);
		
		frame.setSize(460,620);
		frame.setVisible(true);
		
		//adding event listeners
		convert.addActionListener(this);
		
	}
	
	/**
	* The listener interface for receiving action events.
	* The action event occurs when you select convert option 
	* @return the convert result that show in the text field 
	*/
	@Override
	public void actionPerformed(ActionEvent e) 
	{
		//after we show the user Error we want to clear it
		GUIError.setText(space);
		
		//get the currency I have from the list
		String from = currency1.getSelectedItem().toString();			   
		String to   = currency2.getSelectedItem().toString(); 			   
		
		//get the currency code from the string JPY - Japan
		from = from.substring(0, 3);                          			   
		to = to.substring(0, 3);						    			
		
		try
		{
			if(e.getSource() == convert)
			{
				//prevent from user to convert the same currencies
				if(from.equals(to))												   
				{
					GUIError.setText("You trying to convert the same currency");
					Main.logger.info("The user is trying to convert the same currency"); 
				}
			
				else if(amount.getText() != null)
				{
					//convert amount in type string to double
					double amountToConvert = Double.parseDouble(amount.getText());	
				
					//call to convert function in scala and we will receive the result
					double temp = RateConvert.convert(from, to, amountToConvert);    
			   
					//get instance
					NumberFormat nf = NumberFormat.getInstance(); 					
					//set decimal places, show number with 5 numbers after dot
					nf.setMaximumFractionDigits(5);
					//casting double to string and set the result to Jtexfild
					result.setText(String.valueOf(nf.format(temp)));                 
				
					Main.logger.info("The user covert" + " " + amount.getText() + " " + " " + from + " " + "to" + " " + to);
				}
			}
		}   
		/**
		* if we doesn't success to convert an amount in type String to variable in type double 
		*/
		catch(NumberFormatException ex)
		{
			
			Main.logger.info("User did not enter an amount");
			GUIError.setText("Please enter an amount to exchange");	
		}
		
		
	}
	
	/**
	* This function is a anonymous thread that runs every 1 minute and checks if there is a Internet connection.
	* If the connection is interrupted the status label in the GUI changes it to offline
	*/
	void threadConnection()
	{
		Thread internetStatus  = new Thread(new Runnable(){
			
			@Override
			public void run() {
				while(true)
				{
					//A socket is an endpoint for communication between two machines
					Socket sock = new Socket();
					//Creates a socket address from an IP address and a port number.
				    InetSocketAddress addr = new InetSocketAddress("www.google.com",80);
					
				    try 
				    {
				    	//Connects this socket to the server with a specified timeout value.
				        sock.connect(addr,3000);
				        statusFlag =  true;
				        internetSet(statusFlag);
				    } 
				    /**
					* Signals that I/O exception of some sort has occurred 
					*/
				    catch (IOException e) 
				    {
				    	Main.logger.warning("[Thread-internetStatus]: The coonection to internt is interruptrd ");
				    	statusFlag = false;
				    	internetSet(statusFlag);
				    } 
				    finally 
				    {
				        try {sock.close();}
				        /**
						* Signals that I/O exception of some sort has occurred 
						*/
				        catch (IOException e) {Main.logger.warning(e.getMessage());}
				    }
				    try {
						Thread.sleep(1 * 60 * 1000);
						/**
						* Thrown when a thread is waiting, sleeping or occupied  
						*/
					} catch (InterruptedException e) {
						Main.logger.warning(e.getMessage());
						e.printStackTrace();
					}
					
				}
				
			}
				
		});
		internetStatus.start();
	}
	
	/**
	* This function change the status label
	* @param stateFlag the status of the Internet connection
	*/
	void internetSet(boolean stateFlag)
	{
	
		if(stateFlag)
		{	
			status.setText("Status: Online");
			Color statusColor = new Color(29, 99, 30);
			status.setForeground(statusColor);
			convert.setForeground(statusColor);
			
		}
		else
		{
			status.setText("Status: Offline");
			status.setForeground(Color.red);
			convert.setForeground(Color.red);
			
		}
	
	
	}
	
	/**
	* This function create Object data
	* @param rates - The rates from BOI
	* @return initialized Object with country name and rates
	*/
	public void setData(ArrayList<String> Rates) {
	
	  
	Object[][] tempData =		{
								{ "Currency", "Country Name", "Rate" },{"USD" , "USA ", Rates.get(0)},{"GBP","Great Britain",Rates.get(1)},
								{"JPY","Japan",Rates.get(2)}, {"EUR","EMU",Rates.get(3)},{"AUD","Australia",Rates.get(4)},{"CAD","Canada",Rates.get(5)},
								{"DKK", "Denmark",Rates.get(6)},{"NOK","Norway",Rates.get(7)},{"ZAR","South Africa",Rates.get(8)},
								{"SEK","Sweden",Rates.get(9)},{"CHF","Switzerland",Rates.get(10)},{"JOD","Jordan",Rates.get(11)},
								{"LBP","Lebanon",Rates.get(12)},{"EGP","Egypt",Rates.get(13) }
								};
		
								this.data = tempData;
	}
	
	
	/**
	* The model calls this function if the rates have been updated
	* @return GUI table
	*/
	public JTable getTable() 
	{
		return table;
	}
	
	/**
	* The model calls this function for changing the values in GUI table
	* @param jTable The update table
	*/
	public void setTable(JTable jTable)
	{
		//we get a table with updated rates from Thread
		this.table.setEnabled(true);
		this.table = jTable;
		this.table.setEnabled(false);
	}
	
	/**
	* The model calls this function to compare the rates of the GUI between BIO rates 
	* @return list of rates
	*/
	public ArrayList<String> getRates() {
		
		return rates;
	}
	
}
