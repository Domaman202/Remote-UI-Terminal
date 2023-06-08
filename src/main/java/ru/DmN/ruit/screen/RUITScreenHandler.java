package ru.DmN.ruit.screen;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
import net.minecraft.nbt.NbtLong;
import net.minecraft.screen.GenericContainerScreenHandler;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;

public class RUITScreenHandler extends GenericContainerScreenHandler {
    protected final NbtList nbt;

    public RUITScreenHandler(int syncId, PlayerInventory playerInventory) {
        super(ScreenHandlerType.GENERIC_9X6, syncId, playerInventory, 6);
        this.nbt = null;
    }

    public RUITScreenHandler(int syncId, PlayerInventory playerInventory, ItemStack stack) {
        super(ScreenHandlerType.GENERIC_9X6, syncId, playerInventory, new InventoryImpl(stack, playerInventory.player), 6);
        this.nbt = (NbtList) stack.getOrCreateNbt().get("terminals");;
    }

    @Override
    public void onSlotClick(int slotIndex, int button, SlotActionType actionType, PlayerEntity player) {
        if (player instanceof ServerPlayerEntity) {
            if (nbt.size() > slotIndex && slotIndex > -1) {
                if (button == 0) {
                    var pos = getPos(nbt, slotIndex);
                    var state = player.world.getBlockState(pos);
                    state.getBlock().onUse(state, player.world, pos, player, player.getActiveHand(), new BlockHitResult(player.getPos(), Direction.UP, pos, true));
                } else if (button == 1) {
                    nbt.remove(slotIndex);
                }
            }
        }
    }

    private static BlockPos getPos(NbtList nbt, int index) {
        return BlockPos.fromLong(((NbtLong) nbt.get(index)).longValue());
    }

    @Override
    public boolean canUse(PlayerEntity player) {
        return true;
    }

    public static class InventoryImpl implements Inventory {
        protected final NbtCompound nbt;
        protected final PlayerEntity player;

        public InventoryImpl(ItemStack stack, PlayerEntity player) {
            this.nbt = stack.getNbt();
            this.player = player;
        }


        @Override
        public int size() {
            return 9 * 6;
        }

        @Override
        public boolean isEmpty() {
            return false;
        }

        @Override
        public ItemStack getStack(int slot) {
            Item item;
            String name;
            if (nbt.getInt("size") <= slot) {
                item = Items.BARRIER;
                name = "#";
            } else {
                var list = (NbtList) nbt.get("terminals");
                if (list.size() <= slot) {
                    item = Items.GRAY_STAINED_GLASS_PANE;
                    name = "Пустая ячейка";
                } else {
                    var pos = getPos(list, slot);
                    item = player.world.getBlockState(pos).getBlock().asItem();
                    name = pos.toShortString();
                }
            }
            var stack = new ItemStack(item);
            stack.setCustomName(Text.of(name));
            return stack;
        }

        @Override
        public ItemStack removeStack(int slot, int amount) {
            return null;
        }

        @Override
        public ItemStack removeStack(int slot) {
            return null;
        }

        @Override
        public void setStack(int slot, ItemStack stack) {

        }

        @Override
        public void markDirty() {

        }

        @Override
        public boolean canPlayerUse(PlayerEntity player) {
            return false;
        }

        @Override
        public void clear() {

        }
    }
}
