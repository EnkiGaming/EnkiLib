package com.enkigaming.lib.misc.coordinates;

public class CoOrdinate3d implements XYZCoOrdSet
{
    public CoOrdinate3d(int x, int y, int z)
    {
        this.x = x;
        this.y = y;
        this.z = z;
    }
    
    final int x, y, z;

    @Override
    public int getX()
    { return x; }

    @Override
    public int getY()
    { return y; }

    @Override
    public int getZ()
    { return z; }

    @Override
    public int hashCode()
    {
        int hash = 7;
        hash = 17 * hash + this.x;
        hash = 17 * hash + this.y;
        hash = 17 * hash + this.z;
        return hash;
    }

    @Override
    public boolean equals(Object obj)
    {
        if(obj == null)
            return false;
        if(getClass() != obj.getClass())
            return false;
        final CoOrdinate3d other = (CoOrdinate3d) obj;
        if(this.x != other.x)
            return false;
        if(this.y != other.y)
            return false;
        if(this.z != other.z)
            return false;
        return true;
    }
}