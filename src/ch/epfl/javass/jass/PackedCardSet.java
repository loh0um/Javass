package ch.epfl.javass.jass;

import java.util.StringJoiner;

import ch.epfl.javass.jass.Card.Color;
import ch.epfl.javass.jass.Card.Rank;

/**
 * @author Antoine Masanet (288366)
 * @author Loïc Houmard (297181)
 *
 */
public final class PackedCardSet

{
    /**
     * L'ensemble de cartes vide.
     */
    public static final long EMPTY = 0L;

    /**
     * L'ensemble des 36 cartes du jeu de Jass.
     */
    public static final long ALL_CARDS = 0b00000001_11111111_00000001_11111111_00000001_11111111_00000001_11111111L;

    private static final int SUBSETOFCOLORLENGTH = Long.SIZE / Color.COUNT;
    private static final long ALL_CARD_OF_ONE_COLOR = 0b00000001_11111111L;

    // Emsemble de toutes les acrtes de pique,coeur,carreau,trefle
    private static long[] subsetOfColorArray = { 0b00000001_11111111L,
            ALL_CARD_OF_ONE_COLOR << SUBSETOFCOLORLENGTH,
            ALL_CARD_OF_ONE_COLOR << 2 * SUBSETOFCOLORLENGTH,
            ALL_CARD_OF_ONE_COLOR << 3 * SUBSETOFCOLORLENGTH };

    private static long[] trumpAboveArray = initializeTrumpAboveArray();

    /**
     * Constructeur vide pour classe non instantiable.
     */
    private PackedCardSet() {
    }

    /**
     * Initialise le tableau trumpAboveArray avec les bon cardSet a l'aide de
     * la methode isBetter
     * 
     * @return: un tableau correspondant a trumpAboveArray
     */
    private static long[] initializeTrumpAboveArray() {
        long array[] = new long[Long.SIZE];

        for (int i = 0; i < Color.COUNT; ++i) {
            Color trump = Color.ALL.get(i);

            for (int j = 0; j < SUBSETOFCOLORLENGTH; ++j) {

                if (j % SUBSETOFCOLORLENGTH >= Rank.COUNT)
                    // Toute les cartes sont meilleurs qu'une carte invalide

                    array[i * SUBSETOFCOLORLENGTH + j] = ALL_CARDS;
                else {
                    long betterCardsSet = PackedCardSet.EMPTY;
                    // Carte pour laquelle on doit determiner le trumpAbove
                    // packedCardSet
                    int thisCard = PackedCard.pack(Color.ALL.get(i),
                            Rank.ALL.get(j));

                    for (int k = 0; k < Rank.COUNT; ++k) {

                        // On ne doit pas comparer la carte avec elle meme
                        if (k != j) {
                            // Carte dont nous voulons determiner si elle est
                            // plus forte a l'atout que thisCard
                            int otherCard = PackedCard.pack(Color.ALL.get(i),
                                    Rank.ALL.get(k));

                            // Si otherCard est meilleure, on la rajoute au
                            // trumpAbove cardSet de thisCard
                            if (PackedCard.isBetter(trump, otherCard,
                                    thisCard)) {
                                betterCardsSet = PackedCardSet
                                        .add(betterCardsSet, otherCard);
                            }
                        }
                    }
                    array[i * SUBSETOFCOLORLENGTH + j] = betterCardsSet;
                }
            }
        }
        return array;
    }

    /**
     * Retourne vrai ssi la valeur donnée représente un 
     * ensemble de cartes empaqueté valide.
     * 
     * @param: pkCardSet: un ensemble de cartes empaqueté.
     * 
     * @return: vrai si aucun des 28 bits inutilisés ne vaut 1.
     */
    public static boolean isValid(long pkCardSet) {
        return (pkCardSet | ALL_CARDS) == ALL_CARDS;
    }

    /**
     * Retourne l'ensemble des cartes strictement plus fortes que 
     * la carte empaquetée donnée, sachant qu'il s'agit d'une carte d'atout.
     * 
     * @param: pkCard: une carte empaquete.
     * 
     * @return: l'ensemble des cartes strictement plus fortes que 
     *          la carte empaquetée donnée, sachant qu'il s'agit d'une carte d'atout.
     */
    public static long trumpAbove(int pkCard) {
        assert PackedCard.isValid(pkCard);
        return trumpAboveArray[pkCard];
    }

    /**
     * Retourne l'ensemble de cartes empaqueté contenant uniquement la carte empaquetée donnée
     * 
     * @param: pkCard: une carte empaquete.
     * 
     * @return: l'ensemble de cartes empaqueté contenant uniquement la carte empaquetée donnée
     */
    public static long singleton(int pkCard) {
        assert (PackedCard.isValid(pkCard));
        return EMPTY | (1L << pkCard);
    }

    /**
     * Retourne vrai ssi l'ensemble de cartes empaqueté donné est vide.
     * 
     * @param pkCardSet: un ensemble de cartes empaqueté.
     * 
     * @return vrai ssi l'ensemble de cartes empaqueté donné est vide
     */
    public static boolean isEmpty(long pkCardSet) {
        assert (PackedCardSet.isValid(pkCardSet));

        return pkCardSet == EMPTY;
    }

    /**
     * Retourne le nombre de cartes que contient l'ensemble.
     * 
     * @param pkCardSet:un ensemble de cartes empaqueté.
     * 
     * @return: le nombre de cartes que contient l'ensemble.
     */
    public static int size(long pkCardSet) {
        assert (PackedCardSet.isValid(pkCardSet));

        return Long.bitCount(pkCardSet);
    }

    /**
     * Retourne la version empaquetée de la carte d'index donné.
     * 
     * @param pkCardSet: un ensemble de cartes empaqueté.
     * @param index: Index de la carte en partant de la droite 
     * 
     * @return: la version empaquetée de la carte d'index donné.
     */
    public static int get(long pkCardSet, int index) {
        assert (PackedCardSet.isValid(pkCardSet)
                && index < PackedCardSet.size(pkCardSet));

        for (int i = 0; i < index; ++i) {
            // Supprime le 1 plus a droite
            pkCardSet = pkCardSet & ~(Long.lowestOneBit(pkCardSet));
        }

        return Long.numberOfTrailingZeros(pkCardSet);
    }

    /**
     * Retourne l'ensemble de cartes empaqueté donné auquel 
     * la carte empaquetée donnée a été ajoutée.
     * 
     * @param: pkCardSet: un ensemble de cartes empaqueté.
     * @param: pkCard:une carte empaquete. 
     * 
     * @return: l'ensemble de cartes empaqueté donné auquel 
     *          la carte empaquetée donnée a été ajoutée.
     */
    public static long add(long pkCardSet, int pkCard) {
        assert (PackedCardSet.isValid(pkCardSet) && PackedCard.isValid(pkCard));

        return (pkCardSet | (1L << pkCard));
    }

    /**
     * Retourne l'ensemble de cartes empaqueté donné duquel 
     * la carte empaquetée donnée a été supprimée.
     * 
     * @param: pkCardSet: un ensemble de cartes empaqueté. 
     * @param: pkCard: une carte empaquete. 
     * 
     * @return: l'ensemble de cartes empaqueté donné duquel 
     *          la carte empaquetée donnée a été supprimée.
     */
    public static long remove(long pkCardSet, int pkCard) {
        assert (PackedCardSet.isValid(pkCardSet) && PackedCard.isValid(pkCard));

        return (pkCardSet & ~(1L << pkCard));
    }

    /**
     * Retourne vrai ssi l'ensemble de cartes empaqueté donné contient 
     * la carte empaquetée donnée.
     * 
     * @param: pkCardSet: un ensemble de cartes empaqueté. 
     * @param: pkCard: une carte empaquete. 
     * 
     * @return: vrai ssi l'ensemble de cartes empaqueté donné contient 
     *          la carte empaquetée donnée.
     */
    public static boolean contains(long pkCardSet, int pkCard) {
        assert (PackedCardSet.isValid(pkCardSet) && PackedCard.isValid(pkCard));

        return ((pkCardSet & (1L << pkCard)) != 0);

    }

    /**
     * Retourne le complément de l'ensemble de cartes empaqueté donné.
     * 
     * @param: pkCardSet: un ensemble de cartes empaqueté.
     * 
     * @return: le complément de l'ensemble de cartes empaqueté donné. 
     */
    public static long complement(long pkCardSet) {
        assert (PackedCardSet.isValid(pkCardSet));

        return pkCardSet ^ ALL_CARDS;
    }

    /**
     * Retourne l'union des deux ensembles de cartes empaquetés donnés.
     * 
     * @param: pkCardSet1: un ensemble de cartes empaqueté. 
     * @param: pkCardSet2: un ensemble de cartes empaqueté.
     * 
     * @return: l'union des deux ensembles de cartes empaquetés donnés.
     */
    public static long union(long pkCardSet1, long pkCardSet2) {
        assert (PackedCardSet.isValid(pkCardSet1)
                && PackedCardSet.isValid(pkCardSet2));

        return pkCardSet1 | pkCardSet2;
    }

    /**
     * Retourne l'intersection des deux ensembles de cartes empaquetés donnés.
     * 
     * @param: pkCardSet1: un ensemble de cartes empaqueté. 
     * @param: pkCardSet2: un ensemble de cartes empaqueté.
     * 
     * @return: l'intersection des deux ensembles de cartes empaquetés donnés. 
     */
    public static long intersection(long pkCardSet1, long pkCardSet2) {
        assert (PackedCardSet.isValid(pkCardSet1)
                && PackedCardSet.isValid(pkCardSet2));

        return pkCardSet1 & pkCardSet2;
    }

    /**
     * Retourne l'ensemble des cartes qui se trouvent dans le premier
     * ensemble mais pas dans le second.
     * 
     * @param: pkCardSet1: un ensemble de cartes empaqueté. 
     * @param: pkCardSet2: un ensemble de cartes empaqueté.
     * 
     * @return: l'ensemble des cartes qui se trouvent dans le premier
     *          ensemble mais pas dans le second.
     */
    public static long difference(long pkCardSet1, long pkCardSet2) {
        assert (PackedCardSet.isValid(pkCardSet1)
                && PackedCardSet.isValid(pkCardSet2));

        return pkCardSet1 ^ intersection(pkCardSet1, pkCardSet2);
    }

    /**
     * Retourne le sous-ensemble de l'ensemble de cartes empaqueté donné 
     * constitué uniquement des cartes de la couleur donnée.
     * 
     * @param: pkCardSet: un ensemble de cartes empaqueté. 
     * @param: color: La couleur demandee.
     * 
     * @return: le sous-ensemble de l'ensemble de cartes empaqueté donné 
     *          constitué uniquement des cartes de la couleur donnée.
     */
    public static long subsetOfColor(long pkCardSet, Card.Color color) {
        assert PackedCardSet.isValid(pkCardSet);

        return pkCardSet & subsetOfColorArray[color.ordinal()];
    }

    /**
     * Retourne la représentation textuelle de l'ensemble de cartes empaqueté donné.
     * 
     * @param: pkCardSet: un ensemble de cartes empaqueté.
     * 
     * @return: la représentation textuelle de l'ensemble de cartes empaqueté donné.
     */
    public static String toString(long pkCardSet) {
        assert (PackedCardSet.isValid(pkCardSet));

        StringJoiner j = new StringJoiner(",", "{", "}");

        for (int pkCard = 0; pkCard < Long.SIZE; ++pkCard) {
            if (PackedCard.isValid(pkCard)
                    && PackedCardSet.contains(pkCardSet, pkCard)) {
                j.add(PackedCard.toString(pkCard));
            }
        }

        return j.toString();
    }
}
