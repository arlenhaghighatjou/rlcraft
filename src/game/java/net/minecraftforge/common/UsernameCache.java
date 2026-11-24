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

package net.minecraftforge.common;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.Map;
import net.lax1dude.eaglercraft.v1_8.EaglercraftUUID;
import net.lax1dude.eaglercraft.v1_8.internal.vfs2.VFile2;

import javax.annotation.Nullable;

import net.lax1dude.eaglercraft.v1_8.log4j.LogManager;
import net.lax1dude.eaglercraft.v1_8.log4j.Logger;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonParseException;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

/**
 * Caches player's last known usernames
 * <p>
 * Modders should use {@link #getLastKnownUsername(EaglercraftUUID)} to determine a players
 * last known username.<br>
 * For convenience, {@link #getMap()} is provided to get an immutable copy of
 * the caches underlying map.
 */
public final class UsernameCache {

    private static Map<EaglercraftUUID, String> map = Maps.newHashMap();

    private static final VFile2 saveFile = new VFile2("usernamecache.json");
    private static final Gson gson = new GsonBuilder().setPrettyPrinting().create();

    private static final Logger log = LogManager.getLogger(ForgeVersion.MOD_ID + ".UsernameCache");

    private UsernameCache() {}

    /**
     * Set a player's current username
     *
     * @param uuid
     *            the player's {@link net.lax1dude.eaglercraft.v1_8.EaglercraftUUID EaglercraftUUID}
     * @param username
     *            the player's username
     */
    protected static synchronized void setUsername(EaglercraftUUID uuid, String username)
    {
        checkNotNull(uuid);
        checkNotNull(username);

        if (username.equals(map.get(uuid))) return;

        map.put(uuid, username);
        save();
    }

    /**
     * Remove a player's username from the cache
     *
     * @param uuid
     *            the player's {@link net.lax1dude.eaglercraft.v1_8.EaglercraftUUID EaglercraftUUID}
     * @return if the cache contained the user
     */
    protected static synchronized boolean removeUsername(EaglercraftUUID uuid)
    {
        checkNotNull(uuid);

        if (map.remove(uuid) != null)
        {
            save();
            return true;
        }

        return false;
    }

    /**
     * Get the player's last known username
     * <p>
     * <b>May be <code>null</code></b>
     *
     * @param uuid
     *            the player's {@link net.lax1dude.eaglercraft.v1_8.EaglercraftUUID EaglercraftUUID}
     * @return the player's last known username, or <code>null</code> if the
     *         cache doesn't have a record of the last username
     */
    @Nullable
    public static synchronized String getLastKnownUsername(EaglercraftUUID uuid)
    {
        checkNotNull(uuid);
        return map.get(uuid);
    }

    /**
     * Check if the cache contains the given player's username
     *
     * @param uuid
     *            the player's {@link net.lax1dude.eaglercraft.v1_8.EaglercraftUUID EaglercraftUUID}
     * @return if the cache contains a username for the given player
     */
    public static synchronized boolean containsUUID(EaglercraftUUID uuid)
    {
        checkNotNull(uuid);
        return map.containsKey(uuid);
    }

    /**
     * Get an immutable copy of the cache's underlying map
     *
     * @return the map
     */
    public static synchronized Map<EaglercraftUUID, String> getMap()
    {
        return ImmutableMap.copyOf(map);
    }

    /**
     * Save the cache to file
     */
    protected static synchronized void save()
    {
        JsonObject usernames = new JsonObject();
        for (Map.Entry<EaglercraftUUID, String> entry : map.entrySet())
        {
            usernames.addProperty(entry.getKey().toString(), entry.getValue());
        }
        saveFile.setAllChars(gson.toJson(usernames));
    }

    /**
     * Load the cache from file
     */
    protected static synchronized void load()
    {
        if (!saveFile.exists()) return;

        try
        {
            Map<EaglercraftUUID, String> loaded = Maps.newHashMap();
            JsonObject object = JsonParser.parseString(saveFile.getAllChars()).getAsJsonObject();
            for (Map.Entry<String, JsonElement> entry : object.entrySet())
            {
                JsonElement username = entry.getValue();
                if (username.isJsonPrimitive() && username.getAsJsonPrimitive().isString())
                {
                    loaded.put(EaglercraftUUID.fromString(entry.getKey()), username.getAsString());
                }
            }
            map = loaded;
        }
        catch (JsonParseException | IllegalArgumentException | IllegalStateException e)
        {
            log.error("Could not parse username cache file", e);
        }
    }
}
