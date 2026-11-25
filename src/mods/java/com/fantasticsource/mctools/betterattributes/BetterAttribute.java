package com.fantasticsource.mctools.betterattributes;

import com.fantasticsource.mctools.MCTools;
import com.fantasticsource.mctools.Network;
import com.fantasticsource.tools.ReflectionTool;
import com.fantasticsource.tools.Tools;
import com.fantasticsource.tools.datastructures.ExplicitPriorityQueue;
import com.fantasticsource.tools.datastructures.Pair;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.function.Predicate;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeMap;
import net.minecraft.entity.ai.attributes.IAttribute;
import net.minecraft.entity.ai.attributes.IAttributeInstance;
import net.minecraft.entity.ai.attributes.ModifiableAttributeInstance;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.entity.player.PlayerCapabilities;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.translation.I18n;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.Event;

public class BetterAttribute {
   public static final boolean DEBUG_MODS = false;
   public static final HashMap<String, BetterAttribute> BETTER_ATTRIBUTES = new HashMap();
   public static final Field MODIFIABLE_ATTRIBUTE_INSTANCE_NEEDS_UPDATE_FIELD = ReflectionTool.getField(ModifiableAttributeInstance.class, "needsUpdate", "needsUpdate");
   public static final Field PLAYER_CAPABILITIES_WALK_SPEED_FIELD = ReflectionTool.getField(PlayerCapabilities.class, "walkSpeed", "walkSpeed");
   public final String name;
   public final double defaultBaseAmount;
   public boolean isGood;
   public boolean canUseTotalAmountCaching;
   public boolean syncClientEntityDataToClient;
   public boolean syncOtherEntityDataToClient;
   public ArrayList<BetterAttributeMod> parentMods;
   public ArrayList<BetterAttribute> children;
   public IAttribute mcAttributeToSet;
   public double mcAttributeScalar;
   public ArrayList<Predicate<Pair<Entity, ArrayList<String>>>> displayValueArgumentEditors;

   public static void register(BetterAttribute betterAttribute) {
      String name = betterAttribute.name;

      for(BetterAttributeMod parentMod : betterAttribute.parentMods) {
         if (BETTER_ATTRIBUTES.get(parentMod.parentAttributeName) == null) {
            System.err.println(TextFormatting.RED + "COULD NOT REGISTER BETTER ATTRIBUTE, BECAUSE ONE OF ITS PARENTS IS NULL: " + name);
            return;
         }
      }

      if (BETTER_ATTRIBUTES.containsKey(name)) {
         System.err.println(TextFormatting.RED + "COULD NOT REGISTER BETTER ATTRIBUTE, BECAUSE IT ALREADY EXISTS: " + name);
      } else {
         BETTER_ATTRIBUTES.put(name, betterAttribute);
      }
   }

   public BetterAttribute(String name, BetterAttribute... parents) {
      this(name, 0.0D, parents);
   }

   public BetterAttribute(String name, double defaultBaseAmount, BetterAttribute... parents) {
      this(name, defaultBaseAmount, 0, parents);
   }

   public BetterAttribute(String name, double defaultBaseAmount, int parentsOperation, BetterAttribute... parents) {
      this(name, defaultBaseAmount, true, genParentMods(name, parentsOperation, parents));
   }

   public BetterAttribute(String name, double defaultBaseAmount, int parentsOperation, Pair<BetterAttribute, Double>... parents) {
      this(name, defaultBaseAmount, true, genParentMods(name, parentsOperation, parents));
   }

   public BetterAttribute(String name, double defaultBaseAmount, boolean ignored, BetterAttributeMod... parentMods) {
      this.isGood = true;
      this.canUseTotalAmountCaching = true;
      this.syncClientEntityDataToClient = true;
      this.syncOtherEntityDataToClient = false;
      this.parentMods = new ArrayList();
      this.children = new ArrayList();
      this.mcAttributeToSet = null;
      this.mcAttributeScalar = 1.0D;
      this.displayValueArgumentEditors = new ArrayList();
      this.name = name;
      this.defaultBaseAmount = defaultBaseAmount;
      this.parentMods.addAll(Arrays.asList(parentMods));

      for(BetterAttributeMod parentMod : parentMods) {
         ((BetterAttribute)BETTER_ATTRIBUTES.get(parentMod.parentAttributeName)).children.add(this);
      }

      register(this);
   }

   protected static BetterAttributeMod[] genParentMods(String attributeName, int operation, BetterAttribute... parents) {
      BetterAttributeMod[] mods = new BetterAttributeMod[parents.length];

      for(int i = 0; i < mods.length; ++i) {
         mods[i] = new BetterAttributeMod("Parent", attributeName, operation * 100, operation, 1.0D);
         mods[i].parentAttributeName = parents[i].name;
      }

      return mods;
   }

   protected static BetterAttributeMod[] genParentMods(String attributeName, int operation, Pair<BetterAttribute, Double>... parents) {
      BetterAttributeMod[] mods = new BetterAttributeMod[parents.length];

      for(int i = 0; i < mods.length; ++i) {
         mods[i] = new BetterAttributeMod("Parent", attributeName, operation * 100, operation, ((Double)parents[i].getValue()).doubleValue());
         mods[i].parentAttributeName = ((BetterAttribute)parents[i].getKey()).name;
      }

      return mods;
   }

   public final void setBaseAmount(Entity entity, double amount) {
      if (amount != this.getBaseAmount(entity)) {
         if (amount == this.defaultBaseAmount) {
            NBTTagCompound compound = MCTools.getSubCompoundIfExists(entity.getEntityData(), "fantasticlib", "baseAttributes");
            if (compound != null) {
               compound.removeTag(this.name);
            }
         } else {
            MCTools.getOrGenerateSubCompound(entity.getEntityData(), "fantasticlib", "baseAttributes").setDouble(this.name, amount);
         }

         this.calculateTotal(entity);
      }
   }

   public double getBaseAmount(Entity entity) {
      NBTTagCompound compound = MCTools.getSubCompoundIfExists(entity.getEntityData(), "fantasticlib", "baseAttributes");
      return compound != null && compound.hasKey(this.name) ? compound.getDouble(this.name) : this.defaultBaseAmount;
   }

   protected final double calculateTotal(Entity entity) {
      double result = this.getBaseAmount(entity);
      BetterAttribute.BetterAttributeCalcEvent event = new BetterAttribute.BetterAttributeCalcEvent(this, entity);
      MinecraftForge.EVENT_BUS.post(event);
      if (!event.functions.isEmpty()) {
         double[] d = new double[]{result, result};
         int oldTier = (int)event.functions.peekPriority();
         int newTier = 0;

         while(!event.functions.isEmpty() && ((Predicate)event.functions.poll()).test(d)) {
            newTier = (int)event.functions.peekPriority();
            if (newTier != oldTier) {
               d[1] = d[0];
               oldTier = newTier;
            }
         }

         result = d[0];
      }

      if (this.mcAttributeToSet != null && entity instanceof EntityLivingBase) {
         AttributeMap attributeMap = (AttributeMap)((EntityLivingBase)entity).getAttributeMap();
         IAttributeInstance attributeInstance = attributeMap.getAttributeInstance(this.mcAttributeToSet);
         if (attributeInstance != null) {
            double convertedAmount = result * this.mcAttributeScalar;
            attributeInstance.setBaseValue(convertedAmount);
            ReflectionTool.set(MODIFIABLE_ATTRIBUTE_INSTANCE_NEEDS_UPDATE_FIELD, attributeInstance, Boolean.valueOf(true));
            if (entity instanceof EntityPlayerMP && this.mcAttributeToSet == SharedMonsterAttributes.MOVEMENT_SPEED) {
               ReflectionTool.set(PLAYER_CAPABILITIES_WALK_SPEED_FIELD, ((EntityPlayer)entity).capabilities, Float.valueOf((float)convertedAmount));
            }
         }
      }

      if (this.canUseTotalAmountCaching) {
         NBTTagCompound compound = MCTools.getOrGenerateSubCompound(entity.getEntityData(), "fantasticlib", "attributes");
         compound.setDouble(this.name, result);
      }

      MinecraftForge.EVENT_BUS.post(new BetterAttribute.BetterAttributeChangedEvent(this, entity));
      this.sync(entity);

      for(BetterAttribute child : this.children) {
         if (child.canUseTotalAmountCaching) {
            child.calculateTotal(entity);
         }
      }

      return result;
   }

   public final double getTotalAmount(Entity entity) {
      if (this.canUseTotalAmountCaching) {
         NBTTagCompound compound = MCTools.getOrGenerateSubCompound(entity.getEntityData(), "fantasticlib", "attributes");
         if (compound.hasKey(this.name)) {
            return compound.getDouble(this.name);
         }
      }

      return this.calculateTotal(entity);
   }

   public void setCurrentAmount(Entity entity, double amount) {
      if (amount != this.getCurrentAmount(entity)) {
         MCTools.getOrGenerateSubCompound(entity.getEntityData(), "fantasticlib", "currentAttributes").setDouble(this.name, amount);
         if (this.mcAttributeToSet == SharedMonsterAttributes.MAX_HEALTH && entity instanceof EntityLivingBase) {
            ((EntityLivingBase)entity).setHealth((float)amount);
         }

         MinecraftForge.EVENT_BUS.post(new BetterAttribute.BetterAttributeChangedEvent(this, entity));
         this.sync(entity);
      }
   }

   public double getCurrentAmount(Entity entity) {
      if (this.mcAttributeToSet == SharedMonsterAttributes.MAX_HEALTH && entity instanceof EntityLivingBase) {
         return (double)((EntityLivingBase)entity).getHealth();
      } else {
         NBTTagCompound compound = MCTools.getSubCompoundIfExists(entity.getEntityData(), "fantasticlib", "currentAttributes");
         return compound != null && compound.hasKey(this.name) ? compound.getDouble(this.name) : this.getTotalAmount(entity);
      }
   }

   public final void sync(Entity entity) {
      if (!entity.world.isRemote) {
         if (this.syncOtherEntityDataToClient) {
            MCTools.sendToAllTracking(Network.WRAPPER, new Network.BetterAttributePacket(entity, this), entity);
         } else if (this.syncClientEntityDataToClient && entity instanceof EntityPlayerMP) {
            Network.WRAPPER.sendTo(new Network.BetterAttributePacket(entity, this), (EntityPlayerMP)entity);
         }
      }

   }

   public BetterAttribute setMCAttribute(IAttribute mcAttribute, double scalar) {
      this.mcAttributeToSet = mcAttribute;
      this.mcAttributeScalar = scalar;
      return this;
   }

   public BetterAttribute addDisplayValueArgumentEditor(Predicate<Pair<Entity, ArrayList<String>>> predicate) {
      this.displayValueArgumentEditors.add(predicate);
      return this;
   }

   public String getLocalizedName() {
      return I18n.translateToLocal("attribute.name." + this.name);
   }

   public String getLocalizedDisplayValue(Entity entity) {
      ArrayList<String> args = new ArrayList();
      args.add(Tools.formatNicely(this.getTotalAmount(entity)));
      args.add(Tools.formatNicely(this.getBaseAmount(entity)));
      args.add(Tools.formatNicely(this.getCurrentAmount(entity)));

      for(Predicate<Pair<Entity, ArrayList<String>>> editor : this.displayValueArgumentEditors) {
         editor.test(new Pair(entity, args));
      }

      String result = I18n.translateToLocalFormatted("attribute.value." + this.name, args.toArray());
      return result.contains("attribute.value") ? "" + (String)args.get(0) : result;
   }

   public String getLocalizedDescription() {
      return I18n.translateToLocal("attribute.description." + this.name);
   }

   public static class BetterAttributeCalcEvent extends Event {
      public final BetterAttribute attribute;
      public final Entity entity;
      public final ExplicitPriorityQueue<Predicate<double[]>> functions = new ExplicitPriorityQueue<Predicate<double[]>>();

      protected BetterAttributeCalcEvent(BetterAttribute attribute, Entity entity) {
         this.attribute = attribute;
         this.entity = entity;
      }
   }

   public static class BetterAttributeChangedEvent extends Event {
      public final BetterAttribute attribute;
      public final Entity entity;

      public BetterAttributeChangedEvent(BetterAttribute attribute, Entity entity) {
         this.attribute = attribute;
         this.entity = entity;
      }
   }
}
