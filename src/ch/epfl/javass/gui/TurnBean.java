package ch.epfl.javass.gui;

import ch.epfl.javass.jass.PlayerId;
import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;

/**
 * Classe contenant les propriétés changeant en début de tour (pour choisir l'atout par exemple).
 * 
 * @author Antoine Masanet (288366)
 * @author Loïc Houmard (297181)
 *
 */
public final class TurnBean {
    
    /**Propriétés disant si le joueur graphique doit choisir atout ou pas (pour afficher le trumpPane).*/
    private final SimpleBooleanProperty mustChooseTrump = new SimpleBooleanProperty();
    /**Propriétés disant si le joueur graphique peut chibrer ou pas (pour afficher le bouton de chibre).*/
    private final SimpleBooleanProperty canPass = new SimpleBooleanProperty();
    /**Propriétés pour le premier joueur du tour*/
    private final SimpleObjectProperty<PlayerId> firstTurnPlayer = new SimpleObjectProperty<>();
    /**Propriétés pour le premier joueur du tour*/
    private final SimpleObjectProperty<PlayerId> trumpChooser = new SimpleObjectProperty<>();
    
    /**
     * Retourne la propriete mustChooseTrump.
     * 
     * @return la propriete mustChooseTrump.
     */
    public ReadOnlyBooleanProperty mustChooseTrumpProperty() {
        return mustChooseTrump;
    }
    
    /**
     * Modifie le boolean de mustChooseTrump.
     * 
     * @param newMustChooseTrump: Le nouveau boolean.
     */
    public void setMustChooseTrump(boolean newMustChooseTrump) {
        mustChooseTrump.set(newMustChooseTrump);
    }
    
    /**
     * Retourne la propriete canPass.
     * 
     * @return la propriete canPass.
     */
    public ReadOnlyBooleanProperty canPassProperty() {
        return canPass;
    }
    
    /**
     * Modifie le boolean de canPass.
     * 
     * @param newCanPass: Le nouveau boolean.
     */
    public void setCanPass(boolean newCanPass) {
        canPass.set(newCanPass);
    }
    
    /**
     * Retourne la propriete du premier joueur.
     * 
     * @return la propriete du premier joueur.
     */
    public ReadOnlyObjectProperty<PlayerId> firstTurnPlayerProperty() {
        return firstTurnPlayer;
    }
    
    /**
     * Modifie le premier joueur.
     * 
     * @param newFirstPlayerTurn: Le nouveau premier joueur.
     */
    public void setFirstTurnPlayer(PlayerId newFirstTurnPlayer) {
        firstTurnPlayer.set(newFirstTurnPlayer);
    }
    
    /**
     * Retourne la propriete du joueur ayant choisi l'atout.
     * 
     * @return la propriete du joueur ayant choisi l'atout.
     */
    public ReadOnlyObjectProperty<PlayerId> trumpChooserProperty() {
        return trumpChooser;
    }
    
    /**
     * Modifie le joueur ayant choisi l'atout.
     * 
     * @param newTrumpChooser: Le nouveau joueur ayant choisi l'atout.
     */
    public void setTrumpChooser(PlayerId newTrumpChooser) {
        trumpChooser.set(newTrumpChooser);
    }
}
