package com.yyon.grapplinghook;

import com.yyon.grapplinghook.blocks.TileEntityGrappleModifier;
import com.yyon.grapplinghook.controllers.airfrictionController;
import com.yyon.grapplinghook.controllers.grappleController;
import com.yyon.grapplinghook.controllers.repelController;
import com.yyon.grapplinghook.entities.RenderGrappleArrow;
import com.yyon.grapplinghook.entities.grappleArrow;
import com.yyon.grapplinghook.items.KeypressItem;
import com.yyon.grapplinghook.items.grappleBow;
import com.yyon.grapplinghook.items.launcherItem;
import com.yyon.grapplinghook.items.repeller;
import java.util.ArrayList;
import java.util.ConcurrentModificationException;
import java.util.HashMap;
import java.util.Map;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.MovingSound;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.client.audio.ISound.AttenuationType;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.renderer.ItemMeshDefinition;
import net.minecraft.client.renderer.block.model.ModelBakery;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.MovementInput;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.RayTraceResult.Type;
import net.minecraft.world.World;
import net.minecraftforge.client.event.InputUpdateEvent;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.event.EntityViewRenderEvent.CameraSetup;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.event.world.BlockEvent.BreakEvent;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.client.registry.IRenderFactory;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.InputEvent.KeyInputEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.ClientTickEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.Phase;
import net.minecraftforge.fml.common.network.FMLNetworkEvent.ClientDisconnectionFromServerEvent;

public class ClientProxyClass extends CommonProxyClass {
   public boolean[] prevkeys = new boolean[]{false, false, false, false, false};
   public HashMap<Integer, Long> enderlaunchtimer = new HashMap();
   public double rocketFuel = 1.0D;
   public double rocketIncreaseTick = 0.0D;
   public double rocketDecreaseTick = 0.0D;
   public static long prev_rope_jump_time = 0L;
   public int tickswallrunning = 0;
   public ModelResourceLocation grapplinghookloc = new ModelResourceLocation("grapplemod:grapplinghook", "inventory");
   public ModelResourceLocation hookshotloc = new ModelResourceLocation("grapplemod:hookshot", "inventory");
   public ModelResourceLocation smarthookloc = new ModelResourceLocation("grapplemod:smarthook", "inventory");
   public ModelResourceLocation smarthookropeloc = new ModelResourceLocation("grapplemod:smarthookrope", "inventory");
   public ModelResourceLocation enderhookloc = new ModelResourceLocation("grapplemod:enderhook", "inventory");
   public ModelResourceLocation magnetbowloc = new ModelResourceLocation("grapplemod:magnetbow", "inventory");
   public ModelResourceLocation ropeloc = new ModelResourceLocation("grapplemod:rope", "inventory");
   public ModelResourceLocation hookshotropeloc = new ModelResourceLocation("grapplemod:hookshotrope", "inventory");
   public ModelResourceLocation repellerloc = new ModelResourceLocation("grapplemod:repeller", "inventory");
   public ModelResourceLocation repelleronloc = new ModelResourceLocation("grapplemod:repelleron", "inventory");
   public ModelResourceLocation multihookloc = new ModelResourceLocation("grapplemod:multihook", "inventory");
   public ModelResourceLocation multihookropeloc = new ModelResourceLocation("grapplemod:multihookrope", "inventory");
   public ModelResourceLocation odmloc = new ModelResourceLocation("grapplemod:odm", "inventory");
   public ModelResourceLocation odmropeloc = new ModelResourceLocation("grapplemod:odmrope", "inventory");
   public ModelResourceLocation rocketloc = new ModelResourceLocation("grapplemod:rocket", "inventory");
   public ModelResourceLocation rocketropeloc = new ModelResourceLocation("grapplemod:rocketrope", "inventory");
   public ResourceLocation doubleJumpSoundLoc = new ResourceLocation("grapplemod", "doublejump");
   public ResourceLocation slideSoundLoc = new ResourceLocation("grapplemod", "slide");
   public static ArrayList<KeyBinding> keyBindings = new ArrayList();
   public static KeyBinding key_boththrow = createkeybinding("key.boththrow.desc", -99, "key.grapplemod.category");
   public static KeyBinding key_leftthrow = createkeybinding("key.leftthrow.desc", 0, "key.grapplemod.category");
   public static KeyBinding key_rightthrow = createkeybinding("key.rightthrow.desc", 0, "key.grapplemod.category");
   public static KeyBinding key_motoronoff = createkeybinding("key.motoronoff.desc", 42, "key.grapplemod.category");
   public static KeyBinding key_jumpanddetach = createkeybinding("key.jumpanddetach.desc", 57, "key.grapplemod.category");
   public static KeyBinding key_slow = createkeybinding("key.slow.desc", 42, "key.grapplemod.category");
   public static KeyBinding key_climb = createkeybinding("key.climb.desc", 42, "key.grapplemod.category");
   public static KeyBinding key_climbup = createkeybinding("key.climbup.desc", 0, "key.grapplemod.category");
   public static KeyBinding key_climbdown = createkeybinding("key.climbdown.desc", 0, "key.grapplemod.category");
   public static KeyBinding key_enderlaunch = createkeybinding("key.enderlaunch.desc", -100, "key.grapplemod.category");
   public static KeyBinding key_rocket = createkeybinding("key.rocket.desc", -100, "key.grapplemod.category");
   public static KeyBinding key_slide = createkeybinding("key.slide.desc", 42, "key.grapplemod.category");
   public crosshairRenderer crosshairrenderer;
   boolean prevjumpbutton = false;
   int tickssincelastonground = 0;
   boolean alreadyuseddoublejump = false;
   public float currentCameraTilt = 0.0F;

   public ClientProxyClass() {
   }

   public void preInit(FMLPreInitializationEvent event) {
      super.preInit(event);
      RenderingRegistry.registerEntityRenderingHandler(grappleArrow.class, new IRenderFactory<grappleArrow>() {
         public Render<? super grappleArrow> createRenderFor(RenderManager manager) {
            return new RenderGrappleArrow<grappleArrow>(manager, Items.IRON_PICKAXE, Minecraft.getMinecraft().getRenderItem());
         }
      });
      ModelResourceLocation itemModelResourceLocation = new ModelResourceLocation("grapplemod:block_grapple_modifier", "inventory");
      int DEFAULT_ITEM_SUBTYPE = 0;
      ModelLoader.setCustomModelResourceLocation(grapplemod.itemBlockGrappleModifier, 0, itemModelResourceLocation);
   }

   private void setgrapplebowtextures(Item item, final ModelResourceLocation notinusetexture, final ModelResourceLocation inusetexture) {
      ModelLoader.setCustomMeshDefinition(item, new ItemMeshDefinition() {
         public ModelResourceLocation getModelLocation(ItemStack stack) {
            return ClientProxyClass.isactive(stack) ? inusetexture : notinusetexture;
         }
      });
      ModelBakery.registerItemVariants(item, new ResourceLocation[]{notinusetexture});
      ModelBakery.registerItemVariants(item, new ResourceLocation[]{inusetexture});
   }

   private void registerItemModels() {
      this.registerItemModel(grapplemod.launcheritem);
      this.registerItemModel(grapplemod.longfallboots);
      this.setgrapplebowtextures(grapplemod.repelleritem, this.repellerloc, this.repelleronloc);
      this.registerItemModel(grapplemod.baseupgradeitem);
      this.registerItemModel(grapplemod.doubleupgradeitem);
      this.registerItemModel(grapplemod.forcefieldupgradeitem);
      this.registerItemModel(grapplemod.magnetupgradeitem);
      this.registerItemModel(grapplemod.motorupgradeitem);
      this.registerItemModel(grapplemod.ropeupgradeitem);
      this.registerItemModel(grapplemod.staffupgradeitem);
      this.registerItemModel(grapplemod.swingupgradeitem);
      this.registerItemModel(grapplemod.throwupgradeitem);
      this.registerItemModel(grapplemod.limitsupgradeitem);
      this.registerItemModel(grapplemod.rocketupgradeitem);
      ItemMeshDefinition itemmeshdefinition = new ItemMeshDefinition() {
         public ModelResourceLocation getModelLocation(ItemStack stack) {
            boolean active = !ClientProxyClass.isactive(stack);
            if (stack.hasTagCompound()) {
               NBTTagCompound compound = stack.getTagCompound();
               if (compound.getBoolean("rocket")) {
                  if (compound.getBoolean("doublehook")) {
                     return active ? ClientProxyClass.this.odmloc : ClientProxyClass.this.odmropeloc;
                  }

                  return active ? ClientProxyClass.this.rocketloc : ClientProxyClass.this.rocketropeloc;
               }

               if (compound.getBoolean("motor")) {
                  if (compound.getBoolean("doublehook")) {
                     return active ? ClientProxyClass.this.multihookloc : ClientProxyClass.this.multihookropeloc;
                  } else if (compound.getBoolean("smartmotor")) {
                     return active ? ClientProxyClass.this.smarthookloc : ClientProxyClass.this.smarthookropeloc;
                  } else {
                     return active ? ClientProxyClass.this.hookshotloc : ClientProxyClass.this.hookshotropeloc;
                  }
               }

               if (compound.getBoolean("enderstaff")) {
                  return active ? ClientProxyClass.this.enderhookloc : ClientProxyClass.this.ropeloc;
               }

               if (compound.getBoolean("repel") || compound.getBoolean("attract")) {
                  return active ? ClientProxyClass.this.magnetbowloc : ClientProxyClass.this.ropeloc;
               }
            }

            return active ? ClientProxyClass.this.grapplinghookloc : ClientProxyClass.this.ropeloc;
         }
      };
      ModelLoader.setCustomMeshDefinition(grapplemod.grapplebowitem, itemmeshdefinition);
      ModelLoader.setCustomMeshDefinition(grapplemod.motorhookitem, itemmeshdefinition);
      ModelLoader.setCustomMeshDefinition(grapplemod.smarthookitem, itemmeshdefinition);
      ModelLoader.setCustomMeshDefinition(grapplemod.doublemotorhookitem, itemmeshdefinition);
      ModelLoader.setCustomMeshDefinition(grapplemod.rocketdoublemotorhookitem, itemmeshdefinition);
      ModelLoader.setCustomMeshDefinition(grapplemod.enderhookitem, itemmeshdefinition);
      ModelLoader.setCustomMeshDefinition(grapplemod.magnethookitem, itemmeshdefinition);
      ModelLoader.setCustomMeshDefinition(grapplemod.rockethookitem, itemmeshdefinition);

      for(ResourceLocation loc : new ResourceLocation[]{this.multihookloc, this.multihookropeloc, this.smarthookloc, this.smarthookropeloc, this.hookshotloc, this.hookshotropeloc, this.enderhookloc, this.magnetbowloc, this.grapplinghookloc, this.ropeloc, this.odmloc, this.odmropeloc, this.rocketloc, this.rocketropeloc}) {
         ModelBakery.registerItemVariants(grapplemod.grapplebowitem, new ResourceLocation[]{loc});
         ModelBakery.registerItemVariants(grapplemod.motorhookitem, new ResourceLocation[]{loc});
         ModelBakery.registerItemVariants(grapplemod.smarthookitem, new ResourceLocation[]{loc});
         ModelBakery.registerItemVariants(grapplemod.doublemotorhookitem, new ResourceLocation[]{loc});
         ModelBakery.registerItemVariants(grapplemod.rocketdoublemotorhookitem, new ResourceLocation[]{loc});
         ModelBakery.registerItemVariants(grapplemod.enderhookitem, new ResourceLocation[]{loc});
         ModelBakery.registerItemVariants(grapplemod.magnethookitem, new ResourceLocation[]{loc});
         ModelBakery.registerItemVariants(grapplemod.rockethookitem, new ResourceLocation[]{loc});
      }

   }

   @SubscribeEvent
   public void registerAllModels(ModelRegistryEvent event) {
      System.out.println("REGISTERING ALL MODELS!!!!!!!!!!!!!");
      this.registerItemModels();
   }

   private void registerItemModel(Item item) {
      this.registerItemModel(item, item.getRegistryName().toString());
   }

   private void registerItemModel(Item item, String modelLocation) {
      ModelResourceLocation fullModelLocation = new ModelResourceLocation(modelLocation, "inventory");
      ModelLoader.setCustomModelResourceLocation(item, 0, fullModelLocation);
   }

   public static KeyBinding createkeybinding(String desc, int key, String category) {
      KeyBinding k = new NonConflictingKeyBinding(desc, key, category);
      keyBindings.add(k);
      return k;
   }

   public void init(FMLInitializationEvent event, grapplemod grappleModInst) {
      super.init(event, grappleModInst);

      for(int i = 0; i < keyBindings.size(); ++i) {
         ClientRegistry.registerKeyBinding((KeyBinding)keyBindings.get(i));
      }

   }

   public void postInit(FMLPostInitializationEvent event) {
      super.postInit(event);
      this.crosshairrenderer = new crosshairRenderer();
   }

   public ItemStack getKeypressStack(EntityPlayer player) {
      if (player != null) {
         ItemStack stack = player.getHeldItemMainhand();
         if (stack != null) {
            Item item = stack.getItem();
            if (item instanceof KeypressItem) {
               return stack;
            }
         }

         stack = player.getHeldItemOffhand();
         if (stack != null) {
            Item item = stack.getItem();
            if (item instanceof KeypressItem) {
               return stack;
            }
         }
      }

      return null;
   }

   public boolean isLookingAtModifierBlock(EntityPlayer player) {
      RayTraceResult raytraceresult = Minecraft.getMinecraft().objectMouseOver;
      if (raytraceresult != null && raytraceresult.typeOfHit == Type.BLOCK) {
         BlockPos pos = raytraceresult.getBlockPos();
         IBlockState state = player.world.getBlockState(pos);
         return state.getBlock() == grapplemod.blockGrappleModifier;
      } else {
         return false;
      }
   }

   @SubscribeEvent
   public void onClientTick(ClientTickEvent event) {
      EntityPlayer player = Minecraft.getMinecraft().player;
      if (player != null && (!Minecraft.getMinecraft().isGamePaused() || !Minecraft.getMinecraft().isSingleplayer())) {
         if (player.onGround || grapplemod.controllers.containsKey(Integer.valueOf(player.getEntityId())) && ((grappleController)grapplemod.controllers.get(Integer.valueOf(player.getEntityId()))).controllerid == grapplemod.GRAPPLEID) {
            this.tickswallrunning = 0;
         }

         if (this.iswallrunning(player, vec.motionvec(player))) {
            if (!grapplemod.controllers.containsKey(Integer.valueOf(player.getEntityId()))) {
               grappleController controller = this.createControl(grapplemod.AIRID, -1, player.getEntityId(), player.world, new vec(0.0D, 0.0D, 0.0D), (BlockPos)null, (GrappleCustomization)null);
               if (controller.getwalldirection() == null) {
                  controller.unattach();
               }
            }

            if (grapplemod.controllers.containsKey(Integer.valueOf(player.getEntityId()))) {
               this.tickssincelastonground = 0;
               this.alreadyuseddoublejump = false;
            }
         }

         this.checkdoublejump();
         this.checkslide(player);
         this.rocketFuel += this.rocketIncreaseTick;

         try {
            for(grappleController controller : grapplemod.controllers.values()) {
               controller.doClientTick();
            }
         } catch (ConcurrentModificationException var9) {
            System.out.println("ConcurrentModificationException caught");
         }

         if (this.rocketFuel > 1.0D) {
            this.rocketFuel = 1.0D;
         }

         if (Minecraft.getMinecraft().currentScreen == null) {
            boolean[] keys = new boolean[]{key_enderlaunch.isKeyDown(), key_leftthrow.isKeyDown(), key_rightthrow.isKeyDown(), key_boththrow.isKeyDown(), key_rocket.isKeyDown()};

            for(int i = 0; i < keys.length; ++i) {
               boolean iskeydown = keys[i];
               boolean prevkey = this.prevkeys[i];
               if (iskeydown != prevkey) {
                  KeypressItem.Keys key = KeypressItem.Keys.values()[i];
                  ItemStack stack = this.getKeypressStack(player);
                  if (stack != null && !this.isLookingAtModifierBlock(player)) {
                     if (iskeydown) {
                        ((KeypressItem)stack.getItem()).onCustomKeyDown(stack, player, key, true);
                     } else {
                        ((KeypressItem)stack.getItem()).onCustomKeyUp(stack, player, key, true);
                     }
                  }
               }

               this.prevkeys[i] = iskeydown;
            }
         }

         if (player.onGround && this.enderlaunchtimer.containsKey(Integer.valueOf(player.getEntityId()))) {
            long timer = player.world.getTotalWorldTime() - ((Long)this.enderlaunchtimer.get(Integer.valueOf(player.getEntityId()))).longValue();
            if (timer > 10L) {
               this.resetlaunchertime(player.getEntityId());
            }
         }
      }

   }

   private void checkslide(EntityPlayer player) {
      if (key_slide.isKeyDown() && !grapplemod.controllers.containsKey(Integer.valueOf(player.getEntityId())) && this.issliding(player, vec.motionvec(player))) {
         this.createControl(grapplemod.AIRID, -1, player.getEntityId(), player.world, new vec(0.0D, 0.0D, 0.0D), (BlockPos)null, (GrappleCustomization)null);
      }

   }

   public void startrocket(EntityPlayer player, GrappleCustomization custom) {
      if (custom.rocket) {
         grappleController controller;
         if (!grapplemod.controllers.containsKey(Integer.valueOf(player.getEntityId()))) {
            controller = this.createControl(grapplemod.AIRID, -1, player.getEntityId(), player.world, new vec(0.0D, 0.0D, 0.0D), (BlockPos)null, custom);
         } else {
            controller = (grappleController)grapplemod.controllers.get(Integer.valueOf(player.getEntityId()));
            if (controller.custom == null || !controller.custom.rocket) {
               if (controller.custom == null) {
                  controller.custom = custom;
               }

               controller.custom.rocket = true;
               controller.custom.rocket_active_time = custom.rocket_active_time;
               controller.custom.rocket_force = custom.rocket_force;
               controller.custom.rocket_refuel_ratio = custom.rocket_refuel_ratio;
               this.updateRocketRegen(custom.rocket_active_time, custom.rocket_refuel_ratio);
            }
         }

         ClientProxyClass.RocketSound sound = new ClientProxyClass.RocketSound(controller, new SoundEvent(new ResourceLocation("grapplemod", "rocket")), SoundCategory.PLAYERS);
         Minecraft.getMinecraft().getSoundHandler().playSound(sound);
      }
   }

   public void launchplayer(EntityPlayer player) {
      long prevtime;
      if (this.enderlaunchtimer.containsKey(Integer.valueOf(player.getEntityId()))) {
         prevtime = ((Long)this.enderlaunchtimer.get(Integer.valueOf(player.getEntityId()))).longValue();
      } else {
         prevtime = 0L;
      }

      long timer = player.world.getTotalWorldTime() - prevtime;
      if (timer > (long)GrappleConfig.getconf().ender_staff_recharge && (player.getHeldItemMainhand() != null && (player.getHeldItemMainhand().getItem() instanceof launcherItem || player.getHeldItemMainhand().getItem() instanceof grappleBow) || player.getHeldItemOffhand() != null && (player.getHeldItemOffhand().getItem() instanceof launcherItem || player.getHeldItemOffhand().getItem() instanceof grappleBow))) {
         this.enderlaunchtimer.put(Integer.valueOf(player.getEntityId()), Long.valueOf(player.world.getTotalWorldTime()));
         vec facing = new vec(player.getLookVec());
         GrappleCustomization custom = null;
         if (player.getHeldItemMainhand().getItem() instanceof grappleBow) {
            custom = ((grappleBow)player.getHeldItemMainhand().getItem()).getCustomization(player.getHeldItemMainhand());
         } else if (player.getHeldItemOffhand().getItem() instanceof grappleBow) {
            custom = ((grappleBow)player.getHeldItemOffhand().getItem()).getCustomization(player.getHeldItemOffhand());
         }

         if (!grapplemod.controllers.containsKey(Integer.valueOf(player.getEntityId()))) {
            player.onGround = false;
            this.createControl(grapplemod.AIRID, -1, player.getEntityId(), player.world, new vec(0.0D, 0.0D, 0.0D), (BlockPos)null, custom);
         }

         facing.mult_ip(GrappleConfig.getconf().ender_staff_strength);
         grapplemod.receiveEnderLaunch(player.getEntityId(), facing.x, facing.y, facing.z);
         this.playSound(new ResourceLocation("grapplemod", "enderstaff"), GrappleConfig.client_options.enderstaff_sound_volume * 0.5F);
      }

   }

   public void resetlaunchertime(int playerid) {
      if (this.enderlaunchtimer.containsKey(Integer.valueOf(playerid))) {
         this.enderlaunchtimer.put(Integer.valueOf(playerid), Long.valueOf(0L));
      }

   }

   public boolean isSneaking(Entity entity) {
      return entity == Minecraft.getMinecraft().player ? GameSettings.isKeyDown(Minecraft.getMinecraft().gameSettings.keyBindSneak) : entity.isSneaking();
   }

   public void blockbreak(BreakEvent event) {
      if (event.getPos() != null && grapplemod.controllerpos.containsKey(event.getPos())) {
         grappleController control = (grappleController)grapplemod.controllerpos.get(event.getPos());
         control.unattach();
         grapplemod.controllerpos.remove(event.getPos());
      }

   }

   public String getkeyname(CommonProxyClass.keys keyenum) {
      KeyBinding binding = null;
      GameSettings gs = Minecraft.getMinecraft().gameSettings;
      if (keyenum == CommonProxyClass.keys.keyBindAttack) {
         binding = gs.keyBindAttack;
      } else if (keyenum == CommonProxyClass.keys.keyBindBack) {
         binding = gs.keyBindBack;
      } else if (keyenum == CommonProxyClass.keys.keyBindForward) {
         binding = gs.keyBindForward;
      } else if (keyenum == CommonProxyClass.keys.keyBindJump) {
         binding = gs.keyBindJump;
      } else if (keyenum == CommonProxyClass.keys.keyBindLeft) {
         binding = gs.keyBindLeft;
      } else if (keyenum == CommonProxyClass.keys.keyBindRight) {
         binding = gs.keyBindRight;
      } else if (keyenum == CommonProxyClass.keys.keyBindSneak) {
         binding = gs.keyBindSneak;
      } else if (keyenum == CommonProxyClass.keys.keyBindUseItem) {
         binding = gs.keyBindUseItem;
      }

      if (binding == null) {
         return "";
      } else {
         String displayname = binding.getDisplayName();
         if (displayname.equals("Button 1")) {
            return "Left Click";
         } else {
            return displayname.equals("Button 2") ? "Right Click" : displayname;
         }
      }
   }

   public static boolean isactive(ItemStack stack) {
      EntityPlayer p = Minecraft.getMinecraft().player;
      int entityid = p.getEntityId();
      if (grapplemod.controllers.containsKey(Integer.valueOf(entityid))) {
         Item item = stack.getItem();
         grappleController controller = (grappleController)grapplemod.controllers.get(Integer.valueOf(entityid));
         if (item instanceof grappleBow && controller.controllerid == grapplemod.GRAPPLEID) {
            return true;
         }

         if (item.getClass() == repeller.class && controller.controllerid == grapplemod.REPELID) {
            return true;
         }
      }

      return false;
   }

   public void openModifierScreen(TileEntityGrappleModifier tileent) {
      Minecraft.getMinecraft().displayGuiScreen(new GuiModifier(tileent));
   }

   @SubscribeEvent
   public void onPlayerLoggedOutEvent(ClientDisconnectionFromServerEvent e) {
      System.out.println("deleting server options");
      GrappleConfig.setserveroptions((GrappleConfig.Config)null);
   }

   @SubscribeEvent
   public void clientTick(ClientTickEvent event) {
      if (event.phase == Phase.END) {
         ;
      }

   }

   public String localize(String string) {
      return I18n.format(string, new Object[0]);
   }

   public void updateRocketRegen(double rocket_active_time, double rocket_refuel_ratio) {
      this.rocketDecreaseTick = 0.025D / rocket_active_time;
      this.rocketIncreaseTick = 0.025D / rocket_active_time / rocket_refuel_ratio;
   }

   public double getRocketFunctioning() {
      this.rocketFuel -= this.rocketIncreaseTick;
      this.rocketFuel -= this.rocketDecreaseTick;
      if (this.rocketFuel >= 0.0D) {
         return 1.0D;
      } else {
         this.rocketFuel = 0.0D;
         return this.rocketIncreaseTick / this.rocketDecreaseTick / 2.0D;
      }
   }

   public boolean iswallrunning(Entity entity, vec motion) {
      if (entity.collidedHorizontally && !entity.onGround && !entity.isSneaking()) {
         if (entity instanceof EntityLivingBase && ((EntityLivingBase)entity).isOnLadder()) {
            return false;
         }

         for(ItemStack stack : entity.getArmorInventoryList()) {
            if (stack != null) {
               Map<Enchantment, Integer> enchantments = EnchantmentHelper.getEnchantments(stack);
               if (enchantments.containsKey(grapplemod.wallrunenchantment)) {
                  if (((Integer)enchantments.get(grapplemod.wallrunenchantment)).intValue() >= 1 && !key_jumpanddetach.isKeyDown() && !Minecraft.getMinecraft().gameSettings.keyBindJump.isKeyDown()) {
                     RayTraceResult raytraceresult = entity.world.rayTraceBlocks(entity.getPositionVector(), vec.positionvec(entity).add(new vec(0.0D, -1.0D, 0.0D)).toVec3d(), false, true, false);
                     if (raytraceresult == null || raytraceresult.typeOfHit != Type.BLOCK) {
                        double current_speed = Math.sqrt(Math.pow(motion.x, 2.0D) + Math.pow(motion.z, 2.0D));
                        if (current_speed >= GrappleConfig.getconf().wallrun_min_speed) {
                           return true;
                        }
                     }
                  }
                  break;
               }
            }
         }
      }

      return false;
   }

   public void checkdoublejump() {
      EntityPlayer player = Minecraft.getMinecraft().player;
      if (player.onGround) {
         this.tickssincelastonground = 0;
         this.alreadyuseddoublejump = false;
      } else {
         ++this.tickssincelastonground;
      }

      boolean isjumpbuttondown = Minecraft.getMinecraft().gameSettings.keyBindJump.isKeyDown();
      if (isjumpbuttondown && !this.prevjumpbutton && !player.isInWater() && !player.isInLava() && this.tickssincelastonground > 3 && !this.alreadyuseddoublejump && this.wearingdoublejumpenchant(player)) {
         if (!grapplemod.controllers.containsKey(Integer.valueOf(player.getEntityId()))) {
            this.createControl(grapplemod.AIRID, -1, player.getEntityId(), player.world, new vec(0.0D, 0.0D, 0.0D), (BlockPos)null, (GrappleCustomization)null);
         }

         grappleController controller = (grappleController)grapplemod.controllers.get(Integer.valueOf(player.getEntityId()));
         if (controller instanceof airfrictionController) {
            this.alreadyuseddoublejump = true;
            controller.doublejump();
         }

         this.playDoubleJumpSound(controller.entity);
      }

      this.prevjumpbutton = isjumpbuttondown;
   }

   public boolean wearingdoublejumpenchant(Entity entity) {
      if (entity instanceof EntityPlayer && ((EntityPlayer)entity).capabilities.isFlying) {
         return false;
      } else {
         for(ItemStack stack : entity.getArmorInventoryList()) {
            if (stack != null) {
               Map<Enchantment, Integer> enchantments = EnchantmentHelper.getEnchantments(stack);
               if (enchantments.containsKey(grapplemod.doublejumpenchantment) && ((Integer)enchantments.get(grapplemod.doublejumpenchantment)).intValue() >= 1) {
                  return true;
               }
            }
         }

         return false;
      }
   }

   public static boolean isWearingSlidingEnchant(Entity entity) {
      for(ItemStack stack : entity.getArmorInventoryList()) {
         if (stack != null) {
            Map<Enchantment, Integer> enchantments = EnchantmentHelper.getEnchantments(stack);
            if (enchantments.containsKey(grapplemod.slidingenchantment) && ((Integer)enchantments.get(grapplemod.slidingenchantment)).intValue() >= 1) {
               return true;
            }
         }
      }

      return false;
   }

   public boolean issliding(Entity entity, vec motion) {
      if (!entity.isInWater() && !entity.isInLava()) {
         if (entity.onGround && key_slide.isKeyDown() && isWearingSlidingEnchant(entity)) {
            boolean was_sliding = false;
            int id = entity.getEntityId();
            if (grapplemod.controllers.containsKey(Integer.valueOf(id))) {
               grappleController controller = (grappleController)grapplemod.controllers.get(Integer.valueOf(id));
               if (controller instanceof airfrictionController) {
                  airfrictionController afc = (airfrictionController)controller;
                  if (afc.was_sliding) {
                     was_sliding = true;
                  }
               }
            }

            double speed = motion.removealong(new vec(0.0D, 1.0D, 0.0D)).length();
            if (speed > GrappleConfig.getconf().sliding_end_min_speed && (was_sliding || speed > GrappleConfig.getconf().sliding_min_speed)) {
               return true;
            }
         }

         return false;
      } else {
         return false;
      }
   }

   @SubscribeEvent(
      priority = EventPriority.LOW
   )
   public void onKeyInputEvent(KeyInputEvent event) {
      EntityPlayer player = Minecraft.getMinecraft().player;
      grappleController controller = null;
      if (grapplemod.controllers.containsKey(Integer.valueOf(player.getEntityId()))) {
         controller = (grappleController)grapplemod.controllers.get(Integer.valueOf(player.getEntityId()));
      }

      if (Minecraft.getMinecraft().gameSettings.keyBindJump.isKeyDown() && controller != null && controller instanceof airfrictionController && ((airfrictionController)controller).was_sliding) {
         controller.slidingJump();
      }

      this.checkslide(Minecraft.getMinecraft().player);
   }

   @SubscribeEvent
   public void onInputUpdate(InputUpdateEvent event) {
      int id = Minecraft.getMinecraft().player.getEntityId();
      if (grapplemod.controllers.containsKey(Integer.valueOf(id))) {
         MovementInput input = event.getMovementInput();
         grappleController control = (grappleController)grapplemod.controllers.get(Integer.valueOf(id));
         control.receivePlayerMovementMessage(input.moveStrafe, input.moveForward, input.jump, input.sneak);
         boolean overrideMovement = true;
         if (Minecraft.getMinecraft().player.onGround && !(control instanceof airfrictionController) && !(control instanceof repelController)) {
            overrideMovement = false;
         }

         if (overrideMovement) {
            input.jump = false;
            input.backKeyDown = false;
            input.forwardKeyDown = false;
            input.leftKeyDown = false;
            input.rightKeyDown = false;
            input.moveForward = 0.0F;
            input.moveStrafe = 0.0F;
         }
      }

   }

   @SubscribeEvent
   public void CameraSetup(CameraSetup event) {
      EntityPlayer player = Minecraft.getMinecraft().player;
      int id = player.getEntityId();
      int targetCameraTilt = 0;
      if (grapplemod.controllers.containsKey(Integer.valueOf(id))) {
         grappleController controller = (grappleController)grapplemod.controllers.get(Integer.valueOf(id));
         if (controller instanceof airfrictionController) {
            airfrictionController afcontroller = (airfrictionController)controller;
            if (afcontroller.was_wallrunning) {
               vec walldirection = afcontroller.getwalldirection();
               if (walldirection != null) {
                  vec lookdirection = new vec(player.getLookVec());
                  int dir = lookdirection.cross(walldirection).y > 0.0D ? 1 : -1;
                  targetCameraTilt = dir;
               }
            }
         }
      }

      if (this.currentCameraTilt != (float)targetCameraTilt) {
         float cameraDiff = (float)targetCameraTilt - this.currentCameraTilt;
         if (cameraDiff != 0.0F) {
            float anim_s = GrappleConfig.client_options.wallrun_camera_animation_s;
            float speed = anim_s == 0.0F ? 9999.0F : 1.0F / (anim_s * 20.0F);
            if (speed > Math.abs(cameraDiff)) {
               this.currentCameraTilt = (float)targetCameraTilt;
            } else {
               this.currentCameraTilt += speed * (float)(cameraDiff > 0.0F ? 1 : -1);
            }
         }
      }

      if (this.currentCameraTilt != 0.0F) {
         event.setRoll(event.getRoll() + this.currentCameraTilt * GrappleConfig.client_options.wallrun_camera_tilt_degrees);
      }

   }

   public grappleController createControl(int id, int arrowid, int entityid, World world, vec pos, BlockPos blockpos, GrappleCustomization custom) {
      grappleArrow arrow = null;
      Entity arrowentity = world.getEntityByID(arrowid);
      if (arrowentity != null && arrowentity instanceof grappleArrow) {
         arrow = (grappleArrow)arrowentity;
      }

      boolean multi = custom != null && custom.doublehook;
      grappleController currentcontroller = (grappleController)grapplemod.controllers.get(Integer.valueOf(entityid));
      if (currentcontroller != null && (!multi || currentcontroller.custom == null || !currentcontroller.custom.doublehook)) {
         currentcontroller.unattach();
      }

      grappleController control = null;
      if (id == grapplemod.GRAPPLEID) {
         if (!multi) {
            control = new grappleController(arrowid, entityid, world, pos, id, custom);
         } else {
            control = (grappleController)grapplemod.controllers.get(Integer.valueOf(entityid));
            boolean created = false;
            if (control != null && control.getClass().equals(grappleController.class) && control.custom.doublehook && arrow != null && arrow instanceof grappleArrow) {
               grappleArrow multiarrow = (grappleArrow)arrowentity;
               created = true;
               control.addArrow(multiarrow);
            }

            if (!created) {
               control = new grappleController(arrowid, entityid, world, pos, id, custom);
            }
         }
      } else if (id == grapplemod.REPELID) {
         control = new repelController(arrowid, entityid, world, pos, id);
      } else {
         if (id != grapplemod.AIRID) {
            return null;
         }

         control = new airfrictionController(arrowid, entityid, world, pos, id, custom);
      }

      if (blockpos != null && control != null) {
         grapplemod.controllerpos.put(blockpos, control);
      }

      Entity e = world.getEntityByID(entityid);
      if (e != null && e instanceof EntityPlayerSP) {
         EntityPlayerSP p = (EntityPlayerSP)e;
         control.receivePlayerMovementMessage(p.movementInput.moveStrafe, p.movementInput.moveForward, p.movementInput.jump, p.movementInput.sneak);
      }

      return control;
   }

   public void playSlideSound(Entity entity) {
      this.playSound(this.slideSoundLoc, GrappleConfig.client_options.slide_sound_volume);
   }

   private void playDoubleJumpSound(Entity entity) {
      this.playSound(this.doubleJumpSoundLoc, GrappleConfig.client_options.doublejump_sound_volume * 0.7F);
   }

   public void playWallrunJumpSound(Entity entity) {
      this.playSound(this.doubleJumpSoundLoc, GrappleConfig.client_options.wallrunjump_sound_volume * 0.7F);
   }

   public void playSound(ResourceLocation loc, float volume) {
      EntityPlayer player = Minecraft.getMinecraft().player;
      Minecraft.getMinecraft().getSoundHandler().playSound(new PositionedSoundRecord(loc, SoundCategory.PLAYERS, volume, 1.0F, false, 0, AttenuationType.NONE, (float)player.posX, (float)player.posY, (float)player.posZ));
   }

   public int getWallrunTicks() {
      return this.tickswallrunning;
   }

   public void setWallrunTicks(int newWallrunTicks) {
      this.tickswallrunning = newWallrunTicks;
   }

   public static class RocketSound extends MovingSound {
      grappleController controller;
      boolean stopping = false;
      public float changespeed;

      protected RocketSound(grappleController controller, SoundEvent p_i46532_1_, SoundCategory p_i46532_2_) {
         super(p_i46532_1_, p_i46532_2_);
         this.repeat = true;
         this.controller = controller;
         controller.rocket_key = true;
         controller.rocket_on = 1.0D;
         this.changespeed = GrappleConfig.client_options.rocket_sound_volume * 0.5F * 0.2F;
         this.volume = this.changespeed;
         this.repeatDelay = 0;
         this.attenuationType = AttenuationType.NONE;
      }

      public void update() {
         if (!this.controller.rocket_key || !this.controller.attached) {
            this.stopping = true;
         }

         float targetvolume = (float)this.controller.rocket_on * GrappleConfig.client_options.rocket_sound_volume * 0.5F;
         if (this.stopping) {
            targetvolume = 0.0F;
         }

         float diff = Math.abs(targetvolume - this.volume);
         if (diff > this.changespeed) {
            this.volume += this.changespeed * (float)(this.volume > targetvolume ? -1 : 1);
         } else {
            this.volume = targetvolume;
         }

         if (this.volume == 0.0F && this.stopping) {
            this.donePlaying = true;
         }

         this.xPosF = (float)this.controller.entity.posX;
         this.yPosF = (float)this.controller.entity.posY;
         this.zPosF = (float)this.controller.entity.posZ;
      }
   }
}
