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
    
    final int x, y;

    @Override
    public int getX()
    { return x; }

    @Override
    public int getY()
    { return y; }
}