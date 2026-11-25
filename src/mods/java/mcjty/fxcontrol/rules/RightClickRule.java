package mcjty.fxcontrol.rules;

import com.google.gson.JsonElement;
import java.util.function.Consumer;
import mcjty.fxcontrol.FxControl;
import mcjty.fxcontrol.compat.ModRuleCompatibilityLayer;
import mcjty.fxcontrol.rules.support.GenericRuleEvaluator;
import mcjty.fxcontrol.rules.support.RuleKeys;
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
import net.minecraftforge.event.entity.player.PlayerInteractEvent.RightClickBlock;
import net.minecraftforge.fml.common.eventhandler.Event.Result;

public class RightClickRule extends RuleBase<EventGetter> {
   private static final GenericAttributeMapFactory FACTORY = new GenericAttributeMapFactory();
   public static final IEventQuery<RightClickBlock> EVENT_QUERY = new IEventQuery<RightClickBlock>() {
      public World getWorld(RightClickBlock o) {
         return o.getWorld();
      }

      public BlockPos getPos(RightClickBlock o) {
         return o.getPos();
      }

      public BlockPos getValidBlockPos(RightClickBlock o) {
         return o.getPos();
      }

      public int getY(RightClickBlock o) {
         return o.getPos().getY();
      }

      public Entity getEntity(RightClickBlock o) {
         return o.getEntityPlayer();
      }

      public DamageSource getSource(RightClickBlock o) {
         return null;
      }

      public Entity getAttacker(RightClickBlock o) {
         return null;
      }

      public EntityPlayer getPlayer(RightClickBlock o) {
         return o.getEntityPlayer();
      }

      public ItemStack getItem(RightClickBlock o) {
         return o.getItemStack();
      }
   };
   private Result result;
   private final GenericRuleEvaluator ruleEvaluator;

   private RightClickRule(AttributeMap map) {
      super(FxControl.setup.getLogger());
      this.ruleEvaluator = new GenericRuleEvaluator(map);
      this.addActions(map, new ModRuleCompatibilityLayer());
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
         this.result = Result.DEFAULT;
      }

   }

   public boolean match(RightClickBlock event) {
      return this.ruleEvaluator.match(event, EVENT_QUERY);
   }

   public void action(final RightClickBlock event) {
      EventGetter getter = new EventGetter() {
         public EntityLivingBase getEntityLiving() {
            return event.getEntityPlayer();
         }

         public EntityPlayer getPlayer() {
            return event.getEntityPlayer();
         }

         public World getWorld() {
            return event.getWorld();
         }

         public BlockPos getPosition() {
            return event.getPos();
         }
      };

      for(Consumer<EventGetter> action : this.actions) {
         action.accept(getter);
      }

   }

   public Result getResult() {
      return this.result;
   }

   public static RightClickRule parse(JsonElement element) {
      if (element == null) {
         return null;
      } else {
         AttributeMap map = null;

         try {
            map = FACTORY.parse(element);
         } catch (Exception var3) {
            var3.printStackTrace();
            return null;
         }

         return new RightClickRule(map);
      }
   }

   static {
      FACTORY.attribute(Attribute.create(RuleKeys.MINTIME)).attribute(Attribute.create(RuleKeys.MAXTIME)).attribute(Attribute.create(RuleKeys.MINLIGHT)).attribute(Attribute.create(RuleKeys.MAXLIGHT)).attribute(Attribute.create(RuleKeys.MINHEIGHT)).attribute(Attribute.create(RuleKeys.MAXHEIGHT)).attribute(Attribute.create(RuleKeys.MINDIFFICULTY)).attribute(Attribute.create(RuleKeys.MAXDIFFICULTY)).attribute(Attribute.create(RuleKeys.MINSPAWNDIST)).attribute(Attribute.create(RuleKeys.MAXSPAWNDIST)).attribute(Attribute.create(RuleKeys.RANDOM)).attribute(Attribute.create(RuleKeys.SEESKY)).attribute(Attribute.create(RuleKeys.WEATHER)).attribute(Attribute.create(RuleKeys.TEMPCATEGORY)).attribute(Attribute.create(RuleKeys.DIFFICULTY)).attribute(Attribute.create(RuleKeys.STRUCTURE)).attribute(Attribute.createMulti(RuleKeys.MOD)).attribute(Attribute.create(RuleKeys.GAMESTAGE)).attribute(Attribute.create(RuleKeys.WINTER)).attribute(Attribute.create(RuleKeys.SUMMER)).attribute(Attribute.create(RuleKeys.SPRING)).attribute(Attribute.create(RuleKeys.AUTUMN)).attribute(Attribute.create(RuleKeys.INBUILDING)).attribute(Attribute.create(RuleKeys.INCITY)).attribute(Attribute.create(RuleKeys.INSTREET)).attribute(Attribute.create(RuleKeys.INSPHERE)).attribute(Attribute.createMulti(RuleKeys.AMULET)).attribute(Attribute.createMulti(RuleKeys.RING)).attribute(Attribute.createMulti(RuleKeys.BELT)).attribute(Attribute.createMulti(RuleKeys.TRINKET)).attribute(Attribute.createMulti(RuleKeys.HEAD)).attribute(Attribute.createMulti(RuleKeys.BODY)).attribute(Attribute.createMulti(RuleKeys.CHARM)).attribute(Attribute.createMulti(RuleKeys.BLOCK)).attribute(Attribute.create(RuleKeys.BLOCKOFFSET)).attribute(Attribute.createMulti(RuleKeys.HELMET)).attribute(Attribute.createMulti(RuleKeys.CHESTPLATE)).attribute(Attribute.createMulti(RuleKeys.LEGGINGS)).attribute(Attribute.createMulti(RuleKeys.BOOTS)).attribute(Attribute.createMulti(RuleKeys.HELDITEM)).attribute(Attribute.createMulti(RuleKeys.PLAYER_HELDITEM)).attribute(Attribute.createMulti(RuleKeys.OFFHANDITEM)).attribute(Attribute.createMulti(RuleKeys.BOTHHANDSITEM)).attribute(Attribute.createMulti(RuleKeys.BIOME)).attribute(Attribute.createMulti(RuleKeys.BIOMETYPE)).attribute(Attribute.createMulti(RuleKeys.DIMENSION)).attribute(Attribute.create(RuleKeys.ACTION_COMMAND)).attribute(Attribute.create(RuleKeys.ACTION_ADDSTAGE)).attribute(Attribute.create(RuleKeys.ACTION_REMOVESTAGE)).attribute(Attribute.create(RuleKeys.ACTION_MESSAGE)).attribute(Attribute.create(RuleKeys.ACTION_FIRE)).attribute(Attribute.create(RuleKeys.ACTION_EXPLOSION)).attribute(Attribute.create(RuleKeys.ACTION_CLEAR)).attribute(Attribute.create(RuleKeys.ACTION_DAMAGE)).attribute(Attribute.create(RuleKeys.ACTION_SETBLOCK)).attribute(Attribute.create(RuleKeys.ACTION_SETHELDITEM)).attribute(Attribute.create(RuleKeys.ACTION_SETHELDAMOUNT)).attribute(Attribute.create(RuleKeys.ACTION_RESULT)).attribute(Attribute.create(RuleKeys.ACTION_SETSTATE)).attribute(Attribute.create(RuleKeys.ACTION_SETPSTATE)).attribute(Attribute.createMulti(RuleKeys.ACTION_POTION)).attribute(Attribute.createMulti(RuleKeys.ACTION_GIVE)).attribute(Attribute.createMulti(RuleKeys.ACTION_DROP));
   }
}
