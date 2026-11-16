package com.aspctt.createtailwind.client;

import com.aspctt.createtailwind.TailwindConfig;
import com.aspctt.createtailwind.jetpack.Jetpacks;
import com.simibubi.create.content.equipment.armor.BacktankUtil;

import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.util.FastColor;
import net.minecraft.util.StringUtil;
import net.minecraft.world.item.ItemStack;

// How long the worn jetpack can keep going, shown beside the hotbar while it flies or boosts: the jetpack and the
// time its air lasts at the configured cost, where Create shows a backtank's air while diving. As in Create, the time
// flashes red as the tank runs low, here from the point where the low pressure warning comes up. Adapted from
// Create's RemainingAirOverlay, used under the MIT License. The licence text is in NOTICE.
public final class JetpackAirOverlay {
    // Create's spot for its own, across from the middle of the screen and up from the bottom
    private static final int OFFSET_X = 90;
    private static final int OFFSET_Y = 53;
    // Create's diving overlay takes that spot, so while it may be up this one sits a row above it
    private static final int ROW = 18;
    // Create marks a player whose diving helmet is drawing on a tank with this, on the client too, for its overlay
    private static final String CREATE_DIVING = "VisualBacktankAir";

    private static final int TEXT_COLOUR = 0xFFFFFFFF;
    private static final int LOW_COLOUR = 0xFFFF0000;
    // Create's floor on the flashing time's share of white, so it never turns fully red
    private static final float LEAST_WHITE = 0.25F;

    public static void render(GuiGraphics graphics, DeltaTracker deltaTracker) {
        Minecraft minecraft = Minecraft.getInstance();
        LocalPlayer player = minecraft.player;
        ClientLevel level = minecraft.level;
        if (minecraft.options.hideGui || player == null || level == null || !JetpackFlight.isFlying(player)) {
            return;
        }
        // A jetpack that costs no air never runs out, so it has no time to show
        double airPerTick = TailwindConfig.AIR_CONSUMPTION_PER_TICK.get();
        ItemStack jetpack = Jetpacks.findWorn(player);
        if (airPerTick <= 0.0 || jetpack.isEmpty()) {
            return;
        }

        // Time left, and the text's colour: white, flashing red every other second once the tank is low, redder the
        // closer it is to empty
        int air = BacktankUtil.getAir(jetpack);
        int ticksLeft = (int) Math.ceil(air / airPerTick);
        float tickRate = level.tickRateManager().tickrate();
        float lowAir = BacktankUtil.maxAir(jetpack) / 10.0F;
        int colour = TEXT_COLOUR;
        if (air < lowAir && (int) (ticksLeft / tickRate) % 2 == 0) {
            colour = FastColor.ARGB32.lerp(Math.max(air / lowAir, LEAST_WHITE), LOW_COLOUR, TEXT_COLOUR);
        }

        int x = graphics.guiWidth() / 2 + OFFSET_X;
        int y = graphics.guiHeight() - OFFSET_Y - (player.getPersistentData().contains(CREATE_DIVING) ? ROW : 0);
        graphics.renderItem(jetpack, x, y);
        graphics.drawString(minecraft.font, StringUtil.formatTickDuration(ticksLeft, tickRate), x + 16, y + 5, colour);
    }

    private JetpackAirOverlay() {
    }
}
