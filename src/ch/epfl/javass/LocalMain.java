package ch.epfl.javass;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import ch.epfl.javass.gui.GraphicalPlayerAdapter;
import ch.epfl.javass.jass.JassGame;
import ch.epfl.javass.jass.MctsPlayer;
import ch.epfl.javass.jass.PacedPlayer;
import ch.epfl.javass.jass.Player;
import ch.epfl.javass.jass.PlayerId;
import ch.epfl.javass.net.RemotePlayerClient;
import ch.epfl.javass.net.StringSerializer;
import javafx.application.Application;
import javafx.stage.Stage;

/**
 * Classe s'occupant de lancer et de faire fonctionner une partie de Jass
 * 
 * @author Antoine Masanet (288366)
 * @author Loïc Houmard (297181)
 *
 */
public final class LocalMain extends Application {

    private final int RNG_SEED_INDEX = 4;
    private final int ERROR = 1;
    private final int PLAYER_ARG_MAXIMUM = 3;
    private final int PLAYER_TYPE_INDEX = 0;
    private final int PLAYER_NAME_INDEX = 1;
    private final int DEFAULT_MCTS_ITERATION = 10_000;
    private final int MINIMUM_MCTS_ITERATION = 10;
    private final int MINIMUM_MCTS_PLAYING_TIME = 2;
    private final int PAUSE_BEFORE_NEXT_TRICK = 1000;
    private final String LOCAL_HOST = "127.0.0.1";
    private final Map<PlayerId, String> DEFAULT_NAMES = initializeDefaultNames();

    private final Map<PlayerId, Player> players = new HashMap<>();
    private final Map<PlayerId, String> playerNames = new HashMap<>();
    private Random rngGenerator;
    private List<String> originalArgs;
    private List<String[]> parsedArgs;

    /**
     * Main de la classe, qui prend en parametre les arguments necessaires
     * au lancement d'une partie de Jass.
     * 
     * @param args: les arguments necessaires au lancement d'une partie de Jass.
     * 
     */
    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {

        originalArgs = getParameters().getRaw();
        parsedArgs = initializeParsedArgs();

        verifyArgSize();

        rngGenerator = originalArgs.size() == 5 ? new Random(readRngSeed())
                : new Random();

        long jassGameSeed = rngGenerator.nextLong();

        readArgs();

        try {
            Thread gameThread = new Thread(() -> {
                JassGame game = new JassGame(jassGameSeed, players, playerNames);
                while (!game.isGameOver()) {
                    game.advanceToEndOfNextTrick();
                    try {
                        Thread.sleep(PAUSE_BEFORE_NEXT_TRICK);
                    } catch (InterruptedException e) {
                        System.err.println(
                                "Interrupted exception lors du sleep du gameThread");
                        e.printStackTrace();
                        System.exit(ERROR);
                    }
                }
            });

            gameThread.setDaemon(true);
            gameThread.start();

        } catch (Exception e) {

            e.printStackTrace();
            System.exit(ERROR);
        }
    }

    /**
     * Verifie que le nombre d'argument fourni a la main est correct 
     */
    private void verifyArgSize() {

        if (originalArgs.size() > 5 || originalArgs.size() < 4) {
            System.err.println("Utilisation: java ch.epfl.javass.LocalMain \n"
                    + "<j1>…<j4> [<graine>] où :\n"
                    + "<jn> spécifie le joueur n, ainsi:\n"
                    + "   h:<nom>  un joueur humain nommé <nom>\n"
                    + "   s:<nom>  un joueur simulé nommé <nom>\n"
                    + "   r:<nom>  un joueur distant nommé <nom>\n"
                    + "Il est a note que les noms peuvent etre omis\n"
                    + "et seront remplace par des noms par defaut \n"
                    + "selon l'indice du joueur n respectivement "
                    + DEFAULT_NAMES.get(PlayerId.PLAYER_1) + ","
                    + DEFAULT_NAMES.get(PlayerId.PLAYER_2) + ","
                    + DEFAULT_NAMES.get(PlayerId.PLAYER_3) + ", "
                    + DEFAULT_NAMES.get(PlayerId.PLAYER_4) + "\n"
                    + "La troisieme composante est aussi optionnelle:\n"
                    + "pour un joueur humain, elle ne represente rien et doit etre omise\n"
                    + "pour un joueur simule, ce doit etre un entier positif representant le\n"
                    + "nombre d'iterations de l'algorithme MTCS de ce joueur(10 000 par defaut)\n"
                    + "pour un joueur distant, elle donne le nom ou l'adresse IP de\n"
                    + "l'hôte sur lequel le serveur du joueur s'exécute (localhost par defaut\n"
                    + "chaque composante est separe par ':' <type de joueur:<nom>:<3eme composante optionnelle>");

            System.exit(ERROR);
        }
    }

    /**
     * Lis tous les arguments fournis en entree et les stockent pour le futur lancement
     * de la partie 
     */
    private void readArgs() {

        final int SPECIFIC_ARGUMENT = 2;

        for (int i = 0; i < PlayerId.COUNT; ++i) {

            // Tableau de taille 3 contenant les arguments dans le bon
            // ordre et le string vide sinon
            String[] fullArgs = new String[PLAYER_ARG_MAXIMUM];

            PlayerId playerId = PlayerId.ALL.get(i);

            if (parsedArgs.get(i).length > PLAYER_ARG_MAXIMUM) {
                System.err.println(
                        "Il ne devrait pas y avoir plus de 3 arguments par joueur");
                System.exit(ERROR);
            }

            // Rempli un tableau contenant 3 arguments avec l'argument
            // correspondant ou avec le string vide "" si il n'y a pas
            // d'arguments
            for (int j = 0; j < PLAYER_ARG_MAXIMUM; ++j) {
                fullArgs[j] = parsedArgs.get(i).length > j && parsedArgs.get(i)[j] != null
                        ? parsedArgs.get(i)[j]
                        : "";
            }

            // S'occupe de stocker le nom de chaque joueur dans une map avec
            // son playerId ou de mettre le nom par defaut
            if (fullArgs[PLAYER_NAME_INDEX].equals(""))
                playerNames.put(playerId, DEFAULT_NAMES.get(playerId));
            else
                playerNames.put(playerId, fullArgs[PLAYER_NAME_INDEX]);

            // Initialise chaque joueur avec les attributs qui lui sont fourni
            // ou les attributs par defaut sinon
            switch (fullArgs[PLAYER_TYPE_INDEX]) {

            case "h": {

                if (!fullArgs[SPECIFIC_ARGUMENT].equals("")) {
                    System.err.println(
                            "Il ne devrait pas y avoir plus de 2 arguments pour un joueur humain");
                    System.exit(ERROR);
                }

                players.put(playerId, new GraphicalPlayerAdapter());
            }
                break;

            case "s": {

                int iterationNumber = DEFAULT_MCTS_ITERATION;

                if (!fullArgs[SPECIFIC_ARGUMENT].equals("")) {
                    try {
                        iterationNumber = Integer.parseInt(fullArgs[SPECIFIC_ARGUMENT]);
                    } catch (NumberFormatException e) {
                        System.err.println(
                                "La valeur pour le nombre d'iterations du simulated player est invalide");
                        System.exit(ERROR);
                    }
                }

                if (iterationNumber < MINIMUM_MCTS_ITERATION) {
                    System.err.println(
                            "Le nombre d'iteration doit etre superieur ou egal a:"
                                    + MINIMUM_MCTS_ITERATION);
                    System.exit(ERROR);
                }

                else {
                    players.put(playerId,
                            new PacedPlayer(new MctsPlayer(playerId,
                                    rngGenerator.nextLong(), iterationNumber),
                                    MINIMUM_MCTS_PLAYING_TIME));
                }
            }
                break;

            case "r": {

                String host = fullArgs[SPECIFIC_ARGUMENT].equals("") ? LOCAL_HOST
                        : fullArgs[SPECIFIC_ARGUMENT];

                try {
                    RemotePlayerClient player = new RemotePlayerClient(host);
                    players.put(playerId, player);
                } catch (IOException e) {
                    System.err.println("Connexion au remotePlayer echouee");
                    System.exit(ERROR);
                }
            }
                break;

            default: {
                System.err.println("Le premier argument doit etre:\n"
                        + "h pour un joueur humain,\n" + "s pour un joueur simulé\n"
                        + "r pour un joueur distant");

                System.exit(ERROR);
            }
                break;
            }
        }

    }

    /**
     * Renvoie une liste avec chaque element correpondant a un argument
     * dans cette liste, un tableau contenant les sous argument modife est initialise.
     * 
     * @return: la liste d'argument plus facile a manipuler.
     */
    private List<String[]> initializeParsedArgs() {

        List<String[]> parsedArgs = new ArrayList<>();
        for (String arg : originalArgs) {
            parsedArgs.add(StringSerializer.split(':', arg));
        }
        return parsedArgs;
    }

    /**Initialize une map qui pour chaque playerId lui attribue son nom par defaut.
     * 
     * @return: L'ensemble des noms pas defaut.
     */
    private Map<PlayerId, String> initializeDefaultNames() {
        Map<PlayerId, String> defaultNames = new HashMap<>();

        defaultNames.put(PlayerId.PLAYER_1, "Aline");
        defaultNames.put(PlayerId.PLAYER_2, "Bastien");
        defaultNames.put(PlayerId.PLAYER_3, "Claudette");
        defaultNames.put(PlayerId.PLAYER_4, "David");

        return defaultNames;
    }

    /**
     * @return: Un long contenant la seed pour le generateur aleatoire.
     */
    private long readRngSeed() {
        long rngSeed = 0;
        try {
            String s = originalArgs.get(RNG_SEED_INDEX);
            rngSeed = Long.parseLong(s);
        } catch (NumberFormatException e) {
            System.err.println("La graine est invalide");
            System.exit(ERROR);
        }
        return rngSeed;
    }
}
