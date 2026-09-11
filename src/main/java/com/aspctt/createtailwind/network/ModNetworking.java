package com.aspctt.createtailwind.network;

import com.aspctt.createtailwind.client.JetpackFlight;
import com.aspctt.createtailwind.jetpack.JetpackHandler;

import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public final class ModNetworking {
    public static void register(RegisterPayloadHandlersEvent event) {
        event.registrar("1")
                .playToClient(JetpackFlightPayload.TYPE, JetpackFlightPayload.STREAM_CODEC, ModNetworking::handleJetpackFlight)
                .playToServer(JetpackBoostPayload.TYPE, JetpackBoostPayload.STREAM_CODEC, ModNetworking::handleJetpackBoost);
    }

    // Runs on the client's main thread. The client class is only resolved when this runs, which a dedicated
    // server never does, so registering the handler on both sides is safe.
    private static void handleJetpackFlight(JetpackFlightPayload payload, IPayloadContext context) {
        JetpackFlight.set(payload.entityId(), payload.flying());
    }

    // Runs on the server's main thread.
    private static void handleJetpackBoost(JetpackBoostPayload payload, IPayloadContext context) {
        JetpackHandler.setBoosting(context.player(), payload.boosting());
    }

    private ModNetworking() {
    }
}
