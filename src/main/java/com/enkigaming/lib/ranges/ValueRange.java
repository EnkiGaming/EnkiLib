package com.enkigaming.lib.ranges;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class ValueRange<T extends Comparable<T>> implements FlatRange<T>
{
    public ValueRange(T min, T max)
    {
        this.min = min;
        this.max = max;
    }
    
    public ValueRange(Range<? extends T> source)
    {
        min = source.getMin();
        max = source.getMax();
    }
    
    protected final T min, max;

    @Override
    public List<Range<T>> splitBy(final T splitPoint)
    {
        if(splitPoint.compareTo(min) < 0 || splitPoint.compareTo(max) > 0)
            return Arrays.<Range<T>>asList(this);
        
        return Arrays.<Range<T>>asList(new ValueRange<T>(min, splitPoint),
                                       new ValueRange<T>(splitPoint, max));
    }

    @Override
    public List<Range<T>> splitBy(T... splitPoints)
    {
        List<Range<T>> results = new ArrayList<Range<T>>();
        T[] splits = Arrays.copyOf(splitPoints, splitPoints.length);
        Arrays.sort(splits, new Comparator<T>()
        {
            @Override
            public int compare(T o1, T o2)
            { return o1.compareTo(o2); }
        });
        
        T previousSplit = min;
        
        for(int i = 0; i < splits.length; i++)
            if(splits[i].compareTo(min) > 0 && splits[i].compareTo(max) < 0)
                results.add(new ValueRange<T>(previousSplit, previousSplit = splits[i]));
        
        if(previousSplit.compareTo(max) <= 0)
            results.add(new ValueRange<T>(previousSplit, max));
        
        return results;
    }

    @Override
    public List<Range<T>> splitBy(Collection<? extends T> splitPoints)
    {
        List<Range<T>> results = new ArrayList<Range<T>>();
        List<T> splits = new ArrayList<T>(splitPoints);
        Collections.sort(splits);
        
        T previousSplit = min;
        
        for(int i = 0; i < splits.size(); i++)
            if(splits.get(i).compareTo(min) > 0 && splits.get(i).compareTo(max) < 0)
                results.add(new ValueRange<T>(previousSplit, previousSplit = splits.get(i)));
        
        if(previousSplit.compareTo(max) <= 0)
            results.add(new ValueRange<T>(previousSplit, max));
        
        return results;
    }

    @Override
    public T getMin()
    { return min; }

    @Override
    public T getMax()
    { return max; }

    @Override
    public boolean contains(T value)
    { return min.compareTo(value) <= 0 && max.compareTo(value) >= 0; }

    @Override
    public boolean containsAll(T... values)
    {
        for(T i : values)
            if(!contains(i))
                return false;
        
        return true;
    }

    @Override
    public boolean containsAll(Collection<? extends T> values)
    {
        for(T i : values)
            if(!contains(i))
                return false;
        
        return true;
    }

    @Override
    public boolean containsAny(T... values)
    {
        for(T i : values)
            if(contains(i))
                return true;
        
        return false;
    }

    @Override
    public boolean containsAny(Collection<? extends T> values)
    {
        for(T i : values)
            if(contains(i))
                return true;
        
        return false;
    }

    @Override
    public boolean overlapsWith(Range<? extends T> other)
    {
        if(other.getMin().compareTo(max) > 0 || other.getMax().compareTo(min) < 0)
            return false;
        
        if(other instanceof FlatRange)
            return true;
        
        for(FlatRange<? extends T> i : other.toListOfFlatRanges())
            if(overlapsWith(i))
                return true;

        return false;
    }

    @Override
    public boolean encloses(Range<? extends T> other)
    { return other.getMin().compareTo(min) >= 0 && other.getMax().compareTo(max) <= 0; }

    @Override
    public boolean isEnclosedBy(Range<? extends T> other)
    { return other.getMin().compareTo(min) <= 0 && other.getMax().compareTo(max) >= 0; }

    @Override
    public List<FlatRange<T>> toListOfFlatRanges()
    { return Arrays.<FlatRange<T>>asList(this); }

    @Override
    public FlatRange<T> toFlatRange()
    { return this; }

    @Override
    public Range<T> getOverlapWith(Range<? extends T> other)
    {
        if(!overlapsWith(other))
            return null;
        
        List<FlatRange<T>> overlaps = new ArrayList<FlatRange<T>>();
        List<FlatRange<? extends T>> otherAsFlatRanges
            = new ArrayList<FlatRange<? extends T>>(other.toListOfFlatRanges());
        
        for(FlatRange<? extends T> i : otherAsFlatRanges)
        {
            if(i.getMax().compareTo(min) < 0 || i.getMin().compareTo(max) > 0)
                continue;
            
            T overlapMin = i.getMin().compareTo(min) < 0 ? min : i.getMin();
            T overlapMax = i.getMax().compareTo(max) > 0 ? max : i.getMax();
            overlaps.add(new ValueRange<T>(overlapMin, overlapMax));
        }
        
        if(overlaps.isEmpty())
            return null;
        
        if(overlaps.size() == 1)
            overlaps.get(0);
        
        return new ExclusiveRange<T>(overlaps);
    }

    @Override
    public Range<T> getOverlapWith(Range<? extends T>... others)
    {
        Range<T> result = new ValueRange<T>(this);

        for(Range<? extends T> i : others)
            result = result.getOverlapWith(i);

        return result;
    }

    @Override
    public Range<T> getOverlapWith(Collection<? extends Range<? extends T>> others)
    {
        Range<T> result = new ValueRange<T>(this);

        for(Range<? extends T> i : others)
            result = result.getOverlapWith(i);

        return result;
    }

    @Override
    public Range<T> exclude(Range<? extends T> other)
    {
        List<? extends FlatRange<? extends T>> otherAsFlatRanges = other.toListOfFlatRanges();
        List<FlatRange<T>> results = new ArrayList<FlatRange<T>>();
        T lastExclusionTop = min;
        
        for(FlatRange<? extends T> i : otherAsFlatRanges)
        {
            if(i.getMax().compareTo(min) < 0 || i.getMin().compareTo(max) > 0)
                continue;
            
            /*
                continue writing...
            
                Start taking into account the passed range possibly including explicitly held values for inclusion.
            
                That is, ExclusiveRanges should include a list of values, in addition to the single values being held
                in the list as flat ranges with the same value as the min and max. Values explicitly excluded should
                be held in another list as well.
            
                This solves complications relating to getting mutated copies where individual values rather than ranges
                of values are at sensitive areas.
            */
        }
    }

    @Override
    public Range<T> excludeAll(Range<? extends T>... others)
    {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Range<T> excludeAll(Collection<? extends Range<? extends T>> others)
    {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Range<T> include(Range<? extends T> other)
    {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Range<T> includeAll(Range<? extends T>... others)
    {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Range<T> includeAll(Collection<? extends Range<? extends T>> others)
    {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Range<T> includeValue(T toInclude)
    {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Range<T> includeAllValues(T... toInclude)
    {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Range<T> includeAllValues(Collection<? extends T> toInclude)
    {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public boolean coversTheSameValuesAs(Range<? extends T> other)
    {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}