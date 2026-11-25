package net.darkhax.surge;

import net.darkhax.surge.core.SurgeConfiguration;
import net.darkhax.surge.lib.DyeBlending;
import net.lax1dude.eaglercraft.v1_8.internal.vfs2.VFile2;
import net.lax1dude.eaglercraft.v1_8.log4j.LogManager;
import net.lax1dude.eaglercraft.v1_8.log4j.Logger;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.client.resources.SimpleReloadableResourceManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.passive.EntityAnimal;
import net.minecraft.entity.passive.EntitySheep;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagDouble;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLConstructionEvent;
import net.minecraftforge.fml.common.event.FMLEvent;

@Mod(
   modid = "surge",
   name = "Surge",
   version = "2.0.79",
   acceptableRemoteVersions = "*",
   acceptedMinecraftVersions = "[1.12.2]"
)
public class Surge {
   public static final Logger LOG = LogManager.getLogger("Surge");

   public Surge() {
   }

   public void handleModStateEvent(FMLEvent event) {
      if (event instanceof FMLConstructionEvent) {
         SurgeConfiguration.init(new VFile2(Loader.instance().getConfigDir(), "surge.cfg"));
      }

   }

   public static void writeBoundingBox(Entity entity, NBTTagCompound tag) {
      if (SurgeConfiguration.fixWallGlitch) {
         AxisAlignedBB bounds = entity.getEntityBoundingBox();
         if (bounds != null) {
            tag.setTag("SurgeAABB", newDoubleList(bounds.minX, bounds.minY, bounds.minZ, bounds.maxX, bounds.maxY, bounds.maxZ));
         }
      }

   }

   private static NBTTagList newDoubleList(double... numbers) {
      NBTTagList list = new NBTTagList();

      for(int i = 0; i < numbers.length; ++i) {
         list.appendTag(new NBTTagDouble(numbers[i]));
      }

      return list;
   }

   public static void readBoundingBox(Entity entity, NBTTagCompound tag) {
      if (SurgeConfiguration.fixWallGlitch && tag.hasKey("SurgeAABB")) {
         NBTTagList verticies = tag.getTagList("SurgeAABB", 6);
         entity.setEntityBoundingBox(new AxisAlignedBB(verticies.getDoubleAt(0), verticies.getDoubleAt(1), verticies.getDoubleAt(2), verticies.getDoubleAt(3), verticies.getDoubleAt(4), verticies.getDoubleAt(5)));
      }

   }

   public static boolean isFullStack(EntityItem item) {
      ItemStack stack = item.getItem();
      return stack.getCount() >= stack.getMaxStackSize();
   }

   public static EnumDyeColor blendSheepColors(EntityAnimal father, EntityAnimal mother) {
      if (SurgeConfiguration.sheepDyeBlendTable && father instanceof EntitySheep && mother instanceof EntitySheep) {
         EnumDyeColor fatherColor = ((EntitySheep)father).getFleeceColor();
         EnumDyeColor motherColor = ((EntitySheep)mother).getFleeceColor();
         EnumDyeColor blended = DyeBlending.getBlendedColor(fatherColor, motherColor);
         if (blended != null) {
            return blended;
         }

         return father.world.rand.nextBoolean() ? fatherColor : motherColor;
      }

      return null;
   }

   public static int getMaxRenameLength() {
      return SurgeConfiguration.maxRenameLength;
   }

   public static boolean skipAnimatedModel(IResourceManager manager, ResourceLocation location) {
      return SurgeConfiguration.disableAnimatedModels || SurgeConfiguration.checkForAnimatedModels && manager instanceof SimpleReloadableResourceManager && !((SimpleReloadableResourceManager)manager).hasResource(location);
   }

   public static boolean disableDebugSoundInfo() {
      return SurgeConfiguration.disableDebugSoundInfo;
   }
}
