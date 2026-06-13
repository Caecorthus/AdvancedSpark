package dev.doctor4t.wathe.item.component;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;

/**
 * English: Spark-wathe walkie-talkie channel data component payload.
 * Chinese: Spark-wathe 对讲机频道数据组件载荷。
 */
public record WalkieTalkieComponent(int channel) {
    public static final Codec<WalkieTalkieComponent> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.INT.fieldOf("channel").forGetter(WalkieTalkieComponent::channel)
    ).apply(instance, WalkieTalkieComponent::new));

    public static final PacketCodec<RegistryByteBuf, WalkieTalkieComponent> PACKET_CODEC =
            PacketCodec.tuple(PacketCodecs.INTEGER, WalkieTalkieComponent::channel, WalkieTalkieComponent::new);

    public static final WalkieTalkieComponent DEFAULT = new WalkieTalkieComponent(0);
}
