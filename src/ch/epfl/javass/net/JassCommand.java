package ch.epfl.javass.net;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Enumeration enumerant les 7 types de messages 
 * échangés par le client et le serveur
 * 
 * @author Antoine Masanet (288366)
 * @author Loïc Houmard (297181)
 *
 */
public enum JassCommand {
    PLRS, TRMP, HAND, TRCK, CARD, SCOR, WINR, CHTR, FTPL, TRCH;

    /**
     * Nombre de commandes possibles
     */
    public static final int COUNT = 10;

    /**
     * Contient toutes les valeurs possible de commandes
     */
    public static final List<JassCommand> ALL = Collections
            .unmodifiableList(Arrays.asList(values()));

}
