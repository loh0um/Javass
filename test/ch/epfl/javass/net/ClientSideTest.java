/**
 * 
 */
package ch.epfl.javass.net;

import java.util.HashMap;
import java.util.Map;

import ch.epfl.javass.jass.JassGame;
import ch.epfl.javass.jass.MctsPlayer;
import ch.epfl.javass.jass.Player;
import ch.epfl.javass.jass.PlayerId;
import ch.epfl.javass.jass.PrintingPlayer;
import ch.epfl.javass.jass.RandomPlayer;

/**
 * @author Antoine Masanet (288366)
 * @author Loïc Houmard (297181)
 *
 */
public class ClientSideTest 
{

    public static void main (String[]args)
    {
        String serverIP="127.0.0.1";//IP du serveur (de l'ordi où le RemotePlayerServer run)
                
        try(RemotePlayerClient remotePlayerClient=new RemotePlayerClient(serverIP))
        {            
            
            Map<PlayerId, Player> players = new HashMap<>();
            Map<PlayerId, String> playerNames = new HashMap<>();
    
            for (PlayerId pId: PlayerId.ALL) 
            {
                
              Player player = new RandomPlayer(2019);
              if (pId == PlayerId.PLAYER_1) player =new MctsPlayer(PlayerId.PLAYER_1, 2019, 100_000);
              
              if (pId == PlayerId.PLAYER_3)  player = new PrintingPlayer(remotePlayerClient);
         

              players.put(pId, player);
              playerNames.put(pId, pId.name());
            }
    
            JassGame g = new JassGame(2019, players, playerNames);
            
            while (!g.isGameOver()) 
            {
              g.advanceToEndOfNextTrick();
              System.out.println("----");
            }
            
        }catch (Exception e) {
            e.printStackTrace();
            System.out.println("Exception lancee");
            System.out.println(e);
        }
    }

}
