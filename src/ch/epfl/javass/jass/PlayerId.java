package ch.epfl.javass.jass;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Enumeration permettant d'identifier un joueur
 * @author Antoine Masanet (288366)
 * @author Loïc Houmard (297181)
 *
 */
public enum PlayerId {
    PLAYER_1, PLAYER_2, PLAYER_3, PLAYER_4;

    /**
     * Nombre de joueurs possibles
     */
    public static final int COUNT = 4;

    /**
     * Contient toutes les valeurs possible de joueurs
     */
    public static final List<PlayerId> ALL = Collections
            .unmodifiableList(Arrays.asList(values()));

    /**
     * Renvoie l'equipe a laquelle appartient le joueur
     * 
     * @return l'equipe a laquele appartient le joueur
     */
    public TeamId team() {
        return this.ordinal() % TeamId.COUNT == 0 ? TeamId.TEAM_1 : TeamId.TEAM_2;
    }
    
    /**
     * Renvoie le playerId du partenaire du joueur actuel
     * @return: le playerId du partenaire du joueur actuel
     */
    public PlayerId teamMate() {
        return ALL.get((this.ordinal()+TeamId.COUNT)%PlayerId.COUNT);
    }
    
    /**
     * Renvoie le playerId du joueur apres celui-ci
     * 
     * @return:le playerId du joueur apres celui-ci
     */
    public PlayerId next() {
        return ALL.get((this.ordinal()+1)%PlayerId.COUNT); 
    }
}
