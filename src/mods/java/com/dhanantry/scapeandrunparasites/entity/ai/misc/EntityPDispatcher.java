package com.dhanantry.scapeandrunparasites.entity.ai.misc;

import com.dhanantry.scapeandrunparasites.util.config.SRPConfigMobs;
import java.util.ArrayList;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityList;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.world.World;

public abstract class EntityPDispatcher extends EntityPStationaryArchitect {
   protected ArrayList<String> mobNameId = new ArrayList();
   protected int crude;
   protected int inf;
   protected int mangler;
   protected int flesh;

   public EntityPDispatcher(World worldIn) {
      super(worldIn);
   }

   public void onLivingUpdate() {
      super.onLivingUpdate();
   }

   public boolean storeLodo(EntityParasiteBase in, boolean upgrade) {
      if (in.getParasiteIDRegister() == 5 && upgrade) {
         this.mobNameId.add("srparasites:rupter");
         in.particleStatus((byte)7);
         in.setDead();
         return true;
      } else if (in.getParasiteIDRegister() == 23 && upgrade) {
         in.particleStatus((byte)7);
         in.setDead();
         ++this.flesh;
         if (this.flesh >= 2) {
            this.flesh -= 2;
            this.addPrim();
            return true;
         } else {
            return true;
         }
      } else {
         return false;
      }
   }

   protected boolean storeInf(EntityParasiteBase in, boolean upgrade) {
      if ((in instanceof EntityPInfected || in.getParasiteIDRegister() == 23) && upgrade) {
         ++this.inf;
         this.mobNameId.add(EntityList.getKey(in).toString());
         in.particleStatus((byte)7);
         in.setDead();
         if (this.inf >= 4) {
            this.inf -= 4;
            int current = 0;

            for(int i = 0; i < this.mobNameId.size(); ++i) {
               if (((String)this.mobNameId.get(i)).contains("sim_")) {
                  this.mobNameId.remove(i);
                  i = -1;
                  ++current;
                  if (current >= 4) {
                     this.addPrim();
                     return true;
                  }
               }
            }
         }

         return true;
      } else {
         return false;
      }
   }

   protected boolean storeCrude(EntityParasiteBase in, boolean upgrade) {
      if ((in.getParasiteIDRegister() == 43 || in.getParasiteIDRegister() == 39) && upgrade) {
         if (in.getParasiteIDRegister() == 39) {
            ++this.crude;
         } else {
            this.crude += 2;
         }

         this.mobNameId.add(EntityList.getKey(in).toString());
         in.particleStatus((byte)7);
         in.setDead();
         if (this.crude >= 6) {
            this.crude -= 6;
            int current = 0;

            for(int i = 0; i < this.mobNameId.size(); ++i) {
               if (((String)this.mobNameId.get(i)).contains("incompleteform")) {
                  this.mobNameId.remove(i);
                  i = -1;
                  ++current;
                  if (current >= 6) {
                     this.mobNameId.add("srparasites:heed");
                     break;
                  }
               }
            }
         }

         return true;
      } else {
         return false;
      }
   }

   protected boolean storeMudo(EntityParasiteBase in, boolean upgrade) {
      if (in.getParasiteIDRegister() == 12 && upgrade) {
         this.mobNameId.add("srparasites:mangler");
         in.particleStatus((byte)7);
         in.setDead();
         return true;
      } else {
         return false;
      }
   }

   protected boolean storeMangler(EntityParasiteBase in, boolean upgrade) {
      if (in.getParasiteIDRegister() == 76 && upgrade) {
         ++this.mangler;
         this.mobNameId.add(EntityList.getKey(in).toString());
         in.particleStatus((byte)7);
         in.setDead();
         return true;
      } else {
         return false;
      }
   }

   protected boolean storeAll(EntityParasiteBase in) {
      this.mobNameId.add(EntityList.getKey(in).toString());
      in.particleStatus((byte)7);
      in.setDead();
      return true;
   }

   public boolean storeParasite(EntityParasiteBase in) {
      if (!in.isEntityAlive()) {
         return true;
      } else if (in.getParasiteIDRegister() == 36) {
         return true;
      } else {
         float bHard = 0.0F;
         IBlockState state2 = this.world.getBlockState(this.getPosition().down());
         float atm = state2.getBlockHardness(this.world, this.getPosition().down());
         if (atm <= 0.0F) {
            return true;
         } else {
            bHard = bHard + atm;
            state2 = this.world.getBlockState(this.getPosition().down(2));
            atm = state2.getBlockHardness(this.world, this.getPosition().down(2));
            if (atm <= 0.0F) {
               return true;
            } else {
               bHard = bHard + atm;
               state2 = this.world.getBlockState(this.getPosition().down(3));
               atm = state2.getBlockHardness(this.world, this.getPosition().down(3));
               if (atm <= 0.0F) {
                  return true;
               } else {
                  bHard = bHard + atm;
                  return bHard >= 10.0F;
               }
            }
         }
      }
   }

   private void addPrim() {
      int prim = this.rand.nextInt(7);
      boolean canAdd = true;

      for(int safe = 0; canAdd && safe < 10; ++safe) {
         switch(prim) {
         case 0:
            if (SRPConfigMobs.emanaEnabled) {
               this.mobNameId.add("srparasites:pri_yelloweye");
               canAdd = false;
            }
            break;
         case 1:
            if (SRPConfigMobs.canraEnabled) {
               this.mobNameId.add("srparasites:pri_summoner");
               canAdd = false;
            }
            break;
         case 2:
            if (SRPConfigMobs.zetmoEnabled) {
               this.mobNameId.add("srparasites:pri_bolster");
               canAdd = false;
            }
            break;
         case 3:
            if (SRPConfigMobs.shycoEnabled) {
               this.mobNameId.add("srparasites:pri_longarms");
               canAdd = false;
            }
            break;
         case 4:
            if (SRPConfigMobs.arachnidaEnabled) {
               this.mobNameId.add("srparasites:pri_arachnida");
               canAdd = false;
            }
            break;
         case 5:
            if (SRPConfigMobs.noglaEnabled) {
               this.mobNameId.add("srparasites:pri_reeker");
               canAdd = false;
            }
            break;
         case 6:
            if (SRPConfigMobs.hullEnabled) {
               this.mobNameId.add("srparasites:pri_manducater");
               canAdd = false;
            }
         }

         ++prim;
         if (prim >= 7) {
            prim = 0;
         }
      }

   }

   public String getParasiteStored() {
      if (this.mobNameId.isEmpty()) {
         return null;
      } else {
         int j = this.rand.nextInt(this.mobNameId.size());
         String name = (String)this.mobNameId.get(j);
         if (name.contains("sim_")) {
            --this.inf;
         } else if (name.contains("incompleteform_small")) {
            --this.crude;
         } else if (name.contains("incompleteform_medium")) {
            this.crude -= 2;
         } else if (name.contains("mangler")) {
            --this.mangler;
         }

         this.mobNameId.remove(j);
         return name;
      }
   }

   public void addParaBack(String id) {
      this.mobNameId.add(id);
   }

   public ArrayList<String> getMobList() {
      return this.mobNameId;
   }

   public void writeEntityToNBT(NBTTagCompound compound) {
      super.writeEntityToNBT(compound);
      compound.setInteger("dodcrude", this.crude);
      compound.setInteger("dodinf", this.inf);
      compound.setInteger("dodflesh", this.flesh);
      NBTTagList allResS = new NBTTagList();

      for(int i = 0; i < this.mobNameId.size(); ++i) {
         String res = (String)this.mobNameId.get(i);
         NBTTagCompound resT = new NBTTagCompound();
         resT.setString("mobs" + i, res);
         allResS.appendTag(resT);
      }

      compound.setTag("dodmobs", allResS);
   }

   public void readEntityFromNBT(NBTTagCompound compound) {
      super.readEntityFromNBT(compound);
      if (compound.hasKey("dodcrude", 99)) {
         this.crude = compound.getInteger("dodcrude");
      }

      if (compound.hasKey("dodinf", 99)) {
         this.inf = compound.getInteger("dodinf");
      }

      if (compound.hasKey("dodflesh", 99)) {
         this.flesh = compound.getInteger("dodflesh");
      }

      if (compound.hasKey("dodmobs")) {
         NBTTagList allResS = compound.getTagList("dodmobs", 10);

         for(int i = 0; i < allResS.tagCount(); ++i) {
            NBTTagCompound resT = allResS.getCompoundTagAt(i);
            String res = resT.getString("mobs" + i);
            this.mobNameId.add(i, res);
         }
      }

   }
}
