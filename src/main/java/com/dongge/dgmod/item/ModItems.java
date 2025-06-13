package com.dongge.dgmod.item;

import com.dongge.dgmod.dgmod;
import com.dongge.dgmod.item.custom.ChiselItem;
import net.minecraft.world.item.Item;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModItems {
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(dgmod.MOD_ID);

    //normal items
    public static final DeferredItem<Item> BISMUTH = ITEMS.register("bismuth",
            () -> new Item(new Item.Properties()));

    public static final DeferredItem<Item> RAW_BISMUTH = ITEMS.register("raw_bismuth",
            () -> new Item(new Item.Properties()));
    public static final DeferredItem<Item> SHIT = ITEMS.register("shit",
            () -> new Item(new Item.Properties()));

    //advanced items
    public static final DeferredItem<Item> CHISEL = ITEMS.register("chisel",
            () -> new ChiselItem(new Item.Properties().durability(32)));
    public static void register(IEventBus eventBus){
        ITEMS.register(eventBus);
    }
}
