package net.minecraft.world.storage.loot;

import com.arlenh7.eaglercraft.forge.WorldTextFile;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import net.lax1dude.eaglercraft.v1_8.cache.EaglerLoadingCache;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonParseException;
import java.io.File;
import java.io.IOException;
import javax.annotation.Nullable;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.storage.loot.conditions.LootCondition;
import net.minecraft.world.storage.loot.conditions.LootConditionManager;
import net.minecraft.world.storage.loot.functions.LootFunction;
import net.minecraft.world.storage.loot.functions.LootFunctionManager;
import net.lax1dude.eaglercraft.v1_8.log4j.LogManager;
import net.lax1dude.eaglercraft.v1_8.log4j.Logger;

public class LootTableManager
{
    private static final Logger LOGGER = LogManager.getLogger();
    private static final Gson GSON_INSTANCE = (new GsonBuilder()).registerTypeAdapter(RandomValueRange.class, new RandomValueRange.Serializer()).registerTypeAdapter(LootPool.class, new LootPool.Serializer()).registerTypeAdapter(LootTable.class, new LootTable.Serializer()).registerTypeHierarchyAdapter(LootEntry.class, new LootEntry.Serializer()).registerTypeHierarchyAdapter(LootFunction.class, new LootFunctionManager.Serializer()).registerTypeHierarchyAdapter(LootCondition.class, new LootConditionManager.Serializer()).registerTypeHierarchyAdapter(LootContext.EntityTarget.class, new LootContext.EntityTarget.Serializer()).create();
    private final EaglerLoadingCache<ResourceLocation, LootTable> registeredLootTables = CacheBuilder.newBuilder().<ResourceLocation, LootTable>build(new LootTableManager.Loader());
    private final WorldTextFile baseFolder;

    public LootTableManager(WorldTextFile folder)
    {
        this.baseFolder = folder;
        this.reloadLootTables();
    }

    public LootTable getLootTableFromLocation(ResourceLocation ressources)
    {
        return this.registeredLootTables.getUnchecked(ressources);
    }

    public void reloadLootTables()
    {
        this.registeredLootTables.invalidateAll();

        for (ResourceLocation resourcelocation : LootTableList.getAll())
        {
            this.getLootTableFromLocation(resourcelocation);
        }
    }

    class Loader extends CacheLoader<ResourceLocation, LootTable>
    {
        private Loader()
        {
        }

        public LootTable load(ResourceLocation p_load_1_) throws Exception
        {
            if (p_load_1_.getResourcePath().contains("."))
            {
                LootTableManager.LOGGER.debug("Invalid loot table name '{}' (can't contain periods)", (Object)p_load_1_);
                return LootTable.EMPTY_LOOT_TABLE;
            }
            else
            {
                LootTable loottable = this.loadLootTable(p_load_1_);

                if (loottable == null)
                {
                    loottable = this.loadBuiltinLootTable(p_load_1_);
                }

                if (loottable == null)
                {
                    loottable = LootTable.EMPTY_LOOT_TABLE;
                    LootTableManager.LOGGER.warn("Couldn't find resource table {}", (Object)p_load_1_);
                }

                return loottable;
            }
        }

        @Nullable
        private LootTable loadLootTable(ResourceLocation resource)
        {
			if (LootTableManager.this.baseFolder == null) {
				return null;
			}
			WorldTextFile file = LootTableManager.this.baseFolder
					.resolve(resource.getResourceDomain() + "/" + resource.getResourcePath() + ".json");
			if (!file.isFile()) {
				return null;
			}
			try {
				return net.minecraftforge.common.ForgeHooks.loadLootTable(
						GSON_INSTANCE, resource, file.read(), true, LootTableManager.this);
			} catch (IllegalArgumentException | JsonParseException | IOException ex) {
				LOGGER.error("Couldn't load loot table " + resource + " from " + file, ex);
				return LootTable.EMPTY_LOOT_TABLE;
			}
		}

        @Nullable
        private LootTable loadBuiltinLootTable(ResourceLocation resource)
        {
			String path = "/assets/" + resource.getResourceDomain() + "/loot_tables/"
					+ resource.getResourcePath() + ".json";
			String json = net.lax1dude.eaglercraft.v1_8.EagRuntime.getResourceString(path);
			if (json == null) {
				return null;
			}
			try {
				return net.minecraftforge.common.ForgeHooks.loadLootTable(
						GSON_INSTANCE, resource, json, false, LootTableManager.this);
			} catch (IllegalArgumentException | JsonParseException ex) {
				LOGGER.error("Couldn't load loot table " + resource + " from " + path, ex);
				return LootTable.EMPTY_LOOT_TABLE;
			}
        }
    }
}
