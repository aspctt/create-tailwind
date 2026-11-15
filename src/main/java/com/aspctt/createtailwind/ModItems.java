package com.aspctt.createtailwind;

import com.aspctt.createtailwind.jetpack.JetpackItem;

import net.minecraft.world.item.Item;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

public final class ModItems {
    private static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(CreateTailwind.MOD_ID);

    public static final DeferredItem<JetpackItem> JETPACK = ITEMS.registerItem("netherite_jetpack",
            properties -> new JetpackItem(ModBlocks.JETPACK.get(), properties), new Item.Properties().stacksTo(1));

    // Loads jetpacks saved under the name used up to 1.0.x, in any inventory, container or Curios or Accessories
    // slot, as this item. See ModBlocks.
    static {
        ITEMS.addAlias(CreateTailwind.id("jetpack"), JETPACK.getId());
    }

    public static void register(IEventBus modEventBus) {
        ITEMS.register(modEventBus);
    }

    private ModItems() {
    }
}
