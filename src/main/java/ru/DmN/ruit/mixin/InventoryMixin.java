package ru.DmN.ruit.mixin;

import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import ru.DmN.ruit.Main;

@Mixin(Inventory.class)
public interface InventoryMixin {
    /**
     * @author DomamaN202
     * @reason Remove Entity.squaredDistanceTo check
     */
    @Overwrite
    static boolean canPlayerUse(BlockEntity blockEntity, PlayerEntity player, int range) {
        World world = blockEntity.getWorld();
        BlockPos blockPos = blockEntity.getPos();
        if (world == null) {
            return false;
        } else if (world.getBlockEntity(blockPos) != blockEntity) {
            return false;
        } else if (player.getMainHandStack().getItem() == Main.RUIT_ITEM) {
            return true;
        } else {
            return player.squaredDistanceTo((double)blockPos.getX() + 0.5, (double)blockPos.getY() + 0.5, (double)blockPos.getZ() + 0.5) <= (double)(range * range);
        }
    }
}