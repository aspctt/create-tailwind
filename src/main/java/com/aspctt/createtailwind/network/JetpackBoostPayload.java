package com.aspctt.createtailwind.network;

import com.aspctt.createtailwind.CreateTailwind;

import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

// Tells the server that the player's client started or stopped boosting an elytra glide with a jetpack. A player's
// movement is simulated by their own client, so only it knows when the forward key is held and the push applied.
public record JetpackBoostPayload(boolean boosting) implements CustomPacketPayload {
    public static final Type<JetpackBoostPayload> TYPE =
            new Type<>(ResourceLocation.fromNamespaceAndPath(CreateTailwind.MOD_ID, "jetpack_boost"));

    public static final StreamCodec<ByteBuf, JetpackBoostPayload> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.BOOL, JetpackBoostPayload::boosting,
            JetpackBoostPayload::new);

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
