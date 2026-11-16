package com.aspctt.createtailwind.client;

import java.util.function.Consumer;

import com.aspctt.createtailwind.jetpack.JetpackBlockEntity;
import com.simibubi.create.content.equipment.armor.BacktankBlockEntity;
import com.simibubi.create.content.kinetics.base.SingleAxisRotatingVisual;

import dev.engine_room.flywheel.api.instance.Instance;
import dev.engine_room.flywheel.api.visualization.VisualizationContext;
import dev.engine_room.flywheel.lib.instance.InstanceTypes;
import dev.engine_room.flywheel.lib.instance.TransformedInstance;
import dev.engine_room.flywheel.lib.model.Models;

// Draws a placed jetpack's moving parts through Flywheel when it is running, as Create draws its backtank: the shaft
// input turned on the GPU by Create's rotating instance, and the gauges' fill as an instance that is only moved when
// the air changes. Without Flywheel, JetpackRenderer draws the same parts every frame.
public class JetpackVisual extends SingleAxisRotatingVisual<BacktankBlockEntity> {
    private final TransformedInstance fill;

    public JetpackVisual(VisualizationContext context, BacktankBlockEntity blockEntity, float partialTick) {
        super(context, blockEntity, partialTick, Models.partial(ModPartialModels.JETPACK_SHAFT_INPUT));
        fill = instancerProvider().instancer(InstanceTypes.TRANSFORMED,
                Models.partial(ModPartialModels.JETPACK_GAUGE_FILL)).createInstance();
        placeFill();
    }

    // Create queues this each time the block entity is sent an update, which JetpackBlockEntity sends whenever the
    // air moves the gauges a step
    @Override
    public void update(float partialTick) {
        super.update(partialTick);
        placeFill();
    }

    @Override
    public void updateLight(float partialTick) {
        super.updateLight(partialTick);
        relight(fill);
    }

    @Override
    protected void _delete() {
        super._delete();
        fill.delete();
    }

    @Override
    public void collectCrumblingInstances(Consumer<Instance> consumer) {
        super.collectCrumblingInstances(consumer);
        consumer.accept(fill);
    }

    // An empty tank hides the fill, leaving the gauges' bare glass. Every jetpack's block entity is this mod's own
    // kind, which knows how full its tank is.
    private void placeFill() {
        float fullness = blockEntity instanceof JetpackBlockEntity jetpack ? jetpack.getFullness() : 0.0F;
        if (fullness <= 0.0F) {
            fill.setZeroTransform().setChanged();
            return;
        }

        fill.setIdentityTransform().translate(getVisualPosition());
        JetpackGauge.place(fill, JetpackGauge.yRotation(blockState), fullness).setChanged();
    }
}
