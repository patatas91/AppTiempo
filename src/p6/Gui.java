package p6;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.Iterator;
import java.util.List;
import java.util.Scanner;
import java.util.Vector;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JEditorPane;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.input.SAXBuilder;


/**
 * 
 * @author Cristian Simon Moreno - NIP: 611487
 * 
 */
@SuppressWarnings({ "deprecation", "serial", "rawtypes", "unchecked" })
public class Gui extends JFrame {

	// Elementos Gui
	JPanel panel;
	JComboBox municipio;
	JEditorPane panelEditor;
	JEditorPane panelEditorJson;
	JButton botonXML;
	JButton botonHTML;
	JButton botonJSON;
	private JLabel label2;
	private JLabel label3;
	private JLabel label4;

	static String lugar;
	String ruta = "http://www.aemet.es/xml/municipios/localidad_";
	static int n;
	static List dias;
	static List estCielo;
	static List probPrec;
	static List cotaNieve;
	static List temp;
	static List vientDir;
	static List vientVel;
	static List uvMax;

	// List para datos JSON
	static List diasH;
	static List estCieloH;
	static List probPrecH;
	static List cotaNieveH;
	static List tempH;
	static List vientDirH;
	static List vientVelH;
	static List uvMaxH;
	static String lista;

	/**
	 * Metodo que mete en un JComboBox los datos de un fichero de texto
	 * 
	 * @param c
	 */
	private void llenarBox(JComboBox c) {
		File archivo = null;
		FileReader fr = null;
		BufferedReader br = null;

		try {
			// Apertura del fichero y creacion de BufferedReader para poder
			// hacer una lectura comoda (disponer del metodo readLine()).
			archivo = new File("provincias.txt");
			fr = new FileReader(archivo);
			br = new BufferedReader(fr);

			// Lectura del fichero
			String linea = null;
			Scanner s;
			while ((linea = br.readLine()) != null) {
				s = new Scanner(linea);
				// Sacamos el codigo y el nombre de la localidad
				String a = s.next();
				String b = s.next();
				String nombre = s.nextLine();
				linea = a + b + " " + nombre;
				municipio.addItem(linea);
			}
		
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			// En el finally cerramos el fichero, para asegurarnos
			// que se cierra tanto si todo va bien como si salta
			// una excepcion.
			try {
				if (null != fr) {
					fr.close();
				}
			} catch (Exception e2) {
				e2.printStackTrace();
			}
		}
	}

	/**
	 * Crea una ventana en la que dispondremos de un JComboBox en el que estaran
	 * todos los municipios españoles con sus codigo, al haber seleccionado un
	 * municipio podremos descargarnos el XML con su informacion meteorologica
	 * para posteriormente poder generar el HTML o el JSON con esa informacion.
	 * Si se pretende generar el HTML o el JSON sin haber descargado antes el
	 * XML dara error.
	 */
	public Gui() {
		super("Agent Directory Service Layer Monitor");
		// Look and feel
		try {
			UIManager
					.setLookAndFeel("com.sun.java.swing.plaf.nimbus.NimbusLookAndFeel");
		} catch (UnsupportedLookAndFeelException e) {
		} catch (ClassNotFoundException e) {
		} catch (InstantiationException e) {
		} catch (IllegalAccessException e) {
		}

		panel = new JPanel();
		municipio = new JComboBox();
		panelEditor = new JEditorPane();
		panelEditor.setEditable(false);	
		panelEditorJson = new JEditorPane();
		panelEditorJson.setEditable(false);
		botonXML = new JButton("XML ");
		botonHTML = new JButton("HTML");
		botonJSON = new JButton("JSON");
		label2 = new JLabel("Descargar el XML con la info meteorologica");
		label3 = new JLabel("Generar el HTML con la info meteorologica");
		label4 = new JLabel("Generar el JSON con la info meteorologica");

		llenarBox(municipio);
		// Elemento incial vacio
		municipio.setSelectedIndex(-1);

		// Los añadimos al JFrame
		this.add(panel);
		panel.add(municipio, BorderLayout.CENTER);
		panel.add(label2);
		panel.add(botonXML, BorderLayout.CENTER);
		panel.add(label3);
		panel.add(botonHTML, BorderLayout.CENTER);
		panel.add(label4);
		panel.add(botonJSON, BorderLayout.CENTER);

		// Accion a realizar cuando el JComboBox cambia de item seleccionado.
		municipio.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				lugar = (String) municipio.getSelectedItem();
				System.out.println(lugar);
			}
		});

		// Accion a realizar cuando el boton XML es pulsado
		botonXML.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (lugar == null) {
					JOptionPane.showMessageDialog(null,
							"No ha seleccionado municipio.", "Error",
							JOptionPane.ERROR_MESSAGE);
				} else {
					System.out.println("XML");
					Scanner sc = new Scanner(lugar);
					String codigo = sc.next();
					System.out.println(ruta + codigo + ".xml");
					if (descargarFich(ruta + codigo + ".xml")) {
						JOptionPane.showMessageDialog(null, "XML Descargado.",
								"Informacion", JOptionPane.INFORMATION_MESSAGE);
					} else {
						JOptionPane.showMessageDialog(null,
								"Error al descargar el fichero.", "Error",
								JOptionPane.ERROR_MESSAGE);
					}
					sc.close();
				}
			}
		});

		// Accion a realizar cuando el boton HTML es pulsado
		botonHTML.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (lugar == null) {
					JOptionPane.showMessageDialog(null,
							"No ha seleccionado municipio.", "Error",
							JOptionPane.ERROR_MESSAGE);
				} else {
					System.out.println("HTML");
					if (generarHTML("temp/prediccion.xml")) {
						JOptionPane.showMessageDialog(null, "HTML Generado.",
								"Informacion", JOptionPane.INFORMATION_MESSAGE);
						File paginaHTML = new File("temp/prediccion.html");
						String urlDocumento = "file://localhost/"+paginaHTML.getAbsolutePath();
						JFrame html = new JFrame();
						try {
							panelEditor.setPage(new URL(urlDocumento));
						} catch (MalformedURLException e1) {							
							e1.printStackTrace();
						} catch (IOException e1) {							
							e1.printStackTrace();
						}						
						html.add(new JScrollPane(panelEditor));						
						html.setSize(800,450);
						html.setTitle("Documento HTML");						
						html.setVisible(true);
						
					} else {
						JOptionPane.showMessageDialog(null,
								"Error al crear HTML.", "Error",
								JOptionPane.ERROR_MESSAGE);
					}

				}
			}
		});

		// Accion a realizar cuando el boton JSON es pulsado
		botonJSON.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (lugar == null) {
					JOptionPane.showMessageDialog(null,
							"No ha seleccionado municipio.", "Error",
							JOptionPane.ERROR_MESSAGE);
				} else {
					System.out.println("JSON");
					if (generarJSON("temp/prediccion.xml")) {
						JOptionPane.showMessageDialog(null, "JSON Generado.",
								"Informacion", JOptionPane.INFORMATION_MESSAGE);
						File paginaJSON = new File("temp/prediccion.json");
						String urlDocumentoJson = "file://localhost/"+paginaJSON.getAbsolutePath();
						JFrame json = new JFrame();
						try {
							panelEditorJson.setPage(new URL(urlDocumentoJson));
						} catch (MalformedURLException e1) {							
							e1.printStackTrace();
						} catch (IOException e1) {							
							e1.printStackTrace();
						}						
						json.add(new JScrollPane(panelEditorJson));						
						json.setSize(800,450);
						json.setTitle("Documento JSON");						
						json.setVisible(true);
						
					} else {
						JOptionPane.showMessageDialog(null,
								"Error al crear JSON.", "Error",
								JOptionPane.ERROR_MESSAGE);
					}
				}
			}
		});

	}

	/**
	 * Crea una carpeta temporal en la que alojara el fichero descargado de
	 * "ruta"
	 * 
	 * @param ruta
	 * @return boolean
	 */
	private static boolean descargarFich(String ruta) {
		try {
			// creamos una carpeta temporal
			File directorio = new File("temp");
			if (!directorio.exists()) {
				directorio.mkdir();
			}
			// url con el fichero
			URL url = new URL(ruta);

			// establezco la conexion
			URLConnection urlCon = url.openConnection();

			// Sacamos el inpmutStream del fichero y abrimos el fichero en el
			// que lo guardaremos
			InputStream is = urlCon.getInputStream();
			FileOutputStream fos = new FileOutputStream("temp/prediccion.xml");

			// Lectura del fichero y escritura en el nuevo
			byte[] array = new byte[1000];
			int leido = is.read(array);
			while (leido > 0) {
				fos.write(array, 0, leido);
				leido = is.read(array);
			}

			// cerramos conexiones
			is.close();
			fos.close();
			System.out
					.println("Fichero de prediccion meteorologica descargado con exito.");
			return true;
		} catch (Exception e) {
			System.out.println("Fallo al descargar el fichero.");
			return false;
		}

	}

	/**
	 * Dado un fichero XML genera un documento HTML con una tabla que contiene
	 * la informacion meteorologica
	 * 
	 * @param ficheroXML
	 */
	private static boolean generarHTML(String ficheroXML) {
		try {

			SAXBuilder constructor = new SAXBuilder(false);

			// quitar comprobacion dtd
			constructor
					.setFeature(
							"http://apache.org/xml/features/nonvalidating/load-external-dtd",
							false);

			Document doc = constructor.build(ficheroXML);

			Element raiz = doc.getRootElement();
			raiz = raiz.getChild("prediccion");
			// ya estamos en prediccion

			if (raiz.getName().equals("prediccion")) {

				FileOutputStream archivo;
				PrintStream p;
				// Se crea en la carpeta temporal
				archivo = new FileOutputStream("temp/prediccion.html");
				p = new PrintStream(archivo);
				p.println("<html>");
				p.println("<head><title>El tiempo</title></head>");
				Scanner sc = new Scanner(lugar);
				sc.next();
				String ciudad = sc.next();
				p.println("<h1>Prediccion meteorologica de " + ciudad + "</h1>");
				sc.close();
				p.println("<table border=\"2\" cellspacing=\"0\" cellpadding=\"0\">");
				p.println("<tr>");

				// FECHAS
				p.println("<td width=\"20%\" bgcolor=\"CCD2FF\"><strong>Fecha</strong></td>");
				dias = raiz.getChildren();
				Iterator itDias = dias.iterator();
				while (itDias.hasNext()) {
					Element dia = (Element) itDias.next();
					p.println("<th bgcolor=\"CCD2FF\" width=\"10%\" colspan=\"4\">"
							+ dia.getAttributeValue("fecha") + "</th>");
					dia = (Element) itDias.next();
					p.println("<th bgcolor=\"CCD2FF\"  width=\"10%\" colspan=\"4\">"
							+ dia.getAttributeValue("fecha") + "</th>");
					dia = (Element) itDias.next();
					p.println("<th bgcolor=\"CCD2FF\"  width=\"10%\" colspan=\"2\">"
							+ dia.getAttributeValue("fecha") + "</th>");
					dia = (Element) itDias.next();
					p.println("<th bgcolor=\"CCD2FF\"  width=\"10%\" colspan=\"2\">"
							+ dia.getAttributeValue("fecha") + "</th>");
					while (itDias.hasNext()) {
						dia = (Element) itDias.next();
						p.println("<th bgcolor=\"CCD2FF\"  width=\"10%\">"
								+ dia.getAttributeValue("fecha") + "</th>");
					}
				}

				p.println("</tr>");
				p.println("<tr>");
				p.println("<th bgcolor=\"CCD2FF\"></th>");
				p.println("<th>0-6</th>");
				p.println("<th>6-12</th>");
				p.println("<th>12-18</th>");
				p.println("<th>18-24</th>");
				p.println("<th>0-6</th>");
				p.println("<th>6-12</th>");
				p.println("<th>12-18</th>");
				p.println("<th>18-24</th>");
				p.println("<th>0-12</th>");
				p.println("<th>12-24</th>");
				p.println("<th>0-12</th>");
				p.println("<th>12-24</th>");
				p.println("</tr>");
				p.println("<tr>");

				// ESTADO CIELO
				p.println("<td bgcolor=\"CCD2FF\"><strong>Estado del cielo</strong></td>");
				dias = raiz.getChildren();
				itDias = dias.iterator();
				n = 0;
				while (itDias.hasNext()) {
					Element dia = (Element) itDias.next();
					estCielo = dia.getChildren("estado_cielo");
					Iterator itCiel = estCielo.iterator();
					// dias de 4 franjas horarias
					if (n < 2) {
						while (itCiel.hasNext()) {
							Element e = (Element) itCiel.next();
							// Seleccionamos los periodos correctos
							if (!e.getAttributeValue("periodo").equals("00-24")
									&& !e.getAttributeValue("periodo").equals(
											"00-12")
									&& !e.getAttributeValue("periodo").equals(
											"12-24")) {
								if (e.getAttributeValue("descripcion").equals(
										"Despejado")) {
									if (e.getAttributeValue("periodo").equals(
											"00-06")
											|| e.getAttributeValue("periodo")
													.equals("18-24")) {
										p.println("<td align=\"center\"><img src=\"gif/11n.gif\" alt=\"Despejado de noche\"/></td>");
									} else {
										p.println("<td align=\"center\"><img src=\"gif/11.gif\" alt=\"Despejado de dia\"/></td>");
									}
								} else if (e.getAttributeValue("descripcion")
										.equals("Intervalos nubosos")) {
									p.println("<td align=\"center\"><img src=\"gif/13.gif\" alt=\"Intervalos nubosos\"/></td>");
								} else if (e.getAttributeValue("descripcion")
										.equals("Nubes altas")) {
									p.println("<td align=\"center\"><img src=\"gif/13.gif\" alt=\"Nubes altas\"/></td>");
								} else if (e.getAttributeValue("descripcion")
										.equals("Cubierto con lluvia")) {
									p.println("<td align=\"center\"><img src=\"gif/23.gif\" alt=\"Cubierto con lluvia\"/></td>");
								} else if (e.getAttributeValue("descripcion")
										.equals("Cubierto con lluvia escasa")) {
									p.println("<td align=\"center\"><img src=\"gif/23.gif\" alt=\"Cubierto con lluvia escasa\"/></td>");
								} else if (e.getAttributeValue("descripcion")
										.equals("Cubierto")) {
									p.println("<td align=\"center\"><img src=\"gif/13.gif\" alt=\"Cubierto\"/></td>");
								} else if (e.getAttributeValue("descripcion")
										.equals("Nuboso")) {
									p.println("<td align=\"center\"><img src=\"gif/13.gif\" alt=\"Nuboso\"/></td>");
								} else if (e.getAttributeValue("descripcion")
										.equals("Muy nuboso")) {
									p.println("<td align=\"center\"><img src=\"gif/13.gif\" alt=\"Muy nuboso\"/></td>");
								} else if (e.getAttributeValue("descripcion")
										.equals("Nuboso con lluvia escasa")) {
									p.println("<td align=\"center\"><img src=\"gif/23.gif\" alt=\"Nuboso con lluvia escasa\"/></td>");
								} else if (e.getAttributeValue("descripcion")
										.equals("Nuboso con lluvia")) {
									p.println("<td align=\"center\"><img src=\"gif/23.gif\" alt=\"Nuboso con lluvia\"/></td>");
								} else if (e.getAttributeValue("descripcion")
										.equals("Muy nuboso con lluvia escasa")) {
									p.println("<td align=\"center\"><img src=\"gif/45.gif\" alt=\"Muy nuboso con lluvia escasa\"/></td>");
								} else if (e
										.getAttributeValue("descripcion")
										.equals("Intervalos nubosos con lluvia")) {
									p.println("<td align=\"center\"><img src=\"gif/45.gif\" alt=\"Intervalos nubosos con lluvia\"/></td>");
								} else if (e.getAttributeValue("descripcion")
										.equals("Muy nuboso con lluvia")) {
									p.println("<td align=\"center\"><img src=\"gif/45.gif\" alt=\"Muy nuboso con lluvia\"/></td>");
								} else if (e.getAttributeValue("descripcion")
										.equals("Poco nuboso")) {
									p.println("<td align=\"center\"><img src=\"gif/12.gif\" alt=\"Poco nuboso\"/></td>");
								} else {
									p.println("<td></td>");
								}

							}
						}
					}
					// dias de 2 franjas horarias
					if (n >= 2 && n < 4) {
						while (itCiel.hasNext()) {
							Element e = (Element) itCiel.next();
							// Seleccionamos los periodos correctos
							if (e.getAttributeValue("periodo").equals("00-12")
									|| e.getAttributeValue("periodo").equals(
											"12-24")) {
								if (e.getAttributeValue("descripcion").equals(
										"Despejado")) {
									p.println("<td align=\"center\"><img src=\"gif/11.gif\" alt=\"Despejado\"/></td>");
								} else if (e.getAttributeValue("descripcion")
										.equals("Intervalos nubosos")) {
									p.println("<td align=\"center\"><img src=\"gif/13.gif\" alt=\"Intervalos nubosos\"/></td>");
								} else if (e.getAttributeValue("descripcion")
										.equals("Nubes altas")) {
									p.println("<td align=\"center\"><img src=\"gif/13.gif\" alt=\"Nubes altas\"/></td>");
								} else if (e.getAttributeValue("descripcion")
										.equals("Cubierto con lluvia")) {
									p.println("<td align=\"center\"><img src=\"gif/23.gif\" alt=\"Cubierto con lluvia\"/></td>");
								} else if (e.getAttributeValue("descripcion")
										.equals("Cubierto con lluvia escasa")) {
									p.println("<td align=\"center\"><img src=\"gif/23.gif\" alt=\"Cubierto con lluvia escasa\"/></td>");
								} else if (e.getAttributeValue("descripcion")
										.equals("Cubierto")) {
									p.println("<td align=\"center\"><img src=\"gif/13.gif\" alt=\"Cubierto\"/></td>");
								} else if (e.getAttributeValue("descripcion")
										.equals("Nuboso")) {
									p.println("<td align=\"center\"><img src=\"gif/13.gif\" alt=\"Nuboso\"/></td>");
								} else if (e.getAttributeValue("descripcion")
										.equals("Muy nuboso")) {
									p.println("<td align=\"center\"><img src=\"gif/13.gif\" alt=\"Muy nuboso\"/></td>");
								} else if (e.getAttributeValue("descripcion")
										.equals("Nuboso con lluvia escasa")) {
									p.println("<td align=\"center\"><img src=\"gif/23.gif\" alt=\"Nuboso con lluvia escasa\"/></td>");
								} else if (e.getAttributeValue("descripcion")
										.equals("Nuboso con lluvia")) {
									p.println("<td align=\"center\"><img src=\"gif/23.gif\" alt=\"Nuboso con lluvia\"/></td>");
								} else if (e.getAttributeValue("descripcion")
										.equals("Muy nuboso con lluvia escasa")) {
									p.println("<td align=\"center\"><img src=\"gif/45.gif\" alt=\"Muy nuboso con lluvia escasa\"/></td>");
								} else if (e
										.getAttributeValue("descripcion")
										.equals("Intervalos nubosos con lluvia")) {
									p.println("<td align=\"center\"><img src=\"gif/45.gif\" alt=\"Intervalos nubosos con lluvia\"/></td>");
								} else if (e.getAttributeValue("descripcion")
										.equals("Muy nuboso con lluvia")) {
									p.println("<td align=\"center\"><img src=\"gif/45.gif\" alt=\"Muy nuboso con lluvia\"/></td>");
								} else if (e.getAttributeValue("descripcion")
										.equals("Poco nuboso")) {
									p.println("<td align=\"center\"><img src=\"gif/12.gif\" alt=\"Poco nuboso\"/></td>");
								} else {
									p.println("<td></td>");
								}

							}
						}
					} else {
						while (itCiel.hasNext()) {
							Element e = (Element) itCiel.next();
							if (e.getAttributeValue("descripcion").equals(
									"Despejado")) {
								p.println("<td align=\"center\"><img src=\"gif/11.gif\" alt=\"Despejado\"/></td>");
							} else if (e.getAttributeValue("descripcion")
									.equals("Intervalos nubosos")) {
								p.println("<td align=\"center\"><img src=\"gif/13.gif\" alt=\"Intervalos nubosos\"/></td>");
							} else if (e.getAttributeValue("descripcion")
									.equals("Nubes altas")) {
								p.println("<td align=\"center\"><img src=\"gif/13.gif\" alt=\"Nubes altas\"/></td>");
							} else if (e.getAttributeValue("descripcion")
									.equals("Cubierto con lluvia")) {
								p.println("<td align=\"center\"><img src=\"gif/23.gif\" alt=\"Cubierto con lluvia\"/></td>");
							} else if (e.getAttributeValue("descripcion")
									.equals("Cubierto con lluvia escasa")) {
								p.println("<td align=\"center\"><img src=\"gif/23.gif\" alt=\"Cubierto con lluvia escasa\"/></td>");
							} else if (e.getAttributeValue("descripcion")
									.equals("Cubierto")) {
								p.println("<td align=\"center\"><img src=\"gif/13.gif\" alt=\"Cubierto\"/></td>");
							} else if (e.getAttributeValue("descripcion")
									.equals("Nuboso")) {
								p.println("<td align=\"center\"><img src=\"gif/13.gif\" alt=\"Nuboso\"/></td>");
							} else if (e.getAttributeValue("descripcion")
									.equals("Muy nuboso")) {
								p.println("<td align=\"center\"><img src=\"gif/13.gif\" alt=\"Muy nuboso\"/></td>");
							} else if (e.getAttributeValue("descripcion")
									.equals("Nuboso con lluvia escasa")) {
								p.println("<td align=\"center\"><img src=\"gif/23.gif\" alt=\"Nuboso con lluvia escasa\"/></td>");
							} else if (e.getAttributeValue("descripcion")
									.equals("Nuboso con lluvia")) {
								p.println("<td align=\"center\"><img src=\"gif/23.gif\" alt=\"Nuboso con lluvia\"/></td>");
							} else if (e.getAttributeValue("descripcion")
									.equals("Muy nuboso con lluvia escasa")) {
								p.println("<td align=\"center\"><img src=\"gif/45.gif\" alt=\"Muy nuboso con lluvia escasa\"/></td>");
							} else if (e
									.getAttributeValue("descripcion")
									.equals("Intervalos nubosos con lluvia")) {
								p.println("<td align=\"center\"><img src=\"gif/45.gif\" alt=\"Intervalos nubosos con lluvia\"/></td>");
							} else if (e.getAttributeValue("descripcion")
									.equals("Muy nuboso con lluvia")) {
								p.println("<td align=\"center\"><img src=\"gif/45.gif\" alt=\"Muy nuboso con lluvia\"/></td>");
							} else if (e.getAttributeValue("descripcion")
									.equals("Poco nuboso")) {
								p.println("<td align=\"center\"><img src=\"gif/12.gif\" alt=\"Poco nuboso\"/></td>");
							} else {
								p.println("<td></td>");
							}

						}
					}

					n++;
				}

				p.println("</tr>");
				p.println("<tr>");

				// PROB. PRECIPITACIONES
				p.println("<td bgcolor=\"CCD2FF\"><strong>Prob. precip.</strong></td>");
				dias = raiz.getChildren();
				itDias = dias.iterator();
				n = 0;
				while (itDias.hasNext()) {
					Element dia = (Element) itDias.next();
					probPrec = dia.getChildren("prob_precipitacion");
					Iterator itPrec = probPrec.iterator();
					// dias de 4 franjas horarias
					if (n < 2) {
						while (itPrec.hasNext()) {
							Element e = (Element) itPrec.next();
							// Seleccionamos los periodos correctos
							if (!e.getAttributeValue("periodo").equals("00-24")
									&& !e.getAttributeValue("periodo").equals(
											"00-12")
									&& !e.getAttributeValue("periodo").equals(
											"12-24")) {
								p.println("<td align=\"center\">"
										+ e.getValue() + "</td>");
							}
						}
					}
					// dias de 2 franjas horarias
					if (n >= 2 && n < 4) {
						while (itPrec.hasNext()) {
							Element e = (Element) itPrec.next();
							// Seleccionamos los periodos correctos
							if (e.getAttributeValue("periodo").equals("00-12")
									|| e.getAttributeValue("periodo").equals(
											"12-24")) {
								p.println("<td align=\"center\">"
										+ e.getValue() + "</td>");
							}
						}
					} else {
						while (itPrec.hasNext()) {
							Element e = (Element) itPrec.next();

							p.println("<td align=\"center\">" + e.getValue()
									+ "</td>");
						}
					}

					n++;
				}

				p.println("</tr>");
				p.println("<tr>");

				// COTA NIEVE
				p.println("<td bgcolor=\"CCD2FF\"><strong>Cota nieve prov.(m)</strong></td>");
				dias = raiz.getChildren();
				itDias = dias.iterator();
				n = 0;
				while (itDias.hasNext()) {
					Element dia = (Element) itDias.next();
					cotaNieve = dia.getChildren("cota_nieve_prov");
					Iterator itNieve = cotaNieve.iterator();
					// dias de 4 franjas horarias
					if (n < 2) {
						while (itNieve.hasNext()) {
							Element e = (Element) itNieve.next();
							// Seleccionamos los periodos correctos
							if (!e.getAttributeValue("periodo").equals("00-24")
									&& !e.getAttributeValue("periodo").equals(
											"00-12")
									&& !e.getAttributeValue("periodo").equals(
											"12-24")) {
								p.println("<td align=\"center\">"
										+ e.getValue() + "</td>");
							}
						}
					}
					// dias de 2 franjas horarias
					if (n >= 2 && n < 4) {
						while (itNieve.hasNext()) {
							Element e = (Element) itNieve.next();
							// Seleccionamos los periodos correctos
							if (e.getAttributeValue("periodo").equals("00-12")
									|| e.getAttributeValue("periodo").equals(
											"12-24")) {
								p.println("<td align=\"center\">"
										+ e.getValue() + "</td>");
							}
						}
					} else {
						while (itNieve.hasNext()) {
							Element e = (Element) itNieve.next();

							p.println("<td align=\"center\">" + e.getValue()
									+ "</td>");
						}
					}

					n++;
				}

				p.println("</tr>");
				p.println("<tr>");

				// TEMP MIN MAX
				p.println("<td bgcolor=\"CCD2FF\"><strong>Temp. min./max.(C)</strong></td>");
				dias = raiz.getChildren();
				itDias = dias.iterator();
				n = 0;
				while (itDias.hasNext()) {
					Element dia = (Element) itDias.next();
					temp = dia.getChildren("temperatura");
					Iterator itTemp = temp.iterator();
					// dias de 4 franjas horarias
					if (n < 2) {
						while (itTemp.hasNext()) {
							Element e = (Element) itTemp.next();
							String maxima = e.getChild("maxima").getValue();
							String minima = e.getChild("minima").getValue();
							p.println("<td colspan=\"4\" align=\"center\">"
									+ minima + "/" + maxima + "</td>");
						}
					}

					// dias de 2 franjas horarias
					if (n >= 2 && n < 4) {
						while (itTemp.hasNext()) {
							Element e = (Element) itTemp.next();
							String maxima = e.getChild("maxima").getValue();
							String minima = e.getChild("minima").getValue();
							p.println("<td colspan=\"2\"align=\"center\"> "
									+ minima + "/" + maxima + "</td>");
						}
					} else {
						while (itTemp.hasNext()) {
							Element e = (Element) itTemp.next();
							String maxima = e.getChild("maxima").getValue();
							String minima = e.getChild("minima").getValue();
							p.println("<td align=\"center\"> " + minima + "/"
									+ maxima + "</td>");
						}
					}

					n++;
				}

				p.println("</tr>");
				p.println("<tr>");

				// VIENTO DIRECCION
				p.println("<td bgcolor=\"CCD2FF\"><strong>Viento</strong></td>");
				dias = raiz.getChildren();
				itDias = dias.iterator();
				n = 0;
				while (itDias.hasNext()) {
					Element dia = (Element) itDias.next();
					vientDir = dia.getChildren("viento");
					Iterator itVientoD = vientDir.iterator();
					// dias de 4 franjas horarias
					if (n < 2) {
						while (itVientoD.hasNext()) {
							Element e = (Element) itVientoD.next();
							// Seleccionamos los periodos correctos
							if (!e.getAttributeValue("periodo").equals("00-24")
									&& !e.getAttributeValue("periodo").equals(
											"00-12")
									&& !e.getAttributeValue("periodo").equals(
											"12-24")) {
								if (e.getChild("direccion").getValue()
										.equals("NO")) {
									p.println("<td align=\"center\"><img src=\"gif/NO.gif\" alt=\"NO\"/></td>");
								} else if (e.getChild("direccion").getValue()
										.equals("O")) {
									p.println("<td align=\"center\"><img src=\"gif/O.gif\" alt=\"O\"/></td>");
								} else if (e.getChild("direccion").getValue()
										.equals("SO")) {
									p.println("<td align=\"center\"><img src=\"gif/SO.gif\" alt=\"SO\"/></td>");
								} else if (e.getChild("direccion").getValue()
										.equals("E")) {
									p.println("<td align=\"center\"><img src=\"gif/E.gif\" alt=\"E\"/></td>");
								} else if (e.getChild("direccion").getValue()
										.equals("N")) {
									p.println("<td align=\"center\"><img src=\"gif/N.gif\" alt=\"N\"/></td>");
								} else if (e.getChild("direccion").getValue()
										.equals("NE")) {
									p.println("<td align=\"center\"><img src=\"gif/NE.gif\" alt=\"NE\"/></td>");
								} else if (e.getChild("direccion").getValue()
										.equals("SE")) {
									p.println("<td align=\"center\"><img src=\"gif/SE.gif\" alt=\"SE\"/></td>");
								} else if (e.getChild("direccion").getValue()
										.equals("C")) {
									p.println("<td align=\"center\"><img src=\"gif/C.gif\" alt=\"C\"/></td>");
								} else {
									p.println("<td></td>");
								}

							}
						}
					}

					// dias de 2 franjas horarias
					if (n >= 2 && n < 4) {
						while (itVientoD.hasNext()) {
							Element e = (Element) itVientoD.next();
							// Seleccionamos los periodos correctos
							if (e.getAttributeValue("periodo").equals("00-12")
									|| e.getAttributeValue("periodo").equals(
											"12-24")) {
								if (e.getChild("direccion").getValue()
										.equals("NO")) {
									p.println("<td align=\"center\"><img src=\"gif/NO.gif\" alt=\"NO\"/></td>");
								} else if (e.getChild("direccion").getValue()
										.equals("O")) {
									p.println("<td align=\"center\"><img src=\"gif/O.gif\" alt=\"O\"/></td>");
								} else if (e.getChild("direccion").getValue()
										.equals("SO")) {
									p.println("<td align=\"center\"><img src=\"gif/SO.gif\" alt=\"SO\"/></td>");
								} else if (e.getChild("direccion").getValue()
										.equals("E")) {
									p.println("<td align=\"center\"><img src=\"gif/E.gif\" alt=\"E\"/></td>");
								} else if (e.getChild("direccion").getValue()
										.equals("N")) {
									p.println("<td align=\"center\"><img src=\"gif/N.gif\" alt=\"N\"/></td>");
								} else if (e.getChild("direccion").getValue()
										.equals("NE")) {
									p.println("<td align=\"center\"><img src=\"gif/NE.gif\" alt=\"NE\"/></td>");
								} else if (e.getChild("direccion").getValue()
										.equals("SE")) {
									p.println("<td align=\"center\"><img src=\"gif/SE.gif\" alt=\"SE\"/></td>");
								} else if (e.getChild("direccion").getValue()
										.equals("C")) {
									p.println("<td align=\"center\"><img src=\"gif/C.gif\" alt=\"C\"/></td>");
								} else {
									p.println("<td></td>");
								}
							}
						}
					} else {
						while (itVientoD.hasNext()) {
							Element e = (Element) itVientoD.next();
							if (e.getChild("direccion").getValue().equals("NO")) {
								p.println("<td align=\"center\"><img src=\"gif/NO.gif\" alt=\"NO\"/></td>");
							} else if (e.getChild("direccion").getValue()
									.equals("O")) {
								p.println("<td align=\"center\"><img src=\"gif/O.gif\" alt=\"O\"/></td>");
							} else if (e.getChild("direccion").getValue()
									.equals("SO")) {
								p.println("<td align=\"center\"><img src=\"gif/SO.gif\" alt=\"SO\"/></td>");
							} else if (e.getChild("direccion").getValue()
									.equals("E")) {
								p.println("<td align=\"center\"><img src=\"gif/E.gif\" alt=\"E\"/></td>");
							} else if (e.getChild("direccion").getValue()
									.equals("N")) {
								p.println("<td align=\"center\"><img src=\"gif/N.gif\" alt=\"N\"/></td>");
							} else if (e.getChild("direccion").getValue()
									.equals("NE")) {
								p.println("<td align=\"center\"><img src=\"gif/NE.gif\" alt=\"NE\"/></td>");
							} else if (e.getChild("direccion").getValue()
									.equals("SE")) {
								p.println("<td align=\"center\"><img src=\"gif/SE.gif\" alt=\"SE\"/></td>");
							} else if (e.getChild("direccion").getValue()
									.equals("C")) {
								p.println("<td align=\"center\"><img src=\"gif/C.gif\" alt=\"C\"/></td>");
							} else {
								p.println("<td></td>");
							}
						}
					}

					n++;
				}

				p.println("</tr>");
				p.println("<tr>");

				// VIENTO VELOCIDAD
				p.println("<td bgcolor=\"CCD2FF\"><strong>(km/h)</strong></td>");
				dias = raiz.getChildren();
				itDias = dias.iterator();
				n = 0;
				while (itDias.hasNext()) {
					Element dia = (Element) itDias.next();
					vientVel = dia.getChildren("viento");
					Iterator itVientoV = vientVel.iterator();
					// dias de 4 franjas horarias
					if (n < 2) {
						while (itVientoV.hasNext()) {
							Element e = (Element) itVientoV.next();
							// Seleccionamos los periodos correctos
							if (!e.getAttributeValue("periodo").equals("00-24")
									&& !e.getAttributeValue("periodo").equals(
											"00-12")
									&& !e.getAttributeValue("periodo").equals(
											"12-24")) {
								p.println("<td align=\"center\"> "
										+ e.getChild("velocidad").getValue()
										+ "</td>");
							}
						}
					}

					// dias de 2 franjas horarias
					if (n >= 2 && n < 4) {
						while (itVientoV.hasNext()) {
							Element e = (Element) itVientoV.next();
							// Seleccionamos los periodos correctos
							if (e.getAttributeValue("periodo").equals("00-12")
									|| e.getAttributeValue("periodo").equals(
											"12-24")) {
								p.println("<td align=\"center\"> "
										+ e.getChild("velocidad").getValue()
										+ "</td>");
							}
						}
					} else {
						while (itVientoV.hasNext()) {
							Element e = (Element) itVientoV.next();

							p.println("<td align=\"center\"> "
									+ e.getChild("velocidad").getValue()
									+ "</td>");
						}
					}

					n++;
				}

				p.println("</tr>");
				p.println("<tr>");

				// UV MAXIMO
				p.println("<td bgcolor=\"CCD2FF\"><strong>Indice UV maximo</strong></td>");
				dias = raiz.getChildren();
				itDias = dias.iterator();
				n = 0;
				while (itDias.hasNext()) {
					Element dia = (Element) itDias.next();
					uvMax = dia.getChildren("uv_max");
					Iterator itUv = uvMax.iterator();
					// dias de 4 franjas horarias
					if (n < 2) {
						while (itUv.hasNext()) {
							Element e = (Element) itUv.next();
							p.println("<td colspan=\"4\" align=\"center\"> "
									+ e.getValue() + "</td>");
						}
					}

					// dias de 2 franjas horarias
					if (n >= 2 && n < 4) {
						while (itUv.hasNext()) {
							Element e = (Element) itUv.next();
							p.println("<td colspan=\"2\" align=\"center\"> "
									+ e.getValue() + "</td>");
						}
					} else {
						while (itUv.hasNext()) {
							Element e = (Element) itUv.next();
							p.println("<td align=\"center\"> " + e.getValue()
									+ "</td>");
						}
					}

					n++;
				}

				p.println("</tr>");
				p.println("</tr>");
				p.println("</html>");
				p.close();
			}
			System.out.println("Generado HTML");
			return true;
			
		} catch (Exception e) {
			System.out.println(e.getMessage());
			System.out.println("Fallo al procesar HTML");
			return false;
		}		

	}

	/**
	 * Dado un fichero XML primero se pasara toda la informacion a una serie de
	 * List para posteriormente pasar esos datos a el formato JSON que se
	 * guardaran en un fichero con ese formato.
	 * 
	 * @param ficheroXML
	 */
	private static boolean generarJSON(String ficheroXML) {
		try {
			// Vectores para guardar los datos del XML
			diasH = new Vector();
			estCieloH = new Vector();
			probPrecH = new Vector();
			cotaNieveH = new Vector();
			tempH = new Vector();
			vientDirH = new Vector();
			vientVelH = new Vector();
			uvMaxH = new Vector();

			SAXBuilder constructor = new SAXBuilder(false);

			// quitar comprobacion dtd
			constructor
					.setFeature(
							"http://apache.org/xml/features/nonvalidating/load-external-dtd",
							false);

			Document doc = constructor.build(ficheroXML);

			Element raiz = doc.getRootElement();
			raiz = raiz.getChild("prediccion");
			// ya estamos en prediccion

			if (raiz.getName().equals("prediccion")) {

				FileOutputStream archivo;
				PrintStream p;
				archivo = new FileOutputStream("temp/prediccion.json");
				p = new PrintStream(archivo);

				// TABLA FECHAS
				dias = raiz.getChildren();
				Iterator itDias = dias.iterator();
				n = 0;
				while (itDias.hasNext()) {
					Element dia = (Element) itDias.next();
					String fech = dia.getAttributeValue("fecha");
					diasH.add(fech);
					n++;

				}

				// TABLA ESTADO CIELO
				dias = raiz.getChildren();
				itDias = dias.iterator();
				n = 0;
				while (itDias.hasNext()) {
					Element dia = (Element) itDias.next();
					estCielo = dia.getChildren("estado_cielo");
					Iterator itCiel = estCielo.iterator();
					// dias de 4 franjas horarias
					if (n < 2) {
						while (itCiel.hasNext()) {
							Element e = (Element) itCiel.next();
							// Seleccionamos los periodos correctos
							if (!e.getAttributeValue("periodo").equals("00-24")
									&& !e.getAttributeValue("periodo").equals(
											"00-12")
									&& !e.getAttributeValue("periodo").equals(
											"12-24")) {
								String estado = e
										.getAttributeValue("descripcion");
								estCieloH.add(estado);
							}
						}
					}
					// dias de 2 franjas horarias
					if (n >= 2 && n < 4) {
						while (itCiel.hasNext()) {
							Element e = (Element) itCiel.next();
							// Seleccionamos los periodos correctos
							if (e.getAttributeValue("periodo").equals("00-12")
									|| e.getAttributeValue("periodo").equals(
											"12-24")) {
								String estado = e
										.getAttributeValue("descripcion");
								estCieloH.add(estado);
							}
						}
					} else {
						while (itCiel.hasNext()) {
							Element e = (Element) itCiel.next();
							String estado = e.getAttributeValue("descripcion");
							estCieloH.add(estado);
						}
					}

					n++;
				}

				// TABLA PROB. PRECIPITACIONES
				dias = raiz.getChildren();
				itDias = dias.iterator();
				n = 0;
				while (itDias.hasNext()) {
					Element dia = (Element) itDias.next();
					probPrec = dia.getChildren("prob_precipitacion");
					Iterator itPrec = probPrec.iterator();
					// dias de 4 franjas horarias
					if (n < 2) {
						while (itPrec.hasNext()) {
							Element e = (Element) itPrec.next();
							// Seleccionamos los periodos correctos
							if (!e.getAttributeValue("periodo").equals("00-24")
									&& !e.getAttributeValue("periodo").equals(
											"00-12")
									&& !e.getAttributeValue("periodo").equals(
											"12-24")) {
								String precipitacion = e.getValue();
								probPrecH.add(precipitacion);

							}
						}
					}
					// dias de 2 franjas horarias
					if (n >= 2 && n < 4) {
						while (itPrec.hasNext()) {
							Element e = (Element) itPrec.next();
							// Seleccionamos los periodos correctos
							if (e.getAttributeValue("periodo").equals("00-12")
									|| e.getAttributeValue("periodo").equals(
											"12-24")) {
								String precipitacion = e.getValue();
								probPrecH.add(precipitacion);
							}
						}
					} else {
						while (itPrec.hasNext()) {
							Element e = (Element) itPrec.next();

							String precipitacion = e.getValue();
							probPrecH.add(precipitacion);
						}
					}

					n++;
				}

				// TABLA COTA NIEVE
				dias = raiz.getChildren();
				itDias = dias.iterator();
				n = 0;
				while (itDias.hasNext()) {
					Element dia = (Element) itDias.next();
					cotaNieve = dia.getChildren("cota_nieve_prov");
					Iterator itNieve = cotaNieve.iterator();
					// dias de 4 franjas horarias
					if (n < 2) {
						while (itNieve.hasNext()) {
							Element e = (Element) itNieve.next();
							// Seleccionamos los periodos correctos
							if (!e.getAttributeValue("periodo").equals("00-24")
									&& !e.getAttributeValue("periodo").equals(
											"00-12")
									&& !e.getAttributeValue("periodo").equals(
											"12-24")) {
								String cota = e.getValue();
								cotaNieveH.add(cota);
							}
						}
					}
					// dias de 2 franjas horarias
					if (n >= 2 && n < 4) {
						while (itNieve.hasNext()) {
							Element e = (Element) itNieve.next();
							// Seleccionamos los periodos correctos
							if (e.getAttributeValue("periodo").equals("00-12")
									|| e.getAttributeValue("periodo").equals(
											"12-24")) {
								String cota = e.getValue();
								cotaNieveH.add(cota);
							}
						}
					} else {
						while (itNieve.hasNext()) {
							Element e = (Element) itNieve.next();

							String cota = e.getValue();
							cotaNieveH.add(cota);
						}
					}

					n++;
				}

				// TABLA TEMP MIN MAX
				dias = raiz.getChildren();
				itDias = dias.iterator();
				n = 0;
				while (itDias.hasNext()) {
					Element dia = (Element) itDias.next();
					temp = dia.getChildren("temperatura");
					Iterator itTemp = temp.iterator();
					// dias de 4 franjas horarias
					if (n < 2) {
						while (itTemp.hasNext()) {
							Element e = (Element) itTemp.next();
							String maxima = e.getChild("maxima").getValue();
							String minima = e.getChild("minima").getValue();
							String temp = (minima + "/" + maxima);
							tempH.add(temp);

						}
					}

					// dias de 2 franjas horarias
					if (n >= 2 && n < 4) {
						while (itTemp.hasNext()) {
							Element e = (Element) itTemp.next();
							String maxima = e.getChild("maxima").getValue();
							String minima = e.getChild("minima").getValue();
							String temp = (minima + "/" + maxima);
							tempH.add(temp);
						}
					} else {
						while (itTemp.hasNext()) {
							Element e = (Element) itTemp.next();
							String maxima = e.getChild("maxima").getValue();
							String minima = e.getChild("minima").getValue();
							String temp = (minima + "/" + maxima);
							tempH.add(temp);
						}
					}

					n++;
				}

				// TABLA VIENTO DIRECCION
				dias = raiz.getChildren();
				itDias = dias.iterator();
				n = 0;
				while (itDias.hasNext()) {
					Element dia = (Element) itDias.next();
					vientDir = dia.getChildren("viento");
					Iterator itVientoD = vientDir.iterator();
					// dias de 4 franjas horarias
					if (n < 2) {
						while (itVientoD.hasNext()) {
							Element e = (Element) itVientoD.next();
							// Seleccionamos los periodos correctos
							if (!e.getAttributeValue("periodo").equals("00-24")
									&& !e.getAttributeValue("periodo").equals(
											"00-12")
									&& !e.getAttributeValue("periodo").equals(
											"12-24")) {
								String dir = e.getChild("direccion").getValue();
								vientDirH.add(dir);

							}
						}
					}

					// dias de 2 franjas horarias
					if (n >= 2 && n < 4) {
						while (itVientoD.hasNext()) {
							Element e = (Element) itVientoD.next();
							// Seleccionamos los periodos correctos
							if (e.getAttributeValue("periodo").equals("00-12")
									|| e.getAttributeValue("periodo").equals(
											"12-24")) {
								String dir = e.getChild("direccion").getValue();
								vientDirH.add(dir);
							}
						}
					} else {
						while (itVientoD.hasNext()) {
							Element e = (Element) itVientoD.next();
							String dir = e.getChild("direccion").getValue();
							vientDirH.add(dir);
						}
					}

					n++;
				}

				// TABLA VIENTO VELOCIDAD
				dias = raiz.getChildren();
				itDias = dias.iterator();
				n = 0;
				while (itDias.hasNext()) {
					Element dia = (Element) itDias.next();
					vientVel = dia.getChildren("viento");
					Iterator itVientoV = vientVel.iterator();
					// dias de 4 franjas horarias
					if (n < 2) {
						while (itVientoV.hasNext()) {
							Element e = (Element) itVientoV.next();
							// Seleccionamos los periodos correctos
							if (!e.getAttributeValue("periodo").equals("00-24")
									&& !e.getAttributeValue("periodo").equals(
											"00-12")
									&& !e.getAttributeValue("periodo").equals(
											"12-24")) {
								String vel = e.getChild("velocidad").getValue();
								vientVelH.add(vel);

							}
						}
					}

					// dias de 2 franjas horarias
					if (n >= 2 && n < 4) {
						while (itVientoV.hasNext()) {
							Element e = (Element) itVientoV.next();
							// Seleccionamos los periodos correctos
							if (e.getAttributeValue("periodo").equals("00-12")
									|| e.getAttributeValue("periodo").equals(
											"12-24")) {
								String vel = e.getChild("velocidad").getValue();
								vientVelH.add(vel);
							}
						}
					} else {
						while (itVientoV.hasNext()) {
							Element e = (Element) itVientoV.next();

							String vel = e.getChild("velocidad").getValue();
							vientVelH.add(vel);
						}
					}

					n++;
				}

				// TABLA UV MAXIMO
				dias = raiz.getChildren();
				itDias = dias.iterator();
				n = 0;
				while (itDias.hasNext()) {
					Element dia = (Element) itDias.next();
					uvMax = dia.getChildren("uv_max");
					Iterator itUv = uvMax.iterator();
					// dias de 4 franjas horarias
					if (n < 2) {
						while (itUv.hasNext()) {
							Element e = (Element) itUv.next();
							String uv = e.getValue();
							uvMaxH.add(uv);

						}
					}

					// dias de 2 franjas horarias
					if (n >= 2 && n < 4) {
						while (itUv.hasNext()) {
							Element e = (Element) itUv.next();
							String uv = e.getValue();
							uvMaxH.add(uv);
						}
					} else {
						while (itUv.hasNext()) {
							Element e = (Element) itUv.next();
							String uv = e.getValue();
							uvMaxH.add(uv);
						}
					}

					n++;
				}

				p = new PrintStream(archivo);
				p.println("{");
				p.println("\"Prediccion\" : [");
				Iterator diasIt = diasH.iterator();
				Iterator cieloIt = estCieloH.iterator();
				Iterator precIt = probPrecH.iterator();
				Iterator cotaIt = cotaNieveH.iterator();
				Iterator tempIt = tempH.iterator();
				Iterator dirIt = vientDirH.iterator();
				Iterator velIt = vientVelH.iterator();
				Iterator uvIt = uvMaxH.iterator();
				int ultimo = 0;
				while (diasIt.hasNext()) {
					ultimo++;
					p.println("{\"fecha\" : \"" + diasIt.next() + "\",");

					// Estado cielo
					lista = ("\"estadoCielo\" : [");
					if (ultimo < 3) {
						lista = lista + "\"" + cieloIt.next().toString()
								+ "\" ,";
						lista = lista + "\"" + cieloIt.next().toString()
								+ "\" ,";
						lista = lista + "\"" + cieloIt.next().toString()
								+ "\" ,";
						lista = lista + "\"" + cieloIt.next().toString()
								+ "\" ,";
					} else if (ultimo >= 3 && ultimo < 5) {
						lista = lista + "\"" + cieloIt.next().toString()
								+ "\" ,";
						lista = lista + "\"" + cieloIt.next().toString()
								+ "\" ,";
					} else {
						lista = lista + "\"" + cieloIt.next().toString()
								+ "\" ,";
					}
					lista = lista.substring(0, lista.length() - 2);
					p.println(lista + "],");

					// Precipitaciones
					lista = ("\"precipitaciones\" : [");
					if (ultimo < 3) {
						lista = lista + "\"" + precIt.next().toString()
								+ "\" ,";
						lista = lista + "\"" + precIt.next().toString()
								+ "\" ,";
						lista = lista + "\"" + precIt.next().toString()
								+ "\" ,";
						lista = lista + "\"" + precIt.next().toString()
								+ "\" ,";
					} else if (ultimo >= 3 && ultimo < 5) {
						lista = lista + "\"" + precIt.next().toString()
								+ "\" ,";
						lista = lista + "\"" + precIt.next().toString()
								+ "\" ,";
					} else {
						lista = lista + "\"" + precIt.next().toString()
								+ "\" ,";
					}
					lista = lista.substring(0, lista.length() - 2);
					p.println(lista + "],");

					// Cota de nieve
					lista = ("\"cotaNieve\" : [");
					if (ultimo < 3) {
						lista = lista + "\"" + cotaIt.next().toString()
								+ "\" ,";
						lista = lista + "\"" + cotaIt.next().toString()
								+ "\" ,";
						lista = lista + "\"" + cotaIt.next().toString()
								+ "\" ,";
						lista = lista + "\"" + cotaIt.next().toString()
								+ "\" ,";
					} else if (ultimo >= 3 && ultimo < 5) {
						lista = lista + "\"" + cotaIt.next().toString()
								+ "\" ,";
						lista = lista + "\"" + cotaIt.next().toString()
								+ "\" ,";
					} else {
						lista = lista + "\"" + cotaIt.next().toString()
								+ "\" ,";
					}
					lista = lista.substring(0, lista.length() - 2);
					p.println(lista + "],");

					// Temperatura min/max
					lista = ("\"temperaturas\" : ");
					lista = lista + "\"" + tempIt.next().toString() + "\" ,";
					lista = lista.substring(0, lista.length() - 2);
					p.println(lista + ",");

					// Direccion viento
					lista = ("\"dirViento\" : [");
					if (ultimo < 3) {
						lista = lista + "\"" + dirIt.next().toString() + "\" ,";
						lista = lista + "\"" + dirIt.next().toString() + "\" ,";
						lista = lista + "\"" + dirIt.next().toString() + "\" ,";
						lista = lista + "\"" + dirIt.next().toString() + "\" ,";
					} else if (ultimo >= 3 && ultimo < 5) {
						lista = lista + "\"" + dirIt.next().toString() + "\" ,";
						lista = lista + "\"" + dirIt.next().toString() + "\" ,";
					} else {
						lista = lista + "\"" + dirIt.next().toString() + "\" ,";
					}
					lista = lista.substring(0, lista.length() - 2);
					p.println(lista + "],");

					// Velocidad viento
					lista = ("\"velViento\" : [");
					if (ultimo < 3) {
						lista = lista + "\"" + velIt.next().toString() + "\" ,";
						lista = lista + "\"" + velIt.next().toString() + "\" ,";
						lista = lista + "\"" + velIt.next().toString() + "\" ,";
						lista = lista + "\"" + velIt.next().toString() + "\" ,";
					} else if (ultimo >= 3 && ultimo < 5) {
						lista = lista + "\"" + velIt.next().toString() + "\" ,";
						lista = lista + "\"" + velIt.next().toString() + "\" ,";
					} else {
						lista = lista + "\"" + velIt.next().toString() + "\" ,";
					}
					lista = lista.substring(0, lista.length() - 2);
					p.println(lista + "],");

					// Indice UV
					lista = ("\"uv\" : ");
					try {
						lista = lista + "\"" + uvIt.next().toString() + "\" ,";
					} catch (Exception e) {
					}
					if (lista.length() == 12) {
						lista = lista.substring(0, lista.length() - 2);
					} else {
						lista = lista + "\"\"";
						lista = lista.substring(0, lista.length());
					}
					if (ultimo != dias.size()) {
						p.println(lista + "},");
					} else {
						p.println(lista + "}");
					}

				}
				p.println("]");
				p.println("}");
				p.close();
				
			}
			System.out.println("Generado JSON.");
			return true;

		} catch (Exception e) {
			System.out.println("Fallo al procesar JSON.");
			return false;
		}
	}

	/**
	 * Establece los parametros de la ventana Gui
	 */
	private static void start() {
		Gui monitor = new Gui();
		monitor.setTitle("Aplicacion Tiempo");
		monitor.setLayout(new BoxLayout(monitor.getContentPane(),
				BoxLayout.Y_AXIS));
		monitor.pack();
		monitor.setSize(350, 175);
		monitor.setResizable(false);
		monitor.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		monitor.setVisible(true);
	}

	public static void main(String[] args) {
		start();
	}

}
