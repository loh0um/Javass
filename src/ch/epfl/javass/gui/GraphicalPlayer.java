package ch.epfl.javass.gui;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ArrayBlockingQueue;

import ch.epfl.javass.jass.Card;
import ch.epfl.javass.jass.Card.Color;
import ch.epfl.javass.jass.Jass;
import ch.epfl.javass.jass.PlayerId;
import ch.epfl.javass.jass.TeamId;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.ObjectBinding;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.beans.property.ReadOnlyIntegerProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.ObservableList;
import javafx.collections.ObservableMap;
import javafx.collections.ObservableSet;
import javafx.geometry.HPos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.effect.ColorAdjust;
import javafx.scene.effect.Effect;
import javafx.scene.effect.Lighting;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import javafx.stage.Stage;

/**
 * Classe se chargeant de "dessiner" l'interface graphique d'un joueur.
 * 
 * @author Antoine Masanet (288366)
 * @author Loïc Houmard (297181)
 * 
 */
public final class GraphicalPlayer {

    private final Scene scene;
    private final PlayerId ownId;
    private final Map<PlayerId, String> names;


    //Valeur représentant une carte invalide dans la queue (quand le joueuer veut chibrer)
    public static final int CHIBRE_INT = -1;
    // Map de toutes les cartes/couleurs avec leur image correspondante
    private final ObservableMap<Card, Image> CARD_TO_IMAGE = ImageToolBox.initializeCardImages();
    private final ObservableMap<Color, Image> TRUMP_TO_IMAGE = ImageToolBox.initializeTrumpImages();

    /**
     * Construit l'ensemble de l'interface graphique à l'exception de la fenêtre elle-même.
     * 
     * @param ownId: L'identitée du joueur.
     * @param names: Une table associative identitée des joueurs-noms des joueurs.
     * @param scoreBean: Le bean des scores.
     * @param trickBean: Le bean des plis.
     */
    public GraphicalPlayer(PlayerId ownId, Map<PlayerId, String> names,
            ScoreBean scoreBean, TrickBean trickBean, HandBean handBean, TurnBean turnBean, 
            ArrayBlockingQueue<Card> cardCommunicationQueue, ArrayBlockingQueue<Integer> colorCommunicationQueue) {

        // utilisé dans createStage() pour le titre
        this.ownId = ownId;
        this.names = names;

        BorderPane victoryPanes[] = createVictoryPanes(names, scoreBean);

        GridPane trumpPane = createTrumpPane(handBean, turnBean, colorCommunicationQueue);
        GridPane trickPane = createTrickPane(trickBean, turnBean);
        
        StackPane centerPane = new StackPane();
        centerPane.getChildren().addAll(trickPane, trumpPane);
        
        BorderPane mainPane = new BorderPane();
        mainPane.setTop(createScorePane(scoreBean));
        mainPane.setCenter(centerPane);
        mainPane.setBottom(createHandPane(handBean, turnBean, cardCommunicationQueue));
        
        StackPane stackedPane = new StackPane();
        stackedPane.getChildren().addAll(mainPane,
                victoryPanes[TeamId.TEAM_1.ordinal()],
                victoryPanes[TeamId.TEAM_2.ordinal()]);

        this.scene = new Scene(stackedPane);

    }

    /**
     * Construit le panneau des scores
     * 
     * @param names: La table associative identitée des joueurs-noms des joueurs.
     * @param scoreBean: Le bean des scores.
     * 
     * @return Le panneau des scores.
     */
    private GridPane createScorePane(ScoreBean scoreBean) {

        GridPane scorePane = new GridPane();

        scorePane.setStyle(
                "-fx-font: 16 Optima;" + "-fx-background-color: lightgray;"
                        + "-fx-padding: 5px;" + "-fx-alignment: center;");

        // Crée les 2 lignes de scores des équipes
        for (TeamId team : TeamId.ALL) {
            int teamOrdinal = team.ordinal();

            Text name = new Text(names.get(PlayerId.ALL.get(teamOrdinal))
                    + " et "
                    + names.get(PlayerId.ALL.get(teamOrdinal + TeamId.COUNT))
                    + " : ");

            ReadOnlyIntegerProperty turnPointsProperty = scoreBean
                    .turnPointsProperty(team);
            Text turnPoints = new Text();
            turnPoints.textProperty()
                    .bind(Bindings.convert(turnPointsProperty));

            Text lastTrickWonPoints = new Text();
            SimpleStringProperty lastTrickWonPointsProperty = new SimpleStringProperty();
            turnPointsProperty.addListener((o, oV, nV) -> {
                int difference = nV.intValue() - oV.intValue();
                lastTrickWonPointsProperty.setValue(difference <= 0 ? ""
                        : "(+" + String.valueOf(difference) + ")");
            });

            lastTrickWonPoints.textProperty().bind(lastTrickWonPointsProperty);

            Text gamePoints = new Text();
            gamePoints.textProperty()
                    .bind(Bindings.convert(scoreBean.gamePointsProperty(team)));

            scorePane.addRow(teamOrdinal, name, turnPoints, lastTrickWonPoints,
                    new Text("/Total : "), gamePoints);
        }

        return scorePane;
    }

    /**
     * Construit le panneau du pli
     * 
     * @param names: La table associative identitée des joueurs-noms des joueurs.
     * @param trickBean: Le bean du pli.
     * 
     * @return Le panneau du pli.
     */
    private GridPane createTrickPane(TrickBean trickBean, TurnBean turnBean) {


        //Constante pour le halo
        final String HALO_COLOR = "lightpink";
        
        // Constantes utilisées pour instancier les différents Nodes
        final int TRUMP_IMAGE_WIDTH = 101;
        final int TRUMP_IMAGE_HEIGHT = 101;
        final int BOARD_CARD_IMAGE_WIDTH = ImageToolBox.CardSize.CARD_BOARD.getWidth()
                / ImageToolBox.SIZE_RATIO;
        final int BOARD_CARD_IMAGE_HEIGHT = ImageToolBox.CardSize.CARD_BOARD.getHeight()
                / ImageToolBox.SIZE_RATIO;
        final int BLUR_RADIUS = 4;
        
        //Constnates pour l'image quand un joueur chibre
        final Image CROSS_IMAGE = new Image("/cross2.png");//Image de SmashIcon téléchargé sur Flaticon
        final int SMALL_IMAGE_WIDTH = 20;
        final int SMALL_IMAGE_HEIGHT = 20;
        final int PADDING_NAME_TRUMP = 5;
        final ImageView NOTHING = new ImageView();


        // Constantes pour placer les cartes/noms et l'atout dans la pane
        final int TRUMP_COLOMN_INDEX = 1;
        final int TRUMP_ROW_INDEX = 1;

        final int LEFT_PANE_COLUMN = 0;
        final int LEFT_PANE_ROW = 0;
        final int LEFT_PANE_COLUMN_SPAN = 1;
        final int LEFT_PANE_ROW_SPAN = 3;

        final int RIGHT_PANE_COLUMN = 2;
        final int RIGHT_PANE_ROW = 0;
        final int RIGHT_PANE_COLUMN_SPAN = 1;
        final int RIGHT_PANE_ROW_SPAN = 3;

        final int TOP_PANE_COLUMN = 1;
        final int TOP_PANE_ROW = 0;

        final int BOTTOM_PANE_COLUMN = 1;
        final int BOTTOM_PANE_ROW = 2;

        // Constantes pour identifier les joueurs et leurs positions
        final PlayerId BOTTOM_PLAYER = ownId;
        final PlayerId LEFT_PLAYER = PlayerId.ALL
                .get((ownId.ordinal() + 3) % PlayerId.COUNT);
        final PlayerId TOP_PLAYER = PlayerId.ALL
                .get((ownId.ordinal() + 2) % PlayerId.COUNT);
        final PlayerId RIGHT_PLAYER = PlayerId.ALL
                .get((ownId.ordinal() + 1) % PlayerId.COUNT);

        GridPane trickPane = new GridPane();
        trickPane.setStyle("-fx-background-color: whitesmoke;"
                + "-fx-padding: 5px;" + "-fx-border-width: 3px 0px;"
                + "-fx-border-style: solid;" + "-fx-border-color: gray;"
                + "-fx-alignment: center;");

        ImageView trumpImage = new ImageView();
        trumpImage.imageProperty().bind(
                Bindings.valueAt(TRUMP_TO_IMAGE, trickBean.trumpProperty()));
        trumpImage.setFitWidth(TRUMP_IMAGE_WIDTH);
        trumpImage.setFitHeight(TRUMP_IMAGE_HEIGHT);
        trickPane.add(trumpImage, TRUMP_COLOMN_INDEX, TRUMP_ROW_INDEX);
        GridPane.setHalignment(trumpImage, HPos.CENTER);

        // Crée les paires d'images des joueurs avec leurs noms et les place
        // dans le gridPane
        for (PlayerId playerId : PlayerId.ALL) {

            // Crée le halo de couleur (seulement visible sur la carte gagnante)
            Rectangle halo = ImageToolBox.createRectangleHalo(BOARD_CARD_IMAGE_WIDTH,
                    BOARD_CARD_IMAGE_HEIGHT, BLUR_RADIUS, HALO_COLOR);
            halo.visibleProperty().bind(
                    trickBean.winningPlayerProperty().isEqualTo(playerId));

            Text name = new Text(names.get(playerId));
            name.setStyle("-fx-font: 14 Optima;" + "-fx-alignment: center;");
            
            
            /*Place à côté des noms de l'équipe qui choisit atout, soit une 
              croix si le joueur a chibé ou l'atout qu'il a choisi*/
            ImageView chooseTrumpImage = new ImageView();
            
            chooseTrumpImage.imageProperty().bind(Bindings.when(Bindings.equal(turnBean.trumpChooserProperty(), playerId))
                    .then(trumpImage.imageProperty())
                    .otherwise(Bindings.when(Bindings.equal(turnBean.trumpChooserProperty(), playerId.teamMate())
                            .and(Bindings.equal(turnBean.firstTurnPlayerProperty(), playerId)))
                                .then(CROSS_IMAGE).otherwise(NOTHING.imageProperty())));
            
            chooseTrumpImage.setFitHeight(SMALL_IMAGE_HEIGHT);
            chooseTrumpImage.setFitWidth(SMALL_IMAGE_WIDTH);
            
            HBox nameAndTrumpChoose = new HBox(PADDING_NAME_TRUMP, name, chooseTrumpImage);
            nameAndTrumpChoose.setStyle("-fx-alignment: center;");

            //Crée les cartes
            ImageView cardImage = new ImageView();
            cardImage.imageProperty().bind(Bindings.valueAt(CARD_TO_IMAGE,
                    Bindings.valueAt(trickBean.trick(), playerId)));
            cardImage.setFitHeight(BOARD_CARD_IMAGE_HEIGHT);
            cardImage.setFitWidth(BOARD_CARD_IMAGE_WIDTH);

            StackPane cardAndHalo = new StackPane(cardImage, halo);

            VBox coupleNameAndTrumpWithCard;

            // Place le nom du joueur sur la carte (sauf pour le joueur
            // principale où le nom est sous la carte)
            if (playerId == ownId) {
                coupleNameAndTrumpWithCard = new VBox(cardAndHalo, nameAndTrumpChoose);
            } else {
                coupleNameAndTrumpWithCard = new VBox(nameAndTrumpChoose, cardAndHalo);
            }
            coupleNameAndTrumpWithCard
                    .setStyle("-fx-padding: 5px;" + "-fx-alignment: center;");
            
            // Place les couples nom-carte au bon endroit dans la gridPane du
            // trick
            if (playerId.equals(BOTTOM_PLAYER)) {
                trickPane.add(coupleNameAndTrumpWithCard, BOTTOM_PANE_COLUMN,
                        BOTTOM_PANE_ROW);
            } else if (playerId.equals(LEFT_PLAYER)) {
                trickPane.add(coupleNameAndTrumpWithCard, LEFT_PANE_COLUMN, LEFT_PANE_ROW,
                        LEFT_PANE_COLUMN_SPAN, LEFT_PANE_ROW_SPAN);
            } else if (playerId.equals(TOP_PLAYER)) {
                trickPane.add(coupleNameAndTrumpWithCard, TOP_PANE_COLUMN, TOP_PANE_ROW);
            } else if (playerId.equals(RIGHT_PLAYER)) {
                trickPane.add(coupleNameAndTrumpWithCard, RIGHT_PANE_COLUMN, RIGHT_PANE_ROW,
                        RIGHT_PANE_COLUMN_SPAN, RIGHT_PANE_ROW_SPAN);
            }
        }

        return trickPane;
    }

    /**
     * Construit les panneaux de la victoire des 2 équipes
     * 
     * @param names: La table associative identitée des joueurs-noms des joueurs.
     * @param scoreBean: Le bean des scores.
     * 
     * @return Un tableau avec les 2 panneaux de la victoire.
     */
    private BorderPane[] createVictoryPanes(Map<PlayerId, String> names,
            ScoreBean scoreBean) {

        BorderPane[] bothWinningPane = new BorderPane[TeamId.COUNT];

        for (TeamId team : TeamId.ALL) {

            int teamOrdinal = team.ordinal();

            String nameFirstPlayer = names.get(PlayerId.ALL.get(teamOrdinal));
            String nameSecondPlayer = names
                    .get(PlayerId.ALL.get(teamOrdinal + TeamId.COUNT));

            Text victoryText = new Text();
            victoryText.textProperty()
                    .bind(Bindings.format(
                            "%s et %s ont gagné avec %d points contre %d.",
                            nameFirstPlayer, nameSecondPlayer,
                            scoreBean.totalPointsProperty(team),
                            scoreBean.totalPointsProperty(team.other())));

            BorderPane victoryPane = new BorderPane(victoryText);
            victoryPane.setStyle(
                    "-fx-font: 16 Optima; -fx-background-color: white;");

            victoryPane.visibleProperty()
                    .bind(scoreBean.winningTeamProperty().isEqualTo(team));

            bothWinningPane[teamOrdinal] = victoryPane;
        }
        return bothWinningPane;
    }

    /**
     * Construit le panneau de la main du joueur.
     * 
     * @param handBean: Le bean de la main.
     * @param queue: La queue où la carte que le joueur veut jouer est stoquée.
     * 
     * @return Un panneau représentant la main.
     */
    private HBox createHandPane(HandBean handBean, TurnBean turnBean,
            ArrayBlockingQueue<Card> queue) {

        // Constantes de taille
        final int Hand_CARD_IMAGE_HEIGHT = ImageToolBox.CardSize.CARD_HAND.getHeight()
                / ImageToolBox.SIZE_RATIO;
        final int Hand_CARD_IMAGE_WIDTH = ImageToolBox.CardSize.CARD_HAND.getWidth()
                / ImageToolBox.SIZE_RATIO;

        //Constantes pour les effets
        final double HIDDEN_OPACITY = 0.2;       
        final Effect EFFECT_ENTERRED = new Lighting();
        final Effect EFFECT_EXITED = new ColorAdjust();//Correspond à aucun effet


        Node[] children = new Node[Jass.HAND_SIZE];

        for (int i = 0; i < Jass.HAND_SIZE; ++i) {

            ObservableList<Card> observableHand = handBean.hand();
            ObservableSet<Card> observablePlayableCards = handBean
                    .playableCards();

            ObjectBinding<Card> cardProperty = Bindings.valueAt(observableHand,
                    i);

            ImageView cardImage = new ImageView();
            cardImage.imageProperty()
                    .bind(Bindings.valueAt(CARD_TO_IMAGE, cardProperty));
            cardImage.setFitHeight(Hand_CARD_IMAGE_HEIGHT);
            cardImage.setFitWidth(Hand_CARD_IMAGE_WIDTH);

            // Place la carte sélectionnée dans la queue lorsque le joueur click
            // dessus
            cardImage.setOnMouseClicked(event -> {
                if (queue.isEmpty()) {
                    queue.add(cardProperty.get());
                }
            });

            // Visibilité/activation de la carte
            BooleanProperty isPlayable = new SimpleBooleanProperty();
            ReadOnlyBooleanProperty mustChooseTrump = turnBean.mustChooseTrumpProperty();
            
            isPlayable.bind(Bindings.createBooleanBinding(() -> {
                return observablePlayableCards.contains(cardProperty.get());
            }, observablePlayableCards, observableHand));
            
            cardImage.opacityProperty().bind(Bindings.when(isPlayable.or(mustChooseTrump))
                    .then(ImageToolBox.FULL_OPACITY).otherwise(HIDDEN_OPACITY));
            cardImage.disableProperty().bind(isPlayable.not());
            
            
            //Mets un effect sur l'image quand on passe dessus
            BooleanProperty mouseOnCard = new SimpleBooleanProperty(false);
            cardImage.effectProperty().bind(Bindings.when(isPlayable.and(mouseOnCard))
                    .then(EFFECT_ENTERRED)
                    .otherwise(EFFECT_EXITED));
            cardImage.setOnMouseEntered(event -> {
                mouseOnCard.set(true);
            });
            cardImage.setOnMouseExited(event -> {
                mouseOnCard.set(false);
            });

            
            
            children[i] = cardImage;
        }

        HBox handPane = new HBox(children);

        handPane.setStyle(
                "-fx-background-color: lightgray;" + "-fx-spacing: 5px;"
                        + "-fx-padding: 5px;" + "-fx-alignment: center;");

        return handPane;
    }
    
    /**
     * Construit le panneau pour choisir l'atout.
     * 
     * @param handBean: Le bean de la main.
     * @param trumpBean: Le bean de l'atout.
     * @param queue: La queue où l'ordinal de la couleur (où -1 en cas de chibre)
     *               que le joueur veut jouer est stoquée.
     * 
     * @return Un panneau représentant la main.
     */
    private GridPane createTrumpPane(HandBean handBean, TurnBean turnBean, ArrayBlockingQueue<Integer> queue) {
        
        //Constantes pour la taille des cartes
        final int TRUMP_IMAGE_WIDTH = 120;
        final int TRUMP_IMAGE_HEIGHT = 120;
        
        //Constantes pour l'espaces entre les atouts:
        final double HGAP = 20;
        final double VGAP = 50;
        
        //Constante pour le halo et l'atout
        final int BLUR_RADIUS = 8;
        final double WIDTH_TO_RADIUS = 2.0;
        final String HALO_COLOR = "red";
        final double HIDDEN_OPACITY = 0.85;
        
        //Constante pour le bouton        
        final String BACKGROUND_COLOR_ENTERRED = "#FEA7A8"; //rouge clair
        final String BACKGROUND_COLOR_NOT_ENTERRED = "transparent";

        // Constantes pour placer les cartes/noms et l'atout dans la pane
        final int TRUMP_COLOMN_INDEX = 1;
        final int TRUMP_ROW_INDEX = 1;

        final int LEFT_PANE_COLUMN = 0;
        final int LEFT_PANE_ROW = 0;
        final int LEFT_PANE_COLUMN_SPAN = 1;
        final int LEFT_PANE_ROW_SPAN = 3;

        final int RIGHT_PANE_COLUMN = 2;
        final int RIGHT_PANE_ROW = 0;
        final int RIGHT_PANE_COLUMN_SPAN = 1;
        final int RIGHT_PANE_ROW_SPAN = 3;

        final int TOP_PANE_COLUMN = 1;
        final int TOP_PANE_ROW = 0;

        final int BOTTOM_PANE_COLUMN = 1;
        final int BOTTOM_PANE_ROW = 2;
        
        
        GridPane trumpPane = new GridPane();
        trumpPane.setStyle("-fx-background-color: whitesmoke;"
                + "-fx-padding: 5px;" + "-fx-border-width: 3px 0px;"
                + "-fx-border-style: solid;" + "-fx-border-color: gray;"
                + "-fx-alignment: center;");
        
        //gère la visibilité du Pane:
        trumpPane.visibleProperty().bind(turnBean.mustChooseTrumpProperty());
        
        //Crée les 4  images des couleurs et les stoque dans un tableau avec leur halo (dans un StackPane)
        List<Node> colorImages = new ArrayList<>();        
        for(Color c: Color.ALL) {
            
            ImageView colorImage = new ImageView();
            colorImage.setFitWidth(TRUMP_IMAGE_WIDTH);
            colorImage.setFitHeight(TRUMP_IMAGE_HEIGHT);

            colorImage.setImage(TRUMP_TO_IMAGE.get(c));
            
            Circle halo = ImageToolBox.createRoundHalo(TRUMP_IMAGE_WIDTH/WIDTH_TO_RADIUS, BLUR_RADIUS, HALO_COLOR);
            halo.setVisible(false);

            StackPane cardAndHalo = new StackPane(halo, colorImage);
            
            //Rend le halo de lumière visible quand on passe sur l'atout et invisible sinon        
            colorImage.setOnMouseEntered(event -> {
                halo.setVisible(true);
                colorImage.setOpacity(HIDDEN_OPACITY);
            });
            colorImage.setOnMouseExited(event -> {
                halo.setVisible(false);
                colorImage.setOpacity(ImageToolBox.FULL_OPACITY);
            });
            
            //Ajoute l'ordinal de la couleur à la queue quand on clique sur l'image de l'atout
            colorImage.setOnMouseClicked(event -> {
                if (queue.isEmpty()) {
                    queue.add(c.ordinal());
                }
            });
            
            colorImages.add(cardAndHalo);       
        }
        
        //Gère le bouton de chibre   
        SimpleStringProperty buttonBackgroundColorProperty = new SimpleStringProperty(BACKGROUND_COLOR_NOT_ENTERRED);
        
        Button passButton = new Button("CHIBER");
        passButton.styleProperty().bind(Bindings.format("-fx-font-weight: bold;-fx-border-radius: %s;-fx-background-color: %s;-fx-border-color: red;", "50%", buttonBackgroundColorProperty));
        
        passButton.setOnMouseClicked(event -> {
                if (queue.isEmpty()) {
                    queue.add(CHIBRE_INT);
                }
        });
        passButton.setOnMouseEntered(event -> {
            buttonBackgroundColorProperty.set(BACKGROUND_COLOR_ENTERRED);
        });
        passButton.setOnMouseExited(event -> {
            buttonBackgroundColorProperty.set(BACKGROUND_COLOR_NOT_ENTERRED);
        });
        
        passButton.visibleProperty().bind(turnBean.canPassProperty());
        passButton.disableProperty().bind(turnBean.canPassProperty().not());

        //Place le tout dans le pane
        trumpPane.add(colorImages.get(Color.DIAMOND.ordinal()), BOTTOM_PANE_COLUMN,
                BOTTOM_PANE_ROW);

        trumpPane.add(colorImages.get(Color.SPADE.ordinal()), LEFT_PANE_COLUMN, LEFT_PANE_ROW,
                LEFT_PANE_COLUMN_SPAN, LEFT_PANE_ROW_SPAN);

        trumpPane.add(colorImages.get(Color.HEART.ordinal()), TOP_PANE_COLUMN, TOP_PANE_ROW);

        trumpPane.add(colorImages.get(Color.CLUB.ordinal()), RIGHT_PANE_COLUMN, RIGHT_PANE_ROW,
                RIGHT_PANE_COLUMN_SPAN, RIGHT_PANE_ROW_SPAN);
        
        trumpPane.add(passButton, TRUMP_COLOMN_INDEX, TRUMP_ROW_INDEX);
        
        
        for (Node node: trumpPane.getChildren()) {
            GridPane.setHalignment(node, HPos.CENTER);
        }
        
        trumpPane.setHgap(HGAP);
        trumpPane.setVgap(VGAP);
     
        return trumpPane;
    }
    
    /**
     * Construit la fenetre.
     * 
     * @return  La fenetre construite.
     */
    public Stage createStage() {
        Stage stage=new Stage();
        stage.setScene(scene);
        stage.setTitle("Javass - "+ names.get(ownId));
        return stage;
    }

}