package ru.DmN.ruit.screen;

import net.minecraft.block.entity.FurnaceBlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
import net.minecraft.nbt.NbtLong;
import net.minecraft.registry.Registries;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.screen.GenericContainerScreenHandler;
import net.minecraft.screen.NamedScreenHandlerFactory;
import net.minecraft.screen.ScreenHandlerFactory;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

public class RUITScreenHandler extends GenericContainerScreenHandler {
    protected final NbtCompound nbt;

    public RUITScreenHandler(int syncId, PlayerInventory playerInventory) {
        super(ScreenHandlerType.GENERIC_9X1, syncId, playerInventory, 1);
        this.nbt = null;
    }

    public RUITScreenHandler(int syncId, PlayerInventory playerInventory, ItemStack stack) {
        super(ScreenHandlerType.GENERIC_9X1, syncId, playerInventory, new InventoryImpl(stack, playerInventory.player), 1);
        this.nbt = stack.getOrCreateNbt();
    }

    @Override
    public void onSlotClick(int slotIndex, int button, SlotActionType actionType, PlayerEntity player) {
        if (player instanceof ServerPlayerEntity) {
            var positions = (NbtList) nbt.get("positions");
            if (positions.size() > slotIndex && slotIndex > -1) {
                if (button == 0) {
                    var pos = getPos(positions, slotIndex);
                    var world = getWorld((NbtList) nbt.get("worlds"), slotIndex, player);
                    var entity = world.getBlockEntity(pos);
                    if (entity instanceof NamedScreenHandlerFactory factory) {
                        player.openHandledScreen(factory);
                    } else {
                        var state = world.getBlockState(pos);
                        state.getBlock().onUse(state, world, pos, player, player.getActiveHand(), new BlockHitResult(player.getPos(), Direction.UP, pos, true));
                    }
                } else if (button == 1) {
                    positions.remove(slotIndex);
                    ((NbtList) nbt.get("worlds")).remove(slotIndex);
                }
            }
        }
    }

    private static BlockPos getPos(NbtList nbt, int index) {
        return BlockPos.fromLong(((NbtLong) nbt.get(index)).longValue());
    }

    private static World getWorld(NbtList nbt, int index, PlayerEntity player) {
        return ((ServerWorld) player.getWorld()).getServer().getWorld(RegistryKey.of(RegistryKeys.WORLD, new Identifier(nbt.getString(index))));
    }

    @Override
    public boolean canUse(PlayerEntity player) {
        return true;

    }

    @Override
    public void onClosed(PlayerEntity player) {
        super.onClosed(player);
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
            return 9 * 1;
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
                var list = (NbtList) nbt.get("positions");
                if (list.size() <= slot) {
                    item = Items.GRAY_STAINED_GLASS_PANE;
                    name = "Пустая ячейка";
                } else {
                    var pos = getPos(list, slot);
                    var world = getWorld((NbtList) nbt.get("worlds"), slot, player);
                    item = world.getBlockState(pos).getBlock().asItem();
                    name = "[" + world.getRegistryKey().getValue().getPath() + "][" + pos.getX() + ", " + pos.getY() + ", " + pos.getZ() + "]";
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
