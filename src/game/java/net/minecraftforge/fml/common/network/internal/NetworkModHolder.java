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

package net.minecraftforge.fml.common.network.internal;

import java.util.Map;
import java.util.Set;
import java.util.function.BiPredicate;

import net.minecraftforge.fml.common.FMLLog;
import net.minecraftforge.fml.common.ModContainer;
import net.minecraftforge.fml.common.versioning.DefaultArtifactVersion;
import net.minecraftforge.fml.common.versioning.InvalidVersionSpecificationException;
import net.minecraftforge.fml.common.versioning.VersionRange;
import net.minecraftforge.fml.relauncher.Side;

import com.google.common.base.Preconditions;
import com.google.common.base.Strings;
import com.google.common.collect.ImmutableMap;

import javax.annotation.Nullable;

public class NetworkModHolder
{
    /**
     * Validates that the mods versions on the client and server are compatible with mod.
     */
    public abstract class NetworkChecker {
        /**
         * @deprecated use {@link #checkCompatible(Map, Side)}
         */
        @Deprecated // TODO remove in 1.13
        public abstract boolean check(Map<String,String> remoteVersions, Side side);

        /**
         * @param remoteVersions map of modIds to version strings, represents all the mods on the given side
         * @param side the side that the remoteVersions are from
         * @return null if these mod versions are compatible with this mod,
         *         an error string reason if these mod versions are not compatible with this mod.
         */
        @Nullable
        public String checkCompatible(Map<String,String> remoteVersions, Side side)
        {
            return check(remoteVersions, side) ? toString() : null;
        }
    }

    private class IgnoredChecker extends NetworkChecker {
        @Override
        public boolean check(Map<String, String> remoteVersions, Side side)
        {
            return true;
        }

        @Nullable
        @Override
        public String checkCompatible(Map<String, String> remoteVersions, Side side)
        {
            return null;
        }

        @Override
        public String toString()
        {
            return "No network checking performed";
        }
    }
    private class DefaultNetworkChecker extends NetworkChecker {
        @Override
        public boolean check(Map<String,String> remoteVersions, Side side)
        {
            return checkCompatible(remoteVersions, side) == null;
        }

        @Nullable
        @Override
        public String checkCompatible(Map<String, String> remoteVersions, Side side)
        {
            String version = remoteVersions.get(container.getModId());
            if (version != null && acceptVersion(version))
            {
                return null;
            }
            if (side == Side.SERVER)
            {
                return null;
            }
            String versionString;
            if (acceptableRange != null)
            {
                if (acceptableRange.hasRestrictions())
                {
                    versionString = String.format("version %s", acceptableRange.toStringFriendly());
                }
                else
                {
                    versionString = String.format("version range %s", acceptableRange.toStringFriendly());
                }
            }
            else
            {
                versionString = String.format("version %s", container.getVersion());
            }
            if (version != null)
            {
                return String.format("Requires %s but client has %s.", versionString, version);
            }
            else
            {
                return String.format("Requires %s but mod is not found on client.", versionString);
            }
        }

        @Override
        public String toString()
        {
            return acceptableRange != null ? String.format("Accepting range %s", acceptableRange.toStringFriendly()) : String.format("Accepting version %s", container.getVersion());
        }
    }
    private class MethodNetworkChecker extends NetworkChecker {
        @Override
        public boolean check(Map<String,String> remoteVersions, Side side)
        {
            return checkCompatible(remoteVersions, side) == null;
        }

        @Nullable
        @Override
        public String checkCompatible(Map<String, String> remoteVersions, Side side)
        {
            try
            {
                if (checkHandler.test(remoteVersions, side))
                {
                    return null;
                }
                return String.format("Failed mod's custom NetworkCheckHandler %s", container);
            }
            catch (Exception e)
            {
                FMLLog.log.error("Error occurred invoking NetworkCheckHandler at {}", container, e);
                return String.format("Error occurred invoking NetworkCheckHandler at %s", container);
            }
        }

        @Override
        public String toString()
        {
            return "Invoking custom network check handler";
        }
    }
    private static int assignedIds = 1;

    private int localId;
    private int networkId;

    private ModContainer container;
    private BiPredicate<Map<String,String>, Side> checkHandler;

    private VersionRange acceptableRange;

    private NetworkChecker checker;

    private boolean acceptsVanillaClient;
    private boolean acceptsVanillaServer;

    public NetworkModHolder(ModContainer container)
    {
        this.container = container;
        this.localId = assignedIds++;
        this.networkId = this.localId;
    }
    public NetworkModHolder(ModContainer container, NetworkChecker checker)
    {
        this(container);
        this.checker = Preconditions.checkNotNull(checker);
        FMLLog.log.debug("The mod {} is using a custom checker {}", container.getModId(), checker.getClass().getName());
    }
    public NetworkModHolder(ModContainer container, BiPredicate<Map<String,String>, Side> checkHandler)
    {
        this(container);
        this.checkHandler = checkHandler;
        this.checker = new MethodNetworkChecker();
        verifySelf();
    }

    public NetworkModHolder(ModContainer container, @Nullable String acceptableVersionRange)
    {
        this(container);
        if (!Strings.isNullOrEmpty(acceptableVersionRange) && acceptableVersionRange.equals("*"))
        {
            this.checker = new IgnoredChecker();
        }
        else
        {
            try
            {
                this.acceptableRange = VersionRange.createFromVersionSpec(acceptableVersionRange);
            }
            catch (InvalidVersionSpecificationException e)
            {
                FMLLog.log.warn("Invalid bounded range {} specified for network mod id {}", acceptableVersionRange, container.getModId(), e);
            }
            this.checker = new DefaultNetworkChecker();
        }
        verifySelf();
    }

    private void verifySelf()
    {
        FMLLog.log.trace("Mod {} is using network checker : {}", container.getModId(), this.checker);
        FMLLog.log.trace("Testing mod {} to verify it accepts its own version in a remote connection", container.getModId());
        boolean acceptsSelf = acceptVersion(container.getVersion());
        if (!acceptsSelf)
        {
            FMLLog.log.fatal("The mod {} appears to reject its own version number ({}) in its version handling. This is likely a severe bug in the mod!", container.getModId(), container.getVersion());
        }
        else
        {
            FMLLog.log.trace("The mod {} accepts its own version ({})", container.getModId(), container.getVersion());
        }
    }

    public boolean acceptVersion(String version)
    {
        if (acceptableRange != null)
        {
            return acceptableRange.containsVersion(new DefaultArtifactVersion(version));
        }

        return container.getVersion().equals(version);
    }

    public boolean check(Map<String,String> data, Side side)
    {
        return checker.checkCompatible(data, side) == null;
    }

    @Nullable
    public String checkCompatible(Map<String,String> data, Side side)
    {
        return checker.checkCompatible(data, side);
    }

    public int getLocalId()
    {
        return localId;
    }

    public int getNetworkId()
    {
        return networkId;
    }

    public ModContainer getContainer()
    {
        return container;
    }

    public void setNetworkId(int value)
    {
        this.networkId = value;
    }

    public void testVanillaAcceptance() {
        acceptsVanillaClient = check(ImmutableMap.of(), Side.CLIENT);
        acceptsVanillaServer = check(ImmutableMap.of(), Side.SERVER);
    }
    public boolean acceptsVanilla(Side from) {
        return from == Side.CLIENT ? acceptsVanillaClient : acceptsVanillaServer;
    }
}
