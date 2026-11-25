package mcjty.incontrol.rules;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import java.util.ArrayList;
import java.util.List;
import mcjty.incontrol.InControl;
import mcjty.incontrol.rules.support.GenericRuleEvaluator;
import mcjty.incontrol.rules.support.RuleKeys;
import mcjty.tools.rules.IEventQuery;
import mcjty.tools.rules.RuleBase;
import mcjty.tools.rules.RuleBase.EventGetter;
import mcjty.tools.typed.Attribute;
import mcjty.tools.typed.AttributeMap;
import mcjty.tools.typed.GenericAttributeMapFactory;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.DamageSource;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.datafix.fixes.EntityId;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome.SpawnListEntry;
import net.minecraftforge.event.world.WorldEvent.PotentialSpawns;
import net.minecraftforge.fml.common.registry.EntityEntry;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import org.apache.logging.log4j.Level;

public class PotentialSpawnRule extends RuleBase<EventGetter> {
   public static final IEventQuery<PotentialSpawns> EVENT_QUERY = new IEventQuery<PotentialSpawns>() {
      public World getWorld(PotentialSpawns o) {
         return o.getWorld();
      }

      public BlockPos getPos(PotentialSpawns o) {
         return o.getPos();
      }

      public BlockPos getValidBlockPos(PotentialSpawns o) {
         return o.getPos().down();
      }

      public int getY(PotentialSpawns o) {
         return o.getPos().getY();
      }

      public Entity getEntity(PotentialSpawns o) {
         return null;
      }

      public DamageSource getSource(PotentialSpawns o) {
         return null;
      }

      public Entity getAttacker(PotentialSpawns o) {
         return null;
      }

      public EntityPlayer getPlayer(PotentialSpawns o) {
         return null;
      }

      public ItemStack getItem(PotentialSpawns o) {
         return ItemStack.EMPTY;
      }
   };
   public static final EntityId FIXER = new EntityId();
   private static final GenericAttributeMapFactory FACTORY = new GenericAttributeMapFactory();
   private static final GenericAttributeMapFactory MOB_FACTORY = new GenericAttributeMapFactory();
   private final GenericRuleEvaluator ruleEvaluator;
   private List<SpawnListEntry> spawnEntries = new ArrayList();
   private List<Class> toRemoveMobs = new ArrayList();

   private PotentialSpawnRule(AttributeMap map) {
      super(InControl.setup.getLogger());
      this.ruleEvaluator = new GenericRuleEvaluator(map);
      if (!map.has(RuleKeys.ACTION_MOBS) && !map.has(RuleKeys.ACTION_REMOVE_MOBS)) {
         InControl.setup.getLogger().log(Level.ERROR, "No 'mobs' or 'remove' specified!");
      } else {
         this.makeSpawnEntries(map);
         if (map.has(RuleKeys.ACTION_REMOVE_MOBS)) {
            this.addToRemoveAction(map);
         }

      }
   }

   public static String fixEntityId(String id) {
      NBTTagCompound nbt = new NBTTagCompound();
      nbt.setString("id", id);
      nbt = FIXER.fixTagCompound(nbt);
      return nbt.getString("id");
   }

   public static PotentialSpawnRule parse(JsonElement element) {
      if (element == null) {
         return null;
      } else {
         JsonObject jsonObject = element.getAsJsonObject();
         if (!jsonObject.has("mobs") && !jsonObject.has("remove")) {
            return null;
         } else {
            AttributeMap map = FACTORY.parse(element);
            if (jsonObject.has("mobs")) {
               for(JsonElement mob : jsonObject.get("mobs").getAsJsonArray()) {
                  AttributeMap mobMap = MOB_FACTORY.parse(mob);
                  map.addList(RuleKeys.ACTION_MOBS, mobMap);
               }
            }

            return new PotentialSpawnRule(map);
         }
      }
   }

   private void addToRemoveAction(AttributeMap map) {
      for(String s : map.getList(RuleKeys.ACTION_REMOVE_MOBS)) {
         String id = fixEntityId(s);
         EntityEntry entry = (EntityEntry)ForgeRegistries.ENTITIES.getValue(new ResourceLocation(id));
         Class<? extends Entity> clazz = entry == null ? null : entry.getEntityClass();
         if (clazz == null) {
            InControl.setup.getLogger().log(Level.ERROR, "Cannot find mob '" + s + "'!");
            return;
         }

         this.toRemoveMobs.add(clazz);
      }

   }

   private void makeSpawnEntries(AttributeMap map) {
      for(AttributeMap mobMap : map.getList(RuleKeys.ACTION_MOBS)) {
         String id = fixEntityId((String)mobMap.get(RuleKeys.MOB_NAME));
         EntityEntry ee = (EntityEntry)ForgeRegistries.ENTITIES.getValue(new ResourceLocation(id));
         Class<? extends Entity> clazz = ee == null ? null : ee.getEntityClass();
         if (clazz == null) {
            InControl.setup.getLogger().log(Level.ERROR, "Cannot find mob '" + (String)mobMap.get(RuleKeys.MOB_NAME) + "'!");
            return;
         }

         Integer weight = (Integer)mobMap.get(RuleKeys.MOB_WEIGHT);
         if (weight == null) {
            weight = Integer.valueOf(1);
         }

         Integer groupCountMin = (Integer)mobMap.get(RuleKeys.MOB_GROUPCOUNTMIN);
         if (groupCountMin == null) {
            groupCountMin = Integer.valueOf(1);
         }

         Integer groupCountMax = (Integer)mobMap.get(RuleKeys.MOB_GROUPCOUNTMAX);
         if (groupCountMax == null) {
            groupCountMax = Math.max(groupCountMin.intValue(), 1);
         }

         SpawnListEntry entry = new SpawnListEntry(clazz, weight.intValue(), groupCountMin.intValue(), groupCountMax.intValue());
         this.spawnEntries.add(entry);
      }

   }

   public List<SpawnListEntry> getSpawnEntries() {
      return this.spawnEntries;
   }

   public boolean match(PotentialSpawns event) {
      return this.ruleEvaluator.match(event, EVENT_QUERY);
   }

   public List<Class> getToRemoveMobs() {
      return this.toRemoveMobs;
   }

   static {
      FACTORY.attribute(Attribute.create(RuleKeys.MINCOUNT)).attribute(Attribute.create(RuleKeys.MAXCOUNT)).attribute(Attribute.create(RuleKeys.MINTIME)).attribute(Attribute.create(RuleKeys.MAXTIME)).attribute(Attribute.create(RuleKeys.MINLIGHT)).attribute(Attribute.create(RuleKeys.MAXLIGHT)).attribute(Attribute.create(RuleKeys.MINHEIGHT)).attribute(Attribute.create(RuleKeys.MAXHEIGHT)).attribute(Attribute.create(RuleKeys.MINDIFFICULTY)).attribute(Attribute.create(RuleKeys.MAXDIFFICULTY)).attribute(Attribute.create(RuleKeys.MINSPAWNDIST)).attribute(Attribute.create(RuleKeys.MAXSPAWNDIST)).attribute(Attribute.create(RuleKeys.RANDOM)).attribute(Attribute.create(RuleKeys.SEESKY)).attribute(Attribute.create(RuleKeys.WEATHER)).attribute(Attribute.create(RuleKeys.TEMPCATEGORY)).attribute(Attribute.create(RuleKeys.DIFFICULTY)).attribute(Attribute.create(RuleKeys.STRUCTURE)).attribute(Attribute.create(RuleKeys.WINTER)).attribute(Attribute.create(RuleKeys.SUMMER)).attribute(Attribute.create(RuleKeys.SPRING)).attribute(Attribute.create(RuleKeys.AUTUMN)).attribute(Attribute.create(RuleKeys.STATE)).attribute(Attribute.createMulti(RuleKeys.BLOCK)).attribute(Attribute.create(RuleKeys.BLOCKOFFSET)).attribute(Attribute.createMulti(RuleKeys.BIOME)).attribute(Attribute.createMulti(RuleKeys.BIOMETYPE)).attribute(Attribute.createMulti(RuleKeys.DIMENSION)).attribute(Attribute.createMulti(RuleKeys.ACTION_REMOVE_MOBS));
      MOB_FACTORY.attribute(Attribute.create(RuleKeys.MOB_NAME)).attribute(Attribute.create(RuleKeys.MOB_WEIGHT)).attribute(Attribute.create(RuleKeys.MOB_GROUPCOUNTMIN)).attribute(Attribute.create(RuleKeys.MOB_GROUPCOUNTMAX));
   }
}
