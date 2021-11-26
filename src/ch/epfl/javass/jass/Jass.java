package ch.epfl.javass.jass;

/**
 * Interface donnant certaines constantes utiles pour un jeu de jass.
 *
 * @author Antoine Masanet (288366)
 * @author Loïc Houmard (297181)
 */
public interface Jass {

    /**
     * Nombre de cartes d'une main au début d'un tour.
     */
    public final static int HAND_SIZE = 9;

    /**
     * Nombre de plis dans un tour de jeu.
     */
    public final static int TRICKS_PER_TURN = 9;

    /**
     * Nombre de points requis pour gagner une partie.
     */
    public final static int WINNING_POINTS = 1000;

    /**
     * Points additionnels remportés en cas de match (tous les plis d'un tour de jeu).
     */
    public final static int MATCH_ADDITIONAL_POINTS = 100;

    /**
     * Points additionnels remportés par l'équipe ayant le dernier pli.
     */
    public final static int LAST_TRICK_ADDITIONAL_POINTS = 5;

}