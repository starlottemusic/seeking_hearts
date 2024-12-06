package com.starlotte.seeking_hearts.neoforge;

import com.starlotte.seeking_hearts.seeking_hearts;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Items;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.IModBusEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;

// the snake case is an easter egg for whoevers looking at the source. the easter egg is that its shit

@Mod(seeking_hearts.MOD_ID)
public final class seeking_heartsNeoForge {
    public seeking_heartsNeoForge(IEventBus modBus) {
        // Run our common setup.
        seeking_hearts.init();
        modBus.addListener(seeking_heartsNeoForge::creativeTabAddition);
    }

    private static void creativeTabAddition(BuildCreativeModeTabContentsEvent event) {
        if (event.getTabKey() == CreativeModeTabs.REDSTONE_BLOCKS) {
            event.insertBefore(Items.SCULK_SENSOR.getDefaultInstance(), Items.CREAKING_HEART.getDefaultInstance(), CreativeModeTab.TabVisibility.PARENT_AND_SEARCH_TABS);
        }
    }
}