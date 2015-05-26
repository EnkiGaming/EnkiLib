package com.enkigaming.lib.misc.coordinates;

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
    
    final protected double x, y;
    
    @Override
    public double getX()
    { return x; }

    @Override
    public double getY()
    { return y; }

    @Override
    public int hashCode()
    {
        int hash = 7;
        hash = 71 * hash + (int) (Double.doubleToLongBits(this.x) ^ (Double.doubleToLongBits(this.x) >>> 32));
        hash = 71 * hash + (int) (Double.doubleToLongBits(this.y) ^ (Double.doubleToLongBits(this.y) >>> 32));
        return hash;
    }

    @Override
    public boolean equals(Object obj)
    {
        if(obj == null)
            return false;
        if(getClass() != obj.getClass())
            return false;
        final Point2d other = (Point2d) obj;
        if(Double.doubleToLongBits(this.x) != Double.doubleToLongBits(other.x))
            return false;
        if(Double.doubleToLongBits(this.y) != Double.doubleToLongBits(other.y))
            return false;
        return true;
    }
}