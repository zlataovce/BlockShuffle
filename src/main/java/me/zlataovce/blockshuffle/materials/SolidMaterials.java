package me.zlataovce.blockshuffle.materials;

import org.bukkit.Material;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class SolidMaterials {
    public static List<Material> listOfNetherSolidMaterials() {
        return Arrays.stream(NetherSolidMaterials.values()).map(NetherSolidMaterials::getMaterial).filter(Optional::isPresent).map(Optional::get).collect(Collectors.toList());
    }

    public static List<Material> listOfEndSolidMaterials() {
        return Arrays.stream(EndSolidMaterials.values()).map(EndSolidMaterials::getMaterial).filter(Optional::isPresent).map(Optional::get).collect(Collectors.toList());
    }
}
