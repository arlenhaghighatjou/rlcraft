package net.minecraft.world;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

public final class DimensionType
{
    private static final List<DimensionType> VALUES = new ArrayList<>();
    private static final Map<String, DimensionType> BY_NAME = new HashMap<>();
    public static final DimensionType OVERWORLD = new DimensionType("OVERWORLD", 0, "overworld", "", WorldProviderSurface.class, WorldProviderSurface::new);
    public static final DimensionType NETHER = new DimensionType("NETHER", -1, "the_nether", "_nether", WorldProviderHell.class, WorldProviderHell::new);
    public static final DimensionType THE_END = new DimensionType("THE_END", 1, "the_end", "_end", WorldProviderEnd.class, WorldProviderEnd::new);

    private final String constantName;
    private final int ordinal;
    private final int id;
    private final String name;
    private final String suffix;
    private final Class <? extends WorldProvider > clazz;
	private final Supplier<? extends WorldProvider> factory;
    private boolean shouldLoadSpawn = false;

    private DimensionType(String constantName, int idIn, String nameIn, String suffixIn, Class<? extends WorldProvider> clazzIn, Supplier<? extends WorldProvider> factory)
    {
        this.constantName = constantName;
        this.ordinal = VALUES.size();
        VALUES.add(this);
        BY_NAME.put(constantName, this);
        this.id = idIn;
        this.name = nameIn;
        this.suffix = suffixIn;
        this.clazz = clazzIn;
        this.factory = factory;
        this.shouldLoadSpawn = idIn == 0;
    }

    public int getId()
    {
        return this.id;
    }

    public String getName()
    {
        return this.name;
    }

    public String getSuffix()
    {
        return this.suffix;
    }

    public WorldProvider createDimension()
    {
        return this.factory.get();
    }

    public static DimensionType getById(int id)
    {
        for (int i = 0, l = VALUES.size(); i < l; ++i)
        {
            DimensionType dimensiontype = VALUES.get(i);

            if (dimensiontype.getId() == id)
            {
                return dimensiontype;
            }
        }

        throw new IllegalArgumentException("Invalid dimension id " + id);
    }

    public boolean shouldLoadSpawn(){ return this.shouldLoadSpawn; }
    public DimensionType setLoadSpawn(boolean value) { this.shouldLoadSpawn = value; return this; }

    public static DimensionType register(String name, String suffix, int id, Class<? extends WorldProvider> provider, Supplier<? extends WorldProvider> factory, boolean keepLoaded)
    {
        String enum_name = name.replace(" ", "_").toLowerCase();
        DimensionType ret = new DimensionType(enum_name, id, name, suffix, provider, factory);
        return ret.setLoadSpawn(keepLoaded);
    }

    public static DimensionType valueOf(String name)
    {
        DimensionType type = BY_NAME.get(java.util.Objects.requireNonNull(name, "name"));
        if (type == null)
        {
            throw new IllegalArgumentException("No enum constant net.minecraft.world.DimensionType." + name);
        }
        return type;
    }

    public static DimensionType[] values()
    {
        return VALUES.toArray(new DimensionType[VALUES.size()]);
    }

    public String name()
    {
        return this.constantName;
    }

    public int ordinal()
    {
        return this.ordinal;
    }

    @Override
    public String toString()
    {
        return this.constantName;
    }
    //TODO: Unregister? There is no way to really delete a enum value...

    public static DimensionType byName(String p_193417_0_)
    {
        for (int i = 0, l = VALUES.size(); i < l; ++i)
        {
            DimensionType dimensiontype = VALUES.get(i);

            if (dimensiontype.getName().equals(p_193417_0_))
            {
                return dimensiontype;
            }
        }

        throw new IllegalArgumentException("Invalid dimension " + p_193417_0_);
    }
}
