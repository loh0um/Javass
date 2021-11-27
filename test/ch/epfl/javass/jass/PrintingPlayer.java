package ch.epfl.javass.jass;

import java.util.Map;

import ch.epfl.javass.jass.Card.Color;

/**
 * @author Antoine Masanet (288366)
 * @author Loïc Houmard (297181)
 *
 */
public final class PrintingPlayer implements Player {
    private final Player underlyingPlayer;

    public PrintingPlayer(Player underlyingPlayer) {
      this.underlyingPlayer = underlyingPlayer;
    }

    @Override
    public Card cardToPlay(TurnState state, CardSet hand) {
      System.out.print("C'est à moi de jouer... Je joue : ");
      Card c = underlyingPlayer.cardToPlay(state, hand);
      System.out.println(c);
      return c;
    }

    @Override
    public  void updateHand(CardSet newHand) {
        System.out.println("Ma nouvelle main est: "+ PackedCardSet.toString(newHand.packed()));
    }
    
    @Override
    public void setTrump(Color trump) {
        System.out.println("Le nouvel atout est: "+ trump.toString());
    }
    
    @Override
    public void updateTrick(Trick newTrick) {
        System.out.println("Le nouveau pli est: "+PackedTrick.toString(newTrick.packed()));
    }
    
    @Override
    public void updateScore(Score score) {
        System.out.println("Le nouveau score est:");
        System.out.println(PackedScore.toString(score.packed()));
    }
    
    @Override
    public void setWinningTeam(TeamId winningTeam) {
        System.out.println("L'équipe gagnate est: "+ winningTeam);
    } 
    
    @Override
    public void setPlayers(PlayerId ownId, Map<PlayerId, String> playerNames){
        System.out.println("Les joueurs sont: ");
        
        for (int i=0; i<PlayerId.COUNT; ++i) {
            PlayerId playerId = PlayerId.ALL.get(i);
            System.out.print(playerNames.get(playerId));
            if (playerId.equals(ownId)) {
                System.out.println(" (moi)");
            }
            else {
                System.out.println();

            }
        }
    }


  }
