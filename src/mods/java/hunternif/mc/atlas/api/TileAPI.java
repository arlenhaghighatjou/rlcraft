package hunternif.mc.atlas.api;

import hunternif.mc.atlas.client.TextureSet;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public interface TileAPI {
   @SideOnly(Side.CLIENT)
   TextureSet registerTextureSet(String var1, ResourceLocation... var2);

   @SideOnly(Side.CLIENT)
   void registerTextureSet(TextureSet var1);

   @SideOnly(Side.CLIENT)
   void setBiomeTexture(int var1, String var2, ResourceLocation... var3);

   @SideOnly(Side.CLIENT)
   void setBiomeTexture(Biome var1, String var2, ResourceLocation... var3);

   @SideOnly(Side.CLIENT)
   void setBiomeTexture(int var1, TextureSet var2);

   @SideOnly(Side.CLIENT)
   void setBiomeTexture(Biome var1, TextureSet var2);

   @SideOnly(Side.CLIENT)
   void setCustomTileTexture(String var1, ResourceLocation... var2);

   @SideOnly(Side.CLIENT)
   void setCustomTileTexture(String var1, TextureSet var2);

   void putBiomeTile(World var1, int var2, int var3, int var4, int var5);

   void putBiomeTile(World var1, int var2, Biome var3, int var4, int var5);

   void putCustomTile(World var1, int var2, String var3, int var4, int var5);

   void putCustomGlobalTile(World var1, String var2, int var3, int var4);

   void deleteCustomGlobalTile(World var1, int var2, int var3);
}
