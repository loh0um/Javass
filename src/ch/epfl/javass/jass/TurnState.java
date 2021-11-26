package ch.epfl.javass.jass;

import ch.epfl.javass.Preconditions;
import ch.epfl.javass.jass.Card.Color;

/**
 * Classe publique final immuable representant l'etat d'un tour de jeu.
 *
 * @author Antoine Masanet (288366)
 * @author Loïc Houmard (297181)
 *
 */
public final class TurnState {

    /**
     * Version empaquetee du score actuel.
     */
    private final long pkScore;

    /**
     * Version empaquetee de l'ensemble des cartes non jouees.
     */
    private final long pkUnplayedCards;

    /**
     * Version empaquetee du pli actuel.
     */
    private final int pkTrick;

    /**
     * Constructeur prive d'un turnstate
     * 
     * @param pkScore: Score empaquete.
     * @param pkUnplayedCards: Ensemble des cartes non jouees.
     * @param pkTrick:Plis empaquete.
     */
    private TurnState(long pkScore, long pkUnplayedCards, int pkTrick) {
        this.pkScore = pkScore;
        this.pkUnplayedCards = pkUnplayedCards;
        this.pkTrick = pkTrick;
    }

    /**
     * Cree un nouveau turn state correspondant aux infos fournies en entree.
     * 
     * @param trump : La couleur de l'atout.
     * @param score : Le score initial.
     * @param firstPlayer : Le premier joueur a poser une carte au debut du tours.
     * 
     * @return : Un nouveau TurnState correspondant a cette situation.
     */
    public static TurnState initial(Color trump, Score score,
            PlayerId firstPlayer) {
        return new TurnState(score.packed(), PackedCardSet.ALL_CARDS,
                PackedTrick.firstEmpty(trump, firstPlayer));
    }

    /**
     * Cree un nouveau turn state correspondant aux infos fournies en entree.
     * 
     * @param pkScore : Un score empaquete.
     * @param pkUnplayedCards : L'ensemble des cartes n'ayants pas ete jouees.
     * @param pkTrick : L'etat du pli.
     * 
     * @throws IllegalArgumentException : Quand un des arguemnt fourni en entree est invalide.
     * 
     * @return :  Un nouveau TurnState correspondant a cette situation.
     */
    public static TurnState ofPackedComponents(long pkScore,
            long pkUnplayedCards, int pkTrick) throws IllegalArgumentException {
        Preconditions.checkArgument(PackedScore.isValid(pkScore)
                && PackedCardSet.isValid(pkUnplayedCards)
                && PackedTrick.isValid(pkTrick));

        return new TurnState(pkScore, pkUnplayedCards, pkTrick);
    }

    /**
     * Renvoie le score empaquete de ce tour.
     * 
     * @return : Le score empaquete de ce tour.
     */
    public long packedScore() {
        return this.pkScore;
    }

    /**
     * Renvoie l'ensemble empaquete des cartes non jouees durant ce tour de ce tour.
     * 
     * @return: L'ensemble empaquete des cartes non jouees durant ce tour de ce tour.
     */
    public long packedUnplayedCards() {
        return this.pkUnplayedCards;
    }

    /**
     * Renvoie le plis empaquete courant du tour.
     * 
     * @return : Le plis empaquete courant du tour.
     */
    public int packedTrick() {
        return this.pkTrick;
    }

    /**
     * Renvoie le score de ce tour.
     * 
     * @return: Le score de ce tour.
     */
    public Score score() {
        return Score.ofPacked(this.pkScore);
    }

    /**
     * Renvoie l'ensemble des cartes non jouees durant ce tour de ce tour.
     * 
     * @return: L'ensemble des cartes non jouees durant ce tour de ce tour.
     */
    public CardSet unplayedCards() {
        return CardSet.ofPacked(this.pkUnplayedCards);
    }

    /**
     * Renvoie le plis courant du tour.
     * 
     * @return: Le plis empaquete courant du tour.
     */
    public Trick trick() {
        return Trick.ofPacked(this.pkTrick);
    }

    /**
     * Retourne vrai ssi l'état est terminal, c-à-d si le dernier pli du tour a été joué.
     * 
     * @return: Vraie ssi l'état est terminal.
     */
    public boolean isTerminal() {
        return this.packedTrick() == PackedTrick.INVALID;
    }

    /**
     * Retourne l'identité du joueur devant jouer la prochaine carte, 
     * ou lève l'exception IllegalStateException si le pli courant est plein.
     * 
     * @throws: IllegalStateException: si le pli courant est plein.
     * 
     * @return: L'identité du joueur devant jouer la prochaine carte.
     */
    public PlayerId nextPlayer() throws IllegalStateException {
        if (PackedTrick.isFull(this.pkTrick))
            throw new IllegalStateException("The trick should not be full");

        return PackedTrick.player(pkTrick, PackedTrick.size(pkTrick));
    }

    /**
     * Retourne l'état correspondant à celui auquel on l'applique après que 
     * le prochain joueur ait joué la carte donnée, ou lève IllegalStateException 
     * si le pli courant est plein.
     * 
     * @param card: La carte ayant ete jouee.
     * 
     * @throws IllegalStateException: si le pli courant est plein.
     * 
     * @return : L'état correspondant à celui auquel on l'applique après que 
     *           le prochain joueur ait joué la carte donnée.
     */
    public TurnState withNewCardPlayed(Card card) throws IllegalStateException {
        if (PackedTrick.isFull(this.pkTrick))
            throw new IllegalStateException("The trick should not be full");

        return new TurnState(this.pkScore,
                PackedCardSet.remove(this.pkUnplayedCards, card.packed()),
                PackedTrick.withAddedCard(this.pkTrick, card.packed()));
    }

    /**
     * Retourne l'état correspondant à celui auquel on l'applique après que le pli courant ait été ramassé, 
     * ou lève IllegalStateException si le pli courant n'est pas terminé (c-à-d plein).
     * 
     * @throws: IllegalStateException: si le pli courant est plein.
     * 
     * @return:  L'état correspondant à celui auquel on l'applique après que le pli courant ait été ramassé.
     */
    public TurnState withTrickCollected() throws IllegalStateException {

        if (!PackedTrick.isFull(this.pkTrick))
            throw new IllegalStateException("The trick should be full");

        return new TurnState(
                PackedScore.withAdditionalTrick(this.pkScore,
                        PackedTrick.winningPlayer(this.pkTrick).team(),
                        PackedTrick.points(this.pkTrick)),
                pkUnplayedCards, PackedTrick.nextEmpty(this.pkTrick));

    }

    /**
     * Retourne l'état correspondant à celui auquel on l'applique après que le prochain joueur 
     * ait joué la carte donnée, et que le pli courant ait été ramassé s'il est alors plein ; 
     * lève IllegalStateException si le pli courant est plein.
     * 
     * @param card: La carte jouee par le joueur.
     * 
     * @throws IllegalStateException:si le pli courant est plein.
     * 
     * @return: L'état correspondant à celui auquel on l'applique après que le prochain joueur 
     *          ait joué la carte donnée, et que le pli courant ait été ramassé s'il est alors plein.
     * 
     */
    public TurnState withNewCardPlayedAndTrickCollected(Card card)
            throws IllegalStateException {
        TurnState withTrickNotCollected = withNewCardPlayed(card);

        return PackedTrick.isFull(withTrickNotCollected.packedTrick())
                ? withTrickNotCollected.withTrickCollected()
                : withTrickNotCollected;

    }
}
