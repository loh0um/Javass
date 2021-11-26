package ch.epfl.javass;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import ch.epfl.javass.gui.GraphicalLauncher;
import ch.epfl.javass.gui.GraphicalPlayerAdapter;
import ch.epfl.javass.jass.JassGame;
import ch.epfl.javass.jass.MctsPlayer;
import ch.epfl.javass.jass.PacedPlayer;
import ch.epfl.javass.jass.Player;
import ch.epfl.javass.jass.PlayerId;
import ch.epfl.javass.net.RemotePlayerClient;
import ch.epfl.javass.net.RemotePlayerServer;
import ch.epfl.javass.net.StringSerializer;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.stage.Stage;

/**
 * Classe de lancement d'une partie de Jass
 * @author Antoine Masanet (288366)
 * @author Loïc Houmard (297181)
 *
 */
public final class Play extends Application{

    
    private static final int ERROR = 1;
    private static final int PLAYER_ARG_MAXIMUM = 3;
    private static final int PLAYER_TYPE_INDEX = 0;
    private static final int PLAYER_NAME_INDEX = 1;
    private static final int DEFAULT_MCTS_ITERATION = 10_000;
    private static final int MINIMUM_MCTS_PLAYING_TIME = 2;
    private static final int PAUSE_BEFORE_NEXT_TRICK = 1000;
    private static final String LOCAL_HOST = "127.0.0.1";
    
    private static final Map<PlayerId, String> DEFAULT_NAMES = initializeDefaultNames();
    private static final Map<String, Integer> AILevels = initializeAILevels();
    private static final Map<PlayerId, Player> players = new HashMap<>();
    private static final Map<PlayerId, String> playerNames = new HashMap<>();
    
    private static Random rngGenerator;
    private static List<String> originalArgs;
    private static List<String[]> parsedArgs;
    
   
    private static Stage launcherStage;
    
    /**
     * Launch les arguments
     * 
     * @param args: les arguments
     */
    public static void main(String[] args) {launch(args);}
    
    /**
     * Lance le menu de depart
     * 
     * @param primaryStage: le stage principal de l'application
     */
    @Override
    public void start(Stage primaryStage) throws Exception {
        
        //Par defaut Javafx ne terminera plus l'application lorsque la derniere fenetre est ferme
        Platform.setImplicitExit(false);
        
        launcherStage=new GraphicalLauncher().createStage();
        
        launcherStage.show();
    }
    
    /**
     * Debute la partie avec les arguments fournis
     * 
     * @param args: Les parametres de la partie
     */
    public static void startGame(List<String> args) {

        originalArgs=args;
        
        rngGenerator = new Random();
        
        switch (args.get(0)) {
        
        case "Locale":startLocalGame();
            
        break;

        case "Distante":startRemoteGame();
        
        break;
        
        default:{
            System.err.println("Le type de partie n'est pas definie");
        }break;
            
        }
        
        launcherStage.close();
    }
    
    
    /**
     * Debute une partie distante.
     */
    private static void startRemoteGame() {
        
        Thread remoteThread = new Thread(() -> {
            System.out.println(
                    "La partie commencera à la connexion du client...");
            new RemotePlayerServer(new GraphicalPlayerAdapter()).run();
        });

        remoteThread.setDaemon(true);
        remoteThread.start();
    }
    
    /**
     * Debute une partie locale
     */
    private static void startLocalGame(){
        
        long jassGameSeed = rngGenerator.nextLong();
        
        parsedArgs=initializeParsedArgs();
        
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
     * Lis tous les arguments fournis en entree et les stockent pour le futur lancement
     * de la partie 
     */
    private static void readArgs() {

        final int SPECIFIC_ARGUMENT = 2;

        for (int i = 0; i < PlayerId.COUNT; ++i) {

            // Tableau de taille 3 contenant les arguments dans le bon
            // ordre et le string vide sinon
            String[] fullArgs = new String[PLAYER_ARG_MAXIMUM];

            PlayerId playerId = PlayerId.ALL.get(i);

            if (parsedArgs.get(i+1).length > PLAYER_ARG_MAXIMUM) {
                System.err.println(
                        "Il ne devrait pas y avoir plus de 3 arguments par joueur");
                System.exit(ERROR);
            }

            // Rempli un tableau contenant 3 arguments avec l'argument
            // correspondant ou avec le string vide "" si il n'y a pas
            // d'arguments
            for (int j = 0; j < PLAYER_ARG_MAXIMUM; ++j) {
                fullArgs[j] = parsedArgs.get(i+1).length > j && parsedArgs.get(i+1)[j] != null
                        ? parsedArgs.get(i+1)[j]
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

            case "Humain": {

                if (!fullArgs[SPECIFIC_ARGUMENT].equals("")) {
                    System.err.println(
                            "Il ne devrait pas y avoir plus de 2 arguments pour un joueur humain");
                    System.exit(ERROR);
                }

                players.put(playerId, new GraphicalPlayerAdapter());
            }
                break;

            case "Simulé": {

                int iterationNumber = DEFAULT_MCTS_ITERATION;

                if (!fullArgs[SPECIFIC_ARGUMENT].equals("")) {
                    try {
                        
                        iterationNumber = AILevels.get(fullArgs[SPECIFIC_ARGUMENT]);
                        
                    } catch (NumberFormatException e) {
                        System.err.println(
                                "La valeur pour le nombre d'iterations du simulated player est invalide");
                        System.exit(ERROR);
                    }
                }
                    players.put(playerId,
                            new PacedPlayer(new MctsPlayer(playerId,
                                    rngGenerator.nextLong(), iterationNumber),
                                    MINIMUM_MCTS_PLAYING_TIME));
            }
                break;

            case "Distant": {

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
                        + "Humain pour un joueur humain,\n" + "Simulé pour un joueur simulé\n"
                        + "Distant pour un joueur distant");

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
    private static List<String[]> initializeParsedArgs() {

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
    private static Map<PlayerId, String> initializeDefaultNames() {
        Map<PlayerId, String> defaultNames = new HashMap<>();

        defaultNames.put(PlayerId.PLAYER_1, "Aline");
        defaultNames.put(PlayerId.PLAYER_2, "Bastien");
        defaultNames.put(PlayerId.PLAYER_3, "Claudette");
        defaultNames.put(PlayerId.PLAYER_4, "David");

        return defaultNames;
    }
    
    private static Map<String, Integer> initializeAILevels(){
        
        Map<String, Integer> AILevels=new HashMap<>();
        
        AILevels.put("Faible", 10_000);
        AILevels.put("Moyen", 100_000);
        AILevels.put("Bon", 400_000);
        
        return AILevels;
    }  
}
