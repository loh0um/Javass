package ch.epfl.javass;

import ch.epfl.javass.gui.GraphicalPlayerAdapter;
import ch.epfl.javass.net.RemotePlayerServer;
import javafx.application.Application;
import javafx.stage.Stage;

/**
 * Classe principale permettant de jouer une partie à distance.
 * 
 * @author Antoine Masanet (288366)
 * @author Loïc Houmard (297181)
 *
 */
public final class RemoteMain extends Application {

    /**
     * Launch les arguments.
     * 
     * @param args: arguments passés au programme.
     */
    public static void main(String[] args) {
        launch(args);
    }

    /**
     * Crée un fil d'éxecution séparé exécutant un serveur.
     * 
     * @param primaryStage: le stage primaire.
     */
    @Override
    public void start(Stage primaryStage) throws Exception {

        Thread remoteThread = new Thread(() -> {
            System.out.println(
                    "La partie commencera à la connexion du client...");
            new RemotePlayerServer(new GraphicalPlayerAdapter()).run();
        });

        remoteThread.setDaemon(true);
        remoteThread.start();
    }

}
