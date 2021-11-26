package ch.epfl.javass.jass;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import ch.epfl.javass.Preconditions;

/**
 * Classe representant un pli d'un jeu de Jass.
 * 
 * @author Antoine Masanet (288366)
 * @author Loïc Houmard (297181)
 *
 */
public final class Card {

    /**
     * La version empaquetee de la carte.
     */
    private final int packedCard;

    /**
     * Constructeur de la carte.
     * 
     * @param color: La couleur de la nouvelle carte.
     * @param rank: Le rang de la nouvelle carte.
     */
    private Card(int pkCard) {
        this.packedCard = pkCard;
    }

    /**
     * Renvoie une nouvelle carte avec le rang r, et la couleur c.
     * 
     * @param c: La couleur de la nouvelle carte.
     * @param r: Le rang de la nouvelle carte.
     * 
     * @return Une nouvelle carte avec le rang r, et la couleur c.
     */
    public static Card of(Color c, Rank r) {
        return new Card(PackedCard.pack(c, r));
    }

    /**
     * Renvoie une nouvelle carte correspondant a cette version empaquetee.
     * 
     * @param packed: Une version empaquetee de la carte.
     * 
     * @return Une nouvelle carte correspondant a cette version empaquetee.
     */
    public static Card ofPacked(int packed) throws IllegalArgumentException {
        Preconditions.checkArgument(PackedCard.isValid(packed));
        return new Card(packed);
    }

    /**
     * Renvoie une version empaquetee de la carte.
     * 
     * @return Une version empaquetee de la carte.
     */
    public int packed() {
        return this.packedCard;
    }

    /**
     * Renvoie la couleur de la carte.
     * 
     * @return La couleur de la carte.
     */
    public Color color() {
        return PackedCard.color(this.packedCard);
    }

    /**
     * Renvoie le rang de la carte.
     * 
     * @return Le rang de la carte.
     */
    public Rank rank() {
        return PackedCard.rank(this.packedCard);
    }

    /**
     * Renvoie vrai si la carte actuelle est superieure a l'autre carte 
     * et faux sinon.
     * 
     * @param trump: L'atout de la main jouee.
     * @param that: Une autre carte a comparer.
     * 
     * @return Vrai si la carte actuelle est superieure a l'autre carte 
     *         et faux sinon.
     */
    public boolean isBetter(Color trump, Card that) {
        return PackedCard.isBetter(trump, packedCard, that.packed());
    }

    /**
     * Renvoie le nombre de points que vaut cette carte.
     * 
     * @param trump: la couleur de l'atout.
     * 
     * @return Le nombre de points que vaut cette carte.
     */
    public int points(Color trump) {
        return PackedCard.points(trump, packedCard);
    }

    /**
     * Redefinis la methode equals dans le cas de deux cartes.
     * 
     * @param thatO: Une autre carte.
     * 
     * @return Renvoie vrai si les deux cartes sont egales et faux sinon.
     */
    @Override
    public boolean equals(Object thatO) {
        if (thatO == null)
            return false;

        else if (!(thatO instanceof Card))
            return false;

        else
            return ((Card) thatO).packed() == packedCard;
    }

    /**
     * Redefinis la methode hashCode de Object. 
     * 
     * @return Renvoie la version empaquete de la carte.
     */

    @Override
    public int hashCode() {
        return packedCard;
    }

    /**
     * Redefinis la methode toString de Object.
     * 
     * @return Un String contenant une description de la carte, son rang et sa couleur.
     */
    @Override
    public String toString() {
        return PackedCard.toString(packedCard);
    }

    /**
     * Enumeration contenant toutes les couleurs de carte d'un jeu de 52 cartes.
     * 
     * @author Antoine Masanet (288366)
     * @author Loïc Houmard (297181)
     *
     */

    public enum Color {

        SPADE("\u2660"), HEART("\u2665"), DIAMOND("\u2666"), CLUB("\u2663");

        /**
         * Contient tous les couleurs possibles.
         */
        public static final List<Color> ALL = Collections
                .unmodifiableList(Arrays.asList(values()));

        /**
         * Nombre de couleur d'un jeu de 52 cartes.
         */
        public static final int COUNT = 4;

        /**
         * Caractere correspondant a l'affichage de la couleur.
         */
        private final String symbol;

        /**
         * Constructeur prive initialisant l'objet.
         * 
         * @param symbol: Caractere correspondant a l'affichage de la couleur.
         */
        private Color(String symbol) {
            this.symbol = symbol;
        }

        /**
         * Redefinis toString pour afficher le symbole de la couleur. 
         */
        @Override
        public String toString() {
            return symbol;
        }
    }

    /**
     * Enumeration contenant tous les rangs de carte d'un jeu de 36 cartes.
     * 
     * @author Antoine Masanet (288366)
     * @author Loïc Houmard (297181)
     *
     */
    public enum Rank {

        SIX("6", 0), SEVEN("7", 1), EIGHT("8", 2), NINE("9", 7), TEN("10",
                3), JACK("J", 8), QUEEN("Q", 4), KING("K", 5), ACE("A", 6);

        /**
         * Nombre de rang d'un jeu de Jass
         */
        public static final int COUNT = 9;

        /**
         * Contient tous les rank possibles
         */
        public static final List<Rank> ALL = Collections
                .unmodifiableList(Arrays.asList(values()));

        /**
         * Correspond au symbole du rang de la carte
         */
        private final String symbol;

        /**
         * Correspond a l'ordre du rang a l'atout
         */
        private final int trumpOrd;

        /**
         * Constructeur privee.
         *  
         * @param symbol: le symbole du rang.
         * @param trumpOrd: l'ordre du rang a l'atout.
         */
        private Rank(String symbol, int trumpOrd) {
            this.symbol = symbol;
            this.trumpOrd = trumpOrd;
        }

        /**
         * Retourne l'ordre du rang a l'atout.
         * 
         * @return l'ordre du rang a l'atout.
         */
        public final int trumpOrdinal() {
            return this.trumpOrd;
        }

        /**
         * Redefinis la methode to String de Object.
         * 
         * @return le symbole de la classe.
         */
        @Override
        public String toString() {
            return symbol;
        }
    }
}
