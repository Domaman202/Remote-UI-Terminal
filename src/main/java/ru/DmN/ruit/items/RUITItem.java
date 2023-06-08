package ru.DmN.ruit.items;

import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.nbt.NbtList;
import net.minecraft.nbt.NbtLong;
import net.minecraft.nbt.NbtString;
import net.minecraft.screen.NamedScreenHandlerFactory;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import ru.DmN.ruit.Main;
import ru.DmN.ruit.screen.RUITScreenHandler;

public class RUITItem extends Item implements NamedScreenHandlerFactory {
    public RUITItem() {
        super(new FabricItemSettings().maxCount(1));
    }

    @Override
    public ActionResult useOnBlock(ItemUsageContext context) {
        var player = context.getPlayer();
        if (player == null || context.getWorld().isClient())
            return ActionResult.PASS;
        if (player.isSneaking()) {
            var nbt = context.getStack().getOrCreateNbt();
            var positions = (NbtList) nbt.get("positions");
            if (positions.size() < nbt.getInt("size")) {
                var worlds = (NbtList) nbt.get("worlds");
                worlds.add(NbtString.of(context.getWorld().getRegistryKey().getValue().toString()));
                nbt.put("worlds", worlds);
                positions.add(NbtLong.of(context.getBlockPos().asLong()));
                nbt.put("positions", positions);
                player.sendMessage(Text.translatable("text.ruit.save", positions.size() + 1));
                return ActionResult.SUCCESS;
            } else {
                player.sendMessage(Text.translatable("text.ruit.fail"));
                return ActionResult.FAIL;
            }
        } else {
            this.use(player);
            return ActionResult.PASS;
        }
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        if (!world.isClient())
            this.use(user);
        return TypedActionResult.pass(user.getStackInHand(hand));
    }

    private void use(PlayerEntity user) {
        user.openHandledScreen(this);
    }

    @Override
    public Text getDisplayName() {
        return Text.translatable("item.ruit.ruit");
    }

    @Nullable
    @Override
    public ScreenHandler createMenu(int syncId, PlayerInventory playerInventory, PlayerEntity player) {
        return new RUITScreenHandler(syncId, playerInventory, player.getMainHandStack());
    }

    public static ItemStack create(int size) {
        var stack = new ItemStack(Main.RUIT_ITEM);
        var nbt = stack.getOrCreateNbt();
        nbt.putInt("size", size);
        nbt.put("positions", new NbtList());
        nbt.put("worlds", new NbtList());
        return stack;
    }
}
