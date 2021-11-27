package ch.epfl.javass.net;

import ch.epfl.javass.jass.MctsPlayer;
import ch.epfl.javass.jass.PlayerId;

/**
 * @author Antoine Masanet (288366)
 * @author Loïc Houmard (297181)
 *
 */
public class ServerSideTest 
{
    public static void main(String[] args)
    {
        RemotePlayerServer remotePlayerServer=new RemotePlayerServer(new MctsPlayer(PlayerId.PLAYER_3, 2019, 100_000));
        
        remotePlayerServer.run();
        
    }

}
