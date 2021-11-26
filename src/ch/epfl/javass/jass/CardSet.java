package ch.epfl.javass.jass;

import java.util.List;

import ch.epfl.javass.Preconditions;

/**
 * Classe représentant un ensemble de carte. 
 * 
 * @author Antoine Masanet (288366)
 * @author Loïc Houmard (297181)
 * 
 */
public final class CardSet {

    /**
     * Ensemble de cartes vide.
     */
    public final static CardSet EMPTY = new CardSet(PackedCardSet.EMPTY);
    /**
     * Ensemble de cartes contenant toutes les cartes.
     */
    public final static CardSet ALL_CARDS = new CardSet(
            PackedCardSet.ALL_CARDS);

    /**
     * Version empaquetée d'un ensemble de cartes.
     */
    private final long packedCardSet;

    private CardSet(long packedCardSet) {
        this.packedCardSet = packedCardSet;
    }

    /**
     * Retourne un ensemble de cartes contenant la liste de cartes données.
     *
     * @param cards: La liste de cartes.
     *
     * @return Un ensemble de cartes (CardSet) contenant toutes les cartes de la liste
     *         passée en argument.
     */
    public static CardSet of(List<Card> cards) {

        long pkCardSet = PackedCardSet.EMPTY;

        for (Card card : cards) {
            pkCardSet = PackedCardSet.add(pkCardSet, card.packed());
        }

        return new CardSet(pkCardSet);
    }

    /**
     * Retourne l'ensemble de cartes correspondant à l'ensemble de cartes empaqueté donné.
     *
     * @param packed: Version empaquetée de l'ensemble de cartes.
     *
     * @throws IllegalArgumentException si la version empaquetée de l'ensemble de carte
     *         n'est pas valide.
     * 
     * @return Un ensemble de cartes (CardSet) correspondant à la version empaquetée
     *         donnée.
     */
    public static CardSet ofPacked(long packed)
            throws IllegalArgumentException {

        Preconditions.checkArgument(PackedCardSet.isValid(packed));
        return new CardSet(packed);
    }

    /**
     * Retourne la version empaquetée.
     * 
     * @return La version empaquetée de l'ensemble de cartes.
     */
    public long packed() {
        return this.packedCardSet;
    }

    /**
     * Retourne vrai ssi l'ensemble de cartes donné est vide.
     * 
     * @return Vrai ssi l'ensemble de cartes  donné est vide.
     */
    public boolean isEmpty() {
        return PackedCardSet.isEmpty(this.packedCardSet);
    }

    /**
     * Retourne le nombre de cartes que contient l'ensemble.
     * 
     * @return: le nombre de cartes que contient l'ensemble.
     */
    public int size() {
        return PackedCardSet.size(this.packedCardSet);
    }

    /**
     * Retourne la carte d'index donné.
     * 
     * @param index: Index de la carte en partant de la droite 
     * 
     * @return La carte d'index donné.
     */
    public Card get(int index) {
        return Card.ofPacked(PackedCardSet.get(this.packedCardSet, index));
    }

    /**
     * Retourne l'ensemble de cartes auquel la carte  donnée a été ajoutée.
     * 
     * @param: card: La carte à ajouter. 
     * 
     * @return: l'ensemble de cartes auquel la carte donnée a été ajoutée.
     */
    public CardSet add(Card card) {
        return new CardSet(
                PackedCardSet.add(this.packedCardSet, card.packed()));
    }

    /**
     * Retourne l'ensemble de cartes auquel la carte  donnée a été enlevée.
     * 
     * @param: card: La carte à enlever. 
     * 
     * @return: L'ensemble de cartes duquel la carte donnée a été supprimée.
     */
    public CardSet remove(Card card) {
        return new CardSet(
                PackedCardSet.remove(this.packedCardSet, card.packed()));
    }

    /**
     * Retourne vrai ssi l'ensemble de cartes contient la carte donnée.
     * 
     * @param: card: La carte. 
     * 
     * @return: vrai ssi l'ensemble de cartes contient la carte donnée.
     */
    public boolean contains(Card card) {
        return PackedCardSet.contains(this.packedCardSet, card.packed());
    }

    /**
     * Retourne le complément de l'ensemble de cartes.
     * 
     * @return: le complément de l'ensemble de cartes. 
     */
    public CardSet complement() {
        return new CardSet(PackedCardSet.complement(this.packedCardSet));
    }

    /**
     * Retourne l'union de l'ensemble de carte présent avec celui passé en argument.
     * 
     * @param: that: L'ensemble de cartes. 
     * 
     * @return: L'union avec l'ensemble de cartes passé en argument.
     */
    public CardSet union(CardSet that) {
        return new CardSet(
                PackedCardSet.union(this.packedCardSet, that.packed()));
    }

    /**
     * Retourne l'intersection de l'ensemble de carte présent avec celui passé en argument.
     * 
     * @param: that: L'ensemble de cartes. 
     * 
     * @return: L'intersection avec l'ensemble de cartes passé en argument.
     */
    public CardSet intersection(CardSet that) {
        return new CardSet(
                PackedCardSet.intersection(this.packedCardSet, that.packed()));
    }

    /**
     * Retourne la différence de l'ensemble de carte présent avec celui passé en argument.
     * 
     * @param: that: L'ensemble de cartes. 
     * 
     * @return: La différence avec l'ensemble de cartes passé en argument.
     */
    public CardSet difference(CardSet that) {
        return new CardSet(
                PackedCardSet.difference(this.packedCardSet, that.packed()));
    }

    /**
     * Retourne le sous-ensemble de l'ensemble de cartes constitué uniquement
     * des cartes de la couleur donnée.
     * 
     * @param: color: La couleur demandée.
     * 
     * @return: Le sous-ensemble de l'ensemble de cartes constitué uniquement
     *          des cartes de la couleur donnée.
     */
    public CardSet subsetOfColor(Card.Color color) {
        return new CardSet(
                PackedCardSet.subsetOfColor(this.packedCardSet, color));
    }

    /**
     * Redefinis la methode equals dans le cas de deux ensembles de cartes.
     * 
     * @param: Un autre ensemble de cartes.
     * 
     * @return: Renvoie vrai si les deux ensembles de cartes sont egaux et faux sinon.
     */
    @Override
    public boolean equals(Object thatO) {
        if (thatO == null)
            return false;

        else if (!(thatO instanceof CardSet))
            return false;

        else
            return ((CardSet) thatO).packed() == this.packedCardSet;
    }

    /**
     * Redefinis la methode hashCode de Object.
     * 
     * @return Un int correspondant au hashCode de la version empaquetée de l'ensemble.
     *         de cartes.
     */
    @Override
    public int hashCode() {
        return Long.hashCode(this.packedCardSet);
    }

    /**
     * Redefinis la methode toString de Object.
     * 
     * @return un String contenant une description de l'ensemble de cartes.
     */
    @Override
    public String toString() {
        return PackedCardSet.toString(this.packedCardSet);
    }

}
