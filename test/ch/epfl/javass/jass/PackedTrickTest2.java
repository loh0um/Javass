package ch.epfl.javass.jass;

import static ch.epfl.test.TestRandomizer.RANDOM_ITERATIONS;
import static ch.epfl.test.TestRandomizer.newRandom;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Random;
import java.util.SplittableRandom;

import org.junit.jupiter.api.Test;

import ch.epfl.javass.jass.PackedTrick;
import ch.epfl.javass.jass.Card.Color;
import ch.epfl.javass.jass.Card.Rank;

/**
 * @author Antoine Masanet (288366)
 * @author Loïc Houmard (297181)
 *
 */

//Remarques:manque de verification de condition partout

public class PackedTrickTestx 
{
    

    @Test
    
    //void 
    
    private int validFullTrickGenerator()
    {

        SplittableRandom rng = new SplittableRandom((new Random()).nextInt());
        
        int a = VPCG();
        
        int b;
        
        do
        {
            b=VPCG();
        }while(b==a);
        
       int c;
       
        do
        {
            c=VPCG();
        }while(c==a || c==b);
        
        int d;
        
        do
        {
            d=VPCG();
        }while(d==a || d==b || d==c);
        
        int e=rng.nextInt(9);
        
        int f=rng.nextInt(4);
        
        int g=rng.nextInt(4);
       
        
        int validTrick=g<<30|f<<28|e<<24|d<<18|c<<12|b<<6|a;
        
        return validTrick;
    }
    
    
    private int validArbitraryTrickGenerator()
    {

        SplittableRandom rng = new SplittableRandom((new Random()).nextInt());
                
        int a=rng.nextInt(4);
        
        int b=rng.nextInt(4);
        
        int c=rng.nextInt(9);
        
        int d= rng.nextInt(13)==0?PackedCard.INVALID:VPCG();
        
       
        int e;
        if (d!=PackedCard.INVALID)
        {
            do
            {
                e=VPCG();
                
            }while(e==d);
        }
        
        else 
            
            do
            {
                e=rng.nextInt(13)==0?PackedCard.INVALID:VPCG();
            }while((e!=PackedCard.INVALID && e==d));
        
        int f;
        
        if (e!=PackedCard.INVALID)
        do
        {
            f=VPCG();
            
        }while(f==d ||f==e);
        
        else
            
            do
            {
                f=rng.nextInt(13)==0?PackedCard.INVALID:VPCG();
            }while((f!=PackedCard.INVALID && f==d) || (f!=PackedCard.INVALID && f==e));
        
        int g;
        
        if (f!=PackedCard.INVALID)
        do
        {
            g=VPCG();
            
        }while(g==d ||g==e || g==f);
        
        else
            
            do
            {
                g=rng.nextInt(13)==0?PackedCard.INVALID:VPCG();
            }while((g!=PackedCard.INVALID && g==d) || (g!=PackedCard.INVALID && g==e) || (g!=PackedCard.INVALID && g==f) );
        

        int validTrick=a<<30|b<<28|c<<24|d<<18|e<<12|f<<6|g;
        
        return validTrick;
    }
    
    
    @SuppressWarnings("unused")
    private int invalidArbitraryTrickGenerator()
    {

        SplittableRandom rng = new SplittableRandom((new Random()).nextInt());
        
        int probaInvalid=13;
        
        int a=rng.nextInt(4);
        
        int b=rng.nextInt(4);
        
        int c=rng.nextInt(9);
        
        int d= rng.nextInt(13)==0?PackedCard.INVALID:VPCG();
        
       
        int e;
        if (d!=PackedCard.INVALID)
        {
            do
            {
                e=VPCG();
                
            }while(e==d);
        }
        
        else 
            
            do
            {
                e=rng.nextInt(13)==0?PackedCard.INVALID:VPCG();
            }while((e!=PackedCard.INVALID && e==d));
        
        int f;
        
        if (e!=PackedCard.INVALID)
        do
        {
            f=VPCG();
            
        }while(f==d ||f==e);
        
        else
            
            do
            {
                f=rng.nextInt(13)==0?PackedCard.INVALID:VPCG();
            }while((f!=PackedCard.INVALID && f==d) || (f!=PackedCard.INVALID && f==e));
        
        int g;
        
        if (f!=PackedCard.INVALID)
        do
        {
            g=VPCG();
            
        }while(g==d ||g==e || g==f);
        
        else
            
            do
            {
                g=rng.nextInt(13)==0?PackedCard.INVALID:VPCG();
            }while((g!=PackedCard.INVALID && g==d) || (g!=PackedCard.INVALID && g==e) || (g!=PackedCard.INVALID && g==f) );
        

        int validTrick=a<<30|b<<28|c<<24|d<<18|e<<12|f<<6|g;
        
        return validTrick;
    }
    
    private int VPCG()//ValidPackedCardGenerator
    {
        SplittableRandom rng = new SplittableRandom((new Random()).nextInt());

       int color = rng.nextInt(4);
       
       int rank = rng.nextInt(9);
       
       return PackedCard.pack(Color.ALL.get(color), Rank.ALL.get(rank));
    }
    
    //Generates invalid card excepted INVALID card
    @SuppressWarnings("unused")
    private int invalidPackedTrickGenerator()
    {
        SplittableRandom rng = newRandom();
        
        int invalidCard;
        
        do {
            int c = rng.nextInt(4);
            int r = rng.nextInt(16);
            int rest = (r <= 8) ? rng.nextInt(1, 1 << 26) : 0;
            invalidCard = (((rest << 2) | c) << 4) | r;
        } while (invalidCard==PackedCard.INVALID);

       return invalidCard;
    }
    
    
    private int trickCreator(int trump, int player1, int trickIndex, int card3, int card2,int card1, int card0)
    {
        return (trump&0b11)<<30|(player1&0b11)<<28|(trickIndex&0xF)<<24|(card3&0x3F)<<18|(card2&0x3F)<<12|(card1&0x3F)<<6|card0&0x3F;
    }
    
    
    @SuppressWarnings("unused")
    private int trickCreator(int trump, int player1, int trickIndex, Card card3, Card card2,Card card1, Card card0)
    {
        return trickCreator(trump,player1,trickIndex,card3.packed(),card2.packed(),card1.packed(),card0.packed()); 
    }
    
    private int trickCreator(int trump, int player1, int trickIndex, int card3, Card card2,Card card1, Card card0)
    {
        return trickCreator(trump,player1,trickIndex,card3,card2.packed(),card1.packed(),card0.packed()); 
    }
    
 @SuppressWarnings("unused")
private long validSetGenerator() 
 {
        
        SplittableRandom rng = new SplittableRandom((new Random()).nextInt());
        
        long a = rng.nextLong(1L<<9);//Random between 0 and 2^9-1
        long b = rng.nextLong(1L<<9)<<16;//Random between 0 and 2^9-1
        long c = rng.nextLong(1L<<9)<<32;//Random between 0 and 2^9-1
        long d = rng.nextLong(1L<<9)<<48;//Random between 0 and 2^9-1
        
        long validSet=a|b|c|d;
        
        return validSet;
    }
 
 private long validSetGeneratorMax9Cards() {
     
     SplittableRandom rng = new SplittableRandom((new Random()).nextInt());
     
     long validSet=0;
     
     do {
         for (int i=0; i<9;++i) {
             int decalage=rng.nextInt(64);
             if (((1L<<decalage)&PackedCardSet.ALL_CARDS)!=0) {
                 validSet=validSet|(1L<<decalage);
             }
         }
     } while (validSet==0);

     
     return validSet;
 }
    

    
    @Test
    void isValidWorksForSomeValidTrick() 
    {
        
        for (int i = 0; i < RANDOM_ITERATIONS; ++i) 
        { 
            assertTrue(PackedTrick.isValid(validArbitraryTrickGenerator()));
        }
    }
    
    @Test
    void isValidWorksForSomeInvalidTrick() 
    {

      int invalidTrick1=0b11_11_0000_111110_111000_111100_110101;
      
      int invalidTrick2=0b11_11_0000_111111_111000_111111_110101;
      
      int invalidTrick3=0b11_11_0100_111100_111000_111111_111111;
      
      int invalidTrick4=0b11_11_0100_111111_111000_111111_111111;
      
      int invalidTrick5=0b11_11_0100_111111_111000_110011_111111;
      

      assertFalse(PackedTrick.isValid(invalidTrick1));
      assertFalse(PackedTrick.isValid(invalidTrick2));
      assertFalse(PackedTrick.isValid(invalidTrick3));
      assertFalse(PackedTrick.isValid(invalidTrick4));
      assertFalse(PackedTrick.isValid(invalidTrick5));
        
    }
    
    @Test
    void firstEmptyWorks()
    {
        SplittableRandom rng = newRandom();
        
        for (int i = 0; i < RANDOM_ITERATIONS; ++i) 
        { 
            int t=rng.nextInt(4);
            
            Color trump = Color.ALL.get(t);
            
            int p=rng.nextInt(4);
            
            PlayerId player = PlayerId.ALL.get(p);
            
            assertEquals(trickCreator(t, p, 
                    0, PackedCard.INVALID, PackedCard.INVALID, PackedCard.INVALID, PackedCard.INVALID),
                    PackedTrick.firstEmpty(trump, player));         }
        
    }
    
    //Erreur car ne rempli par avec des invalid cards
    @Test 
    void nextEmptyWorksforValidInput()
    {
        SplittableRandom rng = newRandom();
        
        for (int i = 0; i < RANDOM_ITERATIONS; ++i) 
        { 
            int t=rng.nextInt(4);
            
            int p=rng.nextInt(4);
            
            int index=rng.nextInt(8);
            
            int card0=VPCG();
            
            int card1=VPCG();
            
            int card2=VPCG();
            
            int card3=VPCG();
            
            int originalTrick=trickCreator(t, p, index, card3, card2, card1, card0);
            
            
            
//            System.out.println(Integer.toBinaryString(t<<30 | p<<28 |(index+1)<<24));
//            System.out.println(Integer.toBinaryString(PackedTrick.nextEmpty(t<<30 | p<<28 | index<<24)));
            
            assertEquals(trickCreator(t, PackedTrick.winningPlayer(originalTrick).ordinal(), 
                    index+1, PackedCard.INVALID, PackedCard.INVALID, PackedCard.INVALID, PackedCard.INVALID),
                    PackedTrick.nextEmpty(originalTrick)); 
            
            
        }
    }
    
    @Test
    void isLastWorks()
    {
        
        SplittableRandom rng = newRandom();
        
        for (int i = 0; i < RANDOM_ITERATIONS; ++i) 
        { 
            
            int t=rng.nextInt(4);
            
            int p=rng.nextInt(4);
        
        assertTrue(PackedTrick.isLast(trickCreator(t, p, 8, VPCG(), 
                VPCG(), VPCG(), VPCG())));
        
        assertFalse(PackedTrick.isLast(trickCreator(t, p, rng.nextInt(8), VPCG(), 
                VPCG(), VPCG(), VPCG())));
        }
        
    }
    
    @Test
    void isEmptyWorks()
    {
        SplittableRandom rng = newRandom();
        
        for (int i = 0; i < RANDOM_ITERATIONS; ++i) 
        { 
            int t=rng.nextInt(4);
            
            int p=rng.nextInt(4);
            
            int index=rng.nextInt(8);
            
            assertTrue(PackedTrick.isEmpty(trickCreator(t, p, index,PackedCard.INVALID,PackedCard.INVALID,PackedCard.INVALID,PackedCard.INVALID)));
            
            assertFalse(PackedTrick.isEmpty(trickCreator(t, p, index,PackedCard.INVALID,PackedCard.INVALID,PackedCard.INVALID,VPCG())));
            
        }
        
    }
    
    @Test
    void isFullWorks()
    {
        
        SplittableRandom rng = newRandom();
        
        for (int i = 0; i < RANDOM_ITERATIONS; ++i) 
        { 
            int t=rng.nextInt(4);
            
            int p=rng.nextInt(4);
            
            int index=rng.nextInt(8);
            
            assertTrue(PackedTrick.isFull(trickCreator(t, p, index,VPCG(),VPCG(),VPCG(),VPCG())));
            
            assertFalse(PackedTrick.isFull(trickCreator(t, p, index,PackedCard.INVALID,VPCG(),VPCG(),VPCG())));
            
            assertFalse(PackedTrick.isFull(trickCreator(t, p, index,PackedCard.INVALID,PackedCard.INVALID,VPCG(),VPCG())));
            
            assertFalse(PackedTrick.isFull(trickCreator(t, p, index,PackedCard.INVALID,PackedCard.INVALID,PackedCard.INVALID,VPCG())));
            
            assertFalse(PackedTrick.isFull(trickCreator(t, p, index,PackedCard.INVALID,PackedCard.INVALID,PackedCard.INVALID,PackedCard.INVALID)));
           
        }
        
    }
    
    @Test
    void sizeWorks()
    {
        
        SplittableRandom rng = newRandom();
        
        for (int i = 0; i < RANDOM_ITERATIONS; ++i) 
        { 
            int t=rng.nextInt(4);
            
            int p=rng.nextInt(4);
            
            int index=rng.nextInt(8);
            
            assertEquals(4,PackedTrick.size(trickCreator(t, p, index,VPCG(),VPCG(),VPCG(),VPCG())));

            assertEquals(3,PackedTrick.size(trickCreator(t, p, index,PackedCard.INVALID,VPCG(),VPCG(),VPCG())));
            
            assertEquals(2,PackedTrick.size(trickCreator(t, p, index,PackedCard.INVALID,PackedCard.INVALID,VPCG(),VPCG())));
            
            assertEquals(1,PackedTrick.size(trickCreator(t, p, index,PackedCard.INVALID,PackedCard.INVALID,PackedCard.INVALID,VPCG())));
            
            assertEquals(0,PackedTrick.size(trickCreator(t, p, index,PackedCard.INVALID,PackedCard.INVALID,
                    PackedCard.INVALID,PackedCard.INVALID)));
        }
    }
    
    @Test
    void trumpWorks()
    {
        
        SplittableRandom rng = newRandom();
        
        for (int i = 0; i < RANDOM_ITERATIONS; ++i) 
        { 
            int t=rng.nextInt(4);
            
            int p=rng.nextInt(4);
            
            int index=rng.nextInt(8);
            
            assertEquals(Color.ALL.get(t),PackedTrick.trump(trickCreator(t, p, index,VPCG(),VPCG(),VPCG(),VPCG())));
            
            assertEquals(Color.ALL.get(t),PackedTrick.trump(trickCreator(t, p, index,PackedCard.INVALID,VPCG(),VPCG(),VPCG())));
            
            assertEquals(Color.ALL.get(t),PackedTrick.trump(trickCreator(t, p, index,PackedCard.INVALID,PackedCard.INVALID,VPCG(),VPCG())));
            
            assertEquals(Color.ALL.get(t),PackedTrick.trump(trickCreator(t, p, index,PackedCard.INVALID,PackedCard.INVALID,PackedCard.INVALID,VPCG())));
            
            assertEquals(Color.ALL.get(t),PackedTrick.trump(trickCreator(t, p, index,PackedCard.INVALID,PackedCard.INVALID, PackedCard.INVALID,PackedCard.INVALID)));
        }
        
    }
    
    @Test
    void playerWorks()
    {
        
        SplittableRandom rng = newRandom();
        
        for (int i = 0; i < RANDOM_ITERATIONS; ++i) 
        { 
            int t=rng.nextInt(4);
            
            int p=rng.nextInt(4);
            
            int index=rng.nextInt(8);
            
            assertEquals(PlayerId.ALL.get(p),PackedTrick.player(trickCreator(t, p, index,VPCG(),VPCG(),VPCG(),VPCG()),0));
            
            assertEquals(PlayerId.ALL.get(p),PackedTrick.player(trickCreator(t, p, index,PackedCard.INVALID,VPCG(),VPCG(),VPCG()),0));
            
            assertEquals(PlayerId.ALL.get(p),PackedTrick.player(trickCreator(t, p, index,PackedCard.INVALID,PackedCard.INVALID,VPCG(),VPCG()),0));
            
            assertEquals(PlayerId.ALL.get(p),PackedTrick.player(trickCreator(t, p, index,PackedCard.INVALID,PackedCard.INVALID,PackedCard.INVALID,VPCG()),0));
            
            assertEquals(PlayerId.ALL.get(p),PackedTrick.player(trickCreator(t, p, index,PackedCard.INVALID,PackedCard.INVALID, PackedCard.INVALID,PackedCard.INVALID),0));
        }
        
    }
    
    
    //Doit on verifier que la carte est valide?
    @Test
    void cardWorks()
    {
        
        SplittableRandom rng = newRandom();
        
        for (int i = 0; i < RANDOM_ITERATIONS; ++i) 
        { 
            int t=rng.nextInt(4);
            
            int p=rng.nextInt(4);
            
            int index=rng.nextInt(8);
            
            int cardIndex= rng.nextInt(4);
            
            int card[]= {VPCG(),VPCG(),VPCG(),VPCG()};
            
            int originalTrick=trickCreator(t, p, index, card[3], card[2], card[1], card[0]);
            
            assertEquals(card[cardIndex],PackedTrick.card(originalTrick,cardIndex));
            
            
            
        }
        
    }
    
    @Test
    void withAddedCardWorks()
    {
        
        SplittableRandom rng = newRandom();
        
        for (int i = 0; i < RANDOM_ITERATIONS; ++i) 
        { 
            int t=rng.nextInt(4);
            
            int p=rng.nextInt(4);
            
            int index=rng.nextInt(8);
                        
            int card[]= {VPCG(),VPCG(),VPCG(),VPCG()};
            
            int addedCard=VPCG();
            
           
            assertEquals(trickCreator(t, p, index,PackedCard.INVALID,PackedCard.INVALID,PackedCard.INVALID,addedCard),
                    PackedTrick.withAddedCard(trickCreator(t, p, index,PackedCard.INVALID,PackedCard.INVALID,
                            PackedCard.INVALID,PackedCard.INVALID),addedCard));
            
            assertEquals(trickCreator(t, p, index,PackedCard.INVALID,PackedCard.INVALID,addedCard,card[0]),
                    PackedTrick.withAddedCard(trickCreator(t, p, index,PackedCard.INVALID,PackedCard.INVALID,PackedCard.INVALID,card[0]),addedCard));
            
            assertEquals(trickCreator(t, p, index,PackedCard.INVALID,addedCard,card[1],card[0]),
                    PackedTrick.withAddedCard(trickCreator(t, p, index,PackedCard.INVALID,PackedCard.INVALID,
                            card[1],card[0]),addedCard));
            
            assertEquals(trickCreator(t, p, index,addedCard,card[2],card[1],card[0]),
                    PackedTrick.withAddedCard(trickCreator(t, p, index,PackedCard.INVALID,card[2],
                            card[1],card[0]),addedCard));
            
            } 
        
    }
    
    //Verifie pas si le plis n'est pas vide
    @Test
    void baseColorWorks()
    {
        SplittableRandom rng = newRandom();
        
        for (int i = 0; i < RANDOM_ITERATIONS; ++i) 
        { 
            int t=rng.nextInt(4);
            
            int p=rng.nextInt(4);
            
            int index=rng.nextInt(8);
                        
            int card[]= {VPCG(),VPCG(),VPCG(),VPCG()};
            
            int originalTrick=trickCreator(t, p, index, card[3], card[2], card[1], card[0]);
             
            assertEquals(PackedCard.color(card[0]), PackedTrick.baseColor(originalTrick));
        }
        
        
    }
    
    @Test
    void playableCardsWorks()
    {
        System.out.println();
        System.out.println("*********************");
        System.out.println("playableCardsWorks");
        System.out.println();
        System.out.println();
        SplittableRandom rng = newRandom();
        
        for (int i = 0; i < 100; ++i) 
        { 
            int t=rng.nextInt(4);
            
            int p=rng.nextInt(4);
            
            int index=rng.nextInt(8);
                        
            int card[]= {VPCG(),VPCG(),VPCG(),PackedCard.INVALID};
            
            int originalTrick=trickCreator(t, p, index, card[3], card[2], card[1], card[0]);
            
            //long pkHand=validSetGenerator();
            long pkHand=validSetGeneratorMax9Cards();
            
            System.out.println(PackedTrick.toString(originalTrick));
            
            System.out.println(PackedCardSet.toString(pkHand));
            
            System.out.println(PackedCardSet.toString(PackedTrick.playableCards(originalTrick, pkHand)));
            //System.out.println(PackedCardSet.toString(PackedTrick.reflexionPlayableCard(originalTrick, pkHand)));
            
            assertFalse(0==PackedTrick.playableCards(originalTrick, pkHand));
            //assertEquals(pkHand, PackedTrick.playableCards(originalTrick, pkHand));
                        
            System.out.println();
        }        
    }
    
    @Test
    void playableCardsWorksWithSpecialChoosenSet() {
        System.out.println();
        System.out.println("*********************");
        System.out.println("playableCardsWorksWithSpecialChoosenSet");
        System.out.println();
        System.out.println();
        
        long pkHand;
        int pkTrick;
        
        //Obligation de sous-couper
        pkHand=0b00000000_00000000_00000000_00000000_00000000_00000000_00000000_00000011L; //6 et 7 de pique
        pkTrick=trickCreator(0,0,1,PackedCard.INVALID, Card.of(Color.HEART,Rank.JACK),Card.of(Color.HEART,Rank.KING),Card.of(Color.SPADE,Rank.NINE));

        System.out.println(PackedCard.toString(0b010101)+", "+PackedCard.toString(0b010111)+", "+PackedCard.toString(0b000011));
        
        System.out.println(PackedCardSet.toString(pkHand));
        System.out.println(PackedTrick.toString(pkTrick));
        assertEquals(0b00000000_00000000_00000000_00000000_00000000_00000000_00000000_00000011L, PackedTrick.playableCards(pkTrick, pkHand));
    }
    
    @Test
    void pointsWorks()
    {
        System.out.println();
        System.out.println("*********************");
        System.out.println("pointsWorks");
        System.out.println();
        System.out.println();


        SplittableRandom rng = newRandom();
        
        for (int i = 0; i < 100; ++i) 
        { 
            int t=rng.nextInt(4);
            
            int p=rng.nextInt(4);
            
            int index=rng.nextInt(9);
                        
            int card[]= {VPCG(),VPCG(),VPCG(),VPCG()};
            
            int originalTrick=trickCreator(t, p, index, card[3], card[2], card[1], card[0]);
            System.out.println(Integer.toBinaryString(originalTrick));
            System.out.println(PackedTrick.toString(originalTrick));
            System.out.println(PackedTrick.points(originalTrick));
        }
        
    }
    
    @Test
    void winningPlayerWorks()
    {
        
        System.out.println();
        System.out.println("*********************");
        System.out.println("winningPlayerWorks");
        System.out.println();
        System.out.println();
        
        SplittableRandom rng = newRandom();
        
        for (int i = 0; i < 10; ++i) 
        { 
            int t=rng.nextInt(4);
            
            int p=rng.nextInt(4);
            
            int index=rng.nextInt(8);
                        
            int card[]= {VPCG(),VPCG(),VPCG(),VPCG()};
            
            int originalTrick=trickCreator(t, p, index, card[3], card[2], card[1], card[0]);
            
            System.out.println(PackedTrick.toString(originalTrick));
            
            System.out.println(PackedTrick.winningPlayer(originalTrick));
        }
        
    }
    
    @Test
    void toStringWorks()
    {
        
    }
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
}
