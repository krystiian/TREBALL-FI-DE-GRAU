package com.christian.mavenproject2.analisy_algorithms;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import com.mysql.cj.api.jdbc.Statement;
import com.mysql.cj.jdbc.PreparedStatement;

public class Database {

	public static Connection conn = null;
	public static String url = "jdbc:mysql://localhost:3306/";
	public static String dbName= "dbTFG";
	public static String properties = "?createDatabaseIfNotExist=true";
	public static String driver ="com.mysql.cj.jdbc.Driver";
	
	public static Connection main(String args[]) throws Exception
	{
			
		String userName = args[0];
		String password = args[1];
		conn = DriverManager.getConnection(url+dbName+properties,userName,password);
		Statement stmt = (Statement) conn.createStatement();
	      
	      String crawler_sesion = "CREATE TABLE IF NOT EXISTS crawler_sesion (\r\n" + 
	      		"	    				  id_sesion INT(11) NOT NULL AUTO_INCREMENT,\r\n" + 
	      		"	    				  numero_crawlers INT(11) NOT NULL,\r\n" + 
	      		"	    				  profundidad INT(11) NOT NULL,\r\n" + 
	      		"	    				  tiempo INT(11) NOT NULL,\r\n" + 
	      		"	    				  visita_no_contiene TINYINT(4) NOT NULL,\r\n" + 
	      		"	    				  visita_contiene TINYINT(4) NOT NULL,\r\n" + 
	      		"	    				  tiene_prioridad TINYINT(4) NOT NULL,\r\n" + 
	      		"	    				  tiene_penalizacion TINYINT(4) NOT NULL,\r\n" + 
	      		"	    				  data_at_least_one TINYINT(4) NOT NULL,\r\n" + 
	      		"	    				  data_none TINYINT(4) NOT NULL,\r\n" + 
	      		"	    				  data_all TINYINT(4) NOT NULL,\r\n" + 
	      		"	    				  url_no_contiene TINYINT(4) NOT NULL,\r\n" + 
	      		"	    				  url_si_contiene TINYINT(4) NOT NULL,\r\n" + 
	      		"	    				  url_regex TINYINT(4) NOT NULL,\r\n" + 
	      		"	    				  resumable TINYINT(4) NOT NULL,\r\n" + 
	      		"	    				  geo_bounding_box_area TINYINT(4) NOT NULL,\r\n" + 
	      		"	    				  geo_language TINYINT(4) NOT NULL,\r\n" + 
	      		"	    				  storage_excel TINYINT(4) NOT NULL,\r\n" + 
	      		"	    				  fecha_inicio DATETIME NOT NULL,\r\n" + 
	      		"	    				  fecha_final DATETIME NULL DEFAULT NULL,\r\n" + 
	      		"	    				  extract_emails TINYINT(4) NOT NULL,\r\n" + 
	      		"	    				  extract_geolocation TINYINT(4) NOT NULL,\r\n" + 
	      		"	    				  extract_broken TINYINT(4) NOT NULL,\r\n" + 
	      		"	    				  extract_language VARCHAR(45) NOT NULL,\r\n" + 
	      		"	    				  PRIMARY KEY (id_sesion));";
	      
	      String campos = "CREATE TABLE IF NOT EXISTS campos (\r\n" + 
	      		"  id_field INT(11) NOT NULL AUTO_INCREMENT,\r\n" + 
	      		"  categoria VARCHAR(45) NOT NULL,\r\n" + 
	      		"  valor VARCHAR(200) NOT NULL,\r\n" + 
	      		"  id_sesion INT(11) NOT NULL,\r\n" + 
	      		"  PRIMARY KEY (id_field),\r\n" + 
	      		"  INDEX id_sesion_idx (id_sesion ASC),\r\n" + 
	      		"  CONSTRAINT id_sesion\r\n" + 
	      		"    FOREIGN KEY (id_sesion)\r\n" + 
	      		"    REFERENCES crawler_sesion (id_sesion)\r\n" + 
	      		"    ON DELETE NO ACTION\r\n" + 
	      		"    ON UPDATE NO ACTION);";
	      
	      String url_correcta = "CREATE TABLE IF NOT EXISTS url_correcta (\r\n" + 
	      		"  id_url_correcta INT(11) NOT NULL AUTO_INCREMENT,\r\n" + 
	      		"  url VARCHAR(200) NOT NULL,\r\n" + 
	      		"  url_padre VARCHAR(200) NULL DEFAULT NULL,\r\n" + 
	      		"  idioma VARCHAR(45) NULL DEFAULT NULL,\r\n" + 
	      		"  pais VARCHAR(45) NULL DEFAULT NULL,\r\n" + 
	      		"  ciudad VARCHAR(45) NULL DEFAULT NULL,\r\n" + 
	      		"  codigo_postal VARCHAR(45) NULL DEFAULT NULL,\r\n" + 
	      		"  latitud FLOAT NULL DEFAULT NULL,\r\n" + 
	      		"  longitud FLOAT NULL DEFAULT NULL,\r\n" + 
	      		"  sesion_id INT(11) NOT NULL,\r\n" + 
	      		"  PRIMARY KEY (id_url_correcta),\r\n" + 
	      		"  INDEX id_sesion_idx (sesion_id ASC),\r\n" + 
	      		"  CONSTRAINT craw_ulrcorrecta\r\n" + 
	      		"    FOREIGN KEY (sesion_id)\r\n" + 
	      		"    REFERENCES crawler_sesion (id_sesion)\r\n" + 
	      		"    ON DELETE NO ACTION\r\n" + 
	      		"    ON UPDATE NO ACTION);";
	      
	      String email = "CREATE TABLE IF NOT EXISTS email (\r\n" + 
	      		"  id_email INT(11) NOT NULL AUTO_INCREMENT,\r\n" + 
	      		"  email VARCHAR(200) NULL DEFAULT NULL,\r\n" + 
	      		"  url_id INT(11) NOT NULL,\r\n" + 
	      		"  PRIMARY KEY (id_email),\r\n" + 
	      		"  INDEX id_url_idx (url_id ASC),\r\n" + 
	      		"  CONSTRAINT email_urlcorrecta\r\n" + 
	      		"    FOREIGN KEY (url_id)\r\n" + 
	      		"    REFERENCES url_correcta (id_url_correcta)\r\n" + 
	      		"    ON DELETE NO ACTION\r\n" + 
	      		"    ON UPDATE NO ACTION);";
	      
	      String url_erronea = "CREATE TABLE IF NOT EXISTS url_erronea ("
	      		+ "id_url_erronea INT(11) NOT NULL AUTO_INCREMENT,\r\n" + 
	      		"  url VARCHAR(200) NOT NULL,\r\n" + 
	      		"  url_padre VARCHAR(200) NULL DEFAULT NULL,\r\n" + 
	      		"  error_code INT(11) NOT NULL,\r\n" + 
	      		"  sesion_id INT(11) NOT NULL,\r\n" + 
	      		"  PRIMARY KEY (id_url_erronea),\r\n" + 
	      		"  INDEX id_sesion_idx (sesion_id ASC),\r\n" + 
	      		"  CONSTRAINT craw_urlerronea\r\n" + 
	      		"    FOREIGN KEY (sesion_id)\r\n" + 
	      		"    REFERENCES crawler_sesion (id_sesion)\r\n" + 
	      		"    ON DELETE NO ACTION\r\n" + 
	      		"    ON UPDATE NO ACTION);";
	      
	      stmt.executeUpdate(crawler_sesion);
	      stmt.executeUpdate(campos);
	      stmt.execute(url_correcta);
	      stmt.execute(url_erronea);
	      stmt.execute(email);
	      return conn;
	}
	
	public void insertCrawlerSesion(int numero_crawlers,int profundidad,int tiempo,boolean visita_no_contiene,boolean visita_contiene,boolean tiene_prioridad,boolean tiene_penalizacion,boolean data_at_least_one,boolean data_none,boolean data_all,boolean url_no_contiene,boolean url_si_contiene,boolean url_regex,boolean resumable,boolean geo_bounding_box_area,boolean geo_language,boolean storage_excel,boolean extract_emails,boolean extract_geolocation,boolean extract_broken,boolean extract_language){
		String insert = "INSERT INTO crawler_sesion(numero_crawlers,profundidad,tiempo,visita_no_contiene,visita_contiene,tiene_prioridad,tiene_penalizacion,data_at_least_one,data_none,data_all,url_no_contiene,url_si_contiene,url_regex,resumable,geo_bounding_box_area,geo_language,storage_excel,fecha_inicio,extract_emails,extract_geolocation,extract_broken,extract_language)"
						+ "VALUES("+numero_crawlers+","+profundidad+","+tiempo+","+visita_no_contiene+","+visita_contiene+","+tiene_prioridad+","+tiene_penalizacion+","+data_at_least_one+","+data_none+","+data_all+","+url_no_contiene+","+url_si_contiene+","+url_regex+","+resumable+","+geo_bounding_box_area+","+geo_language+","+storage_excel+",'"+new java.sql.Date(System.currentTimeMillis())+"',"+extract_emails+","+extract_geolocation+","+extract_broken+","+extract_language+");";
		try {
			Statement stmt = (Statement) conn.createStatement();
			stmt.executeUpdate(insert);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
