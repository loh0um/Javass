package ch.epfl.javass.jass;

import ch.epfl.javass.bits.Bits32;
import ch.epfl.javass.jass.Card.Color;

/**
 * Classe non instatiable permettant de manipuler les plis empaquetées dans 
 * un entier de type int à l'aide de méthodes statiques. 
 * 
 * @author Antoine Masanet (288366)
 * @author Loïc Houmard (297181)
 * 
 */
public final class PackedTrick {

    private PackedTrick() {
    }

    /**
     * Constante représentant un pli invalide (est contitué de tous des 1).
     */
    public final static int INVALID = ~0; // Tous des 1

    private final static int CARD0_START = 0;
    private final static int CARD_SIZE = 6;
    private final static int INDEX_TRICK_START = 24;
    private final static int INDEX_TRICK_SIZE = 4;
    private final static int FIRST_PLAYER_START = 28;
    private final static int FIRST_PLAYER_SIZE = 2;
    private final static int TRUMP_START = 30;
    private final static int TRUMP_SIZE = 2;

    /**
     * Retourne vrai ssi la valeur donnée représente un 
     * pli empaqueté valide.
     * 
     * @param: pkTrick: Un pli empaqueté.
     * 
     * @return: Vrai si l'index (bits 24 à 27 inclus) est compris entre 0 et 8 (inclus)
     *          et si les éventuelles cartes invalides sont groupées dans
     *          les index supérieurs — c-à-d que le pli ne possède soit aucune 
     *          carte invalide, soit une seule à l'index 3, soit deux aux index
     *          3 et 2, soit trois aux index 3, 2 et 1, soit quatre aux index
     *          3, 2, 1 et 0.
     */
    public static boolean isValid(int pkTrick) {
        int index = Bits32.extract(pkTrick, INDEX_TRICK_START,
                INDEX_TRICK_SIZE);

        int pkCard3 = Bits32.extract(pkTrick, CARD0_START + 3 * CARD_SIZE,
                CARD_SIZE);
        int pkCard2 = Bits32.extract(pkTrick, CARD0_START + 2 * CARD_SIZE,
                CARD_SIZE);
        int pkCard1 = Bits32.extract(pkTrick, CARD0_START + CARD_SIZE,
                CARD_SIZE);
        int pkCard0 = Bits32.extract(pkTrick, CARD0_START, CARD_SIZE);

        boolean card3Invalid = pkCard3 == PackedCard.INVALID;
        boolean card2Invalid = pkCard2 == PackedCard.INVALID;
        boolean card1Invalid = pkCard1 == PackedCard.INVALID;
        boolean card0Invalid = pkCard0 == PackedCard.INVALID;

        // L'expression booléenne pour les cartes invalides a été simplifiée
        // avec les tables de Karnaugh
        return (index <= Jass.TRICKS_PER_TURN - 1 && index >= 0
                && ((card3Invalid && card2Invalid && !card0Invalid)
                        || (card3Invalid && card2Invalid && card1Invalid)
                        || (!card2Invalid && !card1Invalid && !card0Invalid))
                && (PackedCard.isValid(pkCard3) || card3Invalid)
                && (PackedCard.isValid(pkCard2) || card2Invalid)
                && (PackedCard.isValid(pkCard1) || card1Invalid))
                && (PackedCard.isValid(pkCard0) || card0Invalid);
    }

    /**
     * Retourne le premier pli empaqueté vide avec un certain atout et
     * un certain premier joueur.
     * 
     * @param: trump: La couleur atout.
     * @param: firstPlayer: Le premier joueur.
     * 
     * @return: Un entier (int) représentant un pli empaqueté vide avec firstPlayer
     *          comme premier joueur et trump comme couleur d'atout.
     */
    public static int firstEmpty(Color trump, PlayerId firstPlayer) {
        return Bits32.pack(PackedCard.INVALID, CARD_SIZE, PackedCard.INVALID,
                CARD_SIZE, PackedCard.INVALID, CARD_SIZE, PackedCard.INVALID,
                CARD_SIZE, 0, INDEX_TRICK_SIZE, firstPlayer.ordinal(),
                FIRST_PLAYER_SIZE, trump.ordinal(), TRUMP_SIZE);
    }

    /**
     * Retourne le prochain pli empaqueté vide suivant l'actuel supposé plein
     * 
     * @param: pkTrick: Le pli empaqueté.
     * 
     * @return: Un entier (int) représentant le pli empaqueté vide suivant le pli actuel
     */
    public static int nextEmpty(int pkTrick) {
        assert isValid(pkTrick);

        if (isLast(pkTrick)) {
            return INVALID;
        } else {
            return Bits32.pack(PackedCard.INVALID, CARD_SIZE,
                    PackedCard.INVALID, CARD_SIZE, PackedCard.INVALID,
                    CARD_SIZE, PackedCard.INVALID, CARD_SIZE,
                    index(pkTrick) + 1, INDEX_TRICK_SIZE,
                    winningPlayer(pkTrick).ordinal(), FIRST_PLAYER_SIZE,
                    trump(pkTrick).ordinal(), TRUMP_SIZE);
        }
    }

    /**
     * Retourne un booléen indiquant si le pli est le dernier du tour.
     * 
     * @param: pkTrick: Le pli empaqueté.
     * 
     * @return: Vrai ssi l'index du pli (bits 24 à 27 inclus) est égal à 8.
     */
    public static boolean isLast(int pkTrick) {
        assert isValid(pkTrick);
        // -1 car le premier pli est le pli 0
        return index(pkTrick) == Jass.TRICKS_PER_TURN - 1;
    }

    /**
     * Retourne un booléen indiquant si le pli est vide.
     * 
     * @param: pkTrick: Le pli empaqueté.
     * 
     * @return: Vrai ssi le pli est vide, c'est à dire qu'il ne contient aucune carte (que des cartes invalides).
     */
    public static boolean isEmpty(int pkTrick) {
        assert isValid(pkTrick);
        return size(pkTrick) == 0;
    }

    /**
     * Retourne un booléen indiquant si le pli est plein.
     * 
     * @param: pkTrick: Le pli empaqueté.
     * 
     * @return: Vrai ssi le pli est plein, c'est à dire qu'il ne contient
     *          pas de cartes invalides
     */
    public static boolean isFull(int pkTrick) {
        assert isValid(pkTrick);
        return size(pkTrick) == PlayerId.COUNT;
    }

    /**
     * Retourne la taille du pli, c'est à dire le nombre de cartes qu'il contient.
     * 
     * @param: pkTrick: Le pli empaqueté.
     * 
     * @return: Le nombre de cartes contenues dans le pli.
     */
    public static int size(int pkTrick) {
        assert isValid(pkTrick);

        int compteur = PlayerId.COUNT;

        while (compteur > 0 && (Bits32.extract(pkTrick,
                CARD0_START + (compteur - 1) * CARD_SIZE,
                CARD_SIZE)) == PackedCard.INVALID) {
            --compteur;
        }
        return compteur;
    }

    /**
     * Retourne la couleur atout.
     * 
     * @param: pkTrick: Le pli empaqueté.
     * 
     * @return: La couleur atout.
     */
    public static Color trump(int pkTrick) {
        assert isValid(pkTrick);
        return Color.ALL.get(Bits32.extract(pkTrick, TRUMP_START, TRUMP_SIZE));
    }

    /**
     * Retourne le joueur dans le pli d'index donné.
     * 
     * @param: pkTrick: Le pli empaqueté.
     * 
     * @return: Le joueur dans le pli d'index donné, le joueur
     *          d'index 0 étant le premier du pli.
     */
    public static PlayerId player(int pkTrick, int index) {
        assert isValid(pkTrick) && index < PlayerId.COUNT;
        return PlayerId.ALL.get(
                (Bits32.extract(pkTrick, FIRST_PLAYER_START, FIRST_PLAYER_SIZE)
                        + index) % PlayerId.COUNT);
    }

    /**
     * Retourne l'index du pli donné.
     * 
     * @param: pkTrick: Le pli empaqueté.
     * 
     * @return: L'index du pli.
     */
    public static int index(int pkTrick) {
        assert isValid(pkTrick);
        return Bits32.extract(pkTrick, INDEX_TRICK_START, INDEX_TRICK_SIZE);
    }

    /**
     * Retourne la carte d'index donnée.
     * 
     * @param: pkTrick: Le pli empaqueté.
     * @param: index: L'index de la carte.
     * 
     * @return: La carte d'index donnée, supposée avoir été jouée.
     */
    public static int card(int pkTrick, int index) {
        assert isValid(pkTrick) && index < size(pkTrick) && index >= 0;
        return Bits32.extract(pkTrick, CARD0_START + index * CARD_SIZE,
                CARD_SIZE);
    }

    /**
     * Ajoute la carte donnée au pli.
     * 
     * @param: pkTrick: Le pli empaqueté.
     * @param: pkCard: La carte empaquetée à ajouter au pli.
     * 
     * @return: Le pli empaquetée auquel la carte empaquetée a été ajoutée.
     */
    public static int withAddedCard(int pkTrick, int pkCard) {
        assert isValid(pkTrick) && PackedCard.isValid(pkCard)
                && !isFull(pkTrick);

        int trickSize = size(pkTrick);

        return (pkTrick & (~(Bits32.mask(CARD0_START + trickSize * CARD_SIZE,
                CARD_SIZE))) | pkCard << ((trickSize) * CARD_SIZE));

    }

    /**
     * Retourne la couleur de base du pli.
     * 
     * @param: pkTrick: Le pli empaqueté.
     * 
     * @return: La couleur de base du pli, c'est à dire la couleur de la première
     *          carte (supposée avoir été jouée).
     */
    public static Color baseColor(int pkTrick) {
        assert isValid(pkTrick);
        return PackedCard.color(card(pkTrick, CARD0_START));
    }

    /**
     * Retourne les cartes jouables par le prochain joueur du pli.
     * 
     * @param: pkTrick: Le pli empaqueté.
     * @param: pkHand: L'ensemble empaqueté des cartes de la main.
     * 
     * @return: Le sous-ensemble (empaqueté) des cartes de la main pkHand qui
     *          peuvent être jouées comme prochaine carte du pli pkTrick (supposé
     *          non plein)
     */
    public static long playableCards(int pkTrick, long pkHand) {
        assert isValid(pkTrick) && PackedCardSet.isValid(pkHand)
                && !isFull(pkTrick);

        // Aucune carte n'a été jouée jusqu'à présent
        if (isEmpty(pkTrick)) {
            return pkHand;
        } else {
            Color baseColor = baseColor(pkTrick);
            Color trump = trump(pkTrick);
            int packedBourg = PackedCard.pack(trump, Card.Rank.JACK);
            long trumpCardsInHand = PackedCardSet.subsetOfColor(pkHand, trump);

            // Couleur de base Atout
            if (baseColor.equals(trump)) {
                // La main ne contient pas d'atout ou que le bourg
                if (PackedCardSet.isEmpty(trumpCardsInHand) || PackedCardSet
                        .isEmpty(PackedCardSet.difference(trumpCardsInHand,
                                PackedCardSet.singleton(packedBourg)))) {
                    return pkHand;

                }
                // La main contient au moins 1 atout (différent du bourg)
                else {
                    return trumpCardsInHand;
                }
            }
            // Couleur de base non atout
            else {
                long baseCardsInHand = PackedCardSet.subsetOfColor(pkHand,
                        baseColor);
                int packedWinningCard = card(pkTrick,
                        indexWinningCard(pkTrick));
                boolean noBaseCardInHand = PackedCardSet
                        .isEmpty(baseCardsInHand);
                // Aucun atout n'a été joué jusqu'à maintant
                if (!PackedCard.color(packedWinningCard).equals(trump)) {
                    // Le joueur possède au moins une carte de base
                    if (!noBaseCardInHand) {
                        return PackedCardSet.union(baseCardsInHand,
                                trumpCardsInHand);
                    }
                    // Le joueur ne possède pas de cartes de bases
                    else {
                        return pkHand;
                    }
                }
                // Un atout a déjà été joué (coupé)
                else {
                    long trumpBetterInHand = PackedCardSet.intersection(
                            PackedCardSet.trumpAbove(packedWinningCard),
                            trumpCardsInHand);
                    long trumpWorseInHand = PackedCardSet
                            .difference(trumpCardsInHand, trumpBetterInHand);
                    // Le joueur possède au moins une carte de base
                    if (!noBaseCardInHand) {
                        return PackedCardSet.union(baseCardsInHand,
                                trumpBetterInHand);
                    }
                    // Le joueur ne possède pas de cartes de base
                    else {
                        long cardsInHandWithoutWorseTrump = PackedCardSet
                                .difference(pkHand, trumpWorseInHand);
                        // Le joueur a d'autres choix que sous-couper
                        if (!PackedCardSet
                                .isEmpty(cardsInHandWithoutWorseTrump)) {
                            return cardsInHandWithoutWorseTrump;
                        }
                        // Le joueur est contraint de sous-couper (pas d'autres
                        // choix)
                        else {
                            return pkHand;
                        }
                    }
                }
            }
        }

    }

    /**
     * Retourne le nombre de points du pli.
     * 
     * @param: pkTrick: Le pli empaqueté.
     * 
     * @return: Le nombre de points du pli en prenant en compte les 5 de la dernière.
     */
    public static int points(int pkTrick) {
        assert isValid(pkTrick);

        Color trump = trump(pkTrick);
        int pointsTot = isLast(pkTrick) ? Jass.LAST_TRICK_ADDITIONAL_POINTS : 0;

        for (int i = 0; i < size(pkTrick); ++i) {
            pointsTot += PackedCard.points(trump, card(pkTrick, i));
        }

        return pointsTot;
    }

    /**
     * Retourne le gagnant du pli.
     * 
     * @param: pkTrick: Le pli empaqueté.
     * 
     * @return: Le joueur menant le pli.
     */
    public static PlayerId winningPlayer(int pkTrick) {
        assert isValid(pkTrick) && !isEmpty(pkTrick);

        return PlayerId.ALL
                .get((player(pkTrick, 0).ordinal() + indexWinningCard(pkTrick))
                        % PlayerId.COUNT);
    }

    /**
     * Retourne l'index de la carte menant le pli.
     * 
     * @param: pkTrick: Le pli empaqueté.
     * 
     * @return: L'index de la carte menant le pli.
     */
    private static int indexWinningCard(int pkTrick) {

        int pkWinningCard = card(pkTrick, 0); // On prend la première carte
                                              // comme gagnante provisoire, que
                                              // l'on va comparer aux autres
        int indexWinCard = 0;
        Color colorWinningCard = PackedCard.color(pkWinningCard);

        Color trump = trump(pkTrick);

        for (int i = 1; i < size(pkTrick); ++i) {

            int pkNextCard = card(pkTrick, i);
            Color colorNextCard = PackedCard.color(pkNextCard);

            if (colorNextCard.equals(colorWinningCard)
                    || colorNextCard.equals(trump)) {
                if (!PackedCard.isBetter(trump, pkWinningCard, pkNextCard)) {
                    pkWinningCard = pkNextCard;
                    indexWinCard = i;
                    colorWinningCard = PackedCard.color(pkWinningCard);
                }
            }
        }
        return indexWinCard;
    }

    /**
     * Renvoie un String contenant une description d'un pli.
     * 
     * @param: pkTrick: Le pli empaqueté.
     * 
     * @return Une description du pli (String) avec la couleur d'atout, le premier
     *         joueur, l'index du pli ainsi que les 4 cartes jouées.
     */
    public static String toString(int pkTrick) {
        assert isValid(pkTrick);

        StringBuilder builder = new StringBuilder();
        builder.append("Atout: ").append(trump(pkTrick).toString())
                .append(", premier joueuer: ")
                .append(player(pkTrick, 0).toString())
                .append(", numéro du pli: ").append(index(pkTrick));

        for (int i = 0; i < PlayerId.COUNT; ++i) {
            builder.append("\nCarte ").append(i).append(": ");
            builder.append(
                    i < size(pkTrick) ? PackedCard.toString(card(pkTrick, i))
                            : "");
        }

        return builder.toString();
    }
}
