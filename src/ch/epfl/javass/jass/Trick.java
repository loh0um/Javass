package ch.epfl.javass.jass;

import ch.epfl.javass.Preconditions;
import ch.epfl.javass.jass.Card.Color;

/**
 * Classe representant un pli d'un jeu de Jass.
 * 
 * @author Antoine Masanet (288366)
 * @author Loïc Houmard (297181)
 *
 */
public class Trick {

    /**
     * Represente un pli invalid
     */
    public final static Trick INVALID = new Trick(PackedTrick.INVALID);

    private final int packedTrick;

    private Trick(int pkTrick) {
        this.packedTrick = pkTrick;
    }

    /**
     * Retourne le pli empaqueté vide — c-à-d sans aucune carte — d'index
     * 0 avec l'atout et le premier joueur donnés.
     * 
     * @param trump: La couleur de l'atout.
     * @param firstPlayer: Le premier joueur a jouer.
     * 
     * @return: Le pli empaqueté vide — c-à-d sans aucune carte — d'index
     *          0 avec l'atout et le premier joueur donnés. 
     */
    public static Trick firstEmpty(Color trump, PlayerId firstPlayer) {
        return new Trick(PackedTrick.firstEmpty(trump, firstPlayer));
    }

    /**
     * Retourne le pli dont la version empaquetée est celle donnée, 
     * ou lève IllegalArgumentException si celui-ci n'est pas valide.
     * 
     * @param packed: Version empaquetee d'un pli.
     * 
     * @throws IllegalArgumentException: si le pli empquete n'est pas valide
     * 
     * @return le pli dont la version empaquetée est celle donnée.
     */
    public static Trick ofPacked(int packed) throws IllegalArgumentException {
        Preconditions.checkArgument(PackedTrick.isValid(packed));

        return new Trick(packed);
    }

    /**
     * Retourne la version empaquetee de la carte.
     * 
     * @return la version empaquetee de la carte.
     */
    public int packed() {
        return this.packedTrick;
    }

    /**
     * Retourne le pli empaqueté vide suivant celui donné (supposé plein),
     * c-à-d le pli vide dont l'atout est identique à celui du pli donné, 
     * l'index est le successeur de celui du pli donné et le premier joueur 
     * est le vainqueur du pli donné ; si le pli donné est le dernier du tour, 
     * alors le pli invalide (INVALID ci-dessus) est retourné.
     * 
     * @throws IllegalStateException: Si le pli recu c'est pas plein
     * 
     * @return Le plis empaquete vide correspondant.
     */
    public Trick nextEmpty() throws IllegalStateException {
        if (!PackedTrick.isFull(packedTrick))
            throw new IllegalStateException("Le pli n'est pas plein");

        else
            return new Trick(PackedTrick.nextEmpty(this.packedTrick));
    }

    /**
     * Retourne vrai ssi le pli est vide, c-à-d s'il ne contient aucune carte.
     *  
     * @return Vraie ssi le pli est vide
     */
    public boolean isEmpty() {
        return PackedTrick.isEmpty(this.packedTrick);
    }

    /**
     * Retourne vrai ssi le pli est plein, c-à-d s'il contient 4 cartes.
     * 
     * @return  vrai ssi le pli est plein.
     */
    public boolean isFull() {
        return PackedTrick.isFull(this.packedTrick);
    }

    /**
     * Retourne vrai ssi le pli est le dernier du tour, c-à-d si son index vaut 8.
     * 
     * @return vrai ssi le pli est le dernier du tour.
     */
    public boolean isLast() {
        return PackedTrick.isLast(this.packedTrick);
    }

    /**
     * Retourne la taille du pli, c-à-d le nombre de cartes qu'il contient.
     * 
     * @return la taille du pli.
     */
    public int size() {
        return PackedTrick.size(this.packedTrick);
    }

    /**
     * Retourne l'atout du pli.
     * 
     * 
     * @return l'atout du pli.
     */
    public Color trump() {
        return PackedTrick.trump(this.packedTrick);
    }

    /**
     * Retourne l'index du pli.
     * 
     * @return l'index du pli.
     */
    public int index() {
        return PackedTrick.index(this.packedTrick);
    }

    /**
     * Retourne le joueur d'index donné dans le pli, le joueur d'index 0 étant le premier du pli
     * 
     * @param index: L'index du joueur apres le premier joueur a jouer. 
     * 
     * @throws IndexOutOfBoundsException: si l'index est negatif ou plus grand ou egal a PlayerId.COUNT  
     * 
     * @return le joueur d'index donné dans le pli.
     */
    public PlayerId player(int index) throws IndexOutOfBoundsException {
        Preconditions.checkIndex(index, PlayerId.COUNT);

        return PackedTrick.player(this.packedTrick, index);
    }

    /**
     * Retourne la carte du pli à l'index donné.
     * 
     * @param index: Index de la carte dans l'ordre du pli.
     * 
     * @throws IndexOutOfBoundsException: si l'index est negatif ou plus grand ou egal a la taille du pli.
     * 
     * @return La carte du pli à l'index donné.
     */
    public Card card(int index) throws IndexOutOfBoundsException {
        Preconditions.checkIndex(index, PackedTrick.size(this.packedTrick));

        return Card.ofPacked((PackedTrick.card(this.packedTrick, index)));
    }

    /**
     * Retourne un pli identique à ce pli, 
     * mais auquel la carte donnée a été ajoutée.
     * 
     * @param c: La carte a ajoute.
     * 
     * @throws IllegalStateException: si le pli est plein.
     * 
     * @return Ce pli avec une carte ajoutee.
     */
    public Trick withAddedCard(Card c) throws IllegalStateException {
        if (this.isFull())
            throw new IllegalStateException("Trick should not be full");

        else
            return new Trick(
                    PackedTrick.withAddedCard(this.packedTrick, c.packed()));
    }

    /**
     * Retourne la couleur de base du pli, c-à-d la couleur de sa première carte.
     * 
     * @throws IllegalStateException: si le pli est vide.
     * 
     * @return la couleur de base du pli.
     */
    public Color baseColor() throws IllegalStateException {
        if (this.isEmpty())
            throw new IllegalStateException("Trick should not be empty");

        else
            return PackedTrick.baseColor(this.packedTrick);
    }

    /**
     * Retourne le sous-ensemble des cartes de la main hand 
     * qui peuvent être jouées comme prochaine carte du pli. 
     * 
     * @param hand: Un ensemble de cartes. 
     * 
     * @throws IllegalStateException: si le pli est plein.
     * 
     * @return le sous-ensemble des cartes de la main hand 
     *         qui peuvent être jouées comme prochaine carte du pli.
     * 
     */
    public CardSet playableCards(CardSet hand) throws IllegalStateException {
        if (this.isFull())
            throw new IllegalStateException("Trick should not be full");

        return CardSet.ofPacked(
                PackedTrick.playableCards(this.packedTrick, hand.packed()));
    }

    /**
     * Retourne la valeur du pli, en tenant compte des « 5 de der.
     *  
     * @return la valeur du pli.
     */
    public int points() {
        return PackedTrick.points(this.packedTrick);
    }

    /**
     * Retourne l'identité du joueur menant le pli.
     * 
     * @throws IllegalStateException: si le pli est vide. 
     * 
     * @return l'identité du joueur menant le pli.
     * 
     */
    public PlayerId winningPlayer() throws IllegalStateException {
        if (this.isEmpty())
            throw new IllegalStateException("Trick should not be empty");

        return PackedTrick.winningPlayer(this.packedTrick);
    }

    /**
     * Redefinis la methode equals dans le cas de deux plis.
     * 
     * @param thatO: Un autre pli.
     * 
     * @return Renvoie vrai si les deux plis sont egaux et faux sinon.
     */
    @Override
    public boolean equals(Object thatO) {
        if (thatO == null)
            return false;

        else if (!(thatO instanceof Trick))
            return false;

        else
            return ((Trick) thatO).packed() == this.packedTrick;

    }

    /**
     * Redefinis la methode hashCode de Object. 
     * 
     * @return la version empaquete du pli.
     */
    @Override
    public int hashCode() {
        return this.packedTrick;
    }

    /**
     * Redefinis la methode toString de Object.
     * 
     * @return Un String contenant une description du pli.
     */
    @Override
    public String toString() {
        return PackedTrick.toString(this.packedTrick);
    }
}
