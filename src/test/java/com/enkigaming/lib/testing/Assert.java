package com.enkigaming.lib.testing;

import java.util.ArrayList;
import java.util.Collection;
import static org.junit.Assert.fail;

public class Assert
{
    public static <T> void assertCollectionEquals(Collection<T> howItBe,
                                                  Collection<T> howItShouldBe)
    { assertCollectionEquals("", howItBe, howItShouldBe); }
    
    public static <T> void assertCollectionEquals(String message,
                                                  Collection<T> howItBe,
                                                  Collection<T> howItShouldBe)
    {
        if(howItBe == null)
            if(howItShouldBe == null)
                return;
            else
                fail(message + ": " + "Collection was null, should not have been.");
        
        if(howItShouldBe == null)
            fail(message + ": " + "Collection should have been null, was not.");
        
        if(howItBe.size() != howItShouldBe.size())
        {
            String sizeDescriptor;
            
            if(howItBe.size() > howItShouldBe.size())
                sizeDescriptor = "bigger";
            else
                sizeDescriptor = "smaller";
            
            fail(message + ": " + "Collection was " + sizeDescriptor + " than it should have been." + "\n\n"
               + "Contents of collection were: " + "\n" + howItBe.toString() + "\n\n"
               + "Contents of collection should have been: " + "\n" + howItShouldBe.toString());
        }
        
        Collection<T> howItBeCopy = new ArrayList<T>(howItBe);
        
        for(T i : howItShouldBe)
        {
            if(!howItBeCopy.remove(i))
                fail(message + ": " + "Collection didn't contain members it should have." + "\n\n"
                   + "Contents of collection were: " + "\n" + howItBe.toString() + "\n\n"
                   + "Contents of collection should have been: " + "\n" + howItShouldBe.toString());
        }
    }
    
    public static <T> void assertCollectionEmpty(Collection<T> howItBe)
    { assertCollectionEmpty("", howItBe); }
    
    public static <T> void assertCollectionEmpty(String message, Collection<T> howItBe)
    {
        if(howItBe == null)
            fail(message + ": " + "Collection was null, should not have been.");
        
        if(!howItBe.isEmpty())
            fail(message + ": " + "Collection was not empty. Contained: " + "\n"
               + howItBe.toString());
    }
}