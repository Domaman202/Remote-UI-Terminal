package ru.DmN.ruit.mixin;

import com.google.common.collect.ImmutableMap;
import com.google.gson.JsonElement;
import net.minecraft.inventory.CraftingInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.recipe.*;
import net.minecraft.recipe.book.CraftingRecipeCategory;
import net.minecraft.registry.DynamicRegistryManager;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.profiler.Profiler;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;
import ru.DmN.ruit.Main;
import ru.DmN.ruit.items.RUITItem;

import java.util.Map;

import static ru.DmN.ruit.Main.ID;

@Mixin(RecipeManager.class)
public class RecipeManagerMixin {
    @Inject(method = "apply(Ljava/util/Map;Lnet/minecraft/resource/ResourceManager;Lnet/minecraft/util/profiler/Profiler;)V", at = @At(value = "INVOKE", target = "Ljava/util/Set;iterator()Ljava/util/Iterator;", shift = At.Shift.AFTER), locals = LocalCapture.CAPTURE_FAILHARD)
    public void apply(Map<Identifier, JsonElement> map, ResourceManager resourceManager, Profiler profiler, CallbackInfo ci, Map<RecipeType<?>, ImmutableMap.Builder<Identifier, Recipe<?>>> map2, ImmutableMap.Builder<Identifier, Recipe<?>> builder) {
        var recipe0 = new ShapedRecipe(
                ID,
                "",
                CraftingRecipeCategory.EQUIPMENT,
                3,
                3,
                DefaultedList.copyOf(Ingredient.EMPTY,
                        Ingredient.ofItems(Items.OBSIDIAN), Ingredient.ofItems(Items.TINTED_GLASS), Ingredient.ofItems(Items.OBSIDIAN),
                        Ingredient.ofItems(Items.OBSIDIAN), Ingredient.ofItems(Items.ENDER_EYE), Ingredient.ofItems(Items.OBSIDIAN),
                        Ingredient.ofItems(Items.OBSIDIAN), Ingredient.ofItems(Items.REDSTONE), Ingredient.ofItems(Items.OBSIDIAN)
                ),
                RUITItem.create(1)
        );
        var id1 = new Identifier("ruit","upgrade");
        var recipe1 = new ShapelessRecipe(
                id1,
                "",
                CraftingRecipeCategory.EQUIPMENT,
                RUITItem.create(1),
                DefaultedList.copyOf(Ingredient.EMPTY,
                        Ingredient.ofItems(Items.ENDER_EYE),
                        Ingredient.ofItems(Items.REDSTONE),
                        Ingredient.ofItems(Main.RUIT_ITEM)
                )
        ) {
            @Override
            public boolean matches(CraftingInventory craftingInventory, World world) {
                ItemStack terminal = null;
                for (int i = 0; i < craftingInventory.size(); i++) {
                    terminal = craftingInventory.getStack(i);
                    if (terminal.getItem() == Main.RUIT_ITEM) break;
                }
                if (terminal == null)
                    return false;
                if (terminal.getOrCreateNbt().getInt("size") == 9)
                    return false;
                return super.matches(craftingInventory, world);
            }

            @Override
            public ItemStack craft(CraftingInventory craftingInventory, DynamicRegistryManager dynamicRegistryManager) {
                ItemStack terminal = null;
                for (int i = 0; i < craftingInventory.size(); i++) {
                    terminal = craftingInventory.getStack(i);
                    if (terminal.getItem() == Main.RUIT_ITEM) break;
                }
                var stack = terminal.copy();
                var nbt = stack.getNbt();
                nbt.putInt("size", nbt.getInt("size") + 1);
                return stack;
            }
        };
        var recipes = map2.computeIfAbsent(RecipeType.CRAFTING, (type) -> ImmutableMap.builder());
        recipes.put(ID, recipe0);
        recipes.put(id1, recipe1);
        builder.put(ID, recipe0);
        builder.put(id1, recipe1);
    }
}
