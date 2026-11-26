package net.minecraft.advancements;

import com.arlenh7.eaglercraft.forge.WorldTextFile;
import com.google.common.collect.Maps;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import java.io.File;
import java.io.IOException;
import java.io.Reader;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import javax.annotation.Nullable;
import net.minecraft.util.EnumTypeAdapterFactory;
import net.minecraft.util.JsonUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.Style;
import net.lax1dude.eaglercraft.v1_8.log4j.LogManager;
import net.lax1dude.eaglercraft.v1_8.log4j.Logger;

public class AdvancementManager
{
    private static final Logger LOGGER = LogManager.getLogger();
    public static final Gson GSON = (new GsonBuilder()).registerTypeHierarchyAdapter(Advancement.Builder.class, new JsonDeserializer<Advancement.Builder>()
    {
        public Advancement.Builder deserialize(JsonElement p_deserialize_1_, Type p_deserialize_2_, JsonDeserializationContext p_deserialize_3_) throws JsonParseException
        {
            JsonObject jsonobject = JsonUtils.getJsonObject(p_deserialize_1_, "advancement");
            return Advancement.Builder.deserialize(jsonobject, p_deserialize_3_);
        }
    }).registerTypeAdapter(AdvancementRewards.class, new AdvancementRewards.Deserializer()).registerTypeHierarchyAdapter(ITextComponent.class, new ITextComponent.Serializer()).registerTypeHierarchyAdapter(Style.class, new Style.Serializer()).registerTypeAdapterFactory(new EnumTypeAdapterFactory()).create();
    public static final AdvancementList ADVANCEMENT_LIST = new AdvancementList();
    private final WorldTextFile advancementsDir;
    private boolean hasErrored;

    public AdvancementManager(WorldTextFile advancementsDirIn)
    {
        this.advancementsDir = advancementsDirIn;
        this.reload();
    }

    public void reload()
    {
        this.hasErrored = false;
        ADVANCEMENT_LIST.clear();
        Map<ResourceLocation, Advancement.Builder> map = this.loadCustomAdvancements();
        this.loadBuiltInAdvancements(map);
        this.hasErrored |= net.minecraftforge.common.ForgeHooks.loadAdvancements(map);
        ADVANCEMENT_LIST.loadAdvancements(map);

        for (Advancement advancement : ADVANCEMENT_LIST.getRoots())
        {
            if (advancement.getDisplay() != null)
            {
                AdvancementTreeNode.layout(advancement);
            }
        }
    }

    public boolean hasErrored()
    {
        return this.hasErrored;
    }

	private Map<ResourceLocation, Advancement.Builder> loadCustomAdvancements() {
		Map<ResourceLocation, Advancement.Builder> advancements = Maps.newHashMap();
		if (this.advancementsDir == null) {
			return advancements;
		}
		for (String path : this.advancementsDir.list(".json")) {
			int namespace = path.indexOf('/');
			if (namespace <= 0) {
				continue;
			}
			try {
				ResourceLocation id = new ResourceLocation(path.substring(0, namespace),
						path.substring(namespace + 1, path.length() - 5));
				Advancement.Builder advancement = JsonUtils.gsonDeserialize(GSON,
						this.advancementsDir.resolve(path).read(), Advancement.Builder.class);
				if (advancement == null) {
					LOGGER.error("Empty custom advancement " + path);
					this.hasErrored = true;
				} else {
					advancements.put(id, advancement);
				}
			} catch (IllegalArgumentException | JsonParseException | IOException ex) {
				LOGGER.error("Could not load custom advancement " + path, ex);
				this.hasErrored = true;
			}
		}
		return advancements;
	}

	private void loadBuiltInAdvancements(Map<ResourceLocation, Advancement.Builder> advancements) {
		String directory = "assets/minecraft/advancements/";
		List<String> files = net.lax1dude.eaglercraft.v1_8.EagRuntime.listResources(directory);
		for (int i = 0, l = files.size(); i < l; ++i) {
			String file = files.get(i).substring(directory.length());
			if (!file.endsWith(".json") || file.startsWith("_")) {
				continue;
			}
			ResourceLocation id = new ResourceLocation("minecraft", file.substring(0, file.length() - 5));
			if (advancements.containsKey(id)) {
				continue;
			}
			try (Reader reader = new java.io.InputStreamReader(
					net.lax1dude.eaglercraft.v1_8.EagRuntime.getRequiredResourceStream(directory + file),
					StandardCharsets.UTF_8)) {
				Advancement.Builder advancement = JsonUtils.fromJson(GSON, reader, Advancement.Builder.class);
				advancements.put(id, advancement);
			} catch (AdvancementRewards.UnknownRecipeException ex) {
				if (!file.startsWith("recipes/")) {
					LOGGER.error("Could not load Minecraft advancement " + id, ex);
					this.hasErrored = true;
				}
			} catch (JsonParseException | IOException ex) {
				LOGGER.error("Could not load Minecraft advancement " + id, ex);
				this.hasErrored = true;
			}
		}
	}

    @Nullable
    public Advancement getAdvancement(ResourceLocation id)
    {
        return ADVANCEMENT_LIST.getAdvancement(id);
    }

    public Iterable<Advancement> getAdvancements()
    {
        return ADVANCEMENT_LIST.getAdvancements();
    }
}
