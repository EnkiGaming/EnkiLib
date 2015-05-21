package com.enkigaming.lib.misc.coordinates;

public class CoOrdinate2d implements XYCoOrdPair
{
    public CoOrdinate2d(int x, int y)
    {
        this.x = x;
        this.y = y;
    }
    
    public CoOrdinate2d(Number x, Number y)
    { this(x.intValue(), y.intValue()); }
    
    public CoOrdinate2d(XYCoOrdPair source)
    { this(source.getX(), source.getY()); }
    
    public CoOrdinate2d(XYPoint source)
    { this((int)source.getX(), (int)source.getY()); }
    
    final protected int x, y;

    @Override
    public int getX()
    { return x; }

    @Override
    public int getY()
    { return y; }

    @Override
    public int hashCode()
    {
        int hash = 7;
        hash = 89 * hash + this.x;
        hash = 89 * hash + this.y;
        return hash;
    }

    @Override
    public boolean equals(Object obj)
    {
        if(obj == null)
            return false;
        if(getClass() != obj.getClass())
            return false;
        final CoOrdinate2d other = (CoOrdinate2d) obj;
        if(this.x != other.x)
            return false;
        if(this.y != other.y)
            return false;
        return true;
    }
}