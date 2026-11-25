package com.creativemd.creativecore.common.utils.math.collision;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import javax.vecmath.Vector2d;

public class IntersectionHelper {
   public static final int EDGE_T_0 = 2;
   public static final int EDGE_S_1 = 4;
   public static final int EDGE_T_1 = 6;
   public static final int EDGE_S_0 = 8;
   public static final int EDGE_EDGE_OFFSET = 2;
   public static final int CORNER_CORNER_OFFSET = 2;
   public static final int CORNER_EDGE_OFFSET = 1;
   public static final Vector2d V00 = new Vector2d(0.0D, 0.0D);
   public static final Vector2d V01 = new Vector2d(0.0D, 1.0D);
   public static final Vector2d V10 = new Vector2d(1.0D, 0.0D);
   public static final Vector2d V11 = new Vector2d(1.0D, 1.0D);
   public static final Vector2d[] VXX = new Vector2d[]{V00, V01, V11, V10};
   public static final int VXX_STEP_SIZE = 2;
   public static final int NONE = 0;

   public IntersectionHelper() {
   }

   public static List<Vector2d> getIntersectionShape(Vector2d[] corners) {
      List<Vector2d> result = new ArrayList(8);
      Vector2d prev = corners[corners.length - 1];
      Vector2d cur = null;
      int cornerCount = 0;
      int lastIntersectingEdge = 0;
      int firstCornerCount = 0;
      IntersectionHelper.FF ff = new IntersectionHelper.FF();

      for(int ci = 0; ci < corners.length; ++ci) {
         cur = corners[ci];
         ff.reset();
         if ((prev.x >= 0.0D || cur.x >= 0.0D) && (prev.x <= 1.0D || cur.x <= 1.0D) && (prev.y >= 0.0D || cur.y >= 0.0D) && (prev.y <= 1.0D || cur.y <= 1.0D)) {
            List<Integer> intersections = new ArrayList(2);
            List<Vector2d> postfix = new ArrayList(2);
            if (prev.x >= 0.0D && prev.x <= 1.0D) {
               if (prev.x == 0.0D && cur.x == 0.0D || prev.x == 1.0D && cur.x == 1.0D || prev.y == 0.0D && cur.y == 0.0D || prev.y == 1.0D && cur.y == 1.0D) {
                  if ((prev.x >= 0.0D || cur.x != 0.0D) && (prev.x <= 1.0D || cur.x != 1.0D) && (prev.y >= 0.0D || cur.y != 0.0D) && (prev.y <= 1.0D || cur.y != 1.0D)) {
                     if ((0.0D > prev.x || prev.x > 1.0D || 0.0D > prev.y || prev.y > 1.0D) && (prev.x >= 0.0D || cur.x != 0.0D) && (prev.x <= 1.0D || cur.x != 1.0D) && (prev.y >= 0.0D || cur.y != 0.0D) && (prev.y <= 1.0D || cur.y != 1.0D)) {
                        postfix.add(getClosestNormalCorner(prev));
                     }

                     if ((0.0D > cur.x || cur.x > 1.0D || 0.0D > cur.y || cur.y > 1.0D) && (cur.x >= 0.0D || prev.x != 0.0D) && (cur.x <= 1.0D || prev.x != 1.0D) && (cur.y >= 0.0D || prev.y != 0.0D) && (cur.y <= 1.0D || prev.y != 1.0D)) {
                        postfix.add(getClosestNormalCorner(cur));
                     }
                  }
               } else if (prev.y >= 0.0D && prev.y <= 1.0D) {
                  if ((cur.x >= 0.0D || prev.x != 0.0D) && (cur.x <= 1.0D || prev.x != 1.0D) && (cur.y >= 0.0D || prev.y != 0.0D) && (cur.y <= 1.0D || prev.y != 1.0D)) {
                     if (cur.x >= 0.0D && cur.x <= 1.0D) {
                        if (cur.y < 0.0D || cur.y > 1.0D) {
                           intersections.add(Integer.valueOf(cur.y < 0.0D ? 8 : 4));
                        }
                     } else if (cur.y >= 0.0D && cur.y <= 1.0D) {
                        intersections.add(Integer.valueOf(cur.x < 0.0D ? 2 : 6));
                     } else {
                        double angleCorner = angle(prev, getClosestNormalCorner(cur));
                        double angleCurrent = angle(prev, cur);
                        if (angleCorner > angleCurrent) {
                           intersections.add(Integer.valueOf(cur.x < 0.0D ? 2 : 6));
                        } else if (angleCorner < angleCurrent) {
                           intersections.add(Integer.valueOf(cur.y < 0.0D ? 8 : 4));
                        } else {
                           postfix.add(getClosestNormalCorner(cur));
                        }
                     }
                  }
               } else if ((prev.y >= 0.0D || cur.y != 0.0D) && (prev.y <= 1.0D || cur.y != 1.0D)) {
                  intersections.add(Integer.valueOf(prev.y < 0.0D ? 8 : 4));
                  if (cur.x >= 0.0D && cur.x <= 1.0D) {
                     if (cur.y < 0.0D || cur.y > 1.0D) {
                        intersections.add(Integer.valueOf(cur.y < 0.0D ? 8 : 4));
                     }
                  } else if (0.0D <= cur.y && cur.y <= 1.0D) {
                     intersections.add(Integer.valueOf(cur.x < 0.0D ? 2 : 6));
                  } else {
                     double angleCorner = angle(prev, getClosestNormalCorner(cur));
                     double angleCurrent = angle(prev, cur);
                     if (angleCorner > angleCurrent) {
                        intersections.add(Integer.valueOf(cur.x < 0.0D ? 2 : 6));
                     } else if (angleCorner < angleCurrent) {
                        intersections.add(Integer.valueOf(prev.y < 0.0D ? 4 : 8));
                     } else {
                        postfix.add(getClosestNormalCorner(cur));
                     }
                  }
               }
            } else if (prev.y >= 0.0D && prev.y <= 1.0D) {
               if (prev.y == 0.0D && cur.y == 0.0D || prev.y == 1.0D && cur.y == 1.0D) {
                  if ((0.0D > prev.x || prev.x > 1.0D || 0.0D > prev.y || prev.y > 1.0D) && (prev.x >= 0.0D || cur.x != 0.0D) && (prev.x <= 1.0D || cur.x != 1.0D)) {
                     postfix.add(getClosestNormalCorner(prev));
                  }

                  if ((0.0D > cur.x || cur.x > 1.0D || 0.0D > cur.y || cur.y > 1.0D) && (cur.x >= 0.0D || prev.x != 0.0D) && (cur.x <= 1.0D || prev.x != 1.0D)) {
                     postfix.add(getClosestNormalCorner(cur));
                  }
               } else if (prev.x < 0.0D && cur.x != 0.0D || prev.x > 1.0D && cur.x != 1.0D) {
                  intersections.add(Integer.valueOf(prev.x < 0.0D ? 2 : 6));
                  if (cur.x >= 0.0D && cur.x <= 1.0D) {
                     if (cur.y < 0.0D || cur.y > 1.0D) {
                        intersections.add(Integer.valueOf(cur.y < 0.0D ? 8 : 4));
                     }
                  } else if (0.0D <= cur.y && cur.y <= 1.0D && cur.x != (double)(prev.x < 0.0D ? 1 : 0)) {
                     intersections.add(Integer.valueOf(cur.x < 0.0D ? 2 : 6));
                  } else {
                     double angleCorner = angle(prev, getClosestNormalCorner(cur));
                     double angleCurrent = angle(prev, cur);
                     if (angleCorner > angleCurrent) {
                        intersections.add(Integer.valueOf(prev.x < 0.0D ? 6 : 2));
                     } else if (angleCorner < angleCurrent) {
                        intersections.add(Integer.valueOf(cur.y < 0.0D ? 8 : 4));
                     } else {
                        postfix.add(getClosestNormalCorner(cur));
                     }
                  }
               }
            } else {
               double angleCorner = angle(prev, getClosestNormalCorner(prev));
               double angleCurrent = angle(prev, cur);
               if (angleCorner > angleCurrent) {
                  if (0.0D > cur.x || cur.x > 1.0D || cur.y != (double)(prev.y < 0.0D ? 0 : 1)) {
                     intersections.add(Integer.valueOf(prev.y < 0.0D ? 8 : 4));
                  }
               } else if (angleCorner < angleCurrent) {
                  if (0.0D > cur.y || cur.y > 1.0D || cur.x != (double)(prev.x < 0.0D ? 0 : 1)) {
                     intersections.add(Integer.valueOf(prev.x < 0.0D ? 2 : 6));
                  }
               } else {
                  postfix.add(getClosestNormalCorner(prev));
               }

               if (cur.x < 0.0D || cur.x > 1.0D || cur.y < 0.0D || cur.y > 1.0D) {
                  angleCorner = angle(prev, getOpositeClosestNormalCorner(prev));
                  if (angleCorner > angleCurrent) {
                     if (0.0D > cur.y || cur.y > 1.0D || cur.x != (double)(prev.x < 0.0D ? 1 : 0)) {
                        intersections.add(Integer.valueOf(prev.x < 0.0D ? 6 : 2));
                     }
                  } else if (angleCorner < angleCurrent) {
                     if (0.0D > cur.x || cur.x > 1.0D || cur.y != (double)(prev.y < 0.0D ? 1 : 0)) {
                        intersections.add(Integer.valueOf(prev.y < 0.0D ? 4 : 8));
                     }
                  } else {
                     postfix.add(getOpositeClosestNormalCorner(prev));
                  }
               }
            }

            for(int i = 0; i < intersections.size(); ++i) {
               switch(((Integer)intersections.get(i)).intValue()) {
               case 2:
                  if (ff.ft == null) {
                     ff.ft = getTFormulaFrom(prev, cur);
                  }

                  if (isBetween(0.0D, 1.0D, ff.ft != null ? ff.ft.y : prev.y)) {
                     if (lastIntersectingEdge != 0) {
                        cornerCount = cornerCount + getCornerCount(lastIntersectingEdge, 2, ff, prev, cur);
                        if (cornerCount != 0 && lastIntersectingEdge != 0) {
                           addNormalCorners(result, lastIntersectingEdge, 2, cornerCount);
                        }

                        cornerCount = 0;
                        lastIntersectingEdge = 0;
                     }

                     result.add(ff.ft != null ? new Vector2d(0.0D, ff.ft.y) : new Vector2d(0.0D, prev.y));
                  } else {
                     intersections.clear();
                  }
               case 3:
               case 5:
               case 7:
               default:
                  break;
               case 4:
                  if (ff.fs == null) {
                     ff.fs = getSFormulaFrom(prev, cur);
                  }

                  if (isBetween(0.0D, 1.0D, ff.fs != null ? ff.fs.x + ff.fs.y : prev.x)) {
                     if (lastIntersectingEdge != 0) {
                        cornerCount = cornerCount + getCornerCount(lastIntersectingEdge, 4, ff, prev, cur);
                        if (cornerCount != 0 && lastIntersectingEdge != 0) {
                           addNormalCorners(result, lastIntersectingEdge, 4, cornerCount);
                        }

                        cornerCount = 0;
                        lastIntersectingEdge = 0;
                     }

                     result.add(ff.fs != null ? new Vector2d(ff.fs.x + ff.fs.y, 1.0D) : new Vector2d(prev.x, 1.0D));
                  } else {
                     intersections.clear();
                  }
                  break;
               case 6:
                  if (ff.ft == null) {
                     ff.ft = getTFormulaFrom(prev, cur);
                  }

                  if (isBetween(0.0D, 1.0D, ff.ft != null ? ff.ft.x + ff.ft.y : prev.y)) {
                     if (lastIntersectingEdge != 0) {
                        cornerCount = cornerCount + getCornerCount(lastIntersectingEdge, 6, ff, prev, cur);
                        if (cornerCount != 0 && lastIntersectingEdge != 0) {
                           addNormalCorners(result, lastIntersectingEdge, 6, cornerCount);
                        }

                        cornerCount = 0;
                        lastIntersectingEdge = 0;
                     }

                     result.add(ff.ft != null ? new Vector2d(1.0D, ff.ft.x + ff.ft.y) : new Vector2d(1.0D, prev.y));
                  } else {
                     intersections.clear();
                  }
                  break;
               case 8:
                  if (ff.fs == null) {
                     ff.fs = getSFormulaFrom(prev, cur);
                  }

                  if (isBetween(0.0D, 1.0D, ff.fs != null ? ff.fs.y : prev.x)) {
                     if (lastIntersectingEdge != 0) {
                        cornerCount = cornerCount + getCornerCount(lastIntersectingEdge, 8, ff, prev, cur);
                        if (cornerCount != 0 && lastIntersectingEdge != 0) {
                           addNormalCorners(result, lastIntersectingEdge, 8, cornerCount);
                        }

                        cornerCount = 0;
                        lastIntersectingEdge = 0;
                     }

                     result.add(ff.fs != null ? new Vector2d(ff.fs.y, 0.0D) : new Vector2d(prev.x, 0.0D));
                  } else {
                     intersections.clear();
                  }
               }
            }

            if (!postfix.isEmpty()) {
               if (lastIntersectingEdge != 0) {
                  if (lastIntersectingEdge != 0) {
                     cornerCount = cornerCount + getCornerCount(lastIntersectingEdge, getEdgeFrom(!result.isEmpty() ? (Vector2d)result.get(result.size() - 1) : (Vector2d)postfix.get(0)), ff, prev, cur);
                     if (cornerCount != 0) {
                        addNormalCorners(result, lastIntersectingEdge, getEdgeFrom((Vector2d)postfix.get(0)), cornerCount);
                     }
                  }

                  cornerCount = 0;
                  lastIntersectingEdge = 0;
               }

               for(int i = 0; i < postfix.size(); ++i) {
                  result.add(postfix.get(i));
               }
            }

            if (0.0D <= cur.x && cur.x <= 1.0D && 0.0D <= cur.y && cur.y <= 1.0D) {
               if (lastIntersectingEdge != 0) {
                  if (cur.x != 0.0D && cur.x != 1.0D && cur.y != 0.0D && cur.y != 1.0D) {
                     cornerCount = cornerCount + getCornerCount(lastIntersectingEdge, getEdgeFrom(!result.isEmpty() ? (Vector2d)result.get(result.size() - 1) : cur), ff, prev, cur);
                  } else {
                     cornerCount = cornerCount + getCornerCount(lastIntersectingEdge, getEdgeFrom(cur), ff, prev, cur);
                  }

                  if (cornerCount != 0 && lastIntersectingEdge != 0) {
                     addNormalCorners(result, lastIntersectingEdge, getEdgeFrom(cur), cornerCount);
                  }

                  cornerCount = 0;
                  lastIntersectingEdge = 0;
               }

               result.add(cur);
            } else {
               if (lastIntersectingEdge == 0) {
                  lastIntersectingEdge = getEdgeFrom(!result.isEmpty() ? (Vector2d)result.get(result.size() - 1) : prev);
               }

               cornerCount += getCornerCount(lastIntersectingEdge, 0, ff, prev, cur);
               if (result.isEmpty()) {
                  firstCornerCount = cornerCount;
               }
            }

            prev = cur;
         } else {
            cornerCount += getCornerCount(lastIntersectingEdge, 0, ff, prev, cur);
            if (result.isEmpty()) {
               firstCornerCount = cornerCount;
            }

            prev = cur;
         }
      }

      if (firstCornerCount + cornerCount != 0) {
         if (!result.isEmpty()) {
            if (lastIntersectingEdge != 0) {
               addNormalCorners(result, lastIntersectingEdge, getEdgeFrom((Vector2d)result.get(0)), firstCornerCount + cornerCount);
            }
         } else {
            result = Arrays.asList(VXX);
         }
      }

      return result;
   }

   public static int getCornerCount(int lastIntersectingEdge, int currentIntersectingEdge, IntersectionHelper.FF ff, Vector2d prev, Vector2d cur) {
      if (prev.x < 0.0D) {
         if (prev.y < 0.0D) {
            if (cur.x > 1.0D) {
               if (cur.y > 1.0D) {
                  if (ff.ft == null) {
                     ff.ft = getTFormulaFrom(prev, cur);
                  }

                  return ff.ft.y < 0.0D ? -4 : 4;
               }

               if (cur.y < 0.0D) {
                  return -2;
               }

               return -3;
            }

            if (cur.x < 0.0D) {
               if (cur.y >= 1.0D) {
                  return 2;
               }

               if (cur.y > 0.0D) {
                  return 1;
               }

               return 0;
            }

            if (cur.y < 0.0D) {
               return -1;
            }

            if (cur.y > 1.0D) {
               return 3;
            }

            if (currentIntersectingEdge == 2) {
               return 2;
            }

            if (currentIntersectingEdge == 2) {
               return 1;
            }

            if (currentIntersectingEdge == 1) {
               return 0;
            }

            if (currentIntersectingEdge == 8) {
               return -1;
            }

            if (currentIntersectingEdge == 7) {
               return -2;
            }
         } else if (prev.y > 1.0D) {
            if (cur.x > 1.0D) {
               if (cur.y < 0.0D) {
                  if (ff.ft == null) {
                     ff.ft = getTFormulaFrom(prev, cur);
                  }

                  return ff.ft.y < 0.0D ? -4 : 4;
               }

               if (cur.y > 1.0D) {
                  return 2;
               }

               return 3;
            }

            if (cur.x < 0.0D) {
               if (cur.y <= 0.0D) {
                  return -2;
               }

               if (cur.y < 1.0D) {
                  return -1;
               }

               return 0;
            }

            if (cur.y < 0.0D) {
               return -3;
            }

            if (cur.y > 1.0D) {
               return 1;
            }

            if (currentIntersectingEdge == 1) {
               return -2;
            }

            if (currentIntersectingEdge == 2) {
               return -1;
            }

            if (currentIntersectingEdge == 3) {
               return 0;
            }

            if (currentIntersectingEdge == 4) {
               return 1;
            }

            if (currentIntersectingEdge == 5) {
               return 2;
            }
         } else {
            if (cur.x > 1.0D) {
               if (cur.y < 0.0D) {
                  return -3;
               }

               return 3;
            }

            if (cur.x < 0.0D) {
               if (cur.y < 0.0D) {
                  return -1;
               }

               if (cur.y > 1.0D) {
                  return 1;
               }

               return 0;
            }

            if (cur.y < 0.0D) {
               return -2;
            }

            if (cur.y > 1.0D) {
               return 2;
            }

            if (currentIntersectingEdge == 1) {
               return -1;
            }

            if (currentIntersectingEdge == 2) {
               return 0;
            }

            if (currentIntersectingEdge == 3) {
               return 1;
            }
         }
      } else if (prev.x > 1.0D) {
         if (prev.y < 0.0D) {
            if (cur.x < 0.0D) {
               if (cur.y > 1.0D) {
                  if (ff.ft == null) {
                     ff.ft = getTFormulaFrom(prev, cur);
                  }

                  return ff.ft.y > 1.0D ? -4 : 4;
               }

               if (cur.y < 0.0D) {
                  return 2;
               }

               return 3;
            }

            if (cur.x > 1.0D) {
               if (cur.y > 1.0D) {
                  return -2;
               }

               if (cur.y >= 0.0D) {
                  return -1;
               }

               return 0;
            }

            if (cur.y < 0.0D) {
               return 1;
            }

            if (cur.y > 1.0D) {
               return -3;
            }

            if (currentIntersectingEdge == 5) {
               return -2;
            }

            if (currentIntersectingEdge == 6) {
               return -1;
            }

            if (currentIntersectingEdge == 7) {
               return 0;
            }

            if (currentIntersectingEdge == 8) {
               return 1;
            }

            if (currentIntersectingEdge == 0) {
               return 2;
            }
         } else if (prev.y > 1.0D) {
            if (cur.x < 0.0D) {
               if (cur.y < 0.0D) {
                  if (ff.ft == null) {
                     ff.ft = getTFormulaFrom(prev, cur);
                  }

                  return ff.ft.y > 1.0D ? -4 : 4;
               }

               if (cur.y > 1.0D) {
                  return -2;
               }

               return -3;
            }

            if (cur.x > 1.0D) {
               if (cur.y < 0.0D) {
                  return 2;
               }

               if (cur.y <= 1.0D) {
                  return 1;
               }

               return 0;
            }

            if (cur.y < 0.0D) {
               return 3;
            }

            if (cur.y > 1.0D) {
               return -1;
            }

            if (currentIntersectingEdge == 3) {
               return -2;
            }

            if (currentIntersectingEdge == 4) {
               return -1;
            }

            if (currentIntersectingEdge == 5) {
               return 0;
            }

            if (currentIntersectingEdge == 6) {
               return 1;
            }

            if (currentIntersectingEdge == 7) {
               return 2;
            }
         } else if (cur.x < 0.0D) {
            if (cur.y < 0.0D) {
               return 3;
            }

            if (cur.y > 1.0D) {
               return -3;
            }
         } else {
            if (cur.x > 1.0D) {
               if (cur.y > 1.0D) {
                  return -1;
               }

               if (cur.y < 0.0D) {
                  return 1;
               }

               return 0;
            }

            if (cur.y < 0.0D) {
               return 2;
            }

            if (cur.y > 1.0D) {
               return -2;
            }

            if (currentIntersectingEdge == 5) {
               return -1;
            }

            if (currentIntersectingEdge == 7) {
               return 1;
            }

            if (currentIntersectingEdge == 6) {
               return 0;
            }
         }
      } else if (prev.y < 0.0D) {
         if (cur.x < 0.0D) {
            if (cur.y < 0.0D) {
               return 1;
            }

            if (cur.y > 1.0D) {
               return 3;
            }

            return 2;
         }

         if (cur.x > 1.0D) {
            if (cur.y < 0.0D) {
               return -1;
            }

            if (cur.y > 1.0D) {
               return -3;
            }

            return -2;
         }

         if (currentIntersectingEdge == 7) {
            return -1;
         }

         if (currentIntersectingEdge == 8) {
            return 0;
         }

         if (currentIntersectingEdge == 1) {
            return 1;
         }
      } else if (prev.y > 1.0D) {
         if (cur.x < 0.0D) {
            if (cur.y < 0.0D) {
               return -3;
            }

            if (cur.y > 1.0D) {
               return -1;
            }

            return -2;
         }

         if (cur.x > 1.0D) {
            if (cur.y < 0.0D) {
               return 3;
            }

            if (cur.y > 1.0D) {
               return 1;
            }

            return 2;
         }

         if (currentIntersectingEdge == 3) {
            return -1;
         }

         if (currentIntersectingEdge == 4) {
            return 0;
         }

         if (currentIntersectingEdge == 5) {
            return 1;
         }
      } else {
         switch(lastIntersectingEdge) {
         case 0:
         default:
            break;
         case 1:
            if (cur.y > 1.0D) {
               return 2;
            }

            if (cur.y >= 0.0D) {
               return 1;
            }

            if (cur.x > 1.0D) {
               return -2;
            }

            if (cur.x >= 0.0D) {
               return -1;
            }

            return 0;
         case 2:
            if (cur.y < 0.0D) {
               return -1;
            }

            if (cur.y > 1.0D) {
               return 1;
            }

            return 0;
         case 3:
            if (cur.x > 1.0D) {
               return 2;
            }

            if (cur.x >= 0.0D) {
               return 1;
            }

            if (cur.y < 0.0D) {
               return -2;
            }

            if (cur.y <= 1.0D) {
               return -1;
            }

            return 0;
         case 4:
            if (cur.x < 0.0D) {
               return -1;
            }

            if (cur.x > 1.0D) {
               return 1;
            }

            return 0;
         case 5:
            if (cur.y < 0.0D) {
               return 2;
            }

            if (cur.y <= 1.0D) {
               return 1;
            }

            if (cur.x < 0.0D) {
               return -2;
            }

            if (cur.x <= 1.0D) {
               return -1;
            }

            return 0;
         case 6:
            if (cur.y < 0.0D) {
               return 1;
            }

            if (cur.y > 1.0D) {
               return -1;
            }

            return 0;
         case 7:
            if (cur.x < 0.0D) {
               return 2;
            }

            if (cur.x <= 1.0D) {
               return 1;
            }

            if (cur.y > 1.0D) {
               return -2;
            }

            if (cur.y >= 0.0D) {
               return -1;
            }

            return 0;
         case 8:
            if (cur.x < 0.0D) {
               return 1;
            }

            if (cur.x > 1.0D) {
               return -1;
            }

            return 0;
         }
      }

      return 0;
   }

   public static void addNormalCorners(List<Vector2d> result, int lastIntersectingEdge, int currentIntersectingEdge, int cornerCount) {
      if (cornerCount > 0) {
         if (lastIntersectingEdge % 2 == 1) {
            if (currentIntersectingEdge - lastIntersectingEdge == 2) {
               return;
            }

            ++lastIntersectingEdge;
         }

         if (currentIntersectingEdge % 2 == 1) {
            --currentIntersectingEdge;
         }

         if (currentIntersectingEdge < lastIntersectingEdge) {
            currentIntersectingEdge += 2 * VXX.length;
         }

         for(int i = lastIntersectingEdge; i < currentIntersectingEdge; i += 2) {
            result.add(VXX[(int)(Math.floor((double)(i / 2)) % (double)VXX.length)]);
         }
      } else if (cornerCount < 0) {
         if (lastIntersectingEdge % 2 == 1) {
            if (currentIntersectingEdge - lastIntersectingEdge == -2) {
               return;
            }

            --lastIntersectingEdge;
         }

         if (currentIntersectingEdge % 2 == 1) {
            ++currentIntersectingEdge;
         }

         if (lastIntersectingEdge < currentIntersectingEdge) {
            lastIntersectingEdge += 2 * VXX.length;
         }

         int i = lastIntersectingEdge;

         while(true) {
            i -= 2;
            if (i <= currentIntersectingEdge) {
               break;
            }

            result.add(VXX[(int)(Math.floor((double)(i / 2)) % (double)VXX.length)]);
         }
      }

   }

   public static int getEdgeFrom(Vector2d vec) {
      if (vec.x == 0.0D) {
         if (vec.y == 0.0D) {
            return 1;
         } else {
            return vec.y == 1.0D ? 3 : 2;
         }
      } else if (vec.x == 1.0D) {
         if (vec.y == 0.0D) {
            return 7;
         } else {
            return vec.y == 1.0D ? 5 : 6;
         }
      } else if (vec.y == 0.0D) {
         if (vec.x == 0.0D) {
            return 9;
         } else {
            return vec.x == 1.0D ? 7 : 8;
         }
      } else if (vec.y == 1.0D) {
         if (vec.x == 0.0D) {
            return 3;
         } else {
            return vec.x == 1.0D ? 5 : 4;
         }
      } else {
         return 0;
      }
   }

   public static int getNonantsEdgeFrom(Vector2d vec) {
      if (vec.x <= 0.0D) {
         if (vec.y <= 0.0D) {
            return 1;
         } else {
            return vec.y >= 1.0D ? 3 : 2;
         }
      } else if (vec.x >= 1.0D) {
         if (vec.y <= 0.0D) {
            return 7;
         } else {
            return vec.y >= 1.0D ? 5 : 6;
         }
      } else if (vec.y <= 0.0D) {
         if (vec.x <= 0.0D) {
            return 9;
         } else {
            return vec.x >= 1.0D ? 7 : 8;
         }
      } else if (vec.y >= 1.0D) {
         if (vec.x <= 0.0D) {
            return 3;
         } else {
            return vec.x >= 1.0D ? 5 : 4;
         }
      } else {
         return 0;
      }
   }

   public static Vector2d getClosestNormalCorner(Vector2d vec) {
      if (vec.x < 0.5D) {
         return vec.y < 0.5D ? V00 : V01;
      } else {
         return vec.y < 0.5D ? V10 : V11;
      }
   }

   public static Vector2d getOpositeClosestNormalCorner(Vector2d vec) {
      if (vec.x < 0.5D) {
         return vec.y < 0.5D ? V11 : V10;
      } else {
         return vec.y < 0.5D ? V01 : V00;
      }
   }

   public static boolean isBetween(double min, double max, double vec) {
      return min <= vec && vec <= max;
   }

   public static Vector2d getSFormulaFrom(Vector2d prev, Vector2d cur) {
      if (prev.y == cur.y) {
         return null;
      } else {
         double a = (cur.x - prev.x) / (cur.y - prev.y);
         double b = prev.x - a * prev.y;
         return new Vector2d(a, b);
      }
   }

   public static Vector2d getTFormulaFrom(Vector2d prev, Vector2d cur) {
      if (prev.x == cur.x) {
         return null;
      } else {
         double a = (cur.y - prev.y) / (cur.x - prev.x);
         double b = prev.y - a * prev.x;
         return new Vector2d(a, b);
      }
   }

   public static double angle(Vector2d v) {
      if (v.x == 0.0D) {
         return 0.0D;
      } else {
         return v.y == 0.0D ? Double.POSITIVE_INFINITY : Math.abs(v.y) / Math.abs(v.x);
      }
   }

   public static double angle(Vector2d from, Vector2d to) {
      if (from.x == to.x) {
         return 0.0D;
      } else {
         return from.y == to.y ? Double.POSITIVE_INFINITY : Math.abs(to.y - from.y) / Math.abs(to.x - from.x);
      }
   }

   public static class FF {
      public Vector2d ft;
      public Vector2d fs;

      public FF() {
      }

      public void reset() {
         this.ft = null;
         this.fs = null;
      }
   }
}
