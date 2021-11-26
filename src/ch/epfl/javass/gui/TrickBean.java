package ch.epfl.javass.gui;

import ch.epfl.javass.jass.Card;
import ch.epfl.javass.jass.PlayerId;
import ch.epfl.javass.jass.Trick;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableMap;


/**
 * Classe contenant les proprietes d'un pli dans le but
 * de pouvoir les observer.
 * 
 * @author Antoine Masanet (288366)
 * @author Loïc Houmard (297181)
 *
 */
public final class TrickBean 
{   
    private final SimpleObjectProperty<Card.Color> trump=new SimpleObjectProperty<>();
    private final SimpleObjectProperty<PlayerId> winningPlayer=new SimpleObjectProperty<>();
    private final ObservableMap <PlayerId, Card> trick= FXCollections.observableHashMap();
    
    
    /**
     * Retourne la propriete correspondant a l'atout du tour.
     * 
     * @return la propriete correspondant a l'atout du tour.
     */
    public ReadOnlyObjectProperty<Card.Color> trumpProperty()
    {
        return trump;
    }
    
    /**
     * Modifie la propriete stockant l'atout du tour.
     * 
     * @param newTrump: Le nouvel atout du tour.
     */
    public void setTrump(Card.Color newTrump) 
    {
        if(trump.get()!=newTrump)
            trump.set(newTrump);
    }
    
    /**
     * Retourne la propriete correspondant au joueur gagnant le pli.
     * 
     * @return la propriete correspondant au joueur gagnant le pli.
     */
    public ReadOnlyObjectProperty <PlayerId> winningPlayerProperty()
    {
        return winningPlayer;
    }
    
    /**
     * Retourne une map reliant chaque joueur avec la carte qu'il a joue durant le pli.
     * 
     * @return une map du pli.
     */
    public ObservableMap<PlayerId, Card> trick() 
    { 
       return FXCollections.unmodifiableObservableMap(trick); 
    }
    
    /**
     * Modifie le pli stocke dans la propriete trick par un nouveau pli.
     * 
     * @param newTrick: Le nouveau pli.
     */
    public void setTrick(Trick newTrick)
    {
      winningPlayer.set(newTrick.isEmpty() ? null : newTrick.winningPlayer());
      
      trick.clear();
      
      for(int i=0;i<newTrick.size();++i)
      {  
         trick.put(newTrick.player(i), newTrick.card(i));
      } 
    }
    
    /**
     * Modifie le pli en début de tour en mettant toutes les valeurs à null.
     */
    public void setEmptyTrick() {
        
        winningPlayer.set(null);
        trump.set(null);
        
        for (PlayerId pId: PlayerId.ALL) {
            trick.put(pId, null);
        }
    }
}
