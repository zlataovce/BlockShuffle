package me.zlataovce.blockshuffle.materials;

import lombok.Getter;
import org.bukkit.Material;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public enum EndSolidMaterials {
    BEDROCK("BEDROCK"),
    CHORUS_FLOWER("CHORUS_FLOWER"),
    CHORUS_PLANT("CHORUS_PLANT"),
    END_STONE("END_STONE"),
    DRAGON_EGG("DRAGON_EGG"),
    END_STONE_BRICKS("END_STONE_BRICKS"),
    PURPUR_BLOCK("PURPUR_BLOCK"),
    PURPUR_PILLAR("PURPUR_PILLAR");

    @Getter public Optional<Material> material;

    EndSolidMaterials(String... materials) {
        for (Material mat : Arrays.stream(materials).map(Material::matchMaterial).collect(Collectors.toList())) {
            if (mat != null) {
                this.material = Optional.of(mat);
            }
        }
    }
}
