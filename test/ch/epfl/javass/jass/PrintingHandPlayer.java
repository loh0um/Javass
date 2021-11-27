package ch.epfl.javass.jass;

public class PrintingHandPlayer implements Player{
    private final Player underlyingPlayer;

    public PrintingHandPlayer(Player underlyingPlayer) {
      this.underlyingPlayer = underlyingPlayer;
    }
    
    @Override
    public Card cardToPlay(TurnState state, CardSet hand) {
      return underlyingPlayer.cardToPlay(state, hand);
    }
    
    @Override
    public  void updateHand(CardSet newHand) {
        System.out.println("Nouvelle main: "+ PackedCardSet.toString(newHand.packed()));
    }
    
}
