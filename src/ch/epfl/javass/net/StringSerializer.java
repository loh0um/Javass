package ch.epfl.javass.net;

import static java.nio.charset.StandardCharsets.US_ASCII;
import static java.nio.charset.StandardCharsets.UTF_8;

import java.util.Base64;
import java.util.Base64.Decoder;
import java.util.Base64.Encoder;

/**
 * Classe facilitant la (dé)sérialisation des valeurs échangées 
 * entre le client et le serveur.
 * 
 * @author Antoine Masanet (288366)
 * @author Loïc Houmard (297181)
 *
 */
public final class StringSerializer {

    /**
     * Valeur de la base hexadecimale 
     */
    public final static int RADIX_HEX = 16;

    /**
     * Serialise un int sous la forme de sa représentation textuelle en base 16.
     * 
     * @param i: L'entier a serialise.
     * 
     * @return: La representation textuelle en base 16 de l'entier i.
     */
    public static String serializeInt(int i) {
        return Integer.toUnsignedString(i, RADIX_HEX);
    }

    /**
     * Deserialise un int de sa représentation textuelle en base 16 a sa valeur entiere.
     * 
     * @param s: Representation textuelle de l'entier a deserialise.
     * 
     * @return: La valeur de l'entier apres deserialisation.
     */
    public static int deserializeInt(String s) {
        return Integer.parseUnsignedInt(s, RADIX_HEX);
    }
    
    /**
     * Serialise un boolean sous la forme de sa représentation textuelle en base 16.
     * 
     * @param b: Le boolean a serialise.
     * 
     * @return: La representation textuelle en base 16 de l'entier i.
     */
    public static String serializeBool(boolean b) {
        return b==true? Integer.toUnsignedString(1, RADIX_HEX):Integer.toUnsignedString(0, RADIX_HEX);
    }

    /**
     * Deserialise un boolean de sa représentation textuelle en base 16 a sa valeur entiere.
     * 
     * @param s: Representation textuelle du boolean a deserialise.
     * 
     * @return: La valeur du boolean apres deserialisation.
     */
    public static boolean deserializeBool(String s) {
        return Integer.parseUnsignedInt(s, RADIX_HEX)==1?true:false;
    }

    /**
     * Serialise un long sous la forme de sa représentation textuelle en base 16.
     * 
     * @param i: L'entier a serialise.
     * 
     * @return: La representation textuelle en base 16 de l'entier i.
     */
    public static String serializeLong(long i) {
        return Long.toUnsignedString(i, RADIX_HEX);
    }

    /**
     * Deserialise un long de sa représentation textuelle en base 16 a sa valeur entiere.
     * 
     * @param s: Representation textuelle de l'entier a deserialise.
     * 
     * @return: La valeur de l'entier apres deserialisation.
     */
    public static long deserializeLong(String s) {
        return Long.parseUnsignedLong(s, RADIX_HEX);
    }

    /**
     * Retourne le string encode sous un string en base64 apres l'avoir recu en UTF8.
     * 
     * @param sIn: Le string a convertir.
     * 
     * @return le string encode. 
     */
    public static String serializeString(String sIn) {
        Encoder base64Encoder = Base64.getEncoder();

        byte[] bytes = sIn.getBytes(UTF_8);

        return base64Encoder.encodeToString(bytes);
    }

    /**
     * Retourne le string encode sous un string en UTF8 apres l'avoir recu en base64(ASCII).
     * 
     * @param sIn: Le string a convertir.
     * 
     * @return: le string encode.
     */
    public static String deserializeString(String sIn) {
        Decoder base64Decoder = Base64.getDecoder();

        byte[] bytes = sIn.getBytes(US_ASCII);

        return new String(base64Decoder.decode(bytes), UTF_8);
    }

    /**
     * Combine un ensemble de strings en les separant par un delimiteur.
     * 
     * @param delimiter: Le delimiteur.
     * @param strings: L'ensembles des strings a delimiter.
     * 
     * @return: Un string correspondant a l'ensemble des strings combines.
     */
    public static String combine(char delimiter, String... strings) {
        return String.join(String.valueOf(delimiter), strings);
    }

    /**
     * Separe un string correspondant a une combinaison de string 
     * en un tableau de string contenant ses composants separes.
     * 
     * @param delimiter: Le delimiteur.
     * @param sIn: Le string a separer.
     * 
     * @return: Un tableau de string contenant ses composants separes.
     */
    public static String[] split(char delimiter, String sIn) {
        return sIn.split(String.valueOf(delimiter));
    }
}
