package com.christian.mavenproject2.geoip;

import com.maxmind.geoip.Location;
import java.math.BigInteger;
import java.net.InetAddress;
import java.net.UnknownHostException;

public class GeoLocation {

	private String countryCode;
	private String countryName;
	private String postalCode;
	private String city;
	private String region;
	private int areaCode;
	private int dmaCode;
	private int metroCode;
	private float latitude;
	private float longitude;

	public GeoLocation(String countryCode, String countryName, String postalCode, String city, String region,
			int areaCode, int dmaCode, int metroCode, float latitude, float longitude) {
		this.countryCode = countryCode;
		this.countryName = countryName;
		this.postalCode = postalCode;
		this.city = city;
		this.region = region;
		this.areaCode = areaCode;
		this.dmaCode = dmaCode;
		this.metroCode = metroCode;
		this.latitude = latitude;
		this.longitude = longitude;
	}

	public static GeoLocation map(Location loc) {
		return new GeoLocation(loc.countryCode, loc.countryName, loc.postalCode, loc.city, loc.region, loc.area_code,
				loc.dma_code, loc.metro_code, loc.latitude, loc.longitude);
	}

	public String getCountryCode() {
		return this.countryCode;
	};

	public String getCountryName() {
		return this.countryName;
	};

	public String getPostalCode() {
		return this.postalCode;
	};

	public String getCity() {
		return this.city;
	};

	public String getRegion() {
		return this.region;
	};

	public int getAreaCode() {
		return this.areaCode;
	};

	public int getdmaCode() {
		return this.dmaCode;
	};

	public int getMetroCode() {
		return this.metroCode;
	};

	public float getLatitude() {
		return this.latitude;
	};

	public float getLongitude() {
		return this.longitude;
	};

	public String[] getALL() {
		// 0 1 2 3 4 5 6 7 8 9
		// CountryName, CountryCode, City , PostalCode, Region, AreaCode,
		// dmaCode, MetroCode, Latitude, Longitude
		String[] all = { this.countryName, this.countryCode, this.city, this.postalCode, this.region,
				this.areaCode + "", this.dmaCode + "", this.metroCode + "", this.latitude + "", this.longitude + "" };
		return all;
	}

	@Override
	public String toString() {
		return "GeoLocation{" + "countryCode='" + countryCode + '\'' + ", countryName='" + countryName + '\''
				+ ", postalCode='" + postalCode + '\'' + ", city='" + city + '\'' + ", region='" + region + '\''
				+ ", areaCode=" + areaCode + ", dmaCode=" + dmaCode + ", metroCode=" + metroCode + ", latitude="
				+ latitude + ", longitude=" + longitude + '}';
	}
	
	public long getIP(String url)
	{
		long ipAddress = (Long) null;
		try {
			ipAddress = new BigInteger(InetAddress.getByName(url).getAddress()).longValue();
			return ipAddress;
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return ipAddress;
	}
	
	public String[] getGeolocation(String url) {
		return GeoIPv4.getLocation(url).getALL();
	}
	
}