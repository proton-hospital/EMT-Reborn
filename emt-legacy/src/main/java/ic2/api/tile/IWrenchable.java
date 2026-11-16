package ic2.api.tile;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;

/** IC2 1.7.10 wrench API, kept so EMT tiles compile unchanged. The 1.20.1 IC2 wrench does not call it. */
public interface IWrenchable {
    boolean wrenchCanSetFacing(EntityPlayer entityPlayer, int side);

    short getFacing();

    void setFacing(short facing);

    boolean wrenchCanRemove(EntityPlayer entityPlayer);

    float getWrenchDropRate();

    ItemStack getWrenchDrop(EntityPlayer entityPlayer);
}
