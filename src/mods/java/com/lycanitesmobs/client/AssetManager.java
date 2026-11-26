package com.lycanitesmobs.client;

import com.lycanitesmobs.LycanitesMobs;
import com.lycanitesmobs.ObjectManager;
import com.lycanitesmobs.client.model.ModelEquipmentPart;
import com.lycanitesmobs.client.model.ModelItemBase;
import com.lycanitesmobs.client.model.ModelObjOld;
import com.lycanitesmobs.core.entity.BaseCreatureEntity;
import com.lycanitesmobs.core.info.CreatureInfo;
import com.lycanitesmobs.core.info.CreatureManager;
import com.lycanitesmobs.core.info.ModInfo;
import com.lycanitesmobs.core.info.projectile.ProjectileInfo;
import com.lycanitesmobs.core.info.projectile.ProjectileManager;
import com.lycanitesmobs.core.item.ItemBase;
import com.lycanitesmobs.core.item.equipment.ItemEquipmentPart;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.ModelBase;
import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraftforge.client.model.IModel;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.registries.ForgeRegistry;

public class AssetManager {
   public static Map<String, ResourceLocation> textures = new HashMap();
   public static Map<String, ResourceLocation[]> textureGroups = new HashMap();
   public static Map<String, SoundEvent> sounds = new HashMap();
   public static Map<String, ModelBase> models = new HashMap();
   public static Map<ProjectileInfo, ModelBase> projectileModels = new HashMap();
   public static Map<String, IModel> objModels = new HashMap();
   public static Map<String, ModelItemBase> itemModels = new HashMap();

   public AssetManager() {
   }

   public static void addTexture(String name, ModInfo modInfo, String path) {
      name = name.toLowerCase();
      textures.put(name, new ResourceLocation(modInfo.modid, path));
   }

   public static void addTextureGroup(String name, ModInfo modInfo, String[] paths) {
      name = name.toLowerCase();
      ResourceLocation[] textureGroup = new ResourceLocation[paths.length];

      for(int i = 0; i < paths.length; ++i) {
         textureGroup[i] = new ResourceLocation(modInfo.modid, paths[i]);
      }

      textureGroups.put(name, textureGroup);
   }

   public static void addSound(String name, ModInfo modInfo, String path) {
      if (!((ForgeRegistry)ForgeRegistries.SOUND_EVENTS).isLocked()) {
         name = name.toLowerCase();
         ResourceLocation resourceLocation = new ResourceLocation(modInfo.modid, path);
         SoundEvent soundEvent = new SoundEvent(resourceLocation);
         soundEvent.setRegistryName(resourceLocation);
         sounds.put(name, soundEvent);
         GameRegistry.findRegistry(SoundEvent.class).register(soundEvent);
      }
   }

   public static void addModel(String name, ModelBase model) {
      name = name.toLowerCase();
      models.put(name, model);
   }

   public static void addObjModel(String name, ModInfo modInfo, String path) {
      name = name.toLowerCase();
      objModels.put(name, ModelObjOld.loadModel(new ResourceLocation(modInfo.modid, "models/" + path + ".obj")));
   }

   public static void addItemModel(String name, ModelItemBase model) {
      name = name.toLowerCase();
      itemModels.put(name, model);
   }

   public static ResourceLocation getTexture(String name) {
      name = name.toLowerCase();
      return !textures.containsKey(name) ? null : (ResourceLocation)textures.get(name);
   }

   public static ResourceLocation[] getTextureGroup(String name) {
      name = name.toLowerCase();
      return !textureGroups.containsKey(name) ? null : (ResourceLocation[])textureGroups.get(name);
   }

   public static SoundEvent getSound(String name) {
      name = name.toLowerCase();
      return !sounds.containsKey(name) ? null : (SoundEvent)sounds.get(name);
   }

   public static ModelBase getModel(String name) {
      name = name.toLowerCase();
      CreatureInfo creatureInfo = CreatureManager.getInstance().getCreatureFromId(name);
      if (creatureInfo != null) {
         ;
      }

      return !models.containsKey(name) ? null : (ModelBase)models.get(name);
   }

   public static ModelBase getCreatureModel(BaseCreatureEntity entityCreature) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException {
      if (entityCreature.creatureInfo == null) {
         return null;
      } else if (entityCreature.getSubspecies() != null && entityCreature.getSubspecies().modelClass != null) {
         if (models.containsKey(entityCreature.getSubspecies().modelClass.toString())) {
            return (ModelBase)models.get(entityCreature.getSubspecies().modelClass.toString());
         } else {
            ModelBase creatureModel = LycanitesModels.create(entityCreature.getSubspecies().modelClass);
            models.put(entityCreature.getSubspecies().modelClass.toString(), creatureModel);
            return creatureModel;
         }
      } else {
         return getCreatureModel(entityCreature.creatureInfo);
      }
   }

   public static ModelBase getCreatureModel(CreatureInfo creatureInfo) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException {
      if (creatureInfo != null && creatureInfo.modelClass != null) {
         if (models.containsKey(creatureInfo.modelClass.toString())) {
            return (ModelBase)models.get(creatureInfo.modelClass.toString());
         } else {
            ModelBase creatureModel = LycanitesModels.create(creatureInfo.modelClass);
            models.put(creatureInfo.modelClass.toString(), creatureModel);
            return creatureModel;
         }
      } else {
         return null;
      }
   }

   public static ModelBase getProjectileModel(ProjectileInfo projectileInfo) {
      return projectileModels.containsKey(projectileInfo) ? (ModelBase)projectileModels.get(projectileInfo) : null;
   }

   public static IModel getObjModel(String name) {
      name = name.toLowerCase();
      return !objModels.containsKey(name) ? null : (IModel)objModels.get(name);
   }

   public static IModel getObjModel(String name, ModInfo modInfo, String path) {
      name = name.toLowerCase();
      if (!objModels.containsKey(name)) {
         addObjModel(name, modInfo, path);
      }

      return (IModel)objModels.get(name);
   }

   public static ModelItemBase getItemModel(String name) {
      name = name.toLowerCase();
      return !itemModels.containsKey(name) ? null : (ModelItemBase)itemModels.get(name);
   }

   @SideOnly(Side.CLIENT)
   public static void registerModels() {
      for(Item item : ObjectManager.items.values()) {
         if (item instanceof ItemBase) {
            ItemBase itemBase = (ItemBase)item;
            if (itemBase.useItemColors()) {
               Minecraft.getMinecraft().getItemColors().registerItemColorHandler(ClientProxy.itemColor, new Item[]{item});
            }
         }

         if (item instanceof ItemEquipmentPart) {
            ItemEquipmentPart itemEquipmentPart = (ItemEquipmentPart)item;
            addItemModel(itemEquipmentPart.itemName, new ModelEquipmentPart(itemEquipmentPart.itemName, itemEquipmentPart.modInfo));
         }
      }

      for(ProjectileInfo projectileInfo : ProjectileManager.getInstance().projectiles.values()) {
         if (projectileInfo.modelClassName != null) {
            try {
               projectileModels.put(projectileInfo, LycanitesModels.create(LycanitesModels.forName(projectileInfo.modelClassName)));
            } catch (Exception var3) {
               LycanitesMobs.logWarning("", "Unable to load a Projectile model, check that the model class name is correct in the associated projectile json.");
               var3.printStackTrace();
            }
         }
      }

   }
}
