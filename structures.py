"""Construye las 5 estructuras como plantillas NBT de Minecraft 1.20.1 (DataVersion 3465), sin librerías externas."""
import gzip
import math
import os
import random
import struct

OUT = "src/main/resources/data/sanriofriends/structures"
FOUND = 4          # capas de cimientos (debe coincidir con data.FOUND)
F = FOUND          # y de la plantilla que queda a ras de suelo
CX = CZ = 8        # centro del terreno circular de 17x17
NS = "sanriofriends"


# ------------------------------------------------------------------ NBT mínimo
class Byte(int): ID = 1
class Short(int): ID = 2
class Int(int): ID = 3
class Long(int): ID = 4
class Float(float): ID = 5
class Double(float): ID = 6


class NList:
    def __init__(self, tid, items):
        self.tid, self.items = tid, items


def type_id(v):
    if isinstance(v, (Byte, Short, Int, Long, Float, Double)):
        return v.ID
    if type(v) is int:
        return 3
    if type(v) is float:
        return 6
    if isinstance(v, str):
        return 8
    if isinstance(v, NList):
        return 9
    if isinstance(v, dict):
        return 10
    raise TypeError(type(v))


def w_str(s):
    b = s.encode("utf-8")
    return struct.pack(">H", len(b)) + b


def payload(v):
    t = type_id(v)
    if t == 1: return struct.pack(">b", v)
    if t == 2: return struct.pack(">h", v)
    if t == 3: return struct.pack(">i", v)
    if t == 4: return struct.pack(">q", v)
    if t == 5: return struct.pack(">f", v)
    if t == 6: return struct.pack(">d", v)
    if t == 8: return w_str(v)
    if t == 9:
        out = struct.pack(">bi", v.tid, len(v.items))
        for it in v.items:
            out += payload(it)
        return out
    if t == 10:
        out = b""
        for k, val in v.items():
            out += struct.pack(">b", type_id(val)) + w_str(k) + payload(val)
        return out + b"\x00"


def write_nbt(path, root):
    os.makedirs(os.path.dirname(path), exist_ok=True)
    data = struct.pack(">b", 10) + w_str("") + payload(root)
    with gzip.GzipFile(path, "wb", mtime=0) as fh:
        fh.write(data)


# ------------------------------------------------------------------ bloques y plantilla
def B(name, **props):
    full = name if ":" in name else "minecraft:" + name
    return (full, tuple(sorted((k, str(v).lower() if isinstance(v, bool) else str(v)) for k, v in props.items())))


AIR = B("air")
DIRS = {"north": (0, -1), "south": (0, 1), "east": (1, 0), "west": (-1, 0)}
FENCES = {"minecraft:cherry_fence", "minecraft:dark_oak_fence"}


class Tpl:
    def __init__(self, sx, sy, sz):
        self.sx, self.sy, self.sz = sx, sy, sz
        self.c, self.nbt, self.entities, self.flowers = {}, {}, [], []

    def set(self, x, y, z, b, nbt=None):
        assert 0 <= x < self.sx and 0 <= y < self.sy and 0 <= z < self.sz, (x, y, z)
        self.c[(x, y, z)] = b
        if nbt:
            self.nbt[(x, y, z)] = nbt

    def get(self, x, y, z):
        return self.c.get((x, y, z))

    def fill(self, x1, y1, z1, x2, y2, z2, b):
        for x in range(x1, x2 + 1):
            for y in range(y1, y2 + 1):
                for z in range(z1, z2 + 1):
                    self.set(x, y, z, b)

    def door(self, x, y, z, name, facing, hinge="left"):
        self.set(x, y, z, B(name, facing=facing, half="lower", hinge=hinge, open=False, powered=False))
        self.set(x, y + 1, z, B(name, facing=facing, half="upper", hinge=hinge, open=False, powered=False))

    def bed(self, x, y, z, color, facing):
        dx, dz = DIRS[facing]
        self.set(x, y, z, B(color + "_bed", facing=facing, part="foot", occupied=False))
        self.set(x + dx, y, z + dz, B(color + "_bed", facing=facing, part="head", occupied=False))

    def chest(self, x, y, z, facing, loot):
        self.set(x, y, z, B("chest", facing=facing, type="single", waterlogged=False),
                 {"id": "minecraft:chest", "LootTable": "%s:chests/%s" % (NS, loot)})

    def entity(self, eid, x, y, z):
        self.entities.append({
            "pos": NList(6, [Double(x), Double(y), Double(z)]),
            "blockPos": NList(3, [Int(int(math.floor(x))), Int(int(math.floor(y))), Int(int(math.floor(z)))]),
            "nbt": {"id": "%s:%s" % (NS, eid), "Air": Short(300), "PersistenceRequired": Byte(1),
                    "Rotation": NList(5, [Float(0.0), Float(0.0)]),
                    "Pos": NList(6, [Double(x), Double(y), Double(z)])}})

    def connect_fences(self):
        for (x, y, z), b in list(self.c.items()):
            if b[0] in FENCES:
                props = dict(b[1])
                for d, (dx, dz) in DIRS.items():
                    nb = self.c.get((x + dx, y, z + dz))
                    props[d] = "true" if nb and nb[0] in FENCES else "false"
                self.c[(x, y, z)] = (b[0], tuple(sorted(props.items())))

    def save(self, name):
        # quitar flores que quedaron sobre algo que no es hierba/podzol
        for (x, y, z) in self.flowers:
            below = self.get(x, y - 1, z)
            if not below or below[0] not in ("minecraft:grass_block", "minecraft:podzol"):
                self.c[(x, y, z)] = AIR
        self.connect_fences()
        palette, index, blocks = [], {}, []
        for (x, y, z), b in sorted(self.c.items(), key=lambda kv: (kv[0][1], kv[0][0], kv[0][2])):
            if b not in index:
                index[b] = len(palette)
                entry = {"Name": b[0]}
                if b[1]:
                    entry["Properties"] = {k: v for k, v in b[1]}
                palette.append(entry)
            e = {"pos": NList(3, [Int(x), Int(y), Int(z)]), "state": Int(index[b])}
            if (x, y, z) in self.nbt:
                e["nbt"] = self.nbt[(x, y, z)]
            blocks.append(e)
        root = {"DataVersion": Int(3465),
                "size": NList(3, [Int(self.sx), Int(self.sy), Int(self.sz)]),
                "palette": NList(10, palette), "blocks": NList(10, blocks),
                "entities": NList(10, self.entities)}
        write_nbt("%s/%s.nbt" % (OUT, name), root)
        return len(blocks)


# ------------------------------------------------------------------ utilidades de construcción
def inside(x, z):
    return math.hypot(x - CX, z - CZ) <= 8.3


def dist(x, z):
    return math.hypot(x - CX, z - CZ)


def base_plot(t, ground, under, clear_h):
    for x in range(17):
        for z in range(17):
            if not inside(x, z):
                continue
            for y in range(FOUND):
                t.set(x, y, z, under)
            t.set(x, F, z, ground)
            for y in range(F + 1, F + 1 + clear_h):
                t.set(x, y, z, AIR)


def scatter(t, rnd, allowed, names, prob):
    for x in range(17):
        for z in range(17):
            if inside(x, z) and allowed(x, z) and rnd.random() < prob:
                n = rnd.choice(names)
                b = B("pink_petals", flower_amount=rnd.randint(1, 4), facing="north") if n == "pink_petals" else B(n)
                t.set(x, F + 1, z, b)
                t.flowers.append((x, F + 1, z))


def fence_ring(t, name, gaps):
    for x in range(17):
        for z in range(17):
            if not inside(x, z) or (x, z) in gaps:
                continue
            edge = any(not inside(x + dx, z + dz) for dx, dz in DIRS.values())
            if edge:
                t.set(x, F + 1, z, B(name))


def stair(name, facing, half="bottom"):
    return B(name, facing=facing, half=half, shape="straight", waterlogged=False)


def perimeter(x1, z1, x2, z2):
    for x in range(x1, x2 + 1):
        for z in range(z1, z2 + 1):
            if x in (x1, x2) or z in (z1, z2):
                yield x, z


def carpet_free(t, color, cells, y):
    for (x, z) in cells:
        if t.get(x, y, z) == AIR:
            t.set(x, y, z, B(color + "_carpet"))


# ------------------------------------------------------------------ CASITA DE HELLO KITTY
def build_kitty():
    t = Tpl(17, F + 12, 17)
    rnd = random.Random(11)
    base_plot(t, B("grass_block"), B("dirt"), 11)
    WHITE, PINKC = B("white_concrete"), B("pink_concrete")
    PLANKS = B("cherry_planks")
    gaps = {(x, z) for x in (7, 8, 9) for z in (15, 16)}
    scatter(t, rnd, lambda x, z: not (3 <= x <= 13 and 3 <= z <= 13) and not (7 <= x <= 9 and z >= 13),
            ["pink_tulip", "pink_tulip", "white_tulip", "red_tulip", "oxeye_daisy", "azure_bluet", "pink_petals"], 0.22)
    # suelo ajedrezado
    for x in range(5, 12):
        for z in range(4, 13):
            t.set(x, F, z, WHITE if (x + z) % 2 == 0 else PINKC)
    # paredes
    for x, z in perimeter(5, 4, 11, 12):
        corner = x in (5, 11) and z in (4, 12)
        for y in range(F + 1, F + 5):
            t.set(x, y, z, B("stripped_cherry_log", axis="y") if corner else WHITE)
    # techo interior
    for x in range(5, 12):
        for z in range(4, 13):
            t.set(x, F + 5, z, PLANKS)
    # tejado a dos aguas (cumbrera de norte a sur, frontón mirando al sur)
    for k in range(4):
        y = F + 5 + k
        for z in range(3, 14):
            t.set(4 + k, y, z, stair("cherry_stairs", "east"))
            t.set(12 - k, y, z, stair("cherry_stairs", "west"))
    for z in range(3, 14):
        t.set(8, F + 9, z, PLANKS)
    for gz in (4, 12):
        for k in range(1, 4):
            for x in range(5 + k, 12 - k):
                t.set(x, F + 5 + k, gz, WHITE)
    # puerta y ventanas
    t.door(8, F + 1, 12, "cherry_door", "north")
    GLASS = B("pink_stained_glass")
    for y in (F + 2, F + 3):
        for z in (7, 8):
            t.set(5, y, z, GLASS); t.set(11, y, z, GLASS)
        for x in (6, 10):
            t.set(x, y, 12, GLASS)
        for x in (7, 8, 9):
            t.set(x, y, 4, GLASS)
    # lazo gigante sobre la puerta
    RED = B("red_wool")
    for x in (6, 7, 9, 10):
        t.set(x, F + 3, 13, RED)
    t.set(8, F + 3, 13, B("red_concrete"))
    t.set(6, F + 4, 13, RED); t.set(10, F + 4, 13, RED)
    # interior
    t.bed(6, F + 1, 6, "pink", "north")
    t.set(10, F + 1, 5, B(NS + ":kitty_plush", facing="south"))
    t.chest(10, F + 1, 11, "west", "hello_kitty")
    t.set(8, F + 1, 7, PLANKS)
    t.set(8, F + 2, 7, B("cake", bites=0))
    t.set(8, F + 5, 8, B(NS + ":star_lamp"))
    t.set(6, F + 5, 10, B(NS + ":star_lamp"))
    carpet_free(t, "pink", [(x, z) for x in range(6, 11) for z in range(5, 12)], F + 1)
    # camino, corazones y valla
    for z in range(13, 17):
        for x in (7, 8, 9):
            if inside(x, z):
                t.set(x, F, z, PINKC)
    t.set(6, F + 1, 14, B(NS + ":pink_heart_block")); t.set(10, F + 1, 14, B(NS + ":pink_heart_block"))
    fence_ring(t, "cherry_fence", gaps)
    t.entity("hello_kitty", 8.5, F + 1.0, 9.5)
    return t.save("hello_kitty_house")


# ------------------------------------------------------------------ CASA-SETA DE MY MELODY
def build_melody():
    t = Tpl(17, F + 12, 17)
    rnd = random.Random(22)
    base_plot(t, B("grass_block"), B("dirt"), 11)
    scatter(t, rnd, lambda x, z: dist(x, z) > 4.8 and not (7 <= x <= 9 and z >= 12),
            ["pink_tulip", "lily_of_the_valley", "red_mushroom", "pink_petals", "pink_petals", "azure_bluet"], 0.2)
    WALL, WOOL, SPOT = B("white_concrete"), B("pink_wool"), B("white_wool")
    for x in range(17):
        for z in range(17):
            d = dist(x, z)
            if d <= 4.5:
                t.set(x, F, z, B("white_wool"))
            if 3.3 < d <= 4.5:
                for y in range(F + 1, F + 5):
                    t.set(x, y, z, WALL)
            # sombrero de la seta
            if d <= 7.3:
                t.set(x, F + 5, z, WOOL)
            if d <= 6.3:
                t.set(x, F + 6, z, SPOT if (d > 4.8 and (x * 7 + z * 5) % 9 == 0) else WOOL)
            if d <= 4.8:
                t.set(x, F + 7, z, SPOT if (d > 3.0 and (x * 3 + z * 7) % 8 == 0) else WOOL)
            if d <= 3.0:
                t.set(x, F + 8, z, WOOL)
            if d <= 1.5:
                t.set(x, F + 9, z, WOOL)
    t.door(8, F + 1, 12, "cherry_door", "north")
    GLASS = B("pink_stained_glass")
    for y in (F + 2, F + 3):
        t.set(12, y, 8, GLASS); t.set(4, y, 8, GLASS); t.set(8, y, 4, GLASS)
    t.bed(6, F + 1, 7, "pink", "north")
    t.set(10, F + 1, 6, B(NS + ":melody_plush", facing="south"))
    t.chest(10, F + 1, 9, "west", "my_melody")
    t.set(8, F + 1, 6, B("cherry_planks"))
    t.set(8, F + 2, 6, B("cake", bites=0))
    t.set(8, F + 5, 8, B(NS + ":star_lamp"))
    carpet_free(t, "pink", [(x, z) for x in range(5, 12) for z in range(5, 12) if dist(x, z) <= 3.3], F + 1)
    for z in range(13, 17):
        for x in (7, 8, 9):
            if inside(x, z):
                t.set(x, F, z, B("pink_concrete"))
    t.set(6, F + 1, 14, B(NS + ":pink_heart_block")); t.set(10, F + 1, 14, B(NS + ":pink_heart_block"))
    t.entity("my_melody", 8.5, F + 1.0, 9.5)
    return t.save("my_melody_house")


# ------------------------------------------------------------------ TORRE DE KUROMI
def build_kuromi():
    t = Tpl(17, F + 20, 17)
    rnd = random.Random(33)
    base_plot(t, B("podzol"), B("dirt"), 19)
    BLK, PUR = B("black_concrete"), B("purple_concrete")
    BRICK = B("polished_blackstone_bricks")
    gaps = {(x, z) for x in (7, 8, 9) for z in (15, 16)}
    scatter(t, rnd, lambda x, z: not (3 <= x <= 13 and 3 <= z <= 13) and not (7 <= x <= 9 and z >= 12),
            ["allium", "allium", "blue_orchid", "dead_bush"], 0.18)
    for x in range(5, 12):
        for z in range(5, 12):
            t.set(x, F, z, BRICK)
    for x, z in perimeter(5, 5, 11, 11):
        corner = x in (5, 11) and z in (5, 11)
        for y in range(F + 1, F + 12):
            if corner or y in (F + 6, F + 11):
                b = PUR
            elif y <= F + 2:
                b = BRICK
            else:
                b = BLK
            t.set(x, y, z, b)
    for x in range(5, 12):
        for z in range(5, 12):
            t.set(x, F + 12, z, PUR)
    # planta intermedia con hueco para la escalera
    for x in range(6, 11):
        for z in range(6, 11):
            t.set(x, F + 6, z, B("dark_oak_planks"))
    for y in range(F + 1, F + 8):
        t.set(8, y, 6, B("ladder", facing="south", waterlogged=False))
    t.door(8, F + 1, 11, "dark_oak_door", "north")
    t.set(8, F + 4, 11, B(NS + ":kuromi_skull_block"))
    GLASS = B("purple_stained_glass")
    for (x, y, z) in [(8, F + 3, 5), (5, F + 3, 8), (11, F + 3, 8), (8, F + 9, 5), (5, F + 9, 8), (11, F + 9, 8), (8, F + 9, 11)]:
        t.set(x, y, z, GLASS)
    # tejado piramidal con calavera
    for i, (lo, hi) in enumerate([(4, 12), (5, 11), (6, 10), (7, 9), (8, 8)]):
        y = F + 13 + i
        for x in range(lo, hi + 1):
            for z in range(lo, hi + 1):
                t.set(x, y, z, PUR if i % 2 == 1 else BLK)
    t.set(8, F + 18, 8, B(NS + ":kuromi_skull_block"))
    # planta baja
    t.set(6, F + 1, 9, B(NS + ":kuromi_plush", facing="east"))
    t.chest(10, F + 1, 10, "west", "kuromi")
    for (x, z) in [(10, 7), (6, 6)]:
        t.set(x, F + 1, z, B("pink_candle", candles=3, lit=True, waterlogged=False))
    t.set(7, F + 6, 9, B(NS + ":star_lamp"))
    # planta alta
    t.bed(10, F + 7, 8, "black", "west")
    t.set(6, F + 7, 6, B(NS + ":kuromi_plush", facing="south"))
    t.set(8, F + 12, 8, B(NS + ":star_lamp"))
    t.set(8, F + 12, 10, B(NS + ":star_lamp"))
    carpet_free(t, "purple", [(x, z) for x in range(6, 11) for z in range(7, 11)], F + 7)
    for z in range(12, 17):
        for x in (7, 8, 9):
            if inside(x, z):
                t.set(x, F, z, B("polished_blackstone"))
    fence_ring(t, "dark_oak_fence", gaps)
    t.entity("kuromi", 8.5, F + 1.0, 9.5)
    return t.save("kuromi_tower")


# ------------------------------------------------------------------ CAFÉ DE CINNAMOROLL
def build_cinnamoroll():
    t = Tpl(17, F + 12, 17)
    rnd = random.Random(44)
    base_plot(t, B("grass_block"), B("dirt"), 11)
    WHITE, LB = B("white_concrete"), B("light_blue_concrete")
    scatter(t, rnd, lambda x, z: not (2 <= x <= 14 and 3 <= z <= 13) and not (7 <= x <= 9 and z >= 12),
            ["white_tulip", "azure_bluet", "cornflower", "oxeye_daisy"], 0.2)
    for x in range(4, 13):
        for z in range(5, 12):
            t.set(x, F, z, WHITE if (x + z) % 2 == 0 else LB)
    for x, z in perimeter(4, 5, 12, 11):
        corner = x in (4, 12) and z in (5, 11)
        for y in range(F + 1, F + 5):
            t.set(x, y, z, LB if (corner or y == F + 1) else WHITE)
    # tejado de nubes
    WOOL, LBW = B("white_wool"), B("light_blue_wool")
    for x in range(3, 14):
        for z in range(4, 13):
            t.set(x, F + 5, z, WOOL)
    blobs = [(5, 6, 2.2), (9, 5, 2.0), (11, 9, 2.3), (7, 10, 2.0), (8, 8, 2.7)]
    for x in range(3, 14):
        for z in range(4, 13):
            for (bx, bz, r) in blobs:
                d = math.hypot(x - bx, z - bz)
                if d <= r:
                    t.set(x, F + 6, z, LBW if (x * 5 + z * 3) % 11 == 0 else WOOL)
                if d <= r - 1.1:
                    t.set(x, F + 7, z, WOOL)
    t.door(8, F + 1, 11, "birch_door", "north")
    GLASS = B("light_blue_stained_glass")
    for y in (F + 2, F + 3):
        for x in (5, 6, 10, 11):
            t.set(x, y, 11, GLASS)
        for z in (7, 8, 9):
            t.set(4, y, z, GLASS); t.set(12, y, z, GLASS)
        for x in (7, 8, 9):
            t.set(x, y, 5, GLASS)
    for x in range(5, 12):
        t.set(x, F + 4, 12, LBW if x % 2 == 1 else WOOL)  # toldo a rayas
    # interior: mostrador, mesas, cama
    for x in range(5, 10):
        t.set(x, F + 1, 6, B("quartz_block"))
    t.set(6, F + 2, 6, B("cake", bites=0)); t.set(8, F + 2, 6, B("cake", bites=0))
    for x in (6, 10):
        t.set(x, F + 1, 9, B("birch_planks"))
        t.set(x, F + 2, 9, B("white_carpet"))
    t.bed(11, F + 1, 7, "light_blue", "north")
    t.set(10, F + 1, 6, B(NS + ":cinnamoroll_plush", facing="south"))
    t.chest(5, F + 1, 10, "east", "cinnamoroll")
    t.set(6, F + 5, 8, B(NS + ":star_lamp")); t.set(10, F + 5, 8, B(NS + ":star_lamp"))
    carpet_free(t, "light_blue", [(x, z) for x in range(5, 12) for z in range(6, 11)], F + 1)
    for z in range(12, 17):
        for x in (7, 8, 9):
            if inside(x, z):
                t.set(x, F, z, LB)
    for (x, y, z) in [(2, F + 1, 8), (2, F + 1, 9), (2, F + 2, 8), (14, F + 1, 7), (14, F + 1, 8), (14, F + 2, 8)]:
        t.set(x, y, z, B(NS + ":cloud_block"))
    t.entity("cinnamoroll", 8.5, F + 2.0, 8.5)
    return t.save("cinnamoroll_cafe")


# ------------------------------------------------------------------ CASA-PUDÍN DE PURIN
def build_purin():
    t = Tpl(17, F + 12, 17)
    rnd = random.Random(55)
    base_plot(t, B("grass_block"), B("dirt"), 11)
    Y, BR = B("yellow_concrete"), B("brown_concrete")
    scatter(t, rnd, lambda x, z: dist(x, z) > 7.2 and not (7 <= x <= 9 and z >= 12),
            ["dandelion", "orange_tulip", "oxeye_daisy", "dandelion"], 0.25)
    for x in range(17):
        for z in range(17):
            d = dist(x, z)
            if d <= 6.5:
                t.set(x, F, z, B("yellow_terracotta"))
            if 4.5 < d <= 5.5:
                for y in (F + 1, F + 2):
                    t.set(x, y, z, Y)
            if 5.5 < d <= 6.5:
                t.set(x, F + 3, z, BR if (x * 5 + z * 3) % 11 == 0 else Y)
                t.set(x, F + 4, z, BR if (x * 5 + z * 3) % 7 == 0 else Y)
            if d <= 7.0:
                t.set(x, F + 5, z, BR)
            if d <= 5.8:
                t.set(x, F + 6, z, B("orange_terracotta"))
            if d <= 1.2:
                t.set(x, F + 7, z, B("red_concrete"))
    t.set(8, F + 8, 8, B("red_concrete"))
    t.set(8, F + 9, 8, B("lime_concrete"))
    t.door(8, F + 1, 13, "birch_door", "north")
    GLASS = B("yellow_stained_glass")
    for y in (F + 3, F + 4):
        for (x, z) in [(8, 14), (8, 2), (2, 8), (14, 8)]:
            t.set(x, y, z, GLASS)
    for x in (6, 8, 10):
        t.set(x, F + 1, 6, B("birch_planks"))
    t.set(7, F + 1, 6, B(NS + ":pudding_block")); t.set(9, F + 1, 6, B(NS + ":pudding_block"))
    t.bed(11, F + 1, 9, "yellow", "north")
    t.set(5, F + 1, 9, B(NS + ":purin_plush", facing="east"))
    t.chest(5, F + 1, 7, "east", "purin")
    t.set(8, F + 5, 8, B(NS + ":star_lamp")); t.set(6, F + 5, 10, B(NS + ":star_lamp"))
    carpet_free(t, "yellow", [(x, z) for x in range(17) for z in range(17) if dist(x, z) <= 4.5], F + 1)
    for x in (7, 8, 9):
        t.set(x, F, 14, Y)
    for z in (15, 16):
        if inside(8, z):
            t.set(8, F, z, Y)
    t.set(7, F + 1, 15, B(NS + ":pudding_block")); t.set(9, F + 1, 15, B(NS + ":pudding_block"))
    t.entity("purin", 8.5, F + 1.0, 9.5)
    return t.save("purin_pudding_house")


def generate_all():
    for fn in (build_kitty, build_melody, build_kuromi, build_cinnamoroll, build_purin):
        n = fn()
        print(fn.__name__, n, "bloques")


if __name__ == "__main__":
    generate_all()
