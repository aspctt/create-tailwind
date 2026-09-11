package com.aspctt.createtailwind.jetpack;

import com.simibubi.create.AllSoundEvents;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.protocol.game.ClientboundSetSubtitleTextPacket;
import net.minecraft.network.protocol.game.ClientboundSetTitleTextPacket;
import net.minecraft.network.protocol.game.ClientboundSetTitlesAnimationPacket;
import net.minecraft.server.level.ServerPlayer;

// The warning Create shows as a backtank runs low, worded for a jetpack. Adapted from Create's BacktankUtil, used
// under the MIT License. The licence text is in NOTICE.
public final class JetpackAirWarning {
    // Takes the air before and after a draw, and the level to warn at. Create warns once on the way past a tenth
    // of a full tank, and once on running out, which it marks with a level of 1.
    public static void send(ServerPlayer player, float air, float newAir, float threshold) {
        if (newAir > threshold || air <= threshold) {
            return;
        }

        boolean depleted = threshold == 1;
        MutableComponent message = Component.translatable(
                depleted ? "createtailwind.jetpack.depleted" : "createtailwind.jetpack.low");

        AllSoundEvents.DENY.play(player.level(), null, player.blockPosition(), 1, 1.25f);
        AllSoundEvents.STEAM.play(player.level(), null, player.blockPosition(), .5f, .5f);

        player.connection.send(new ClientboundSetTitlesAnimationPacket(10, 40, 10));
        player.connection.send(new ClientboundSetSubtitleTextPacket(Component.literal("⚠ ")
                .withStyle(depleted ? ChatFormatting.RED : ChatFormatting.GOLD)
                .append(message.withStyle(ChatFormatting.GRAY))));
        player.connection.send(new ClientboundSetTitleTextPacket(CommonComponents.EMPTY));
    }

    private JetpackAirWarning() {
    }
}
