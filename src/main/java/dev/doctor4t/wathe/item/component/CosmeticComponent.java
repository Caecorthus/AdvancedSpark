package dev.doctor4t.wathe.item.component;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * English: Spark-wathe cosmetic item data component payload.
 * Chinese: Spark-wathe 外观物品数据组件载荷。
 */
public record CosmeticComponent(String cosmeticId, String displayName, String rarity, String textureUrl, String resources) {
    private static final Gson GSON = new Gson();

    public static final Codec<CosmeticComponent> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.STRING.fieldOf("cosmeticId").forGetter(CosmeticComponent::cosmeticId),
            Codec.STRING.fieldOf("displayName").forGetter(CosmeticComponent::displayName),
            Codec.STRING.fieldOf("rarity").forGetter(CosmeticComponent::rarity),
            Codec.STRING.fieldOf("textureUrl").forGetter(CosmeticComponent::textureUrl),
            Codec.STRING.optionalFieldOf("resources", "").forGetter(CosmeticComponent::resources)
    ).apply(instance, CosmeticComponent::new));

    public static final PacketCodec<PacketByteBuf, CosmeticComponent> PACKET_CODEC = PacketCodec.tuple(
            PacketCodecs.STRING, CosmeticComponent::cosmeticId,
            PacketCodecs.STRING, CosmeticComponent::displayName,
            PacketCodecs.STRING, CosmeticComponent::rarity,
            PacketCodecs.STRING, CosmeticComponent::textureUrl,
            PacketCodecs.STRING, CosmeticComponent::resources,
            CosmeticComponent::new
    );

    public CosmeticComponent {
        resources = resources == null ? "" : resources;
    }

    public String getTexture(String key) {
        JsonObject textures = this.textures();
        if (textures != null && textures.has(key)) {
            String url = textures.get(key).getAsString();
            if (!url.isEmpty()) {
                return url;
            }
        }
        return this.textureUrl;
    }

    public @Nullable JsonObject getExtraData() {
        if (this.resources.isEmpty()) {
            return null;
        }
        try {
            JsonObject root = GSON.fromJson(this.resources, JsonObject.class);
            return root != null && root.has("data") ? root.getAsJsonObject("data") : null;
        } catch (RuntimeException exception) {
            return null;
        }
    }

    public List<String> getResourceTextureUrls() {
        JsonObject textures = this.textures();
        if (textures == null) {
            return List.of();
        }
        List<String> urls = new ArrayList<>();
        for (Map.Entry<String, JsonElement> entry : textures.entrySet()) {
            String url = entry.getValue().getAsString();
            if (!url.isEmpty()) {
                urls.add(url);
            }
        }
        return List.copyOf(urls);
    }

    public CosmeticComponent withTextureUrl(String newTextureUrl) {
        return new CosmeticComponent(this.cosmeticId, this.displayName, this.rarity, newTextureUrl, this.resources);
    }

    private @Nullable JsonObject textures() {
        if (this.resources.isEmpty()) {
            return null;
        }
        try {
            JsonObject root = GSON.fromJson(this.resources, JsonObject.class);
            return root != null && root.has("textures") ? root.getAsJsonObject("textures") : null;
        } catch (RuntimeException exception) {
            return null;
        }
    }
}
