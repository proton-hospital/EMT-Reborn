package emt.util;

import ic2.api.item.ElectricItem;
import java.util.ArrayList;
import java.util.Random;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public class EMTRandomHelper {
   public static ItemStack getChargedItem(Item item, int charge) {
      ItemStack stack = new ItemStack(item);
      ElectricItem.manager.charge(stack, (double)charge, charge, true, false);
      return stack;
   }

   public static class Bolt {
      Random rnd = new Random();
      public ArrayList<EMTRandomHelper.Point> points = new ArrayList<>();
      public float angleZ = 0.0F;
      public float angleY = 0.0F;
      float dist = 0.0F;
      float length = 0.0F;
      float height = 0.0F;

      public Bolt(float dist, float length, float height) {
         this.dist = dist;
         this.length = length / 2.0F + (float)this.rnd.nextInt((int)(length / 2.0F));
         this.height = height;
         this.regenerate(this.dist, this.length, this.height);
      }

      public void regenerate() {
         this.regenerate(this.dist, this.length, this.height);
      }

      public void regenerate(float dist, float length, float height) {
         this.points.clear();
         this.angleZ = (float)this.rnd.nextInt(360);
         this.angleY = (float)this.rnd.nextInt(360);

         for (int i = 0; (float)i < length / dist; i++) {
            float x = (float)i * dist;
            float prevY = i == 0 ? 0.0F : this.points.get(i - 1).y;
            float prevZ = i == 0 ? 0.0F : this.points.get(i - 1).z;
            float y = prevY + ((float)this.rnd.nextInt((int)height) - height / 2.0F);
            float z = prevZ + ((float)this.rnd.nextInt((int)height) - height / 2.0F);
            byte color = (byte)((int)(198.0F / (length / dist) * x));
            this.points.add(new EMTRandomHelper.Point(x, y, z, (byte)(255 - color), (byte)(255 - color), (byte)-1));
         }
      }
   }

   public static class Point {
      public float x = 0.0F;
      public float y = 0.0F;
      public float z = 0.0F;
      public byte r = 0;
      public byte g = 0;
      public byte b = 0;

      public Point(float x, float y, float z) {
         this.x = x;
         this.y = y;
         this.z = z;
      }

      public Point(float x, float y, float z, byte r, byte g, byte b) {
         this.x = x;
         this.y = y;
         this.z = z;
         this.r = r;
         this.g = g;
         this.b = b;
      }

      public Point(double x, double y, double z) {
         this.x = (float)x;
         this.y = (float)y;
         this.z = (float)z;
      }
   }
}
