package ch.epfl.javass.jass;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Antoine Masanet (288366)
 * @author Loïc Houmard (297181)
 *
 */
public final class RandomJassGame 
{
    public static void main(String[] args) 
    {
      
      Map<PlayerId, Player> players = new HashMap<>();
      Map<PlayerId, String> playerNames = new HashMap<>();

      for (PlayerId pId: PlayerId.ALL) {
        Player player = new RandomPlayer(2019);
        if (pId == PlayerId.PLAYER_1) {
            player = new PrintingPlayer(new MctsPlayer(pId, 2019, 100_000));
        }
        else if (pId == PlayerId.PLAYER_3 ) {
            player = new MctsPlayer(pId, 2019, 100_000);

        }
        /*
        else if (pId==PlayerId.PLAYER_2) {
            player = new PacedPlayer(player, 5);
        }
        
        else {
            player = new PrintingHandPlayer(player);

        }
        */
        players.put(pId, player);
        playerNames.put(pId, pId.name());
      }

      JassGame g = new JassGame(2019, players, playerNames);
      while (! g.isGameOver()) {
        g.advanceToEndOfNextTrick();
        System.out.println("----");
      }
    }
  }
