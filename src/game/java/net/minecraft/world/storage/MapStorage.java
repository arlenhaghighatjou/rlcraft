package net.minecraft.world.storage;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import javax.annotation.Nullable;
import net.lax1dude.eaglercraft.v1_8.EagRuntime;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagShort;
import net.minecraft.scoreboard.ScoreboardSaveData;
import net.minecraft.village.VillageCollection;
import net.minecraft.world.gen.structure.MapGenStructureData;
import net.minecraftforge.common.util.WorldCapabilityData;

public class MapStorage
{
    public static final Map<Class<? extends WorldSavedData>, Function<String, ? extends WorldSavedData>> storageProviders = new HashMap<>();

    static
    {
        storageProviders.put(MapData.class, MapData::new);
        storageProviders.put(MapGenStructureData.class, MapGenStructureData::new);
        storageProviders.put(ScoreboardSaveData.class, ScoreboardSaveData::new);
        storageProviders.put(VillageCollection.class, VillageCollection::new);
        storageProviders.put(WorldCapabilityData.class, WorldCapabilityData::new);
    }

    private final ISaveHandler saveHandler;
    protected Map<String, WorldSavedData> loadedDataMap = Maps.<String, WorldSavedData>newHashMap();
    private final List<WorldSavedData> loadedDataList = Lists.<WorldSavedData>newArrayList();
    private final Map<String, Short> idCounts = Maps.<String, Short>newHashMap();

    public MapStorage(ISaveHandler saveHandlerIn)
    {
        this.saveHandler = saveHandlerIn;
        this.loadIdCounts();
    }

    @Nullable
    public WorldSavedData getOrLoadData(Class <? extends WorldSavedData > clazz, String dataIdentifier)
    {
        WorldSavedData worldsaveddata = this.loadedDataMap.get(dataIdentifier);

        if (worldsaveddata != null)
        {
            return worldsaveddata;
        }
        else
        {
            if (this.saveHandler != null)
            {
                try
                {
                    NBTTagCompound nbttagcompound = this.saveHandler.readMapData(dataIdentifier, true);

                    if (nbttagcompound != null)
                    {
                        try
                        {
                            worldsaveddata = storageProviders.get(clazz).apply(dataIdentifier);
                        }
                        catch (Exception exception)
                        {
                            throw new RuntimeException("Failed to instantiate " + clazz, exception);
                        }

                        worldsaveddata.readFromNBT(nbttagcompound.getCompoundTag("data"));
                    }
                }
                catch (Exception exception1)
                {
                    EagRuntime.debugPrintStackTrace(exception1);
                }
            }

            if (worldsaveddata != null)
            {
                this.loadedDataMap.put(dataIdentifier, worldsaveddata);
                this.loadedDataList.add(worldsaveddata);
            }

            return worldsaveddata;
        }
    }

    public void setData(String dataIdentifier, WorldSavedData data)
    {
        if (this.loadedDataMap.containsKey(dataIdentifier))
        {
            this.loadedDataList.remove(this.loadedDataMap.remove(dataIdentifier));
        }

        this.loadedDataMap.put(dataIdentifier, data);
        this.loadedDataList.add(data);
    }

    public void saveAllData()
    {
        for (int i = 0; i < this.loadedDataList.size(); ++i)
        {
            WorldSavedData worldsaveddata = this.loadedDataList.get(i);

            if (worldsaveddata.isDirty())
            {
                if (this.saveData(worldsaveddata)) worldsaveddata.setDirty(false);
            }
        }
    }

    private boolean saveData(WorldSavedData data)
    {
        if (this.saveHandler != null)
        {
            try
            {
                NBTTagCompound nbttagcompound = new NBTTagCompound();
                nbttagcompound.setTag("data", data.writeToNBT(new NBTTagCompound()));
                this.saveHandler.writeMapData(data.mapName, nbttagcompound, true);
                return true;
            }
            catch (Exception exception)
            {
                exception.printStackTrace();
            }
        }
        return false;
    }

    private void loadIdCounts()
    {
        try
        {
            this.idCounts.clear();

            if (this.saveHandler == null)
            {
                return;
            }

            NBTTagCompound nbttagcompound = this.saveHandler.readMapData("idcounts", false);

            if (nbttagcompound != null)
            {
                for (String s : nbttagcompound.getKeySet())
                {
                    NBTBase nbtbase = nbttagcompound.getTag(s);

                    if (nbtbase instanceof NBTTagShort)
                    {
                        NBTTagShort nbttagshort = (NBTTagShort)nbtbase;
                        short short1 = nbttagshort.getShort();
                        this.idCounts.put(s, Short.valueOf(short1));
                    }
                }
            }
        }
        catch (Exception exception)
        {
            exception.printStackTrace();
        }
    }

    public int getUniqueDataId(String key)
    {
        Short oshort = this.idCounts.get(key);

        if (oshort == null)
        {
            oshort = 0;
        }
        else
        {
            oshort = (short)(oshort.shortValue() + 1);
        }

        this.idCounts.put(key, oshort);

        if (this.saveHandler == null)
        {
            return oshort.shortValue();
        }
        else
        {
            try
            {
                NBTTagCompound nbttagcompound = new NBTTagCompound();
                for (String s : this.idCounts.keySet())
                {
                    nbttagcompound.setShort(s, this.idCounts.get(s).shortValue());
                }
                this.saveHandler.writeMapData("idcounts", nbttagcompound, false);
            }
            catch (Exception exception)
            {
                exception.printStackTrace();
            }

            return oshort.shortValue();
        }
    }
}
