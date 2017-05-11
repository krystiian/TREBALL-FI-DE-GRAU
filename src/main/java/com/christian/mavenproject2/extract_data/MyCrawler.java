package com.christian.mavenproject2.extract_data;

import com.christian.mavenproject2.analisy_algorithms.HttpURLConnectionCall;
import com.christian.mavenproject2.analisy_algorithms.MyAlgorithms;
import com.christian.mavenproject2.analisy_algorithms.TimerTaskCalls;
import com.christian.mavenproject2.crawler.CrawlController;
import com.christian.mavenproject2.crawler.HtmlParseData;
import com.christian.mavenproject2.crawler.Page;
import com.christian.mavenproject2.crawler.WebCrawler;
import com.christian.mavenproject2.crawler.WebURL;
import com.christian.mavenproject2.main.mainMenu;
import com.sleepycat.je.rep.utilint.ServiceDispatcher.Response;

import java.util.Set;
import java.util.regex.Pattern;

import javax.swing.text.DefaultCaret;

import org.json.JSONObject;

public class MyCrawler extends WebCrawler {
    private final static Pattern FILTERS = Pattern.compile(
        ".*(\\.(css|js|mid|mp2|mp3|mp4|wav|avi|mov|mpeg|ram|m4v|pdf" +
        "|rm|smil|wmv|swf|wma|zip|rar|gz|bmp|gif|jpe?g|png|tiff?))$");
    MyAlgorithms algorithms = new MyAlgorithms();

    /**
     * This method receives two parameters. The first parameter is the page
     * in which we have discovered this new url and the second parameter is
     * the new url. You should implement this function to specify whether
     * the given url should be crawled or not (based on your crawling logic).
     * In this example, we are instructing the crawler to ignore urls that
     * have css, js, git, ... extensions and to only accept urls that start
     * with "http://www.ics.uci.edu/". In this case, we didn't need the
     * referringPage parameter to make the decision.
     */
     @Override
     public boolean shouldVisit(Page referringPage, WebURL url) {
         mainMenu menu = this.getMyController().menu;
    	 menu.enlacesTotales += 1;
         menu.setTextStats(menu.enlacesTotales + " ENLACES  |  " + menu.enlacesProcesados + " PROCESADOS  |  " + menu.enlacesValidos + " VALIDOS  |  " + menu.enlacesAnalizados + " ANALIZADOS  |  "+ menu.enlacesErroneos+ " ROTOS  |  " + menu.emailsFetched + " EMAILS");
    	 String href = url.getURL().toLowerCase();
    	 if(FILTERS.matcher(href).matches()) return false;
    	 url.setPriority((byte)10);
         if(menu.contentParentPriority && algorithms.pageContainsContent(referringPage, menu.contains, menu.isAll, menu.isAtLeast, menu.isNone,menu))
        	 {
        	 	url.setPriority(((byte)((int)url.getPriority()-1)));
        	 }
         if(menu.linkPriority && isUnderCondition(menu, url))
        	 {
	            url.setPriority(((byte)((int)url.getPriority()-1)));
        	 }
         return true; 
     }
         
     /**
      * This function is called when a page is fetched and ready
      * to be processed by your program.
      */
     @Override
     public void visit(Page page) {
         String url = page.getWebURL().getURL();
         mainMenu menu = this.getMyController().menu;
         menu.enlacesProcesados += 1;
         if (page.getParseData() instanceof HtmlParseData && isUnderCondition(menu, page.getWebURL()) && algorithms.pageContainsContent(page, menu.contains, menu.isAll, menu.isAtLeast, menu.isNone,menu)) {
             menu.enlacesValidos += 1;
             menu.setTextStats(menu.enlacesTotales + " ENLACES  |  " + menu.enlacesProcesados + " PROCESADOS  |  " + menu.enlacesValidos + " VALIDOS  |  " + menu.enlacesAnalizados + " ANALIZADOS  |  "+ menu.enlacesErroneos+ " ROTOS  |  " + menu.emailsFetched + " EMAILS");
             menu.writeConsole(tiempoEjecucion(page,menu));
             menu.enlacesAnalizados += 1;
             menu.setTextStats(menu.enlacesTotales + " ENLACES  |  " + menu.enlacesProcesados + " PROCESADOS  |  " + menu.enlacesValidos + " VALIDOS  |  " + menu.enlacesAnalizados + " ANALIZADOS  |  "+ menu.enlacesErroneos+ " ROTOS  |  " + menu.emailsFetched + " EMAILS");
         }
     }
     
     public String removePrefix(String url)
     {
        if(url.startsWith("http://")) url = url.replace("http://","");
        else if(url.startsWith("http:\\\\"))url = url.replace("http:\\\\","");
        else if(url.startsWith("https://")) url = url.replace("https://","");
        else if(url.startsWith("https:\\\\"))url = url.replace("https:\\\\","");
        return url;
     }
     
     public Boolean isUnderCondition(mainMenu menu, WebURL href)
     {
         String s = href.getURL().toLowerCase();
         if(menu.isContiene)
         {
             boolean foundContiene = false;
             String[] contiene = menu.contiene;
             for(int i=0; i < contiene.length; ++i)
             {
                 if(s.contains(contiene[i])) foundContiene = true;
             }
             if(!foundContiene) return false;
         }
         
         if(menu.isNoContiene)
         {
             String[] noContiene = menu.noContiene;
             for(int i=0; i < noContiene.length; ++i)
             {
                 if(s.contains(noContiene[i])) return false;
             }
         }
                  
         if(menu.isRegex)
         {
             Pattern regex = Pattern.compile(menu.regex);
             if(!regex.matcher(s).matches()) return false;
         }
         return true;
     }
     
     public String tiempoEjecucion(Page p, mainMenu menu)
     {
    	 String s = "";
    	 
    	 String url = p.getWebURL().getURL().toLowerCase(); s="URL: " + url +"\n";
         String idioma = "";
         String email = "";
         String status = "";
         if(menu.isIdioma) {idioma = algorithms.detectLanguage(p); if(!idioma.isEmpty())s+="LANG: " + idioma + "\n";}
         if(menu.isEmails) {email = algorithms.getAllEmails(algorithms.detectEmails(p),menu);if(email != null) s+= "EMAIL: "+email +"\n";}
         //if(menu.isBroken) {status = ""+ p.getStatusCode(); s+= "STATUS: " + status + "\n";}
         if(menu.isGeolocation)isGeolocation("http://ip-api.com/json/208.80.152.201",menu,0,0,0,0);
         menu.data.put(menu.enlacesAnalizados+1+"", new Object[]{url,idioma,email});
         s+= "\n";
         return s;    
     }
     
     public boolean isGeolocation(String url, mainMenu menu, double _lngSW, double _latSW, double _lngNE, double _latNE)
     {
    	 HttpURLConnectionCall http = new HttpURLConnectionCall();
    	 String status = "";
    	 String country = "";
    	 String countryCode = "";
    	 String city = "";
    	 boolean shouldRepeat = true;
    	 double lat = 0;
    	 double lon = 0;
         do{
             if(menu.periodFunction.getNumberOfCalls() < 150)
             {
            	 shouldRepeat = false;
            	 try {
					JSONObject response = new JSONObject(http.main("http://ip-api.com/json/208.80.152.201"));
					status = response.getString("status");
					menu.periodFunction.setNumberOfCalls(menu.periodFunction.getNumberOfCalls()+1);
					if(status.equals("success"))
					{
						country = response.getString("country");
						countryCode = response.getString("countryCode");
						city = response.getString("city");
						lat = response.getDouble("lat");
						lon = response.getDouble("lon");
						if(menu.byCoordinates && !((_lngSW <= lon) && (_lngNE >= lon) && (_latSW <= lat) && (_latNE >= lat))) return false;
						if(menu.byCountryCode && !countryCode.equals(menu.countryCode)) return false;	
						String s = city + " | " + country + " | " + countryCode + " | " + city + " | " + lon + " | " + lat;
						menu.geolocation[menu.geolocation.length] = s;
						//menu.writeConsole(menu.geolocation[menu.geolocation.length]);
						return true;
					}
		         } catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
		         }
             }
         }while(shouldRepeat);
    	 return false;
     }
}