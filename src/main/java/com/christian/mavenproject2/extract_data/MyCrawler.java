package com.christian.mavenproject2.extract_data;

import java.math.BigInteger;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import javax.annotation.Priority;

import org.omg.CosNaming.NamingContextExtPackage.AddressHelper;

import com.christian.mavenproject2.analisy_algorithms.CreateMaps;
import com.christian.mavenproject2.analisy_algorithms.GeoIPv4;
import com.christian.mavenproject2.analisy_algorithms.MyAlgorithms;
import com.christian.mavenproject2.crawler.HtmlParseData;
import com.christian.mavenproject2.crawler.Page;
import com.christian.mavenproject2.crawler.WebCrawler;
import com.christian.mavenproject2.crawler.WebURL;
import com.christian.mavenproject2.main.mainMenu;
import com.mysql.cj.jdbc.PreparedStatement;

public class MyCrawler extends WebCrawler {
	private final static Pattern FILTERS = Pattern.compile(".*(\\.(css|js|mid|mp2|mp3|mp4|wav|avi|mov|mpeg|ram|m4v|pdf"
			+ "|rm|smil|wmv|swf|wma|zip|rar|gz|bmp|gif|jpe?g|png|tiff?))$");
	MyAlgorithms algorithms = new MyAlgorithms();

	/**
	 * This method receives two parameters. The first parameter is the page in
	 * which we have discovered this new url and the second parameter is the new
	 * url. You should implement this function to specify whether the given url
	 * should be crawled or not (based on your crawling logic). In this example,
	 * we are instructing the crawler to ignore urls that have css, js, git, ...
	 * extensions and to only accept urls that start with
	 * "http://www.ics.uci.edu/". In this case, we didn't need the referringPage
	 * parameter to make the decision.
	 */
	@Override
	public boolean shouldVisit(Page referringPage, WebURL url) {
		mainMenu menu = this.getMyController().menu;
		menu.enlacesTotales += 1;
		if(url.getDepth() == 0) {
			menu.enlacesAceptados += 1;
			return true;
		}
		menu.setTextStats(menu.enlacesTotales + " ENLACES  |  " + menu.enlacesAceptados + " ACEPTADOS  |  "
				+ menu.enlacesProcesados + " PROCESADOS  |  " + menu.enlacesValidos + " VÁLIDOS  |  "
				+ menu.enlacesCaidos + " CAIDOS");
		String href = url.getURL().toLowerCase();
		/*if (FILTERS.matcher(href).matches())
			return false;*/
		
		//if ((menu.linkIsContains || menu.linkIsNoContains) && !visitLinkCondition(menu, href)) return false;	
		
		if(menu.linkIsContains || menu.linkIsNoContains) if(!shouldVisitLink(url, menu, referringPage)) return false;
		if(menu.isPriority) applyPriority(url, referringPage, menu);
		menu.enlacesAceptados += 1;
		return true;
	}
	
	/**
	 * This function is called when a page is fetched and ready to be processed
	 * by your program.
	 */
	@Override
	public void visit(Page page) {
		WebURL webURL = page.getWebURL();
		String geoURL = webURL.getSubDomain()+"."+webURL.getDomain();
		mainMenu menu = this.getMyController().menu;
		menu.enlacesProcesados += 1;
		if (page.getParseData() instanceof HtmlParseData && isUnderCondition(menu, page.getWebURL()) && visitLinkCondition(menu, page.getWebURL().getURL())
				&& algorithms.pageContainsContent(page, menu.contains, menu.isAll, menu.isAtLeast, menu.isNone, menu) && isGeolocation(geoURL,menu,menu.geoBoundingBox[0],menu.geoBoundingBox[1],menu.geoBoundingBox[2],menu.geoBoundingBox[3],page)) {
			menu.enlacesValidos += 1;
			menu.setTextStats(menu.enlacesTotales + " ENLACES  |  " + menu.enlacesAceptados + " ACEPTADOS  |  "
					+ menu.enlacesProcesados + " PROCESADOS  |  " + menu.enlacesValidos + " VÁLIDOS  |  "
					+ menu.enlacesCaidos + " CAIDOS");
			menu.writeConsole(tiempoEjecucion(page, menu));
		}
	}

	public String removePrefix(String url) {
		if (url.startsWith("http://"))
			url = url.replace("http://", "");
		else if (url.startsWith("http:\\\\"))
			url = url.replace("http:\\\\", "");
		else if (url.startsWith("https://"))
			url = url.replace("https://", "");
		else if (url.startsWith("https:\\\\"))
			url = url.replace("https:\\\\", "");
		return url;
	}

	public Boolean isUnderCondition(mainMenu menu, WebURL href) {
		String s = href.getURL().toLowerCase();
		if (menu.isContiene) {
			boolean foundContiene = false;
			String[] contiene = menu.contiene;
			for (int i = 0; i < contiene.length; ++i) {
				if (s.contains(contiene[i]))
					foundContiene = true;
			}
			if (!foundContiene)
				return false;
		}

		if (menu.isNoContiene) {
			String[] noContiene = menu.noContiene;
			for (int i = 0; i < noContiene.length; ++i) {
				if (s.contains(noContiene[i]))
					return false;
			}
		}

		if (menu.isRegex) {
			Pattern regex = Pattern.compile(menu.regex);
			if (!regex.matcher(s).matches())
				return false;
		}
		return true;
	}

	public Boolean visitLinkCondition(mainMenu menu, String href) {
		String s = href.toLowerCase();
		if (menu.linkIsContains) {
			boolean foundContiene = false;
			String[] contiene = menu.linkContainsValue;
			for (int i = 0; i < contiene.length; ++i) {
				if (s.contains(contiene[i]))
					foundContiene = true;
			}
			if (!foundContiene)
				return false;
		}

		if (menu.linkIsNoContains) {
			String[] noContiene = menu.linkNoContainsValue;
			for (int i = 0; i < noContiene.length; ++i) {
				if (s.contains(noContiene[i]))
					return false;
			}
		}
		return true;
	}

	public String tiempoEjecucion(Page p, mainMenu menu) {
		CreateMaps cr = new CreateMaps();
		String s = "";
		String url = p.getWebURL().getURL().toLowerCase();
		String geoURL = p.getWebURL().getSubDomain()+"."+p.getWebURL().getDomain();
		s = "URL: " + url + "\n";
		String idioma = "null";
		String email = "null";
		String geolocation = "null";
		String pais = "null";
		String ciudad = "null";
		String codigo_postal = "null";
		float latitud = 0;
		float longitud = 0;
		List emailList = new ArrayList();
		if (menu.isIdioma) {
			idioma = algorithms.detectLanguage(p);
			if (!idioma.isEmpty())
				s += "LANG: " + idioma + "\n";
		}
		if (menu.isEmails) {
			emailList = algorithms.detectEmails(p);
			email = algorithms.getAllEmails(emailList, menu);
			if (!email.isEmpty())
				s += "EMAIL: " + email + "\n";
		}
		if(menu.fetchGeolocation) {
			String[] geo = getGeolocation(geoURL);
			pais = geo[0];
			ciudad = geo[2];
			codigo_postal = geo[3];
			if(ciudad != null) geolocation = ciudad + "," + pais;
			else geolocation = pais;
			latitud = Float.valueOf(geo[8]);
			longitud = Float.valueOf(geo[9]);
			menu.heatMap += cr.addHeatMApValue(latitud+"", longitud+"");
			menu.circleMap += cr.addCircleMApValue(pais, getIP(geoURL), latitud+"", longitud+"", menu.enlacesValidos, geoURL, url);
			s += "GEOLOCATION : " + geolocation +"\n";
		}
		// CountryName, CountryCode, City , PostalCode, Region, AreaCode, dmaCode, MetroCode, Latitude, Longitude
		menu.data.put(menu.enlacesValidos + 1 + "", new Object[] { url, idioma, email, geolocation });
		if(menu.dbStore)
		{
			insertIntoURLCorrecta(menu,url,p.getWebURL().getParentUrl(),idioma,pais,ciudad,codigo_postal,latitud,longitud,menu.current_sesion);
			for(int i = 0; i < emailList.size(); ++i)
			{
				insertIntoEamil(menu, emailList.get(i).toString(), url, menu.current_sesion);
			}
		}
		s += "\n";
		return s;
	}

	 public boolean isGeolocation(String url, mainMenu menu, float _lngSW, float _latSW, float _lngNE, float _latNE, Page page)
	 {
		 if(menu.isGeoLanguage)
		 {
			 MyAlgorithms myAlg = new MyAlgorithms();
			 if(!myAlg.detectLanguage(page).equals(menu.geoLanguage)) return false;
		 }
		 if(menu.isGeoBoundingBox)
		 {
			 String[] ipGeo = getGeolocation(url);
			float lng = (float)(Float.parseFloat(ipGeo[9]));
			float lat = (float)(Float.parseFloat(ipGeo[8]));
			if(!((lng >= _lngSW) && (lng <= _lngNE) && (lat >= _latSW) && (lat <= _latNE))) return false;
		 }
		 return true;
	 }

	// CountryName, CountryCode, City , PostalCode, Region, AreaCode, dmaCode, MetroCode, Latitude, Longitude
	public String[] getGeolocation(String url) {
		return GeoIPv4.getLocation(url).getALL();
	}
	
	public String getIP(String url)
	{
		InetAddress ip = null;
		try {
			ip = java.net.InetAddress.getByName(url);
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return ip.getHostAddress();
	}
	
	public void applyPriority(WebURL url,Page referringPage, mainMenu menu)
	{
		url.setPriority((byte)3);
		boolean found = false;
		if (menu.dataPriority < 0 && algorithms.pageContainsContent(referringPage, menu.contains, menu.isAll,
				menu.isAtLeast, menu.isNone, menu)) {
			found = true;
			if(menu.isPenalyze) {
				url.setPriority((byte)(url.getPriority()+menu.dataPriority+url.getDepth()));				
			}
			else url.setPriority((byte)(url.getPriority()+menu.dataPriority));			}
		
		if (menu.linkPriority < 0 && isUnderCondition(menu, url)) {
			found = true;
			if(menu.isPenalyze) {
				url.setPriority((byte)(url.getPriority()+menu.linkPriority+url.getDepth()));
			}
			else url.setPriority((byte)(url.getPriority()+menu.linkPriority));
		}
		if (!found) url.setPriority((byte)100);
	}
	
	public boolean contieneTerminos(String url, String[] contiene){
		for (int i = 0; i < contiene.length; ++i) {
			if (url.contains(contiene[i])) return true;
		}
		return false;
	}
	
	public boolean noContieneTerminos(String url, String[] noContiene){
		for (int i = 0; i < noContiene.length; ++i) {
			if (url.contains(noContiene[i]))
				return false;
		}
		return true;
	}
	
	
	public boolean shouldVisitLink(WebURL url, mainMenu menu, Page referringPage){
		String href = url.getURL().toLowerCase();
		if(menu.isBroken){
			if(url.getDepth() == 1 && !visitLinkCondition(menu, href)) return false;
			if(url.getDepth() > 1 && !visitLinkCondition(menu,referringPage.getWebURL().getURL()) && visitLinkCondition(menu,referringPage.getWebURL().getParentUrl())) return false;
		}
		else{
			if(!visitLinkCondition(menu, href)) return false;
		}
		return true;
	}
	
	public void insertIntoURLCorrecta(mainMenu menu, String url, String url_padre, String language, String pais, String ciudad, String codigo_postal, float latitud, float longitud,int sesion_id)
	{
		try {
			String url_correcta;
			if(latitud == 0 || longitud ==0) {
				url_correcta = "INSERT INTO url_correcta(url,url_padre,idioma,pais,ciudad,codigo_postal,latitud,longitud,sesion_id)"+
						 "VALUE('"+url+"','"+url_padre+"','"+language+"','"+pais+"','"+ciudad+"','"+codigo_postal+"',null,null,"+sesion_id+");";
			}
			else
				url_correcta = "INSERT INTO url_correcta(url,url_padre,idioma,pais,ciudad,codigo_postal,latitud,longitud,sesion_id)"+
								 "VALUE('"+url+"','"+url_padre+"','"+language+"','"+pais+"','"+ciudad+"','"+codigo_postal+"',"+latitud+","+longitud+","+sesion_id+");";
			PreparedStatement stmt = (PreparedStatement) menu.con.prepareStatement(url_correcta);
			stmt.executeUpdate();
		} catch (SQLException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
	}
	
	public void insertIntoEamil(mainMenu menu, String email, String url, int sesion_id)
	{
		try {
			String insert_email = "INSERT INTO email(email,url_id) VALUES('"+email+"',(SELECT id_url_correcta FROM url_correcta"
					+ " WHERE sesion_id = "+sesion_id + " AND url = '"+url+"'));";
			PreparedStatement stmt = (PreparedStatement) menu.con.prepareStatement(insert_email);
			stmt.executeUpdate();
		} catch (SQLException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
	}
		
}