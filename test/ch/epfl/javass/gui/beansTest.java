package ch.epfl.javass.gui;

import org.junit.jupiter.api.Test;

import ch.epfl.javass.jass.Card;
import ch.epfl.javass.jass.Card.Color;
import ch.epfl.javass.jass.Card.Rank;
import ch.epfl.javass.jass.CardSet;
import ch.epfl.javass.jass.PlayerId;
import ch.epfl.javass.jass.TeamId;
import ch.epfl.javass.jass.Trick;
import javafx.beans.value.ChangeListener;
import javafx.collections.ListChangeListener;
import javafx.collections.MapChangeListener;
import javafx.collections.SetChangeListener;


/**
 * @author Antoine Masanet (288366)
 * @author Loïc Houmard (297181)
 *
 */

public class beansTest 
{
    @Test
    public void handBeanTest()
    {
        System.out.println("******************HandBeanTest:**********************");
        
        HandBean hb = new HandBean();
        ListChangeListener<Card> listener = e -> System.out.println(e);
        hb.hand().addListener(listener);
    
        CardSet h = CardSet.EMPTY
          .add(Card.of(Color.SPADE, Rank.SIX))
          .add(Card.of(Color.SPADE, Rank.NINE))
          .add(Card.of(Color.SPADE, Rank.JACK))
          .add(Card.of(Color.HEART, Rank.SEVEN))
          .add(Card.of(Color.HEART, Rank.ACE))
          .add(Card.of(Color.DIAMOND, Rank.KING))
          .add(Card.of(Color.DIAMOND, Rank.ACE))
          .add(Card.of(Color.CLUB, Rank.TEN))
          .add(Card.of(Color.CLUB, Rank.QUEEN));
        
        hb.setHand(h);
        
        while (! h.isEmpty()) {
          h = h.remove(h.get(0));
          hb.setHand(h);
        }

        SetChangeListener<Card> setListener=e -> System.out.println(e);

        hb.playableCards().addListener(setListener);
        
        CardSet pc = CardSet.EMPTY
                .add(Card.of(Color.SPADE, Rank.SIX))
                .add(Card.of(Color.SPADE, Rank.NINE))
                .add(Card.of(Color.SPADE, Rank.JACK))
                .add(Card.of(Color.HEART, Rank.SEVEN))
                .add(Card.of(Color.HEART, Rank.ACE))
                .add(Card.of(Color.DIAMOND, Rank.KING))
                .add(Card.of(Color.DIAMOND, Rank.ACE))
                .add(Card.of(Color.CLUB, Rank.TEN))
                .add(Card.of(Color.CLUB, Rank.QUEEN));
        
        CardSet newPc= CardSet.EMPTY
                .add(Card.of(Color.SPADE, Rank.SEVEN))
                .add(Card.of(Color.SPADE, Rank.NINE))
                .add(Card.of(Color.HEART, Rank.KING))
                .add(Card.of(Color.DIAMOND, Rank.ACE))
                .add(Card.of(Color.CLUB, Rank.NINE))
                .add(Card.of(Color.CLUB, Rank.QUEEN));
        
        hb.setPlayableCards(pc);
        
        hb.setPlayableCards(newPc);
        
        System.out.println("********************************************");
    }
    
    @Test
    public void scoreBeanTest()
    {
        System.out.println("******************ScoreBeanTest:**********************");
        
        ScoreBean sb=new ScoreBean();
        ChangeListener<Number> numberListener =(obsValue,oldValue,newValue) -> System.out.println(newValue);
        
        ChangeListener<TeamId> winningListener =(obsValue,oldValue,newValue) -> System.out.println(newValue);
        
        
        sb.gamePointsProperty(TeamId.TEAM_1).addListener(numberListener);
        sb.gamePointsProperty(TeamId.TEAM_2).addListener(numberListener);
        
        sb.turnPointsProperty(TeamId.TEAM_1).addListener(numberListener);
        sb.turnPointsProperty(TeamId.TEAM_2).addListener(numberListener);
        
        sb.totalPointsProperty(TeamId.TEAM_1).addListener(numberListener);
        sb.totalPointsProperty(TeamId.TEAM_2).addListener(numberListener);
        
        sb.winningTeamProperty().addListener(winningListener);
        
        sb.setTurnPoints(TeamId.TEAM_1, 10);
        sb.setTurnPoints(TeamId.TEAM_2, 15);
        sb.setTurnPoints(TeamId.TEAM_2, 15);
        sb.setTurnPoints(TeamId.TEAM_2, 10);
        
        sb.setGamePoints(TeamId.TEAM_1, 20);
        sb.setGamePoints(TeamId.TEAM_2, 25);
        
        sb.setTotalPoints(TeamId.TEAM_1, 30);
        sb.setTotalPoints(TeamId.TEAM_2, 35);
        
        sb.setWinningTeam(TeamId.TEAM_1);
        sb.setWinningTeam(TeamId.TEAM_2);
        
        
        System.out.println("********************************************");
    }
   
    @Test
    public void trickBeanTest()
    {
      System.out.println("******************TrickBeanTest:**********************"); 
        
      TrickBean tb=new TrickBean();
      
      MapChangeListener<PlayerId, Card> trickListener=e -> System.out.println(e);
      ChangeListener<Card.Color> trumpListener= (obsValue,oldValue,newValue) -> System.out.println(newValue);
      ChangeListener<PlayerId> wpListener= (obsValue,oldValue,newValue) -> System.out.println(newValue);
      
      
      tb.trick().addListener(trickListener);
      tb.trumpProperty().addListener(trumpListener);
      tb.winningPlayerProperty().addListener(wpListener);
      
      Trick trick= Trick.firstEmpty(Card.Color.SPADE, PlayerId.PLAYER_1);
      
      tb.setTrump(Card.Color.SPADE);
      tb.setTrick(trick);
      
      trick=trick.withAddedCard(Card.of(Color.SPADE, Rank.SIX));
      trick=trick.withAddedCard(Card.of(Color.SPADE, Rank.SEVEN));
      
      tb.setTrick(trick);
      
      trick=trick.withAddedCard(Card.of(Color.HEART, Rank.QUEEN));
      trick=trick.withAddedCard(Card.of(Color.DIAMOND, Rank.ACE));
      
      tb.setTrump(Card.Color.SPADE);
      tb.setTrick(trick);
      
      trick=Trick.firstEmpty(Card.Color.HEART, PlayerId.PLAYER_1);
      trick=trick.withAddedCard(Card.of(Color.SPADE, Rank.EIGHT));
      
      tb.setTrump(Card.Color.HEART);
      tb.setTrick(trick);
      
      
      
      System.out.println("********************************************");
    }
}
