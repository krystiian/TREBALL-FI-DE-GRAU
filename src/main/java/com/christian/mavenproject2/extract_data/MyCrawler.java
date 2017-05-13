package com.christian.mavenproject2.extract_data;

import com.christian.mavenproject2.analisy_algorithms.HttpURLConnectionCall;
import com.christian.mavenproject2.analisy_algorithms.MyAlgorithms;
import com.christian.mavenproject2.analisy_algorithms.TimerTaskCalls;
import com.christian.mavenproject2.crawler.CrawlController;
import com.christian.mavenproject2.crawler.HtmlParseData;
import com.christian.mavenproject2.crawler.Page;
import com.christian.mavenproject2.crawler.WebCrawler;
import com.christian.mavenproject2.crawler.WebURL;
import com.christian.mavenproject2.geoip.GeoIPv4;
import com.christian.mavenproject2.main.mainMenu;
import com.sleepycat.je.rep.utilint.ServiceDispatcher.Response;

import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Set;
import java.util.regex.Pattern;

import javax.swing.text.DefaultCaret;

import org.apache.commons.codec.binary.Base64;
import org.json.JSONObject;
import org.xbill.DNS.Address;
import org.xbill.DNS.DClass;
import org.xbill.DNS.GPOSRecord;
import org.xbill.DNS.Lookup;
import org.xbill.DNS.Name;
import org.xbill.DNS.Record;
import org.xbill.DNS.SimpleResolver;
import org.xbill.DNS.TextParseException;
import org.xbill.DNS.Type;

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
		menu.setTextStats(menu.enlacesTotales + " ENLACES  |  " + menu.enlacesProcesados + " PROCESADOS  |  "
				+ menu.enlacesValidos + " VALIDOS  |  " + menu.enlacesAnalizados + " ANALIZADOS  |  "
				+ menu.enlacesErroneos + " ROTOS  |  " + menu.emailsFetched + " EMAILS");
		String href = url.getURL().toLowerCase();
		if (FILTERS.matcher(href).matches())
			return false;
		if ((menu.linkIsContains || menu.linkIsNoContains) && !visitLinkCondition(menu, href))
			return false;
		url.setPriority((byte) +url.getDepth());
		if (menu.dataPriority < 0 && algorithms.pageContainsContent(referringPage, menu.contains, menu.isAll,
				menu.isAtLeast, menu.isNone, menu)) {
			url.setPriority(((byte) ((int) url.getPriority() - menu.dataPriority + url.getDepth())));

		}
		if (menu.linkPriority < 0 && isUnderCondition(menu, url)) {
			url.setPriority(((byte) ((int) url.getPriority() - menu.linkPriority + url.getDepth())));
		}
		return true;
	}

	/**
	 * This function is called when a page is fetched and ready to be processed
	 * by your program.
	 */
	@Override
	public void visit(Page page) {
		WebURL webURL = page.getWebURL();
		String url = webURL.getURL();
		mainMenu menu = this.getMyController().menu;
		menu.writeConsole(getGeolocation(webURL.getSubDomain() + "." + webURL.getDomain())[0] + "\n");
		menu.enlacesProcesados += 1;
		if (page.getParseData() instanceof HtmlParseData && isUnderCondition(menu, page.getWebURL())
				&& algorithms.pageContainsContent(page, menu.contains, menu.isAll, menu.isAtLeast, menu.isNone, menu)) {
			menu.enlacesValidos += 1;
			menu.setTextStats(menu.enlacesTotales + " ENLACES  |  " + menu.enlacesProcesados + " PROCESADOS  |  "
					+ menu.enlacesValidos + " VALIDOS  |  " + menu.enlacesAnalizados + " ANALIZADOS  |  "
					+ menu.enlacesErroneos + " ROTOS  |  " + menu.emailsFetched + " EMAILS");
			// menu.writeConsole(page.getWebURL().getDepth()+"\n");
			menu.writeConsole(tiempoEjecucion(page, menu));
			menu.enlacesAnalizados += 1;
			menu.setTextStats(menu.enlacesTotales + " ENLACES  |  " + menu.enlacesProcesados + " PROCESADOS  |  "
					+ menu.enlacesValidos + " VALIDOS  |  " + menu.enlacesAnalizados + " ANALIZADOS  |  "
					+ menu.enlacesErroneos + " ROTOS  |  " + menu.emailsFetched + " EMAILS");
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
		String s = "";
		// COMPROBAR TODO EL TEMA DE GEOLOCATION
		menu.writeConsole(p.getWebURL().getURL());
		// if(!isGeolocation(p.getWebURL().getSubDomain()+"."+p.getWebURL().getDomain(),menu,-10.37109375,35.567980458012144,2.4609375,44.33956524809711))
		// return "MECK";
		String url = p.getWebURL().getURL().toLowerCase();
		s = "URL: " + url + "\n";
		String idioma = "";
		String email = "";
		String status = "";
		String geolocation = "";
		if (menu.isIdioma) {
			idioma = algorithms.detectLanguage(p);
			if (!idioma.isEmpty())
				s += "LANG: " + idioma + "\n";
		}
		if (menu.isEmails) {
			email = algorithms.getAllEmails(algorithms.detectEmails(p), menu);
			if (email != null)
				s += "EMAIL: " + email + "\n";
		}
		// if(menu.isBroken) {status = ""+ p.getStatusCode(); s+= "STATUS: " +
		// status + "\n";}
		// if(menu.isGeolocation)isGeolocation(p.getWebURL().getSubDomain()+p.getWebURL().getDomain(),menu,2.06817626953125,41.28606238749825,2.296142578125,41.492120839687814);
		menu.data.put(menu.enlacesAnalizados + 1 + "", new Object[] { url, idioma, email, geolocation });
		s += "\n";
		return s;
	}

	/*
	 * public boolean isGeolocation(String url, mainMenu menu, double _lngSW,
	 * double _latSW, double _lngNE, double _latNE) {
	 * 
	 * }
	 */

	public String[] getGeolocation(String url) {
		return GeoIPv4.getLocation(url).getALL();
	}
}