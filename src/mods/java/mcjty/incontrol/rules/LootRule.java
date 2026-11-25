package mcjty.incontrol.rules;

import com.google.gson.JsonElement;
import java.util.ArrayList;
import java.util.List;
import net.lax1dude.eaglercraft.v1_8.EaglercraftRandom;
import java.util.function.Function;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import mcjty.incontrol.InControl;
import mcjty.incontrol.compat.ModRuleCompatibilityLayer;
import mcjty.incontrol.rules.support.GenericRuleEvaluator;
import mcjty.incontrol.rules.support.RuleKeys;
import mcjty.tools.rules.CommonRuleEvaluator;
import mcjty.tools.rules.IEventQuery;
import mcjty.tools.rules.IModRuleCompatibilityLayer;
import mcjty.tools.rules.RuleBase;
import mcjty.tools.rules.RuleBase.EventGetter;
import mcjty.tools.typed.Attribute;
import mcjty.tools.typed.AttributeMap;
import mcjty.tools.typed.GenericAttributeMapFactory;
import mcjty.tools.varia.Tools;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.JsonToNBT;
import net.minecraft.nbt.NBTException;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.living.LivingDropsEvent;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import net.lax1dude.eaglercraft.v1_8.log4j.Level;

public class LootRule extends RuleBase<EventGetter> {
   public static final IEventQuery<LivingDropsEvent> EVENT_QUERY = new IEventQuery<LivingDropsEvent>() {
      public World getWorld(LivingDropsEvent o) {
         return o.getEntity().getEntityWorld();
      }

      public BlockPos getPos(LivingDropsEvent o) {
         return o.getEntity().getPosition();
      }

      public BlockPos getValidBlockPos(LivingDropsEvent o) {
         return o.getEntity().getPosition().down();
      }

      public int getY(LivingDropsEvent o) {
         return o.getEntity().getPosition().getY();
      }

      public Entity getEntity(LivingDropsEvent o) {
         return o.getEntity();
      }

      public DamageSource getSource(LivingDropsEvent o) {
         return o.getSource();
      }

      public Entity getAttacker(LivingDropsEvent o) {
         return o.getSource().getTrueSource();
      }

      public EntityPlayer getPlayer(LivingDropsEvent o) {
         Entity entity = o.getSource().getTrueSource();
         return entity instanceof EntityPlayer ? (EntityPlayer)entity : null;
      }

      public ItemStack getItem(LivingDropsEvent o) {
         return ItemStack.EMPTY;
      }
   };
   private static final GenericAttributeMapFactory FACTORY = new GenericAttributeMapFactory();
   private static EaglercraftRandom rnd = new EaglercraftRandom();
   private final GenericRuleEvaluator ruleEvaluator;
   private List<Predicate<ItemStack>> toRemoveItems = new ArrayList();
   private List<Pair<ItemStack, Function<Integer, Integer>>> toAddItems = new ArrayList();
   private boolean removeAll = false;

   private LootRule(AttributeMap map) {
      super(InControl.setup.getLogger());
      this.ruleEvaluator = new GenericRuleEvaluator(map);
      this.addActions(map, new ModRuleCompatibilityLayer());
   }

   public static LootRule parse(JsonElement element) {
      if (element == null) {
         return null;
      } else {
         AttributeMap map = FACTORY.parse(element);
         return new LootRule(map);
      }
   }

   protected void addActions(AttributeMap map, IModRuleCompatibilityLayer layer) {
      super.addActions(map, layer);
      if (map.has(RuleKeys.ACTION_ITEM)) {
         this.addItem(map);
      }

      if (map.has(RuleKeys.ACTION_REMOVE)) {
         this.removeItem(map);
      }

      if (map.has(RuleKeys.ACTION_REMOVEALL)) {
         this.removeAll = ((Boolean)map.get(RuleKeys.ACTION_REMOVEALL)).booleanValue();
      }

   }

   public List<Predicate<ItemStack>> getToRemoveItems() {
      return this.toRemoveItems;
   }

   public boolean isRemoveAll() {
      return this.removeAll;
   }

   public List<Pair<ItemStack, Function<Integer, Integer>>> getToAddItems() {
      return this.toAddItems;
   }

   private Function<Integer, Integer> getCountFunction(@Nullable String itemcount) {
      if (itemcount == null) {
         return (looting) -> {
            return Integer.valueOf(1);
         };
      } else {
         String[] loottable = StringUtils.split(itemcount, '/');
         int[] min = new int[loottable.length];
         int[] max = new int[loottable.length];

         for(int i = 0; i < loottable.length; ++i) {
            String[] minmax = StringUtils.split(loottable[i], '-');
            if (minmax.length == 1) {
               try {
                  min[i] = max[i] = Integer.parseInt(minmax[0]);
               } catch (NumberFormatException var8) {
                  InControl.setup.getLogger().log(Level.ERROR, "Bad amount specified in loot rule: " + minmax);
                  min[i] = max[i] = 1;
               }
            } else if (minmax.length == 2) {
               try {
                  min[i] = Integer.parseInt(minmax[0]);
                  max[i] = Integer.parseInt(minmax[1]);
               } catch (NumberFormatException var9) {
                  InControl.setup.getLogger().log(Level.ERROR, "Bad amounts specified in loot rule: " + minmax);
                  min[i] = max[i] = 1;
               }
            } else {
               InControl.setup.getLogger().log(Level.ERROR, "Bad amount range specified in loot rule: " + minmax);
               min[i] = max[i] = 1;
            }
         }

         if (loottable.length == 1) {
            if (min[0] == max[0]) {
               return (looting) -> {
                  return min[0];
               };
            } else {
               return (looting) -> {
                  return rnd.nextInt(max[0] - min[0] + 1) + min[0];
               };
            }
         } else {
            return (looting) -> {
               if (looting.intValue() >= min.length) {
                  return rnd.nextInt(max[min.length - 1] - min[min.length - 1] + 1) + min[min.length - 1];
               } else {
                  return looting.intValue() >= 0 ? rnd.nextInt(max[looting.intValue()] - min[looting.intValue()] + 1) + min[looting.intValue()] : rnd.nextInt(max[0] - min[0] + 1) + min[0];
               }
            };
         }
      }
   }

   private List<Pair<ItemStack, Function<Integer, Integer>>> getItems(List<String> itemNames, @Nullable String nbtJson, @Nullable String itemcount) {
      Function<Integer, Integer> countFunction = this.getCountFunction(itemcount);
      List<Pair<ItemStack, Function<Integer, Integer>>> items = new ArrayList();

      for(String name : itemNames) {
         ItemStack stack = Tools.parseStack(name, InControl.setup.getLogger());
         if (stack.isEmpty()) {
            InControl.setup.getLogger().log(Level.ERROR, "Unknown item '" + name + "'!");
         } else {
            if (nbtJson != null) {
               try {
                  stack.setTagCompound(JsonToNBT.getTagFromJson(nbtJson));
               } catch (NBTException var10) {
                  InControl.setup.getLogger().log(Level.ERROR, "Bad nbt for '" + name + "'!");
               }
            }

            items.add(Pair.of(stack, countFunction));
         }
      }

      return items;
   }

   private void addItem(AttributeMap map) {
      String nbt = (String)map.get(RuleKeys.ACTION_ITEMNBT);
      String itemcount = (String)map.get(RuleKeys.ACTION_ITEMCOUNT);
      this.toAddItems.addAll(this.getItems(map.getList(RuleKeys.ACTION_ITEM), nbt, itemcount));
   }

   private void removeItem(AttributeMap map) {
      this.toRemoveItems.addAll(CommonRuleEvaluator.getItems(map.getList(RuleKeys.ACTION_REMOVE), this.logger));
   }

   public boolean match(LivingDropsEvent event) {
      return this.ruleEvaluator.match(event, EVENT_QUERY);
   }

   static {
      FACTORY.attribute(Attribute.create(RuleKeys.MINTIME)).attribute(Attribute.create(RuleKeys.MAXTIME)).attribute(Attribute.create(RuleKeys.MINLIGHT)).attribute(Attribute.create(RuleKeys.MAXLIGHT)).attribute(Attribute.create(RuleKeys.MINHEIGHT)).attribute(Attribute.create(RuleKeys.MAXHEIGHT)).attribute(Attribute.create(RuleKeys.MINDIFFICULTY)).attribute(Attribute.create(RuleKeys.MAXDIFFICULTY)).attribute(Attribute.create(RuleKeys.MINSPAWNDIST)).attribute(Attribute.create(RuleKeys.MAXSPAWNDIST)).attribute(Attribute.create(RuleKeys.RANDOM)).attribute(Attribute.create(RuleKeys.INBUILDING)).attribute(Attribute.create(RuleKeys.INCITY)).attribute(Attribute.create(RuleKeys.GAMESTAGE)).attribute(Attribute.create(RuleKeys.WINTER)).attribute(Attribute.create(RuleKeys.SUMMER)).attribute(Attribute.create(RuleKeys.SPRING)).attribute(Attribute.create(RuleKeys.AUTUMN)).attribute(Attribute.create(RuleKeys.INSTREET)).attribute(Attribute.create(RuleKeys.INSPHERE)).attribute(Attribute.create(RuleKeys.PASSIVE)).attribute(Attribute.create(RuleKeys.HOSTILE)).attribute(Attribute.create(RuleKeys.SEESKY)).attribute(Attribute.create(RuleKeys.WEATHER)).attribute(Attribute.create(RuleKeys.TEMPCATEGORY)).attribute(Attribute.create(RuleKeys.DIFFICULTY)).attribute(Attribute.create(RuleKeys.STRUCTURE)).attribute(Attribute.create(RuleKeys.PLAYER)).attribute(Attribute.create(RuleKeys.REALPLAYER)).attribute(Attribute.create(RuleKeys.FAKEPLAYER)).attribute(Attribute.create(RuleKeys.PROJECTILE)).attribute(Attribute.create(RuleKeys.EXPLOSION)).attribute(Attribute.create(RuleKeys.FIRE)).attribute(Attribute.create(RuleKeys.MAGIC)).attribute(Attribute.createMulti(RuleKeys.MOB)).attribute(Attribute.createMulti(RuleKeys.MOD)).attribute(Attribute.createMulti(RuleKeys.BLOCK)).attribute(Attribute.create(RuleKeys.BLOCKOFFSET)).attribute(Attribute.createMulti(RuleKeys.BIOME)).attribute(Attribute.createMulti(RuleKeys.BIOMETYPE)).attribute(Attribute.createMulti(RuleKeys.DIMENSION)).attribute(Attribute.createMulti(RuleKeys.SOURCE)).attribute(Attribute.createMulti(RuleKeys.HELMET)).attribute(Attribute.createMulti(RuleKeys.CHESTPLATE)).attribute(Attribute.createMulti(RuleKeys.LEGGINGS)).attribute(Attribute.createMulti(RuleKeys.BOOTS)).attribute(Attribute.createMulti(RuleKeys.AMULET)).attribute(Attribute.createMulti(RuleKeys.RING)).attribute(Attribute.createMulti(RuleKeys.BELT)).attribute(Attribute.createMulti(RuleKeys.TRINKET)).attribute(Attribute.createMulti(RuleKeys.HEAD)).attribute(Attribute.createMulti(RuleKeys.BODY)).attribute(Attribute.createMulti(RuleKeys.CHARM)).attribute(Attribute.create(RuleKeys.STATE)).attribute(Attribute.create(RuleKeys.PSTATE)).attribute(Attribute.createMulti(RuleKeys.HELDITEM)).attribute(Attribute.createMulti(RuleKeys.PLAYER_HELDITEM)).attribute(Attribute.createMulti(RuleKeys.OFFHANDITEM)).attribute(Attribute.createMulti(RuleKeys.BOTHHANDSITEM)).attribute(Attribute.create(RuleKeys.ACTION_ITEMNBT)).attribute(Attribute.create(RuleKeys.ACTION_ITEMCOUNT)).attribute(Attribute.createMulti(RuleKeys.ACTION_ITEM)).attribute(Attribute.createMulti(RuleKeys.ACTION_REMOVE)).attribute(Attribute.create(RuleKeys.ACTION_REMOVEALL));
   }
}
