package com.fantasticsource.mctools.event;

import com.fantasticsource.mctools.GlobalInventory;
import com.fantasticsource.mctools.MCTools;
import com.fantasticsource.mctools.items.ItemMatcher;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.profiler.Profiler;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.WorldServer;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.event.entity.EntityEvent;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.Phase;
import net.minecraftforge.fml.common.gameevent.TickEvent.ServerTickEvent;
import net.minecraftforge.fml.relauncher.Side;

public class InventoryChangedEvent extends EntityEvent {
   public static HashSet<Class<? extends Entity>> watchedClasses = new HashSet();
   public static HashMap<Entity, InventoryChangedEvent.GlobalInventoryData> previousContents = new LinkedHashMap();
   public final InventoryChangedEvent.GlobalInventoryData oldInventory;
   public final InventoryChangedEvent.GlobalInventoryData newInventory;

   public InventoryChangedEvent(Entity entity, InventoryChangedEvent.GlobalInventoryData oldInventory, InventoryChangedEvent.GlobalInventoryData newInventory) {
      super(entity);
      this.oldInventory = oldInventory;
      this.newInventory = newInventory;
   }

   @SubscribeEvent
   public static void serverTick(ServerTickEvent event) {
      if (event.side != Side.CLIENT && event.phase != Phase.START && watchedClasses.size() != 0) {
         MinecraftServer server = FMLCommonHandler.instance().getMinecraftServerInstance();
         Profiler profiler = server.profiler;
         profiler.startSection("Fantastic Lib: InventoryChangedEvent");
         HashMap<Entity, InventoryChangedEvent.GlobalInventoryData> newContents = new LinkedHashMap();
         ArrayList<InventoryChangedEvent> events = new ArrayList();
         ObjectIterator var8 = MCTools.DIMENSION_MANAGER_WORLDS.values().iterator();

         while(var8.hasNext()) {
            WorldServer world = (WorldServer)var8.next();

            for(Entity entity : (Entity[])world.loadedEntityList.toArray(new Entity[0])) {
               if (!entity.isDead) {
                  boolean found = false;

                  for(Class<? extends Entity> cls : watchedClasses) {
                     if (cls.isAssignableFrom(entity.getClass())) {
                        found = true;
                        break;
                     }
                  }

                  if (found) {
                     InventoryChangedEvent.GlobalInventoryData oldInventory = (InventoryChangedEvent.GlobalInventoryData)previousContents.get(entity);
                     InventoryChangedEvent.GlobalInventoryData newInventory = new InventoryChangedEvent.GlobalInventoryData(entity);
                     if (oldInventory != null && oldInventory.equals(newInventory)) {
                        newContents.put(entity, oldInventory);
                     } else {
                        events.add(new InventoryChangedEvent(entity, oldInventory, newInventory));
                        newContents.put(entity, newInventory.deepCopy());
                     }
                  }
               }
            }
         }

         profiler.endStartSection("Fantastic Lib: InventoryChangedEvent listeners");

         for(InventoryChangedEvent event1 : events) {
            MinecraftForge.EVENT_BUS.post(event1);
         }

         profiler.endStartSection("Fantastic Lib: InventoryChangedEvent map replacement");
         previousContents = newContents;
         profiler.endSection();
      }
   }

   static {
      MinecraftForge.EVENT_BUS.register(InventoryChangedEvent.class);
   }

   public static class GlobalInventoryData implements INBTSerializable<NBTTagCompound> {
      public final ItemStack[] allNonSkin;
      public final ItemStack[] tiamatInventory;

      public GlobalInventoryData(Entity entity) {
         this.allNonSkin = (ItemStack[])GlobalInventory.getAllNonSkinItems(entity).toArray(new ItemStack[0]);
         this.tiamatInventory = (ItemStack[])GlobalInventory.getAllTiamatItems(entity).toArray(new ItemStack[0]);
      }

      public InventoryChangedEvent.GlobalInventoryData deepCopy() {
         int i = 0;

         for(ItemStack stack : this.allNonSkin) {
            this.allNonSkin[i++] = stack.copy();
         }

         i = 0;

         for(ItemStack stack : this.tiamatInventory) {
            this.tiamatInventory[i++] = stack.copy();
         }

         return this;
      }

      public boolean equals(Object obj) {
         if (!(obj instanceof InventoryChangedEvent.GlobalInventoryData)) {
            return false;
         } else {
            InventoryChangedEvent.GlobalInventoryData other = (InventoryChangedEvent.GlobalInventoryData)obj;
            return this.allNonSkin.length != other.allNonSkin.length ? false : ItemMatcher.stacksMatch(this.allNonSkin, other.allNonSkin);
         }
      }

      public NBTTagCompound serializeNBT() {
         new NBTTagCompound();
         return null;
      }

      public void deserializeNBT(NBTTagCompound nbt) {
      }
   }
}
