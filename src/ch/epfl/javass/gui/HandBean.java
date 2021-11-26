package ch.epfl.javass.gui;

import java.util.Arrays;

import ch.epfl.javass.jass.Card;
import ch.epfl.javass.jass.CardSet;
import ch.epfl.javass.jass.Jass;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.ObservableSet;

/**
 * Classe contentant les propriétés d'une main dans
 * le but de pouvoir les observer. 
 * 
 * @author Antoine Masanet (288366)
 * @author Loïc Houmard (297181)
 * 
 */
public final class HandBean {

    /**
     * Ensemble des propriétés représentant une main.
     */
    private final ObservableList<Card> hand = FXCollections
            .observableArrayList(Arrays.asList(new Card[Jass.HAND_SIZE]));
    private final ObservableSet<Card> playableCards = FXCollections
            .observableSet();

    /**
     * Retourne la propriété représentant la main.
     * 
     * @return La propriété hand représentant les cartes en main.
     */
    public ObservableList<Card> hand() {
        return FXCollections.unmodifiableObservableList(hand);
    }

    /**
     * Modifie les cartes de la main.
     * 
     * @param newHand: Le nouvel ensemble de cartes en main.
     */
    public void setHand(CardSet newHand) {

        assert newHand.size() <= Jass.HAND_SIZE;

        // La main est entièrement nouvelle (9 cartes)
        if (newHand.size() == Jass.HAND_SIZE) {
            for (int i = 0; i < Jass.HAND_SIZE; ++i) {
                hand.set(i, newHand.get(i));
            }
        }
        // La main est juste modifiée (plus petite)
        else {
            for (int i = 0; i < Jass.HAND_SIZE; ++i) {

                Card card = hand.get(i);

                if (card != null && !newHand.contains(card)) {
                    hand.set(i, null);
                }
            }
        }
    }

    /**
     * Retourne la propriété représentant les cartes jouables.
     * 
     * @return La propriété playableCards représentant les cartes jouables.
     */
    public ObservableSet<Card> playableCards() {
        return FXCollections.unmodifiableObservableSet(playableCards);
    }

    /**
     * Modifie les cartes jouables.
     * 
     * @param newPlayableCards: Les nouvelles cartes jouables.
     */
    public void setPlayableCards(CardSet newPlayableCards) {

        playableCards.clear();

        for (int i = 0; i < newPlayableCards.size(); ++i) {
            playableCards.add(newPlayableCards.get(i));
        }
    }

}
