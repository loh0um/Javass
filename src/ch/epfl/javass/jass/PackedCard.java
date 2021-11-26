package ch.epfl.javass.jass;

import ch.epfl.javass.bits.Bits32;
import ch.epfl.javass.jass.Card.Rank;

/**
 * 
 * Classe non instantiable permettant de manipuler des cartes d'un jeu de Jass empaquetées dans 
 * un entier de type int a l'aide de methode statiques. 
 * 
 * @author Antoine Masanet (288366)
 * @author Loïc Houmard (297181)
 * 
 */
public final class PackedCard {

    /**
     * Constructeur vide
     */
    private PackedCard() {
    }

    /**
     * Represente une carte empaquete non valide
     */
    public final static int INVALID = 0b111111;

    /**
     * Contient dans la premiere colonne le nombre de points que valent les cartes
     * qui ne sont pas a l'atout. 
     * Contient dans la seconde colonne le nombre de points que valent les cartes
     * qui sont a l'atout. 
     */
    public final static int CARDPOINTS[][] = { { 0, 0, 0, 0, 10, 2, 3, 4, 11 },
            { 0, 0, 0, 14, 10, 20, 3, 4, 11 } };

    /**
     * Ensemble des constantes privees de la classe.
     */
    private static final int NONTRUMPOINTS = 0;
    private static final int TRUMPOINTS = 1;
    private static final int RANKSIZE = 4;
    private static final int COLORSIZE = 2;
    private static final int STARTRANK = 0;
    private static final int STARTCOLOR = 4;
    private static final int STARTUNUSEDBITS = RANKSIZE + COLORSIZE;

    /**
     * Renvoie vrai si la carte est bien empaquetee et faux sinon
     * 
     * @param pkCard
     * 
     * @return vrai ssi la valeur donnée est une carte empaquetée valide, c-à-d 
     *         si les bits contenant le rang contiennent une valeur comprise entre
     *         0 et 8 (inclus) et si les bits inutilisés valent tous 0.
     * 
     */
    public static boolean isValid(int pkCard) {
        return Bits32.extract(pkCard, STARTRANK, RANKSIZE) < Rank.COUNT
                && (Bits32.extract(pkCard, STARTUNUSEDBITS,
                        Integer.SIZE - STARTUNUSEDBITS) == 0);
    }

    /**
     * Empaquete une carte
     * 
     * @param c: Couleur d'une carte.
     * @param r: Rang d'une carte.
     * 
     * @return Une version empaquetee sur 32bit de la carte correspondante a ces attributs.
     */
    public static int pack(Card.Color c, Card.Rank r) {
        return Bits32.pack(r.ordinal(), RANKSIZE, c.ordinal(), COLORSIZE);
    }

    /**
     * Extrait la couleur d'une carte empaquetee sur 32bit. 
     * 
     * @param pkCard: Une version empaquetee sur 32bit d'une carte.
     * 
     * @return La couleur de la carte empaquetee dans pkCard. 
     */
    public static Card.Color color(int pkCard) {
        assert isValid(pkCard);

        return Card.Color.ALL
                .get(Bits32.extract(pkCard, STARTCOLOR, COLORSIZE));
    }

    /**
     * Extrait le rang d'une carte empaquetee sur 32bit.
     *  
     * @param pkCard:Une version empaquetee sur 32bit d'une carte.
     * 
     * @return Le rang de la carte empaquetee dans pkCard. 
     */
    public static Card.Rank rank(int pkCard) {
        assert isValid(pkCard);

        return Card.Rank.ALL.get(Bits32.extract(pkCard, STARTRANK, RANKSIZE));
    }

    /**
     * Renvoie vrai si la carte de gauche est plus forte que la carte de droite
     * et faux autrement.
     * 
     * @param trump: la couleur de l'atout.
     * @param pkCardL: Une version empaquetee de la carte a gauche.
     * @param pkCardR: Une version empaquetee de la carte a droite.
     * 
     * @return: vrai si la carte de gauche est plus forte que la carte de droite
     *          et faux autrement.
     */
    public static boolean isBetter(Card.Color trump, int pkCardL, int pkCardR) {

        if (color(pkCardL) == color(pkCardR)) {
            if (color(pkCardL) == trump)
                return rank(pkCardL).trumpOrdinal() > rank(pkCardR)
                        .trumpOrdinal();

            else
                return rank(pkCardL).ordinal() > rank(pkCardR).ordinal();
        }

        else
            return color(pkCardL) == trump;
    }

    /**
     * Renvoie le nombre de point que vaut la carte en fonction de l'atout.
     * 
     * @param trump:La couleur de l'atout.
     * @param pkCard: Une version empaquete de la carte.
     * 
     * @return Le nombre de point que vaut la carte.
     */
    public static int points(Card.Color trump, int pkCard) {

        if (color(pkCard) != trump)
            return CARDPOINTS[NONTRUMPOINTS][rank(pkCard).ordinal()];

        else
            return CARDPOINTS[TRUMPOINTS][rank(pkCard).ordinal()];
    }

    /**
     * Renvoie un String contenant une description de la carte, son rang et sa couleur
     * 
     * @param pkCard:  Une version empaquete de la carte.
     * 
     * @return Une description de la carte 
     */
    public static String toString(int pkCard) {
        return color(pkCard).toString() + rank(pkCard).toString();
    }
}
