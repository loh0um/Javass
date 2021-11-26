package ch.epfl.javass.net;

import static java.nio.charset.StandardCharsets.US_ASCII;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UncheckedIOException;
import java.io.Writer;
import java.net.Socket;
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
 * Classe représentant le client d'un joueur.
 * 
 * @author Antoine Masanet (288366)
 * @author Loïc Houmard (297181)
 * 
 */
public final class RemotePlayerClient implements Player, AutoCloseable {

    private final Socket socket;
    private final BufferedReader reader;
    private final Writer writer;

    /**
     * Constructeur de RemotePlayerClient. Se connecte sur le serveur du joueur distant.
     * 
     * @param: String hostName: Nom de l'hôte sur lequel s'exécute le serveur du joueur distant 
     *                          (adresseIP de l'ordinateur sur lequel run le RemotePlayerServer).
     */

    public RemotePlayerClient(String hostName) throws IOException {

        this.socket = new Socket(hostName, RemotePlayerServer.PORT);
        this.reader = new BufferedReader(
                new InputStreamReader(socket.getInputStream(), US_ASCII));
        this.writer = new BufferedWriter(
                new OutputStreamWriter(socket.getOutputStream(), US_ASCII));

    }

    /**
     * Ecrit au serveur l'état actuel ainsi que la main et attend que celui-ci lui retourne
     * la carte que le joueur veut jouer, pour la retourner.
     * 
     * @param state: L'etat actuel du tour.
     * @param hand: La main du joueur.
     * 
     * @return: La carte que le joueur désire jouer.
     */
    @Override
    public Card cardToPlay(TurnState state, CardSet hand) {

        String scoreSerialized = StringSerializer
                .serializeLong(state.packedScore());
        String unplayedCardsSerialized = StringSerializer
                .serializeLong(state.packedUnplayedCards());
        String trickSerialized = StringSerializer
                .serializeInt(state.packedTrick());
        String handSerialized = StringSerializer.serializeLong(hand.packed());

        String stateString = StringSerializer.combine(',', scoreSerialized,
                unplayedCardsSerialized, trickSerialized);
        writeAsOutput(StringSerializer.combine(' ', JassCommand.CARD.name(),
                stateString, handSerialized));

        try {
            Card cardToPlay = Card.ofPacked(
                    StringSerializer.deserializeInt(reader.readLine()));
            return cardToPlay;
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }

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
        
        String handSerialized=StringSerializer.serializeLong(hand.packed());
        String booleanSerialized=StringSerializer.serializeBool(canPass);
        
        writeAsOutput(StringSerializer.combine(' ', JassCommand.CHTR.name(),handSerialized,booleanSerialized));
        
        //Recupere les infos du remotePlayerServer
        try {
            int colorOrdinal=StringSerializer.deserializeInt(reader.readLine());
            Color trumpChosen = colorOrdinal==-1?null:Color.ALL.get(colorOrdinal);
            return trumpChosen;
        } catch (IOException e) {throw new UncheckedIOException(e);}
    }

    /**
     * Informe le serveur de l'identité des joueur.
     * 
     * @param ownId: L'identite du joueur.
     * @param playerNames: La table associative des noms des joueurs avec leur identifiants.
     */
    @Override
    public void setPlayers(PlayerId ownId, Map<PlayerId, String> playerNames) {

        String[] names = new String[PlayerId.COUNT];
        int i = 0;
        for (String n : playerNames.values()) {
            names[i] = StringSerializer.serializeString(n);
            ++i;
        }
        String concatenatedNames = StringSerializer.combine(',', names);

        String ownIdSerialized = StringSerializer.serializeInt(ownId.ordinal());

        writeAsOutput(StringSerializer.combine(' ', JassCommand.PLRS.name(),
                ownIdSerialized, concatenatedNames));

    }

    /**
     * Informe le serveur de la nouvelle main du joueur.
     * 
     * @param newHand: La nouvelle main du joueur.
     */
    @Override
    public void updateHand(CardSet newHand) {

        String newHandSerialized = StringSerializer
                .serializeLong(newHand.packed());
        writeAsOutput(StringSerializer.combine(' ', JassCommand.HAND.name(),
                newHandSerialized));
    }

    /**
     * Informe le serveur de l'atout
     * 
     * @param trump: Le nouvel atout.
     */
    @Override
    public void setTrump(Color trump) {

        String trumpSerialized = StringSerializer.serializeInt(trump.ordinal());
        writeAsOutput(StringSerializer.combine(' ', JassCommand.TRMP.name(),
                trumpSerialized));
    }
    
    /**
     * Informe le serveur de l'équipe gagnante.
     * 
     * @param winningTeam: L'equipe gagnante.
     */
    @Override
    public void setWinningTeam(TeamId winningTeam) {

        String teamSerialized = StringSerializer
                .serializeInt(winningTeam.ordinal());
        writeAsOutput(StringSerializer.combine(' ', JassCommand.WINR.name(),
                teamSerialized));
    }

    /**
     * Informe le serveur du nouveau pli.
     *  
     * @param newTrick: Le nouveau pli.
     */
    @Override
    public void updateTrick(Trick newTrick) {

        String trickSerialized = StringSerializer
                .serializeInt(newTrick.packed());
        writeAsOutput(StringSerializer.combine(' ', JassCommand.TRCK.name(),
                trickSerialized));
    }

    /**
     * Informe le serveur du nouveau score.
     * 
     * @param score: Le nouveau score.
     */
    @Override
    public void updateScore(Score score) {

        String scoreSerialized = StringSerializer.serializeLong(score.packed());
        writeAsOutput(StringSerializer.combine(' ', JassCommand.SCOR.name(),
                scoreSerialized));
    }
    
    /**
     * Informe le serveur lors d'un nouveau tour du prochain joueur
     * 
     * @param player: Le nouveau premier joueur du tour.
     */
    @Override
    public void updateFirstTurnPlayer(PlayerId player) {
       String playerSerialized= StringSerializer.serializeInt(player.ordinal());
       writeAsOutput(StringSerializer.combine(' ', JassCommand.FTPL.name(),
                           playerSerialized));
       
    }
    
    /**
     * Informe le serveur lors d'un nouveau tour du joueur choisissant l'atout
     * 
     * @param player: Le joueur choississant l'atout.
     */
    @Override
    public void updateTrumpChooser(PlayerId player) {
        String playerSerialized= StringSerializer.serializeInt(player.ordinal());
        writeAsOutput(StringSerializer.combine(' ', JassCommand.TRCH.name(),
                            playerSerialized));   
    }
    
    
    /**
     * Ecrit la chaîne de caractère reçue en argument dans le flux, pour le serveur.
     * 
     * @param s: La String à écrire dans le flux.
     */
    private void writeAsOutput(String s) {
        try {
            writer.write(s);
            writer.write('\n');
            writer.flush();
        } catch (IOException e) {throw new UncheckedIOException(e);}
    }

    /**
     * Ferme les flots d'entrée et de sortie de la prise utilisée pour se connecter au serveur, 
     * ainsi que celle de la prise elle-même. 
     * 
     * @throws: IOException: Si une I/O erreur appartaît.
     */
    @Override
    public void close() throws IOException {
        socket.close();
        reader.close();
        writer.close();
    }
}
