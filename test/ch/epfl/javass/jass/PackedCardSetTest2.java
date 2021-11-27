package ch.epfl.javass.jass;

import static ch.epfl.test.TestRandomizer.RANDOM_ITERATIONS;
import static ch.epfl.test.TestRandomizer.newRandom;
import static org.junit.jupiter.api.Assertions.*;

import java.util.Random;
import java.util.SplittableRandom;

import org.junit.jupiter.api.Test;

class PackedCardSetTestx {

    @Test
    void isValidWorksForSomeValidSet() {
        
        for (int i = 0; i < RANDOM_ITERATIONS; ++i) {
            
            assertTrue(PackedCardSet.isValid(validSetGenerator()));
        }
    }
    
    @Test
    void isValidWorksForSomeInvalidSet() {
        
        for (int i = 0; i < RANDOM_ITERATIONS; ++i) {
            assertFalse(PackedCardSet.isValid(invalidSetGenerator()));
        }
    }
    
    @Test
    void trumpAboveWorksWithValidCard() {
        SplittableRandom rng = newRandom();
        System.out.println("trumpAboveWorksWithValidCard:\n");

        
        for (int i=0; i<20; ++i) {
            int validCard=rng.nextInt(9)|(rng.nextInt(4)<<4);
            System.out.println(Card.ofPacked(validCard).toString());
            
            System.out.println(PackedCardSet.toString(PackedCardSet.trumpAbove(validCard)));
            
            assertTrue(true);
            }  
        System.out.println("\n\n\n\n\n\n\n\n");
    }
    
    @Test
    void singletonWorks() {
        SplittableRandom rng = newRandom();
        System.out.println("singletonWorks:\n");

        
        for (int i=0; i<20; ++i) {
            int validCard=rng.nextInt(9)|(rng.nextInt(4)<<4);
            System.out.println(Card.ofPacked(validCard).toString());
            
            System.out.println(PackedCardSet.toString(PackedCardSet.singleton(validCard)));
            
            assertEquals("{"+Card.ofPacked(validCard).toString()+"}",PackedCardSet.toString(PackedCardSet.singleton(validCard)) );
            }
        System.out.println("\n\n\n\n\n\n\n\n");
    }
    
    @Test
    void isEmptyWorksForEmptySet() {
       assertTrue(PackedCardSet.isEmpty(0));
    }
    
    @Test
    void isEmptyWorksForNonEmptySet() {
        for (int i=0; i<RANDOM_ITERATIONS; ++i) {
            
            long validNonEmptySet=validSetGenerator();
            
            if(validNonEmptySet!=0) {
                assertFalse(PackedCardSet.isEmpty(validNonEmptySet));
            }
        }
    }
    
    @Test
    void getWorks() {
        SplittableRandom rng = newRandom();
        System.out.println("getWorks:\n");

        for (int i=0; i<10; ++i) {

            
            long validCardSet=validSetGenerator();
            int size = PackedCardSet.size(validCardSet);
            int randomIndex=rng.nextInt(size);
            
            System.out.println(PackedCardSet.toString(validCardSet));
            System.out.println("Index: "+randomIndex);
            System.out.println(PackedCard.toString(PackedCardSet.get(validCardSet, randomIndex)));
        }
        System.out.println("\n\n\n\n\n\n\n\n");
    }
    
    @Test
    void addWorks() {
        SplittableRandom rng = newRandom();
        System.out.println("addWorks:\n");

        for (int i=0; i<10; ++i) {

            
            long validCardSet=validSetGenerator();
            int validCard=rng.nextInt(9)|(rng.nextInt(4)<<4);
            
            System.out.println(PackedCardSet.toString(validCardSet));
            System.out.println(PackedCard.toString(validCard));
            System.out.println(PackedCardSet.toString(PackedCardSet.add(validCardSet, validCard)));
            System.out.println();
        }
        System.out.println("\n\n\n\n\n\n\n\n");
    }
    
    @Test
    void removeWorks() {
        SplittableRandom rng = newRandom();
        System.out.println("removeWorks:\n");

        for (int i=0; i<10; ++i) {

            
            long validCardSet=validSetGenerator();
            int validCard=rng.nextInt(9)|(rng.nextInt(4)<<4);
            
            System.out.println(PackedCardSet.toString(validCardSet));
            System.out.println(PackedCard.toString(validCard));
            System.out.println(PackedCardSet.toString(PackedCardSet.remove(validCardSet, validCard)));
            System.out.println();
        }
        System.out.println("\n\n\n\n\n\n\n\n");
    }
    
    @Test
    void containsWorks() {
        SplittableRandom rng = newRandom();
        System.out.println("containsWorks:\n");

        for (int i=0; i<10; ++i) {

            long validCardSet=validSetGenerator();
            int validCard=rng.nextInt(9)|(rng.nextInt(4)<<4);
            String stringCardSet=PackedCardSet.toString(validCardSet);
            String stringValidCard=PackedCard.toString(validCard);
            boolean verification=false;
            
            System.out.println(stringCardSet);
            System.out.println(stringValidCard);
            System.out.println(PackedCardSet.contains(validCardSet, validCard));
            
            for (int j=0; j<stringCardSet.length()-1; ++j) {
                if (stringCardSet.substring(j, j+2).equals(stringValidCard)) {
                    verification=true;
                }
            }
            assertEquals(verification, PackedCardSet.contains(validCardSet, validCard));

            
            
            
        }
        System.out.println("\n\n\n\n\n\n\n\n");
    }
    
    @Test
    void complementWorks() {
        System.out.println("complementWorks:\n");

        for (int i=0; i<10; ++i) {

            long validCardSet=validSetGenerator();
            
            System.out.println(PackedCardSet.toString(validCardSet));
            System.out.println(PackedCardSet.toString(PackedCardSet.complement(validCardSet)));
        }
        System.out.println("\n\n\n\n\n\n\n\n");
    }
    
    @Test
    void unionWorks() {
        System.out.println("unionWorks:\n");

        for (int i=0; i<10; ++i) {

            long validCardSet1=validSetGenerator();
            long validCardSet2=validSetGenerator();

            
            
            System.out.println(PackedCardSet.toString(validCardSet1));
            System.out.println(PackedCardSet.toString(validCardSet2));
            System.out.println(PackedCardSet.toString(PackedCardSet.union(validCardSet1, validCardSet2)));


        }
        System.out.println("\n\n\n\n\n\n\n\n");
    }
    
    @Test
    void intersectionWorks() {
        System.out.println("intersectionWorks:\n");

        for (int i=0; i<10; ++i) {

            long validCardSet1=validSetGenerator();
            long validCardSet2=validSetGenerator();

            
            
            System.out.println(PackedCardSet.toString(validCardSet1));
            System.out.println(PackedCardSet.toString(validCardSet2));
            System.out.println(PackedCardSet.toString(PackedCardSet.intersection(validCardSet1, validCardSet2)));


        }
        System.out.println("\n\n\n\n\n\n\n\n");
    }
    
    @Test
    void differenceWorks() {
        System.out.println("differenceWorks:\n");

        for (int i=0; i<10; ++i) {

            long validCardSet1=validSetGenerator();
            long validCardSet2=validSetGenerator();

            
            
            System.out.println(PackedCardSet.toString(validCardSet1));
            System.out.println(PackedCardSet.toString(validCardSet2));
            System.out.println(PackedCardSet.toString(PackedCardSet.difference(validCardSet1, validCardSet2)));


        }
        System.out.println("\n\n\n\n\n\n\n\n");
    }
    
    @Test
    void subsetOfColorWorks() {
        System.out.println("subsetOfColorWorks:\n");
        SplittableRandom rng = newRandom();


        for (int i=0; i<10; ++i) {
            int randomNumber = rng.nextInt(4);//Random between 0 and 3
            long validCardSet1=validSetGenerator();
            Card.Color color = Card.Color.ALL.get(randomNumber);
            
            
            System.out.println(PackedCardSet.toString(validCardSet1));
            System.out.println(color);
            System.out.println(PackedCardSet.toString(PackedCardSet.subsetOfColor(validCardSet1, color)));


        }
        System.out.println("\n\n\n\n\n\n\n\n");
    }
    
    @Test
    void sizeWorks() {
        System.out.println("sizeWorks:\n");

        for (int i=0; i<10; ++i) {

            long validCardSet=validSetGenerator();
            int size=PackedCardSet.size(validCardSet);
            String stringCardSet=Long.toBinaryString(validCardSet);
            int verification=0;
            
            System.out.println(stringCardSet);
            System.out.println(size);
            
            for (int j=0; j<stringCardSet.length(); ++j) {
                if (stringCardSet.substring(j,j+1).equals("1")) {
                    ++verification;
                }
            }
            assertEquals(verification, size);
        }
            
            
    }
    
    /**
     * Create a valid Set
     */
    private long validSetGenerator() {
        
        SplittableRandom rng = new SplittableRandom((new Random()).nextInt());
        
        long a = rng.nextLong(1L<<9);//Random between 0 and 2^9-1
        long b = rng.nextLong(1L<<9)<<16;//Random between 0 and 2^9-1
        long c = rng.nextLong(1L<<9)<<32;//Random between 0 and 2^9-1
        long d = rng.nextLong(1L<<9)<<48;//Random between 0 and 2^9-1
        
        long validSet=a|b|c|d;
        
        return validSet;
    }
    
    /**
     * Create an invalid Set
     */
    private long invalidSetGenerator() {
        
        SplittableRandom rng = new SplittableRandom((new Random()).nextInt());

        
        long a = rng.nextLong((1L<<16)-(1L<<9))+(1L<<9);
        long b = rng.nextLong((1L<<16)-(1L<<9))+(1L<<9)<<16;
        long c = rng.nextLong((1L<<16)-(1L<<9))+(1L<<9)<<32;
        long d = rng.nextLong((1L<<16)-(1L<<9))+(1L<<9)<<48;
        
        long invalidSet=a|b|c|d;
        
        return invalidSet;
    }

    

}
