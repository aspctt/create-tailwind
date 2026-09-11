package com.aspctt.createtailwind.client;

import com.aspctt.createtailwind.TailwindConfig;
import com.aspctt.createtailwind.jetpack.Jetpacks;
import com.aspctt.createtailwind.network.JetpackBoostPayload;

import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.client.event.ClientPlayerNetworkEvent;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.network.PacketDistributor;

// The thrust is adapted from Do a Barrel Roll by enjarai (github.com/enjarai/do-a-barrel-roll), by way of its
// NeoForge port Do Another Barrel Roll, and is used under the GNU General Public License v3.0.
//
// A jetpack worn with an elytra boosts the glide while the forward key is held. A player's movement is simulated by
// their own client, so the push is added here, to the local player only (LivingEntityMixin), and the server is told
// when boosting starts and stops so it can draw the air and show everyone the exhaust.
public final class JetpackBoost {
    // What the server was last told.
    private static boolean announced;

    public static boolean isBoosting(Player player) {
        return player.isFallFlying() && Minecraft.getInstance().options.keyUp.isDown() && Jetpacks.isBooster(player);
    }

    // The push for one tick of gliding, along where the player is looking. It is the full acceleration from a
    // standstill and shrinks to nothing at the top speed, so the boost settles there rather than running away.
    public static Vec3 thrust(Player player) {
        double maxSpeed = TailwindConfig.BOOST_MAX_SPEED.get();
        double speedIncrease = Math.max(maxSpeed - player.getDeltaMovement().length(), 0) / maxSpeed;
        return player.getLookAngle().scale(TailwindConfig.BOOST_ACCELERATION.get() * speedIncrease);
    }

    public static void onClientTick(ClientTickEvent.Post event) {
        Minecraft minecraft = Minecraft.getInstance();
        LocalPlayer player = minecraft.player;
        boolean boosting = player != null && isBoosting(player);
        if (boosting != announced && minecraft.getConnection() != null) {
            PacketDistributor.sendToServer(new JetpackBoostPayload(boosting));
            announced = boosting;
        }
    }

    public static void onLoggingOut(ClientPlayerNetworkEvent.LoggingOut event) {
        announced = false;
    }

    private JetpackBoost() {
    }
}
