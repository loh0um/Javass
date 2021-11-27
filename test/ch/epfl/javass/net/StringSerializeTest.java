package ch.epfl.javass.net;

import org.junit.jupiter.api.Test;
import ch.epfl.javass.net.StringSerializer;

import static ch.epfl.test.TestRandomizer.newRandom;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Arrays;
import java.util.List;
import java.util.SplittableRandom;

public class StringSerializeTest {

    private final int ITERATIONS = 1_000_000;
    
    @Test
    void deserializeIntAfterSerializeIntGivesSameOutputAsInput() {
        
        SplittableRandom rng = newRandom();
        boolean egal=true;
        int i=0;
        
        while(egal && i<ITERATIONS) {
            int random=rng.nextInt(Integer.MAX_VALUE);
            if (random!=StringSerializer.deserializeInt(StringSerializer.serializeInt(random))) {
                egal=false;
            }
            ++i;
        }
        
        assertTrue(egal);        
    }
    
    @Test
    void deserializeLongAfterSerializeLongGivesSameOutputAsInput() {
        
        SplittableRandom rng = newRandom();
        boolean equal=true;
        int i=0;
        
        while(equal && i<ITERATIONS) {
            long random=rng.nextLong(Long.MAX_VALUE);
            if (random!=StringSerializer.deserializeLong(StringSerializer.serializeLong(random))) {
                equal=false;
            }
            ++i;
        }
        
        assertTrue(equal);        
    }
    
    @Test
    void serializeStringWorksWithSomeSpecialStrings() {
        List<String> names=Arrays.asList("Amélie","Gaëlle","Émile", "Nadège" );
        List<String> serializedNames = Arrays.asList("QW3DqWxpZQ==","R2HDq2xsZQ==", "w4ltaWxl", "TmFkw6hnZQ==");
        
        for (int i=0; i<names.size();++i) {
            assertEquals(serializedNames.get(i), StringSerializer.serializeString(names.get(i)));
        }              
    }
    
    @Test
    void deserializeStringWorksWithSomeSpecialStrings() {
        List<String> names=Arrays.asList("Amélie","Gaëlle","Émile", "Nadège" );
        List<String> serializedNames = Arrays.asList("QW3DqWxpZQ==","R2HDq2xsZQ==", "w4ltaWxl", "TmFkw6hnZQ==");
        
        for (int i=0; i<names.size();++i) {
            assertEquals(names.get(i), StringSerializer.deserializeString(serializedNames.get(i)));
        }              
    }
    
    @Test
    void deserializeStringAfterSerializeStringWorksWithSomeSpecialStrings() {
        List<String> names=Arrays.asList("Amélie","Gaëlle","Émile", "Nadège" );
        
        for (int i=0; i<names.size();++i) {
            String name=names.get(i);
            assertEquals(name, StringSerializer.deserializeString(StringSerializer.serializeString(name)));
        }              
    }
    
    @Test
    void combineWorksWithSomeValues() {
        String[] names=new String[]{"Amélie","Gaëlle","Émile", "Nadège" };
        
        assertEquals("Amélie,Gaëlle,Émile,Nadège", StringSerializer.combine(',', names));
        assertEquals("Amélie Gaëlle Émile Nadège", StringSerializer.combine(' ', names));      
    }
    
    @Test
    void combineAfterSplitWorksWithSomeValues() {
        
        String namesConcatenated="Amélie,Gaëlle,Émile,Nadège";
        String namesConcatenated2="Amélie Gaëlle Émile Nadège";
        
        assertEquals(namesConcatenated, StringSerializer.combine(',',StringSerializer.split(',', namesConcatenated)));
        assertEquals(namesConcatenated2, StringSerializer.combine(' ',StringSerializer.split(' ', namesConcatenated2)));
    }
    
}
