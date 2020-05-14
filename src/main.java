import java.io.IOException;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.Arrays;
import java.util.Random;
import java.util.Scanner;
import java.util.concurrent.TimeUnit;



public class main {


	public static void main(String[] args) {
		// TODO Auto-generated method stub
		System.out.println("Bitte binäre Folge eingeben: ");
		
		Scanner sc = new Scanner(System.in);
		
		
		try {
			String in = sc.nextLine();
			boolean[] data = new boolean[in.length()];
			for(int i = 0; i < in.length(); i++) {
				char c = in.charAt(i);
				if(c == '0' || c == '1') {
					data[i] = c == '1';
				} else throw new Exception(c + " ist kein binäres Zeichen!");
				
			}
			System.out.println("                  Datenfolge: " + binformat(data));
			System.out.println("        Länge der Datenfolge: "+ data.length);
			
			DecimalFormat df = new DecimalFormat("#.####");
			df.setRoundingMode(RoundingMode.HALF_EVEN);
			
			
			for(int i = 0; i <= 10; i++) {
				boolean[] temp = channel_bsc((float)i/10,data);
				
				int counter = 0;
				
				System.out.print("Fehlerwahrscheinlichkeit "+ (float)i/10 + ": ");

				for(int j = 0; j < data.length; j++) {
					if(data[j] != temp[j]) {
						counter++;
					}
					System.out.print((temp[j] ? "1" : "0"));
				}
				
				System.out.println();
				System.out.println("          Abweichung bei "+ (float)i/10 + ": " + counter + " / " + df.format((double)counter/data.length*100) + "%");
			}
		} catch (Exception e) {
			System.err.println(e.getMessage());
		}
		
	}
	
	/*
	 * Hilfsmethoden
	 */
	
	/**
	 * Gibt an, wie viele Unterschiede zwischen zwei gleich langen Datenfolgen sind.
	 * @param d1 Datenfolge 1
	 * @param d2 Datenfolge 2
	 * @return Anzahl der Unterschiede
	 */
	private static void diff(boolean[] d1, boolean[] d2) throws IOException {
		

	}
	
	/**
	 * Wandelt eine binäre Folge in einen String aus Nullen und Einsen um.
	 * @param d
	 * @return
	 */
	private static String binformat(boolean[] d) {
		String out = "";
		for(boolean b:d) {
			if(b) out += "1"; else out += "0";
		}
		return out;
	}
	
	/*
	 * P2-1
	 */
	
	/**
	 * Erzeugt eine binäre Datenfolge.
	 * @param p Die Wahrscheinlichkeit einer 1.
	 * @param n Die Länge der Folge.
	 * @return Die binäre Datenfolge.
	 * @throws IOException p ist größer als 1 oder kleiner als 0
	 */
	public static boolean[] channel_bsc(float p,int n) throws IOException {
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
	 * @throws IOException
	 */
	public static boolean[] channel_bsc(float p, boolean[] data) throws IOException {
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
	


}
