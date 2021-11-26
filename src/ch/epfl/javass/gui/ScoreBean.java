package ch.epfl.javass.gui;

import ch.epfl.javass.jass.TeamId;
import javafx.beans.property.ReadOnlyIntegerProperty;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;

/**
 * Classe contentant les proprietes d'un score dans le but 
 * de pouvoir les observer. 
 * @author Antoine Masanet (288366)
 * @author Loïc Houmard (297181)
 *
 */
public final class ScoreBean {

    /**
     * Ensemble des proprietees representant un score d'une partie
     */
    private final SimpleIntegerProperty turnPointsTeam1 = new SimpleIntegerProperty();
    private final SimpleIntegerProperty turnPointsTeam2 = new SimpleIntegerProperty();
    private final SimpleIntegerProperty gamePointsTeam1 = new SimpleIntegerProperty();
    private final SimpleIntegerProperty gamePointsTeam2 = new SimpleIntegerProperty();
    private final SimpleIntegerProperty totalPointsTeam1 = new SimpleIntegerProperty();
    private final SimpleIntegerProperty totalPointsTeam2 = new SimpleIntegerProperty();
    private final SimpleObjectProperty<TeamId> winningTeam = new SimpleObjectProperty<>();

    /**
     * Retourne la propriete turnPoints de l'equipe correspondante.
     * 
     * @param team: L'equipe dont on veut obtenir la propriete turnPoints.
     * 
     * @return la propriete turnPoints de l'equipe corresopndante.
     */
    public ReadOnlyIntegerProperty turnPointsProperty(TeamId team) {
        return team == TeamId.TEAM_1 ? turnPointsTeam1 : turnPointsTeam2;
    }

    /**
     * Modifie l'entier stocke dans la propriete turnPoints.
     * 
     * @param team: L'equipe dont le turnPoints doit etre modifie.
     * @param newTurnPoints: Le nouveau turnPoints.
     */
    public void setTurnPoints(TeamId team, int newTurnPoints) {
        if (team == TeamId.TEAM_1 && turnPointsTeam1.get() != newTurnPoints)
            turnPointsTeam1.set(newTurnPoints);

        else if (team == TeamId.TEAM_2 && turnPointsTeam2.get() != newTurnPoints)
            turnPointsTeam2.set(newTurnPoints);
    }

    /**
     * Retourne la propriete gamePoints de l'equipe correspondante.
     * 
     * @param team: L'equipe dont on veut obtenir la propriete gamePoints.
     * 
     * @return la propriete gamePoints de l'equipe correspondante.
     */
    public ReadOnlyIntegerProperty gamePointsProperty(TeamId team) {
        return team == TeamId.TEAM_1 ? gamePointsTeam1 : gamePointsTeam2;
    }

    /**
     * Modifie l'entier stocke dans la propriete gamePoints.
     * 
     * @param team: L'equipe dont le gamePoints doit etre modifie.
     * @param newGamePoints: Le nouveau gamePoints.
     */
    public void setGamePoints(TeamId team, int newGamePoints) {
        if (team == TeamId.TEAM_1 && gamePointsTeam1.get() != newGamePoints)
            gamePointsTeam1.set(newGamePoints);

        else if (gamePointsTeam1.get() != newGamePoints)
            gamePointsTeam2.set(newGamePoints);
    }

    /**
     * Retourne la propriete totalPoints de l'equipe correspondante.
     * 
     * @param team: L'equipe dont on veut obtenir la propriete totalPoints.
     * 
     * @return: la propriete totalPoints de l'equipe correspondante.
     */
    public ReadOnlyIntegerProperty totalPointsProperty(TeamId team) {
        return team == TeamId.TEAM_1 ? totalPointsTeam1 : totalPointsTeam2;
    }

    /**
     * Modifie l'entier stocke dans la propriete totalPoints.
     * 
     * @param team: L'equipe dont le totalPoints doit etre modifie.
     * @param newTotalPoints: Le nouveau totalPoints. 
     */
    public void setTotalPoints(TeamId team, int newTotalPoints) {
        if (team == TeamId.TEAM_1 && totalPointsTeam1.get() != newTotalPoints)
            totalPointsTeam1.set(newTotalPoints);

        else if (team == TeamId.TEAM_2 && totalPointsTeam2.get() != newTotalPoints)
            totalPointsTeam2.set(newTotalPoints);
    }

    /**
     * Retourne la propriete correspondant a l'equipe gagnante de la partie.
     * 
     * @return la propriete correspondant a l'equipe gagnante de la partie.
     */
    public ReadOnlyObjectProperty<TeamId> winningTeamProperty() {
        return winningTeam;
    }

    /**
     * Modifie le teamId de la propriete de l'equipe gagnante.
     * 
     * @param newWinningTeam: La nouvelle equipe gagnante.
     */
    public void setWinningTeam(TeamId newWinningTeam) {
        if (winningTeam.get() != newWinningTeam)
            winningTeam.set(newWinningTeam);

    }
}
