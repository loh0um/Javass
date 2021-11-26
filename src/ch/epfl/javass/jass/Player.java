package ch.epfl.javass.jass;

import java.util.Map;

import ch.epfl.javass.jass.Card.Color;

/**
 * Interface publique representant un joueur et les methodes qui lui sont propres.
 * 
 * @author Antoine Masanet (288366)
 * @author Loïc Houmard (297181)
 *
 */
public interface Player {

    /**
     * Retourne la carte que le joueur désire jouer, sachant que l'état 
     * actuel du tour est celui décrit par state et que le joueur a les cartes hand en main.
     * 
     * @param state: L'etat actuel du tour.
     * @param hand: La main du joueur.
     * 
     * @return: La carte que le joueur désire jouer.
     */
    public abstract Card cardToPlay(TurnState state, CardSet hand);
    
    /**
     * Demande au joueur quel atout il veut choisir
     * 
     * @param hand: La main du joueur
     * @param canPass:Si le joueur a le droit de chibrer
     * 
     * @return: L'atout choisi ou null si le joueur decide de passer (il ne 
     *          peut pas passer si son partenaire a deja passe).
     */
    public abstract Color chooseTrump(CardSet hand, boolean canPass);

    /**
     * Appelée une seule fois en début de partie pour informer le joueur 
     * qu'il a l'identité ownId et que les différents joueurs (lui inclus) 
     * sont nommés selon le contenu de la table associative playerNames.
     * 
     * @param ownId: L'identite du joueur.
     * @param playerNames: La table associative des noms des joueurs avec leur identifiants.
     */
    public default void setPlayers(PlayerId ownId,
            Map<PlayerId, String> playerNames) {
    }

    /**
     * Appelée chaque fois que la main du joueur change — soit en début 
     * de tour lorsque les cartes sont distribuées, soit après qu'il ait 
     * joué une carte — pour l'informer de sa nouvelle main.
     * 
     * @param newHand: La nouvelle main du joueur.
     */
    public default void updateHand(CardSet newHand) {
    }

    /**
     * Appelée chaque fois que l'atout change — c-à-d au début de chaque tour — 
     * pour informer le joueur de l'atout.
     * 
     * @param trump: Le nouvel atout.
     */
    public default void setTrump(Color trump) {
    }

    /**
     *  Appelée chaque fois que le pli change, c-à-d chaque fois qu'une 
     *  carte est posée ou lorsqu'un pli terminé est ramassé et que le 
     *  prochain pli (vide) le remplace.
     *  
     * @param newTrick: Le nouveau pli.
     */
    public default void updateTrick(Trick newTrick) {
    }

    /**
     * Appelée chaque fois que le score change, c-à-d chaque fois qu'un pli est ramassé.
     * 
     * @param score: Le nouveau score.
     */
    public default void updateScore(Score score) {
    }

    /**
     * Appelée une seule fois dès qu'une équipe à gagné en obtenant 1000 points ou plus.
     * 
     * @param winningTeam: L'equipe gagnante.
     */
    public default void setWinningTeam(TeamId winningTeam) {
    }
    
    /**
     * Appelée a chaque début de tour pour indiquer qui est le premier joueur du tour.
     * 
     * @param player: Le premier joueur du tour.
     */
    public default void updateFirstTurnPlayer(PlayerId player) {        
    }
    
    /**
     * Appelé après le round de choix d'atout pour indiquer qui a choisi l'atout.
     * 
     * @param player: Le joueur qui a choisi l'atout.
     */
    public default void updateTrumpChooser(PlayerId player) {          
    }

}
