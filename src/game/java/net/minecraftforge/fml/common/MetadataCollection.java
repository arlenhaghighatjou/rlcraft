/*
 * Minecraft Forge
 * Copyright (c) 2016-2020.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation version 2.1
 * of the License.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301  USA
 */

package net.minecraftforge.fml.common;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.Collection;

import net.minecraftforge.fml.common.versioning.ArtifactVersion;
import net.minecraftforge.fml.common.versioning.VersionParser;
import net.minecraft.util.JsonUtils;

import com.google.common.collect.Maps;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonParser;
import com.google.gson.JsonObject;

import javax.annotation.Nullable;

public class MetadataCollection
{
    private String modListVersion;
    private ModMetadata[] modList;
    private Map<String, ModMetadata> metadatas = Maps.newHashMap();
    public static MetadataCollection from(@Nullable InputStream inputStream, String sourceName)
    {
        if (inputStream == null)
        {
            return new MetadataCollection();
        }

        InputStreamReader reader = new InputStreamReader(inputStream, StandardCharsets.UTF_8);
        try
        {
            MetadataCollection collection = new MetadataCollection();
            JsonElement rootElement = new JsonParser().parse(reader);
            JsonArray jsonList;
            if (rootElement.isJsonArray())
            {
                jsonList = rootElement.getAsJsonArray();
            }
            else
            {
                JsonObject root = rootElement.getAsJsonObject();
                collection.modListVersion = JsonUtils.getString(root, "modListVersion", null);
                jsonList = JsonUtils.getJsonArray(root, "modList");
            }
            collection.modList = new ModMetadata[jsonList.size()];
            for (int i = 0, l = jsonList.size(); i < l; ++i)
            {
                collection.modList[i] = readMetadata(jsonList.get(i).getAsJsonObject());
            }
            collection.parseModMetadataList();
            return collection;
        }
        catch (JsonParseException e)
        {
            FMLLog.log.error("The mcmod.info file in {} cannot be parsed as valid JSON. It will be ignored", sourceName, e);
            return new MetadataCollection();
        }
    }

    private static ModMetadata readMetadata(JsonObject json)
    {
        ModMetadata meta = new ModMetadata();
        meta.modId = JsonUtils.getString(json, "modid", null);
        meta.name = JsonUtils.getString(json, "name", null);
        meta.description = JsonUtils.getString(json, "description", meta.description);
        meta.url = JsonUtils.getString(json, "url", meta.url);
        meta.updateUrl = JsonUtils.getString(json, "updateUrl", meta.updateUrl);
        meta.updateJSON = JsonUtils.getString(json, "updateJSON", meta.updateJSON);
        meta.logoFile = JsonUtils.getString(json, "logoFile", meta.logoFile);
        meta.version = JsonUtils.getString(json, "version", meta.version);
        meta.credits = JsonUtils.getString(json, "credits", meta.credits);
        meta.parent = JsonUtils.getString(json, "parent", meta.parent);
        meta.useDependencyInformation = JsonUtils.getBoolean(json, "useDependencyInformation", false);
        if (json.has("authorList"))
        {
            JsonArray arr = JsonUtils.getJsonArray(json, "authorList");
            for (int i = 0, l = arr.size(); i < l; ++i)
            {
                meta.authorList.add(arr.get(i).getAsString());
            }
        }
        if (json.has("screenshots"))
        {
            JsonArray arr = JsonUtils.getJsonArray(json, "screenshots");
            meta.screenshots = new String[arr.size()];
            for (int i = 0; i < meta.screenshots.length; ++i)
            {
                meta.screenshots[i] = arr.get(i).getAsString();
            }
        }
        readVersions(json, "requiredMods", meta.requiredMods);
        readVersions(json, "dependencies", meta.dependencies);
        readVersions(json, "dependants", meta.dependants);
        return meta;
    }

    private static void readVersions(JsonObject json, String key, Collection<ArtifactVersion> out)
    {
        if (json.has(key))
        {
            JsonArray arr = JsonUtils.getJsonArray(json, key);
            for (int i = 0, l = arr.size(); i < l; ++i)
            {
                out.add(VersionParser.parseVersionReference(arr.get(i).getAsString()));
            }
        }
    }

    private void parseModMetadataList()
    {
        for (ModMetadata modMetadata : modList)
        {
            metadatas.put(modMetadata.modId, modMetadata);
        }
    }

    public ModMetadata getMetadataForId(String modId, Map<String, Object> extraData)
    {
        if (!metadatas.containsKey(modId))
        {
            ModMetadata dummy = new ModMetadata();
            dummy.modId = modId;
            dummy.name = (String) extraData.get("name");
            dummy.version = (String) extraData.get("version");
            dummy.autogenerated = true;
            metadatas.put(modId, dummy);
        }
        return metadatas.get(modId);
    }
}
