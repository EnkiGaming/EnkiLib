package com.enkigaming.mc.lib.misc;

import com.enkigaming.lib.misc.coordinates.Point3d;
import com.enkigaming.lib.misc.coordinates.XYZCoOrdSet;
import com.enkigaming.lib.misc.coordinates.XYZPoint;

public class PlayerPosition extends Point3d
{
    public PlayerPosition(double x, double y, double z, double yaw, double pitch)
    {
        super(x, y, z);
        this.yaw = yaw;
        this.pitch = pitch;
    }
    
    public PlayerPosition(double x, double y, double z)
    { this(x, y, z, 0, 0); }
    
    public PlayerPosition(int x, int y, int z, int yaw, int pitch)
    { this((double)x, (double)y, (double)z, (double)yaw, (double)pitch); }
    
    public PlayerPosition(int x, int y, int z)
    { this((double)x, (double)y, (double)z); }
    
    public PlayerPosition(Number x, Number y, Number z, Number yaw, Number pitch)
    { this(x.doubleValue(), y.doubleValue(), z.doubleValue(), yaw.doubleValue(), pitch.doubleValue()); }
    
    public PlayerPosition(Number x, Number y, Number z)
    { this(x.doubleValue(), y.doubleValue(), z.doubleValue()); }
    
    public PlayerPosition(XYZPoint source, double yaw, double pitch)
    { this(source.getX(), source.getY(), source.getZ(), yaw, pitch); }
    
    public PlayerPosition(XYZPoint source, int yaw, int pitch)
    { this(source.getX(), source.getY(), source.getZ(), (double)yaw, (double)pitch); }
    
    public PlayerPosition(XYZPoint source, Number yaw, Number pitch)
    { this(source.getX(), source.getY(), source.getZ(), yaw.intValue(), pitch.intValue()); }
    
    public PlayerPosition(XYZPoint source)
    { this((double)source.getX(), (double)source.getY(), (double)source.getZ()); }
    
    public PlayerPosition(XYZCoOrdSet source, double yaw, double pitch)
    { this((double)source.getX(), (double)source.getY(), (double)source.getZ(), yaw, pitch); }
    
    public PlayerPosition(XYZCoOrdSet source, int yaw, int pitch)
    { this((double)source.getX(), (double)source.getY(), (double)source.getZ(), (double)yaw, (double)pitch); }
    
    public PlayerPosition(XYZCoOrdSet source, Number yaw, Number pitch)
    { this((double)source.getX(), (double)source.getY(), (double)source.getZ(), yaw.doubleValue(), pitch.doubleValue()); }
    
    public PlayerPosition(XYZCoOrdSet source)
    { this((double)source.getX(), (double)source.getY(), (double)source.getZ()); }
    
    final protected double yaw, pitch;
    
    public double getYaw()
    { return yaw; }
    
    public double getPitch()
    { return pitch; }

    @Override
    public int hashCode()
    {
        int hash = 7;
        hash = 67 * hash + (int) (Double.doubleToLongBits(this.x)     ^ (Double.doubleToLongBits(this.x)     >>> 32));
        hash = 67 * hash + (int) (Double.doubleToLongBits(this.y)     ^ (Double.doubleToLongBits(this.y)     >>> 32));
        hash = 67 * hash + (int) (Double.doubleToLongBits(this.z)     ^ (Double.doubleToLongBits(this.z)     >>> 32));
        hash = 67 * hash + (int) (Double.doubleToLongBits(this.yaw)   ^ (Double.doubleToLongBits(this.yaw)   >>> 32));
        hash = 67 * hash + (int) (Double.doubleToLongBits(this.pitch) ^ (Double.doubleToLongBits(this.pitch) >>> 32));
        return hash;
    }

    @Override
    public boolean equals(Object obj)
    {
        if(obj == null)
            return false;
        if(getClass() != obj.getClass())
            return false;
        final PlayerPosition other = (PlayerPosition) obj;
        if(Double.doubleToLongBits(this.x) != Double.doubleToLongBits(other.x))
            return false;
        if(Double.doubleToLongBits(this.y) != Double.doubleToLongBits(other.y))
            return false;
        if(Double.doubleToLongBits(this.z) != Double.doubleToLongBits(other.z))
            return false;
        if(Double.doubleToLongBits(this.yaw) != Double.doubleToLongBits(other.yaw))
            return false;
        if(Double.doubleToLongBits(this.pitch) != Double.doubleToLongBits(other.pitch))
            return false;
        return true;
    }
}