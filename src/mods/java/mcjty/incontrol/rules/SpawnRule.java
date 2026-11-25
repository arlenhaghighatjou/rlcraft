package mcjty.incontrol.rules;

import com.google.gson.JsonElement;
import java.util.function.Consumer;
import javax.annotation.Nullable;
import mcjty.incontrol.InControl;
import mcjty.incontrol.compat.ModRuleCompatibilityLayer;
import mcjty.incontrol.rules.support.GenericRuleEvaluator;
import mcjty.incontrol.rules.support.RuleKeys;
import mcjty.tools.rules.IEventQuery;
import mcjty.tools.rules.IModRuleCompatibilityLayer;
import mcjty.tools.rules.RuleBase;
import mcjty.tools.rules.RuleBase.EventGetter;
import mcjty.tools.typed.Attribute;
import mcjty.tools.typed.AttributeMap;
import mcjty.tools.typed.GenericAttributeMapFactory;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.living.LivingSpawnEvent.CheckSpawn;
import net.minecraftforge.fml.common.eventhandler.Event.Result;

public class SpawnRule extends RuleBase<EventGetter> {
   public static final IEventQuery<CheckSpawn> EVENT_QUERY = new IEventQuery<CheckSpawn>() {
      public World getWorld(CheckSpawn o) {
         return o.getWorld();
      }

      public BlockPos getPos(CheckSpawn o) {
         return new BlockPos((double)o.getX(), (double)o.getY(), (double)o.getZ());
      }

      public BlockPos getValidBlockPos(CheckSpawn o) {
         return new BlockPos((double)o.getX(), (double)(o.getY() - 1.0F), (double)o.getZ());
      }

      public int getY(CheckSpawn o) {
         return (int)o.getY();
      }

      public Entity getEntity(CheckSpawn o) {
         return o.getEntity();
      }

      public DamageSource getSource(CheckSpawn o) {
         return null;
      }

      public Entity getAttacker(CheckSpawn o) {
         return null;
      }

      public EntityPlayer getPlayer(CheckSpawn o) {
         return SpawnRule.getClosestPlayer(o.getWorld(), new BlockPos((double)o.getX(), (double)o.getY(), (double)o.getZ()));
      }

      public ItemStack getItem(CheckSpawn o) {
         return ItemStack.EMPTY;
      }
   };
   public static final IEventQuery<EntityJoinWorldEvent> EVENT_QUERY_JOIN = new IEventQuery<EntityJoinWorldEvent>() {
      public World getWorld(EntityJoinWorldEvent o) {
         return o.getWorld();
      }

      public BlockPos getPos(EntityJoinWorldEvent o) {
         return o.getEntity().getPosition();
      }

      public BlockPos getValidBlockPos(EntityJoinWorldEvent o) {
         return o.getEntity().getPosition().down();
      }

      public int getY(EntityJoinWorldEvent o) {
         return o.getEntity().getPosition().getY();
      }

      public Entity getEntity(EntityJoinWorldEvent o) {
         return o.getEntity();
      }

      public DamageSource getSource(EntityJoinWorldEvent o) {
         return null;
      }

      public Entity getAttacker(EntityJoinWorldEvent o) {
         return null;
      }

      public EntityPlayer getPlayer(EntityJoinWorldEvent o) {
         return SpawnRule.getClosestPlayer(o.getWorld(), o.getEntity().getPosition());
      }

      public ItemStack getItem(EntityJoinWorldEvent o) {
         return ItemStack.EMPTY;
      }
   };
   private static final GenericAttributeMapFactory FACTORY = new GenericAttributeMapFactory();
   private final boolean onJoin;
   private final GenericRuleEvaluator ruleEvaluator;
   private Result result = null;

   private static EntityPlayer getClosestPlayer(World world, BlockPos pos) {
      return world.getClosestPlayer((double)pos.getX(), (double)pos.getY(), (double)pos.getZ(), 100.0D, false);
   }

   private SpawnRule(AttributeMap map, boolean onJoin) {
      super(InControl.setup.getLogger());
      this.onJoin = onJoin;
      this.ruleEvaluator = new GenericRuleEvaluator(map);
      this.addActions(map, new ModRuleCompatibilityLayer());
   }

   public static SpawnRule parse(JsonElement element) {
      if (element == null) {
         return null;
      } else {
         AttributeMap map = FACTORY.parse(element);
         boolean onJoin = element.getAsJsonObject().has("onjoin") && element.getAsJsonObject().get("onjoin").getAsBoolean();
         return new SpawnRule(map, onJoin);
      }
   }

   protected void addActions(AttributeMap map, IModRuleCompatibilityLayer layer) {
      super.addActions(map, layer);
      if (map.has(RuleKeys.ACTION_RESULT)) {
         String br = (String)map.get(RuleKeys.ACTION_RESULT);
         if (!"default".equals(br) && !br.startsWith("def")) {
            if (!"allow".equals(br) && !"true".equals(br)) {
               this.result = Result.DENY;
            } else {
               this.result = Result.ALLOW;
            }
         } else {
            this.result = Result.DEFAULT;
         }
      } else {
         this.result = null;
      }

   }

   public boolean match(CheckSpawn event) {
      return this.ruleEvaluator.match(event, EVENT_QUERY);
   }

   public boolean match(EntityJoinWorldEvent event) {
      return this.ruleEvaluator.match(event, EVENT_QUERY_JOIN);
   }

   public void action(final CheckSpawn event) {
      EventGetter getter = new EventGetter() {
         public EntityLivingBase getEntityLiving() {
            return event.getEntityLiving();
         }

         public EntityPlayer getPlayer() {
            return null;
         }

         public World getWorld() {
            return event.getWorld();
         }

         public BlockPos getPosition() {
            return event.getEntityLiving().getPosition();
         }
      };

      for(Consumer<EventGetter> action : this.actions) {
         action.accept(getter);
      }

   }

   public void action(final EntityJoinWorldEvent event) {
      EventGetter getter = new EventGetter() {
         public EntityLivingBase getEntityLiving() {
            return event.getEntity() instanceof EntityLivingBase ? (EntityLivingBase)event.getEntity() : null;
         }

         public EntityPlayer getPlayer() {
            return null;
         }

         public World getWorld() {
            return event.getWorld();
         }

         public BlockPos getPosition() {
            return event.getEntity() != null ? event.getEntity().getPosition() : null;
         }
      };

      for(Consumer<EventGetter> action : this.actions) {
         action.accept(getter);
      }

   }

   @Nullable
   public Result getResult() {
      return this.result;
   }

   public boolean isOnJoin() {
      return this.onJoin;
   }

   static {
      FACTORY.attribute(Attribute.create(RuleKeys.MINTIME)).attribute(Attribute.create(RuleKeys.MAXTIME)).attribute(Attribute.create(RuleKeys.MINCOUNT)).attribute(Attribute.create(RuleKeys.MAXCOUNT)).attribute(Attribute.create(RuleKeys.MINLIGHT)).attribute(Attribute.create(RuleKeys.MAXLIGHT)).attribute(Attribute.create(RuleKeys.MINHEIGHT)).attribute(Attribute.create(RuleKeys.MAXHEIGHT)).attribute(Attribute.create(RuleKeys.MINDIFFICULTY)).attribute(Attribute.create(RuleKeys.MAXDIFFICULTY)).attribute(Attribute.create(RuleKeys.MINSPAWNDIST)).attribute(Attribute.create(RuleKeys.MAXSPAWNDIST)).attribute(Attribute.create(RuleKeys.RANDOM)).attribute(Attribute.create(RuleKeys.CANSPAWNHERE)).attribute(Attribute.create(RuleKeys.NOTCOLLIDING)).attribute(Attribute.create(RuleKeys.SPAWNER)).attribute(Attribute.create(RuleKeys.INBUILDING)).attribute(Attribute.create(RuleKeys.INCITY)).attribute(Attribute.create(RuleKeys.INSTREET)).attribute(Attribute.create(RuleKeys.INSPHERE)).attribute(Attribute.create(RuleKeys.GAMESTAGE)).attribute(Attribute.create(RuleKeys.PASSIVE)).attribute(Attribute.create(RuleKeys.HOSTILE)).attribute(Attribute.create(RuleKeys.SEESKY)).attribute(Attribute.create(RuleKeys.WEATHER)).attribute(Attribute.create(RuleKeys.TEMPCATEGORY)).attribute(Attribute.create(RuleKeys.DIFFICULTY)).attribute(Attribute.create(RuleKeys.STRUCTURE)).attribute(Attribute.create(RuleKeys.WINTER)).attribute(Attribute.create(RuleKeys.SUMMER)).attribute(Attribute.create(RuleKeys.SPRING)).attribute(Attribute.create(RuleKeys.AUTUMN)).attribute(Attribute.createMulti(RuleKeys.MOB)).attribute(Attribute.createMulti(RuleKeys.MOD)).attribute(Attribute.createMulti(RuleKeys.BLOCK)).attribute(Attribute.create(RuleKeys.BLOCKOFFSET)).attribute(Attribute.createMulti(RuleKeys.BIOME)).attribute(Attribute.createMulti(RuleKeys.BIOMETYPE)).attribute(Attribute.createMulti(RuleKeys.DIMENSION)).attribute(Attribute.create(RuleKeys.STATE)).attribute(Attribute.createMulti(RuleKeys.HELMET)).attribute(Attribute.createMulti(RuleKeys.CHESTPLATE)).attribute(Attribute.createMulti(RuleKeys.LEGGINGS)).attribute(Attribute.createMulti(RuleKeys.BOOTS)).attribute(Attribute.createMulti(RuleKeys.AMULET)).attribute(Attribute.createMulti(RuleKeys.RING)).attribute(Attribute.createMulti(RuleKeys.BELT)).attribute(Attribute.createMulti(RuleKeys.TRINKET)).attribute(Attribute.createMulti(RuleKeys.HEAD)).attribute(Attribute.createMulti(RuleKeys.BODY)).attribute(Attribute.createMulti(RuleKeys.CHARM)).attribute(Attribute.createMulti(RuleKeys.PLAYER_HELDITEM)).attribute(Attribute.createMulti(RuleKeys.OFFHANDITEM)).attribute(Attribute.createMulti(RuleKeys.BOTHHANDSITEM)).attribute(Attribute.create(RuleKeys.ACTION_RESULT)).attribute(Attribute.create(RuleKeys.ACTION_MESSAGE)).attribute(Attribute.create(RuleKeys.ACTION_HEALTHMULTIPLY)).attribute(Attribute.create(RuleKeys.ACTION_HEALTHADD)).attribute(Attribute.create(RuleKeys.ACTION_SPEEDMULTIPLY)).attribute(Attribute.create(RuleKeys.ACTION_SPEEDADD)).attribute(Attribute.create(RuleKeys.ACTION_DAMAGEMULTIPLY)).attribute(Attribute.create(RuleKeys.ACTION_DAMAGEADD)).attribute(Attribute.create(RuleKeys.ACTION_SIZEMULTIPLY)).attribute(Attribute.create(RuleKeys.ACTION_SIZEADD)).attribute(Attribute.create(RuleKeys.ACTION_ANGRY)).attribute(Attribute.create(RuleKeys.ACTION_MOBNBT)).attribute(Attribute.create(RuleKeys.ACTION_CUSTOMNAME)).attribute(Attribute.createMulti(RuleKeys.ACTION_HELDITEM)).attribute(Attribute.createMulti(RuleKeys.ACTION_ARMORBOOTS)).attribute(Attribute.createMulti(RuleKeys.ACTION_ARMORLEGS)).attribute(Attribute.createMulti(RuleKeys.ACTION_ARMORCHEST)).attribute(Attribute.createMulti(RuleKeys.ACTION_ARMORHELMET)).attribute(Attribute.createMulti(RuleKeys.ACTION_POTION));
   }
}
