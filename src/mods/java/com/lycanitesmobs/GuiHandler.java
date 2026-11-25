package com.lycanitesmobs;

import com.lycanitesmobs.client.gui.CreatureInventoryScreen;
import com.lycanitesmobs.client.gui.beastiary.CreaturesBeastiaryScreen;
import com.lycanitesmobs.client.gui.beastiary.ElementsBeastiaryScreen;
import com.lycanitesmobs.client.gui.beastiary.IndexBeastiaryScreen;
import com.lycanitesmobs.client.gui.beastiary.PetsBeastiaryScreen;
import com.lycanitesmobs.client.gui.beastiary.SummoningBeastiaryScreen;
import com.lycanitesmobs.client.gui.overlays.MinionSelectionOverlay;
import com.lycanitesmobs.core.container.CreatureContainer;
import com.lycanitesmobs.core.entity.BaseCreatureEntity;
import com.lycanitesmobs.core.tileentity.TileEntityBase;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.IGuiHandler;

public class GuiHandler implements IGuiHandler {
   public static GuiHandler instance;

   public GuiHandler() {
      instance = this;
   }

   public Object getServerGuiElement(int id, EntityPlayer player, World world, int x, int y, int z) {
      if (id == GuiHandler.GuiType.TILEENTITY.id) {
         TileEntity tileEntity = world.getTileEntity(new BlockPos(x, y, z));
         if (tileEntity instanceof TileEntityBase) {
            return ((TileEntityBase)tileEntity).getGUI(player);
         }
      } else if (id == GuiHandler.GuiType.ENTITY.id) {
         Entity entity = world.getEntityByID(x);
         if (entity instanceof BaseCreatureEntity) {
            return new CreatureContainer((BaseCreatureEntity)entity, player.inventory);
         }
      } else if (id == GuiHandler.GuiType.ITEM.id) {
         ;
      }

      return null;
   }

   public Object getClientGuiElement(int id, EntityPlayer player, World world, int x, int y, int z) {
      if (id == GuiHandler.GuiType.TILEENTITY.id) {
         TileEntity tileEntity = world.getTileEntity(new BlockPos(x, y, z));
         if (tileEntity instanceof TileEntityBase) {
            return ((TileEntityBase)tileEntity).getGUI(player);
         }
      } else if (id == GuiHandler.GuiType.ENTITY.id) {
         Entity entity = world.getEntityByID(x);
         if (entity instanceof BaseCreatureEntity) {
            return new CreatureInventoryScreen((BaseCreatureEntity)entity, player.inventory);
         }
      } else if (id != GuiHandler.GuiType.ITEM.id) {
         if (id == GuiHandler.GuiType.BEASTIARY.id) {
            if (x == GuiHandler.Beastiary.INDEX.id) {
               return new IndexBeastiaryScreen(player);
            }

            if (x == GuiHandler.Beastiary.CREATURES.id) {
               return new CreaturesBeastiaryScreen(player);
            }

            if (x == GuiHandler.Beastiary.PETS.id) {
               return new PetsBeastiaryScreen(player);
            }

            if (x == GuiHandler.Beastiary.SUMMONING.id) {
               return new SummoningBeastiaryScreen(player);
            }

            if (x == GuiHandler.Beastiary.ELEMENTS.id) {
               return new ElementsBeastiaryScreen(player);
            }
         } else if (id == GuiHandler.GuiType.PLAYER.id && x == GuiHandler.PlayerGuiType.MINION_SELECTION.id) {
            return new MinionSelectionOverlay(player);
         }
      }

      return null;
   }

   public static enum Beastiary {
      INDEX((byte)0),
      CREATURES((byte)1),
      PETS((byte)2),
      SUMMONING((byte)3),
      ELEMENTS((byte)4);

      public byte id;

      private Beastiary(byte i) {
         this.id = i;
      }
   }

   public static enum GuiType {
      TILEENTITY((byte)0),
      ENTITY((byte)1),
      ITEM((byte)2),
      BEASTIARY((byte)3),
      PLAYER((byte)4);

      public byte id;

      private GuiType(byte i) {
         this.id = i;
      }
   }

   public static enum PlayerGuiType {
      MINION_SELECTION((byte)0);

      public byte id;

      private PlayerGuiType(byte i) {
         this.id = i;
      }
   }
}
