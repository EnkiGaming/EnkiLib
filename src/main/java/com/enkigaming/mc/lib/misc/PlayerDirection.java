package com.enkigaming.mc.lib.misc;

import com.enkigaming.lib.misc.coordinates.Point3d;
import com.enkigaming.lib.misc.coordinates.XYZCoOrdSet;
import com.enkigaming.lib.misc.coordinates.XYZPoint;

public class PlayerDirection extends Point3d
{
    public PlayerDirection(double x, double y, double z, double yaw, double pitch)
    {
        super(x, y, z);
        this.yaw = yaw;
        this.pitch = pitch;
    }
    
    public PlayerDirection(double x, double y, double z)
    { this(x, y, z, 0, 0); }
    
    public PlayerDirection(int x, int y, int z, int yaw, int pitch)
    { this((double)x, (double)y, (double)z, (double)yaw, (double)pitch); }
    
    public PlayerDirection(int x, int y, int z)
    { this((double)x, (double)y, (double)z); }
    
    public PlayerDirection(Number x, Number y, Number z, Number yaw, Number pitch)
    { this(x.doubleValue(), y.doubleValue(), z.doubleValue(), yaw.doubleValue(), pitch.doubleValue()); }
    
    public PlayerDirection(Number x, Number y, Number z)
    { this(x.doubleValue(), y.doubleValue(), z.doubleValue()); }
    
    public PlayerDirection(XYZPoint source, double yaw, double pitch)
    { this(source.getX(), source.getY(), source.getZ(), yaw, pitch); }
    
    public PlayerDirection(XYZPoint source, int yaw, int pitch)
    { this(source.getX(), source.getY(), source.getZ(), (double)yaw, (double)pitch); }
    
    public PlayerDirection(XYZPoint source, Number yaw, Number pitch)
    { this(source.getX(), source.getY(), source.getZ(), yaw.intValue(), pitch.intValue()); }
    
    public PlayerDirection(XYZPoint source)
    { this((double)source.getX(), (double)source.getY(), (double)source.getZ()); }
    
    public PlayerDirection(XYZCoOrdSet source, double yaw, double pitch)
    { this((double)source.getX(), (double)source.getY(), (double)source.getZ(), yaw, pitch); }
    
    public PlayerDirection(XYZCoOrdSet source, int yaw, int pitch)
    { this((double)source.getX(), (double)source.getY(), (double)source.getZ(), (double)yaw, (double)pitch); }
    
    public PlayerDirection(XYZCoOrdSet source, Number yaw, Number pitch)
    { this((double)source.getX(), (double)source.getY(), (double)source.getZ(), yaw.doubleValue(), pitch.doubleValue()); }
    
    public PlayerDirection(XYZCoOrdSet source)
    { this((double)source.getX(), (double)source.getY(), (double)source.getZ()); }
    
    public 
    
    final double yaw, pitch;
    
    public double getYaw()
    { return yaw; }
    
    public double getPitch()
    { return pitch; }
}