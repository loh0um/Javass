package ch.epfl.javass.gui;

import java.util.Arrays;
import java.util.EnumMap;
import java.util.Map;
import java.util.concurrent.ArrayBlockingQueue;

import ch.epfl.javass.jass.Card;
import ch.epfl.javass.jass.Card.Color;
import ch.epfl.javass.jass.Card.Rank;
import ch.epfl.javass.jass.CardSet;
import ch.epfl.javass.jass.PlayerId;
import ch.epfl.javass.jass.Score;
import ch.epfl.javass.jass.TeamId;
import ch.epfl.javass.jass.TurnState;
import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.stage.Stage;

public final class GuiTestP1 extends Application{
    public static void main(String[] args) { launch(args); }

    @Override
    public void start(Stage primaryStage) throws Exception {
      Map<PlayerId, String> ns = new EnumMap<>(PlayerId.class);
      PlayerId.ALL.forEach(p -> ns.put(p, p.name()));
      ScoreBean sB = new ScoreBean();
      TrickBean tB = new TrickBean();
      HandBean hB = new HandBean();
      ArrayBlockingQueue<Card> queue = new ArrayBlockingQueue<>(1);
      GraphicalPlayer g =
        new GraphicalPlayer(PlayerId.PLAYER_2, ns, sB, tB, hB, queue);
      g.createStage().show();

      new AnimationTimer() {
        long now0 = 0;
        TurnState s = TurnState.initial(Color.SPADE,
                        Score.INITIAL,
                        PlayerId.PLAYER_3);
        CardSet d = CardSet.ALL_CARDS;

        @Override
        public void handle(long now) {
      if (now - now0 < 1_000_000_000L || s.isTerminal())
        return;
      now0 = now;

      s = s.withNewCardPlayed(d.get(0));
      d = d.remove(d.get(0));
      tB.setTrump(s.trick().trump());
      tB.setTrick(s.trick());
      
      hB.setHand(CardSet.of(Arrays.asList(Card.of(Color.CLUB, Rank.ACE), Card.of(Color.DIAMOND, Rank.ACE),
              Card.of(Color.DIAMOND, Rank.KING), Card.of(Color.DIAMOND, Rank.QUEEN),Card.of(Color.DIAMOND, Rank.SIX),
              Card.of(Color.HEART, Rank.ACE), Card.of(Color.HEART, Rank.EIGHT), Card.of(Color.HEART, Rank.SEVEN),
              Card.of(Color.SPADE, Rank.NINE))));
      hB.setPlayableCards(CardSet.of(Arrays.asList(Card.of(Color.DIAMOND, Rank.ACE))));
       

      if (s.trick().isFull()) {
        s = s.withTrickCollected();
        for (TeamId t: TeamId.ALL)
          sB.setTurnPoints(t, s.score().turnPoints(t));
      }
        }
      }.start();
    }
}
