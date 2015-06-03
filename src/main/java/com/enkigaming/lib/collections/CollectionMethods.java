package com.enkigaming.lib.collections;

import com.enkigaming.lib.exceptions.NullArgumentException;
import com.enkigaming.lib.tuples.Pair;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;

/**
 * Static methods to do with collections. Were this written in a language that supported extension methods, most of the
 * methods in this class would be implemented as such, either to be called on the object reference or statically on the
 * class.
 * @author Hanii Puppy <hanii.puppy@googlemail.com>
 */
public class CollectionMethods
{
    /**
     * Takes a number of collections and creates a new collection with all of the members within. Where two collections
     * contain the same object, that object will be includes in the resulting collection multiple times.
     * @param <T> The type (or common derived-from type) of the collections being passed in.
     * @param collections The collections from which to gather members to put into the resulting collection.
     * @return A new collection containing the members of all passed in collections.
     */
    public static <T> Collection<T> combineCollection(Collection<? extends T>... collections)
    { return combineCollections(true, collections); }
    
    /**
     * Takes a number of collections and creates a new collection with all of the members within.
     * @param <T> The type (or common derived-from type) of the collections being passed in.
     * @param allowDuplicates Whether or not the same object should be included multiple times in the resulting
     * collection if it appears multiple times in any of the passed in collections or across all of the passed in
     * collections.
     * @param collections The collections from which to gather members to put into the resulting collection.
     * @return A new collection containing the members of all passed in collections.
     */
    public static <T> Collection<T> combineCollections(boolean allowDuplicates, Collection<? extends T>... collections)
    {
        if(collections == null)
            throw new NullArgumentException("collections");
        
        Collection<T> combinedCollection = allowDuplicates ? new ArrayList<T>() : new HashSet<T>();
        
        for(Collection<? extends T> i : collections)
            if(i != null)
                combinedCollection.addAll(i);
        
        return combinedCollection;
    }
    
    /**
     * Gets a collection containing pairs of the keys and values contained within the map at the point of calling this
     * methods. The resulting collection is not backed by the map or vice-versa.
     * @param <K> The key type of the map.
     * @param <V> The value type of the map.
     * @param map The map from which to get the values to fill the resulting collection of pairs.
     * @return A collection of pairs of keys and values, containing the matched keys and values contained within the
     * passed map.
     */
    public static <K, V> Collection<Pair<K, V>> getMapAsCollectionOfPairs(Map<K, V> map)
    {
        Collection<Pair<K, V>> col = new HashSet<Pair<K, V>>();
        Map<K, V> mapCopy = new HashMap<K, V>(map);
        
        for(Entry<K, V> entry : mapCopy.entrySet())
            col.add(new Pair<K, V>(entry.getKey(), entry.getValue()));
        
        return col;
    }
}