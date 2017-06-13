/*
 * Copyright 2017 chris.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.christian.mavenproject2.main;

import java.awt.Color;
import java.awt.Component;
import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TreeMap;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Pattern;

import javax.swing.DefaultComboBoxModel;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JRadioButton;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.SwingConstants;
import javax.swing.ToolTipManager;
import javax.swing.UIManager;
import javax.swing.border.BevelBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.EtchedBorder;
import javax.swing.border.LineBorder;
import javax.swing.border.MatteBorder;
import javax.swing.border.SoftBevelBorder;
import javax.swing.text.DefaultCaret;

import com.christian.mavenproject2.analisy_algorithms.CreateMaps;
import com.christian.mavenproject2.analisy_algorithms.MyAlgorithms;
import com.christian.mavenproject2.analisy_algorithms.MyExcel;
import com.christian.mavenproject2.analisy_algorithms.lastAction;
import com.christian.mavenproject2.analisy_algorithms.Database;
import com.christian.mavenproject2.crawler.CrawlConfig;
import com.christian.mavenproject2.crawler.CrawlController;
import com.christian.mavenproject2.crawler.PageFetcher;
import com.christian.mavenproject2.crawler.RobotstxtConfig;
import com.christian.mavenproject2.crawler.RobotstxtServer;
import com.christian.mavenproject2.extract_data.MyCrawler;
import com.mysql.cj.api.jdbc.Statement;
import com.mysql.cj.jdbc.PreparedStatement;
import javax.swing.JLayeredPane;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.ScrollPaneConstants;
import javax.swing.JPanel;
import javax.swing.ListSelectionModel;
import java.awt.SystemColor;

/**
 *
 * @author chris
 */
public class mainMenu extends javax.swing.JFrame {
	final URI uri = new URI("http://penggalian.org/bbox/");

	public mainMenu menu = this;
	public int enlacesTotales = 0; // OBTENIDOS
	public int emailtTotales = 0; // OBTENIDOS
	public int enlacesAceptados = 0; // ENLACES CUMPLEN CON LO PEDIDO
	public int enlacesCaidos = 0; // ENLACES CON ERROR
	public int enlacesProcesados = 0; // ENLACES GUARDADOS EN LA BBDD
	public int enlacesValidos = 0; // ENLACES GUARDADOS EN LA BBDD
	public int emailsFetched = 0; // EMAILS RECOPILADOS
	public int linkPriority = 0;
	public int dataPriority = 0;
	public int crawlers = 5;
	public int profundidad = -1;
	public int tiempo = 50;
	public long lastAction = 0;
	public String loadedSesion = null;
	public Connection con = null;
	public Connection viewCon = null;
	public float[] geoBoundingBox = {0,0,0,0};

	public static Pattern filenameRegex = Pattern.compile("[_a-zA-Z0-9\\-\\.]+");

	public Map<String, Object[]> data = new TreeMap<String, Object[]>();
	public Map<String, Object[]> dataBroken = new TreeMap<String, Object[]>();

	public String[] semilla = {};
	public String[] contiene = {};
	public String[] noContiene = {};
	public String[] linkContainsValue = {};
	public String[] linkNoContainsValue = {};
	public String[] contains = {};
	public String heatMap = "";
	public String circleMap = "";
	public String geoLanguage= "";
	public String priority = "";
	public String regex = "";
	public String store = "";
	public String dbUsername = "";
	public String dbpassword = "";
	public int current_sesion;
	
	
	public boolean isResumable = false;
	public boolean isContiene = false;
	public boolean isNoContiene = false;
	public boolean isRegex = false;
	public boolean isIdioma = false;
	public boolean isEmails = false;
	public boolean isAtLeast = false;
	public boolean isNone = false;
	public boolean isAll = false;
	public boolean isBroken = false;
	public boolean isStore = true;
	public boolean alreadyStored = false;
	public boolean linkIsContains = false;
	public boolean linkIsNoContains = false;
	public boolean isPriority = false;
	public boolean isPenalyze = false;
	public boolean isGeoLanguage = false;
	public boolean isGeoBoundingBox = false;
	public boolean fetchGeolocation = false;
	public boolean dbStore = false;
	
	CrawlController controller;

	/**
	 * Creates new form mainMenu
	 * 
	 * @throws URISyntaxException
	 * @throws SQLException 
	 */

	public mainMenu() throws URISyntaxException, SQLException {
		initComponents();
		menu.data.put("0", new Object[] { "URL", "LANGUAGE", "EMAILS", "GEOLOCATION"});
		menu.dataBroken.put("0", new Object[] { "STATUS", "URL", "LINK" });
		menu.setTextStats(menu.enlacesTotales + " ENLACES  |  " + menu.enlacesAceptados + " ACEPTADOS  |  "
				+ menu.enlacesProcesados + " PROCESADOS  |  " + menu.enlacesValidos + " VÁLIDOS  |  "
				+ menu.enlacesCaidos + " CAIDOS  |  " + menu.emailtTotales + " EMAILS");
		DefaultCaret caret = (DefaultCaret) this.jTextArea2.getCaret();
		caret.setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);
		jTextArea2.setEditable(false);		
		
		JLayeredPane layeredPane = new JLayeredPane();
		layeredPane.setBackground(new Color(57, 83, 109));
		layeredPane.setVisible(false);
		jTabbedPane1.addTab("RESULTS", null, layeredPane, null);
		
		scrollPane = new JScrollPane();
		scrollPane.setRequestFocusEnabled(false);
		scrollPane.setPreferredSize(new Dimension(58, 2));
		scrollPane.setMinimumSize(new Dimension(500, 500));
		scrollPane.setBounds(10, 120, 786, 395);
		layeredPane.add(scrollPane);
		
		table = new JTable();
		scrollPane.setViewportView(table);
		table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);

		comboBox = new JComboBox();
		comboBox.setForeground(new Color(57, 83, 109));
		comboBox.setBackground(SystemColor.inactiveCaption);
		comboBox.setEnabled(false);
		comboBox.setBounds(616, 8, 54, 20);
		layeredPane.add(comboBox);
		
		btnNewButton = new JButton("LOAD SESION");
		btnNewButton.setForeground(new Color(57, 83, 109));
		btnNewButton.setBackground(SystemColor.inactiveCaption);
		btnNewButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				menu.loadedSesion = (String) menu.comboBox.getSelectedItem();
				if(menu.loadedSesion.equals("ALL")) menu.label_2.setText("ALL SESIONS LOADED");
				else menu.label_2.setText("SESION " + menu.loadedSesion + " LOADED");
				menu.label_2.setVisible(true);
				VisibleViewButtons();
		        btnUrl.setEnabled(true);
			}
		});
		btnNewButton.setEnabled(false);
		btnNewButton.setBounds(680, 7, 116, 23);
		layeredPane.add(btnNewButton);
		
		txtyy_1 = new JTextField();
		txtyy_1.setBounds(153, 39, 86, 20);
		layeredPane.add(txtyy_1);
		txtyy_1.setColumns(10);
		
		txtRoot_1 = new JTextField();
		txtRoot_1.setBounds(153, 8, 86, 20);
		layeredPane.add(txtRoot_1);
		txtRoot_1.setColumns(10);
		
		JButton btnLoadDatabase = new JButton("LOAD DATABASE");
		btnLoadDatabase.setForeground(new Color(57, 83, 109));
		btnLoadDatabase.setBackground(SystemColor.inactiveCaption);
		btnLoadDatabase.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				try {
					String[] values = {getViewUsername(),getViewPassword()};
					Connection con = Database.main(values);
					menu.viewCon = con;
					lblDbLoadedSuccesfuly.setVisible(true);
					fillViewSesions();
					new java.util.Timer().schedule( 
					        new java.util.TimerTask() {
					            @Override
					            public void run() {
					            	lblDbLoadedSuccesfuly.setVisible(false);
					            }
					        }, 
					        5000 
					);
					
				} catch (Exception e) {
					// TODO Auto-generated catch block
					menu.writeConsole("The Username or Password introduced for DB is not correct.\n");
					jTabbedPane1.setSelectedIndex(1);
					e.printStackTrace();
				}
			}
		});
		btnLoadDatabase.setBounds(10, 21, 133, 23);
		layeredPane.add(btnLoadDatabase);
		
		btnEmails = new JButton("EMAILS");
		btnEmails.setForeground(new Color(57, 83, 109));
		btnEmails.setBackground(SystemColor.inactiveCaption);
		btnEmails.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				if(menu.loadedSesion.equals("ALL"))
					{
						menu.writeConsole("ENTRO IF\n");
						populate(3);
					}
				else{
					menu.writeConsole("ENTRO ELSE\n");
					populate(4);
				}
			}
		});
		btnEmails.setEnabled(false);
		btnEmails.setBounds(164, 86, 89, 23);
		layeredPane.add(btnEmails);
		
		btnNewButton_2 = new JButton("GEOLOCATION");
		btnNewButton_2.setForeground(new Color(57, 83, 109));
		btnNewButton_2.setBackground(SystemColor.inactiveCaption);
		btnNewButton_2.setEnabled(false);
		btnNewButton_2.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				if(menu.loadedSesion.equals("ALL")) populate(7);
				else populate(8);
			}
		});
		btnNewButton_2.setBounds(492, 86, 137, 23);
		layeredPane.add(btnNewButton_2);
		
		btnLanguage = new JButton("LANGUAGE");
		btnLanguage.setForeground(new Color(57, 83, 109));
		btnLanguage.setBackground(SystemColor.inactiveCaption);
		btnLanguage.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if(menu.loadedSesion.equals("ALL")) populate(5);
				else populate(6);
			}
		});
		btnLanguage.setEnabled(false);
		btnLanguage.setBounds(321, 86, 116, 23);
		layeredPane.add(btnLanguage);
		
		btnBrokenUrl = new JButton("BROKEN URL");
		btnBrokenUrl.setForeground(new Color(57, 83, 109));
		btnBrokenUrl.setBackground(SystemColor.inactiveCaption);
		btnBrokenUrl.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if(menu.loadedSesion.equals("ALL")) populate(9);
				else populate(10);
			}
		});
		btnBrokenUrl.setEnabled(false);
		btnBrokenUrl.setBounds(680, 86, 116, 23);
		layeredPane.add(btnBrokenUrl);
		
		lblDbLoadedSuccesfuly = new JLabel("DB LOADED SUCCESFULLY");
		lblDbLoadedSuccesfuly.setForeground(new Color(0, 102, 0));
		lblDbLoadedSuccesfuly.setVisible(false);
		lblDbLoadedSuccesfuly.setFont(new Font("Tahoma", Font.BOLD, 10));
		lblDbLoadedSuccesfuly.setBounds(250, 25, 173, 14);
		layeredPane.add(lblDbLoadedSuccesfuly);
		
		label_2 = new JLabel("");
		label_2.setHorizontalAlignment(SwingConstants.CENTER);
		label_2.setFont(new Font("Tahoma", Font.BOLD, 12));
		label_2.setBounds(614, 39, 182, 20);
		label_2.setForeground(new Color(0, 102, 0));
		label_2.setVisible(false);
		layeredPane.add(label_2);
		
		btnUrl = new JButton("URL");
		btnUrl.setForeground(new Color(57, 83, 109));
		btnUrl.setBackground(SystemColor.inactiveCaption);
		btnUrl.setEnabled(false);
		btnUrl.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if(menu.loadedSesion.equals("ALL")) populate(1);
				else populate(2);
			}
		});
		btnUrl.setBounds(10, 86, 89, 23);
		layeredPane.add(btnUrl);		
		ToolTipManager.sharedInstance().setDismissDelay(20000);
	}

	/**
	 * This method is called from within the constructor to initialize the form.
	 * WARNING: Do NOT modify this code. The content of this method is always
	 * regenerated by the Form Editor.
	 */
	@SuppressWarnings("unchecked")
	// <editor-fold defaultstate="collapsed" desc="Generated
	// Code">//GEN-BEGIN:initComponents
	private void initComponents() {
		jPopupMenu1 = new javax.swing.JPopupMenu();
		jTabbedPane1 = new javax.swing.JTabbedPane();
		jTabbedPane1.setTabLayoutPolicy(JTabbedPane.SCROLL_TAB_LAYOUT);
		jLayeredPane5 = new javax.swing.JLayeredPane();
		jScrollPane2 = new javax.swing.JScrollPane();
		jTextArea2 = new javax.swing.JTextArea();
		jTextField10 = new javax.swing.JTextField();

		setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

		jTextArea2.setBackground(new java.awt.Color(0, 0, 0));
		jTextArea2.setColumns(20);
		jTextArea2.setFont(new java.awt.Font("Consolas", 1, 12)); // NOI18N
		jTextArea2.setForeground(new java.awt.Color(51, 255, 51));
		jTextArea2.setRows(5);
		jTextArea2.addMouseListener(new java.awt.event.MouseAdapter() {
			@Override
			public void mouseClicked(java.awt.event.MouseEvent evt) {
				jTextArea2MouseClicked(evt);
			}
		});
		jScrollPane2.setViewportView(jTextArea2);

		jTextField10.setBackground(new java.awt.Color(0, 0, 0));
		jTextField10.setFont(new java.awt.Font("Consolas", 1, 11)); // NOI18N
		jTextField10.setForeground(new java.awt.Color(51, 255, 51));
		jTextField10.setHorizontalAlignment(SwingConstants.CENTER);
		jTextField10.addActionListener(new java.awt.event.ActionListener() {
			@Override
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				jTextField10ActionPerformed(evt);
			}
		});
		jPanel1 = new javax.swing.JPanel();
		jPanel1.setBackground(new Color(57, 83, 109));
		jLayeredPane2 = new javax.swing.JLayeredPane();
		jToggleButton1 = new javax.swing.JToggleButton();
		jToggleButton1.setBorder(null);
		jToggleButton1.setFont(new Font("Dialog", Font.BOLD, 12));
		jToggleButton1.setForeground(new Color(57, 83, 109));
		jToggleButton1.setBackground(UIManager.getColor("inactiveCaption"));

		jToggleButton1.setText("START");
		jToggleButton1.setToolTipText(
				"<html>\n\nSTART: Inicia el Crawling <br><br>\n\nCLOSE: Termina la ejecuci�n <br><br>\n\n</html>"); // NOI18N
		jToggleButton1.addActionListener(new java.awt.event.ActionListener() {
			@Override
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				jToggleButton1ActionPerformed(evt);
			}
		});
		jLayeredPane2.setLayer(jToggleButton1, javax.swing.JLayeredPane.DEFAULT_LAYER);
		jRadioButton1 = new javax.swing.JRadioButton();
		jRadioButton1.setBorder(new SoftBevelBorder(BevelBorder.LOWERED, null, null, null, null));
		jRadioButton1.setBackground(new Color(57, 83, 109));
		jRadioButton1.setFont(new Font("Dialog", Font.BOLD, 12));
		jRadioButton1.setForeground(new Color(240, 255, 255));
		jRadioButton1.setHorizontalAlignment(SwingConstants.RIGHT);
		jRadioButton1.setText("Contains");
		jRadioButton1.setToolTipText(
				"<html>\r\n<ol>\r\n<li>El campo <b>CONTAINS</b>, dir&aacute; que un enlace es valido si y solo s&iacute; el enlace <u>contiene </u>alguno de los terminos introducidos (separar por espacios para introducir m&aacute;s de un t&eacute;rmino)<ol>\r\n<li><i>camp secretaria est</i><br />\r\nEl enlace <i><a data-saferedirecturl=\"https://www.google.com/url?hl=ca&amp;q=https://www.upf.edu/&amp;source=gmail&amp;ust=1494655485883000&amp;usg=AFQjCNGtn5Q0CNbFQn66iWgM0pNyqLptmA\" href=\"https://www.upf.edu/\" target=\"_blank\">https://www.upf.edu/</a>&nbsp;&nbsp;&nbsp; </i>ser&aacute; identificado como de <i><u>no interes</u></i><br />\r\nEl enlace <i><a data-saferedirecturl=\"https://www.google.com/url?hl=ca&amp;q=https://www.upf.edu/&amp;source=gmail&amp;ust=1494655485883000&amp;usg=AFQjCNGtn5Q0CNbFQn66iWgM0pNyqLptmA\" href=\"https://www.upf.edu/\" target=\"_blank\">https://www.upf.edu/</a><u>camp</u>us/&nbsp;&nbsp; </i>ser&aacute; identificado como de<u><i> interes</i></u></li>\r\n</ol>\r\n</li>\r\n</ol>\r\n\r\n</html>");
		jTextField2 = new javax.swing.JTextField();
		jTextField2.setBorder(new SoftBevelBorder(BevelBorder.LOWERED, null, null, null, null));

		jTextField2.addActionListener(new java.awt.event.ActionListener() {
			@Override
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				jTextField2ActionPerformed(evt);
			}
		});
		jRadioButton9 = new javax.swing.JRadioButton();
		jRadioButton9.setBackground(new Color(57, 83, 109));
		jRadioButton9.setFont(new Font("Dialog", Font.BOLD, 12));
		jRadioButton9.setForeground(new Color(240, 255, 255));
		jRadioButton9.setHorizontalAlignment(SwingConstants.CENTER);
		jRadioButton9.setToolTipText(
				"<html><p>Ser&aacute; v&aacute;lido si <strong>todos </strong>los t&eacute;rminos introducidos est&aacute;n en el contenido de la p&aacute;gina</p></html>\r\n");

		jRadioButton9.setText("All");
		jRadioButton9.addActionListener(new java.awt.event.ActionListener() {
			@Override
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				jRadioButton9ActionPerformed(evt);
			}
		});
		jLabel8 = new javax.swing.JLabel();
		jLabel8.setFont(new Font("Dialog", Font.BOLD, 12));
		jLabel8.setForeground(new Color(240, 255, 255));
		jLabel8.setHorizontalAlignment(SwingConstants.RIGHT);
		jLabel8.setHorizontalTextPosition(SwingConstants.LEADING);

		jLabel8.setText("Contains");
		jLabel8.setToolTipText(
				"<html>\r\n\r\nSolo serán válidos aquellos enlaces que su contenido cumpla con la condición escogida.<br><br>\r\n\r\n</html>");
		jRadioButton11 = new javax.swing.JRadioButton();
		jRadioButton11.setSelected(true);
		jRadioButton11.setBackground(new Color(57, 83, 109));
		jRadioButton11.setFont(new Font("Dialog", Font.BOLD, 12));
		jRadioButton11.setForeground(new Color(240, 255, 255));

		jRadioButton11.setText("Excel");
		jRadioButton11.setToolTipText(
				"<html>\r\n\r\nLos resultados extraidos se guardarán en un excel,<br>bajo el nombre {filename}_success.xlsx<br><br>\r\n\r\n<i> Si la opción Broken Links está activa, ser generara<br>un fichero {filename}_errores</i>\r\n\r\n</html>");
		jRadioButton11.addActionListener(new java.awt.event.ActionListener() {
			@Override
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				jRadioButton11ActionPerformed(evt);
			}
		});
		jRadioButton10 = new javax.swing.JRadioButton();
		jRadioButton10.setBorder(new BevelBorder(BevelBorder.LOWERED, null, null, null, null));
		jRadioButton10.setBackground(new Color(57, 83, 109));
		jRadioButton10.setFont(new Font("Dialog", Font.BOLD, 12));
		jRadioButton10.setForeground(new Color(240, 255, 255));
		jRadioButton10.setToolTipText(
				"<html>\r\n\r\nSe extraeran todos los enlaces caidos de las páginas que cumplan la condicion URL<br><br>\r\n\r\nLos resultados se guardarán en {filename}_errores.xlsx\r\n\r\n</html>");
		jRadioButton10.setHorizontalAlignment(SwingConstants.LEFT);
		jRadioButton10.setSelected(true);

		jRadioButton10.setText("Broken Links");
		jRadioButton10.addActionListener(new java.awt.event.ActionListener() {
			@Override
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				jRadioButton10ActionPerformed(evt);
			}
		});

		rdbtnGeolocation = new JRadioButton("Geolocation");
		rdbtnGeolocation.setSelected(true);
		rdbtnGeolocation.setBorder(new EmptyBorder(0, 0, 0, 0));
		rdbtnGeolocation.setBackground(new Color(57, 83, 109));
		rdbtnGeolocation.setFont(new Font("Dialog", Font.BOLD, 12));
		rdbtnGeolocation.setForeground(new Color(240, 255, 255));
		rdbtnGeolocation.setToolTipText(
				"<html>\r\n\r\nSe extraerá la información siguiente del servidor donde está\r\n<br>localizada la página de interés<br>\r\n\r\n<ul>\r\n<li>Pais</li>\r\n<li>Ciudad</li>\r\n<li>Latitud | Longitud</li>\r\n</ul>\r\n\r\n</html>");
		rdbtnGeolocation.setHorizontalAlignment(SwingConstants.CENTER);
		jRadioButton4 = new javax.swing.JRadioButton();
		jRadioButton4.setSelected(true);
		jRadioButton4.setBorder(new EtchedBorder(EtchedBorder.LOWERED, null, null));
		jRadioButton4.setBackground(new Color(57, 83, 109));
		jRadioButton4.setFont(new Font("Dialog", Font.BOLD, 12));
		jRadioButton4.setForeground(new Color(240, 255, 255));
		jRadioButton4.setHorizontalAlignment(SwingConstants.CENTER);

		jRadioButton4.setText("Language");
		jRadioButton4.setToolTipText(
				"<html>\r\nExtraerá en que idioma se encuentra la web,<br>basandose en la primera condición que encuentre.<br>\r\n\r\n<ol>\r\n<li>Buscará si existe algún tag que indique el idioma (MUY FIABLE)</li>\r\n<li>Buscará si contiene algún párrafo y lo analizará (FIABLE)</li>\r\n<li>Buscará el título de la página y lo analizará (POCO FIAFLE)</li>\r\n</ol>\r\n</html>\r\n");
		jRadioButton5 = new javax.swing.JRadioButton();
		jRadioButton5.setSelected(true);
		jRadioButton5.setBorder(null);
		jRadioButton5.setBackground(new Color(57, 83, 109));
		jRadioButton5.setFont(new Font("Dialog", Font.BOLD, 12));
		jRadioButton5.setForeground(new Color(240, 255, 255));
		jRadioButton5.setHorizontalAlignment(SwingConstants.LEFT);

		jRadioButton5.setText("Emails");
		jRadioButton5.setToolTipText(
				"<html>\r\n\r\nExtraerá todos los emails contenidos en las páginas consideradas de interés.\r\n\r\n</html>");

		lblSeed = new JLabel("Seed");
		lblSeed.setFont(new Font("Dialog", Font.BOLD, 12));
		lblSeed.setForeground(new Color(240, 255, 255));
		lblSeed.setHorizontalAlignment(SwingConstants.RIGHT);
		lblSeed.setToolTipText(
				"<html>introducir una o m&aacute;s paginas iniciales [separadas por espacios]&nbsp; est&aacute;s p&aacute;ginas ser&aacute;n donde el programa empieza a recopilar los enlaces.<ol><br>\r\n\t<li><i><a data-saferedirecturl=\"https://www.google.com/url?hl=ca&amp;q=http://www.tecnonews.info&amp;source=gmail&amp;ust=1494655485966000&amp;usg=AFQjCNGIe_LIiz7i_ULE-iu2N94VqWo3EA\" href=\"http://www.tecnonews.info\" target=\"_blank\">http://www.tecnonews.info</a></i> para una &uacute;nica pagina SEED</li>\r\n\t<li><i><a data-saferedirecturl=\"https://www.google.com/url?hl=ca&amp;q=http://www.tecnonews.info&amp;source=gmail&amp;ust=1494655485967000&amp;usg=AFQjCNEzYjPenUw-VyCDhwnMWp3Mtgb2Ug\" href=\"http://www.tecnonews.info\" target=\"_blank\">http://www.tecnonews.info</a> <a data-saferedirecturl=\"https://www.google.com/url?hl=ca&amp;q=http://www.upf.edu&amp;source=gmail&amp;ust=1494655485967000&amp;usg=AFQjCNHJyOfEVP3tv1W-w5OUt_RGQXtOcQ\" href=\"http://www.upf.edu\" target=\"_blank\">http://www.upf.edu</a> </i>para m&aacute;s de una p&aacute;gina SEED<br />\r\n\t&nbsp;</li>\r\n\t</ol></html>");

		jTextField1 = new JTextField();
		jTextField1.setBorder(new SoftBevelBorder(BevelBorder.LOWERED, null, null, null, null));
		jTextField1.setText("http://www.tecnonews.info");
		jTextField1.setColumns(10);

		lblCrawlers = new JLabel("Crawlers");
		lblCrawlers.setFont(new Font("Dialog", Font.BOLD, 12));
		lblCrawlers.setForeground(new Color(240, 255, 255));
		lblCrawlers.setToolTipText(
				"<html>\r\n<p>Indica cuantos procesos crawlers concurrentes habr&aacute; en la ejecuci&oacute;n</p>\r\n<p>&nbsp;</p>\r\n<ol>\r\n<li>El valor de este tiene que ser como <strong><u>m&iacute;nimo 1</u></strong>.</li>\r\n</ol>\r\n\r\n</html>");
		lblCrawlers.setHorizontalAlignment(SwingConstants.RIGHT);

		jTextField3 = new JTextField();
		jTextField3.setBorder(new SoftBevelBorder(BevelBorder.LOWERED, null, null, null, null));
		jTextField3.setHorizontalAlignment(SwingConstants.CENTER);
		jTextField3.setText("5");
		jTextField3.setColumns(10);

		lblDepth = new JLabel("Depth");
		lblDepth.setFont(new Font("Dialog", Font.BOLD, 12));
		lblDepth.setForeground(new Color(240, 255, 255));
		lblDepth.setToolTipText(
				"<html>\r\n<ol>\r\n<li>El campo depth es el atributo que tiene cada enlace indicando cuantos saltos se han requerido desde la p&aacute;gina SEED para llegar hasta ese enlace,<br />\r\nel campo <b>DEPTH</b> por lo tanto, limitar&aacute; la recopilacion de nuevos enlaces basados en la produndidad de este.<ol>\r\n<li>Si <i>DEPTH = 0</i> , &uacute;nicamente se analizar&aacute; la p&aacute;gina inicial (no se visitar&aacute;n los enlaces de esta)</li>\r\n<li>Si <i>DEPTH = -1</i>, se analizar&aacute;n todos los enlaces sin importar cuan lejos esten.</li>\r\n<li>SI <i>DEPTH</i> es cualquier otro valor, 2 por ejemplo, y tenemos la pag&iacute;na <a data-saferedirecturl=\"https://www.google.com/url?hl=ca&amp;q=http://www.tecnonews.info&amp;source=gmail&amp;ust=1494655485883000&amp;usg=AFQjCNH3A5jT7VCucwIjs8vnzm18n7s3tA\" href=\"http://www.tecnonews.info\" target=\"_blank\">http://www.tecnonews.info</a> como seed, uno de los casos ser&iacute;a<br />\r\n&nbsp;<br />\r\n&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; <a data-saferedirecturl=\"https://www.google.com/url?hl=ca&amp;q=http://www.tecnonews.info&amp;source=gmail&amp;ust=1494655485883000&amp;usg=AFQjCNH3A5jT7VCucwIjs8vnzm18n7s3tA\" href=\"http://www.tecnonews.info\" target=\"_blank\">http://www.tecnonews.info</a> (0) -&gt;<br />\r\n&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; <a data-saferedirecturl=\"https://www.google.com/url?hl=ca&amp;q=http://www.tecnonews.info/encuestas&amp;source=gmail&amp;ust=1494655485883000&amp;usg=AFQjCNGeB5D3gv2rTpAdBPQAGTnTkXRvEQ\" href=\"http://www.tecnonews.info/encuestas\" target=\"_blank\">http://www.tecnonews.info/<wbr>encuestas</wbr></a> (1) -&gt;<br />\r\n&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; <a data-saferedirecturl=\"https://www.google.com/url?hl=ca&amp;q=https://www.facebook.com/Tecnonews&amp;source=gmail&amp;ust=1494655485883000&amp;usg=AFQjCNEFC5dKo0NeLvk5fluRrTB85mNPpA\" href=\"https://www.facebook.com/Tecnonews\" target=\"_blank\">https://www.facebook.com/<wbr><wbr><wbr><wbr><wbr>Tecnonews</wbr></wbr></wbr></wbr></wbr></a> (2)<br />\r\n<br />\r\nY los enlaces contenidos en <a data-saferedirecturl=\"https://www.google.com/url?hl=ca&amp;q=https://www.facebook.com/Tecnonews&amp;source=gmail&amp;ust=1494655485883000&amp;usg=AFQjCNEFC5dKo0NeLvk5fluRrTB85mNPpA\" href=\"https://www.facebook.com/Tecnonews\" target=\"_blank\">https://www.facebook.com/<wbr><wbr><wbr><wbr><wbr><wbr><wbr><wbr><wbr><wbr><wbr><wbr><wbr><wbr><wbr>Tecnonews</wbr></wbr></wbr></wbr></wbr></wbr></wbr></wbr></wbr></wbr></wbr></wbr></wbr></wbr></wbr></a>, ya no se visitar&iacute;an, puesto que serian<br />\r\nde profundidad &gt; 2.</li>\r\n</ol>\r\n</li>\r\n</ol>\r\n</html>");

		jTextField9 = new JTextField();
		jTextField9.setBorder(new SoftBevelBorder(BevelBorder.LOWERED, null, null, null, null));
		jTextField9.setHorizontalAlignment(SwingConstants.CENTER);
		jTextField9.setText("-1");
		jTextField9.setColumns(10);
		jTextField5 = new javax.swing.JTextField();
		jTextField5.setBorder(new SoftBevelBorder(BevelBorder.LOWERED, null, null, null, null));
		jTextField5.setAlignmentY(Component.TOP_ALIGNMENT);

		jTextField5.setHorizontalAlignment(SwingConstants.CENTER);
		jTextField5.setText("50");
		jTextField5.addActionListener(new java.awt.event.ActionListener() {
			@Override
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				jTextField5ActionPerformed(evt);
			}
		});

		lblTime = new JLabel("Time");
		lblTime.setFont(new Font("Dialog", Font.BOLD, 12));
		lblTime.setForeground(new Color(240, 255, 255));
		lblTime.setToolTipText(
				"<html>\r\n<p>El campo <b>TIME</b> es la limitaci&oacute;n en el tiempo en que se hacen visitas a p&aacute;ginas de un mismo servidor, para evitar saturaciones, expresado en <em>milisegundos(ms)</em></p>\r\n\r\n</html>");

		lblDoNotVisit = new JLabel("VISIT LINKS WHICH");
		lblDoNotVisit.setToolTipText(
				"<html>\r\n\r\nAquí se indicará que enlaces serán los que se pondrán en la cola para comprobar<br>\r\nque estos cumplen las condiciones indicadas y así analizarlos<br><br>\r\n\r\nSi no se especifica ninguna opción, se cogerán todos los enlaces contenidos<br>\r\nen cada página.\r\n\r\n</html>");
		lblDoNotVisit.setForeground(new Color(153, 204, 255));
		lblDoNotVisit.setHorizontalAlignment(SwingConstants.CENTER);
		lblDoNotVisit.setFont(new Font("Tahoma", Font.BOLD, 12));

		lblCrawlerParameters = new JLabel("CRAWLER PARAMETERS");
		lblCrawlerParameters.setForeground(new Color(153, 204, 255));
		lblCrawlerParameters.setHorizontalAlignment(SwingConstants.CENTER);
		lblCrawlerParameters.setFont(new Font("Tahoma", Font.BOLD, 12));

		javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
		jPanel1Layout.setHorizontalGroup(jPanel1Layout.createParallelGroup(Alignment.LEADING)
				.addGroup(jPanel1Layout.createSequentialGroup()
						.addComponent(jLayeredPane2, GroupLayout.PREFERRED_SIZE, 801, GroupLayout.PREFERRED_SIZE)
						.addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)));
		jPanel1Layout.setVerticalGroup(jPanel1Layout.createParallelGroup(Alignment.LEADING)
				.addGroup(jPanel1Layout.createSequentialGroup()
						.addComponent(jLayeredPane2, GroupLayout.DEFAULT_SIZE, 515, Short.MAX_VALUE)
						.addContainerGap()));
		jRadioButton8 = new javax.swing.JRadioButton();
		jRadioButton8.setBackground(new Color(57, 83, 109));
		jRadioButton8.setFont(new Font("Dialog", Font.BOLD, 12));
		jRadioButton8.setForeground(new Color(240, 255, 255));
		jRadioButton8.setHorizontalAlignment(SwingConstants.CENTER);
		jRadioButton8.setToolTipText(
				"<html>\r\n<p>Ser&aacute; v&aacute;lido si <strong>al menos uno</strong> de los t&eacute;rminos est&aacute; en el contenido de la p&aacute;gina</p>\r\n</html>");

		jRadioButton8.setText("At least one");
		jRadioButton8.addActionListener(new java.awt.event.ActionListener() {
			@Override
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				jRadioButton8ActionPerformed(evt);
			}
		});
		jRadioButton3 = new javax.swing.JRadioButton();
		jRadioButton3.setBackground(new Color(57, 83, 109));
		jRadioButton3.setFont(new Font("Dialog", Font.BOLD, 12));
		jRadioButton3.setForeground(new Color(240, 255, 255));
		jRadioButton3.setHorizontalAlignment(SwingConstants.CENTER);
		jRadioButton3.setToolTipText(
				"<html>\r\nSerá válido si ninguno de los términos aparece en el contenido de la página\r\n</html>");

		jRadioButton3.setText("None");
		jRadioButton3.addActionListener(new java.awt.event.ActionListener() {
			@Override
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				jRadioButton3ActionPerformed(evt);
			}
		});
		jTextField8 = new javax.swing.JTextField();
		jTextField8.setBorder(new SoftBevelBorder(BevelBorder.LOWERED, null, null, null, null));
		jTextField8.setText("nombre_fichero");

		linkNoContains = new JTextField();
		linkNoContains.setText("twitter facebook instagram youtube");
		linkNoContains.setBorder(new SoftBevelBorder(BevelBorder.LOWERED, null, null, null, null));
		linkNoContains.setColumns(10);

		linkContains = new JTextField();
		linkContains.setBorder(new SoftBevelBorder(BevelBorder.LOWERED, null, null, null, null));
		linkContains.setColumns(10);

		lblAnalyse = new JLabel("EXTRACT");
		lblAnalyse.setForeground(new Color(153, 204, 255));
		lblAnalyse.setToolTipText(
				"<html>\r\n\r\nSe indica la información que se quiere extraer de los<br>enlaces considerados <b>válidos</b>.\r\n</html>");
		lblAnalyse.setFont(new Font("Tahoma", Font.BOLD, 12));
		lblAnalyse.setHorizontalAlignment(SwingConstants.CENTER);

		jRadioButton7 = new JRadioButton();
		jRadioButton7.setBackground(new Color(57, 83, 109));
		jRadioButton7.setFont(new Font("Dialog", Font.BOLD, 12));
		jRadioButton7.setForeground(new Color(240, 255, 255));
		jRadioButton7.setHorizontalTextPosition(SwingConstants.RIGHT);
		jRadioButton7.setToolTipText(
				"<html>\r\n<ol>\r\n<li>El campo <strong>NOT CONTAINS</strong>, dir&aacute; que un enlace es valido si y solo s&iacute; el enlace <u>no contiene</u> niguno de los terminos introducidos (separar por espacios para introducir m&aacute;s de un t&eacute;rmino)<br />\r\n<ol>\r\n<li><i>upf ?</i><br />\r\nEl enlace <i><a data-saferedirecturl=\"https://www.google.com/url?hl=ca&amp;q=https://twitter.com/&amp;source=gmail&amp;ust=1494655485883000&amp;usg=AFQjCNHx1_QNHXtew7o9gyPGxpuMvuNzQw\" href=\"https://twitter.com/\" target=\"_blank\">https://twitter.com/</a> &nbsp;&nbsp; </i>ser&aacute; identificado como de <i><u>interes</u></i><br />\r\nEl enlace <i><a data-saferedirecturl=\"https://www.google.com/url?hl=ca&amp;q=https://www&amp;source=gmail&amp;ust=1494655485883000&amp;usg=AFQjCNHGqrB6C-TzOXNL7VB_vzcVWd1o1w\" href=\"https://www\" target=\"_blank\">https://www</a>.<u>upf</u>.edu/campus/&nbsp;&nbsp; </i>ser&aacute; identificado como de <i><u>no interes</u></i><br />\r\nEl enlace <i><a data-saferedirecturl=\"https://www.google.com/url?hl=ca&amp;q=https://twitter.com/&amp;source=gmail&amp;ust=1494655485884000&amp;usg=AFQjCNFN5J4asHkqng5JpJN3Xf0ItBq6BQ\" href=\"https://twitter.com/\" target=\"_blank\">https://twitter.com/</a><u>?</u>lang=it</i>&nbsp; ser&aacute; identificado como de <i><u>no interes</u></i></li>\r\n</ol>\r\n</li>\r\n</ol>\r\n</html>");
		jRadioButton7.setText("Not contains");
		jRadioButton7.setHorizontalAlignment(SwingConstants.LEFT);

		jRadioButton2 = new JRadioButton();
		jRadioButton2.setBorder(new MatteBorder(1, 1, 1, 1, new Color(0, 0, 0)));
		jRadioButton2.setBackground(new Color(57, 83, 109));
		jRadioButton2.setFont(new Font("Dialog", Font.BOLD, 12));
		jRadioButton2.setForeground(new Color(240, 255, 255));
		jRadioButton2.setToolTipText(
				"<html>\r\n<ol>\r\n<li>El campo <b>REGEX</b>, dir&aacute; que un enlace es valido si y solo s&iacute; el enlace cumple con la <i>expresio&oacute;n regular</i> introducida (creo que esta era la &uacute;nica metedolog&iacute;a de validaci&oacute;n que se hac&iacute;a en el anterior TFG), esto es para busquedas muy concretas y bastante avanzadas.<ol>\r\n<li>&nbsp;&nbsp;&nbsp; .*\\\\.(js|css)($|\\\\?.*)&nbsp;&nbsp; es una expresion regular que valida enlaces que tienen el formato en .js o .css<br />\r\n<br />\r\nEl enlace <i>/a/fancy/uri/which/is/invalid.<wbr>js&nbsp; </wbr></i>ser&aacute; de interes<br />\r\nEl enlace /a/fancy/uri/.js/which/is/<wbr><wbr><wbr>valid&nbsp;&nbsp;&nbsp; ser&aacute; de no interes</wbr></wbr></wbr></li>\r\n</ol>\r\n</li>\r\n</ol>\r\n</html>");
		jRadioButton2.setText("Regex");
		jRadioButton2.setHorizontalAlignment(SwingConstants.RIGHT);

		jTextField7 = new JTextField();
		jTextField7.setBorder(new SoftBevelBorder(BevelBorder.LOWERED, null, null, null, null));

		jTextField6 = new JTextField();
		jTextField6.setBorder(new SoftBevelBorder(BevelBorder.LOWERED, null, null, null, null));

		jTextField4 = new JTextField();
		jTextField4.setBorder(new SoftBevelBorder(BevelBorder.LOWERED, null, null, null, null));

		label = new JLabel("URL");
		label.setForeground(new Color(153, 204, 255));
		label.setToolTipText(
				"<html>\r\nSe indicarán las condiciones que se tienen que cumplir a nivel de enlace<br><br>\r\n<p>Se pueden rellenar varios campos para incluir / excluir determinados enlaces con diversas condicionas, por ejemplo,<br />\r\n<br />\r\n<b>contains </b>: upf tecno&nbsp;&nbsp;</p>\r\n<p><strong>Not contains</strong>: twitter&nbsp;&nbsp;&nbsp;</p>\r\n<p>indicar&aacute; que un enlace es valido si este contiene el termino upf o tecno, pero evitar&aacute; que este enlace contenga la palabra twitter.</p>\r\n\r\n</html>");
		label.setFont(new Font("Tahoma", Font.BOLD, 12));
		label.setHorizontalAlignment(SwingConstants.CENTER);

		lblNewLabel = new JLabel("GEOLOCATION");
		lblNewLabel.setForeground(new Color(153, 204, 255));
		lblNewLabel.setToolTipText(
				"<html>\r\n<p>Indicar&aacute;n las condiciones que se tienen que cumplir a nivel de geolocalizaci&oacute;n del enlace.</p>\r\n</html>");
		lblNewLabel.setHorizontalAlignment(SwingConstants.CENTER);
		lblNewLabel.setFont(new Font("Tahoma", Font.BOLD, 12));

		radioButton = new JRadioButton("Bounding Box Area");
		radioButton.setBorder(UIManager.getBorder("RadioButtonMenuItem.border"));
		radioButton.setBackground(new Color(57, 83, 109));
		radioButton.setToolTipText(
				"<html>\r\n\r\nEl enlace será válido si el servidor que contiene la página está localizado<br>\r\ndentro de las delimitaciones indicadas.<br><br>\r\n\r\n<ol>\r\n<li>Pulsar el botón <b>MAP</b> que abrirá un enlace.<b></li><br>\r\n<li>Seleccionar un recuadro que contega la delimitación deseada</li><br>\r\n<li>Copiar el valor indicado y pegarlo en el campo de arriba</li><br><br>\r\n</ol>\r\n 2.069549560546875,41.29947603002819,2.252197265625,41.463311976686235 <br><br>\r\nDelimitaría la zona de Barcelona. ");
		radioButton.setFont(new Font("Dialog", Font.BOLD, 12));
		radioButton.setForeground(new Color(240, 255, 255));

		radioButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
			}
		});
		radioButton.setHorizontalAlignment(SwingConstants.LEFT);

		textField_3 = new JTextField();
		textField_3.setBorder(new SoftBevelBorder(BevelBorder.LOWERED, null, null, null, null));
		textField_3.setColumns(10);

		JButton btnDisplayMap = new JButton("MAP\r\n");
		btnDisplayMap.setForeground(new Color(57, 83, 109));
		btnDisplayMap.addActionListener(new OpenUrlAction());
		btnDisplayMap.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
			}
		});
		btnDisplayMap.setBackground(UIManager.getColor("inactiveCaption"));
		btnDisplayMap.setFont(new Font("Dialog", Font.BOLD, 12));

		lblConditionsThatMust = new JLabel("CONDITIONS THAT MUST BE SATISFIED");
		lblConditionsThatMust.setForeground(new Color(153, 204, 255));
		lblConditionsThatMust.setHorizontalAlignment(SwingConstants.CENTER);
		lblConditionsThatMust.setFont(new Font("Tahoma", Font.BOLD, 12));

		comboBox_1 = new JComboBox();
		comboBox_1.setModel(new DefaultComboBoxModel(
				new String[] { "Link", "Data", "Equal priority", "Link over data", "Data over link" }));

		rdbtnPriority = new JRadioButton("Priority");
		rdbtnPriority.setBackground(new Color(57, 83, 109));
		rdbtnPriority.setToolTipText(
				"<html>\r\n\r\nIndicamos con que orden se procesarán los enlaces insertados en la cola<br><br>\r\n\r\n<ol>\r\n<li><u>Link</u> Los enlaces que cumplan con las condiciones de URL se <br>procesarán antes</li><br>\r\n<li><u>Data</u> Los enlaces encontrados en una página que cumpla<br> las condiciones de DATA se procesarán antes</li><br>\r\n<li><u>Link over Data</u> ,ambas condiciones tienen prioridad, pero los enlaces que<br> cumplan la condicion de URL se procesara antes </li><br>\r\n<li><u>Data over Link</u> ,ambas condiciones tienen prioridad, pero los enlaces encontrados<br>en una página que cumpla la condición DATA se procesarán antes.</li><br><br><br>\r\n\r\nSi la <u>opcion no se selecciona</u>, se procesarán los enlaces en orden de entrada.\r\n\r\n</html>");
		rdbtnPriority.setFont(new Font("Dialog", Font.BOLD, 12));
		rdbtnPriority.setForeground(new Color(240, 255, 255));

		JLabel lblStorage = new JLabel("RESULTS STORAGE");
		lblStorage.setForeground(new Color(153, 204, 255));
		lblStorage.setHorizontalAlignment(SwingConstants.CENTER);
		lblStorage.setFont(new Font("Tahoma", Font.BOLD, 12));

		lblData = new JLabel("DATA");
		lblData.setForeground(new Color(153, 204, 255));
		lblData.setFont(new Font("Tahoma", Font.BOLD, 12));
		lblData.setHorizontalAlignment(SwingConstants.CENTER);

		radioButton_1 = new JRadioButton("Language");
		radioButton_1.setBorder(UIManager.getBorder("RadioButton.border"));
		radioButton_1.setBackground(new Color(57, 83, 109));
		radioButton_1.setToolTipText(
				"<html>\r\n\r\nSe considerarán de interés las paginas cuyo contenido esté en el idoma<br>\r\nindicado.<br><br><br>\r\n\r\n<i> El algoritmo de detección de idioma no es ideal, posible error en los resultados</i>\r\n</hmtl>");
		radioButton_1.setFont(new Font("Dialog", Font.BOLD, 12));
		radioButton_1.setForeground(new Color(240, 255, 255));

		comboBox_2 = new JComboBox();
		comboBox_2.setModel(new DefaultComboBoxModel(new String[] { "abjasio", "afar", "afrikáans", "aimara", "akano",
				"albanés", "alemán", "amhárico", "árabe", "aragonés", "armenio", "asamés", "avar", "avéstico", "azerí",
				"bambara", "baskir", "bengalí", "bhoyapurí", "bielorruso", "birmano", "bislama", "bosnio", "bretón",
				"búlgaro", "cachemiro", "camboyano", "canarés", "catalán", "chamorro", "checheno", "checo", "chichewa",
				"chino", "chuan", "chuvasio", "cingalés", "coreano", "corso", "cree", "croata", "córnico", "danés",
				"dzongkha", "eslavo eclesiástico antiguo", "eslovaco", "esloveno", "español", "esperanto", "estonio",
				"euskera", "ewé", "feroés", "finés", "fiyiano", "francés", "frisón", "fula", "gallego", "galés",
				"gaélico escocés", "georgiano", "griego", "groenlandés", "guaraní", "guyaratí", "haitiano", "hausa",
				"hebreo", "herero", "hindi", "hiri motu", "húngaro", "ido", "igbo", "indonesio", "inglés",
				"interlingua", "inuktitut", "irlandés", "islandés", "italiano", "iñupiaq", "japonés", "javanés",
				"kanuri", "kazajo", "kikuyu", "kirguís", "kirundi", "komi", "kongo", "kuanyama", "kurdo", "lao",
				"latín", "letón", "limburgués", "lingala", "lituano", "luba-katanga", "luganda", "luxemburgués",
				"macedonio", "malayalam", "malayo", "maldivo", "malgache", "maltés", "manés", "maorí", "maratí",
				"marshalés", "mongol", "nauruano", "navajo", "ndebele del norte", "ndebele del sur", "ndonga",
				"neerlandés", "nepalí", "noruego", "noruego bokmål", "nynorsk", "occidental", "occitano", "ojibwa",
				"oriya", "oromo", "osético", "pali", "panyabí", "pastú", "persa", "polaco", "portugués", "quechua",
				"romanche", "ruandés", "rumano", "ruso", "sami septentrional", "samoano", "sango", "sardo", "serbio",
				"sesotho", "setsuana", "shona", "sindhi", "somalí", "suajili", "suazi", "sueco", "sundanés",
				"sánscrito", "tagalo", "tahitiano", "tailandés", "tamil", "tayiko", "tibetano", "tigriña", "tongano",
				"tsonga", "turco", "turcomano", "twi", "tártaro", "télugu", "ucraniano", "uigur", "urdu", "uzbeko",
				"valón", "venda", "vietnamita", "volapük", "wolof", "xhosa", "yi de Sichuán", "yoruba", "yídish",
				"zulú" }));
		comboBox_2.setSelectedIndex(47);

		jRadioButton6 = new JRadioButton();
		jRadioButton6.setBackground(new Color(57, 83, 109));
		jRadioButton6.setFont(new Font("Dialog", Font.BOLD, 12));
		jRadioButton6.setForeground(new Color(240, 255, 255));
		jRadioButton6.setHorizontalAlignment(SwingConstants.CENTER);
		jRadioButton6.setToolTipText(
				"<html>\r\n\r\nSi activamos esta opción antes de pulsar START, guardaremos los enlaces<br>\r\nTEMPORALES que se han obtenido durante esta sesión. ESTA OPCIÓN REALENTIZA EL PROCESO<br><br>\r\n\r\nSi antes de iniciar la proxima sesion de crawling, activamos el Resumable,<br>\r\nse continuaran procesando los enlaces que se obtuvieron en la sesión previa. <br>(unicamente si la opción tambien estuvo activa)<br><br>\r\n\r\nEn caso de no activar el Resumable antes del inicio de una sesion,<br>se borrará aquella información temporal.<br><br>\r\n\r\nEs una opcion útil pero no recomendada por la realentización del proceso<br>\r\n\r\n</html>");
		jRadioButton6.setText("Resumable");

		label_1 = new JLabel("?");
		label_1.setForeground(new Color(248, 248, 255));
		label_1.setFont(new Font("Tahoma", Font.BOLD | Font.ITALIC, 24));

		radioButton_2 = new JRadioButton("Not contains");
		radioButton_2.setSelected(true);
		radioButton_2.setBackground(new Color(57, 83, 109));
		radioButton_2.setToolTipText(
				"<html>\r\n\r\nIndicar los términos que queremos excluir de páginas que visitar.<br><br>\r\n\r\nNo Contains: <i>twitter</i><br><br>\r\n\r\nEvitaremos todos los enlaces que contengan Twitter en la URL. <br><br>\r\n\r\n<i> Si iniciamos un crawling con esta condición y la página seed<br>únicamente contiene enlaces con el término twitter, la ejecución finaliza.\r\n\r\n</html>");
		radioButton_2.setHorizontalAlignment(SwingConstants.RIGHT);
		radioButton_2.setForeground(new Color(240, 255, 255));
		radioButton_2.setFont(new Font("Dialog", Font.BOLD, 12));

		radioButton_3 = new JRadioButton("Contains");
		radioButton_3.setBackground(new Color(57, 83, 109));
		radioButton_3.setToolTipText(
				"<html>\r\n\r\nIndicar los términos que queremos aparezcan ena las páginas que visitaremos.<br><br>\r\n\r\nContains: <i>twitter</i><br><br>\r\n\r\nUnicamente cogeremos enlaces que contengan el termino Twitter.<br><br>\r\n\r\n<i> Si iniciamos un crawling con esta condición y la página seed<br>no tiene ningún enlace que nos lleve a alguna con el termino twitter, el programa finaliza.\r\n\r\n</html>");
		radioButton_3.setHorizontalAlignment(SwingConstants.RIGHT);
		radioButton_3.setForeground(new Color(240, 255, 255));
		radioButton_3.setFont(new Font("Dialog", Font.BOLD, 12));

		bPenalyze = new JRadioButton("Penalyze D.");
		bPenalyze.setBorder(new LineBorder(new Color(0, 0, 0)));
		bPenalyze.setBackground(new Color(57, 83, 109));
		bPenalyze.setToolTipText(
				"<html>\r\n\r\nPenaliza la profundidad del enlace.<br><br>\r\n\r\nÚnicamente válido si existe una condición de prioridad activada.\r\n\r\n</html>");
		bPenalyze.setForeground(new Color(240, 255, 255));
		bPenalyze.setFont(new Font("Tahoma", Font.BOLD, 11));
		
		rdbtnDb = new JRadioButton("DB");
		rdbtnDb.setHorizontalAlignment(SwingConstants.RIGHT);
		rdbtnDb.setFont(new Font("Dialog", Font.BOLD, 12));
		rdbtnDb.setBorder(new LineBorder(new Color(0, 0, 0)));
		rdbtnDb.setForeground(new Color(240, 255, 255));
		rdbtnDb.setBackground(new Color(57, 83, 109));
		
		txtRoot = new JTextField();
		txtRoot.setColumns(10);
		
		txtyy = new JTextField();
		txtyy.setColumns(10);
		GroupLayout gl_jLayeredPane2 = new GroupLayout(jLayeredPane2);
		gl_jLayeredPane2.setHorizontalGroup(
			gl_jLayeredPane2.createParallelGroup(Alignment.TRAILING)
				.addGroup(gl_jLayeredPane2.createSequentialGroup()
					.addGap(9)
					.addGroup(gl_jLayeredPane2.createParallelGroup(Alignment.LEADING)
						.addGroup(gl_jLayeredPane2.createParallelGroup(Alignment.TRAILING)
							.addGroup(gl_jLayeredPane2.createSequentialGroup()
								.addGroup(gl_jLayeredPane2.createParallelGroup(Alignment.LEADING)
									.addComponent(lblCrawlers, GroupLayout.PREFERRED_SIZE, 81, GroupLayout.PREFERRED_SIZE)
									.addComponent(lblSeed, GroupLayout.PREFERRED_SIZE, 81, GroupLayout.PREFERRED_SIZE))
								.addGap(4))
							.addComponent(radioButton_2, GroupLayout.PREFERRED_SIZE, 140, GroupLayout.PREFERRED_SIZE)
							.addComponent(rdbtnPriority))
						.addComponent(radioButton_3, GroupLayout.PREFERRED_SIZE, 140, GroupLayout.PREFERRED_SIZE))
					.addPreferredGap(ComponentPlacement.RELATED)
					.addGroup(gl_jLayeredPane2.createParallelGroup(Alignment.LEADING)
						.addGroup(gl_jLayeredPane2.createSequentialGroup()
							.addGroup(gl_jLayeredPane2.createParallelGroup(Alignment.LEADING)
								.addComponent(lblAnalyse, GroupLayout.DEFAULT_SIZE, 196, Short.MAX_VALUE)
								.addGroup(gl_jLayeredPane2.createParallelGroup(Alignment.TRAILING, false)
									.addComponent(lblCrawlerParameters, Alignment.LEADING, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
									.addComponent(jTextField1, Alignment.LEADING, GroupLayout.DEFAULT_SIZE, 183, Short.MAX_VALUE))
								.addGroup(gl_jLayeredPane2.createSequentialGroup()
									.addComponent(jTextField3, GroupLayout.PREFERRED_SIZE, 42, GroupLayout.PREFERRED_SIZE)
									.addPreferredGap(ComponentPlacement.RELATED)
									.addComponent(lblDepth)
									.addPreferredGap(ComponentPlacement.RELATED)
									.addComponent(jTextField9, GroupLayout.PREFERRED_SIZE, 36, GroupLayout.PREFERRED_SIZE)
									.addPreferredGap(ComponentPlacement.RELATED)
									.addComponent(lblTime)
									.addPreferredGap(ComponentPlacement.RELATED)
									.addComponent(jTextField5, GroupLayout.PREFERRED_SIZE, 39, GroupLayout.PREFERRED_SIZE))
								.addGroup(gl_jLayeredPane2.createSequentialGroup()
									.addGroup(gl_jLayeredPane2.createParallelGroup(Alignment.LEADING, false)
										.addComponent(jRadioButton10, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
										.addComponent(jRadioButton5, GroupLayout.DEFAULT_SIZE, 89, Short.MAX_VALUE))
									.addPreferredGap(ComponentPlacement.UNRELATED)
									.addGroup(gl_jLayeredPane2.createParallelGroup(Alignment.LEADING)
										.addGroup(gl_jLayeredPane2.createSequentialGroup()
											.addGap(10)
											.addComponent(jRadioButton4))
										.addComponent(rdbtnGeolocation, GroupLayout.DEFAULT_SIZE, 89, Short.MAX_VALUE)))
								.addGroup(gl_jLayeredPane2.createSequentialGroup()
									.addComponent(comboBox_1, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
									.addPreferredGap(ComponentPlacement.RELATED)
									.addComponent(bPenalyze, GroupLayout.DEFAULT_SIZE, 97, Short.MAX_VALUE))
								.addComponent(lblDoNotVisit, GroupLayout.DEFAULT_SIZE, 196, Short.MAX_VALUE)
								.addGroup(gl_jLayeredPane2.createParallelGroup(Alignment.TRAILING, false)
									.addComponent(linkContains, Alignment.LEADING)
									.addComponent(linkNoContains, Alignment.LEADING, GroupLayout.DEFAULT_SIZE, 181, Short.MAX_VALUE)))
							.addGap(51))
						.addGroup(gl_jLayeredPane2.createSequentialGroup()
							.addGroup(gl_jLayeredPane2.createParallelGroup(Alignment.TRAILING)
								.addGroup(Alignment.LEADING, gl_jLayeredPane2.createSequentialGroup()
									.addGroup(gl_jLayeredPane2.createParallelGroup(Alignment.TRAILING)
										.addComponent(jRadioButton11)
										.addComponent(rdbtnDb, GroupLayout.PREFERRED_SIZE, 57, GroupLayout.PREFERRED_SIZE))
									.addPreferredGap(ComponentPlacement.RELATED)
									.addGroup(gl_jLayeredPane2.createParallelGroup(Alignment.LEADING)
										.addComponent(txtRoot, 120, 120, 120)
										.addComponent(txtyy, GroupLayout.PREFERRED_SIZE, 120, GroupLayout.PREFERRED_SIZE)
										.addComponent(jTextField8, GroupLayout.PREFERRED_SIZE, 120, GroupLayout.PREFERRED_SIZE)))
								.addComponent(lblStorage, GroupLayout.PREFERRED_SIZE, 187, GroupLayout.PREFERRED_SIZE))
							.addGap(60)))
					.addGroup(gl_jLayeredPane2.createParallelGroup(Alignment.TRAILING)
						.addComponent(lblConditionsThatMust, GroupLayout.PREFERRED_SIZE, 341, GroupLayout.PREFERRED_SIZE)
						.addGroup(gl_jLayeredPane2.createParallelGroup(Alignment.TRAILING, false)
							.addGroup(gl_jLayeredPane2.createSequentialGroup()
								.addComponent(radioButton_1, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
								.addPreferredGap(ComponentPlacement.RELATED)
								.addComponent(comboBox_2, GroupLayout.PREFERRED_SIZE, 185, GroupLayout.PREFERRED_SIZE))
							.addGroup(gl_jLayeredPane2.createSequentialGroup()
								.addComponent(radioButton, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
								.addPreferredGap(ComponentPlacement.UNRELATED)
								.addComponent(btnDisplayMap, GroupLayout.PREFERRED_SIZE, 126, GroupLayout.PREFERRED_SIZE))
							.addComponent(textField_3)
							.addGroup(gl_jLayeredPane2.createParallelGroup(Alignment.LEADING)
								.addComponent(jRadioButton6)
								.addComponent(jToggleButton1, GroupLayout.PREFERRED_SIZE, 97, GroupLayout.PREFERRED_SIZE)))
						.addGroup(gl_jLayeredPane2.createSequentialGroup()
							.addGroup(gl_jLayeredPane2.createParallelGroup(Alignment.TRAILING)
								.addComponent(jRadioButton7, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
								.addComponent(jLabel8, GroupLayout.PREFERRED_SIZE, 61, GroupLayout.PREFERRED_SIZE)
								.addComponent(jRadioButton1, Alignment.LEADING, GroupLayout.DEFAULT_SIZE, 97, Short.MAX_VALUE)
								.addComponent(jRadioButton2, GroupLayout.PREFERRED_SIZE, 85, GroupLayout.PREFERRED_SIZE))
							.addPreferredGap(ComponentPlacement.RELATED)
							.addGroup(gl_jLayeredPane2.createParallelGroup(Alignment.LEADING)
								.addComponent(lblNewLabel, Alignment.TRAILING, GroupLayout.DEFAULT_SIZE, 250, Short.MAX_VALUE)
								.addGroup(gl_jLayeredPane2.createSequentialGroup()
									.addComponent(jRadioButton8, GroupLayout.PREFERRED_SIZE, 113, GroupLayout.PREFERRED_SIZE)
									.addPreferredGap(ComponentPlacement.RELATED)
									.addComponent(jRadioButton3, GroupLayout.PREFERRED_SIZE, 66, GroupLayout.PREFERRED_SIZE)
									.addPreferredGap(ComponentPlacement.RELATED, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
									.addComponent(jRadioButton9, GroupLayout.PREFERRED_SIZE, 71, GroupLayout.PREFERRED_SIZE))
								.addComponent(jTextField2, Alignment.TRAILING, GroupLayout.DEFAULT_SIZE, 250, Short.MAX_VALUE)
								.addComponent(label, Alignment.TRAILING, GroupLayout.DEFAULT_SIZE, 250, Short.MAX_VALUE)
								.addComponent(lblData, Alignment.TRAILING, GroupLayout.DEFAULT_SIZE, 250, Short.MAX_VALUE)
								.addComponent(jTextField6, GroupLayout.DEFAULT_SIZE, 250, Short.MAX_VALUE)
								.addComponent(jTextField4, Alignment.TRAILING, GroupLayout.DEFAULT_SIZE, 250, Short.MAX_VALUE)
								.addComponent(jTextField7, GroupLayout.DEFAULT_SIZE, 250, Short.MAX_VALUE))))
					.addPreferredGap(ComponentPlacement.RELATED)
					.addComponent(label_1, GroupLayout.PREFERRED_SIZE, 32, GroupLayout.PREFERRED_SIZE)
					.addGap(12))
		);
		gl_jLayeredPane2.setVerticalGroup(
			gl_jLayeredPane2.createParallelGroup(Alignment.TRAILING)
				.addGroup(gl_jLayeredPane2.createSequentialGroup()
					.addGroup(gl_jLayeredPane2.createParallelGroup(Alignment.LEADING)
						.addGroup(gl_jLayeredPane2.createSequentialGroup()
							.addGap(35)
							.addGroup(gl_jLayeredPane2.createParallelGroup(Alignment.BASELINE)
								.addComponent(lblCrawlerParameters)
								.addComponent(lblConditionsThatMust))
							.addGap(18)
							.addGroup(gl_jLayeredPane2.createParallelGroup(Alignment.LEADING)
								.addGroup(gl_jLayeredPane2.createParallelGroup(Alignment.BASELINE)
									.addComponent(lblSeed)
									.addComponent(jTextField1, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
								.addGroup(gl_jLayeredPane2.createSequentialGroup()
									.addGap(11)
									.addComponent(lblData, GroupLayout.PREFERRED_SIZE, 25, GroupLayout.PREFERRED_SIZE)
									.addPreferredGap(ComponentPlacement.RELATED)
									.addGroup(gl_jLayeredPane2.createParallelGroup(Alignment.BASELINE)
										.addComponent(lblCrawlers)
										.addComponent(jTextField3, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
										.addComponent(lblDepth)
										.addComponent(jTextField9, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
										.addComponent(lblTime)
										.addComponent(jTextField5, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
										.addComponent(jTextField2, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
										.addComponent(jLabel8, GroupLayout.PREFERRED_SIZE, 18, GroupLayout.PREFERRED_SIZE))))
							.addGroup(gl_jLayeredPane2.createParallelGroup(Alignment.LEADING)
								.addGroup(gl_jLayeredPane2.createSequentialGroup()
									.addPreferredGap(ComponentPlacement.RELATED)
									.addGroup(gl_jLayeredPane2.createParallelGroup(Alignment.BASELINE)
										.addComponent(jRadioButton8)
										.addComponent(jRadioButton3)
										.addComponent(jRadioButton9))
									.addGap(18)
									.addComponent(label))
								.addGroup(gl_jLayeredPane2.createSequentialGroup()
									.addGap(43)
									.addComponent(lblDoNotVisit)))
							.addPreferredGap(ComponentPlacement.RELATED)
							.addGroup(gl_jLayeredPane2.createParallelGroup(Alignment.LEADING)
								.addGroup(gl_jLayeredPane2.createSequentialGroup()
									.addGroup(gl_jLayeredPane2.createParallelGroup(Alignment.BASELINE)
										.addComponent(linkNoContains, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
										.addComponent(radioButton_2, GroupLayout.PREFERRED_SIZE, 25, GroupLayout.PREFERRED_SIZE))
									.addPreferredGap(ComponentPlacement.RELATED)
									.addGroup(gl_jLayeredPane2.createParallelGroup(Alignment.BASELINE)
										.addComponent(linkContains, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
										.addComponent(radioButton_3, GroupLayout.PREFERRED_SIZE, 25, GroupLayout.PREFERRED_SIZE))
									.addPreferredGap(ComponentPlacement.RELATED)
									.addGroup(gl_jLayeredPane2.createParallelGroup(Alignment.LEADING)
										.addComponent(rdbtnPriority)
										.addGroup(gl_jLayeredPane2.createParallelGroup(Alignment.BASELINE)
											.addComponent(comboBox_1, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
											.addComponent(bPenalyze)))
									.addGap(16)
									.addGroup(gl_jLayeredPane2.createParallelGroup(Alignment.BASELINE)
										.addComponent(lblAnalyse)
										.addComponent(lblNewLabel)))
								.addGroup(gl_jLayeredPane2.createSequentialGroup()
									.addGroup(gl_jLayeredPane2.createParallelGroup(Alignment.BASELINE)
										.addComponent(jRadioButton7)
										.addComponent(jTextField6, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
									.addPreferredGap(ComponentPlacement.UNRELATED)
									.addGroup(gl_jLayeredPane2.createParallelGroup(Alignment.BASELINE)
										.addComponent(jRadioButton1)
										.addComponent(jTextField4, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
									.addPreferredGap(ComponentPlacement.UNRELATED)
									.addGroup(gl_jLayeredPane2.createParallelGroup(Alignment.BASELINE)
										.addComponent(jRadioButton2)
										.addComponent(jTextField7, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))))
							.addGap(1)
							.addGroup(gl_jLayeredPane2.createParallelGroup(Alignment.LEADING)
								.addGroup(gl_jLayeredPane2.createSequentialGroup()
									.addGroup(gl_jLayeredPane2.createParallelGroup(Alignment.BASELINE)
										.addComponent(jRadioButton10)
										.addComponent(rdbtnGeolocation))
									.addPreferredGap(ComponentPlacement.RELATED)
									.addGroup(gl_jLayeredPane2.createParallelGroup(Alignment.BASELINE)
										.addComponent(jRadioButton5)
										.addComponent(jRadioButton4, GroupLayout.PREFERRED_SIZE, 23, GroupLayout.PREFERRED_SIZE))
									.addPreferredGap(ComponentPlacement.UNRELATED)
									.addComponent(lblStorage)
									.addGap(14)
									.addGroup(gl_jLayeredPane2.createParallelGroup(Alignment.BASELINE)
										.addComponent(jRadioButton11, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
										.addComponent(jTextField8, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
									.addGap(2))
								.addGroup(gl_jLayeredPane2.createSequentialGroup()
									.addGap(10)
									.addComponent(textField_3, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
									.addGap(4)
									.addGroup(gl_jLayeredPane2.createParallelGroup(Alignment.BASELINE)
										.addComponent(btnDisplayMap)
										.addComponent(radioButton))
									.addPreferredGap(ComponentPlacement.UNRELATED)
									.addGroup(gl_jLayeredPane2.createParallelGroup(Alignment.BASELINE)
										.addComponent(comboBox_2, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
										.addComponent(radioButton_1))))
							.addGap(2)
							.addGroup(gl_jLayeredPane2.createParallelGroup(Alignment.BASELINE)
								.addComponent(txtRoot, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
								.addComponent(rdbtnDb))
							.addPreferredGap(ComponentPlacement.RELATED)
							.addGroup(gl_jLayeredPane2.createParallelGroup(Alignment.LEADING)
								.addComponent(txtyy, GroupLayout.PREFERRED_SIZE, 20, GroupLayout.PREFERRED_SIZE)
								.addComponent(jToggleButton1, GroupLayout.PREFERRED_SIZE, 29, GroupLayout.PREFERRED_SIZE)))
						.addGroup(gl_jLayeredPane2.createSequentialGroup()
							.addContainerGap()
							.addComponent(label_1)))
					.addPreferredGap(ComponentPlacement.RELATED)
					.addComponent(jRadioButton6)
					.addGap(12))
		);
		jLayeredPane2.setLayout(gl_jLayeredPane2);
		jPanel1.setLayout(jPanel1Layout);

		jTabbedPane1.addTab("CRAWLING", jPanel1);

		jLayeredPane5.setLayer(jScrollPane2, javax.swing.JLayeredPane.DEFAULT_LAYER);
		jLayeredPane5.setLayer(jTextField10, javax.swing.JLayeredPane.DEFAULT_LAYER);

		javax.swing.GroupLayout jLayeredPane5Layout = new javax.swing.GroupLayout(jLayeredPane5);
		jLayeredPane5.setLayout(jLayeredPane5Layout);
		jLayeredPane5Layout
				.setHorizontalGroup(jLayeredPane5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
						.addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 536, Short.MAX_VALUE)
						.addComponent(jTextField10, javax.swing.GroupLayout.Alignment.LEADING));
		jLayeredPane5Layout
				.setVerticalGroup(jLayeredPane5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
						.addGroup(jLayeredPane5Layout.createSequentialGroup()
								.addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 264, Short.MAX_VALUE)
								.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
								.addComponent(jTextField10, javax.swing.GroupLayout.PREFERRED_SIZE,
										javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)));

		jTabbedPane1.addTab("CONSOLE", jLayeredPane5);

		javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
		layout.setHorizontalGroup(
			layout.createParallelGroup(Alignment.LEADING)
				.addComponent(jTabbedPane1, GroupLayout.PREFERRED_SIZE, 811, Short.MAX_VALUE)
		);
		layout.setVerticalGroup(
			layout.createParallelGroup(Alignment.LEADING)
				.addComponent(jTabbedPane1, GroupLayout.DEFAULT_SIZE, 554, Short.MAX_VALUE)
		);
		getContentPane().setLayout(layout);
		pack();
	}// </editor-fold>//GEN-END:initComponents

	private void jTextField10ActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_jTextField10ActionPerformed
		// TODO add your handling code here:
	}// GEN-LAST:event_jTextField10ActionPerformed

	private void jTextArea2MouseClicked(java.awt.event.MouseEvent evt) {// GEN-FIRST:event_jTextArea2MouseClicked
		// TODO add your handling code here:
	}// GEN-LAST:event_jTextArea2MouseClicked

	private void jRadioButton9ActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_jRadioButton9ActionPerformed
		// TODO add your handling code here:
		jRadioButton8.setSelected(false);
		jRadioButton3.setSelected(false);
	}// GEN-LAST:event_jRadioButton9ActionPerformed

	private void jRadioButton8ActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_jRadioButton8ActionPerformed
		// TODO add your handling code here:
		jRadioButton9.setSelected(false);
		jRadioButton3.setSelected(false);
	}// GEN-LAST:event_jRadioButton8ActionPerformed

	private void jRadioButton3ActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_jRadioButton3ActionPerformed
		// TODO add your handling code here:
		jRadioButton9.setSelected(false);
		jRadioButton8.setSelected(false);
	}// GEN-LAST:event_jRadioButton3ActionPerformed

	private void jTextField2ActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_jTextField2ActionPerformed
		// TODO add your handling code here:
	}// GEN-LAST:event_jTextField2ActionPerformed

	private void jToggleButton1ActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_jToggleButton1ActionPerformed
		// TODO add your handling code here:
		if (jToggleButton1.getText() == "START") {
			jTabbedPane1.setSelectedIndex(1);
			if (validateParams()) {
				if(this.dbStore)
				{
					try {
						String[] values = {this.dbUsername,this.dbpassword};
						Connection con = Database.main(values);
						this.con = con;
						insertSesion();
						setCurrentSesionValue();
						updateFields();
					} catch (Exception e) {
						// TODO Auto-generated catch block
						menu.writeConsole("The Username or Password introduced for DB is not correct.\n");
						jTabbedPane1.setSelectedIndex(1);
						e.printStackTrace();
					}
				}
			    Timer timer = new Timer();
			    timer.schedule(new lastAction(menu), 0, 30000);
			    
				Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {
					@Override
					public void run() {
						controller.shutdown();
						if(menu.dbStore) updateEndTime();
						if (menu.isStore) {
							try {
								Thread.sleep(10000);
								MyExcel excel = new MyExcel();
								CreateMaps cm = new CreateMaps();
								excel.importToExcel(menu.store + "_success.xlsx", data);
								if (menu.isBroken) {
									MyExcel excelErrors = new MyExcel();
									excelErrors.importToExcel(menu.store + "_error.xlsx", dataBroken);
								}
								if(menu.fetchGeolocation) {
									cm.createHeatMap(menu.heatMap, menu.store);
									cm.createCircleMap(menu.circleMap, menu.store);
								}
							}catch (InterruptedException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
						}
					}
				}, "Shutdown-thread"));
				jToggleButton1.setText("CLOSE");
				CrawlConfig config = new CrawlConfig();

				// Fetch parameters from Main Menu
				// this.getAllParamsFromMenu();
				// Set parameters to the Crawler Config
				int numberOfCrawlers = this.crawlers;
				config.setMaxDepthOfCrawling(this.profundidad);
				config.setResumableCrawling(this.isResumable);
				config.setPolitenessDelay(this.tiempo);
				config.setCrawlStorageFolder("src");
				menu.consoleParams();
				// System.out.println(numberOfCrawlers);
				PageFetcher pageFetcher = new PageFetcher(config);
				RobotstxtConfig robotstxtConfig = new RobotstxtConfig();
				RobotstxtServer robotstxtServer = new RobotstxtServer(robotstxtConfig, pageFetcher);
				CrawlController controller = null;
				try {
					controller = new CrawlController(config, pageFetcher, robotstxtServer, menu);
					this.controller = controller;
				} catch (Exception ex) {
					Logger.getLogger(mainMenu.class.getName()).log(Level.SEVERE, null, ex);
				}

				addSeeds(this.semilla, controller);
				controller.startNonBlocking(MyCrawler.class, numberOfCrawlers);
			}
		} else {
			System.exit(0);
		}
	}

	private void jTextField5ActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_jTextField5ActionPerformed
		// TODO add your handling code here:
	}// GEN-LAST:event_jTextField5ActionPerformed

	private void jRadioButton10ActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_jRadioButton10ActionPerformed
		// TODO add your handling code here:
	}// GEN-LAST:event_jRadioButton10ActionPerformed

	private void jRadioButton11ActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_jRadioButton11ActionPerformed
		// TODO add your handling code here:
	}// GEN-LAST:event_jRadioButton11ActionPerformed

	// SET PARAMSF
	public void setSemilla(String n) {
		this.semilla = n.split("\\s+");
	}

	public void setCrawlers(int n) {
		this.crawlers = n;
	}

	public void setProfundidad(int n) {
		this.profundidad = n;
	}

	public void setTiempo(int n) {
		this.tiempo = n;
	}

	public void setIsResumable(boolean n) {
		this.isResumable = n;
	}

	public void setIsContiene(boolean n) {
		this.isContiene = n;
	}

	public void setIsNoContiene(boolean n) {
		this.isNoContiene = n;
	}

	public void setIsRegex(boolean n) {
		this.isRegex = n;
	}

	public void setContiene(String n) {
		this.contiene = n.split("\\s+");
	}

	public void setNoContiene(String n) {
		this.noContiene = n.split("\\s+");
	}

	public void setLinkIsContainsValue(String n) {
		this.linkContainsValue = n.split("\\s+");
	}

	public void setLinkIsNoContainsValue(String n) {
		this.linkNoContainsValue = n.split("\\s+");
	}

	public void setRegex(String n) {
		this.regex = n;
	}

	public void setIsIdioma(boolean n) {
		this.isIdioma = n;
	}

	public void setIsEmails(boolean n) {
		this.isEmails = n;
	}

	public void setStore(String n) {
		this.store = n;
	}

	public void setIsPriority(boolean n) {
		this.isPriority = n;
	}

	public void setPriority(String n) {
		this.priority = n;
	}

	public void setUsername(String n) {
		this.dbUsername = n;
	}
	
	public void setPassword(String n) {
		this.dbpassword = n;
	}
	
	public void setIsAtLeast(boolean n) {
		this.isAtLeast = n;
	}

	public void setIsAll(boolean n) {
		this.isAll = n;
	}

	public void setIsNone(boolean n) {
		this.isNone = n;
	}

	public void setContains(String n) {
		this.contains = n.split("\\s+");
	}

	public void setIsBroken(boolean n) {
		this.isBroken = n;
	}

	public void setIsStore(boolean n) {
		this.isStore = n;
	}

	public void setLinkIsContains(boolean n) {
		this.linkIsContains = n;
	}

	public void setLinkIsNoContains(boolean n) {
		this.linkIsNoContains = n;
	}

	public void setIsPenalyze(boolean n) {
		this.isPenalyze = n;
	}

	public void setGeoIsLanguage(boolean n) {
		this.isGeoLanguage = n;
	}

	public void setGeoIsBoundingBox(boolean n) {
		this.isGeoBoundingBox = n;
	}

	public void setIsFetchGeolocation(boolean n) {
		this.fetchGeolocation = n;
	}
	
	public void setStoreDb(boolean n) {
		this.dbStore = n;
	}
	
	public void setGeoLanguante(String n) {
		this.geoLanguage = n;
	}

	public void setGeoBoundingBox(float lngSW, float latSW, float lngNE, float latNE) {
		float[] bb = {lngSW,latSW,lngNE,latNE};
		this.geoBoundingBox = bb;
	}

	// GET CRAWLING PARAMETERS FROM MENU
	public String getSemilla() {
		return this.jTextField1.getText().toLowerCase();
	}

	public String getStore() {
		return this.jTextField8.getText();
	}

	public String getCrawlers() {
		return this.jTextField3.getText();
	}
	
	public String getdbUsername() {
		return this.txtRoot.getText();
	}
	
	public String getdbPassword() {
		return this.txtyy.getText();
	}
	
	public String getProfundidad() {
		return this.jTextField9.getText();
	}

	public String getTiempo() {
		return this.jTextField5.getText();
	}

	public String getContiene() {
		return this.jTextField4.getText().toLowerCase();
	}

	public String getNoContiene() {
		return this.jTextField6.getText().toLowerCase();
	}

	public String getRegEx() {
		return this.jTextField7.getText();
	}

	public String getContains() {
		return this.jTextField2.getText().toLowerCase();
	}

	public String getLinkContains() {
		return this.linkContains.getText().toLowerCase();
	}

	public String getLinkNoContains() {
		return this.linkNoContains.getText().toLowerCase();
	}

	public String getPriority() {
		return this.comboBox_1.getSelectedItem().toString();
	}

	public String getBoundingBoxArea() {
		return this.textField_3.getText();
	}

	public String getLanguage() {
		return this.comboBox_2.getSelectedItem().toString();
	}

	public Boolean getIsBroken() {
		return this.jRadioButton10.isSelected();
	}

	public Boolean getIsResumable() {
		return this.jRadioButton6.isSelected();
	}

	public Boolean getIsContiene() {
		return this.jRadioButton1.isSelected();
	}

	public Boolean getIsNoContiene() {
		return this.jRadioButton7.isSelected();
	}

	public Boolean getIsRegEx() {
		return this.jRadioButton2.isSelected();
	}

	public Boolean getIsIdioma() {
		return this.jRadioButton4.isSelected();
	}

	public Boolean getIsEmails() {
		return this.jRadioButton5.isSelected();
	}

	public Boolean getIsAtLeast() {
		return this.jRadioButton8.isSelected();
	}

	public Boolean getIsAll() {
		return this.jRadioButton9.isSelected();
	}

	public Boolean getIsNone() {
		return this.jRadioButton3.isSelected();
	}

	public Boolean getIsLinkContains() {
		return this.radioButton_3.isSelected();
	}

	public Boolean getIsLinkNoContains() {
		return this.radioButton_2.isSelected();
	}

	public Boolean getIsStore() {
		return this.jRadioButton11.isSelected();
	}

	public Boolean getIsPriority() {
		return rdbtnPriority.isSelected();
	}

	public Boolean getIsPenalyze() {
		return this.bPenalyze.isSelected();
	}

	public Boolean getGeoIsLanguage() {
		return this.radioButton_1.isSelected();
	}

	public Boolean getGeoIsBoundingBox() {
		return this.radioButton.isSelected();
	}
	
	public Boolean getIsFetchGeolocation() {
		return this.rdbtnGeolocation.isSelected();
	}

	public Boolean getIsDbStore() {
		return this.rdbtnDb.isSelected();
	}
	
	public String getViewUsername() {
		return this.txtRoot_1.getText();
	}
	
	public String getViewPassword() {
		return this.txtyy_1.getText();
	}
	
	public void writeConsole(String s) {
		jTextArea2.append(s);

	}

	// COMPROBAR VALIDEZ DE PARAMETROS
	public boolean validateParams() {
		boolean urlActivada = false;
		boolean dataActivada = false;
		float lngSW = 0;
		float latSW = 0;
		float lngNE = 0;
		float latNE = 0;
		jTextArea2.setText("\n\t*********\n\t* ERROR *\n\t*********\n\n");
		if (getSemilla().replaceAll(" ", "").length() == 0) {
			jTabbedPane1.setSelectedIndex(1);
			jTextArea2.append("CRAWLER PARAMETERES -> SEED | EL CAMPO ESTA VACIO");
			return false;
		} else
			setSemilla(getSemilla());

		if (getIsStore() && getStore().length() > 0) {
			if (!filenameRegex.matcher(getStore()).matches()) {
				jTabbedPane1.setSelectedIndex(1);
				jTextArea2.append("STORAGE OPTIONS | INTRODUCE UN NOMBRE DE FICHERO V�LIDO");
				return false;
			} else
				setStore(getStore());
		} else if (!getIsStore() && getStore().length() > 0) {
			jTabbedPane1.setSelectedIndex(1);
			jTextArea2.append(
					"STORAGE OPTIONS | EL CAMPO EST� COMPLETO\nPERO LA OPCION NO EST� SELECCIONADA.\nBORRA EL CAMPO O SELECCIONA LA OPCION");
			return false;
		}

		else if (getIsStore() && getStore().length() == 0) {
			jTabbedPane1.setSelectedIndex(1);
			jTextArea2.append(
					"STORAGE OPTIONS | EL CAMPO EST� VACIO\nPERO LA OPCION EST� SELECCIONADA.\nRELLENA EL CAMPO O DESELECCIONA LA OPCI�N.");
			return false;
		}

		try {
			if (Integer.parseInt(this.getCrawlers()) >= 1)
				setCrawlers(Integer.parseInt(this.getCrawlers()));
			else {
				jTabbedPane1.setSelectedIndex(1);
				jTextArea2.append("CRAWLER PARAMETERES -> CRAWLERS | DEBE SER >= 1.");
				return false;
			}
		} catch (NumberFormatException e) {
			// donothing
			jTabbedPane1.setSelectedIndex(1);
			jTextArea2.append("CRAWLER PARAMETERES -> CRAWLERS | DEBE SER UN NUMERO");
			return false;
		}

		try {
			if (Integer.parseInt(this.getProfundidad()) >= -1)
				this.profundidad = Integer.parseInt(this.getProfundidad());
			else {
				jTabbedPane1.setSelectedIndex(1);
				jTextArea2.append("CRAWLER PARAMETERES -> DEPTH | DEBE SER >= -1");
				return false;
			}
		} catch (NumberFormatException e) {
			jTabbedPane1.setSelectedIndex(1);
			jTextArea2.append("CRAWLER PARAMETERES -> DEPTH | DEBE SER UN NUMERO");
			return false;
		}

		try {
			if (Integer.parseInt(this.getTiempo()) >= 50)
				this.tiempo = Integer.parseInt(this.getTiempo());
			else {
				jTabbedPane1.setSelectedIndex(1);
				jTextArea2.append("CRAWLER PARAMETERES -> TIME | DEBE SER >= 50");
				return false;
			}
		} catch (NumberFormatException e) {
			jTabbedPane1.setSelectedIndex(1);
			jTextArea2.append("CRAWLER PARAMETERES -> TIME | DEBE SER UN NUMERO");
			return false;
		}

		// LINK REQUIREMETS
		// CONTAINS
		if (getIsContiene() && getContiene().replaceAll(" ", "").length() == 0) {
			jTabbedPane1.setSelectedIndex(1);
			jTextArea2.append(
					"LINK REQUIREMENTS aaa-> CONTAINS \nOPCION SELECCIONADA PERO EL CAMPO EST� VACIO\nDESELECCIONA LA OPCION O RELLENA EL CAMPO");
			return false;
		} else if (!getIsContiene() && getContiene().replaceAll(" ", "").length() > 0) {
			jTabbedPane1.setSelectedIndex(1);
			jTextArea2.append(
					"LINK REQUIREMENTS -> CONTAINS \nCAMPO RELLENO PERO LA OPCI�N NO EST� SELECCIONADA\nSELECCIONA LA OPCION O BORRA EL CAMPO");
			return false;
		} else if (getIsContiene() && getContiene().replaceAll(" ", "").length() > 0) {
			setContiene(getContiene());
			setIsContiene(true);
			urlActivada = true;
		}

		// REGEX
		if (getIsRegEx() && getRegEx().replaceAll(" ", "").length() == 0) {
			jTabbedPane1.setSelectedIndex(1);
			jTextArea2.append(
					"LINK REQUIREMENTS -> REGEX \nOPCION SELECCIONADA PERO EL CAMPO EST� VACIO\nDESELECCIONA LA OPCION O RELLENA EL CAMPO");
			return false;
		} else if (!getIsRegEx() && getRegEx().replaceAll(" ", "").length() > 0) {
			jTabbedPane1.setSelectedIndex(1);
			jTextArea2.append(
					"LINK REQUIREMENTS -> REGEX \nCAMPO RELLENO PERO LA OPCI�N NO EST� SELECCIONADA\nSELECCIONA LA OPCION O BORRA EL CAMPO");
			return false;
		} else if (getIsRegEx() && getRegEx().replaceAll(" ", "").length() > 0) {
			setRegex(getRegEx());
			setIsRegex(true);
			urlActivada = true;
		}

		// AVOID
		if (getIsNoContiene() && getNoContiene().replaceAll(" ", "").length() == 0) {
			jTabbedPane1.setSelectedIndex(1);
			jTextArea2.append(
					"LINK REQUIREMENTS -> AVOID \nOPCION SELECCIONADA PERO EL CAMPO EST� VACIO\nDESELECCIONA LA OPCION O RELLENA EL CAMPO");
			return false;
		} else if (!getIsNoContiene() && getNoContiene().replaceAll(" ", "").length() > 0) {
			jTabbedPane1.setSelectedIndex(1);
			jTextArea2.append(
					"LINK REQUIREMENTS -> AVOID \nCAMPO RELLENO PERO LA OPCI�N NO EST� SELECCIONADA\nSELECCIONA LA OPCION O BORRA EL CAMPO");
			return false;
		} else if (getIsNoContiene() && getNoContiene().replaceAll(" ", "").length() > 0) {
			setNoContiene(getNoContiene());
			setIsNoContiene(true);
			urlActivada = true;
		}

		if ((getIsAll() || getIsNone() || getIsAtLeast()) && getContains().replaceAll(" ", "").length() == 0) {
			jTabbedPane1.setSelectedIndex(1);
			jTextArea2.append(
					"CONTENT REQUIREMENTS -> CONTAINS \nOPCION SELECCIONADA PERO EL CAMPO EST� VACIO\nDESELECCIONA UNA OPCION O RELLENA EL CAMPO");
			return false;
		} else if (!(getIsAll() || getIsNone() || getIsAtLeast()) && getContains().replaceAll(" ", "").length() > 0) {
			jTabbedPane1.setSelectedIndex(1);
			jTextArea2.append(
					"CONTENT REQUIREMENTS -> CONTAINS \nCAMPO RELLENO PERO NIGUNA OPCIÓN EST� SELECCIONADA\nSELECCIONA UNA OPCION O BORRA EL CAMPO");
			return false;
		}

		else if ((getIsAll() || getIsNone() || getIsAtLeast()) && getContains().replaceAll(" ", "").length() > 0) {
			setContains(getContains());
			dataActivada = true;
		}

		// VISIT LINK
		if (getIsLinkContains() && getLinkContains().replaceAll(" ", "").length() == 0) {
			jTabbedPane1.setSelectedIndex(1);
			jTextArea2.append(
					"VISIT LINK -> CONTAINS \nOPCION SELECCIONADA PERO EL CAMPO ESTÁ VACIO\nDESELECCIONA LA OPCION O RELLENA EL CAMPO");
			return false;
		} else if (!getIsLinkContains() && getLinkContains().replaceAll(" ", "").length() > 0) {
			jTabbedPane1.setSelectedIndex(1);
			jTextArea2.append(
					"VISIT LINK -> CONTAINS \nCAMPO RELLENO PERO LA OPCIÓN NO ESTÁ SELECCIONADA\nSELECCIONA LA OPCION O BORRA EL CAMPO");
			return false;
		} else if (getIsLinkContains() && getLinkContains().replaceAll(" ", "").length() > 0) {
			setLinkIsContainsValue(getLinkContains());
			setLinkIsContains(true);
		}

		// NO VISIT LINK
		if (getIsLinkNoContains() && getLinkNoContains().replaceAll(" ", "").length() == 0) {
			jTabbedPane1.setSelectedIndex(1);
			jTextArea2.append(
					"VISIT LINK -> NO CONTAINS \nOPCION SELECCIONADA PERO EL CAMPO ESTÁ VACIO\nDESELECCIONA LA OPCION O RELLENA EL CAMPO");
			return false;
		} else if (!getIsLinkNoContains() && getLinkNoContains().replaceAll(" ", "").length() > 0) {
			jTabbedPane1.setSelectedIndex(1);
			jTextArea2.append(
					"VISIT LINK -> NO CONTAINS \nCAMPO RELLENO PERO LA OPCIÓN NO ESTÁ SELECCIONADA\nSELECCIONA LA OPCION O BORRA EL CAMPO");
			return false;
		} else if (getIsLinkNoContains() && getLinkNoContains().replaceAll(" ", "").length() > 0) {
			setLinkIsNoContainsValue(getLinkNoContains());
			setLinkIsNoContains(true);
		}

		// PRIORITY

		if (this.getIsPriority()) {
			if ((this.getPriority().equals("Link") || this.getPriority().equals("Link over data")
					|| this.getPriority().equals("Data over link") || this.getPriority().equals("Equal priority"))
					&& !urlActivada) {
				jTabbedPane1.setSelectedIndex(1);
				jTextArea2.append(
						"PRIORITY -> HAS INDICADO UNA OPCIONA QUE PONDERA LA PRIORIDAD EN ENLACE \n PERO NO TIENES NINGUNA CONDICION DE URL INDICADA");
				return false;
			} else if ((this.getPriority().equals("Data") || this.getPriority().equals("Link over data")
					|| this.getPriority().equals("Data over link") || this.getPriority().equals("Equal priority"))
					&& !dataActivada) {
				jTabbedPane1.setSelectedIndex(1);
				jTextArea2.append(
						"PRIORITY -> HAS INDICADO UNA OPCIONA QUE PONDERA LA PRIORIDAD EN DATA \n PERO NO TIENES NINGUNA CONDICION DE DATA INDICADA");
				return false;
			}

			else if ((this.getPriority().equals("Link over data") || this.getPriority().equals("Data over link")
					|| this.getPriority().equals("Equal priority")) && !dataActivada && !urlActivada) {
				jTabbedPane1.setSelectedIndex(1);
				jTextArea2.append(
						"PRIORITY -> HAS INDICADO UNA OPCIONA QUE PONDERA LA PRIORIDAD EN DATA y LINK \n PERO NO TIENES NINGUNA CONDICION DE DATA y URL INDICADAS");
				return false;
			}

			else if ((this.getPriority().equals("Link over data") || this.getPriority().equals("Data over link")
					|| this.getPriority().equals("Equal priority")) && !dataActivada && urlActivada) {
				jTabbedPane1.setSelectedIndex(1);
				jTextArea2.append(
						"PRIORITY -> HAS INDICADO UNA OPCIONA QUE PONDERA LA PRIORIDAD EN DATA y LINK \n PERO NO TIENES NINGUNA CONDICION DE DATA INDICADA");
				return false;
			}

			else if ((this.getPriority().equals("Link over data") || this.getPriority().equals("Data over link")
					|| this.getPriority().equals("Equal priority")) && dataActivada && !urlActivada) {
				jTabbedPane1.setSelectedIndex(1);
				jTextArea2.append(
						"PRIORITY -> HAS INDICADO UNA OPCIONA QUE PONDERA LA PRIORIDAD EN DATA y LINK \n PERO NO TIENES NINGUNA CONDICION DE URL INDICADA");
				return false;
			} else {
				this.isPriority = true;
				this.setPriority(this.getPriority());
				if (this.getPriority().equals("Link over data")) {
					this.linkPriority = -2;
					this.dataPriority = -1;
				} else if (this.getPriority().equals("Data over link")) {
					this.dataPriority = -2;
					this.linkPriority = -1;
				} else if (this.getPriority().equals("Equal priority")) {
					this.dataPriority = -1;
					this.linkPriority = -1;
				} else if (this.getPriority().equals("Data")) {
					this.dataPriority = -1;
					this.linkPriority = 0;
				} else {
					this.dataPriority = 0;
					this.linkPriority = -1;
				}
			}
		}
		
		
		if(getGeoIsBoundingBox() && getBoundingBoxArea().length() > 0)
		{
			String[] s = getBoundingBoxArea().replaceAll("\\s+"," ").split(",");
			try {
				lngSW = (float)(Float.parseFloat(s[0]));
				latSW = (float)(Float.parseFloat(s[1]));
				lngNE = (float)(Float.parseFloat(s[2]));
				latNE = (float)(Float.parseFloat(s[3]));
				if((lngSW > lngNE) || (latSW > latNE))
				{
					jTabbedPane1.setSelectedIndex(1);
					jTextArea2.append("GEOLOCATION -> BOUDING BOX AREA \n\nLos valores no cumples las condiciones de Bounding Box.");
					return false;	
				}
				else {
					setGeoBoundingBox((float)lngSW,(float)latSW,(float)lngNE,(float)latNE);
					setGeoIsBoundingBox(true);
				}
			} catch (NumberFormatException e) {
				// donothing
				jTabbedPane1.setSelectedIndex(1);
				jTextArea2.append("GEOLOCATION -> BOUDING BOX AREA \n\nEl formato introducido no es el correcto.");
				return false;
			}	
		}
		
		else if(getGeoIsBoundingBox() && getBoundingBoxArea().length() > 0 && getBoundingBoxArea().replaceAll("\\s+"," ").split(",").length != 4) {
			jTabbedPane1.setSelectedIndex(1);
			jTextArea2.append(
					"GEOLOCATION -> BOUNDING BOX AREA \n\nEl formato introducido, no es correcto. Copia y pega los valores obtenidos en el enlace.");
			return false;
		}
		
		else if(getGeoIsBoundingBox() && getBoundingBoxArea().replaceAll("\\s+"," ").isEmpty()) {
			jTabbedPane1.setSelectedIndex(1);
			jTextArea2.append(
					"GEOLOCATION -> BOUNDING BOX AREA \n\nLa opción está seleccionada pero el campo está vacio.");
			return false;
		}
		
		else if(!getGeoIsBoundingBox() && !getBoundingBoxArea().replaceAll("\\s+"," ").isEmpty()) {
			jTabbedPane1.setSelectedIndex(1);
			jTextArea2.append(
					"GEOLOCATION -> BOUNDING BOX AREA \n\nEl campo está completo pero la opción no está seleccionada.");
			return false;
		}
		
		
		if(!getIsDbStore() && !getdbUsername().replaceAll("\\s+"," ").isEmpty()) {
			jTabbedPane1.setSelectedIndex(1);
			jTextArea2.append(
					"RESULTS STORAGE -> DB \n\nEl campo está completo pero la opción no está seleccionada.");
			return false;
		}
		
		else if(getIsDbStore() && getdbUsername().replaceAll("\\s+"," ").isEmpty()) {
			jTabbedPane1.setSelectedIndex(1);
			jTextArea2.append(
					"RESULTS STORAGE -> DB \n\nLa opción está seleccionada pero el campo está vacio.");
			return false;
		}
		
		else if(getIsDbStore() && !getdbUsername().replaceAll("\\s+", " ").isEmpty())
		{
			setPassword(getdbPassword());
			setUsername(getdbUsername());
			setStoreDb(getIsDbStore());
		}
		
		
		
		if (getGeoIsLanguage()) {
			this.setGeoIsLanguage(true);
			this.setGeoLanguante(getLanguage());
		}

		if (getIsPenalyze() && !getIsPriority()) {
			jTabbedPane1.setSelectedIndex(1);
			jTextArea2.append(
					"PRIORITY -> HAS INDICADO QUE QUIERES PENALIZAR LA PROFUNDIDAD, PERO NO HAY NINGUNA CONDICION DE PRIORIDAD INDICADA");
			return false;
		}

		setIsFetchGeolocation(getIsFetchGeolocation());
		setIsPenalyze(getIsPenalyze());
		setIsBroken(getIsBroken());
		setIsAll(getIsAll());
		setIsAtLeast(getIsAtLeast());
		setIsNone(getIsNone());
		setIsResumable(getIsResumable());
		setIsIdioma(getIsIdioma());
		setIsEmails(getIsEmails());
		setIsStore(getIsStore());
		setLinkIsNoContains(getIsLinkNoContains());
		setLinkIsContains(getIsLinkContains());
		return true;
	}

	public void addSeeds(String[] s, CrawlController controller) {
		MyAlgorithms myAlg = new MyAlgorithms();
		for (int i = 0; i < s.length; ++i) {
			controller.addSeed(s[i]);
			if (!myAlg.isValidURL(s[i]))
				System.out.println("La URL " + s[i] + " PODR�A SER INCORRECTA.");
		}
	}

	public void getAllParamsFromMenu() {
		setSemilla(getSemilla());
		// validateParams(this.getCrawlers(),this.getProfundidad(),this.getTiempo());
		if (getIsContiene()) {
			setContiene(getContiene());
			setIsContiene(true);
		}
		if (getIsNoContiene()) {
			setNoContiene(getNoContiene());
			setIsNoContiene(true);
		}
		if (getIsRegEx()) {
			setRegex(getRegEx());
			setIsRegex(true);
		}

		if (getIsAll()) {
			setContains(getContains());
			setIsAll(true);
		} else if (getIsAtLeast()) {
			setContains(getContains());
			setIsAtLeast(true);
		} else if (getIsNone()) {
			setContains(getContains());
			setIsNone(true);
		}

		setIsResumable(getIsResumable());
		setIsIdioma(getIsIdioma());
		setIsEmails(getIsEmails());
	}

	public void fillViewSesions()
	{
		try {
			String sesions = "SELECT id_sesion from crawler_sesion;";
			Statement stmt = (Statement) menu.viewCon.createStatement();
	        ResultSet rs = stmt.executeQuery(sesions);
	        List<String> ls = new ArrayList<String>();
	        ls.add("ALL");
	        while (rs.next()) {
	            ls.add(rs.getInt("id_sesion")+"");
	        }
	        comboBox.setModel(new DefaultComboBoxModel(ls.toArray()));
			comboBox.setEnabled(true);
			btnNewButton.setEnabled(true);
		}catch (SQLException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
	}
	
	
	public void VisibleViewButtons()
	{
		Statement stmt = null;
		ResultSet rs = null;
		String query = "";
		if(menu.loadedSesion.equals("ALL")){
			try {				

				//EMAILS
				query = "SELECT count(extract_emails) from crawler_sesion where extract_emails = 1;";
				stmt = (Statement) menu.viewCon.createStatement();
		        rs = stmt.executeQuery(query);
		        while (rs.next()) {
		        	if(rs.getInt("count(extract_emails)") > 0) btnEmails.setEnabled(true);
		        	else btnEmails.setEnabled(false);
		        }
		        //LANGUAGE
				query = "SELECT count(extract_language) from crawler_sesion where extract_language = 1;";
				stmt = (Statement) menu.viewCon.createStatement();
		        rs = stmt.executeQuery(query);
		        while (rs.next()) {
		        	if(rs.getInt("count(extract_language)") > 0) btnLanguage.setEnabled(true);
		        	else btnLanguage.setEnabled(false);
		        }
		        
		        //GEOLOCATION
				query = "SELECT count(extract_geolocation) from crawler_sesion where extract_geolocation = 1;";
				stmt = (Statement) menu.viewCon.createStatement();
		        rs = stmt.executeQuery(query);
		        while (rs.next()) {
		        	if(rs.getInt("count(extract_geolocation)") > 0) btnNewButton_2.setEnabled(true);
		        	else btnNewButton_2.setEnabled(false);
		        }
		        
		        //BROKEN URL
				query = "SELECT count(extract_broken) from crawler_sesion where extract_broken = 1;";
				stmt = (Statement) menu.viewCon.createStatement();
		        rs = stmt.executeQuery(query);
		        while (rs.next()) {
		        	if(rs.getInt("count(extract_broken)") > 0) btnBrokenUrl.setEnabled(true);
		        	else btnBrokenUrl.setEnabled(false);
		        }
		        
			}catch (SQLException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}
		else{
			try {				
				//EMAILS	
				query = "SELECT extract_emails from crawler_sesion where id_sesion = " + menu.loadedSesion + ";"; 
				stmt = (Statement) menu.viewCon.createStatement();
		        rs = stmt.executeQuery(query);
		        while (rs.next()) {
		        	if(rs.getBoolean("extract_emails")) btnEmails.setEnabled(true);
		        	else btnEmails.setEnabled(false);
		        }
		        
		        //LANGUAGE	
				query = "SELECT extract_language from crawler_sesion where id_sesion = " + menu.loadedSesion + ";"; 
				stmt = (Statement) menu.viewCon.createStatement();
		        rs = stmt.executeQuery(query);
		        while (rs.next()) {
		        	if(rs.getBoolean("extract_language")) btnLanguage.setEnabled(true);
		        	else btnLanguage.setEnabled(false);
		        }
		        
		        //GEOLOCATION	
				query = "SELECT extract_geolocation from crawler_sesion where id_sesion = " + menu.loadedSesion + ";"; 
				stmt = (Statement) menu.viewCon.createStatement();
		        rs = stmt.executeQuery(query);
		        while (rs.next()) {
		        	if(rs.getBoolean("extract_geolocation")) btnNewButton_2.setEnabled(true);
		        	else btnNewButton_2.setEnabled(false);
		        }
		        
		        //BROKEN URL
				query = "SELECT extract_broken from crawler_sesion where id_sesion = " + menu.loadedSesion + ";"; 
				stmt = (Statement) menu.viewCon.createStatement();
		        rs = stmt.executeQuery(query);
		        while (rs.next()) {
		        	if(rs.getBoolean("extract_broken")) btnBrokenUrl.setEnabled(true);
		        	else btnBrokenUrl.setEnabled(false);
		        }
		        
			}catch (SQLException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}
	}
	
	
	/**
	 * @param args
	 *            the command line arguments
	 */
	public static void main(String args[]) {
		/* Set the Nimbus look and feel */
		// <editor-fold defaultstate="collapsed" desc=" Look and feel setting
		// code (optional) ">
		/*
		 * If Nimbus (introduced in Java SE 6) is not available, stay with the
		 * default look and feel. For details see
		 * http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.
		 * html
		 */
		try {
			for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
				if ("Nimbus".equals(info.getName())) {
					javax.swing.UIManager.setLookAndFeel(info.getClassName());
					break;
				}
			}
		} catch (ClassNotFoundException ex) {
			java.util.logging.Logger.getLogger(mainMenu.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
		} catch (InstantiationException ex) {
			java.util.logging.Logger.getLogger(mainMenu.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
		} catch (IllegalAccessException ex) {
			java.util.logging.Logger.getLogger(mainMenu.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
		} catch (javax.swing.UnsupportedLookAndFeelException ex) {
			java.util.logging.Logger.getLogger(mainMenu.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
		}
		// </editor-fold>

		/* Create and display the form */
		java.awt.EventQueue.invokeLater(new Runnable() {
			@Override
			public void run() {
				try {
					try {
						new mainMenu().setVisible(true);
					} catch (SQLException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				} catch (URISyntaxException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		});
	}

	public void consoleParams() {
		String url = "";
		String contiene = "";
		String noContiene = "";
		String regEx = "";
		String seeders = "";
		String ejec = "";
		String contains = "No hay restriccion a nivel de contenido del enlace\n";
		String excel = "";
		if (this.isEmails)
			ejec += "Emails\n";
		if (this.isIdioma)
			ejec += "Idioma\n";
		if (this.fetchGeolocation)
			ejec += "Geolocalización\n";
		if (this.isBroken)
			ejec += "Enlaces caídos\n";
		if (ejec.isEmpty())
			ejec = "No se analizara nada en tiempo de ejecucion";
		if (this.isStore)
			excel = "Se creará el fichero " + this.store + "_success.xlsx al finalizar la ejecución\n";
		if (this.isBroken)
			excel += "Se creará el fichero " + this.store + "_error.xlsx al finalizar la ejecución\n";
		if (this.getIsFetchGeolocation())
			excel += "Se creará un mapa de calor y un mapa de frecuencia al finalizar la ejecución\n";
		if (this.dbStore)
			excel += "Se almacenarán los resultados en la base de datos durante la ejecución\n";
		if (!this.isContiene && !this.isRegex && !this.isNoContiene)
			url = "Se analizaran todos los enlaces";
		if (this.semilla.length > 0) {
			seeders = "\n\t-Se utilizara como semilla-\n\n";
			for (int i = 0; i < this.semilla.length; ++i) {
				seeders += this.semilla[i] + "\n";
			}
		}

		if (this.isContiene && this.contiene.length > 0) {
			contiene = "\n\t-Contiene alguno de estos terminos-\n\n";
			for (int i = 0; i < this.contiene.length; ++i) {
				contiene += this.contiene[i] + "\n";
			}
		}

		if (this.isNoContiene && this.noContiene.length > 0) {
			noContiene = "\n\t-No contiene ninguno de estos terminos-\n\n";
			for (int i = 0; i < this.noContiene.length; ++i) {
				noContiene += this.noContiene[i] + "\n";
			}
		}

		if (this.isRegex && !this.regex.isEmpty()) {
			regEx = "\n\t-Cumple la expresion regular-\n\n";
			regEx += this.regex + "\n";
		}

		if (this.isAll && this.contains.length > 0) {
			contains = "\n\t-Contiene todos los terminos siguientes-\n\n";
			for (int i = 0; i < this.contains.length; ++i) {
				contains += this.contains[i] + "\n";
			}
		}

		else if (this.isNone && this.contains.length > 0) {
			contains = "\n\t-No contiene los terminos siguientes-\n\n";
			for (int i = 0; i < this.contains.length; ++i) {
				contains += this.contains[i] + "\n";
			}
		}

		if (this.isAtLeast && this.contains.length > 0) {
			contains = "\n\t-Contiene almenos uno de los terminos siguientes-\n\n";
			for (int i = 0; i < this.contains.length; ++i) {
				contains += this.contains[i] + "\n";
			}
		}

		if ((url + contiene + noContiene + regEx).isEmpty())
			url = "No hay restricción a nivel de enlace.\n";
		jTextArea2.setText("");
		writeConsole("Iniciando el Crawling...");
		String s = "\n\n\n   **************************\n   * CONDICIONES DEL ENLACE *\n   **************************\n\n\n"
				+ url + contiene + noContiene + regEx
				+ "\n\n\n   *****************************\n   * CONDICIONES DEL CONTENIDO *\n   *****************************\n\n\n"
				+ contains
				+ "\n\n\n   *************************\n   * ANALIZAR EN EJECUCION *\n   *************************\n\n"
				+ ejec
				+ "\n\n\n   ******************\n   * ALMACENAMIENTO *\n   ******************\n\n"
				+ excel + "\n\n" + "\n\n\n\t\t------------INICIO----------\n\n\n";
		writeConsole(s);
	}

	class OpenUrlAction implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent e) {
			open(uri);
		}
	}
	
	private static void open(URI uri) {
		if (Desktop.isDesktopSupported()) {
			try {
				Desktop.getDesktop().browse(uri);
			} catch (IOException e) {
				/* TODO: error handling */ }
		} else {
			/* TODO: error handling */ }
	}

	public void setTextStats(String s) {
		jTextField10.setText(s);
	}
	
	
	
	//DATABASE FUNCTION
	public void insertSesion()
	{
		try {
			Statement stmt = (Statement) con.createStatement();
			String new_sesion = "INSERT INTO crawler_sesion(numero_crawlers,profundidad,tiempo,visita_no_contiene,visita_contiene,tiene_prioridad,tiene_penalizacion,data_at_least_one,data_none,data_all,url_no_contiene,url_si_contiene,url_regex,resumable,geo_bounding_box_area,geo_language,storage_excel,fecha_inicio,extract_emails,extract_geolocation,extract_broken,extract_language)"
					+ "VALUES("+this.crawlers+","+this.profundidad+","+this.tiempo+","+this.linkIsContains+","+this.linkIsNoContains+","+this.isPriority+","+this.isPenalyze+","+this.isAtLeast+","+this.isNone+","+this.isAll+","+this.isNoContiene+","+this.isContiene+","+this.isRegex+","+this.isResumable+","+this.isGeoBoundingBox+","+this.isGeoLanguage+","+this.isStore+",'"+new java.sql.Timestamp(System.currentTimeMillis())+"',"+this.isEmails+","+this.fetchGeolocation+","+this.isBroken+","+this.isIdioma+");";		
			stmt.executeUpdate(new_sesion);
		} catch (SQLException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
	}
	
	public void setCurrentSesionValue()
	{
		try {
			String last_sesion = "SELECT id_sesion from crawler_sesion ORDER BY id_sesion DESC LIMIT 1;";
			PreparedStatement stmt = (PreparedStatement) con.prepareStatement(last_sesion);
			ResultSet resultSet = stmt.executeQuery();
			resultSet.next();
			this.current_sesion = resultSet.getInt("id_sesion");
		} catch (SQLException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
	}
	
	public void updateEndTime()
	{
		try {
			Statement stmt = (Statement) con.createStatement();
			String update_time = "UPDATE crawler_sesion\r\n" + "SET crawler_sesion.fecha_final ='"+new java.sql.Timestamp(System.currentTimeMillis())+"' where id_sesion = (SELECT id_sesion from (SELECT * FROM crawler_sesion) AS temp ORDER BY id_sesion DESC LIMIT 1);";
			stmt.executeUpdate(update_time);
		} catch (SQLException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
	}
	
	public void populate(int _case){
		   // The Connection is obtained
		Statement stmt;
		ResultSet rs;
		try {
			switch(_case) {
			//TODAS LAS URLS
		    case 1:
				stmt = (Statement) menu.viewCon.createStatement();
				rs = stmt.executeQuery("select url,url_padre from url_correcta;");
				table.setModel(buildTableModel(rs));
				table.getColumnModel().getColumn(0).setPreferredWidth(393);
				table.getColumnModel().getColumn(1).setPreferredWidth(393);
		        break;
		    //URLS DE UNA DETERMINADA SESION
		    case 2:
				stmt = (Statement) menu.viewCon.createStatement();
				rs = stmt.executeQuery("select url,url_padre from url_correcta where sesion_id = " + menu.loadedSesion +";");
				table.setModel(buildTableModel(rs));
				table.getColumnModel().getColumn(0).setPreferredWidth(393);
				table.getColumnModel().getColumn(1).setPreferredWidth(393);
		        break;
		    case 3:
				stmt = (Statement) menu.viewCon.createStatement();
				rs = stmt.executeQuery("SELECT url, email FROM email INNER JOIN url_correcta ON email.url_id = url_correcta.id_url_correcta;");
				table.setModel(buildTableModel(rs));
				table.getColumnModel().getColumn(0).setPreferredWidth(500);
				table.getColumnModel().getColumn(1).setPreferredWidth(286);
				break;
		    case 4:
				stmt = (Statement) menu.viewCon.createStatement();
				rs = stmt.executeQuery("SELECT url, email FROM email INNER JOIN url_correcta ON email.url_id = url_correcta.id_url_correcta where url_correcta.sesion_id =" + menu.loadedSesion + ";");
				table.setModel(buildTableModel(rs));
				table.getColumnModel().getColumn(0).setPreferredWidth(500);
				table.getColumnModel().getColumn(1).setPreferredWidth(286);
				break;
		    case 5:
				stmt = (Statement) menu.viewCon.createStatement();
				rs = stmt.executeQuery("SELECT url, idioma FROM url_correcta where idioma != \"null\";");
				table.setModel(buildTableModel(rs));
				table.getColumnModel().getColumn(0).setPreferredWidth(650);
				table.getColumnModel().getColumn(1).setPreferredWidth(136);
				break;
		    case 6:
				stmt = (Statement) menu.viewCon.createStatement();
				rs = stmt.executeQuery("SELECT url, idioma FROM url_correcta where sesion_id = " + menu.loadedSesion + ";");
				table.setModel(buildTableModel(rs));
				table.getColumnModel().getColumn(0).setPreferredWidth(650);
				table.getColumnModel().getColumn(1).setPreferredWidth(136);
				break;
		    case 7:
				stmt = (Statement) menu.viewCon.createStatement();
				rs = stmt.executeQuery("SELECT url, pais, ciudad, codigo_postal, latitud, longitud FROM url_correcta where pais != \"null\";");
				table.setModel(buildTableModel(rs));
				table.getColumnModel().getColumn(0).setPreferredWidth(401);
				table.getColumnModel().getColumn(1).setPreferredWidth(77);
				table.getColumnModel().getColumn(2).setPreferredWidth(77);
				table.getColumnModel().getColumn(3).setPreferredWidth(77);
				table.getColumnModel().getColumn(4).setPreferredWidth(77);
				table.getColumnModel().getColumn(5).setPreferredWidth(77);
				break;
		    case 8:
				stmt = (Statement) menu.viewCon.createStatement();
				rs = stmt.executeQuery("SELECT url, pais, ciudad, codigo_postal, latitud, longitud FROM url_correcta where sesion_id = " + menu.loadedSesion + ";");
				table.setModel(buildTableModel(rs));
				table.getColumnModel().getColumn(0).setPreferredWidth(401);
				table.getColumnModel().getColumn(1).setPreferredWidth(77);
				table.getColumnModel().getColumn(2).setPreferredWidth(77);
				table.getColumnModel().getColumn(3).setPreferredWidth(77);
				table.getColumnModel().getColumn(4).setPreferredWidth(77);
				table.getColumnModel().getColumn(5).setPreferredWidth(77);
				break;
		    case 9:
				stmt = (Statement) menu.viewCon.createStatement();
				rs = stmt.executeQuery("SELECT url_padre, url, error_code from url_erronea;");
				table.setModel(buildTableModel(rs));
				table.getColumnModel().getColumn(0).setPreferredWidth(350);
				table.getColumnModel().getColumn(1).setPreferredWidth(350);
				table.getColumnModel().getColumn(2).setPreferredWidth(86);
				break;
		    case 10:
				stmt = (Statement) menu.viewCon.createStatement();
				rs = stmt.executeQuery("SELECT url_padre, url, error_code from url_erronea where sesion_id = " + menu.loadedSesion + ";");
				table.setModel(buildTableModel(rs));
				table.getColumnModel().getColumn(0).setPreferredWidth(350);
				table.getColumnModel().getColumn(1).setPreferredWidth(350);
				table.getColumnModel().getColumn(2).setPreferredWidth(86);
				break;
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public static DefaultTableModel buildTableModel(ResultSet rs)
	        throws SQLException {

	    ResultSetMetaData metaData = rs.getMetaData();

	    // names of columns
	    Vector<String> columnNames = new Vector<String>();
	    int columnCount = metaData.getColumnCount();
	    for (int column = 1; column <= columnCount; column++) {
	        columnNames.add(metaData.getColumnName(column));
	    }

	    // data of the table
	    Vector<Vector<Object>> data = new Vector<Vector<Object>>();
	    while (rs.next()) {
	        Vector<Object> vector = new Vector<Object>();
	        for (int columnIndex = 1; columnIndex <= columnCount; columnIndex++) {
	            vector.add(rs.getObject(columnIndex));
	        }
	        data.add(vector);
	    }

	    return new DefaultTableModel(data, columnNames);

	}
	
	public void updateFields()
	{
		try {
			Statement stmt = (Statement) con.createStatement();
			
			//SEMILLA
			String s = "INSERT INTO campos(categoria,valor,id_sesion)VALUES";
			for(int i = 0; i < this.semilla.length; ++i){
				s += "('seed','"+this.semilla[i]+"'," + this.current_sesion+"),";
			}
			
			for(int i = 0; i < this.contains.length; ++i){
				s += "('data_contiene','"+this.contains[i]+"'," + this.current_sesion+"),";
			}
			
			for(int i = 0; i < this.contiene.length; ++i){
				s += "('url_contiene','"+this.contiene[i]+"'," + this.current_sesion+"),";
			}
			
			for(int i = 0; i < this.contiene.length; ++i){
				s += "('url_no_contiene','"+this.noContiene[i]+"'," + this.current_sesion+"),";
			}
			
			if(!this.regex.isEmpty()) s += "('url_regEx','"+this.regex+"'," + this.current_sesion+"),";
			
			for(int i = 0; i < this.linkContainsValue.length; ++i){
				s += "('visita_contiene','"+this.linkContainsValue[i]+"'," + this.current_sesion+"),";
			}
			
			for(int i = 0; i < this.linkNoContainsValue.length; ++i){
				s += "('visita_no_contiene','"+this.linkNoContainsValue[i]+"'," + this.current_sesion+"),";
			}
			
			if(this.isPriority) s += "('prioridad','"+this.priority+"'," + this.current_sesion+"),";
			if(this.isGeoBoundingBox) s += "('bounding_box','["+this.geoBoundingBox[0]+","+this.geoBoundingBox[1]+","+this.geoBoundingBox[2]+","+this.geoBoundingBox[3]+"]'," + this.current_sesion+"),";
			if(this.isGeoLanguage) s += "('geo_language','"+this.geoLanguage+"'," + this.current_sesion+"),";
			if(this.isStore) s += "('storage_filename','"+this.store+"'," + this.current_sesion+"),";
			s = s.substring(0, s.length()-1)+";";
			stmt.executeUpdate(s);
		
		
		} catch (SQLException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
	}

	private javax.swing.JLabel jLabel8;
	private javax.swing.JLayeredPane jLayeredPane2;
	private javax.swing.JLayeredPane jLayeredPane5;
	private javax.swing.JPanel jPanel1;
	private javax.swing.JPopupMenu jPopupMenu1;
	private javax.swing.JRadioButton jRadioButton1;
	private javax.swing.JRadioButton jRadioButton10;
	private javax.swing.JRadioButton jRadioButton11;
	private javax.swing.JRadioButton jRadioButton3;
	private javax.swing.JRadioButton jRadioButton4;
	private javax.swing.JRadioButton jRadioButton5;
	private javax.swing.JRadioButton jRadioButton8;
	private javax.swing.JRadioButton jRadioButton9;
	private javax.swing.JScrollPane jScrollPane2;
	private javax.swing.JTabbedPane jTabbedPane1;
	private javax.swing.JTextArea jTextArea2;
	private javax.swing.JTextField jTextField10;
	private javax.swing.JTextField jTextField2;
	private javax.swing.JTextField jTextField5;
	private javax.swing.JTextField jTextField8;
	private javax.swing.JToggleButton jToggleButton1;
	private JRadioButton rdbtnGeolocation;
	private JLabel lblSeed;
	private JTextField jTextField1;
	private JLabel lblCrawlers;
	private JLabel lblDepth;
	private JLabel lblTime;
	private JTextField jTextField3;
	private JTextField jTextField9;
	private JLabel lblDoNotVisit;
	private JLabel lblCrawlerParameters;
	private JTextField linkNoContains;
	private JTextField linkContains;
	private JLabel lblAnalyse;
	private JRadioButton jRadioButton7;
	private JRadioButton jRadioButton2;
	private JTextField jTextField7;
	private JTextField jTextField6;
	private JTextField jTextField4;
	private JLabel label;
	private JLabel lblNewLabel;
	private JRadioButton radioButton;
	private JTextField textField_3;
	private JLabel label_1;
	private JLabel lblConditionsThatMust;
	private JLabel lblData;
	private JRadioButton radioButton_1;
	private JComboBox comboBox_1;
	private JComboBox comboBox_2;
	private JComboBox comboBox;
	private JRadioButton jRadioButton6;
	private JRadioButton radioButton_2;
	private JRadioButton radioButton_3;
	private JRadioButton rdbtnPriority;
	private JRadioButton bPenalyze;
	private JRadioButton rdbtnDb;
	private JTextField txtRoot;
	private JTextField txtyy;
	private JTable table_1;
	private JScrollBar scrollBar;
	private JTable table_2;
	private JScrollPane scrollPane;
	private JTable table_3;
	private JTable table;
	private JTextField txtyy_1;
	private JTextField txtRoot_1;
	private JButton btnLanguage;
	private JButton btnBrokenUrl;
	private JButton btnNewButton;
	private JButton btnNewButton_2;
	private JButton btnEmails;
	private JLabel lblDbLoadedSuccesfuly;
	private JLabel label_2;
	private JButton btnUrl;
}
