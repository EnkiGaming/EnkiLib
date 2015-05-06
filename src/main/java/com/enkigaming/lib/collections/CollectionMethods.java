package com.enkigaming.lib.collections;

import com.enkigaming.lib.tuples.Pair;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;

public class CollectionMethods
{
    public static <T> Collection<T> combineCollection(Collection<? extends T>... collections)
    { return combineCollections(true, collections); }
    
    public static <T> Collection<T> combineCollections(boolean allowDuplicates, Collection<? extends T>... collections)
    {
        Collection<T> combinedCollection;
        
        if(allowDuplicates)
            combinedCollection = new ArrayList<T>();
        else
            combinedCollection = new HashSet<T>();
        
        for(Collection<? extends T> i : collections)
            combinedCollection.addAll(i);
        
        return combinedCollection;
    }
    
    public static <K, V> Collection<Pair<K, V>> getMapAsCollectionOfPairs(Map<K, V> map)
    {
        Collection<Pair<K, V>> col = new HashSet<Pair<K, V>>();
        Map<K, V> mapCopy = new HashMap<K, V>(map);
        
        for(Entry<K, V> entry : mapCopy.entrySet())
            col.add(new Pair<K, V>(entry.getKey(), entry.getValue()));
        
        return col;
    }
}