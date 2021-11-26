/**
 * 
 */
package ch.epfl.javass.gui;

import ch.epfl.javass.jass.Card;
import ch.epfl.javass.jass.Card.Color;
import ch.epfl.javass.jass.Card.Rank;
import ch.epfl.javass.jass.PackedCard;
import javafx.collections.FXCollections;
import javafx.collections.ObservableMap;
import javafx.scene.effect.GaussianBlur;
import javafx.scene.image.Image;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;

/**
 * @author Antoine Masanet (288366)
 * @author Loïc Houmard (297181)
 *
 */
public interface ImageToolBox {

    
    // Le rapport pour la taille des images par rapport à la taille de base
    // (SIZE_RATIO* plus petite que celle de base)
    public static final int SIZE_RATIO = 2;
    //L'opacité max
    public static final double FULL_OPACITY = 1.0;
    
    /**
     * Construit un halo de lumière flouté.
     * 
     * @param width: La largeur du halo.
     * @param height: La hauteur du halo.
     * @param blurRadius: Le rayon de la ligne floutée (le rayon du pourtour).
     * 
     * @return Un halo de lumière flouté.
     */
    public static Rectangle createRectangleHalo(int width, int height, double blurRadius, String color) {

        Rectangle halo = new Rectangle(width, height);

        halo.setStyle("-fx-arc-width: 20;" + "-fx-arc-height: 20;"
                + "-fx-fill: transparent;" + "-fx-stroke: "+color+";"
                + "-fx-stroke-width: 5;" + "-fx-opacity: 0.5;");

        halo.setEffect(new GaussianBlur(blurRadius));

        return halo;
    }
    
    /**
     * Construit un halo de lumière flouté en cercle.
     * 
     * @param radius: Le rayon du halo.
     * @param blurRadius: Le rayon de la ligne floutée (le rayon du pourtour).
     * 
     * @return Un halo de lumière circulaire flouté.
     */
    public static Circle createRoundHalo(double radius, double blurRadius, String color) {

        Circle halo = new Circle(radius);

        halo.setStyle("-fx-arc-width: 20;" + "-fx-arc-height: 20;"
                + "-fx-fill: transparent;" + "-fx-stroke: "+color+";"
                + "-fx-stroke-width: 5;" + "-fx-opacity: 0.5;");

        halo.setEffect(new GaussianBlur(blurRadius));

        return halo;
    }

    /**
     * Construit une table associative observable Color-Image pour toutes les couleurs.
     * 
     * @return Une table associative observable Color-Image.
     */
    public static ObservableMap<Color, Image> initializeTrumpImages() {
        ObservableMap<Color, Image> trumpMap = FXCollections
                .observableHashMap();

        for (Color color : Color.ALL) {
            trumpMap.put(color, imageFromTrump(color));
        }

        return FXCollections.unmodifiableObservableMap(trumpMap);

    }

    /**
     * Construit une table associative observable Card-Image pour toutes les cartes.
     * 
     * @return Une table associative observable Card-Image.
     */
    public static ObservableMap<Card, Image> initializeCardImages() {
        ObservableMap<Card, Image> cardMap = FXCollections.observableHashMap();

        for (Color color : Color.ALL) {
            for (Rank rank : Rank.ALL) {
                Card card = Card.of(color, rank);

                cardMap.put(card,
                        imageFromCard(card, CardSize.CARD_BOARD));
            }
        }
        return FXCollections.unmodifiableObservableMap(cardMap);
    }

    /**
     * Construit une vue d'image de la carte passée en argument à la taille donnée (soit 
     * 160 pour une image de taille 160×240 pixels ou 240 pour une image de taille
     * 240×360 pixels).
     * 
     * @param card: La carte dont on veut l'image.
     * @param cardSize: La taille en pixel de l'image (160 ou 240).
     * 
     * @return Une vue de l'image de la carte passée en argument à la taille donnée.
     */
    public static Image imageFromCard(Card card, CardSize imageSize) {

        assert card != null && PackedCard.isValid(card.packed());

        StringBuilder builder = new StringBuilder("/card_");
        builder.append(card.color().ordinal()).append('_')
                .append(card.rank().ordinal()).append('_')
                .append(imageSize.width).append(".png");

        return new Image(builder.toString());
    }

    /**
     * Construit une vue de l'image de l'atout passé en argument.
     * 
     * @param trump: La couleur d'atout dont on veut l'image.
     * 
     * @return Une vue de l'image de l'atout passé en argument.
     */
    public static Image imageFromTrump(Card.Color trump) {

        StringBuilder builder = new StringBuilder("/trump_");
        builder.append(trump.ordinal()).append(".png");

        return new Image(builder.toString());
    }

    /**
     * Enumeration contenant toutes les tailles des images de cartes.
     * 
     * @author Antoine Masanet (288366)
     * @author Loïc Houmard (297181)
     *
     */
    public enum CardSize {

        CARD_HAND(160, 240), CARD_BOARD(240, 360);

        /**
         * Nombre de taille de cartes.
         */

        private final int width;
        private final int height;

        /**
         * Constructeur privee.
         *  
         * @param width: La largeur de l'image.
         * @param height: La hauteur de l'image.  
         */
        private CardSize(int width, int height) {
            this.width = width;
            this.height = height;
        }
        
        /**
         * Return the width of the cardImage
         * @return: width
         */
        public int getWidth() {
            return this.width;
        }
        
        /**
         * Return the height of the cardImage
         * 
         * @return height
         */
        public int getHeight() {
            return this.height;
        }

        /**
         * Redefinis la methode toString de Object.
         * 
         * @return Une représentation textuelle.
         */
        @Override
        public String toString() {
            StringBuilder builder = new StringBuilder();
            builder.append(width).append("x").append(height);

            return builder.toString();
        }
    }
}
