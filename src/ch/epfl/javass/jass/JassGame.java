package ch.epfl.javass.jass;

import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import ch.epfl.javass.jass.Card.Color;
import ch.epfl.javass.jass.Card.Rank;

/**
 * Classe représentant une partie de jass.
 * 
 * @author Antoine Masanet (288366)
 * @author Loïc Houmard (297181)
 *
 */
public class JassGame {

    /** Le Random pour mélanger les cartes */
    private final Random shuffleRng;
    /** Le Random pour tirer l'atout */
    private final Random trumpRng;
    /** Table associative joueurId-joueurs */
    private Map<PlayerId, Player> players;
    /** Table associative joueurId-nom des joueurs */
    private Map<PlayerId, String> playerNames;

    /** L'état du tour */
    private TurnState turnState;
    /** Table associative joueurId-main */
    private Map<PlayerId, CardSet> playersHand;
    /** Le premier joueur du prochain tour */
    private PlayerId nextTurnPlayer;
    /** Booléen pour savoir si la partie est terminée */
    private boolean gameIsOver;

    /**
     * Constructeur
     * 
     * @param rngSeed:
     *            La graine pour les tirages aléatoires.
     * @param players:
     *            Table associative entre l'id des joueurs et les joueuers
     * @param playerNames:
     *            Table associative entre l'id des joueurs et les joueuers
     * 
     */
    public JassGame(long rngSeed, Map<PlayerId, Player> players,
            Map<PlayerId, String> playerNames) {

        Random rng = new Random(rngSeed);
        this.shuffleRng = new Random(rng.nextLong());
        this.trumpRng = new Random(rng.nextLong());

        this.players = Collections.unmodifiableMap(new EnumMap<>(players));
        this.playerNames = Collections
                .unmodifiableMap(new EnumMap<>(playerNames));

        this.turnState = null;
        this.nextTurnPlayer = null;
        this.gameIsOver = false;
        this.playersHand = new HashMap<>();
    }

    /**
     * Fait avancer l'état du jeu jusqu'à la fin du prochain pli, ou ne fait
     * rien si la partie est terminée. Fait donc jouer tous les joueurs et mets
     * à jour l'état.
     */
    public void advanceToEndOfNextTrick() {

        if (!isGameOver()) {

            /*
             * Si le tour est le tout premier du jeu, l'atout est généré
             * aléatoirement, le premier joueuer est choisi avec le 7 de carreau
             * et le score initialisé à zéro
             */
            if (turnState == null) {
                setAllPlayers();
                initialiseTurn(Score.INITIAL);
            } else {
                turnState = turnState.withTrickCollected();
            }
            /*
             * Si le pli précédent était le dernier (que l'actuel n'est pas
             * valide), un atout est génèré aléatoirement, le score est repris
             * de l'état d'avant, avec les gamePoints mis à jour et le premier
             * joueuer est celui suivant le premier joueur du tour d'avant.
             */
            if (turnState.isTerminal()) {
                initialiseTurn(turnState.score().nextTurn());
            }

            updateAllPlayersScore(turnState.score());
            updateAllPlayersTrick(turnState.trick());

            // Boucle simulant le déroulement du pli
            for (int i = 0; i < PlayerId.COUNT; ++i) {
                makePlayerPlay(i);
                updateAllPlayersTrick(turnState.trick());
            }

            Score scoreAfterTrick = turnState.withTrickCollected().score();
            TeamId winningTeam = PackedTrick
                    .winningPlayer(turnState.packedTrick()).team();
            gameIsOver = scoreAfterTrick
                    .totalPoints(winningTeam) >= Jass.WINNING_POINTS;

            // Si la partie est terminée, mets les scores à jour et annonce le
            // gagnant aux joueurs
            if (isGameOver()) {
                updateAllPlayersScore(scoreAfterTrick);
                setAllPlayersWinningTeam(winningTeam);
            }
        }

    }

    /**
     * Mets à jour le score pour tous les joueurs.
     */
    private void updateAllPlayersScore(Score score) {
        for(PlayerId id:PlayerId.ALL) {
            players.get(id).updateScore(score);
        }
    }

    /**
     * Mets à jour le pli pour tous les joueurs.
     */
    private void updateAllPlayersTrick(Trick trick) {
        for(PlayerId id:PlayerId.ALL) {
            players.get(id).updateTrick(trick);;
        }
    }
    
    private void updateAllPlayersFirstTurnPlayer(PlayerId player) {
        for(PlayerId id:PlayerId.ALL) {
            players.get(id).updateFirstTurnPlayer(player);
        } 
    }
    
    private void updateAllPlayersTrumpChooser(PlayerId player) {
        for(PlayerId id:PlayerId.ALL) {
            players.get(id).updateTrumpChooser(player);
        } 
    }

    /**
     * Annonce la couleur atout à tous les joueurs.
     */
    private void setAllPlayersTrump(Color trump) {
        for (int i = 0; i < PlayerId.COUNT; ++i) {
            players.get(PlayerId.ALL.get(i)).setTrump(trump);
        }
    }

    /**
     * Annonce l'équipe gagnante à tous les joueurs
     */
    private void setAllPlayersWinningTeam(TeamId winningTeam) {
        for (int i = 0; i < PlayerId.COUNT; ++i) {
            players.get(PlayerId.ALL.get(i)).setWinningTeam(winningTeam);
        }
    }

    /**
     * Annonce l'équipe gagnante à tous les joueurs
     */
    private void setAllPlayers() {
        for (int i = 0; i < PlayerId.COUNT; ++i) {
            PlayerId playerId = PlayerId.ALL.get(i);
            players.get(playerId).setPlayers(playerId, playerNames);
        }
    }

    /**
     * Fait jouer un joueur (en enlevant la carte qu'il joue de sa main)
     * 
     * @Param: index: L'index du joueur
     */
    private void makePlayerPlay(int index) {
        PlayerId playerIdToPlay = turnState.trick().player(index);
        Player playerToPlay = players.get(playerIdToPlay);
        CardSet hand = playersHand.get(playerIdToPlay);

        Card cardToPlay = playerToPlay.cardToPlay(turnState, hand);
        hand = hand.remove(cardToPlay);
        playerToPlay.updateHand(hand);
        playersHand.replace(playerIdToPlay, hand);
        turnState = turnState.withNewCardPlayed(cardToPlay);
    }

    /**
     * Retourne le premier joueur du premier tour du jeu.
     * 
     * @Return: L'identité (PlayerId) du joueur possédant le 7 de carreau.
     */
    private PlayerId sevenOfDiamondPlayer() {

        PlayerId firstPlayer = null;
        boolean found = false;
        int compteur = 0;

        while (!found) {
            PlayerId actualPlayer = PlayerId.ALL.get(compteur);
            if (playersHand.get(actualPlayer)
                    .contains(Card.of(Color.DIAMOND, Rank.SEVEN))) {
                firstPlayer = actualPlayer;
                found = true;
            }
            ++compteur;
        }
        return firstPlayer;
    }

    /**
     * Tire aléatoirement une couleur qui sera la couleur d'atout.
     * 
     * @Return: La couleur qui sera atout.
     */
    @SuppressWarnings("unused")
    private Color randomTrump() {
        return Color.ALL.get(trumpRng.nextInt(Color.COUNT));
    }

    /**
     * Mélange les cartes.
     * 
     * @Return: L'ensemble de cartes contenant toutes les cartes mélangé.
     */
    private List<Card> shuffleDeck() {
        List<Card> cardDeck = new ArrayList<>();
        for (int i = 0; i < Jass.HAND_SIZE * PlayerId.COUNT; ++i) {
            cardDeck.add(CardSet.ALL_CARDS.get(i));
        }
        Collections.shuffle(cardDeck, shuffleRng);

        return cardDeck;
    }

    /**
     * Mélange les cartes et les distribue aux joueurs en leur indiquant leur
     * main et en les stockant dans la table associative playersHand.
     */
    private void dealCards() {
        List<Card> cardDeck = shuffleDeck();
        for (int i = 0; i < PlayerId.COUNT; ++i) {
            CardSet hand = CardSet.of(cardDeck.subList(i * Jass.HAND_SIZE,
                    (i + 1) * Jass.HAND_SIZE));
            PlayerId playerId = PlayerId.ALL.get(i);
            Player player = players.get(playerId);
            playersHand.put(playerId, hand);
            player.updateHand(hand);
        }
    }

    private void initialiseTurn(Score score) {
        dealCards();

        if (nextTurnPlayer == null) {
            nextTurnPlayer = sevenOfDiamondPlayer();
        }
        
        updateAllPlayersScore(score);
        updateAllPlayersFirstTurnPlayer(nextTurnPlayer);
        updateAllPlayersTrumpChooser(nextTurnPlayer);

        
        Color firstTrumpChoice=players.get(nextTurnPlayer).chooseTrump(playersHand.get(nextTurnPlayer), true);
        Color trump;
        
        if(firstTrumpChoice!=null) {
            trump=firstTrumpChoice;
        }
        else {
            PlayerId teamMate=nextTurnPlayer.teamMate();
            updateAllPlayersTrumpChooser(teamMate);
            trump=players.get(teamMate).chooseTrump(playersHand.get(teamMate), false);    
        }
        setAllPlayersTrump(trump);

        turnState = TurnState.initial(trump, score, nextTurnPlayer);
        nextTurnPlayer = nextTurnPlayer.next();
    }

    /**
     * Retourne un booléan indiquant si la partie est terminée.
     * 
     * @Return: Vrai ssi la partie est terminée (Une des 2 équipes a plus de
     *          1000 points)
     */
    public boolean isGameOver() {
        return gameIsOver;
    }
}
