package tschipp.carryon.common.scripting;

import java.lang.reflect.Method;
import javax.annotation.Nullable;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementManager;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.relauncher.ReflectionHelper;
import tschipp.carryon.common.config.CarryOnConfig;
import tschipp.carryon.common.handler.ListHandler;
import tschipp.carryon.common.helper.ScriptParseHelper;

public class ScriptChecker {
   public ScriptChecker() {
   }

   @Nullable
   public static CarryOnOverride inspectBlock(IBlockState state, World world, BlockPos pos, @Nullable NBTTagCompound tag) {
      if (!CarryOnConfig.settings.useScripts) {
         return null;
      } else {
         Block block = state.getBlock();
         int meta = block.getMetaFromState(state);
         Material material = state.getMaterial();
         float hardness = state.getBlockHardness(world, pos);
         float resistance = block.getExplosionResistance((Entity)null);
         NBTTagCompound nbt = tag;
         boolean isAllowed = CarryOnConfig.settings.useWhitelistBlocks ? ListHandler.isAllowed(block) : !ListHandler.isForbidden(block);
         if (isAllowed) {
            for(CarryOnOverride override : ScriptReader.OVERRIDES.values()) {
               if (override.isBlock() && matchesAll(override, block, meta, material, hardness, resistance, nbt)) {
                  return override;
               }
            }
         }

         return null;
      }
   }

   @Nullable
   public static CarryOnOverride inspectEntity(Entity entity) {
      if (!CarryOnConfig.settings.useScripts) {
         return null;
      } else {
         String name = EntityList.getKey(entity).toString();
         float height = entity.height;
         float width = entity.width;
         float health = entity instanceof EntityLivingBase ? ((EntityLivingBase)entity).getHealth() : 0.0F;
         NBTTagCompound tag = new NBTTagCompound();
         entity.writeToNBT(tag);
         boolean isAllowed = CarryOnConfig.settings.useWhitelistEntities ? ListHandler.isAllowed(entity) : !ListHandler.isForbidden(entity);
         if (isAllowed) {
            for(CarryOnOverride override : ScriptReader.OVERRIDES.values()) {
               if (override.isEntity() && matchesAll(override, name, height, width, health, tag)) {
                  return override;
               }
            }
         }

         return null;
      }
   }

   public static boolean matchesAll(CarryOnOverride override, String name, float height, float width, float health, NBTTagCompound tag) {
      boolean matchname = override.getTypeNameEntity() == null ? true : name.equals(override.getTypeNameEntity());
      boolean matchheight = ScriptParseHelper.matches((double)height, override.getTypeHeight());
      boolean matchwidth = ScriptParseHelper.matches((double)width, override.getTypeWidth());
      boolean matchhealth = ScriptParseHelper.matches((double)health, override.getTypeHealth());
      boolean matchnbt = ScriptParseHelper.matches(tag, override.getTypeEntityTag());
      return matchname && matchheight && matchwidth && matchhealth && matchnbt;
   }

   public static boolean matchesAll(CarryOnOverride override, Block block, int meta, Material material, float hardness, float resistance, NBTTagCompound nbt) {
      boolean matchnbt = ScriptParseHelper.matches(nbt, override.getTypeBlockTag());
      boolean matchblock = ScriptParseHelper.matches(block, override.getTypeNameBlock());
      boolean matchmeta = ScriptParseHelper.matches((double)meta, override.getTypeMeta());
      boolean matchmaterial = ScriptParseHelper.matches(material, override.getTypeMaterial());
      boolean matchhardness = ScriptParseHelper.matches((double)hardness, override.getTypeHardness());
      boolean matchresistance = ScriptParseHelper.matches((double)resistance, override.getTypeResistance());
      return matchnbt && matchblock && matchmeta && matchmaterial && matchhardness && matchresistance;
   }

   public static boolean fulfillsConditions(CarryOnOverride override, EntityPlayer player) {
      AdvancementManager manager = ((WorldServer)((EntityPlayerMP)player).world).getAdvancementManager();
      Advancement adv = manager.getAdvancement(new ResourceLocation(override.getConditionAchievement() == null ? "" : override.getConditionAchievement()));
      boolean achievement = adv == null ? true : ((EntityPlayerMP)player).getAdvancements().getProgress(adv).isDone();
      boolean gamemode = ScriptParseHelper.matches((double)((EntityPlayerMP)player).interactionManager.getGameType().getID(), override.getConditionGamemode());
      boolean gamestage = true;
      if (Loader.isModLoaded("gamestages") && override.getConditionGamestage() != null) {
         try {
            Class<?> gameStageHelper = Class.forName("net.darkhax.gamestages.GameStageHelper");
            Class<?> iStageData = Class.forName("net.darkhax.gamestages.data.IStageData");
            Method getPlayerData = ReflectionHelper.findMethod(gameStageHelper, "getPlayerData", (String)null, new Class[]{EntityPlayer.class});
            Method hasStage = ReflectionHelper.findMethod(iStageData, "hasStage", (String)null, new Class[]{String.class});
            Object stageData = getPlayerData.invoke((Object)null, player);
            String condition = override.getConditionGamestage();
            gamestage = ((Boolean)hasStage.invoke(stageData, condition)).booleanValue();
         } catch (Exception var15) {
            try {
               Class<?> playerDataHandler = Class.forName("net.darkhax.gamestages.capabilities.PlayerDataHandler");
               Class<?> iStageData = Class.forName("net.darkhax.gamestages.capabilities.PlayerDataHandler$IStageData");
               Method getStageData = ReflectionHelper.findMethod(playerDataHandler, "getStageData", (String)null, new Class[]{EntityPlayer.class});
               Method hasUnlockedStage = ReflectionHelper.findMethod(iStageData, "hasUnlockedStage", (String)null, new Class[]{String.class});
               Object stageData = getStageData.invoke((Object)null, player);
               String condition = override.getConditionGamestage();
               gamestage = ((Boolean)hasUnlockedStage.invoke(stageData, condition)).booleanValue();
            } catch (Exception var14) {
               ;
            }
         }
      }

      boolean position = ScriptParseHelper.matches(player.getPosition(), override.getConditionPosition());
      boolean xp = ScriptParseHelper.matches((double)player.experienceLevel, override.getConditionXp());
      boolean scoreboard = ScriptParseHelper.matchesScore(player, override.getConditionScoreboard());
      boolean effects = ScriptParseHelper.hasEffects(player, override.getConditionEffects());
      return achievement && gamemode && gamestage && position && xp && scoreboard && effects;
   }

   @Nullable
   public static CarryOnOverride getOverride(EntityPlayer player) {
      NBTTagCompound tag = player.getEntityData();
      if (tag != null && tag.hasKey("overrideKey")) {
         int key = tag.getInteger("overrideKey");
         return (CarryOnOverride)ScriptReader.OVERRIDES.get(Integer.valueOf(key));
      } else {
         return null;
      }
   }

   public static void setCarryOnOverride(EntityPlayer player, int i) {
      NBTTagCompound tag = player.getEntityData();
      if (tag != null) {
         tag.setInteger("overrideKey", i);
      }

   }
}
