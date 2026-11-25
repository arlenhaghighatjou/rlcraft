package xzeroair.trinkets.traits.abilities;

import java.util.List;
import java.util.TreeMap;
import java.util.Map.Entry;
import java.util.function.BiConsumer;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import xzeroair.trinkets.client.keybinds.ModKeyBindings;
import xzeroair.trinkets.client.particles.ParticleGreed;
import xzeroair.trinkets.enums.TargetOreType;
import xzeroair.trinkets.init.ModItems;
import xzeroair.trinkets.traits.abilities.interfaces.IKeyBindInterface;
import xzeroair.trinkets.traits.abilities.interfaces.ITickableAbility;
import xzeroair.trinkets.traits.abilities.interfaces.IToggleAbility;
import xzeroair.trinkets.util.Reference;
import xzeroair.trinkets.util.TrinketsConfig;
import xzeroair.trinkets.util.compat.OreDictionaryCompat;
import xzeroair.trinkets.util.config.ClientConfig;
import xzeroair.trinkets.util.config.ConfigHelper;
import xzeroair.trinkets.util.config.trinkets.ConfigDragonsEye;
import xzeroair.trinkets.util.handlers.Counter;
import xzeroair.trinkets.util.helpers.RayTraceHelper;
import xzeroair.trinkets.util.helpers.TranslationHelper;

public class AbilityBlockFinder extends Ability implements ITickableAbility, IToggleAbility, IKeyBindInterface {
   protected final ConfigDragonsEye serverConfig;
   protected final ClientConfig.ClientConfigItems.ClientConfigDragonsEye clientConfig;
   protected boolean firstTick;
   private int targetValue;

   public AbilityBlockFinder() {
      super("block_detection");
      this.serverConfig = TrinketsConfig.SERVER.Items.DRAGON_EYE;
      this.clientConfig = TrinketsConfig.CLIENT.items.DRAGON_EYE;
      this.firstTick = false;
      this.targetValue = -1;
      this.firstTick = true;
   }

   public int getTargetValue() {
      return this.targetValue;
   }

   public boolean abilityEnabled() {
      return this.targetValue > 0;
   }

   public IToggleAbility toggleAbility(boolean enabled) {
      return this;
   }

   public IToggleAbility toggleAbility(int value) {
      this.targetValue = value;
      return this;
   }

   protected void IterateBlocks(EntityLivingBase entity, Vec3d origin, World world, AxisAlignedBB aabb) {
      int i = MathHelper.floor(aabb.minX);
      int j = MathHelper.floor(aabb.maxX + 1.0D);
      int k = MathHelper.floor(aabb.minY);
      int l = MathHelper.floor(aabb.maxY + 1.0D);
      int i1 = MathHelper.floor(aabb.minZ);
      int j1 = MathHelper.floor(aabb.maxZ + 1.0D);
      boolean closest = this.serverConfig.BLOCKS.closest;
      TreeMap<Double, Vec3d> collection = new TreeMap();
      ConfigHelper.TreasureEntry treasure = this.getTreasure();
      if (treasure != null) {
         if (!treasure.getObjectRegistryName().contentEquals("*:*")) {
            for(int k1 = i; k1 < j; ++k1) {
               for(int l1 = k; l1 < l; ++l1) {
                  for(int i2 = i1; i2 < j1; ++i2) {
                     Vec3d pos = new Vec3d((double)k1, (double)l1, (double)i2);
                     BlockPos bPos = new BlockPos(pos);
                     IBlockState state = world.getBlockState(bPos);
                     Block block = state.getBlock();
                     if (!block.isAir(state, world, bPos) && treasure.doesBlockMatchEntry(state)) {
                        double blockDist = pos.distanceTo(origin);
                        collection.put(Double.valueOf(blockDist), pos);
                     }
                  }
               }
            }

            try {
               ResourceLocation isEntityRegName = new ResourceLocation(treasure.getObjectRegistryName());
               if (EntityList.isRegistered(isEntityRegName)) {
                  Class<? extends Entity> e = EntityList.getClass(isEntityRegName);
                  if (e != null) {
                     List<? extends Entity> entList = world.getEntitiesWithinAABB(e, aabb);
                     if (!entList.isEmpty()) {
                        for(Entity targetEntity : entList) {
                           Vec3d pos = targetEntity.getPositionVector();
                           double chestDist = pos.distanceTo(origin);
                           collection.put(Double.valueOf(chestDist), pos);
                        }
                     }
                  }
               }
            } catch (Exception var23) {
               ;
            }

            if (!collection.isEmpty() && world.isRemote) {
               Entry<Double, Vec3d> first = collection.firstEntry();
               double distance = ((Double)first.getKey()).doubleValue();
               if (((Double)first.getKey()).doubleValue() > 1.8D) {
                  this.playSound(entity, new BlockPos((Vec3d)first.getValue()), distance);
               }

               if (closest) {
                  for(int p1 = 0; p1 < 3; ++p1) {
                     this.SpawnParticle(entity.getEntityWorld(), (Vec3d)first.getValue(), treasure.getColor());
                  }
               } else {
                  collection.forEach((dist, posx) -> {
                     for(int p1 = 0; p1 < 3; ++p1) {
                        this.SpawnParticle(entity.getEntityWorld(), pos, treasure.getColor());
                     }

                  });
               }
            }

         }
      }
   }

   public void tickAbility(EntityLivingBase entity) {
      if (this.serverConfig.oreFinder) {
         int length = this.serverConfig.BLOCKS.Blocks.length;
         World world = entity.getEntityWorld();
         if (this.targetValue > length) {
            this.targetValue = -1;
         }

         if (this.targetValue < 0) {
            return;
         }

         int vd = this.serverConfig.BLOCKS.DR.C00_VD;
         int hd = this.serverConfig.BLOCKS.DR.C001_HD;
         AxisAlignedBB aabb = entity.getEntityBoundingBox().grow((double)hd, (double)vd, (double)hd);
         int rf = this.clientConfig.Render_Cooldown;
         Counter counter = this.tickHandler.getCounter("refresh_rate", rf, true, true, true, false);
         if (this.firstTick || counter.Tick()) {
            this.firstTick = false;
            this.IterateBlocks(entity, entity.getPositionVector(), world, aabb);
         }
      }

   }

   public ConfigHelper.TreasureEntry getTreasure() {
      TreeMap<Integer, ConfigHelper.TreasureEntry> TreasureBlocks = ConfigHelper.TrinketConfigStorage.TreasureBlocks;
      return (ConfigHelper.TreasureEntry)TreasureBlocks.get(Integer.valueOf(this.targetValue));
   }

   @SideOnly(Side.CLIENT)
   protected void drawPath(EntityLivingBase player, Vec3d target, int color) {
      double d = player.getDistance(target.x, target.y, target.z);
      if (d > 2.0D) {
         RayTraceHelper.Beam beam = new RayTraceHelper.Beam(player.world, player, d, 1.0D, false);
         GlStateManager.pushMatrix();
         RayTraceHelper.drawPath(player.getPositionVector().addVector(0.0D, (double)player.getEyeHeight() * 0.8D, 0.0D), target, player.world, beam, color, 2.0D);
         GlStateManager.popMatrix();
      }

   }

   @SideOnly(Side.CLIENT)
   protected void SpawnParticle(World world, Vec3d pos, int color) {
      double X = Reference.random.nextDouble() + pos.x;
      double Y = Reference.random.nextDouble() + pos.y;
      double Z = Reference.random.nextDouble() + pos.z;
      GlStateManager.pushMatrix();
      ParticleGreed effect = new ParticleGreed(world, new Vec3d(X, Y, Z), color, 1.0F, false);
      Minecraft.getMinecraft().effectRenderer.addEffect(effect);
      GlStateManager.popMatrix();
   }

   @SideOnly(Side.CLIENT)
   protected void playSound(EntityLivingBase entity, BlockPos pos, double distance) {
      if (this.clientConfig.Dragon_Growl && entity instanceof EntityPlayer) {
         EntityPlayer player = (EntityPlayer)entity;
         boolean sneaking = this.clientConfig.Dragon_Growl_Sneak.contentEquals("SNEAK") && player.isSneaking();
         boolean standing = this.clientConfig.Dragon_Growl_Sneak.contentEquals("STAND") && !player.isSneaking();
         boolean both = this.clientConfig.Dragon_Growl_Sneak.contentEquals("BOTH");
         float configVolume = (float)this.clientConfig.Dragon_Growl_Volume;
         float volume = configVolume / 100.0F;
         int drH = this.serverConfig.BLOCKS.DR.C001_HD;
         int drV = this.serverConfig.BLOCKS.DR.C00_VD;
         double test = (double)MathHelper.sqrt((float)(drH * drH + drV * drV));
         if (test <= 0.0D) {
            test = 1.0D;
         }

         if (sneaking && !standing || standing && !sneaking || both) {
            float v = 1.0F;

            try {
               v = (float)(1.0D - distance / test);
            } catch (Exception var17) {
               ;
            }

            if (pos != null) {
               player.world.playSound(player, pos, SoundEvents.ENTITY_ENDERDRAGON_GROWL, SoundCategory.PLAYERS, v * volume, 1.0F);
            }
         }
      }

   }

   @SideOnly(Side.CLIENT)
   public String getKey() {
      String kb = ModKeyBindings.DRAGONS_EYE_TARGET.getDisplayName();
      return kb;
   }

   @SideOnly(Side.CLIENT)
   public String getAuxKey() {
      return ModKeyBindings.AUX_KEY.getDisplayName();
   }

   public boolean onKeyPress(Entity entity, boolean Aux) {
      if (TrinketsConfig.SERVER.Items.DRAGON_EYE.oreFinder) {
         int size = TrinketsConfig.SERVER.Items.DRAGON_EYE.BLOCKS.Blocks.length;
         int off = -1;
         int max = size - 1;
         if (!Aux) {
            ++this.targetValue;
         } else {
            --this.targetValue;
         }

         if (this.targetValue >= size) {
            this.targetValue = -1;
         } else if (this.targetValue < -1) {
            this.targetValue = max;
         }

         if (entity instanceof EntityPlayer && entity.world.isRemote) {
            TranslationHelper helper = TranslationHelper.INSTANCE;
            ItemStack stack = new ItemStack(ModItems.trinkets.TrinketDragonsEye);
            EntityPlayer player = (EntityPlayer)entity;
            if (this.targetValue != -1) {
               ConfigHelper.TreasureEntry entry = this.getTreasure();
               String target = this.parseTargetName(entry).trim();
               String entryRegName = entry == null ? "NULL" : entry.getObjectRegistryName();
               String NotFound = helper.formatAddVariables((new TextComponentTranslation(stack.getUnlocalizedName() + ".treasurefinder.notfound", new Object[0])).getFormattedText(), new TranslationHelper.OptionEntry("target", true, entryRegName), new TranslationHelper.OptionEntry("looking", true, helper.toggleCheckTranslation(true)));
               String FoundTarget = helper.formatAddVariables((new TextComponentTranslation(stack.getUnlocalizedName() + ".treasurefinder.on", new Object[0])).getFormattedText(), new TranslationHelper.OptionEntry("target", true, target), new TranslationHelper.OptionEntry("looking", true, helper.toggleCheckTranslation(true)));
               String Message = target.isEmpty() ? NotFound : FoundTarget;
               player.sendStatusMessage(new TextComponentString(Message), true);
            } else {
               player.sendStatusMessage(new TextComponentString(helper.formatAddVariables((new TextComponentTranslation(stack.getUnlocalizedName() + ".treasurefinder.off", new Object[0])).getFormattedText(), new TranslationHelper.OptionEntry("looking", true, helper.toggleCheckTranslation(false)))), true);
            }
         }

         return true;
      } else {
         return false;
      }
   }

   public String parseTargetName(ConfigHelper.TreasureEntry treasure) {
      if (treasure == null) {
         return "";
      } else {
         try {
            String target = treasure.getObjectRegistryName();
            if (treasure.getObjectType() == ConfigHelper.EntryType.OREDICTIONARY) {
               return target.replaceFirst("oreDict:", "").replaceAll("(([ ]?[oO][rR][eE])|([\\[\\]])|([tT][iI][lL][eE][\\.]))", "");
            }

            if (treasure.getObjectType() != ConfigHelper.EntryType.ENTITY) {
               if (treasure.getObjectType() == ConfigHelper.EntryType.MATERIAL) {
                  return "";
               }

               String namespace = treasure.getModID();
               String pathID = treasure.getObjectID();
               boolean isWildSpace = namespace.contentEquals("*");
               boolean isWildPath = pathID.contentEquals("*");
               int meta = treasure.getMeta();
               if (isWildSpace) {
                  if (isWildPath) {
                     return "";
                  }

                  return pathID;
               }

               if (isWildPath) {
                  return namespace + " " + (new TextComponentTranslation("stat.blocksButton", new Object[0])).getFormattedText();
               }

               Item itemTarget = Item.getByNameOrId(target);
               ItemStack parseName = new ItemStack(itemTarget, 1);
               if (itemTarget == null && Block.getBlockFromName(target) != null) {
                  target = Block.getBlockFromName(target).getRegistryName().toString();
               }

               target = parseName.getTextComponent().getUnformattedText();
               if (itemTarget != null && itemTarget.getHasSubtypes() && meta != OreDictionaryCompat.wildcard) {
                  NonNullList<ItemStack> parseMeta = NonNullList.create();
                  itemTarget.getSubItems(CreativeTabs.SEARCH, parseMeta);

                  for(ItemStack t : parseMeta) {
                     if (t.getMetadata() == meta) {
                        parseName = new ItemStack(itemTarget, 1, meta);
                        target = parseName.getTextComponent().getUnformattedText();
                        break;
                     }
                  }
               }

               if (target.equalsIgnoreCase("[air]")) {
                  return "";
               }

               return target.replaceAll("(([ ]?[oO][rR][eE])|([\\[\\]])|([tT][iI][lL][eE][\\.]))", "");
            }

            try {
               ResourceLocation t = new ResourceLocation(target);
               if (EntityList.isRegistered(t)) {
                  return (new TextComponentTranslation("entity." + EntityList.getTranslationName(t) + ".name", new Object[0])).getFormattedText();
               }
            } catch (Exception var13) {
               var13.printStackTrace();
            }
         } catch (Exception var14) {
            var14.printStackTrace();
         }

         return "";
      }
   }

   protected int getColor(String name) {
      return TargetOreType.Color(name);
   }
}
