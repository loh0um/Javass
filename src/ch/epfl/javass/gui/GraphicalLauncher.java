package ch.epfl.javass.gui;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringJoiner;

import ch.epfl.javass.Play;
import ch.epfl.javass.jass.Card;
import ch.epfl.javass.jass.PlayerId;
import javafx.beans.binding.Bindings;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TextField;
import javafx.scene.effect.Effect;
import javafx.scene.effect.Lighting;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.stage.Stage;

/**
 * Interface Graphique pour lancer le jeu
 * 
 * @author Antoine Masanet (288366)
 * @author Loïc Houmard (297181)
 * 
 */
public final class GraphicalLauncher {

    private final Scene scene;
    
    private final int STANDARD_PADDING = 15; 
    

    //Map entre les id des joueurs et tous les TextFields/ChoiceBox où ils doivent entrer des informations.
    private final Map<PlayerId, TextField> IP_TEXT_FIELD;
    private final Map<PlayerId, TextField> PLAYERS_NAME = new HashMap<>();
    private final Map<PlayerId, ChoiceBox<String>> PLAYERS_TYPE = new HashMap<>();
    private final Map<PlayerId, ChoiceBox<String>> IA_LEVEL_CHOICE = new HashMap<>();
    private ChoiceBox<String> gameTypeChoiceBox;
    //Les panneaux qui doivent être ajoutés/supprimés en fonction des choix des joueurs dans les ChoiceBox.
    private final Map<PlayerId, HBox>   IA_LEVEL_BOX;
    private final VBox playersPane;
    
    //Tableaux pour les textes dans les choicBox
    private final String[] GAME_TYPE_TABLE = {"Locale", "Distante"};
    private final String[] PLAYER_TYPE_TABLE = {"Humain", "Distant", "Simulé"};
    private final String[] IA_LEVEL_CHOICES_TABLE = {"Faible", "Moyen", "Bon"};
        
    private VBox mainPane;
    
    /**
     * Construit l'ensemble de l'interface graphique de départ,
     * à l'exception de la fenêtre elle-même.
     */
    public GraphicalLauncher() {
        
        this.IA_LEVEL_BOX = initializeIAChoiceBox();
        this.IP_TEXT_FIELD = initializeIPAdresse();
        this.playersPane = createPlayersPane();
        
        HBox playerTypeBox= createPlayerLocationBox();        
        HBox topBox = createTop();
        HBox startButton = createStartButton();
        
        VBox mainPane = new VBox(topBox, playerTypeBox, playersPane, startButton);
        mainPane.setAlignment(Pos.TOP_CENTER);
        
        this.mainPane = mainPane;
        
        this.scene = new Scene(mainPane);
                
    }
    
    /**
     * Construit la partie du haut du pane avec le "titre" et les cartes
     * 
     * @return le haut du pane construit
     */
    private HBox createTop() {
        
        final double SMALLER_CARD_RATIO = 2.2;
        
        List<ImageView> aceImages = new ArrayList<>();
        for (Card.Color c: Card.Color.ALL) {
            Card card = Card.of(c, Card.Rank.ACE);
            ImageView imageView = new ImageView(ImageToolBox.imageFromCard(card, ImageToolBox.CardSize.CARD_BOARD));
            
            //Met les cartes de trèfle et pique légèrement plus petites que les autres
            if(c.equals(Card.Color.DIAMOND) || c.equals(Card.Color.HEART)) {
                imageView.setFitHeight(ImageToolBox.CardSize.CARD_BOARD.getHeight()/ImageToolBox.SIZE_RATIO);
                imageView.setFitWidth(ImageToolBox.CardSize.CARD_BOARD.getWidth()/ImageToolBox.SIZE_RATIO);
            }
            else {
                imageView.setFitHeight(ImageToolBox.CardSize.CARD_BOARD.getHeight()/SMALLER_CARD_RATIO);
                imageView.setFitWidth(ImageToolBox.CardSize.CARD_BOARD.getWidth()/SMALLER_CARD_RATIO);
            }
            
            aceImages.add(imageView);           
        }
        
        Text javassText = new Text("  JAVASS  ");
        javassText.setFill(Color.DARKRED);
       
        javassText.setStyle("-fx-font: 40 Optima;"
                    + "-fx-font-weight: bold;"
                    + "-fx-alignment: center;");
        
        HBox topBox = new HBox(STANDARD_PADDING, aceImages.get(0), aceImages.get(1), javassText, aceImages.get(2), aceImages.get(3));

        
        topBox.setAlignment(Pos.CENTER);
        topBox.setStyle("-fx-background-color: whitesmoke;"
                + "-fx-padding: 15px;" + "-fx-border-width: 3px 0px;"
                + "-fx-border-style: solid;" + "-fx-border-color: gray;"
                + "-fx-alignment: center;");
        
        return topBox;
    }
    
    /**
     * Construit la HBox où le joueur peut choisir s'il est distant ou local.
     * 
     * @return La HBox où le joueur peut choisir s'il est distant ou local.
     */
    private HBox createPlayerLocationBox() {
        
        final int PLACE_PlayersPane_IN_PANE = 2;
        final Text playerTypeText = new Text("Type de Partie");
        playerTypeText.setStyle("-fx-font: 17 Optima;");
        
        gameTypeChoiceBox = new ChoiceBox<>(FXCollections.observableArrayList(GAME_TYPE_TABLE));
        gameTypeChoiceBox.getSelectionModel().selectFirst();
        
        gameTypeChoiceBox.getSelectionModel().selectedItemProperty().addListener((o, oV, nV)-> {
            if (nV.equals(GAME_TYPE_TABLE[0])) {
                mainPane.getChildren().add(PLACE_PlayersPane_IN_PANE, playersPane);                               
            }
            if (nV.equals(GAME_TYPE_TABLE[1])) {
                mainPane.getChildren().remove(playersPane);
            }
        });
        
        HBox PlayerTypeBox = new HBox(STANDARD_PADDING, playerTypeText, gameTypeChoiceBox);
        
        PlayerTypeBox.setAlignment(Pos.CENTER);
        PlayerTypeBox.setStyle("-fx-background-color: whitesmoke;"
                + "-fx-padding: 15px;" + "-fx-border-width: 3px 0px;"
                + "-fx-border-style: solid;" + "-fx-border-color: gray;"
                );
        
        
        return PlayerTypeBox;
    }
    
    /**
     * Construit le bouton pour lancer la partie.
     * 
     * @return Le bouton de start.
     */
    private HBox createStartButton() {
        //Constante pour le bouton        
        final String BACKGROUND_COLOR_ENTERRED = "#FEA7A8"; //rouge clair
        final String BACKGROUND_COLOR_NOT_ENTERRED = "transparent";
        final Effect EFFECT_ENTERRED = new Lighting();
        
        Button startButton = new Button("start"); 
        startButton.setTextFill(Color.DARKRED);
        
        SimpleStringProperty buttonBackgroundColorProperty = new SimpleStringProperty(BACKGROUND_COLOR_NOT_ENTERRED);
        startButton.styleProperty().bind(Bindings.format("-fx-font-weight: bold;-fx-border-radius: %s;-fx-background-color: %s;-fx-border-color: grey;", "50%", buttonBackgroundColorProperty));
        
        //Gère les effets quand on entre/sort du bouton avec la souris
        startButton.setOnMouseEntered(event -> {
            buttonBackgroundColorProperty.set(BACKGROUND_COLOR_ENTERRED);
            startButton.setEffect(EFFECT_ENTERRED);
        });
        startButton.setOnMouseExited(event -> {
            buttonBackgroundColorProperty.set(BACKGROUND_COLOR_NOT_ENTERRED);
            startButton.setEffect(null);
        });
        
        //Lance la partie en récoltant les informations
        startButton.setOnMouseClicked(event ->{
            List<String> args = extractInformations();       
        
            Play.startGame(args);
        });        
        
        HBox box = new HBox(startButton);
        
        
        box.setStyle("-fx-alignment: center;"
                + "-fx-background-color: whitesmoke;"
                + "-fx-padding: 15px;" 
                + "-fx-border-width: 3px 0px;"
                + "-fx-border-style: solid;" 
                + "-fx-border-color: gray;"
                );
        return box;
    }
    
    /**
     * Construit la VBox consituée des 4 lignes pour les joueurs.
     * 
     * @return La VBox des joueurs.
     */
    private VBox createPlayersPane() {
        
        HBox[] playerTable = new HBox[PlayerId.COUNT];
        for(PlayerId pId: PlayerId.ALL) {
            playerTable[pId.ordinal()] = createOnePlayer(pId);
        }
        
        VBox playerPane = new VBox(playerTable);
        
        return playerPane;
    }
    
    /**
     * Construit la HBox d'un joueur.
     * 
     * @return La HBox d'un joueur.
     */
    private HBox createOnePlayer(PlayerId pId) {
        Text nbrPlayer = new Text("Joueur "+(pId.ordinal()+1) + ":");
        
        ChoiceBox<String> playerTypeChoice = new ChoiceBox<>(FXCollections.observableArrayList(PLAYER_TYPE_TABLE));
        playerTypeChoice.getSelectionModel().selectFirst();
        
        PLAYERS_TYPE.put(pId, playerTypeChoice);
        
        playerTypeChoice.getSelectionModel().selectedItemProperty().addListener((o, oV, nV)-> {
            
            HBox playerLine = (HBox)playersPane.getChildren().get(pId.ordinal());

            //Si le joueur est humain, il n'y a pas de 3ème argument
            if (nV.equals(PLAYER_TYPE_TABLE[0])) {
                playerLine.getChildren().remove(IA_LEVEL_BOX.get(pId));
                playerLine.getChildren().remove(IP_TEXT_FIELD .get(pId));

            }
            //Si le joueur est distant, le 3ème argument est un TextField pour l'adresse IP
            else if (nV.equals(PLAYER_TYPE_TABLE[1])) {
                playerLine.getChildren().remove(IA_LEVEL_BOX.get(pId));
                playerLine.getChildren().add(IP_TEXT_FIELD.get(pId));
            }
            //Si le joueur est simulé, le 3ème argument est un ChoiceBox pour le niveau de l'IA
            else {
                playerLine.getChildren().remove(IP_TEXT_FIELD.get(pId));
                playerLine.getChildren().add(IA_LEVEL_BOX.get(pId));
            }
        });
        
        
        TextField playerName = new TextField();
        playerName.setPromptText("Nom du joueur");
//        playerName.setStyle("-fx-border-color: red");
        
        PLAYERS_NAME.put(pId, playerName);
        
        HBox onePlayerLine = new HBox(STANDARD_PADDING, nbrPlayer, playerTypeChoice, playerName);
        
        onePlayerLine.setAlignment(Pos.CENTER_LEFT);
        onePlayerLine.setStyle(
                "-fx-font: 16 Optima;" + "-fx-background-color: lightgray;"
                        + "-fx-padding: 5px;");
        
        return onePlayerLine;
    }
    
    /**
     * Initialize une map qui pour chaque playerId lui attribue une HBox pour le niveau de l'IA.
     * 
     * @return: L'ensemble des box pour le niveau des IA.
     */
    private Map<PlayerId, HBox> initializeIAChoiceBox(){
        Map<PlayerId, HBox> IAMap = new HashMap<>();
        
        for (PlayerId pId: PlayerId.ALL) {
            Text textIA = new Text("Niveau de l'IA:");
            
            ChoiceBox<String> IALevel = new ChoiceBox<>(FXCollections.observableArrayList(IA_LEVEL_CHOICES_TABLE));
            IALevel.getSelectionModel().select(1);
            IA_LEVEL_CHOICE.put(pId, IALevel);
            
            
            HBox IABox = new HBox(STANDARD_PADDING, textIA, IALevel);
            IABox.setAlignment(Pos.CENTER);
            
            IAMap.put(pId, IABox);
        }
        return IAMap;
    }
    
    /**
     * Initialize une map qui pour chaque playerId lui attribue une HBox pour le niveau de l'IA.
     * 
     * @return: L'ensemble des box pour le niveau des IA.
     */
    private Map<PlayerId, TextField> initializeIPAdresse(){
        Map<PlayerId, TextField> IPAdresseMap = new HashMap<>();
        
        for (PlayerId pId: PlayerId.ALL) {            
            
            TextField IPAdresse = new TextField();
            IPAdresse.setPromptText("Adresse IP");
                        
            IPAdresseMap.put(pId, IPAdresse);
        }
        return IPAdresseMap;
    }
    
    /**
     * Prend toutes les informations entrées par l'utilisateur et crée un
     * tableau de String permettant ensuite de lancer la partie.
     * 
     * @return: Une String avec toutes les infos permettant de lancer la partie.
     */
    private List<String> extractInformations() {
        
        List<String> args = new ArrayList<>();       
        
        String gameType = gameTypeChoiceBox.getValue();
        args.add(gameType);
        
        if (gameType.equals(GAME_TYPE_TABLE[0])) {
            for (PlayerId pId: PlayerId.ALL) {
                args.add(extractPlayerInformation(pId));
            }
        }        
        
        return args;
    }
    
    /**
     * Prend toutes les informations entrées par l'utilisateur pour un joueur
     * et crée une String avec.
     * 
     * @return: Une String pour un joueur.
     */
    private String extractPlayerInformation(PlayerId pId) {
        
        
        StringJoiner joiner = new StringJoiner(":");
        
        String playerType = PLAYERS_TYPE.get(pId).getValue();
        String playerName = PLAYERS_NAME.get(pId).getText();
        
        joiner.add(playerType);
        joiner.add(playerName);
        
        
        if (playerType.equals(PLAYER_TYPE_TABLE[1])) {//Distant
            String IPAdress = IP_TEXT_FIELD.get(pId).getText();
            joiner.add(IPAdress);
        }
        else if(playerType.equals(PLAYER_TYPE_TABLE[2])) {//Simulé
            String IALevel = IA_LEVEL_CHOICE.get(pId).getValue();
            joiner.add(IALevel);
        }        
        
        return joiner.toString();
    }
    
    /**
     * Construit la fenêtre
     * 
     * @return la fenêtre construite
     */
    public Stage createStage() {

        Stage stage = new Stage();
        stage.setScene(scene);
        stage.setTitle("Javass - Launcher");

        return stage;
    }
}
