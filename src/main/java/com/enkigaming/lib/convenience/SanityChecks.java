package com.enkigaming.lib.convenience;

import com.enkigaming.lib.exceptions.NullArgumentException;
import com.enkigaming.lib.tuples.Pair;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

/**
 * A set of static methods for shortening boilerplate code for checking passed arguments, states, etc.
 * @author Hanii Puppy <hanii.puppy@googlemail.com>
 */
public class SanityChecks
{
    /**
     * Interface for checking an object to see if it contains nulls or not.
     */
    static interface DeepNullChecker
    { void check(Object toCheck, String msg); }
    
    /**
     * Checks to make sure none of the objects contained within the passed collection are null.
     * @param nullables A collection of pairs of objects and strings - the object to check for being null, and the
     * string to pass into the null argument exception if the object turns out to be null.
     */
    public static void nullCheck(Collection<? extends Pair<? extends Object, ? extends String>> nullables)
    {
        for(Pair<? extends Object, ? extends String> i : nullables)
            if(i.getFirst() == null)
                throw new NullArgumentException(i.getSecond());
    }
    
    /**
     * Checks to make sure none of the value objects contained within the passed map are null.
     * @param nullables A map containing objects to check whether or not they're null as the values, and strings to be
     * passed into the null argument exceptions if the relative object is null, as the keys.
     */
    public static void nullCheck(Map<? extends String, ? extends Object> nullables)
    {
        for(Map.Entry<? extends String, ? extends Object> i : nullables.entrySet())
            if(i.getValue() == null)
                throw new NullArgumentException(i.getKey());
    }
    
    /**
     * Checks to make sure none of the objects contained within the passed array are null.
     * @param nullables An array of pairs of objects and strings - the object to check for being null, and the
     * string to pass into the null argument exception if the object turns out to be null.
     */
    public static void nullCheck(Pair<? extends Object, ? extends String>... nullables)
    {
        for(Pair<? extends Object, ? extends String> i : nullables)
            if(i.getFirst() == null)
                throw new NullArgumentException(i.getSecond());
    }
    
    /**
     * Checks to make sure none of the first members of each of the passed arrays are null. Each of the arrays passed
     * in act like pairs in the overload of this that takes an array of pairs. Arrays should have at least two members,
     * the first is checked for nullity, and the second is passed into the illegal argument exception if the first is
     * null.
     * @param nullables 
     */
    public static void nullCheck(Object[]... nullables)
    {
        for(Object[] i : nullables)
        {
            if(i.length < 2)
                throw new IllegalArgumentException("Incomplete member of nullables.");
            
            if(i[0] == null)
                throw new NullArgumentException(i[1].toString());
        }
    }
    
    /**
     * All of the deep null checkers to use in deep null checks.
     */
    static DeepNullChecker[] deepNullCheckers = new DeepNullChecker[]
    {
        /**
         * Check instances of List.
         */
        new DeepNullChecker() // List
        {
            @Override
            public void check(Object toCheck, String msg)
            {
                if(toCheck instanceof List)
                {
                    Collection<Pair<Object, String>> lowerNullables = new HashSet<Pair<Object, String>>();

                    for(int i = 0; i < ((List)toCheck).size(); i++)
                        lowerNullables.add(new Pair<Object, String>(((List)toCheck).get(i),
                                           msg + "[" + i + "]"));

                    deepNullCheck(lowerNullables);
                }
            }
        },
        
        /**
         * Check instances of Array
         */
        new DeepNullChecker() // Array
        {
            @Override
            public void check(Object toCheck, String msg)
            {
                if(toCheck instanceof Object[])
                {
                    Collection<Pair<Object, String>> lowerNullables = new HashSet<Pair<Object, String>>();

                    for(int i = 0; i < ((Object[])toCheck).length; i++)
                        lowerNullables.add(new Pair<Object, String>(((Object[])toCheck)[i],
                                           msg + "[" + i + "]"));

                    deepNullCheck(lowerNullables);
                }
            }
        },
        
        /**
         * Check instances of Map
         */
        new DeepNullChecker() // Map
        {
            @Override
            public void check(Object toCheck, String msg)
            {
                if(toCheck instanceof Map)
                {
                    Collection<Pair<Object, String>> lowerNullables = new HashSet<Pair<Object, String>>();
                    
                    for(Object i : ((Map)toCheck).entrySet())
                        lowerNullables.add(new Pair<Object, String>(((Map.Entry)i).getValue(),
                                           msg + "[" + ((Map.Entry)i).getKey().toString() + "]"));
                    
                    deepNullCheck(lowerNullables);
                }
            }
        },
        
        /**
         * Check instances of Collection that aren't instances of List.
         */
        new DeepNullChecker() // Collection
        {
            @Override
            public void check(Object toCheck, String msg)
            {
                if(toCheck instanceof Collection && !(toCheck instanceof List))
                {
                    Collection<Pair<Object, String>> lowerNullables = new HashSet<Pair<Object, String>>();
                    
                    for(Object i : (Collection)toCheck)
                        lowerNullables.add(new Pair<Object, String>(i, msg + "[?]"));
                    
                    deepNullCheck(lowerNullables);
                }
            }
        }
    };
    
    /**
     * Checks the passed object against all deep null checkers in deepNullCheckers
     * @param obj The object to check for the presence of null.
     * @param msg The message to pass to the illegal argument exception should a null be present.
     */
    static void deepNullCheckObject(Object obj, String msg)
    {
        for(DeepNullChecker i : deepNullCheckers)
            i.check(obj, msg);
    }
    
    /**
     * Checks the first object in each of the passed pairs for null, and for containing null if it can.
     * @param nullables A collection containing pairs of objects that might be or might contain null, and the messages
     * to pass to the illegal argument exception should that particular object be or contain null.
     */
    public static void deepNullCheck(Collection<? extends Pair<? extends Object, ? extends String>> nullables)
    {
        for(Pair<? extends Object, ? extends String> i : nullables)
        {
            if(i.getFirst() == null)
                throw new NullArgumentException(i.getSecond());
            
            deepNullCheckObject(i.getFirst(), i.getSecond());
        }
    }
    
    /**
     * Checks the value in each entry of the passed map for null, and for containing null if it can.
     * @param nullables A map containing objects that might be or might contain null as the values, with the messages
     * to pass to the illegal argument exception that would be raised if they do as the keys.
     */
    public static void deepNullCheck(Map<? extends String, ? extends Object> nullables)
    {
        for(Map.Entry<? extends String, ? extends Object> i : nullables.entrySet())
        {
            if(i.getValue() == null)
                throw new NullArgumentException(i.getKey());
            
            deepNullCheckObject(i.getValue(), i.getKey());
        }
    }
    
    /**
     * Checks the first object in each of the passed pairs for null, and for containing null if it can.
     * @param nullables An array containing pairs of objects that might be or might contain null, and the messages to
     * pass to the illegal argument exception should that particular object be or contain null.
     */
    public static void deepNullCheck(Pair<? extends Object, ? extends String>... nullables)
    {
        for(Pair<? extends Object, ? extends String> i : nullables)
        {
            if(i.getFirst() == null)
                throw new NullArgumentException(i.getSecond());
            
            deepNullCheckObject(i.getFirst(), i.getSecond());
        }
    }
    
    /**
     * Checks the first value in each of the passed arrays for null, and for containing null if it can.
     * @param nullables An array of arrays, which are each supposed to contain two objects, the first an object to check
     * whether or not it is null or contains null, the second a string to pass to the illegal argument exception that
     * would be raised if it is/does.
     */
    public static void deepNullCheck(Object[]... nullables)
    {
        for(int i = 0; i < nullables.length; i++)
        {
            if(nullables[i].length < 2)
                throw new IllegalArgumentException("Incomplete member of nullables. [" + i + "]");
            
            if(nullables[i][0] == null)
                throw new NullArgumentException(nullables[i][1].toString());
            
            deepNullCheckObject(nullables[i][0], nullables[i][1].toString());
        }
    }
}