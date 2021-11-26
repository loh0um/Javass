package ch.epfl.javass.jass;

import java.util.Map;

import ch.epfl.javass.jass.Card.Color;

/**
 * Classe permettant de s'assurer qu'un joueur met un temps minimum pour jouer. 
 * 
 * @author Antoine Masanet (288366)
 * @author Loïc Houmard (297181)
 * 
 */
public final class PacedPlayer implements Player {

    private final static int MIlLISECONDS_IN_SECOND = 1000;

    private Player underlyingPlayer;
   /**Temps minimum en seconde avant que le joueur joue*/
    private double minTime;

    /**
     * Constructeur publique
     * 
     * @param: underlyingPlayer: Le joueur sous-jacent.
     * @param: minTime: Le temps minimum (en secondes) dont le joueur doit attendre avant de jouer.
     */
    public PacedPlayer(Player underlyingPlayer, double minTime) {
        this.underlyingPlayer = underlyingPlayer;
        this.minTime = minTime;
    }

    /**
     * Retourne la carte que le joueur désire jouer après un temps minimal, sachant
     * que l'état actuel du tour est celui décrit par state et que le joueur a les
     * cartes hand en main.
     * 
     * @param state: L'etat actuel du tour.
     * @param hand: La main du joueur.
     * 
     * @return: La carte que le joueur désire jouer.
     */
    @Override
    public Card cardToPlay(TurnState state, CardSet hand) {
        long firstCurrentTime = System.currentTimeMillis();
        Card choosenCard = underlyingPlayer.cardToPlay(state, hand);
        long timeElapsed = System.currentTimeMillis() - firstCurrentTime;

        makeSleep(timeElapsed);

        return choosenCard;
    }
    
    /**
     * Demande au joueur quel atout il veut choisir
     * 
     * @param hand: La main du joueur
     * @param canPass:Si le joueur a le droit de chibrer
     * 
     * @return: L'atout choisi ou null si le joueur decide de passer (il ne 
     *          peut pas passer si son partenaire a deja passe).
     */
    @Override
    public Color chooseTrump(CardSet hand, boolean canPass) {
        long firstCurrentTime = System.currentTimeMillis();
        Card.Color choosenTrump = underlyingPlayer.chooseTrump(hand, canPass);
        long timeElapsed = System.currentTimeMillis() - firstCurrentTime;
        
        makeSleep(timeElapsed);

        return choosenTrump;
    }

    /**
     * Appelée une seule fois en début de partie pour informer le joueur 
     * qu'il a l'identité ownId et que les différents joueurs (lui inclus) 
     * sont nommés selon le contenu de la table associative playerNames.
     * 
     * @param ownId: L'identite du joueur.
     * @param playerNames: La table associative des noms des joueurs avec leur identifiants.
     */
    @Override
    public void setPlayers(PlayerId ownId, Map<PlayerId, String> playerNames) {
        underlyingPlayer.setPlayers(ownId, playerNames);
    }

    /**
     * Appelée chaque fois que la main du joueur change — soit en début 
     * de tour lorsque les cartes sont distribuées, soit après qu'il ait 
     * joué une carte — pour l'informer de sa nouvelle main.
     * 
     * @param newHand: La nouvelle main du joueur.
     */
    @Override
    public void updateHand(CardSet newHand) {
        underlyingPlayer.updateHand(newHand);
    }

    /**
     *Appelée chaque fois que l'atout change — c-à-d au début de chaque tour — 
     *pour informer le joueur de l'atout.
     * 
     * @param trump: Le nouvel atout.
     */
    @Override
    public void setTrump(Color trump) {
        underlyingPlayer.setTrump(trump);
    }

    /**
     *  Appelée chaque fois que le pli change, c-à-d chaque fois qu'une 
     *  carte est posée ou lorsqu'un pli terminé est ramassé et que le 
     *  prochain pli (vide) le remplace.
     *  
     * @param newTrick: Le nouveau pli.
     */
    @Override
    public void updateTrick(Trick newTrick) {
        underlyingPlayer.updateTrick(newTrick);
    }

    /**
     * Appelée chaque fois que le score change, c-à-d chaque fois qu'un pli est ramassé.
     * 
     * @param score: Le nouveau score.
     */
    @Override
    public void updateScore(Score score) {
        underlyingPlayer.updateScore(score);
    }

    /**
     * Appelée une seule fois dès qu'une équipe à gagné en obtenant 1000 points ou plus.
     * 
     * @param winningTeam: L'equipe gagnante.
     */
    @Override
    public void setWinningTeam(TeamId winningTeam) {
        underlyingPlayer.setWinningTeam(winningTeam);
    }
    
    /**
     * Appelée a chaque début de tour pour indiquer qui est le premier joueur du tour.
     * 
     * @param player: Le premier joueur du tour.
     */
    @Override
    public void updateFirstTurnPlayer(PlayerId player) {
       underlyingPlayer.updateFirstTurnPlayer(player);
    }
    
    /**
     * Appelé après le round de choix d'atout pour indiquer qui a choisi l'atout.
     * 
     * @param player: Le joueur qui a choisi l'atout.
     */
    @Override
    public void updateTrumpChooser(PlayerId player) {
        underlyingPlayer.updateTrumpChooser(player);
    }
    
    /**
     * Fait dormir le joueur aussi longtemps qu'il doit.
     * 
     * @param timeElapsed: le temps passé entre le début et la fin de son choix.
     */
    private void makeSleep(long timeElapsed) {
        
        if (timeElapsed < (minTime * MIlLISECONDS_IN_SECOND)) {
            try {
                Thread.sleep((long) (minTime * MIlLISECONDS_IN_SECOND)
                        - timeElapsed);
            } catch (InterruptedException e) {
                /* ignore */ }
        }
    }

}
