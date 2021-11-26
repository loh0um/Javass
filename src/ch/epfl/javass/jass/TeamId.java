package ch.epfl.javass.jass;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Enumeration permettant d'identifier une equipe
 * @author Antoine Masanet (288366)
 * @author Loïc Houmard (297181)
 *
 */
public enum TeamId {
    TEAM_1, TEAM_2;

    /**
     * Nombre d'equipes possibles
     */
    public static final int COUNT = 2;

    /**
     * Contient toutes les valeurs possible d'equipes 
     */
    public static final List<TeamId> ALL = Collections
            .unmodifiableList(Arrays.asList(values()));

    /**
     * Renvoie l'equipe adverse 
     * @return l'equipe adverse 
     */
    public TeamId other() {
        return this == TEAM_1 ? TEAM_2 : TEAM_1;
    }
}
