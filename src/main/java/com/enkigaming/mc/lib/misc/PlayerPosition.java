package com.enkigaming.mc.lib.misc;

import com.enkigaming.lib.misc.coordinates.Point3d;
import com.enkigaming.lib.misc.coordinates.XYZCoOrdSet;
import com.enkigaming.lib.misc.coordinates.XYZPoint;

public class PlayerPosition extends Point3d
{
    /*
    
    Let's just take a moment here to appreciate a few features from C# that Java could do with: default parameters,
    named arguments, and the unified type system. Between these three features, the number of constructors required for
    a class can be *drastically* shortened. Even if it was just thw two most easily implementable features, which are
    default parameters and named arguments, things would be so much better. For instance, see this mess of
    constructors? With these three features, the constructor list of this class would be:
    
    public PlayerPosition(int worldId = 0, double x, double y, double z, double yaw = Double.NaN, double pitch = Double.NaN) {}
    public PlayerPosition(int worldId = 0, XYZCoOrdSet source,           double yaw = Double.NaN, double pitch = Double.NaN) {}
    public PlayerPosition(int worldId = 0, XYZPoint source,              double yaw = Double.NaN, double pitch = Double.NaN) {}
    
    There. Isn't that better? All of those constructors condensed down to *three*. To implement default parameters and
    named arguments wouldn't event require any modification to the JVM, they can be handled at compile-time. Features
    like this may be considered syntactical sugar, but they *do help*.
    
    But are Oracle going to add these features? Are they buggery. When you bring up features like this to people that
    are primarily Java fans, they just espouse the "simpler" way that we have at the moment. The problem with this is
    that when they say simpler, they mean a simpler language, which translates as fewer features, etc. Certain features
    introduce new syntax to the language, but make *usage* of the language simpler. On top of that, Oracle seem
    desperate to try and differentiate themselves from C# as much as possible, which just seems to a form of
    self-sabotage. I don't think Oracle are ever going to implement there features unless they either make a massive
    about-face on the matter, or find a way that seems to them to be sufficiently different from the way C# does it.
    
    */
    
    public PlayerPosition(int worldId, double x, double y, double z, double yaw, double pitch)
    {
        super(x, y, z);
        this.worldId = worldId;
        this.yaw = yaw;
        this.pitch = pitch;
    }
    
    public PlayerPosition(double x, double y, double z, double yaw, double pitch)
    { this(0, x, y, z, yaw, pitch); }
    
    public PlayerPosition(int worldId, double x, double y, double z)
    { this(worldId, x, y, z, 0, 0); noDirection = true; }
    
    public PlayerPosition(double x, double y, double z)
    { this(0, x, y, z, 0, 0); noDirection = true; }
    
    public PlayerPosition(int worldId, int x, int y, int z, int yaw, int pitch)
    { this(worldId, (double)x, (double)y, (double)z, (double)yaw, (double)pitch); }
    
    public PlayerPosition(int x, int y, int z, int yaw, int pitch)
    { this(0, (double)x, (double)y, (double)z, (double)yaw, (double)pitch); }
    
    public PlayerPosition(int worldId, int x, int y, int z)
    { this(worldId, (double)x, (double)y, (double)z, 0, 0); noDirection = true; }
    
    public PlayerPosition(int x, int y, int z)
    { this(0, (double)x, (double)y, (double)z, 0, 0); noDirection = true; }
    
    public PlayerPosition(int worldId, Number x, Number y, Number z, Number yaw, Number pitch)
    { this(worldId, x.doubleValue(), y.doubleValue(), z.doubleValue(), yaw.doubleValue(), pitch.doubleValue()); }
    
    public PlayerPosition(Number x, Number y, Number z, Number yaw, Number pitch)
    { this(0, x.doubleValue(), y.doubleValue(), z.doubleValue(), yaw.doubleValue(), pitch.doubleValue()); }
    
    public PlayerPosition(int worldId, Number x, Number y, Number z)
    { this(worldId, x.doubleValue(), y.doubleValue(), z.doubleValue(), 0, 0); noDirection = true; }
    
    public PlayerPosition(Number x, Number y, Number z)
    { this(0, x.doubleValue(), y.doubleValue(), z.doubleValue(), 0, 0); noDirection = true; }
    
    public PlayerPosition(int worldId, XYZPoint source, double yaw, double pitch)
    { this(worldId, source.getX(), source.getY(), source.getZ(), yaw, pitch); }
    
    public PlayerPosition(XYZPoint source, double yaw, double pitch)
    { this(0, source.getX(), source.getY(), source.getZ(), yaw, pitch); }
    
    public PlayerPosition(int worldId, XYZPoint source, int yaw, int pitch)
    { this(worldId, source.getX(), source.getY(), source.getZ(), (double)yaw, (double)pitch); }
    
    public PlayerPosition(XYZPoint source, int yaw, int pitch)
    { this(0, source.getX(), source.getY(), source.getZ(), (double)yaw, (double)pitch); }
    
    public PlayerPosition(int worldId, XYZPoint source, Number yaw, Number pitch)
    { this(worldId, source.getX(), source.getY(), source.getZ(), yaw.intValue(), pitch.intValue()); }
    
    public PlayerPosition(XYZPoint source, Number yaw, Number pitch)
    { this(0, source.getX(), source.getY(), source.getZ(), yaw.intValue(), pitch.intValue()); }
    
    public PlayerPosition(int worldId, XYZPoint source)
    { this(worldId, (double)source.getX(), (double)source.getY(), (double)source.getZ(), 0, 0); noDirection = true; }
    
    public PlayerPosition(XYZPoint source)
    { this(0, (double)source.getX(), (double)source.getY(), (double)source.getZ(), 0, 0); noDirection = true; }
    
    public PlayerPosition(int worldId, XYZCoOrdSet source, double yaw, double pitch)
    { this(worldId, (double)source.getX(), (double)source.getY(), (double)source.getZ(), yaw, pitch); }
    
    public PlayerPosition(XYZCoOrdSet source, double yaw, double pitch)
    { this(0, (double)source.getX(), (double)source.getY(), (double)source.getZ(), yaw, pitch); }
    
    public PlayerPosition(int worldId, XYZCoOrdSet source, int yaw, int pitch)
    { this(worldId, (double)source.getX(), (double)source.getY(), (double)source.getZ(), (double)yaw, (double)pitch); }
    
    public PlayerPosition(XYZCoOrdSet source, int yaw, int pitch)
    { this(0, (double)source.getX(), (double)source.getY(), (double)source.getZ(), (double)yaw, (double)pitch); }
    
    public PlayerPosition(int worldId, XYZCoOrdSet source, Number yaw, Number pitch)
    { this(worldId, (double)source.getX(), (double)source.getY(), (double)source.getZ(), yaw.doubleValue(), pitch.doubleValue()); }
    
    public PlayerPosition(XYZCoOrdSet source, Number yaw, Number pitch)
    { this(0, (double)source.getX(), (double)source.getY(), (double)source.getZ(), yaw.doubleValue(), pitch.doubleValue()); }
    
    public PlayerPosition(int worldId, XYZCoOrdSet source)
    { this(worldId, (double)source.getX(), (double)source.getY(), (double)source.getZ(), 0, 0); noDirection = true; }
    
    public PlayerPosition(XYZCoOrdSet source)
    { this(0, (double)source.getX(), (double)source.getY(), (double)source.getZ(), 0, 0); noDirection = true; }
    
    final protected double yaw, pitch;
    final protected int worldId;
    protected boolean noDirection = false;
    
    public double getYaw()
    { return yaw; }
    
    public double getPitch()
    { return pitch; }
    
    public int getWorldId()
    { return worldId; }
    
    public boolean directionWasSpecified()
    { return !noDirection; }

    @Override
    public int hashCode()
    {
        int hash = 7;
        hash = 67 * hash + this.worldId;
        hash = 67 * hash + (int) (Double.doubleToLongBits(this.x)     ^ (Double.doubleToLongBits(this.x)     >>> 32));
        hash = 67 * hash + (int) (Double.doubleToLongBits(this.y)     ^ (Double.doubleToLongBits(this.y)     >>> 32));
        hash = 67 * hash + (int) (Double.doubleToLongBits(this.z)     ^ (Double.doubleToLongBits(this.z)     >>> 32));
        hash = 67 * hash + (int) (Double.doubleToLongBits(this.yaw)   ^ (Double.doubleToLongBits(this.yaw)   >>> 32));
        hash = 67 * hash + (int) (Double.doubleToLongBits(this.pitch) ^ (Double.doubleToLongBits(this.pitch) >>> 32));
        hash = 67 * hash + (this.noDirection ? 1 : 0);
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