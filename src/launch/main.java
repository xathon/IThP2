package launch;

import java.io.IOException;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.Arrays;
import java.util.InputMismatchException;
import java.util.Random;
import java.util.Scanner;
import java.util.concurrent.TimeUnit;

import p1.Praktikum1;



public class main {


	public static void main(String[] args) {
		// TODO Auto-generated method stub
		
		
		
		System.out.println("Bitte binäre Folge oder Text zum Morse-Encode eingeben: ");
		
		Scanner sc = new Scanner(System.in);
		
		
		try {
			while(true) { //DEBUG
			String in = sc.nextLine();
			boolean[] data = new boolean[in.length()];
			for(int i = 0; i < in.length(); i++) {
				char c = in.charAt(i);
				if(c == '0' || c == '1') {
					data[i] = c == '1';
				} else {
					data = Praktikum1.morse2bin(Praktikum1.text2morse(in));
					break;
				}
				
			}
			boolean[] crc = {true,false,true,true};
			
			
			boolean[] tmp = CRC_Parity(data, crc);
			System.out.println(binformat(tmp));
			System.out.println(binformat(CRC_Parity_Decode(tmp, crc)));
			}
			//System.out.println("Fehlerwahrscheinlichkeit des Kanals (0..1): ");
			//double a = sc.nextDouble();
			
			// auskommentieren für Demonstration der Lösung von Aufgabe P2-1
			//crcdemo(data, a);
			/*	
			if(data.length > 511) { //Blöcke
				boolean[][] blocks = blocks(data, 512);
			} else {
				
			}*/
			
			
		} catch (InputMismatchException e) {
			System.err.println("Bitte auf Dezimalpunkt/komma und Vorzeichen achten.");
		} catch (TransmissionError t) {
			System.err.println("Übertragungsfehler! Angekommene Nachricht: " + t.getMessage());
		}
		catch (Exception e) {
			//System.err.println(e.getMessage());
			e.printStackTrace();
		}
		
	}
	

	
	/*
	 * Hilfsmethoden
	 */

	
	/**
	 * Wandelt eine binäre Folge in einen String aus Nullen und Einsen um.
	 */
	private static String binformat(boolean[] d) {
		String out = "";
		for(boolean b:d) out += (b ? "1" : "0");
		return out;
	}
	
	/*
	 * P2-1
	 */
	
	/**
	 * Demonstriert das Senden einer Datenfolge über einen fehlerhaften Kanal.
	 * @param data Die Daten, die über den Kanal gesendet wird
	 * @param a Die Fehlerwahrscheinlichkeit des Kanals
	 * @throws IOException p ist größer als 1 oder kleiner als 0
	 */
	public static void crcdemo(boolean[] data, double a) throws IOException {
		System.out.println("                  Datenfolge: " + binformat(data));
		System.out.println("        Länge der Datenfolge: "+ data.length);
		
		DecimalFormat df = new DecimalFormat("#.####");
		df.setRoundingMode(RoundingMode.HALF_EVEN);
		
		boolean[] temp = channel_bsc(a,data);
		String errout = "";
		int counter = 0;
		
		System.out.print("Fehlerwahrscheinlichkeit "+ a + ": ");

		for(int j = 0; j < data.length; j++) {
			if(data[j] != temp[j]) {
				counter++;
				errout += "^";
			} else {
				errout += " ";
			}
			System.out.print((temp[j] ? "1" : "0"));
		}
		
		System.out.println("\n                              " + errout);
		System.out.println("          Abweichung bei "+ a + ": " + counter + " / " + df.format((double)counter/data.length*100) + "%\n");
	}
	
	/**
	 * Erzeugt eine binäre Datenfolge.
	 * @param p Die Wahrscheinlichkeit einer 1.
	 * @param n Die Länge der Folge.
	 * @return Die binäre Datenfolge.
	 * @throws IOException p ist größer als 1 oder kleiner als 0
	 */
	public static boolean[] channel_bsc(double p,int n) throws IOException {
		if(p > 1 || p < 0) {
			throw new IOException("Der Anteil p kann nicht höher als 1.0 sein!");
		}
		boolean[] out = new boolean[n];
		if(p == 0.0) return out; //Da das Array mit false initialisiert wird, kann der edge case p = 0.0 (kommt nie vor) abgefangen werden
		Random rng = new Random();
		for(int i = 0; i < n; i++) {
			out[i] = rng.nextDouble() <= p; //Wenn die Zufallszahl größer als die Anteilszahl ist wird die Stelle im Array 1 gesetzt
			
		}
		
		
		return out;
	}
	/**
	 * Lässt eine binäre Datenfolge über einen fehlerhaften Kanal laufen.
	 * @param p Die Fehlerwahrscheinlichkeit des Kanals.
	 * @param data Die Datenfolge.
	 * @return Die Datenfolge, die aus dem Kanal ausgegeben wird.
	 * @throws IOException p ist größer als 1 oder kleiner als 0
	 */
	public static boolean[] channel_bsc(double p, boolean[] data) throws IOException {
		if(p > 1 || p < 0) {
			throw new IOException("Der Anteil p kann nicht höher als 1.0 sein!");
		}
		boolean[] out = channel_bsc(p,data.length); //Generieren des Datenkanals
		for(int i = 0; i < data.length; i++) {
			out[i] = out[i] ^ data[i]; 
			//XOR Operator. Wenn das Bit im Datenkanal true ist, liegt ein Fehler in der Übertragung vor und der
			//Kehrwert des Bit im data Array wird genutzt, ansonsten wird das fehlerfreie Bit genutzt.
		}
		
		
		return out;
	}
	/*
	 * P2-2
	 * 
	 * Die Übertragung von Morse in Binär ist sehr fehleranfällig. Ein einziger Bitfehler kann dazu führen, dass eine große Anzahl von Morsezeichen
	 * nicht decodiert werden kann, wenn der Fehler im Trennzeichen zwischen einzelnen Morsezeichen oder einzelnen Kurz/Langsignalen liegt.
	 * Auch können so ungültige Morsezeichen erzeugt werden. (z.B. .-.-)
	 * 
	 * Beispiel 1: "Hallo" 
	 * -> ....:._:._..:._..:___: 
	 * -> 00000000110001110001000011000100001101010111
	 * Wird diese Nachricht über einen Kanal mit Fehlerwahrscheinlichkeit 10% gesendet, kommt bspw. 00000000110000110001010111000100001101000111 heraus
	 * Dies wird decodiert: ....:..:.___:._..:_._: -> hijlk -> 60% fehlerhaft
	 * 
	 * Beispiel 2:
	 * "Dies ist ein Test."
	 * -> _..:..:.:...::..:...:_::.:..:_.::_:.:...:_:*:
	 * -> 010000110000110011000000111100001100000011011111001100001101001111011100110000001101111011
	 * Wird diese Nachricht über einen Kanal mit Fehlerwahrscheinlichkeit 10% gesendet, kommt bspw. 
	 * 010000110000110011000000111100000100000011011111011100001101000111001100111010001101011011 heraus.
	 * Dies wird decodiert:
	 * -> _..:..:.:...::.._...:_::_:..:_._:.:.:**.:__*:
	 * -> kein gültiger Morsetext
	 * 
	 */
	
	/*
	 * P2-3
	 */
	
	//(100110001)
	public static boolean[] CRC_Parity(boolean[] daten, boolean[] genP) {
		boolean[] crc = new boolean[daten.length + genP.length - 1];
		boolean[] temp = new boolean[genP.length];
		boolean[] xorRes = new boolean[genP.length];
		for(int i = 0; i < daten.length; i++) {
			crc[i] = daten[i];
		}
		
		
		
		int travData = genP.length;
		int newBits = 0;
		
		//initial
		for(int i = 0; i < genP.length; i++) {
			temp[i] = crc[i];
		}
		
		while(travData < crc.length) {
			for(int i = 0; i < genP.length; i++) {
				xorRes[i] = temp[i] ^ genP[i];
			}
			for(newBits = 0; !xorRes[newBits]; newBits++);
			for(int i = 0; i < genP.length - newBits; i++) {
				temp[i] = xorRes[i + newBits];
			}
			for(int i = genP.length - newBits; i < genP.length && travData < crc.length; i++,travData++) {
				temp[i] = crc[travData];
			}
			
		}
		
		//generate CRC
		for(int i = 0; i < genP.length; i++) {
			xorRes[i] = temp[i] ^ genP[i];
		}
		
		//attach CRC
		for(int i = 0; i < genP.length - 1; i++) {
			crc[crc.length - genP.length + 1 + i] = xorRes[i+1];
		}
		
		
		return crc;
	}
	
	public static boolean[] CRC_Parity_Decode(boolean[] daten, boolean[] genP) throws TransmissionError {
		boolean[] crc = new boolean[daten.length - genP.length + 1];
		
		boolean[] temp = new boolean[genP.length];
		boolean[] xorRes = new boolean[genP.length];
		for(int i = 0; i < crc.length; i++) {
			crc[i] = daten[i];
		}
		
		
		
		int travData = genP.length;
		int newBits = 0;
		
		//initial
		for(int i = 0; i < genP.length; i++) {
			temp[i] = crc[i];
		}
		
		while(travData < crc.length) {
			for(int i = 0; i < genP.length; i++) {
				xorRes[i] = temp[i] ^ genP[i];
			}
			for(newBits = 0; !xorRes[newBits]; newBits++);
			for(int i = 0; i < genP.length - newBits; i++) {
				temp[i] = xorRes[i + newBits];
			}
			for(int i = genP.length - newBits; i < genP.length && travData < crc.length; i++,travData++) {
				temp[i] = crc[travData];
			}
			
		}
		
		//generate CRC
		for(int i = 0; i < genP.length; i++) {
			xorRes[i] = temp[i] ^ genP[i];
			
		}
		for(int i = genP.length - 1; i < xorRes.length; i++) {
			if(xorRes[i]) throw new TransmissionError(binformat(xorRes));
		}
		
		
		
		return crc;
	}
	
	public static boolean[][] blocks(boolean[] daten, int length) {
		boolean[][] out = new boolean[(int) Math.ceil(daten.length/length)][length];
		int travData = 0;
		for(int j = 0; travData < daten.length; j++) {
			for(int i = 0; i < length; i++) {
				try {
					out[j][i] = daten[i + travData];
				} catch(Exception e) {
					break;
				}
				
			}
			travData += length;

		}
		
		
		
		
		return out;
	}
	
	private static boolean[] reverse(boolean[] i) {
		boolean[] r = new boolean[i.length];
		
		for(int j = 0; j < i.length; j++) {
			r[j] = i[i.length - 1 - j];
		}
		
		return r;
	}
	


}

class TransmissionError extends Exception {
	TransmissionError(String msg) {
		super(msg);
	}
}
