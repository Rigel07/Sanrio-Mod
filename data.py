"""Genera modelos, blockstates, idiomas, loot tables, recetas y datos de worldgen."""
import json
import os

NS = "sanriofriends"
RES = "src/main/resources"
ASSETS = RES + "/assets/" + NS
DATA = RES + "/data/" + NS

CHARS = [
    # clave, id entidad, nombre, comida, amuleto, peluche
    ("KITTY", "hello_kitty", "Hello Kitty", "kitty_apple_pie", "kitty_ribbon", "kitty_plush"),
    ("MELODY", "my_melody", "My Melody", "melody_almond_cake", "melody_hood_charm", "melody_plush"),
    ("KUROMI", "kuromi", "Kuromi", "kuromi_cupcake", "kuromi_skull_charm", "kuromi_plush"),
    ("CINNAMOROLL", "cinnamoroll", "Cinnamoroll", "cinnamoroll_roll", "cinnamoroll_cloud_charm", "cinnamoroll_plush"),
    ("PURIN", "purin", "Purin", "purin_pudding", "purin_beret_charm", "purin_plush"),
]

# id de estructura -> (id entidad, spacing, separation, salt, start_height, [biomas])
LAND_ALL = ["#minecraft:is_forest", "#minecraft:is_taiga", "#minecraft:is_savanna", "#minecraft:is_jungle",
            "#minecraft:is_badlands", "#minecraft:is_hill", "minecraft:plains", "minecraft:sunflower_plains",
            "minecraft:snowy_plains", "minecraft:meadow", "minecraft:cherry_grove", "minecraft:desert"]
STRUCTS = {
    "hello_kitty_house": ("hello_kitty", 24, 9, 1427819, LAND_ALL),
    "my_melody_house": ("my_melody", 32, 12, 2981347, ["#minecraft:is_forest", "minecraft:cherry_grove", "minecraft:meadow",
                                                       "minecraft:plains", "minecraft:sunflower_plains"]),
    "kuromi_tower": ("kuromi", 32, 12, 5521903, ["minecraft:dark_forest", "#minecraft:is_taiga", "#minecraft:is_badlands",
                                                 "minecraft:windswept_hills", "minecraft:snowy_plains", "minecraft:desert"]),
    "cinnamoroll_cafe": ("cinnamoroll", 32, 12, 7340211, ["minecraft:plains", "minecraft:sunflower_plains", "minecraft:meadow",
                                                          "minecraft:snowy_plains", "#minecraft:is_savanna", "minecraft:cherry_grove"]),
    "purin_pudding_house": ("purin", 32, 12, 9187651, ["minecraft:plains", "minecraft:sunflower_plains", "minecraft:meadow",
                                                       "#minecraft:is_savanna", "minecraft:desert", "#minecraft:is_badlands"]),
}
FOUND = 4  # capas de cimientos de cada plantilla (debe coincidir con structures.py)


def wj(path, obj):
    os.makedirs(os.path.dirname(path), exist_ok=True)
    with open(path, "w", encoding="utf-8") as fh:
        json.dump(obj, fh, indent=2, ensure_ascii=False)
        fh.write("\n")


def blocks_and_items():
    simple = ["pink_heart_block", "cloud_block", "kuromi_skull_block", "star_lamp"]
    for b in simple:
        wj("%s/models/block/%s.json" % (ASSETS, b),
           {"parent": "minecraft:block/cube_all", "textures": {"all": "%s:block/%s" % (NS, b)}})
        wj("%s/blockstates/%s.json" % (ASSETS, b), {"variants": {"": {"model": "%s:block/%s" % (NS, b)}}})
        wj("%s/models/item/%s.json" % (ASSETS, b), {"parent": "%s:block/%s" % (NS, b)})

    wj("%s/models/block/pudding_block.json" % ASSETS, {
        "parent": "minecraft:block/cube_bottom_top",
        "textures": {"top": NS + ":block/pudding_block_top", "bottom": NS + ":block/pudding_block_bottom",
                     "side": NS + ":block/pudding_block_side"}})
    wj("%s/blockstates/pudding_block.json" % ASSETS, {"variants": {"": {"model": NS + ":block/pudding_block"}}})
    wj("%s/models/item/pudding_block.json" % ASSETS, {"parent": NS + ":block/pudding_block"})

    for _, _, _, _, _, plush in CHARS:
        wj("%s/models/block/%s.json" % (ASSETS, plush), {
            "parent": "minecraft:block/orientable",
            "textures": {"top": "%s:block/%s_side" % (NS, plush), "front": "%s:block/%s_front" % (NS, plush),
                         "side": "%s:block/%s_side" % (NS, plush)}})
        wj("%s/blockstates/%s.json" % (ASSETS, plush), {"variants": {
            "facing=north": {"model": "%s:block/%s" % (NS, plush)},
            "facing=east": {"model": "%s:block/%s" % (NS, plush), "y": 90},
            "facing=south": {"model": "%s:block/%s" % (NS, plush), "y": 180},
            "facing=west": {"model": "%s:block/%s" % (NS, plush), "y": 270}}})
        wj("%s/models/item/%s.json" % (ASSETS, plush), {"parent": "%s:block/%s" % (NS, plush)})

    # ítems planos
    for _, eid, _, food, charm, _ in CHARS:
        for it in (food, charm):
            wj("%s/models/item/%s.json" % (ASSETS, it),
               {"parent": "minecraft:item/generated", "textures": {"layer0": "%s:item/%s" % (NS, it)}})
        wj("%s/models/item/%s_spawn_egg.json" % (ASSETS, eid), {"parent": "minecraft:item/template_spawn_egg"})

    # loot tables de bloques (se sueltan a sí mismos)
    all_blocks = simple + ["pudding_block"] + [c[5] for c in CHARS]
    for b in all_blocks:
        wj("%s/loot_tables/blocks/%s.json" % (DATA, b), {
            "type": "minecraft:block",
            "pools": [{"rolls": 1.0, "bonus_rolls": 0.0,
                       "entries": [{"type": "minecraft:item", "name": "%s:%s" % (NS, b)}],
                       "conditions": [{"condition": "minecraft:survives_explosion"}]}]})


def lang():
    es = {"itemGroup.sanriofriends": "Sanrio Friends",
          "block.sanriofriends.pink_heart_block": "Bloque corazón rosa",
          "block.sanriofriends.cloud_block": "Bloque nube",
          "block.sanriofriends.pudding_block": "Bloque de pudín",
          "block.sanriofriends.kuromi_skull_block": "Bloque calavera de Kuromi",
          "block.sanriofriends.star_lamp": "Lámpara estrella"}
    en = {"itemGroup.sanriofriends": "Sanrio Friends",
          "block.sanriofriends.pink_heart_block": "Pink Heart Block",
          "block.sanriofriends.cloud_block": "Cloud Block",
          "block.sanriofriends.pudding_block": "Pudding Block",
          "block.sanriofriends.kuromi_skull_block": "Kuromi Skull Block",
          "block.sanriofriends.star_lamp": "Star Lamp"}
    names = {
        "kitty_apple_pie": ("Tarta de manzana de Hello Kitty", "Hello Kitty's Apple Pie"),
        "melody_almond_cake": ("Bizcocho de almendra de My Melody", "My Melody's Almond Cake"),
        "kuromi_cupcake": ("Cupcake de Kuromi", "Kuromi's Cupcake"),
        "cinnamoroll_roll": ("Rollito de canela de Cinnamoroll", "Cinnamoroll's Cinnamon Roll"),
        "purin_pudding": ("Pudín de Purin", "Purin's Pudding"),
        "kitty_ribbon": ("Lazo de Hello Kitty", "Hello Kitty's Ribbon"),
        "melody_hood_charm": ("Amuleto de My Melody", "My Melody's Charm"),
        "kuromi_skull_charm": ("Amuleto de Kuromi", "Kuromi's Charm"),
        "cinnamoroll_cloud_charm": ("Amuleto de nube de Cinnamoroll", "Cinnamoroll's Cloud Charm"),
        "purin_beret_charm": ("Amuleto de Purin", "Purin's Charm"),
        "kitty_plush": ("Peluche de Hello Kitty", "Hello Kitty Plush"),
        "melody_plush": ("Peluche de My Melody", "My Melody Plush"),
        "kuromi_plush": ("Peluche de Kuromi", "Kuromi Plush"),
        "cinnamoroll_plush": ("Peluche de Cinnamoroll", "Cinnamoroll Plush"),
        "purin_plush": ("Peluche de Purin", "Purin Plush"),
    }
    for _, eid, name, food, charm, plush in CHARS:
        es["entity.%s.%s" % (NS, eid)] = name
        en["entity.%s.%s" % (NS, eid)] = name
        es["item.%s.%s_spawn_egg" % (NS, eid)] = "Generar " + name
        en["item.%s.%s_spawn_egg" % (NS, eid)] = name + " Spawn Egg"
        for k in (food, charm):
            es["item.%s.%s" % (NS, k)] = names[k][0]
            en["item.%s.%s" % (NS, k)] = names[k][1]
        es["block.%s.%s" % (NS, plush)] = names[plush][0]
        en["block.%s.%s" % (NS, plush)] = names[plush][1]
    wj(ASSETS + "/lang/es_es.json", es)
    wj(ASSETS + "/lang/en_us.json", en)


def recipes():
    def ing(i):
        return {"item": i}

    def shapeless(name, ingredients, result, count=1):
        wj("%s/recipes/%s.json" % (DATA, name), {
            "type": "minecraft:crafting_shapeless", "category": "misc",
            "ingredients": [ing(i) for i in ingredients],
            "result": {"item": result, "count": count}})

    def shaped(name, pattern, key, result, count=1):
        wj("%s/recipes/%s.json" % (DATA, name), {
            "type": "minecraft:crafting_shaped", "category": "misc", "pattern": pattern,
            "key": {k: ing(v) for k, v in key.items()}, "result": {"item": result, "count": count}})

    shapeless("star_lamp", ["minecraft:glowstone", "minecraft:yellow_dye"], NS + ":star_lamp", 2)
    shapeless("pink_heart_block", ["minecraft:pink_wool", "minecraft:pink_dye"], NS + ":pink_heart_block", 2)
    shapeless("cloud_block", ["minecraft:white_wool", "minecraft:snowball"], NS + ":cloud_block", 2)
    shapeless("pudding_block", ["minecraft:yellow_wool", "minecraft:brown_wool"], NS + ":pudding_block", 2)
    shapeless("kuromi_skull_block", ["minecraft:black_wool", "minecraft:pink_dye", "minecraft:bone"], NS + ":kuromi_skull_block", 2)

    plush = {"kitty_plush": ("minecraft:white_wool", "minecraft:red_dye"),
             "melody_plush": ("minecraft:pink_wool", "minecraft:white_dye"),
             "kuromi_plush": ("minecraft:black_wool", "minecraft:pink_dye"),
             "cinnamoroll_plush": ("minecraft:white_wool", "minecraft:light_blue_dye"),
             "purin_plush": ("minecraft:yellow_wool", "minecraft:brown_dye")}
    for name, (wool, dye) in plush.items():
        shaped(name, ["WWW", "WDW", "WWW"], {"W": wool, "D": dye}, "%s:%s" % (NS, name))

    shapeless("kitty_apple_pie", ["minecraft:apple", "minecraft:apple", "minecraft:sugar", "minecraft:egg"], NS + ":kitty_apple_pie")
    shapeless("melody_almond_cake", ["minecraft:wheat", "minecraft:sugar", "minecraft:egg", "minecraft:pink_tulip"], NS + ":melody_almond_cake")
    shapeless("kuromi_cupcake", ["minecraft:wheat", "minecraft:sugar", "minecraft:sweet_berries", "minecraft:purple_dye"], NS + ":kuromi_cupcake")
    shapeless("cinnamoroll_roll", ["minecraft:wheat", "minecraft:wheat", "minecraft:sugar", "minecraft:cocoa_beans"], NS + ":cinnamoroll_roll")
    shapeless("purin_pudding", ["minecraft:egg", "minecraft:egg", "minecraft:sugar", "minecraft:honey_bottle"], NS + ":purin_pudding")


def chest_loot():
    for _, eid, _, food, charm, plush in CHARS:
        extra = {"hello_kitty": "minecraft:apple", "my_melody": "minecraft:pink_tulip", "kuromi": "minecraft:amethyst_shard",
                 "cinnamoroll": "minecraft:sugar", "purin": "minecraft:egg"}[eid]

        def entry(name, weight, mn, mx):
            return {"type": "minecraft:item", "name": name, "weight": weight,
                    "functions": [{"function": "minecraft:set_count", "count": {"min": mn, "max": mx}}]}
        wj("%s/loot_tables/chests/%s.json" % (DATA, eid), {
            "type": "minecraft:chest",
            "pools": [{"rolls": {"min": 3, "max": 5}, "bonus_rolls": 0.0, "entries": [
                entry("%s:%s" % (NS, food), 6, 1, 2),
                entry(extra, 6, 2, 4),
                entry("minecraft:cookie", 4, 2, 5),
                entry("minecraft:emerald", 3, 1, 3),
                entry("%s:star_lamp" % NS, 2, 1, 2),
                entry("%s:pink_heart_block" % NS, 2, 1, 3)]}]})


def worldgen():
    for sid, (eid, spacing, sep, salt, biomes) in STRUCTS.items():
        wj("%s/tags/worldgen/biome/has_%s.json" % (DATA, sid), {"values": biomes})
        wj("%s/worldgen/structure/%s.json" % (DATA, sid), {
            "type": "minecraft:jigsaw",
            "biomes": "#%s:has_%s" % (NS, sid),
            "step": "surface_structures",
            "spawn_overrides": {},
            "terrain_adaptation": "beard_thin",
            "start_pool": "%s:%s/start" % (NS, sid),
            "size": 1,
            "start_height": {"absolute": -(FOUND + 1)},
            "project_start_to_heightmap": "WORLD_SURFACE_WG",
            "max_distance_from_center": 80,
            "use_expansion_hack": False})
        wj("%s/worldgen/template_pool/%s/start.json" % (DATA, sid), {
            "fallback": "minecraft:empty",
            "elements": [{"weight": 1, "element": {
                "element_type": "minecraft:single_pool_element",
                "location": "%s:%s" % (NS, sid),
                "processors": "minecraft:empty",
                "projection": "rigid"}}]})
        wj("%s/worldgen/structure_set/%s.json" % (DATA, sid), {
            "structures": [{"structure": "%s:%s" % (NS, sid), "weight": 1}],
            "placement": {"type": "minecraft:random_spread", "spacing": spacing, "separation": sep, "salt": salt}})


def generate_all():
    blocks_and_items()
    lang()
    recipes()
    chest_loot()
    worldgen()


if __name__ == "__main__":
    generate_all()
    print("datos generados")
