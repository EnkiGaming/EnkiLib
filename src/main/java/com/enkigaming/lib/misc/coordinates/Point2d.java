package com.enkigaming.lib.misc.coordinates;

import org.apache.commons.lang3.NotImplementedException;

public class Point2d implements XYPoint
{
    public Point2d(double x, double y)
    {
        this.x = x;
        this.y = y;
    }
    
    public Point2d(int x, int y)
    { this((double)x, (double)y); }
    
    public Point2d(Number x, Number y)
    { this(x.doubleValue(), y.doubleValue()); }
    
    public Point2d(XYPoint source)
    { this(source.getX(), source.getY()); }
    
    public Point2d(XYCoOrdPair source)
    { this((double)source.getX(), (double)source.getY()); }
    
    double x, y;
    
    @Override
    public double getX()
    { return x; }

    @Override
    public double getY()
    { return y; }
}