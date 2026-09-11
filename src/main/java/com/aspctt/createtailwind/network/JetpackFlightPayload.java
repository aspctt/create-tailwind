package com.aspctt.createtailwind.network;

import com.aspctt.createtailwind.CreateTailwind;

import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

// Tells a client that a player started or stopped flying on a jetpack. Abilities are only synced to the player
// they belong to, so without this no client could tell when another player's engine should be running.
public record JetpackFlightPayload(int entityId, boolean flying) implements CustomPacketPayload {
    public static final Type<JetpackFlightPayload> TYPE =
            new Type<>(ResourceLocation.fromNamespaceAndPath(CreateTailwind.MOD_ID, "jetpack_flight"));

    public static final StreamCodec<ByteBuf, JetpackFlightPayload> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.VAR_INT, JetpackFlightPayload::entityId,
            ByteBufCodecs.BOOL, JetpackFlightPayload::flying,
            JetpackFlightPayload::new);

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
