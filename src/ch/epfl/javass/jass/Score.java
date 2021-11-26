package ch.epfl.javass.jass;

import ch.epfl.javass.Preconditions;

/**
 * Classe immuable representant les scores d'une partie de Jass.
 * @author Antoine Masanet (288366)
 * @author Loïc Houmard (297181)
 *
 */
public final class Score {

    /**
     * Score intial au debut de la partie.
     */
    public final static Score INITIAL = new Score(PackedScore.INITIAL);

    /**
     * Score sous format empaquete.
     */
    private final long packedScore;

    private Score(long packedScore) {
        this.packedScore = packedScore;
    }

    /**
     * Retourne le score correspondant au format empaquete.  
     * 
     * @param packed:Score sous format empaquete.
     * 
     * @throws IllegalArgumentException: Envoie l'exception lorsque le score empaquete est invalide.
     * 
     * @return Un score concu a l'aide du format empaquete.
     */
    public static Score ofPacked(long packed) throws IllegalArgumentException {
        Preconditions.checkArgument(PackedScore.isValid(packed));
        return new Score(packed);
    }

    /**
     * Retourne le score sous format empaquete.
     *  
     * @return Le score sous format empaquete.
     */
    public long packed() {
        return this.packedScore;
    }

    /**
     * Retourne le nombre de plis gagne par l'equipe "t" durant ce tour.
     * 
     * @param t: Le nom de l'equipe.
     * 
     * @return Le nombre de plis gagne par l'equipe "t" durant ce tour.
     */
    public int turnTricks(TeamId t) {
        return PackedScore.turnTricks(packedScore, t);
    }

    /**
     * Retourne le nombre de point gagne par l'equipe "t" durant ce tour.
     * 
     * @param t: Le nom de l'equipe.
     * 
     * @return Le nombre de plis gagne par l'equipe "t" durant ce tour.
     */
    public int turnPoints(TeamId t) {
        return PackedScore.turnPoints(packedScore, t);
    }

    /**
     * Retourne le nombre total de point gagne par l'equipe "t" dans les tours precedents.
     * 
     * @param t: Le nom de l'equipe.
     * 
     * @return: Le nombre total de point gagne par l'equipe "t" dans les tours precedents.
     */
    public int gamePoints(TeamId t) {
        return PackedScore.gamePoints(packedScore, t);
    }

    /**
     * Retourne le nombre total de point gagne par l'equipe "t" avec ce tour inclus.
     * 
     * @param t: Le nom de l'equipe.
     * 
     * @return: Le nombre total de point gagne par l'equipe "t". 
     */
    public int totalPoints(TeamId t) {
        return PackedScore.totalPoints(packedScore, t);
    }

    /**
     * Retourne les scores empaquetés donnés tenant compte du fait que 
     * l'équipe winningTeam a remporté un pli valant trickPoints points.
     * 
     * @param winningTeam: Equipe qui a remporte le pli.
     * @param trickPoints: Valeur du pli.
     * 
     * @throws IllegalArgumentException: Lancee si la valeur du pli est negative.
     * 
     * @return: Le nouveau score mis a jour.
     */
    public Score withAdditionalTrick(TeamId winningTeam, int trickPoints)
            throws IllegalArgumentException {
        Preconditions.checkArgument(trickPoints >= 0);

        return new Score(PackedScore.withAdditionalTrick(packedScore,
                winningTeam, trickPoints));
    }

    /** Retourne le score mis à jour pour le tour prochain.
     * 
     * @return: Le score mis à jour pour le tour prochain.
     */
    public Score nextTurn() {
        return new Score(PackedScore.nextTurn(packedScore));
    }

    /**
     * Redefinis la methode equals dans le cas de deux Scores.
     * 
     * @param: Un autre score.
     * 
     * @return: Renvoie vrai si les deux scores sont egaux et faux sinon.
     */
    @Override
    public boolean equals(Object thatO) {
        if (thatO == null)
            return false;

        else if (!(thatO instanceof Score))
            return false;

        else
            return ((Score) thatO).packed() == this.packedScore;
    }

    /**
     * Redefinis la methode toString de Object.
     * 
     * @return un String contenant une description du score.
     */
    @Override
    public String toString() {
        return PackedScore.toString(packedScore);
    }

    /**
     * Redefinis la methode hashCode de Object.
     * 
     * @return un int correspondant au hashCode du packedScore.
     */
    @Override
    public int hashCode() {
        return Long.hashCode(packedScore);
    }
}
