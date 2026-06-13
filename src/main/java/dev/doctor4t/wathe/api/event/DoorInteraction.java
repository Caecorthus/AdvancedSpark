package dev.doctor4t.wathe.api.event;

import dev.doctor4t.wathe.block.SmallDoorBlock;
import dev.doctor4t.wathe.block.TrainDoorBlock;
import dev.doctor4t.wathe.block_entity.DoorBlockEntity;
import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.LoreComponent;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * English: Spark-wathe door interaction event surface.
 * Chinese: Spark-wathe 门交互事件接口。
 */
public interface DoorInteraction {
    Event<DoorInteraction> EVENT = EventFactory.createArrayBacked(
            DoorInteraction.class,
            callbacks -> context -> {
                for (DoorInteraction callback : callbacks) {
                    DoorInteractionResult result = callback.onInteract(context);
                    if (result.shouldTerminate()) {
                        return result;
                    }
                }
                return DoorInteractionResult.PASS;
            }
    );

    DoorInteractionResult onInteract(DoorInteractionContext context);

    enum DoorInteractionResult {
        PASS,
        ALLOW,
        DENY,
        HANDLED;

        public boolean shouldTerminate() {
            return this != PASS;
        }
    }

    final class DoorInteractionContext {
        private final World world;
        private final BlockPos pos;
        private final BlockPos lowerPos;
        private final BlockState state;
        private final DoorBlockEntity entity;
        private final PlayerEntity player;
        private final ItemStack handItem;
        private final DoorInteractionType interactionType;
        private final DoorType doorType;
        private final String keyName;
        private final boolean open;
        private final boolean jammed;
        private final boolean blasted;

        public DoorInteractionContext(
                @NotNull World world,
                @NotNull BlockPos pos,
                @NotNull BlockPos lowerPos,
                @NotNull BlockState state,
                @NotNull DoorBlockEntity entity,
                @NotNull PlayerEntity player,
                @NotNull ItemStack handItem,
                @NotNull DoorInteractionType interactionType,
                @NotNull DoorType doorType
        ) {
            this.world = world;
            this.pos = pos;
            this.lowerPos = lowerPos;
            this.state = state;
            this.entity = entity;
            this.player = player;
            this.handItem = handItem.copy();
            this.interactionType = interactionType;
            this.doorType = doorType;
            this.keyName = entity.getKeyName();
            this.open = entity.isOpen();
            this.jammed = entity.isJammed();
            this.blasted = entity.isBlasted();
        }

        public @NotNull World getWorld() {
            return this.world;
        }

        public @NotNull BlockPos getPos() {
            return this.pos;
        }

        public @NotNull BlockPos getLowerPos() {
            return this.lowerPos;
        }

        public @NotNull BlockState getState() {
            return this.state;
        }

        public @NotNull DoorBlockEntity getEntity() {
            return this.entity;
        }

        public @NotNull PlayerEntity getPlayer() {
            return this.player;
        }

        public @NotNull ItemStack getHandItem() {
            return this.handItem.copy();
        }

        public @NotNull DoorInteractionType getInteractionType() {
            return this.interactionType;
        }

        public @NotNull DoorType getDoorType() {
            return this.doorType;
        }

        public @NotNull String getKeyName() {
            return this.keyName;
        }

        public boolean isOpen() {
            return this.open;
        }

        public boolean isJammed() {
            return this.jammed;
        }

        public boolean isBlasted() {
            return this.blasted;
        }

        public boolean requiresKey() {
            return !this.keyName.isEmpty();
        }

        public boolean isServerSide() {
            return !this.world.isClient();
        }

        public boolean isCreative() {
            return this.player.isCreative();
        }

        public @Nullable String getHandItemLoreFirstLine() {
            LoreComponent lore = this.handItem.get(DataComponentTypes.LORE);
            if (lore == null || lore.lines().isEmpty()) {
                return null;
            }
            return lore.lines().getFirst().getString();
        }

        public boolean isCorrectKey() {
            String loreLine = getHandItemLoreFirstLine();
            return requiresKey() && loreLine != null && loreLine.equals(this.keyName);
        }

        public static Builder builder() {
            return new Builder();
        }

        public static final class Builder {
            private World world;
            private BlockPos pos;
            private BlockPos lowerPos;
            private BlockState state;
            private DoorBlockEntity entity;
            private PlayerEntity player;
            private ItemStack handItem;
            private DoorInteractionType interactionType;
            private DoorType doorType;

            public Builder world(World world) {
                this.world = world;
                return this;
            }

            public Builder pos(BlockPos pos) {
                this.pos = pos;
                return this;
            }

            public Builder lowerPos(BlockPos lowerPos) {
                this.lowerPos = lowerPos;
                return this;
            }

            public Builder state(BlockState state) {
                this.state = state;
                return this;
            }

            public Builder entity(DoorBlockEntity entity) {
                this.entity = entity;
                return this;
            }

            public Builder player(PlayerEntity player) {
                this.player = player;
                return this;
            }

            public Builder handItem(ItemStack handItem) {
                this.handItem = handItem;
                return this;
            }

            public Builder interactionType(DoorInteractionType interactionType) {
                this.interactionType = interactionType;
                return this;
            }

            public Builder doorType(DoorType doorType) {
                this.doorType = doorType;
                return this;
            }

            public DoorInteractionContext build() {
                return new DoorInteractionContext(
                        this.world,
                        this.pos,
                        this.lowerPos,
                        this.state,
                        this.entity,
                        this.player,
                        this.handItem,
                        this.interactionType,
                        this.doorType
                );
            }
        }
    }

    enum DoorType {
        SMALL_DOOR,
        TRAIN_DOOR;

        public static @Nullable DoorType fromBlock(Block block) {
            if (block instanceof TrainDoorBlock) {
                return TRAIN_DOOR;
            }
            if (block instanceof SmallDoorBlock) {
                return SMALL_DOOR;
            }
            return null;
        }
    }

    enum DoorInteractionType {
        OPEN,
        CLOSE,
        USE_KEY,
        USE_LOCKPICK,
        JAM_DOOR,
        USE_CROWBAR,
        BLASTED,
        INTERACT;

        public boolean usesTool() {
            return this == USE_KEY || this == USE_LOCKPICK || this == USE_CROWBAR || this == JAM_DOOR;
        }

        public boolean isOpening() {
            return this == OPEN || this == USE_KEY || this == USE_LOCKPICK || this == USE_CROWBAR;
        }
    }
}
