package net.minecraft.network.datasync;

import net.minecraft.entity.Entity;

public class DataParameter<T>
{
    private int id;
    private final DataSerializer<T> serializer;
    private Class<? extends Entity> owner;
    private int index;

    public DataParameter(int idIn, DataSerializer<T> serializerIn)
    {
        this.id = idIn;
        this.serializer = serializerIn;
    }

    void bind(Class<? extends Entity> ownerIn, int indexIn)
    {
        this.owner = ownerIn;
        this.index = indexIn;
    }

    public int getId()
    {
        if (this.id < 0)
        {
            this.id = EntityDataManager.resolveId(this.owner, this.index);
        }

        return this.id;
    }

    public DataSerializer<T> getSerializer()
    {
        return this.serializer;
    }

    public boolean equals(Object p_equals_1_)
    {
        if (this == p_equals_1_)
        {
            return true;
        }
        else if (p_equals_1_ != null && this.getClass() == p_equals_1_.getClass())
        {
            DataParameter<?> dataparameter = (DataParameter)p_equals_1_;
            return this.getId() == dataparameter.getId();
        }
        else
        {
            return false;
        }
    }

    public int hashCode()
    {
        return this.getId();
    }
}
