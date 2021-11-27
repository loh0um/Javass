package ch.epfl.javass.jass;

import ch.epfl.javass.jass.Card.Color;
import ch.epfl.javass.jass.Card.Rank;

/**
 * @author Antoine Masanet (288366)
 * @author Loïc Houmard (297181)
 *
 */
public class MctsTestx 
{
    
    public static void main(String[] args)
    {
       Player player = new MctsPlayer(PlayerId.PLAYER_2, 0, 100_000) ;
       
       TurnState state = TurnState.initial(Color.SPADE, Score.INITIAL, PlayerId.PLAYER_1);
       
       state=state.withNewCardPlayedAndTrickCollected(Card.of(Color.SPADE, Rank.JACK));
       
       CardSet hand = CardSet.EMPTY
               .add(Card.of(Color.SPADE, Rank.EIGHT))
               .add(Card.of(Color.SPADE, Rank.NINE))
               .add(Card.of(Color.SPADE, Rank.TEN))
               .add(Card.of(Color.HEART, Rank.SIX))
               .add(Card.of(Color.HEART, Rank.SEVEN))
               .add(Card.of(Color.HEART, Rank.EIGHT))
               .add(Card.of(Color.HEART, Rank.NINE))
               .add(Card.of(Color.HEART, Rank.TEN))
               .add(Card.of(Color.HEART, Rank.JACK));
       long timeBefore = System.currentTimeMillis();
       player.cardToPlay(state, hand);
       long timeAfter = System.currentTimeMillis();

       System.out.println("Duration: "+(timeAfter-timeBefore)/1000.0+" seconds");
       System.out.println("Finish");
    }

}
