package com.aspctt.createtailwind.client.compat;

import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

import com.aspctt.createtailwind.CreateTailwind;
import com.aspctt.createtailwind.client.JetpackBoost;

import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;

// Do a Barrel Roll has thrusting of its own, which a player can switch on in its config, and it pushes the same
// glide the jetpack does. While the jetpack is boosting, Do a Barrel Roll's thrust input is zeroed through its
// ThrustEvents API, so the two never add together and the jetpack's settings and air are what count. Its own thrust,
// and its smoke, carry on as before whenever the jetpack is not boosting.
//
// Hooked by reflection, since Do a Barrel Roll publishes no build this mod could compile against. Only loaded when
// Do a Barrel Roll is.
public final class BarrelRollCompat {
    private static final String THRUST_EVENTS = "nl.enjarai.doabarrelroll.api.event.ThrustEvents";
    private static final String EVENT = "nl.enjarai.doabarrelroll.api.event.Event";

    public static void register() {
        try {
            Class<?> listenerType = Class.forName(THRUST_EVENTS + "$ModifyThrustInputEvent");
            Object event = Class.forName(THRUST_EVENTS).getField("MODIFY_THRUST_INPUT").get(null);
            Object listener = Proxy.newProxyInstance(listenerType.getClassLoader(), new Class<?>[] {listenerType},
                    (proxy, method, args) -> invoke(proxy, method, args));
            Class.forName(EVENT).getMethod("register", Object.class).invoke(event, listener);
        } catch (ReflectiveOperationException | LinkageError e) {
            CreateTailwind.LOGGER.warn("Could not hook into Do a Barrel Roll's thrusting, so its thrust may add to the jetpack's boost", e);
        }
    }

    private static Object invoke(Object proxy, Method method, Object[] args) {
        return switch (method.getName()) {
            case "modify" -> {
                LocalPlayer player = Minecraft.getInstance().player;
                yield player != null && JetpackBoost.isBoosting(player) ? 0.0 : args[0];
            }
            case "equals" -> proxy == args[0];
            case "hashCode" -> System.identityHashCode(proxy);
            case "toString" -> "Create: Tailwind jetpack boost";
            default -> throw new UnsupportedOperationException(method.getName());
        };
    }

    private BarrelRollCompat() {
    }
}
