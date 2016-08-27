package il.ac.shenkar
import java.net.URL;
import scala.xml._;


/**
 * This Object is implemented in Scala language.
 * This object responsible to make the conversion between currencies.
 */
object RateConvert extends IRateConvert
{
  
  var rateFrom:Double = 1
  var rateTo:Double = 1
  var unitFrom:Double = 0
  var unitTo:Double = 0
  
 
  /**
  * This function convert the currency 
  * @param fromCurrecny the currency that we want to convert from 
  * @param toCurrecny the currency that we want 
  * @param amount the amount we want to convert
  * @return the result of the convert
  */
  def convert(fromCurrecny:String,toCurrecny:String,amount:Double):Double =
  {
    var result:Double = 0.0
    
    /*
    * call to XMLParsing function, we send to function the currency we want to convert 
    */
    XMLParsing(fromCurrecny,toCurrecny)
    result = (rateFrom * amount)/rateTo
    return result
    
  }
  
  /*
  * The function get two currencies that we want to convert between.
  * connecting to BOI and parsing the xml file and update the rates of the currency
  * and store them in local Variables
  */ 
  def XMLParsing(fromCurrecny:String,toCurrecny:String):Unit = 
  {
       
   try
   {
        val url = new URL("http://www.boi.org.il/currency.xml")
        val conn = url.openConnection
        val doc = XML.load(conn.getInputStream)
        XML.save("Rate.xml",doc)
        for(ob<-(doc\\"CURRENCY"))
        {
           if(fromCurrecny == (ob\\"CURRENCYCODE").text)
              {
                rateFrom = ((ob\\"RATE").text).toDouble
                rateFrom /= ((ob\\"UNIT").text).toDouble
              }
           if(toCurrecny == (ob\\"CURRENCYCODE").text)
               {
                rateTo = ((ob\\"RATE").text).toDouble
                rateTo /= ((ob\\"UNIT").text).toDouble
           
              }
        
         }
    }
   catch //if the Internet connection was interrupted  
   {
      case e:Exception=>
      {
         val doc = XML.load("Rate.xml")
         for(ob<-(doc\\"CURRENCY"))
        {
           if(fromCurrecny == (ob\\"CURRENCYCODE").text)
              {
                rateFrom = ((ob\\"RATE").text).toDouble
                rateFrom /= ((ob\\"UNIT").text).toDouble
       
               }
           if(toCurrecny == (ob\\"CURRENCYCODE").text)
               {
                rateTo = ((ob\\"RATE").text).toDouble
                rateTo /= ((ob\\"UNIT").text).toDouble
               
               }
        
         }
        
       }
      
    } 
  }


}