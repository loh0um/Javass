package ch.epfl.javass.jass;

import java.util.StringJoiner;

import ch.epfl.javass.bits.Bits32;
import ch.epfl.javass.bits.Bits64;

/**
 * Classe non instatiable permettant de manipuler les scores d'un jeu de Jass empaquetées dans 
 * un entier de type long à l'aide de méthodes statiques. 
 * 
 * @author Antoine Masanet (288366)
 * @author Loïc Houmard (297181)
 * 
 */
public final class PackedScore {

    private PackedScore() {
    }

    private final static int TRICK_START = 0;
    private final static int TRICK_SIZE = 4;
    private final static int TURN_POINTS_START = 4;
    private final static int TURN_POINTS_SIZE = 9;
    private final static int GAME_POINTS_START = 13;
    private final static int GAME_POINTS_SIZE = 11;
    private final static int REMAINDER_START = 24;
    private final static int REMAINDER_SIZE = 8;

    /**
     * Constante représentant le nombre de points maximums atteignables en 1 tour (sans compter le match).
     */
    /*
     * Mis en private pour ne pas modifier l'interface, mais pourrait aussi être
     * mis dans l'interface Jass comme public ou protected.
     */
    private final static int MAX_TURN_POINTS = 157;
    /**
     * Constante représentant le nombre de points maximums atteignables en 1 partie.
     */
    /*
     * Mis en private pour ne pas modifier l'interface, mais pourrait aussi être
     * mis dans l'interface Jass comme public ou protected.
     */
    private final static int MAX_GAME_POINTS = 2000;

    /**
     * Contient le score initial d'une partie, dont les six composantes valent 0.
     */
    public static final long INITIAL = 0L;

    /**
     * Contrôle que le long passé en argument représente un score empaqueté valide pour les 2 équipes
     *
     * @param pkScore: La valeur à contrôler.
     *
     * @return Un boolean valant true si l'argument représente bien un score empaqueté valide
     *         pour les 2 équipes
     */
    public static boolean isValid(long pkScore) {
        return (checkFormat((int) pkScore) && checkFormat(
                (int) Bits64.extract(pkScore, Integer.SIZE, Integer.SIZE)));
    }

    /**
     * Contrôle que l'entier passé en argument représente un score empaqueté valide pour une des 2 équipe.
     *
     * @param pkScore: La valeur à contrôler.
     *
     * @return Un boolean valant true si l'argument représente bien un score empaqueté valide
     *         pour une des 2 équipe.
     */
    private static boolean checkFormat(int pkScore) {
        return (Bits32.extract(pkScore, TRICK_START,
                TRICK_SIZE) <= Jass.TRICKS_PER_TURN
                && Bits32.extract(pkScore, TURN_POINTS_START,
                        TURN_POINTS_SIZE) <= MAX_TURN_POINTS
                                + Jass.MATCH_ADDITIONAL_POINTS
                && Bits32.extract(pkScore, GAME_POINTS_START,
                        GAME_POINTS_SIZE) <= MAX_GAME_POINTS
                && Bits32.extract(pkScore, REMAINDER_START,
                        REMAINDER_SIZE) == 0);
    }

    /**
     * Empaquete les 6 composantes d'un score dans un long.
     *
     * @param turnTricks1: Le nombre de plis remportés par la première équipe dans ce tour.
     * @param turnPoints1: Le nombre de points remportés par la première équipe dans ce tour.
     * @param gamePoints1: Le nombre total de points remportés par la première équipe.
     * @param turnTricks2: Le nombre de plis remportés par la deuxième équipe dans ce tour.
     * @param turnPoints2: Le nombre de points remportés par la deuxième équipe dans ce tour.
     * @param gamePoints2: Le nombre total de points remportés par la deuxième équipe.
     *
     * @return  Un long représentant le score sous forme empaquetée.
     */
    public static long pack(int turnTricks1, int turnPoints1, int gamePoints1,
            int turnTricks2, int turnPoints2, int gamePoints2) {
        assert turnTricks1 <= Jass.TRICKS_PER_TURN
                && turnTricks2 <= Jass.TRICKS_PER_TURN
                && turnPoints1 <= MAX_TURN_POINTS + Jass.MATCH_ADDITIONAL_POINTS
                && turnPoints2 <= MAX_TURN_POINTS + Jass.MATCH_ADDITIONAL_POINTS
                && gamePoints1 <= MAX_GAME_POINTS
                && gamePoints2 <= MAX_GAME_POINTS;

        return Bits64.pack(
                Bits32.pack(turnTricks1, TRICK_SIZE, turnPoints1,
                        TURN_POINTS_SIZE, gamePoints1, GAME_POINTS_SIZE),
                Integer.SIZE,
                Bits32.pack(turnTricks2, TRICK_SIZE, turnPoints2,
                        TURN_POINTS_SIZE, gamePoints2, GAME_POINTS_SIZE),
                Integer.SIZE);
    }

    /**
     * Donne le nombre de plis gagnés par l'équipe passée en paramètre.
     *
     * @param pkScore: La valeur empaquetée des scores.
     * @param t: L'équipe.
     *
     * @return Un entier représentant le nombre de plis remportés par l'équipe dans ce tour
     */
    public static int turnTricks(long pkScore, TeamId t) {
        assert isValid(pkScore);
        return t == TeamId.TEAM_1
                ? (int) Bits64.extract(pkScore, TRICK_START, TRICK_SIZE)
                : (int) Bits64.extract(pkScore, TRICK_START + Integer.SIZE,
                        TRICK_SIZE);
    }

    /**
     * Donne le nombre de points gagnés par l'équipe passée en paramètre.
     *
     * @param pkScore: La valeur empaquetée des scores.
     * @param t: L'équipe.
     *
     * @return Un entier représentant le nombre de points remportés par l'équipe dans ce tour
     */
    public static int turnPoints(long pkScore, TeamId t) {
        assert isValid(pkScore);
        return t == TeamId.TEAM_1
                ? (int) Bits64.extract(pkScore, TURN_POINTS_START,
                        TURN_POINTS_SIZE)
                : (int) Bits64.extract(pkScore,
                        TURN_POINTS_START + Integer.SIZE, TURN_POINTS_SIZE);
    }

    /**
     * Donne le nombre de points gagnés par l'équipe passée en paramètre dans les tours précédents
     * (sans inclure le tour courant).
     *
     * @param pkScore: La valeur empaquetée des scores.
     * @param t: L'équipe.
     *
     * @return Un entier représentant le nombre de points remportés par l'équipe dans les
     *         tours précédents (sans inclure le tour courant).
     */
    public static int gamePoints(long pkScore, TeamId t) {
        assert isValid(pkScore);
        return t == TeamId.TEAM_1
                ? (int) Bits64.extract(pkScore, GAME_POINTS_START,
                        GAME_POINTS_SIZE)
                : (int) Bits64.extract(pkScore,
                        GAME_POINTS_START + Integer.SIZE, GAME_POINTS_SIZE);
    }

    /**
     * Donne le nombre de points gagnés par l'équipe passée en paramètre dans les tours précédents
     * et celui en cours.
     *
     * @param pkScore: La valeur empaquetée des scores.
     * @param t: L'équipe.
     *
     * @return Un entier représentant le nombre de points remportés par l'équipe dans les
     *         tours précédents et celui en cours.
     */
    public static int totalPoints(long pkScore, TeamId t) {
        assert isValid(pkScore);
        return gamePoints(pkScore, t) + turnPoints(pkScore, t);
    }

    /**
     * Retourne les scores empaquetés donnés mis à jour après un pli (sans prendre en
     * compte la dernière).
     *
     * @param pkScore: La valeur empaquetée des scores.
     * @param winningTeam: L'équipe qui a remporté le pli.
     * @param trickPoints: Le nombre de points remportés dans le pli.
     * 
     * @return Un long représentant les scores empaquetés mis à jour (en comptant 1 pli de plus et 
     *         trickPoints de plus pour l'équipe gagnante, le nombre de points total n'étant pas mis
     *         à jour).
     */
    public static long withAdditionalTrick(long pkScore, TeamId winningTeam,
            int trickPoints) {

        assert isValid(pkScore);

        int numberOfTricksWinningTeam = turnTricks(pkScore, winningTeam) + 1;
        int turnPointsWinningTeam = turnPoints(pkScore, winningTeam)
                + trickPoints;

        assert numberOfTricksWinningTeam <= Jass.TRICKS_PER_TURN
                && turnPointsWinningTeam <= MAX_TURN_POINTS;

        if (numberOfTricksWinningTeam == Jass.TRICKS_PER_TURN) {
            turnPointsWinningTeam += Jass.MATCH_ADDITIONAL_POINTS;
        }

        int shift = winningTeam == TeamId.TEAM_1 ? 0 : Integer.SIZE;
        // Remplace seulement les bits qui doivent être modifiés, soit entre 0
        // et 12 si la winningTeam est la 1, et sinon entre 32 et 44
        return (pkScore & (~Bits64.mask(TRICK_START + shift,
                TRICK_SIZE + TURN_POINTS_SIZE)))
                | (((long) numberOfTricksWinningTeam) << TRICK_START + shift)
                | (((long) turnPointsWinningTeam) << TURN_POINTS_START + shift);
    }

    /**
     * Retourne les scores empaquetés donnés mis à jour à la fin d'un tour
     *
     * @param pkScore: La valeur empaquetée des scores.
     * 
     * @return Un long représentant les scores empaquetés mis à jour, c'est à dire avec le nombre de plis
     *         de chaque équipe ainsi que leur nombre de points du tour mis à zéro et avec leurs points 
     *         totaux auquels ont été ajoutés les points du tour.
     */
    public static long nextTurn(long pkScore) {
        assert isValid(pkScore);
        return (((long) totalPoints(pkScore,
                TeamId.TEAM_2)) << GAME_POINTS_START + Integer.SIZE)
                | (totalPoints(pkScore, TeamId.TEAM_1) << GAME_POINTS_START);                
    }

    /**
     * Représente un score
     *
     * @param pkScore: La valeur empaquetée des scores.
     * 
     * @return Une représentation des scores (sous forme de String) avec d'abord l'équipe 2
     *         puis l'équipe 1 avec dans l'ordre: le nombre de points totaux, le nombre de 
     *         points du tour et le nombre de plis du tour.
     *         
     */
    public static String toString(long pkScore) {
        assert isValid(pkScore);

        StringJoiner stringTeam1 = new StringJoiner(",", "(", ")");
        stringTeam1.add(String.valueOf(turnTricks(pkScore, TeamId.TEAM_1)))
                .add(String.valueOf(turnPoints(pkScore, TeamId.TEAM_1)))
                .add(String.valueOf(gamePoints(pkScore, TeamId.TEAM_1)));

        StringJoiner stringTeam2 = new StringJoiner(",", "(", ")");
        stringTeam2.add(String.valueOf(turnTricks(pkScore, TeamId.TEAM_2)))
                .add(String.valueOf(turnPoints(pkScore, TeamId.TEAM_2)))
                .add(String.valueOf(gamePoints(pkScore, TeamId.TEAM_2)));

        return stringTeam1.toString() + '/' + stringTeam2.toString();
    }

}
