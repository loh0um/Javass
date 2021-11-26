/**
 * 
 */
package ch.epfl.javass.gui;

import java.util.Map;
import java.util.concurrent.ArrayBlockingQueue;

import ch.epfl.javass.jass.Card;
import ch.epfl.javass.jass.Card.Color;
import ch.epfl.javass.jass.CardSet;
import ch.epfl.javass.jass.Player;
import ch.epfl.javass.jass.PlayerId;
import ch.epfl.javass.jass.Score;
import ch.epfl.javass.jass.TeamId;
import ch.epfl.javass.jass.Trick;
import ch.epfl.javass.jass.TurnState;
import javafx.application.Platform;

/**
 * Classe representant un adaptateur permettant d'adapter l'interface 
 * graphique pour en faire un joueur.
 *  
 * @author Antoine Masanet (288366)
 * @author Loïc Houmard (297181)
 *
 */
public class GraphicalPlayerAdapter implements Player {

    private final HandBean handBean;
    private final ScoreBean scoreBean;
    private final TrickBean trickBean;
    private final TurnBean turnBean;
    private GraphicalPlayer graphicalPlayer;
    private final ArrayBlockingQueue<Card> cardCommunicationQueue;
    private final ArrayBlockingQueue<Integer> colorCommunicationQueue;

    /**
     * Constructeur public permettant d'initialiser les attributs 
     * d'un GraphicalPlayerAdapter 
     */
    public GraphicalPlayerAdapter() {

        handBean = new HandBean();
        scoreBean = new ScoreBean();
        trickBean = new TrickBean();
        turnBean = new TurnBean();
        // Queue avec une capacite de 1 pour la carte
        cardCommunicationQueue = new ArrayBlockingQueue<>(1);
        // Queue avec une capacite de 1 pour la couleur d'atout
        colorCommunicationQueue = new ArrayBlockingQueue<>(1);

    }

    /**
     * Retourne la carte que le joueur désire jouer, sachant que l'état 
     * actuel du tour est celui décrit par state et que le joueur a les cartes hand en main.
     * 
     * @param state: L'etat actuel du tour.
     * @param hand: La main du joueur.
     * 
     * @return: La carte que le joueur désire jouer.
     */
    @Override
    public Card cardToPlay(TurnState state, CardSet hand) {

        Platform.runLater(() -> {
            handBean.setPlayableCards(state.trick().playableCards(hand));
        });

        try {
            Card playedCard = cardCommunicationQueue.take();

            Platform.runLater(() -> {
                handBean.setPlayableCards(CardSet.EMPTY);
            });
            return playedCard;

        } catch (InterruptedException e) {
            System.out.println("Thread communication queue was interrupted");
            e.printStackTrace();
            return null;
        }
    }
    
    /**
     * Retourne la couleur que le joueur choisi comme atout (ou null s'il chibre), sachant 
     * que le joueur a les cartes hand en main et qu'il peut encore chibrer (si canPass = true)
     * ou pas.
     * 
     * @param hand: La main du joueur.
     * @param canPass: Boolean disant si un joueur peut chibrer ou pas.
     * 
     * @return: La couleur que le joueur choisi comme atout.
     */
    @Override
    public Color chooseTrump(CardSet hand, boolean canPass) {
        
        Platform.runLater(() -> {
            turnBean.setMustChooseTrump(true);
            turnBean.setCanPass(canPass);
        });

        try {
            Integer trumpOrdinal = colorCommunicationQueue.take();

            assert GraphicalPlayer.CHIBRE_INT <= trumpOrdinal && trumpOrdinal < Color.COUNT;
            
            Platform.runLater(() -> {
                turnBean.setMustChooseTrump(false);
            });
            
            if (trumpOrdinal == GraphicalPlayer.CHIBRE_INT) {
                return null;
            }
            else {
                return Color.ALL.get(trumpOrdinal);
            }

        } catch (InterruptedException e) {
            System.out.println("Thread communication queue was interrupted");
            e.printStackTrace();
            return null;
        }
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

        graphicalPlayer = new GraphicalPlayer(ownId, playerNames, scoreBean, trickBean,
                handBean, turnBean, cardCommunicationQueue, colorCommunicationQueue);

        Platform.runLater(() -> {
            graphicalPlayer.createStage().show();
        });
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

        Platform.runLater(() -> {
            handBean.setHand(newHand);
        });
    }

    /**
     * Appelée chaque fois que l'atout change — c-à-d au début de chaque tour — 
     * pour informer le joueur de l'atout.
     * 
     * @param trump: Le nouvel atout.
     */
    @Override
    public void setTrump(Color trump) {

        Platform.runLater(() -> {
            trickBean.setTrump(trump);
        });
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

        Platform.runLater(() -> {
            trickBean.setTrick(newTrick);
        });
    }

    /**
     * Appelée chaque fois que le score change, c-à-d chaque fois qu'un pli est ramassé.
     * 
     * @param score: Le nouveau score.
     */
    @Override
    public void updateScore(Score score) {

        Platform.runLater(() -> {
            for (TeamId team : TeamId.ALL) {

                scoreBean.setTurnPoints(team, score.turnPoints(team));
                scoreBean.setGamePoints(team, score.gamePoints(team));
                scoreBean.setTotalPoints(team, score.totalPoints(team));
            }
        });
    }

    /**
     * Appelée une seule fois dès qu'une équipe à gagné en obtenant 1000 points ou plus.
     * 
     * @param winningTeam: L'equipe gagnante.
     */
    @Override
    public void setWinningTeam(TeamId winningTeam) {

        Platform.runLater(() -> {
            scoreBean.setWinningTeam(winningTeam);
        });
    }
    
    @Override
    public void updateFirstTurnPlayer(PlayerId player) {
        Platform.runLater(() -> {
            trickBean.setEmptyTrick();
            turnBean.setFirstTurnPlayer(player);
        });
    }
    
    @Override
    public void updateTrumpChooser(PlayerId player) {
        Platform.runLater(() -> {
            turnBean.setTrumpChooser(player);
        });
    }
}
