package p1;

/**
 * Diese Datei enthält Code aus dem ersten Praktikum.
 * @author Alexander Fuchs
 *
 */
public class Praktikum1 {
	
	//A-Z und 0-9 Array in Morse
	static final String[] morse = {"._","_...","_._.","_..",".",".._.","__.","....","..",".___","_._","._..",
			"__","_.","___",".__.","__._","._.","...","_",".._","..._",".__","_.._","_.__","__..",
			"_____",".____","..___","...__","...._",".....","_....","__...","___..","____."}; 
	
	/**
	 * Codiert einen Text in Morsezeichen nach folgendem Schema:
	 * 
	 * Punkt: '.'
	 * Strich: '_' 
	 * Pause zwischen Zeichen: ':' 
	 * Pause zwischen zwei Wörtern: '::' 
	 * Satzende: '*' .
	 * 
	 * Dies weicht leicht von der Aufgabenstellung ab, ist aber der effektivste Weg, 
	 * die einzelnen Morsezeichen auch im Binären eindeutig voneinander abzugrenzen.
	 * 
	 * Zulässige Zeichen sind {A-Za-z0-9}, wobei der Text intern in lowercase umgewandelt wird.
	 * 
	 * @param t2mtext Der Text, der in Morse umgewandelt werden soll
	 * @return Der Text in Morse codiert
	 */
	public static String text2morse(String t2mtext) {

		t2mtext = t2mtext.toLowerCase(); //da Morse keine Groß- und Kleinschreibung kennt, ist es einfacher, einheitlich zu arbeiten.
		String out = "";
		char c = 0;
		for(int i = 0; i < t2mtext.length(); i++) {
			c = t2mtext.charAt(i);
			out += t2mhelper(c);
			if(c != ' ') { // Hinter jedes Morsezeichen (außer dem Leerzeichen) wird ein : angehängt, um das Ende des Zeichens anzuzeigen.
				out += ":";
			}
		}

		return out;

	}

	/**
	 * Helper für Text2Morse. Hier werden einzelne Buchstaben in Morse umgewandelt.
	 * @param c Der Buchstabe, der in Morse umgewandelt wird
	 * @return Das Morsezeichen
	 */
	private static String t2mhelper(char c) {
		try {
			//Punkte und Leerzeichen werden gesondert abgefragt
			if(c == '.') return "*";
			if(c == ' ') return ":";
			if(c - 60 < 0) { // Jeder char ist eine Zahl, die die Position des Zeichens in der ASCII-Tabelle beschreibt. Ist diese Zahl
				// kleiner als 60, ist das Zeichen garantiert eine Zahl von 0-9. Anderernfalls ist es ein Buchstabe.
				return morse[c - 22]; // Im morse[] Array beginnen die Zahlen ab Position 26 mit der 0, die einen ASCII Wert von 48 hat
			} else {
				return morse[c - 97]; // Die Buchstaben im Array beginnen bei 0, mit a, das einen ASCII Wert von 97 hat.
			}
		} catch (Exception e) {
			return "x"; //Wenn Fehler auftreten, wird ein X zurückgegeben, um dem Hauptprogramm zu signalisieren, dass es ein Problem gibt.
		}


	}
	
	/**
	 * Konvertiert Morsezeichen in eine binäre Folge nach dem folgenden Muster:
	 * '.': 00,
	 * '_': 01,
	 * '*': 10,
	 * ':': 11.
	 * 
	 * @param morsetext Der Text in Morsezeichen, der codiert werden soll
	 * @return boolean-Array der binären Daten
	 */
	public static boolean[] morse2bin(String morsetext) {

		int length = morsetext.length();
		boolean[] out = new boolean[length*2]; // Pro Morseelement 2 Bits

		for(int i = 0; i < length; i++) {
			switch(morsetext.charAt(i)) {
			case '.': //00
				out[2*i] = false;
				out[2*i + 1] = false;
				break;
			case '_': //01
				out[2*i] = false;
				out[2*i + 1] = true;
				break;
			case '*': //10
				out[2*i] = true;
				out[2*i + 1] = false;
				break;
			case ':': //11
				out[2*i] = true;
				out[2*i + 1] = true;
				break;
			}
		}

		return out;
	}
	/**
	 * Wandelt binäre Daten, die nach dem oben genannten Prinzip encodiert werden, in Morsezeichen um.
	 * @param binarray Die binären Daten
	 * @return Die Morsezeichen als String
	 */
	public static String bin2morse(boolean[] binarray) {
		String out = "";
		String temp = "";

		for(int i = 0; i < binarray.length / 2; i++) {
			temp += (binarray[2 * i] ? "1" : "0") + (binarray[2*i + 1] ? "1" : "0"); // Umwandlung in String für einfaches Vergleichen
			switch(temp) {
			case "00":
				out += ".";
				break;
			case "01":
				out += "_";
				break;
			case "10":
				out += "*";
				break;
			case "11":
				out += ":";
				break;
			}
			temp = "";

		}


		return out;


	}

	/**
	 * Wandelt Morsezeichen in Text um.
	 * @param morse Der Text in Morsezeichen
	 * @return Der decodierte Text
	 */
	public static String morse2text(String morse) {

		String rest = morse;
		String zeichen = "";
		String out = "";
		char c = 'x';
		char prev = 'x'; // Damit zwischen :: und : unterschieden werden kann, 
										 // wird jeweils der vorherige Charakter aufgezeichnet.
		for(int i = 0; i < morse.length(); i++) {
			c = rest.charAt(0);
			if(rest.length() > 1) { // diese Abfrage stellt sicher, dass das letzte Zeichen 
															// interpretiert wird, auch ohne : am Ende
				rest = rest.substring(1); // löscht das erste Zeichen, das in c gespeichert ist

				if(c != ':') {

					zeichen += c;
				} else {
					if(prev == ':') {
						//Wort zuende
						out += " ";
					} else {
						//Zeichen zuende, an Interpreter senden

						out+= m2thelper(zeichen);

						zeichen = "";
					}
				}
				prev = c;
			} else { //letztes Zeichen

				if(c == '*') { // Letztes Zeichen interpretieren und Punkt anhängen
					out += m2thelper(zeichen);
					out += m2thelper(Character.toString(c));
				} else if(c == ':'){ // Kann entweder Leerzeichen oder Zeichenende sein
						if(prev == ':') { // Leerzeichen
							out += " ";
						} else { // Zeichenende, vorheriges Zeichen an Interpreter senden
							out += m2thelper(zeichen);
						}
				} else { // Letztes Zeichen ist Teil des Morsezeichen, anhängen und an Interpreter senden
					out += m2thelper(zeichen + c);
				}

			}
		}

		return out;


	}

	
	/**
	 * Helper für Morse2Text. Hier wird ein Morsezeichen in einen Buchstaben umgewandelt.
	 * @param zeichen Das Morsezeichen, das umgewandelt wird
	 * @return Der Buchstabe, für den das Morsezeichen steht
	 */
	private static char m2thelper (String zeichen) {
		if(zeichen.equals("*")) return '.'; // Punkt wird als Sonderfall vorher abgefragt
		if(zeichen.length() == 5) { // Nur Zahlen sind 5stellig
			for(int i = 26; i < 36; i++) { // Zahlen fangen im Morsearray bei 26 an
				if(morse[i].equals(zeichen)) {
					return (char)(22+i); //Die Zahl 0 hat den ASCII Wert 48, um diesen Wert zu erreichen wird 22 addiert
				}
			}
		} else {
			for(int i = 0; i < 26; i++) {
				if(morse[i].equals(zeichen)) {
					return (char)(97+i); // a hat den ASCII Wert 97, also wird dieser auf die Position im Array addiert.
				}
			}
		}


		return '!';

	}
	 
}
