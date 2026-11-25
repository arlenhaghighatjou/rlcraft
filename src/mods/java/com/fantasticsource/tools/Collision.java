package com.fantasticsource.tools;

public class Collision {
   public static char ON = 0;
   public static char LEFT = 1;
   public static char RIGHT = 2;

   public Collision() {
   }

   public static boolean within(double n, double limit1, double limit2) {
      return limit1 <= n && n <= limit2 || limit2 <= n && n <= limit1;
   }

   public static boolean pointPoint(double x1, double y1, double x2, double y2) {
      return x1 == x2 && y1 == y2;
   }

   public static boolean pointRectangle(double x, double y, double rx1, double ry1, double rx2, double ry2) {
      return within(x, rx1, rx2) && within(y, ry1, ry2);
   }

   public static boolean rectangleRectangle(double r1x1, double r1y1, double r1x2, double r1y2, double r2x1, double r2y1, double r2x2, double r2y2) {
      return pointRectangle(r1x1, r1y1, r2x1, r2y1, r2x2, r2y2) || pointRectangle(r1x1, r1y2, r2x1, r2y1, r2x2, r2y2) || pointRectangle(r1x2, r1y1, r2x1, r2y1, r2x2, r2y2) || pointRectangle(r1x2, r1y2, r2x1, r2y1, r2x2, r2y2) || pointRectangle(r2x1, r2y1, r1x1, r1y1, r1x2, r1y2) || pointRectangle(r2x1, r2y2, r1x1, r1y1, r1x2, r1y2) || pointRectangle(r2x2, r2y1, r1x1, r1y1, r1x2, r1y2) || pointRectangle(r2x2, r2y2, r1x1, r1y1, r1x2, r1y2);
   }

   public static double[] rectangleRectangleExt(double r1x1, double r1y1, double r1x2, double r1y2, double r2x1, double r2y1, double r2x2, double r2y2) {
      if (r1x1 > r1x2) {
         double swap = r1x1;
         r1x1 = r1x2;
         r1x2 = swap;
      }

      if (r1y1 > r1y2) {
         double swap = r1y1;
         r1y1 = r1y2;
         r1y2 = swap;
      }

      if (r2x1 > r2x2) {
         double swap = r2x1;
         r2x1 = r2x2;
         r2x2 = swap;
      }

      if (r2y1 > r2y2) {
         double swap = r2y1;
         r2y1 = r2y2;
         r2y2 = swap;
      }

      if (within(r1x1, r2x1, r2x2)) {
         if (!within(r1x2, r2x1, r2x2)) {
            if (within(r1y1, r2y1, r2y2)) {
               if (within(r1y2, r2y1, r2y2)) {
                  return new double[]{r1x1, r1y1, r2x2, r1y2};
               }

               return new double[]{r1x1, r1y1, r2x2, r2y2};
            }

            if (within(r1y2, r2y1, r2y2)) {
               return new double[]{r1x1, r2y1, r2x2, r1y2};
            }

            return new double[]{r1x1, r2y1, r2x2, r2y2};
         }

         if (within(r1y1, r2y1, r2y2)) {
            if (within(r1y2, r2y1, r2y2)) {
               return new double[]{r1x1, r1y1, r1x2, r1y2};
            }

            return new double[]{r1x1, r1y1, r1x2, r2y1};
         }

         if (within(r1y2, r2y1, r2y2)) {
            return new double[]{r1x1, r2y2, r1x2, r1y2};
         }

         if (within(r2y1, r1y1, r1y2)) {
            return new double[]{r1x1, r2y1, r1x2, r2y2};
         }
      } else if (within(r1x2, r2x1, r2x2)) {
         if (within(r1y1, r2y1, r2y2)) {
            if (within(r1y2, r2y1, r2y2)) {
               return new double[]{r2x1, r1y1, r1x2, r1y2};
            }

            return new double[]{r2x1, r1y1, r1x2, r2y2};
         }

         if (within(r1y2, r2y1, r2y2)) {
            return new double[]{r2x1, r2y1, r1x2, r1y2};
         }

         if (within(r2y1, r1y1, r1y2)) {
            return new double[]{r2x1, r2y1, r1x2, r2y2};
         }
      } else {
         if (within(r1y1, r2y1, r2y2)) {
            if (within(r1y2, r2y1, r2y2)) {
               return new double[]{r2x1, r1y1, r2x2, r1y2};
            }

            return new double[]{r2x1, r1y1, r2x2, r2y2};
         }

         if (within(r1y2, r2y1, r2y2)) {
            return new double[]{r2x1, r2y1, r2x2, r1y2};
         }

         if (pointRectangle(r2x1, r2y1, r1x1, r1y1, r1x2, r1y2)) {
            return new double[]{r2x1, r2y1, r2x2, r2y2};
         }
      }

      return null;
   }

   public static boolean colinear(double x1, double y1, double x2, double y2, double x3, double y3) {
      return pointLine(x1, y1, x2, y2, x3, y3);
   }

   public static boolean pointLine(double x, double y, double linex1, double liney1, double linex2, double liney2) {
      return (linex1 - x) * (liney2 - y) == (linex2 - x) * (liney1 - y);
   }

   public static char pointLineSide(double x, double y, double linex1, double liney1, double linex2, double liney2, boolean upNegative) {
      double d = (linex2 - x) * (liney1 - y) - (linex1 - x) * (liney2 - y);
      if (d > 0.0D) {
         return upNegative ? LEFT : RIGHT;
      } else if (d < 0.0D) {
         return upNegative ? RIGHT : LEFT;
      } else {
         return ON;
      }
   }

   public static boolean pointSegment(double x, double y, double sx1, double sy1, double sx2, double sy2) {
      return pointRectangle(x, y, sx1, sy1, sx2, sy2) && pointLine(x, y, sx1, sy1, sx2, sy2);
   }

   public static boolean pointConvexPolygon(double x, double y, double[] points) {
      char otherDir = pointLineSide(points[4], points[5], points[0], points[1], points[2], points[3], false);

      for(int i = 2; i < points.length; i += 2) {
         if (pointLineSide(x, y, points[i - 2], points[i - 1], points[i], points[i + 1], true) == otherDir) {
            return false;
         }
      }

      return pointLineSide(x, y, points[points.length - 2], points[points.length - 1], points[0], points[1], true) != otherDir;
   }

   public static double[] lineLine(double line1x1, double line1y1, double line1x2, double line1y2, double line2x1, double line2y1, double line2x2, double line2y2) {
      boolean line1Point = line1x1 == line1x2 && line1y1 == line1y2;
      boolean line2Point = line2x1 == line2x2 && line2y1 == line2y2;
      if (line1Point) {
         if (line2Point) {
            return line1x1 == line2x1 && line1y1 == line2y1 ? new double[]{line1x1, line1y1} : null;
         } else {
            return pointLine(line1x1, line1y1, line2x1, line2y1, line2x2, line2y2) ? new double[]{line1x1, line1y1} : null;
         }
      } else if (line2Point) {
         return pointLine(line2x1, line2y1, line1x1, line1y1, line1x2, line1y2) ? new double[]{line2x1, line2y1} : null;
      } else {
         double xDist1 = line1x2 - line1x1;
         double yDist1 = line1y2 - line1y1;
         double xDist2 = line2x2 - line2x1;
         double yDist2 = line2y2 - line2y1;
         double denominator = -xDist2 * yDist1 + xDist1 * yDist2;
         if (denominator == 0.0D) {
            return colinear(line1x1, line1y1, line1x2, line1y2, line2x1, line2y1) ? new double[]{Double.NaN, Double.NaN} : null;
         } else {
            double numerator1 = xDist2 * (line1y1 - line2y1) - yDist2 * (line1x1 - line2x1);
            double scalar1 = numerator1 / denominator;
            return new double[]{line1x1 + scalar1 * xDist1, line1y1 + scalar1 * yDist1};
         }
      }
   }

   public static double[] segmentLine(double sx1, double sy1, double sx2, double sy2, double lx1, double ly1, double lx2, double ly2) {
      if (lx1 == lx2 && ly1 == ly2) {
         return pointSegment(lx1, ly1, sx1, sy1, sx2, sy2) ? new double[]{lx1, ly1} : null;
      } else if (sx1 == sx2 && sy1 == sy2 && pointLine(sx1, sy1, lx1, ly1, lx2, ly2)) {
         return new double[]{sx1, sy1};
      } else {
         double[] p1 = lineLine(lx1, ly1, lx2, ly2, sx1, sy1, sx2, sy2);
         if (p1 == null) {
            return null;
         } else if (Double.isNaN(p1[1])) {
            return new double[]{sx1, sy1, sx2, sy2};
         } else {
            return pointRectangle(p1[0], p1[1], sx1, sy1, sx2, sy2) ? p1 : null;
         }
      }
   }

   public static boolean segmentSegment(double s1x1, double s1y1, double s1x2, double s1y2, double s2x1, double s2y1, double s2x2, double s2y2) {
      return segmentSegmentExt(s1x1, s1y1, s1x2, s1y2, s2x1, s2y1, s2x2, s2y2)[0] >= 8.0D;
   }

   public static double[] segmentSegmentExt(double s1x1, double s1y1, double s1x2, double s1y2, double s2x1, double s2y1, double s2x2, double s2y2) {
      double xDist1 = s1x2 - s1x1;
      double yDist1 = s1y2 - s1y1;
      double xDist2 = s2x2 - s2x1;
      double yDist2 = s2y2 - s2y1;
      double denominator = -xDist2 * yDist1 + xDist1 * yDist2;
      if (denominator != 0.0D) {
         double numerator1 = xDist2 * (s1y1 - s2y1) - yDist2 * (s1x1 - s2x1);
         double numerator2 = -yDist1 * (s1x1 - s2x1) + xDist1 * (s1y1 - s2y1);
         if (denominator > 0.0D) {
            if (numerator2 >= 0.0D && numerator2 <= denominator && numerator1 >= 0.0D && numerator1 <= denominator) {
               double scalar1 = numerator1 / denominator;
               return new double[]{11.0D, s1x1 + scalar1 * xDist1, s1y1 + scalar1 * yDist1};
            }
         } else if (numerator2 >= denominator && numerator2 <= 0.0D && numerator1 >= denominator && numerator1 <= 0.0D) {
            double scalar1 = numerator1 / denominator;
            return new double[]{11.0D, s1x1 + scalar1 * xDist1, s1y1 + scalar1 * yDist1};
         }

         return new double[]{3.0D};
      } else if (xDist1 == 0.0D && yDist1 == 0.0D) {
         if (xDist2 == 0.0D && yDist2 == 0.0D) {
            return s1x1 == s2x1 && s1y1 == s2y1 ? new double[]{8.0D, s1x1, s1y1} : new double[]{0.0D};
         } else {
            return pointSegment(s1x1, s1y1, s2x1, s2y1, s2x2, s2y2) ? new double[]{10.0D, s1x1, s1y1} : new double[]{2.0D};
         }
      } else if (xDist2 == 0.0D && yDist2 == 0.0D) {
         return pointSegment(s2x1, s2y1, s1x1, s1y1, s1x2, s1y2) ? new double[]{9.0D, s2x1, s2y1} : new double[]{1.0D};
      } else if (!colinear(s1x1, s1y1, s1x2, s1y2, s2x1, s2y1)) {
         return new double[]{4.0D};
      } else {
         boolean s1p1ONs2 = pointRectangle(s1x1, s1y1, s2x1, s2y1, s2x2, s2y2);
         boolean s1p2ONs2 = pointRectangle(s1x2, s1y2, s2x1, s2y1, s2x2, s2y2);
         boolean s2p1ONs1 = pointRectangle(s2x1, s2y1, s1x1, s1y1, s1x2, s1y2);
         boolean s2p2ONs1 = pointRectangle(s2x2, s2y2, s1x1, s1y1, s1x2, s1y2);
         double[] result = new double[5];
         if (s1p1ONs2 && s1p2ONs2) {
            result[0] = 31.0D;
            result[1] = s1x1;
            result[2] = s1y1;
            result[3] = s1x2;
            result[4] = s1y2;
            return result;
         } else if (s2p1ONs1 && s2p2ONs1) {
            result[0] = 31.0D;
            if ((s1x2 - s1x1 >= 0.0D || s2x2 - s2x1 >= 0.0D) && (s1x2 - s1x1 <= 0.0D || s2x2 - s2x1 <= 0.0D) && (s1y2 - s1y1 >= 0.0D || s2y2 - s2y1 >= 0.0D) && (s1y2 - s1y1 <= 0.0D || s2y2 - s2y1 <= 0.0D)) {
               result[1] = s2x2;
               result[2] = s2y2;
               result[3] = s2x1;
               result[4] = s2y1;
            } else {
               result[1] = s2x1;
               result[2] = s2y1;
               result[3] = s2x2;
               result[4] = s2y2;
            }

            return result;
         } else {
            result[0] = 15.0D;
            if (s1p1ONs2) {
               result[1] = s1x1;
               result[2] = s1y1;
               if (s2p1ONs1) {
                  result[3] = s2x1;
                  result[4] = s2y1;
               } else {
                  result[3] = s2x2;
                  result[4] = s2y2;
               }
            } else {
               result[3] = s1x2;
               result[4] = s1y2;
               if (s2p1ONs1) {
                  result[1] = s2x1;
                  result[2] = s2y1;
               } else {
                  result[1] = s2x2;
                  result[2] = s2y2;
               }
            }

            if (result[1] != result[3] || result[2] != result[4]) {
               result[0] += 16.0D;
            }

            return result;
         }
      }
   }

   public static double[] lineRectangle(double x1, double y1, double x2, double y2, double rx1, double ry1, double rx2, double ry2) {
      if (x1 == x2 && y1 == y2) {
         return pointRectangle(x1, y1, rx1, ry1, rx2, ry2) ? new double[]{x1, y1} : null;
      } else if (rx1 == rx2) {
         if (ry1 == ry2) {
            return pointLine(rx1, ry1, x1, y1, x2, y2) ? new double[]{rx1, ry1} : null;
         } else {
            return segmentLine(rx1, ry1, rx2, ry2, x1, y1, x2, y2);
         }
      } else if (ry1 == ry2) {
         return segmentLine(rx1, ry1, rx2, ry2, x1, y1, x2, y2);
      } else {
         double[] points = segmentLine(rx1, ry1, rx2, ry1, x1, y1, x2, y2);
         double[] result = new double[4];
         boolean onePoint = false;
         if (points != null) {
            if (points.length == 4) {
               return points;
            }

            result[0] = points[0];
            result[1] = points[1];
            onePoint = true;
         }

         points = segmentLine(rx1, ry2, rx2, ry2, x1, y1, x2, y2);
         if (points != null) {
            if (points.length == 4) {
               return points;
            }

            if (onePoint) {
               result[2] = points[0];
               result[3] = points[1];
               return result;
            }

            result[0] = points[0];
            result[1] = points[1];
            onePoint = true;
         }

         points = segmentLine(rx1, ry1, rx1, ry2, x1, y1, x2, y2);
         if (points != null) {
            if (points.length == 4) {
               return points;
            }

            if (onePoint) {
               if (points[0] != result[0] || points[1] != result[1]) {
                  result[2] = points[0];
                  result[3] = points[1];
                  return result;
               }
            } else {
               result[0] = points[0];
               result[1] = points[1];
               onePoint = true;
            }
         }

         points = segmentLine(rx2, ry1, rx2, ry2, x1, y1, x2, y2);
         if (points != null) {
            if (points.length == 4) {
               return points;
            }

            if (onePoint) {
               if (points[0] != result[0] || points[1] != result[1]) {
                  result[2] = points[0];
                  result[3] = points[1];
                  return result;
               }
            } else {
               result[0] = points[0];
               result[1] = points[1];
               onePoint = true;
            }
         }

         if (onePoint) {
            return new double[]{result[0], result[1]};
         } else {
            return null;
         }
      }
   }

   public static double[] segmentRectangle(double x1, double y1, double x2, double y2, double rx1, double ry1, double rx2, double ry2) {
      if (x1 == x2 && y1 == y2) {
         return pointRectangle(x1, y1, rx1, ry1, rx2, ry2) ? new double[]{x1, y1} : null;
      } else if (rx1 == rx2) {
         if (ry1 == ry2) {
            return pointSegment(rx1, ry1, x1, y1, x2, y2) ? new double[]{rx1, ry1} : null;
         } else {
            double[] result = segmentSegmentExt(x1, y1, x2, y2, rx1, ry1, rx2, ry2);
            if (result.length == 1) {
               return null;
            } else {
               return result.length == 3 ? new double[]{result[1], result[2]} : new double[]{result[1], result[2], result[3], result[4]};
            }
         }
      } else if (ry1 == ry2) {
         double[] result = segmentSegmentExt(x1, y1, x2, y2, rx1, ry1, rx2, ry2);
         if (result.length == 1) {
            return null;
         } else {
            return result.length == 3 ? new double[]{result[1], result[2]} : new double[]{result[1], result[2], result[3], result[4]};
         }
      } else if (pointRectangle(x1, y1, rx1, ry1, rx2, ry2)) {
         if (pointRectangle(x2, y2, rx1, ry1, rx2, ry2)) {
            return new double[]{x1, y1, x2, y2};
         } else {
            double[] result = segmentSegmentExt(x1, y1, x2, y2, rx1, ry1, rx2, ry1);
            if (result.length == 5) {
               return new double[]{result[1], result[2], result[3], result[4]};
            } else if (result[1] == x1 && result[2] == y1) {
               result = segmentSegmentExt(x1, y1, x2, y2, rx1, ry2, rx2, ry2);
               if (result.length == 5) {
                  return new double[]{result[1], result[2], result[3], result[4]};
               } else if (result[1] == x1 && result[2] == y1) {
                  result = segmentSegmentExt(x1, y1, x2, y2, rx1, ry1, rx1, ry2);
                  if (result.length == 5) {
                     return new double[]{result[1], result[2], result[3], result[4]};
                  } else if (result[1] == x1 && result[2] == y1) {
                     result = segmentSegmentExt(x1, y1, x2, y2, rx2, ry1, rx2, ry2);
                     if (result.length == 5) {
                        return new double[]{result[1], result[2], result[3], result[4]};
                     } else {
                        return result[1] == x1 && result[2] == y1 ? new double[]{x1, y1} : new double[]{x1, y1, result[1], result[2]};
                     }
                  } else {
                     return new double[]{x1, y1, result[1], result[2]};
                  }
               } else {
                  return new double[]{x1, y1, result[1], result[2]};
               }
            } else {
               return new double[]{x1, y1, result[1], result[2]};
            }
         }
      } else if (pointRectangle(x2, y2, rx1, ry1, rx2, ry2)) {
         double[] result = segmentSegmentExt(x1, y1, x2, y2, rx1, ry1, rx2, ry1);
         if (result.length == 5) {
            return new double[]{result[1], result[2], result[3], result[4]};
         } else if (result[1] == x2 && result[2] == y2) {
            result = segmentSegmentExt(x1, y1, x2, y2, rx1, ry2, rx2, ry2);
            if (result.length == 5) {
               return new double[]{result[1], result[2], result[3], result[4]};
            } else if (result[1] == x2 && result[2] == y2) {
               result = segmentSegmentExt(x1, y1, x2, y2, rx1, ry1, rx1, ry2);
               if (result.length == 5) {
                  return new double[]{result[1], result[2], result[3], result[4]};
               } else if (result[1] == x2 && result[2] == y2) {
                  result = segmentSegmentExt(x1, y1, x2, y2, rx2, ry1, rx2, ry2);
                  if (result.length == 5) {
                     return new double[]{result[1], result[2], result[3], result[4]};
                  } else {
                     return result[1] == x2 && result[2] == y2 ? new double[]{x2, y2} : new double[]{result[1], result[2], x2, y2};
                  }
               } else {
                  return new double[]{result[1], result[2], x2, y2};
               }
            } else {
               return new double[]{result[1], result[2], x2, y2};
            }
         } else {
            return new double[]{result[1], result[2], x2, y2};
         }
      } else {
         double[] result = segmentSegmentExt(x1, y1, x2, y2, rx1, ry1, rx2, ry1);
         if (result.length == 5) {
            return new double[]{result[1], result[2], result[3], result[4]};
         } else {
            double[] prevResult = null;
            if (result.length == 3) {
               prevResult = result;
            }

            result = segmentSegmentExt(x1, y1, x2, y2, rx1, ry2, rx2, ry2);
            if (result.length == 5) {
               return new double[]{result[1], result[2], result[3], result[4]};
            } else {
               if (result.length == 3) {
                  if (prevResult == null) {
                     prevResult = result;
                  } else if (prevResult[1] != result[1] || prevResult[2] != result[2]) {
                     double[] nearest = pointListNearest(x1, y1, prevResult[1], prevResult[2], result[1], result[2]);
                     return result[1] == nearest[0] && result[2] == nearest[1] ? new double[]{nearest[0], nearest[1], prevResult[1], prevResult[2]} : new double[]{nearest[0], nearest[1], result[1], result[2]};
                  }
               }

               result = segmentSegmentExt(x1, y1, x2, y2, rx1, ry1, rx1, ry2);
               if (result.length == 5) {
                  return new double[]{result[1], result[2], result[3], result[4]};
               } else {
                  if (result.length == 3) {
                     if (prevResult == null) {
                        prevResult = result;
                     } else if (prevResult[1] != result[1] || prevResult[2] != result[2]) {
                        double[] nearest = pointListNearest(x1, y1, prevResult[1], prevResult[2], result[1], result[2]);
                        if (result[1] == nearest[0] && result[2] == nearest[1]) {
                           return new double[]{nearest[0], nearest[1], prevResult[1], prevResult[2]};
                        }

                        return new double[]{nearest[0], nearest[1], result[1], result[2]};
                     }
                  }

                  result = segmentSegmentExt(x1, y1, x2, y2, rx1, ry1, rx1, ry2);
                  if (result.length == 5) {
                     return new double[]{result[1], result[2], result[3], result[4]};
                  } else if (result.length != 3) {
                     if (prevResult != null) {
                        return new double[]{prevResult[1], prevResult[2]};
                     } else {
                        return null;
                     }
                  } else if (prevResult != null && (prevResult[1] != result[1] || prevResult[2] != result[2])) {
                     double[] nearest = pointListNearest(x1, y1, prevResult[1], prevResult[2], result[1], result[2]);
                     if (result[1] == nearest[0] && result[2] == nearest[1]) {
                        return new double[]{nearest[0], nearest[1], prevResult[1], prevResult[2]};
                     } else {
                        return new double[]{nearest[0], nearest[1], result[1], result[2]};
                     }
                  } else {
                     return new double[]{result[1], result[2]};
                  }
               }
            }
         }
      }
   }

   public static double[] segmentConvexPolygon(double x1, double y1, double x2, double y2, double[] points) {
      if (x1 == x2 && y1 == y2) {
         return pointConvexPolygon(x1, y1, points) ? new double[]{x1, y1} : null;
      } else if (pointConvexPolygon(x1, y1, points)) {
         if (pointConvexPolygon(x2, y2, points)) {
            return new double[]{x1, y1, x2, y2};
         } else {
            int i = 2;

            while(true) {
               if (i < points.length) {
                  double[] result = segmentSegmentExt(x1, y1, x2, y2, points[i - 2], points[i - 1], points[i], points[i + 1]);
                  if (result.length == 5) {
                     return new double[]{result[1], result[2], result[3], result[4]};
                  }

                  if (result.length != 3 || result[1] == x1 && result[2] == y1) {
                     i += 2;
                     continue;
                  }

                  return new double[]{x1, y1, result[1], result[2]};
               }

               double[] result = segmentSegmentExt(x1, y1, x2, y2, points[points.length - 2], points[points.length - 1], points[0], points[1]);
               if (result.length == 5) {
                  return new double[]{result[1], result[2], result[3], result[4]};
               }

               if (result.length != 3 || result[1] == x1 && result[2] == y1) {
                  return new double[]{x1, y1};
               }

               return new double[]{x1, y1, result[1], result[2]};
            }
         }
      } else if (pointConvexPolygon(x2, y2, points)) {
         int i = 2;

         while(true) {
            if (i < points.length) {
               double[] result = segmentSegmentExt(x1, y1, x2, y2, points[i - 2], points[i - 1], points[i], points[i + 1]);
               if (result.length == 5) {
                  return new double[]{result[1], result[2], result[3], result[4]};
               }

               if (result.length != 3 || result[1] == x2 && result[2] == y2) {
                  i += 2;
                  continue;
               }

               return new double[]{result[1], result[2], x2, y2};
            }

            double[] result = segmentSegmentExt(x1, y1, x2, y2, points[points.length - 2], points[points.length - 1], points[0], points[1]);
            if (result.length == 5) {
               return new double[]{result[1], result[2], result[3], result[4]};
            }

            if (result.length != 3 || result[1] == x2 && result[2] == y2) {
               return new double[]{x2, y2};
            }

            return new double[]{result[1], result[2], x2, y2};
         }
      } else {
         double[] prevResult = null;

         for(int i = 2; i < points.length; i += 2) {
            double[] result = segmentSegmentExt(x1, y1, x2, y2, points[i - 2], points[i - 1], points[i], points[i + 1]);
            if (result.length == 5) {
               return new double[]{result[1], result[2], result[3], result[4]};
            }

            if (result.length == 3) {
               if (prevResult == null) {
                  prevResult = result;
               } else if (result[1] != prevResult[1] || result[2] != prevResult[2]) {
                  double[] nearest = pointListNearest(x1, y1, result[1], result[2], prevResult[1], prevResult[2]);
                  if (result[1] == nearest[0] && result[2] == nearest[1]) {
                     return new double[]{nearest[0], nearest[1], prevResult[1], prevResult[2]};
                  }

                  return new double[]{nearest[0], nearest[1], result[1], result[2]};
               }
            }
         }

         double[] result = segmentSegmentExt(x1, y1, x2, y2, points[points.length - 2], points[points.length - 1], points[0], points[1]);
         if (result.length == 5) {
            return new double[]{result[1], result[2], result[3], result[4]};
         } else if (result.length != 3) {
            return null;
         } else if (prevResult != null && (result[1] != prevResult[1] || result[2] != prevResult[2])) {
            double[] nearest = pointListNearest(x1, y1, result[1], result[2], prevResult[1], prevResult[2]);
            if (result[1] == nearest[0] && result[2] == nearest[1]) {
               return new double[]{nearest[0], nearest[1], prevResult[1], prevResult[2]};
            } else {
               return new double[]{nearest[0], nearest[1], result[1], result[2]};
            }
         } else {
            return new double[]{result[1], result[2]};
         }
      }
   }

   public static double[][] segmentConvexPolygonExt(double x1, double y1, double x2, double y2, double[] points) {
      if (x1 == x2 && y1 == y2) {
         return pointConvexPolygon(x1, y1, points) ? new double[][]{{x1, y1}, null, null} : (double[][])null;
      } else {
         boolean startCoords = false;
         boolean endCoords = false;
         double[] finalResult = new double[4];
         double[] startSides = null;
         double[] midSide = null;
         double[] endSides = null;
         if (pointConvexPolygon(x1, y1, points)) {
            if (pointConvexPolygon(x2, y2, points)) {
               return new double[][]{{x1, y1, x2, y2}, null, null};
            } else {
               finalResult[0] = x1;
               finalResult[1] = y1;

               for(int i = 2; i < points.length; i += 2) {
                  double[] result = segmentSegmentExt(x1, y1, x2, y2, points[i - 2], points[i - 1], points[i], points[i + 1]);
                  if (result.length == 5) {
                     finalResult[2] = result[3];
                     finalResult[3] = result[4];
                     endCoords = true;
                     midSide = new double[]{points[i - 2], points[i - 1], points[i], points[i + 1]};
                  }

                  if (result.length == 3) {
                     if (result[1] == x1 && result[2] == y1) {
                        if (startSides == null) {
                           startSides = new double[]{points[i - 2], points[i - 1], points[i], points[i + 1]};
                        } else {
                           startSides = new double[]{startSides[0], startSides[1], startSides[2], startSides[3], points[i - 2], points[i - 1], points[i], points[i + 1]};
                        }
                     } else {
                        finalResult[2] = result[1];
                        finalResult[3] = result[2];
                        endCoords = true;
                        if (endSides == null) {
                           endSides = new double[]{points[i - 2], points[i - 1], points[i], points[i + 1]};
                        } else {
                           endSides = new double[]{endSides[0], endSides[1], endSides[2], endSides[3], points[i - 2], points[i - 1], points[i], points[i + 1]};
                        }
                     }
                  }
               }

               double[] result = segmentSegmentExt(x1, y1, x2, y2, points[points.length - 2], points[points.length - 1], points[0], points[1]);
               if (result.length == 5) {
                  finalResult[2] = result[3];
                  finalResult[3] = result[4];
                  endCoords = true;
                  midSide = new double[]{points[points.length - 2], points[points.length - 1], points[0], points[1]};
               }

               if (result.length == 3) {
                  if (result[1] == x1 && result[2] == y1) {
                     if (startSides == null) {
                        startSides = new double[]{points[points.length - 2], points[points.length - 1], points[0], points[1]};
                     } else {
                        startSides = new double[]{startSides[0], startSides[1], startSides[2], startSides[3], points[points.length - 2], points[points.length - 1], points[0], points[1]};
                     }
                  } else {
                     finalResult[2] = result[1];
                     finalResult[3] = result[2];
                     endCoords = true;
                     if (endSides == null) {
                        endSides = new double[]{points[points.length - 2], points[points.length - 1], points[0], points[1]};
                     } else {
                        endSides = new double[]{endSides[0], endSides[1], endSides[2], endSides[3], points[points.length - 2], points[points.length - 1], points[0], points[1]};
                     }
                  }
               }

               return endCoords ? new double[][]{finalResult, startSides, midSide, endSides} : new double[][]{{finalResult[0], finalResult[1]}, startSides, null, null};
            }
         } else if (pointConvexPolygon(x2, y2, points)) {
            finalResult[2] = x2;
            finalResult[3] = y2;

            for(int i = 2; i < points.length; i += 2) {
               double[] result = segmentSegmentExt(x1, y1, x2, y2, points[i - 2], points[i - 1], points[i], points[i + 1]);
               if (result.length == 5) {
                  finalResult[0] = result[1];
                  finalResult[1] = result[2];
                  startCoords = true;
                  midSide = new double[]{points[i - 2], points[i - 1], points[i], points[i + 1]};
               }

               if (result.length == 3) {
                  if (result[1] == x2 && result[2] == y2) {
                     if (endSides == null) {
                        endSides = new double[]{points[i - 2], points[i - 1], points[i], points[i + 1]};
                     } else {
                        endSides = new double[]{endSides[0], endSides[1], endSides[2], endSides[3], points[i - 2], points[i - 1], points[i], points[i + 1]};
                     }
                  } else {
                     finalResult[0] = result[1];
                     finalResult[1] = result[2];
                     startCoords = true;
                     if (startSides == null) {
                        startSides = new double[]{points[i - 2], points[i - 1], points[i], points[i + 1]};
                     } else {
                        startSides = new double[]{startSides[0], startSides[1], startSides[2], startSides[3], points[i - 2], points[i - 1], points[i], points[i + 1]};
                     }
                  }
               }
            }

            double[] result = segmentSegmentExt(x1, y1, x2, y2, points[points.length - 2], points[points.length - 1], points[0], points[1]);
            if (result.length == 5) {
               finalResult[0] = result[1];
               finalResult[1] = result[2];
               startCoords = true;
               midSide = new double[]{points[points.length - 2], points[points.length - 1], points[0], points[1]};
            }

            if (result.length == 3) {
               if (result[1] == x2 && result[2] == y2) {
                  if (endSides == null) {
                     endSides = new double[]{points[points.length - 2], points[points.length - 1], points[0], points[1]};
                  } else {
                     endSides = new double[]{endSides[0], endSides[1], endSides[2], endSides[3], points[points.length - 2], points[points.length - 1], points[0], points[1]};
                  }
               } else {
                  finalResult[0] = result[1];
                  finalResult[1] = result[2];
                  startCoords = true;
                  if (startSides == null) {
                     startSides = new double[]{points[points.length - 2], points[points.length - 1], points[0], points[1]};
                  } else {
                     startSides = new double[]{startSides[0], startSides[1], startSides[2], startSides[3], points[points.length - 2], points[points.length - 1], points[0], points[1]};
                  }
               }
            }

            return startCoords ? new double[][]{finalResult, startSides, midSide, endSides} : new double[][]{{finalResult[2], finalResult[3]}, endSides, null, null};
         } else {
            for(int i = 2; i < points.length; i += 2) {
               double[] result = segmentSegmentExt(x1, y1, x2, y2, points[i - 2], points[i - 1], points[i], points[i + 1]);
               if (result.length == 5) {
                  if (startCoords && (finalResult[0] != result[1] || finalResult[1] != result[2])) {
                     endSides = startSides;
                     startSides = null;
                  }

                  finalResult[0] = result[1];
                  finalResult[1] = result[2];
                  finalResult[2] = result[3];
                  finalResult[3] = result[4];
                  startCoords = true;
                  endCoords = true;
                  midSide = new double[]{points[i - 2], points[i - 1], points[i], points[i + 1]};
               }

               if (result.length == 3) {
                  if (!startCoords) {
                     finalResult[0] = result[1];
                     finalResult[1] = result[2];
                     startCoords = true;
                     startSides = new double[]{points[i - 2], points[i - 1], points[i], points[i + 1]};
                  } else if (finalResult[0] == result[1] && finalResult[1] == result[2]) {
                     if (startSides == null) {
                        startSides = new double[]{points[i - 2], points[i - 1], points[i], points[i + 1]};
                     } else {
                        startSides = new double[]{startSides[0], startSides[1], startSides[2], startSides[3], points[i - 2], points[i - 1], points[i], points[i + 1]};
                     }
                  } else if (endCoords && finalResult[2] == result[1] && finalResult[3] == result[2]) {
                     if (endSides == null) {
                        endSides = new double[]{points[i - 2], points[i - 1], points[i], points[i + 1]};
                     } else {
                        endSides = new double[]{endSides[0], endSides[1], endSides[2], endSides[3], points[i - 2], points[i - 1], points[i], points[i + 1]};
                     }
                  } else {
                     endCoords = true;
                     double[] nearest = pointListNearest(x1, y1, finalResult[0], finalResult[1], result[1], result[2]);
                     if (nearest[0] == finalResult[0] && nearest[1] == finalResult[1]) {
                        finalResult[2] = result[1];
                        finalResult[3] = result[2];
                        endSides = new double[]{points[i - 2], points[i - 1], points[i], points[i + 1]};
                     } else {
                        finalResult[2] = finalResult[0];
                        finalResult[3] = finalResult[1];
                        finalResult[0] = result[1];
                        finalResult[1] = result[2];
                        endSides = startSides;
                        startSides = new double[]{points[i - 2], points[i - 1], points[i], points[i + 1]};
                     }
                  }
               }
            }

            double[] result = segmentSegmentExt(x1, y1, x2, y2, points[points.length - 2], points[points.length - 1], points[0], points[1]);
            if (result.length == 5) {
               if (startCoords && (finalResult[0] != result[1] || finalResult[1] != result[2])) {
                  endSides = startSides;
                  startSides = null;
               }

               finalResult[0] = result[1];
               finalResult[1] = result[2];
               finalResult[2] = result[3];
               finalResult[3] = result[4];
               startCoords = true;
               endCoords = true;
               midSide = new double[]{points[points.length - 2], points[points.length - 1], points[0], points[1]};
            }

            if (result.length == 3) {
               if (!startCoords) {
                  finalResult[0] = result[1];
                  finalResult[1] = result[2];
                  startCoords = true;
                  startSides = new double[]{points[points.length - 2], points[points.length - 1], points[0], points[1]};
               } else if (finalResult[0] == result[1] && finalResult[1] == result[2]) {
                  if (startSides == null) {
                     startSides = new double[]{points[points.length - 2], points[points.length - 1], points[0], points[1]};
                  } else {
                     startSides = new double[]{startSides[0], startSides[1], startSides[2], startSides[3], points[points.length - 2], points[points.length - 1], points[0], points[1]};
                  }
               } else if (endCoords && finalResult[2] == result[1] && finalResult[3] == result[2]) {
                  if (endSides == null) {
                     endSides = new double[]{points[points.length - 2], points[points.length - 1], points[0], points[1]};
                  } else {
                     endSides = new double[]{endSides[0], endSides[1], endSides[2], endSides[3], points[points.length - 2], points[points.length - 1], points[0], points[1]};
                  }
               } else {
                  endCoords = true;
                  double[] nearest = pointListNearest(x1, y1, finalResult[0], finalResult[1], result[1], result[2]);
                  if (nearest[0] == finalResult[0] && nearest[1] == finalResult[1]) {
                     finalResult[2] = result[1];
                     finalResult[3] = result[2];
                     endSides = new double[]{points[points.length - 2], points[points.length - 1], points[0], points[1]};
                  } else {
                     finalResult[2] = finalResult[0];
                     finalResult[3] = finalResult[1];
                     finalResult[0] = result[1];
                     finalResult[1] = result[2];
                     endSides = startSides;
                     startSides = new double[]{points[points.length - 2], points[points.length - 1], points[0], points[1]};
                  }
               }
            }

            if (!endCoords) {
               if (!startCoords) {
                  return (double[][])null;
               } else {
                  return new double[][]{{finalResult[0], finalResult[1]}, startSides, null, null};
               }
            } else {
               return new double[][]{finalResult, startSides, midSide, endSides};
            }
         }
      }
   }

   public static double[] lineConvexPolygon(double x1, double y1, double x2, double y2, double[] points) {
      if (x1 == x2 && y1 == y2) {
         return pointConvexPolygon(x1, y1, points) ? new double[]{x1, y1} : null;
      } else {
         double[] prevResult = null;

         for(int i = 2; i < points.length; i += 2) {
            double[] result = segmentLine(points[i - 2], points[i - 1], points[i], points[i + 1], x1, y1, x2, y2);
            if (result != null) {
               if (result.length == 4) {
                  return result;
               }

               if (prevResult == null) {
                  prevResult = result;
               } else if (result[0] != prevResult[0] || result[1] != prevResult[1]) {
                  return new double[]{result[0], result[1], prevResult[1], prevResult[2]};
               }
            }
         }

         double[] result = segmentLine(points[points.length - 2], points[points.length - 1], points[0], points[1], x1, y1, x2, y2);
         if (result != null) {
            if (result.length == 4) {
               return result;
            } else if (prevResult != null && (result[0] != prevResult[0] || result[1] != prevResult[1])) {
               return new double[]{result[0], result[1], prevResult[1], prevResult[2]};
            } else {
               return result;
            }
         } else {
            return prevResult;
         }
      }
   }

   public static double[] pointListNearest(double x, double y, double... arg) {
      if (arg != null && arg.length >= 2) {
         double distSquared = Math.pow(x - arg[0], 2.0D) + Math.pow(y - arg[1], 2.0D);
         double[] result = new double[]{arg[0], arg[1]};

         for(int i = 2; i + 1 < arg.length; i += 2) {
            double checkDistSquared = Math.pow(x - arg[i], 2.0D) + Math.pow(y - arg[i + 1], 2.0D);
            if (checkDistSquared < distSquared) {
               distSquared = checkDistSquared;
               result[0] = arg[i];
               result[1] = arg[i + 1];
            }
         }

         return result;
      } else {
         return null;
      }
   }

   public static boolean rectangleConvexPolygon(double x1, double y1, double x2, double y2, double[] points) {
      if (x1 == x2) {
         if (y1 == y2) {
            return pointConvexPolygon(x1, y1, points);
         } else {
            return segmentConvexPolygon(x1, y1, x2, y2, points) != null;
         }
      } else if (y1 == y2) {
         return segmentConvexPolygon(x1, y1, x2, y2, points) != null;
      } else {
         return pointRectangle(points[0], points[1], x1, y1, x2, y2) || segmentConvexPolygon(x1, y1, x2, y1, points) != null || segmentConvexPolygon(x2, y1, x2, y2, points) != null || segmentConvexPolygon(x2, y2, x1, y2, points) != null || segmentConvexPolygon(x1, y2, x1, y1, points) != null;
      }
   }

   public static boolean convexPolygonConvexPolygon(double[] points1, double[] points2) {
      for(int i = 2; i < points1.length; i += 2) {
         if (segmentConvexPolygon(points1[i - 2], points1[i - 1], points1[i], points1[i + 1], points2) != null) {
            return true;
         }
      }

      return segmentConvexPolygon(points1[points1.length - 2], points1[points1.length - 1], points1[0], points1[1], points2) != null;
   }

   public static double[] pointLineNearest(double x, double y, double x1, double y1, double x2, double y2) {
      return lineLine(x1, y1, x2, y2, x, y, x + y1 - y2, y - x1 + x2);
   }

   public static double[] pointSegmentNearest(double x, double y, double x1, double y1, double x2, double y2) {
      double[] p1 = pointLineNearest(x, y, x1, y1, x2, y2);
      if (p1 == null) {
         return null;
      } else if (pointRectangle(p1[0], p1[1], x1, y1, x2, y2)) {
         return p1;
      } else {
         return Math.pow(x1 - x, 2.0D) + Math.pow(y1 - y, 2.0D) < Math.pow(x2 - x, 2.0D) + Math.pow(y2 - y, 2.0D) ? new double[]{x1, y1} : new double[]{x2, y2};
      }
   }

   public static double[] pointRectangleNearest(double x, double y, double x1, double y1, double x2, double y2) {
      double[] result = new double[2];
      if (x1 > x2) {
         result[0] = x1;
         x1 = x2;
         x2 = result[0];
      }

      if (y1 > y2) {
         result[0] = y1;
         y1 = y2;
         y2 = result[0];
      }

      result[0] = x2;
      result[1] = y2;
      if (x < x1) {
         result[0] = x1;
      } else if (x < x2) {
         result[0] = x;
      }

      if (y < y1) {
         result[1] = y1;
      } else if (y < y2) {
         result[1] = y;
      }

      return result;
   }

   public static double[] pointConvexPolygonNearest(double x, double y, double[] points) {
      if (pointConvexPolygon(x, y, points)) {
         return new double[]{x, y};
      } else {
         double[] result = new double[points.length];
         double[] point = pointSegmentNearest(x, y, points[points.length - 2], points[points.length - 1], points[0], points[1]);
         System.arraycopy(point, 0, result, 0, 2);

         for(int i = 2; i < points.length; i += 2) {
            point = pointSegmentNearest(x, y, points[i - 2], points[i - 1], points[i], points[i + 1]);
            System.arraycopy(point, 0, result, i, 2);
         }

         return pointListNearest(x, y, result);
      }
   }

   public static boolean pointCircle(double x, double y, double cx, double cy, double r) {
      return Math.pow(cx - x, 2.0D) + Math.pow(cy - y, 2.0D) <= Math.pow(r, 2.0D);
   }

   public static double[] lineCircle(double x1, double y1, double x2, double y2, double cx, double cy, double r) {
      if (x1 == x2 && y1 == y2) {
         return pointCircle(x1, y1, cx, cy, r) ? new double[]{x1, y1} : null;
      } else {
         double rSquared = Math.pow(r, 2.0D);
         double[] p1 = pointLineNearest(cx, cy, x1, y1, x2, y2);
         double p1dSquared = Math.pow(p1[0] - cx, 2.0D) + Math.pow(p1[1] - cy, 2.0D);
         if (p1dSquared > rSquared) {
            return null;
         } else if (p1dSquared < rSquared) {
            double legLength = Math.sqrt(rSquared - p1dSquared);
            double p1d2 = Math.sqrt(Math.pow(p1[0] - x1, 2.0D) + Math.pow(p1[1] - y1, 2.0D));
            if (p1d2 != 0.0D) {
               double ratio = legLength / p1d2;
               double xd = ratio * (p1[0] - x1);
               double yd = ratio * (p1[1] - y1);
               return x2 >= x1 && pointCircle(x1, y1, cx, cy, r) ? new double[]{p1[0] + xd, p1[1] + yd, p1[0] - xd, p1[1] - yd} : new double[]{p1[0] - xd, p1[1] - yd, p1[0] + xd, p1[1] + yd};
            } else {
               p1d2 = Math.sqrt(Math.pow(p1[0] - x2, 2.0D) + Math.pow(p1[1] - y2, 2.0D));
               double ratio = legLength / p1d2;
               double xd = ratio * (p1[1] - x2);
               double yd = ratio * (p1[2] - y2);
               return new double[]{p1[0] + xd, p1[1] + yd, p1[0] - xd, p1[1] - yd};
            }
         } else {
            return p1;
         }
      }
   }

   public static double[] segmentCircle(double x1, double y1, double x2, double y2, double cx, double cy, double r) {
      if (x1 == x2 && y1 == y2) {
         return pointCircle(x1, y1, cx, cy, r) ? new double[]{x1, y1} : null;
      } else {
         double[] points = lineCircle(x1, y1, x2, y2, cx, cy, r);
         if (points == null) {
            return null;
         } else if (points.length == 2) {
            return pointRectangle(points[0], points[1], x1, y1, x2, y2) ? points : null;
         } else if (pointRectangle(x1, y1, points[0], points[1], points[2], points[3])) {
            if (pointRectangle(x2, y2, points[0], points[1], points[2], points[3])) {
               return new double[]{x1, y1, x2, y2};
            } else {
               double[] nearest = pointListNearest(x2, y2, points[0], points[1], points[2], points[3]);
               return nearest[0] == x1 && nearest[1] == y1 ? new double[]{x1, y1} : new double[]{x1, y1, nearest[0], nearest[1]};
            }
         } else if (pointRectangle(x2, y2, points[0], points[1], points[2], points[3])) {
            double[] nearest = pointListNearest(x1, y1, points[0], points[1], points[2], points[3]);
            return nearest[0] == x2 && nearest[1] == y2 ? new double[]{x2, y2} : new double[]{nearest[0], nearest[1], x2, y2};
         } else {
            return (points[0] != x1 || points[1] != y1 || points[2] != x2 || points[3] != y2) && (points[0] != x2 || points[1] != y2 || points[2] != x1 || points[3] != y1) ? null : points;
         }
      }
   }

   public static boolean rectangleCircle(double x1, double y1, double x2, double y2, double cx, double cy, double r) {
      if (pointRectangle(cx, cy, x1, y1, x2, y2)) {
         return true;
      } else if (segmentCircle(x1, y1, x2, y1, cx, cy, r) != null) {
         return true;
      } else if (segmentCircle(x1, y2, x2, y2, cx, cy, r) != null) {
         return true;
      } else if (segmentCircle(x1, y1, x1, y2, cx, cy, r) != null) {
         return true;
      } else {
         return segmentCircle(x2, y1, x2, y2, cx, cy, r) != null;
      }
   }

   public static boolean circleConvexPolygon(double cx, double cy, double r, double[] points) {
      if (pointConvexPolygon(cx, cy, points)) {
         return true;
      } else if (segmentCircle(points[points.length - 2], points[points.length - 1], points[0], points[1], cx, cy, r) != null) {
         return true;
      } else {
         for(int i = 2; i < points.length; i += 2) {
            if (segmentCircle(points[i - 2], points[i - 1], points[i], points[i + 1], cx, cy, r) != null) {
               return true;
            }
         }

         return false;
      }
   }

   public static double[] pointCircleNearest(double x, double y, double cx, double cy, double r) {
      double[] points = segmentCircle(x, y, cx, cy, cx, cy, r);
      return new double[]{points[0], points[1]};
   }

   public static double[] circleTan2Center(double cx, double cy, double r, double l1x1, double l1y1, double l1x2, double l1y2, double l2x1, double l2y1, double l2x2, double l2y2) {
      if ((l1x1 != l1x2 || l1y1 != l1y2) && (l2x1 != l2x2 || l2y1 != l2y2)) {
         double[] intersection = lineLine(l1x1, l1y1, l1x2, l1y2, l2x1, l2y1, l2x2, l2y2);
         if (intersection != null && !Double.isNaN(intersection[0])) {
            double dist = Math.sqrt(Math.pow(l1x1 - l1x2, 2.0D) + Math.pow(l1y1 - l1y2, 2.0D));
            double ratio = r / dist;
            double dx = (l1y2 - l1y1) * ratio;
            double dy = (l1x1 - l1x2) * ratio;
            double dir = (double)pointLineSide(cx, cy, l1x1, l1y1, l1x2, l1y2, true);
            double oldX = l1x1;
            double oldY = l1y1;
            l1x1 = l1x1 + dx;
            l1x2 = l1x2 + dx;
            l1y1 = l1y1 + dy;
            l1y2 = l1y2 + dy;
            if (dir == (double)pointLineSide(oldX, oldY, l1x1, l1y1, l1x2, l1y2, true)) {
               l1x1 -= dx * 2.0D;
               l1x2 -= dx * 2.0D;
               l1y1 -= dy * 2.0D;
               l1y2 -= dy * 2.0D;
            }

            dist = Math.sqrt(Math.pow(l2x1 - l2x2, 2.0D) + Math.pow(l2y1 - l2y2, 2.0D));
            ratio = r / dist;
            dx = (l2y2 - l2y1) * ratio;
            dy = (l2x1 - l2x2) * ratio;
            dir = (double)pointLineSide(cx, cy, l2x1, l2y1, l2x2, l2y2, true);
            oldX = l2x1;
            oldY = l2y1;
            l2x1 = l2x1 + dx;
            l2x2 = l2x2 + dx;
            l2y1 = l2y1 + dy;
            l2y2 = l2y2 + dy;
            if (dir == (double)pointLineSide(oldX, oldY, l2x1, l2y1, l2x2, l2y2, true)) {
               l2x1 -= dx * 2.0D;
               l2x2 -= dx * 2.0D;
               l2y1 -= dy * 2.0D;
               l2y2 -= dy * 2.0D;
            }

            return lineLine(l1x1, l1y1, l1x2, l1y2, l2x1, l2y1, l2x2, l2y2);
         } else {
            return null;
         }
      } else {
         return null;
      }
   }

   public static double[] mirror(double x1, double y1, double x2, double y2, double[] points) {
      double[] result = new double[points.length];

      for(int i = 0; i < points.length; i += 2) {
         double[] nearest = pointLineNearest(points[i], points[i + 1], x1, y1, x2, y2);
         result[i] = nearest[0] * 2.0D - points[i];
         result[i + 1] = nearest[1] * 2.0D - points[i + 1];
      }

      return result;
   }
}
