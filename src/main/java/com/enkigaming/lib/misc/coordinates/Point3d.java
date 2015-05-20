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
    
    final double x, y, z;
    
    @Override
    public double getX()
    { return x; }

    @Override
    public double getY()
    { return y; }

    @Override
    public double getZ()
    { return z; }
    
}