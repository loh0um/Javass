package ch.epfl.javass.net;

import static java.nio.charset.StandardCharsets.US_ASCII;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UncheckedIOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

import ch.epfl.javass.jass.Card;
import ch.epfl.javass.jass.Card.Color;
import ch.epfl.javass.jass.CardSet;
import ch.epfl.javass.jass.Player;
import ch.epfl.javass.jass.PlayerId;
import ch.epfl.javass.jass.Score;
import ch.epfl.javass.jass.TeamId;
import ch.epfl.javass.jass.Trick;
import ch.epfl.javass.jass.TurnState;

/**
 * Classe publique et finale, représentant le serveur d'un joueur, 
 * qui attend une connexion sur le port 5108 et pilote un joueur local 
 * en fonction des messages reçus.
 * 
 * @author Antoine Masanet (288366)
 * @author Loïc Houmard (297181)
 *
 */
public final class RemotePlayerServer {
    /**
     * Port a partir duquel les informations transitent
     */
    public static final int PORT = 5108;

    private final Player localPlayer;
    private final ServerSocket serverSocket;

    /**
     * Constructeur publique d'un RemotePLayerServer
     * @param player: Le joueur avec lequel le serveur communique
     */
    public RemotePlayerServer(Player player) {
        localPlayer = player;
        try {
            this.serverSocket = new ServerSocket(PORT);

        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    /**
     * Methodes lisant les informations envoyee par le RemotePlayerServer
     * et appelant les methodes du localPlayer correspondantes
     */
    public void run() {
        final int COMMAND_INDEX = 0;
        final int PLAYER_ID_INDEX = 1;
        final int PLAYER_NAME_INDEX = 2;

        try (Socket clientSocket = serverSocket.accept();
                BufferedReader r = new BufferedReader(
                        new InputStreamReader(clientSocket.getInputStream(), US_ASCII));
                BufferedWriter w = new BufferedWriter(
                        new OutputStreamWriter(clientSocket.getOutputStream(), US_ASCII)))

        {
            String input;
            while ((input = r.readLine()) != null) {

                String[] args = StringSerializer.split(' ', input);

                String cmd = args[COMMAND_INDEX];

                switch (JassCommand.valueOf(cmd)) {
                case PLRS: {
                    // Recupere le playerID de localPlayer
                    int playerId = StringSerializer.deserializeInt(args[PLAYER_ID_INDEX]);

                    // Recupere les noms des players dans le bon ordre
                    String playerNames = args[PLAYER_NAME_INDEX];
                    String[] namesArray = StringSerializer.split(',', playerNames);

                    Map<PlayerId, String> playerMap = new HashMap<>();

                    for (int i = 0; i < namesArray.length; ++i) {
                        playerMap.put(PlayerId.ALL.get(i),
                                StringSerializer.deserializeString(namesArray[i]));
                    }

                    localPlayer.setPlayers(PlayerId.ALL.get(playerId), playerMap);
                }
                    break;

                case TRMP: {
                    localPlayer.setTrump(
                            Color.ALL.get(StringSerializer.deserializeInt(args[1])));
                }
                    break;

                case HAND: {
                    localPlayer.updateHand(
                            CardSet.ofPacked(StringSerializer.deserializeLong(args[1])));
                }
                    break;

                case TRCK: {
                    localPlayer.updateTrick(
                            Trick.ofPacked(StringSerializer.deserializeInt(args[1])));
                }
                    break;

                case CARD: {
                    
                    
                    
                    String turnStateString = args[1];
                    String handString = args[2];

                    String[] turnStateArg = StringSerializer.split(',', turnStateString);

                    TurnState state = TurnState.ofPackedComponents(
                            StringSerializer.deserializeLong(turnStateArg[0]),
                            StringSerializer.deserializeLong(turnStateArg[1]),
                            StringSerializer.deserializeInt(turnStateArg[2]));

                    CardSet hand = CardSet
                            .ofPacked(StringSerializer.deserializeLong(handString));

                    Card card = localPlayer.cardToPlay(state, hand);

                    w.write(StringSerializer.serializeInt(card.packed()));
                    w.write('\n');
                    w.flush();

                }break;

                case SCOR: {
                    localPlayer.updateScore(
                            Score.ofPacked(StringSerializer.deserializeLong(args[1])));
                }
                    break;

                case WINR: {
                    localPlayer.setWinningTeam(
                            TeamId.ALL.get(StringSerializer.deserializeInt(args[1])));
                }
                    break;
                case CHTR:{
                    
                    //La valeur -1 correspond a un argument null    
                    
                    final int NO_COLOR_GIVEN=-1;    
                    String handString=args[1];
                    //Boolean represented by a 0 or a 1
                    String canPassString =args[2];
                    
                    //Extract the information from a string for both args
                    CardSet hand =CardSet
                                  .ofPacked(StringSerializer.deserializeLong(handString));
                    
                    boolean canPass=StringSerializer.deserializeBool(canPassString);
                    
                    Color trump=localPlayer.chooseTrump(hand, canPass);
                    
                    w.write(trump==null?StringSerializer.serializeInt(NO_COLOR_GIVEN):StringSerializer.serializeInt(trump.ordinal()));
                    w.write('\n');
                    w.flush();
                    
                    }break;
                    
                case FTPL:{   
                 localPlayer.updateFirstTurnPlayer(PlayerId.ALL.get(StringSerializer.deserializeInt(args[1])));   
                }break;
                
                case TRCH:{
                    localPlayer.updateTrumpChooser(PlayerId.ALL.get(StringSerializer.deserializeInt(args[1])));      
                }break;
                    
                default:
                    throw new IllegalArgumentException("The command is not known");
                }
            }
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }
}
