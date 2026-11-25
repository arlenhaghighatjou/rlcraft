package tschipp.carryon.common.scripting;

import net.minecraft.nbt.NBTTagCompound;

public class CarryOnOverride {
   private NBTTagCompound typeBlockTag;
   private String typeMeta;
   private String typeNameBlock;
   private String typeMaterial;
   private String typeHardness;
   private String typeResistance;
   private NBTTagCompound typeEntityTag;
   private String typeNameEntity;
   private String typeHeight;
   private String typeWidth;
   private String typeHealth;
   private String conditionGamestage;
   private String conditionAchievement;
   private String conditionXp;
   private String conditionGamemode;
   private String conditionScoreboard;
   private String conditionPosition;
   private String conditionEffects;
   private String renderNameBlock;
   private String renderNameEntity;
   private int renderMeta;
   private NBTTagCompound renderNBT;
   private String renderTranslation;
   private String renderRotation;
   private String renderScale;
   private String renderRotationLeftArm;
   private String renderRotationRightArm;
   private boolean renderLeftArm = true;
   private boolean renderRightArm = true;
   private String commandInit;
   private String commandLoop;
   private String commandPlace;
   private boolean isBlock;
   private boolean isEntity;
   private final String path;

   public CarryOnOverride(String path) {
      this.path = path;
   }

   public String getCommandInit() {
      return this.commandInit;
   }

   public void setCommandInit(String commandInit) {
      this.commandInit = commandInit;
   }

   public String getCommandLoop() {
      return this.commandLoop;
   }

   public void setCommandLoop(String commandLoop) {
      this.commandLoop = commandLoop;
   }

   public String getConditionEffects() {
      return this.conditionEffects;
   }

   public void setConditionEffects(String conditionEffects) {
      this.conditionEffects = conditionEffects;
   }

   public String getRenderRotationLeftArm() {
      return this.renderRotationLeftArm;
   }

   public void setRenderRotationLeftArm(String renderRotationLeftArm) {
      this.renderRotationLeftArm = renderRotationLeftArm;
   }

   public String getRenderRotationRightArm() {
      return this.renderRotationRightArm;
   }

   public void setRenderRotationRightArm(String renderRotationRightArm) {
      this.renderRotationRightArm = renderRotationRightArm;
   }

   public boolean isRenderLeftArm() {
      return this.renderLeftArm;
   }

   public void setRenderLeftArm(boolean renderLeftArm) {
      this.renderLeftArm = renderLeftArm;
   }

   public boolean isRenderRightArm() {
      return this.renderRightArm;
   }

   public void setRenderRightArm(boolean renderRightArm) {
      this.renderRightArm = renderRightArm;
   }

   public int hashCode() {
      int prime = 31;
      int result = 1;
      result = 31 * result + (this.path == null ? 0 : this.path.hashCode());
      return result;
   }

   public String toString() {
      return "Code: " + this.hashCode();
   }

   public boolean equals(Object obj) {
      if (this == obj) {
         return true;
      } else if (obj == null) {
         return false;
      } else if (this.getClass() != obj.getClass()) {
         return false;
      } else {
         CarryOnOverride other = (CarryOnOverride)obj;
         if (this.commandInit == null) {
            if (other.commandInit != null) {
               return false;
            }
         } else if (!this.commandInit.equals(other.commandInit)) {
            return false;
         }

         if (this.commandLoop == null) {
            if (other.commandLoop != null) {
               return false;
            }
         } else if (!this.commandLoop.equals(other.commandLoop)) {
            return false;
         }

         if (this.commandPlace == null) {
            if (other.commandPlace != null) {
               return false;
            }
         } else if (!this.commandPlace.equals(other.commandPlace)) {
            return false;
         }

         if (this.conditionAchievement == null) {
            if (other.conditionAchievement != null) {
               return false;
            }
         } else if (!this.conditionAchievement.equals(other.conditionAchievement)) {
            return false;
         }

         if (this.conditionEffects == null) {
            if (other.conditionEffects != null) {
               return false;
            }
         } else if (!this.conditionEffects.equals(other.conditionEffects)) {
            return false;
         }

         if (this.conditionGamemode == null) {
            if (other.conditionGamemode != null) {
               return false;
            }
         } else if (!this.conditionGamemode.equals(other.conditionGamemode)) {
            return false;
         }

         if (this.conditionGamestage == null) {
            if (other.conditionGamestage != null) {
               return false;
            }
         } else if (!this.conditionGamestage.equals(other.conditionGamestage)) {
            return false;
         }

         if (this.conditionPosition == null) {
            if (other.conditionPosition != null) {
               return false;
            }
         } else if (!this.conditionPosition.equals(other.conditionPosition)) {
            return false;
         }

         if (this.conditionScoreboard == null) {
            if (other.conditionScoreboard != null) {
               return false;
            }
         } else if (!this.conditionScoreboard.equals(other.conditionScoreboard)) {
            return false;
         }

         if (this.conditionXp == null) {
            if (other.conditionXp != null) {
               return false;
            }
         } else if (!this.conditionXp.equals(other.conditionXp)) {
            return false;
         }

         if (this.isBlock != other.isBlock) {
            return false;
         } else if (this.isEntity != other.isEntity) {
            return false;
         } else {
            if (this.path == null) {
               if (other.path != null) {
                  return false;
               }
            } else if (!this.path.equals(other.path)) {
               return false;
            }

            if (this.renderLeftArm != other.renderLeftArm) {
               return false;
            } else if (this.renderMeta != other.renderMeta) {
               return false;
            } else {
               if (this.renderNBT == null) {
                  if (other.renderNBT != null) {
                     return false;
                  }
               } else if (!this.renderNBT.equals(other.renderNBT)) {
                  return false;
               }

               if (this.renderNameBlock == null) {
                  if (other.renderNameBlock != null) {
                     return false;
                  }
               } else if (!this.renderNameBlock.equals(other.renderNameBlock)) {
                  return false;
               }

               if (this.renderNameEntity == null) {
                  if (other.renderNameEntity != null) {
                     return false;
                  }
               } else if (!this.renderNameEntity.equals(other.renderNameEntity)) {
                  return false;
               }

               if (this.renderRightArm != other.renderRightArm) {
                  return false;
               } else {
                  if (this.renderRotation == null) {
                     if (other.renderRotation != null) {
                        return false;
                     }
                  } else if (!this.renderRotation.equals(other.renderRotation)) {
                     return false;
                  }

                  if (this.renderRotationLeftArm == null) {
                     if (other.renderRotationLeftArm != null) {
                        return false;
                     }
                  } else if (!this.renderRotationLeftArm.equals(other.renderRotationLeftArm)) {
                     return false;
                  }

                  if (this.renderRotationRightArm == null) {
                     if (other.renderRotationRightArm != null) {
                        return false;
                     }
                  } else if (!this.renderRotationRightArm.equals(other.renderRotationRightArm)) {
                     return false;
                  }

                  if (this.renderScale == null) {
                     if (other.renderScale != null) {
                        return false;
                     }
                  } else if (!this.renderScale.equals(other.renderScale)) {
                     return false;
                  }

                  if (this.renderTranslation == null) {
                     if (other.renderTranslation != null) {
                        return false;
                     }
                  } else if (!this.renderTranslation.equals(other.renderTranslation)) {
                     return false;
                  }

                  if (this.typeBlockTag == null) {
                     if (other.typeBlockTag != null) {
                        return false;
                     }
                  } else if (!this.typeBlockTag.equals(other.typeBlockTag)) {
                     return false;
                  }

                  if (this.typeEntityTag == null) {
                     if (other.typeEntityTag != null) {
                        return false;
                     }
                  } else if (!this.typeEntityTag.equals(other.typeEntityTag)) {
                     return false;
                  }

                  if (this.typeHardness == null) {
                     if (other.typeHardness != null) {
                        return false;
                     }
                  } else if (!this.typeHardness.equals(other.typeHardness)) {
                     return false;
                  }

                  if (this.typeHealth == null) {
                     if (other.typeHealth != null) {
                        return false;
                     }
                  } else if (!this.typeHealth.equals(other.typeHealth)) {
                     return false;
                  }

                  if (this.typeHeight == null) {
                     if (other.typeHeight != null) {
                        return false;
                     }
                  } else if (!this.typeHeight.equals(other.typeHeight)) {
                     return false;
                  }

                  if (this.typeMaterial == null) {
                     if (other.typeMaterial != null) {
                        return false;
                     }
                  } else if (!this.typeMaterial.equals(other.typeMaterial)) {
                     return false;
                  }

                  if (this.typeMeta == null) {
                     if (other.typeMeta != null) {
                        return false;
                     }
                  } else if (!this.typeMeta.equals(other.typeMeta)) {
                     return false;
                  }

                  if (this.typeNameBlock == null) {
                     if (other.typeNameBlock != null) {
                        return false;
                     }
                  } else if (!this.typeNameBlock.equals(other.typeNameBlock)) {
                     return false;
                  }

                  if (this.typeNameEntity == null) {
                     if (other.typeNameEntity != null) {
                        return false;
                     }
                  } else if (!this.typeNameEntity.equals(other.typeNameEntity)) {
                     return false;
                  }

                  if (this.typeResistance == null) {
                     if (other.typeResistance != null) {
                        return false;
                     }
                  } else if (!this.typeResistance.equals(other.typeResistance)) {
                     return false;
                  }

                  if (this.typeWidth == null) {
                     if (other.typeWidth != null) {
                        return false;
                     }
                  } else if (!this.typeWidth.equals(other.typeWidth)) {
                     return false;
                  }

                  return true;
               }
            }
         }
      }
   }

   public boolean isBlock() {
      return this.isBlock;
   }

   public void setBlock(boolean isBlock) {
      this.isBlock = isBlock;
   }

   public boolean isEntity() {
      return this.isEntity;
   }

   public void setEntity(boolean isEntity) {
      this.isEntity = isEntity;
   }

   public NBTTagCompound getTypeBlockTag() {
      return this.typeBlockTag;
   }

   public String getTypeMeta() {
      return this.typeMeta;
   }

   public String getTypeNameBlock() {
      return this.typeNameBlock;
   }

   public String getTypeMaterial() {
      return this.typeMaterial;
   }

   public String getTypeHardness() {
      return this.typeHardness;
   }

   public String getTypeResistance() {
      return this.typeResistance;
   }

   public NBTTagCompound getTypeEntityTag() {
      return this.typeEntityTag;
   }

   public String getTypeNameEntity() {
      return this.typeNameEntity;
   }

   public String getTypeHeight() {
      return this.typeHeight;
   }

   public String getTypeWidth() {
      return this.typeWidth;
   }

   public String getTypeHealth() {
      return this.typeHealth;
   }

   public String getConditionGamestage() {
      return this.conditionGamestage;
   }

   public String getConditionAchievement() {
      return this.conditionAchievement;
   }

   public String getConditionXp() {
      return this.conditionXp;
   }

   public String getConditionGamemode() {
      return this.conditionGamemode;
   }

   public String getConditionScoreboard() {
      return this.conditionScoreboard;
   }

   public String getConditionPosition() {
      return this.conditionPosition;
   }

   public String getRenderNameBlock() {
      return this.renderNameBlock;
   }

   public String getRenderNameEntity() {
      return this.renderNameEntity;
   }

   public int getRenderMeta() {
      return this.renderMeta;
   }

   public NBTTagCompound getRenderNBT() {
      return this.renderNBT;
   }

   public String getRenderTranslation() {
      return this.renderTranslation;
   }

   public String getRenderRotation() {
      return this.renderRotation;
   }

   public String getRenderScale() {
      return this.renderScale;
   }

   public void setTypeBlockTag(NBTTagCompound typeBlockTag) {
      this.typeBlockTag = typeBlockTag;
   }

   public void setTypeMeta(String typeMeta) {
      this.typeMeta = typeMeta;
   }

   public void setTypeNameBlock(String typeNameBlock) {
      this.typeNameBlock = typeNameBlock;
   }

   public void setTypeMaterial(String typeMaterial) {
      this.typeMaterial = typeMaterial;
   }

   public void setTypeHardness(String typeHardness) {
      this.typeHardness = typeHardness;
   }

   public void setTypeResistance(String typeResistance) {
      this.typeResistance = typeResistance;
   }

   public void setTypeEntityTag(NBTTagCompound typeEntityTag) {
      this.typeEntityTag = typeEntityTag;
   }

   public void setTypeNameEntity(String typeNameEntity) {
      this.typeNameEntity = typeNameEntity;
   }

   public void setTypeHeight(String typeHeight) {
      this.typeHeight = typeHeight;
   }

   public void setTypeWidth(String typeWidth) {
      this.typeWidth = typeWidth;
   }

   public void setTypeHealth(String typeHealth) {
      this.typeHealth = typeHealth;
   }

   public void setConditionGamestage(String conditionGamestage) {
      this.conditionGamestage = conditionGamestage;
   }

   public void setConditionAchievement(String conditionAchievement) {
      this.conditionAchievement = conditionAchievement;
   }

   public void setConditionXp(String conditionXp) {
      this.conditionXp = conditionXp;
   }

   public void setConditionGamemode(String conditionGamemode) {
      this.conditionGamemode = conditionGamemode;
   }

   public void setConditionScoreboard(String conditionScoreboard) {
      this.conditionScoreboard = conditionScoreboard;
   }

   public void setConditionPosition(String conditionPosition) {
      this.conditionPosition = conditionPosition;
   }

   public void setRenderNameBlock(String renderNameBlock) {
      this.renderNameBlock = renderNameBlock;
   }

   public void setRenderNameEntity(String renderNameEntity) {
      this.renderNameEntity = renderNameEntity;
   }

   public void setRenderMeta(int renderMeta) {
      this.renderMeta = renderMeta;
   }

   public void setRenderNBT(NBTTagCompound renderNBT) {
      this.renderNBT = renderNBT;
   }

   public void setRenderTranslation(String renderTranslation) {
      this.renderTranslation = renderTranslation;
   }

   public void setRenderRotation(String renderRotation) {
      this.renderRotation = renderRotation;
   }

   public void setRenderScale(String renderScale) {
      this.renderScale = renderScale;
   }

   public String getCommandPlace() {
      return this.commandPlace;
   }

   public void setCommandPlace(String commandPlace) {
      this.commandPlace = commandPlace;
   }
}
