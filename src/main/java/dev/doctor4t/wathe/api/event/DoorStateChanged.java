package dev.doctor4t.wathe.api.event;

import dev.doctor4t.wathe.block_entity.DoorBlockEntity;
import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

/**
 * English: Spark-wathe door state change events.
 * Chinese: Spark-wathe 门状态变化事件。
 */
public interface DoorStateChanged {
    Event<DoorStateChanged> BLAST = EventFactory.createArrayBacked(
            DoorStateChanged.class,
            callbacks -> (world, pos, entity) -> {
                for (DoorStateChanged callback : callbacks) {
                    callback.onStateChanged(world, pos, entity);
                }
            }
    );
    Event<DoorStateChanged> JAM = EventFactory.createArrayBacked(
            DoorStateChanged.class,
            callbacks -> (world, pos, entity) -> {
                for (DoorStateChanged callback : callbacks) {
                    callback.onStateChanged(world, pos, entity);
                }
            }
    );

    void onStateChanged(World world, BlockPos pos, DoorBlockEntity entity);
}
