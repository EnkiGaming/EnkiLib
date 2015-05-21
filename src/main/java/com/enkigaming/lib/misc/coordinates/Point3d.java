package com.enkigaming.lib.misc.coordinates;

public class Point3d implements XYZPoint
{
    public Point3d(double x, double y, double z)
    {
        this.x = x;
        this.y = y;
        this.z = z;
    }
    
    public Point3d(int x, int y, int z)
    { this((double)x, (double)y, (double)z); }
    
    public Point3d(Number x, Number y, Number z)
    { this(x.doubleValue(), y.doubleValue(), z.doubleValue()); }
    
    public Point3d(XYZPoint source)
    { this(source.getX(), source.getY(), source.getZ()); }
    
    public Point3d(XYZCoOrdSet source)
    { this((double)source.getX(), (double)source.getY(), (double)source.getZ()); }
    
    final protected double x, y, z;
    
    @Override
    public double getX()
    { return x; }

    @Override
    public double getY()
    { return y; }

    @Override
    public double getZ()
    { return z; }

    @Override
    public int hashCode()
    {
        int hash = 7;
        hash = 53 * hash + (int) (Double.doubleToLongBits(this.x) ^ (Double.doubleToLongBits(this.x) >>> 32));
        hash = 53 * hash + (int) (Double.doubleToLongBits(this.y) ^ (Double.doubleToLongBits(this.y) >>> 32));
        hash = 53 * hash + (int) (Double.doubleToLongBits(this.z) ^ (Double.doubleToLongBits(this.z) >>> 32));
        return hash;
    }

    @Override
    public boolean equals(Object obj)
    {
        if(obj == null)
            return false;
        if(getClass() != obj.getClass())
            return false;
        final Point3d other = (Point3d) obj;
        if(Double.doubleToLongBits(this.x) != Double.doubleToLongBits(other.x))
            return false;
        if(Double.doubleToLongBits(this.y) != Double.doubleToLongBits(other.y))
            return false;
        if(Double.doubleToLongBits(this.z) != Double.doubleToLongBits(other.z))
            return false;
        return true;
    }
}