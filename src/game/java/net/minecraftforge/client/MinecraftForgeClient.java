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

package net.minecraftforge.client;

import java.io.IOException;
import java.util.BitSet;
import java.util.HashMap;
import java.util.Locale;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

import javax.annotation.Nonnull;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.TextureUtil;
import net.minecraft.client.resources.IResource;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.ChunkCache;
import net.minecraft.world.World;

import org.apache.commons.lang3.tuple.Pair;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import net.lax1dude.eaglercraft.v1_8.cache.EaglerLoadingCache;
import net.lax1dude.eaglercraft.v1_8.opengl.ImageData;

public class MinecraftForgeClient
{
    public static int getRenderPass()
    {
        return ForgeHooksClient.renderPass;
    }

    public static BlockRenderLayer getRenderLayer()
    {
        return ForgeHooksClient.renderLayer.get();
    }

    /**
     * returns the Locale set by the player in Minecraft.
     * Useful for creating string and number formatters.
     */
    public static Locale getLocale()
    {
        return Minecraft.getMinecraft().getLanguageManager().getCurrentLanguage().getJavaLocale();
    }

    private static final int STENCIL_BIT_COUNT = 8;
    private static final BitSet stencilBits = new BitSet(STENCIL_BIT_COUNT);
    static
    {
        stencilBits.set(0, STENCIL_BIT_COUNT);
    }

    /**
     * Reserve a stencil bit for use in rendering
     *
     * Note: you must check the Framebuffer you are working with to
     * determine if stencil bits are enabled on it before use.
     *
     * @return A bit or -1 if no further stencil bits are available
     */
    public static int reserveStencilBit()
    {
        int bit = stencilBits.nextSetBit(0);
        if (bit >= 0)
        {
            stencilBits.clear(bit);
        }
        return bit;
    }

    /**
     * Release the stencil bit for other use
     *
     * @param bit The bit from {@link #reserveStencilBit()}
     */
    public static void releaseStencilBit(int bit)
    {
        if (bit >= 0 && bit < STENCIL_BIT_COUNT)
        {
            stencilBits.set(bit);
        }
    }

    private static final EaglerLoadingCache<Pair<World, BlockPos>, ChunkCache> regionCache = CacheBuilder.newBuilder()
        .maximumSize(500)
        .expireAfterAccess(1, TimeUnit.SECONDS)
        .build(new CacheLoader<Pair<World, BlockPos>, ChunkCache>()
        {
            @Override
            public ChunkCache load(Pair<World, BlockPos> key)
            {
                return new ChunkCache(key.getLeft(), key.getRight().add(-1, -1, -1), key.getRight().add(16, 16, 16), 1);
            }
        });

    public static void onRebuildChunk(World world, BlockPos position, ChunkCache cache)
    {
        regionCache.put(Pair.of(world, position), cache);
    }

    public static ChunkCache getRegionRenderCache(World world, BlockPos pos)
    {
        int x = pos.getX() & ~0xF;
        int y = pos.getY() & ~0xF;
        int z = pos.getZ() & ~0xF;
        return regionCache.getUnchecked(Pair.of(world, new BlockPos(x, y, z)));
    }

    public static void clearRenderCache()
    {
        regionCache.invalidateAll();
        regionCache.cleanUp();
    }

    private static final HashMap<ResourceLocation, Supplier<ImageData>> imageSuppliers = new HashMap<>();
    public static void registerImageLayerSupplier(ResourceLocation resourceLocation, Supplier<ImageData> supplier)
    {
        imageSuppliers.put(resourceLocation, supplier);
    }

    @Nonnull
    public static ImageData getImageLayer(ResourceLocation resourceLocation, IResourceManager resourceManager) throws IOException
    {
        Supplier<ImageData> supplier = imageSuppliers.get(resourceLocation);
        if (supplier != null)
            return supplier.get();

        try (IResource iresource1 = resourceManager.getResource(resourceLocation))
        {
            return TextureUtil.readBufferedImage(iresource1.getInputStream());
        }
    }
}
