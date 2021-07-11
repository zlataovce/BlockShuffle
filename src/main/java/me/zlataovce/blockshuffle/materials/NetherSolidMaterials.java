package me.zlataovce.blockshuffle.materials;

import lombok.Getter;
import org.bukkit.Material;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public enum NetherSolidMaterials {
    ANCIENT_DEBRIS("ANCIENT_DEBRIS"),
    BASALT("BASALT"),
    BLACKSTONE("BLACKSTONE"),
    GLOWSTONE("GLOWSTONE"),
    MAGMA_BLOCK("MAGMA_BLOCK"),
    NETHER_GOLD_ORE("NETHER_GOLD_ORE"),
    NETHER_QUARTZ_ORE("NETHER_QUARTZ_ORE"),
    NETHERRACK("NETHERRACK"),
    NETHER_BRICK("NETHER_BRICK"),
    CRIMSON_NYLIUM("CRIMSON_NYLIUM"),
    WARPED_NYLIUM("WARPED_NYLIUM"),
    GILDED_BLACKSTONE("GILDED_BLACKSTONE"),
    NETHER_WART_BLOCK("NETHER_WART_BLOCK"),
    SOUL_SOIL("SOUL_SOIL"),
    SOUL_SAND("SOUL_SAND"),
    SHROOMLIGHT("SHROOMLIGHT");

    @Getter public Optional<Material> material;

    NetherSolidMaterials(String... materials) {
        for (Material mat : Arrays.stream(materials).map(Material::matchMaterial).collect(Collectors.toList())) {
            if (mat != null) {
                this.material = Optional.of(mat);
            }
        }
    }
}
