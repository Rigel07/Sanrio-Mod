"""Genera todas las texturas (pixel art propio, estilo cartoon) con Pillow."""
import os
import random
from PIL import Image, ImageDraw
import parts

OUT = "src/main/resources/assets/sanriofriends/textures"

WHITE = (250, 248, 250)
CREAM = (255, 244, 230)
PINK = (255, 182, 206)
LPINK = (255, 214, 228)
HOT = (255, 105, 160)
RED = (222, 44, 66)
DRED = (150, 20, 40)
BLACK = (32, 28, 38)
DBLACK = (22, 18, 28)
PURPLE = (130, 70, 190)
LPURPLE = (190, 150, 235)
LBLUE = (150, 205, 250)
BLUE = (70, 120, 210)
YEL = (250, 215, 95)
LYEL = (255, 238, 170)
BROWN = (125, 78, 45)
DBROWN = (90, 52, 28)
EYE = (30, 25, 40)

FACE_SHADE = {"top": 1.0, "front": 1.0, "right": 0.92, "left": 0.92, "back": 0.88, "bottom": 0.78}


def sh(c, k):
    return tuple(max(0, min(255, int(v * k))) for v in c)


def rect(img, x, y, w, h, c):
    for i in range(int(w)):
        for j in range(int(h)):
            xx, yy = int(x) + i, int(y) + j
            if 0 <= xx < img.width and 0 <= yy < img.height:
                img.putpixel((xx, yy), tuple(c) + (255,))


def box_rects(u, v, w, h, d):
    return {
        "top": (u + d, v, w, d), "bottom": (u + d + w, v, w, d),
        "right": (u, v + d, d, h), "front": (u + d, v + d, w, h),
        "left": (u + d + w, v + d, d, h), "back": (u + 2 * d + w, v + d, w, h),
    }


def paint_box(img, dim, color, **faces):
    u, v, w, h, d = dim
    rects = box_rects(u, v, w, h, d)
    for face, r in rects.items():
        c = faces.get(face, color)
        if c is None:
            continue
        rect(img, *r, sh(c, FACE_SHADE[face]) if face not in faces else c)


def fpx(img, dim, x, y, c, face="front"):
    """Pixel en una cara (coordenadas dentro de la cara)."""
    r = box_rects(*dim)[face]
    if 0 <= x < r[2] and 0 <= y < r[3]:
        img.putpixel((r[0] + x, r[1] + y), tuple(c) + (255,))


def frect(img, dim, x, y, w, h, c, face="front"):
    for i in range(w):
        for j in range(h):
            fpx(img, dim, x + i, y + j, c, face)


# ============================================================ ENTIDADES

def entity(char_key, cid):
    img = Image.new("RGBA", (64, 64), (0, 0, 0, 0))
    D = lambda n: parts.dims(char_key, n)
    head, body = D("head"), D("body")

    if char_key == "KITTY":
        paint_box(img, head, WHITE)
        # cara: ojos, nariz, bigotes
        for x in (2, 7):
            frect(img, head, x, 3, 1, 2, EYE)
        frect(img, head, 4, 5, 2, 1, (255, 200, 40))
        for y in (4, 6):
            frect(img, head, 0, y, 2, 1, EYE)
            frect(img, head, 8, y, 2, 1, EYE)
        paint_box(img, body, WHITE)
        frect(img, body, 0, 2, 6, 4, BLUE)
        frect(img, body, 1, 0, 1, 2, BLUE)
        frect(img, body, 4, 0, 1, 2, BLUE)
        fpx(img, body, 1, 2, (255, 215, 60)); fpx(img, body, 4, 2, (255, 215, 60))
        paint_box(img, D("right_arm"), WHITE); paint_box(img, D("left_arm"), WHITE)
        paint_box(img, D("right_leg"), WHITE); paint_box(img, D("left_leg"), WHITE)
        paint_box(img, D("ear_l"), WHITE, front=LPINK); paint_box(img, D("ear_r"), WHITE, front=LPINK)
        paint_box(img, D("acc1"), RED); paint_box(img, D("acc2"), DRED)
        paint_box(img, D("tail"), WHITE)

    elif char_key == "MELODY":
        paint_box(img, head, WHITE)
        for x in (2, 7):
            frect(img, head, x, 4, 1, 2, EYE)
        frect(img, head, 4, 6, 2, 1, (255, 190, 90))
        frect(img, head, 1, 6, 2, 1, (255, 170, 190)); frect(img, head, 7, 6, 2, 1, (255, 170, 190))
        paint_box(img, body, PINK)
        frect(img, body, 0, 0, 6, 1, WHITE)
        frect(img, body, 2, 2, 2, 3, LPINK)
        paint_box(img, D("right_arm"), WHITE); paint_box(img, D("left_arm"), WHITE)
        paint_box(img, D("right_leg"), WHITE); paint_box(img, D("left_leg"), WHITE)
        paint_box(img, D("ear_l"), (255, 165, 198), front=LPINK); paint_box(img, D("ear_r"), (255, 165, 198), front=LPINK)
        hood = (255, 158, 196)
        paint_box(img, D("acc1"), hood)
        frect(img, D("acc1"), 0, 3, 11, 1, WHITE)  # ribete blanco de la capucha
        paint_box(img, D("acc2"), (255, 90, 130), front=(255, 90, 130))
        fpx(img, D("acc2"), 1, 1, (255, 230, 90))
        paint_box(img, D("tail"), WHITE)

    elif char_key == "KUROMI":
        paint_box(img, head, WHITE)
        for x in (2, 7):
            frect(img, head, x, 4, 1, 2, EYE)
        fpx(img, head, 2, 4, (255, 255, 255)); fpx(img, head, 7, 4, (255, 255, 255))
        frect(img, head, 4, 6, 2, 1, (255, 150, 190))
        frect(img, head, 1, 6, 2, 1, (255, 160, 190)); frect(img, head, 7, 6, 2, 1, (255, 160, 190))
        paint_box(img, body, BLACK)
        frect(img, body, 0, 3, 6, 1, PURPLE)
        frect(img, body, 2, 1, 2, 1, HOT)
        paint_box(img, D("right_arm"), BLACK); paint_box(img, D("left_arm"), BLACK)
        paint_box(img, D("right_leg"), DBLACK); paint_box(img, D("left_leg"), DBLACK)
        paint_box(img, D("ear_l"), BLACK, front=(110, 50, 150)); paint_box(img, D("ear_r"), BLACK, front=(110, 50, 150))
        paint_box(img, D("acc1"), BLACK)
        frect(img, D("acc1"), 0, 3, 11, 1, PURPLE)
        sk = D("acc2")
        paint_box(img, sk, HOT, front=HOT)
        fpx(img, sk, 1, 1, EYE); fpx(img, sk, 2, 1, EYE)
        fpx(img, sk, 1, 2, (255, 230, 240)); fpx(img, sk, 2, 2, (255, 230, 240))
        paint_box(img, D("tail"), BLACK)
        fpx(img, D("tail"), 0, 3, HOT)  # punta rosa

    elif char_key == "CINNAMOROLL":
        paint_box(img, head, WHITE)
        for x in (2, 7):
            frect(img, head, x, 3, 1, 2, (70, 140, 230))
            fpx(img, head, x, 3, (255, 255, 255))
        frect(img, head, 4, 5, 2, 1, EYE)
        frect(img, head, 1, 5, 2, 1, (255, 175, 195)); frect(img, head, 7, 5, 2, 1, (255, 175, 195))
        paint_box(img, body, WHITE)
        frect(img, body, 1, 1, 4, 4, (232, 244, 255))
        paint_box(img, D("right_arm"), WHITE); paint_box(img, D("left_arm"), WHITE)
        paint_box(img, D("right_leg"), WHITE); paint_box(img, D("left_leg"), WHITE)
        paint_box(img, D("ear_l"), WHITE, front=(225, 240, 255)); paint_box(img, D("ear_r"), WHITE, front=(225, 240, 255))
        paint_box(img, D("acc1"), WHITE)
        paint_box(img, D("acc2"), WHITE)
        paint_box(img, D("tail"), WHITE, back=(205, 228, 252))

    elif char_key == "PURIN":
        paint_box(img, head, YEL)
        for x in (2, 7):
            frect(img, head, x, 3, 1, 2, EYE)
        frect(img, head, 4, 5, 2, 1, DBROWN)
        fpx(img, head, 4, 6, DBROWN); fpx(img, head, 5, 6, DBROWN)
        paint_box(img, body, YEL)
        frect(img, body, 1, 1, 4, 4, LYEL)
        paint_box(img, D("right_arm"), YEL); paint_box(img, D("left_arm"), YEL)
        paint_box(img, D("right_leg"), YEL); paint_box(img, D("left_leg"), YEL)
        paint_box(img, D("ear_l"), BROWN); paint_box(img, D("ear_r"), BROWN)
        paint_box(img, D("acc1"), BROWN)
        paint_box(img, D("acc2"), DBROWN)
        paint_box(img, D("tail"), YEL)

    os.makedirs(OUT + "/entity", exist_ok=True)
    img.save("%s/entity/%s.png" % (OUT, cid))


# ============================================================ BLOQUES (16x16)

def noise_tile(base, amp, seed):
    rnd = random.Random(seed)
    img = Image.new("RGBA", (16, 16))
    for x in range(16):
        for y in range(16):
            k = 1.0 + rnd.uniform(-amp, amp)
            img.putpixel((x, y), sh(base, k) + (255,))
    return img


def block_textures():
    d = OUT + "/block"
    os.makedirs(d, exist_ok=True)

    # corazón rosa
    img = noise_tile((255, 150, 190), 0.04, 1)
    dr = ImageDraw.Draw(img)
    dr.ellipse((2, 3, 7, 8), fill=(255, 205, 225)); dr.ellipse((8, 3, 13, 8), fill=(255, 205, 225))
    dr.polygon([(2, 7), (13, 7), (7, 13)], fill=(255, 205, 225))
    dr.point((4, 4), fill=(255, 255, 255)); dr.point((5, 4), fill=(255, 255, 255))
    dr.rectangle((0, 0, 15, 15), outline=(235, 110, 160))
    img.save(d + "/pink_heart_block.png")

    # nube
    img = noise_tile((246, 249, 255), 0.02, 2)
    dr = ImageDraw.Draw(img)
    for (x0, y0, x1, y1) in [(1, 8, 7, 14), (5, 4, 11, 11), (9, 8, 15, 14)]:
        dr.ellipse((x0, y0, x1, y1), outline=(215, 230, 250))
    dr.rectangle((0, 0, 15, 15), outline=(228, 238, 252))
    img.save(d + "/cloud_block.png")

    # pudín
    side = noise_tile((250, 208, 88), 0.03, 3)
    dr = ImageDraw.Draw(side)
    for x in (3, 7, 11):
        dr.line((x, 0, x, 15), fill=(240, 190, 70))
    dr.rectangle((0, 0, 15, 2), fill=(150, 85, 30))
    for x, l in [(1, 4), (4, 5), (8, 4), (12, 6), (14, 3)]:
        dr.line((x, 0, x, l), fill=(150, 85, 30))
    side.save(d + "/pudding_block_side.png")
    top = noise_tile((140, 78, 28), 0.05, 4)
    dr = ImageDraw.Draw(top)
    dr.ellipse((3, 3, 12, 12), outline=(190, 115, 45))
    dr.point((5, 5), fill=(235, 170, 80)); dr.point((6, 5), fill=(235, 170, 80))
    top.save(d + "/pudding_block_top.png")
    noise_tile((235, 190, 70), 0.03, 5).save(d + "/pudding_block_bottom.png")

    # calavera de Kuromi
    img = noise_tile((34, 28, 44), 0.03, 6)
    dr = ImageDraw.Draw(img)
    dr.rectangle((0, 0, 15, 15), outline=(110, 60, 170))
    dr.ellipse((4, 2, 11, 9), fill=(255, 105, 175))
    dr.rectangle((6, 8, 9, 12), fill=(255, 105, 175))
    dr.rectangle((5, 5, 6, 6), fill=(30, 20, 40)); dr.rectangle((9, 5, 10, 6), fill=(30, 20, 40))
    dr.point((7, 7), fill=(30, 20, 40)); dr.point((8, 7), fill=(30, 20, 40))
    for x in (6, 8):
        dr.line((x, 10, x, 12), fill=(30, 20, 40))
    img.save(d + "/kuromi_skull_block.png")

    # lámpara estrella
    img = noise_tile((255, 232, 140), 0.02, 7)
    dr = ImageDraw.Draw(img)
    dr.rectangle((0, 0, 15, 15), outline=(225, 175, 60))
    star = [(8, 2), (9, 6), (13, 6), (10, 9), (11, 13), (8, 11), (5, 13), (6, 9), (3, 6), (7, 6)]
    dr.polygon(star, fill=(255, 255, 235))
    img.save(d + "/star_lamp.png")

    # peluches
    for key, cid, fur, plush in [("KITTY", "hello_kitty", WHITE, "kitty_plush"),
                                 ("MELODY", "my_melody", WHITE, "melody_plush"),
                                 ("KUROMI", "kuromi", WHITE, "kuromi_plush"),
                                 ("CINNAMOROLL", "cinnamoroll", WHITE, "cinnamoroll_plush"),
                                 ("PURIN", "purin", YEL, "purin_plush")]:
        side_c = {"KITTY": WHITE, "MELODY": (255, 178, 205), "KUROMI": (44, 36, 56),
                  "CINNAMOROLL": WHITE, "PURIN": YEL}[key]
        noise_tile(side_c, 0.03, sum(ord(ch) for ch in plush)).save("%s/%s_side.png" % (d, plush))
        img = noise_tile(fur, 0.02, 11)
        dr = ImageDraw.Draw(img)
        if key == "KITTY":
            dr.polygon([(1, 5), (1, 0), (5, 3)], fill=WHITE, outline=(215, 215, 220))
            dr.polygon([(14, 5), (14, 0), (10, 3)], fill=WHITE, outline=(215, 215, 220))
            dr.rectangle((11, 1, 14, 3), fill=RED); dr.point((12, 2), fill=DRED)
            dr.ellipse((3, 7, 5, 10), fill=EYE); dr.ellipse((10, 7, 12, 10), fill=EYE)
            dr.rectangle((7, 10, 8, 11), fill=(255, 200, 40))
            for y in (9, 11):
                dr.line((0, y, 2, y), fill=EYE); dr.line((13, y, 15, y), fill=EYE)
        elif key == "MELODY":
            dr.rectangle((0, 0, 15, 6), fill=(255, 158, 196))
            dr.rectangle((0, 6, 15, 6), fill=WHITE)
            dr.ellipse((11, 2, 14, 5), fill=(255, 90, 130)); dr.point((12, 3), fill=(255, 230, 90))
            dr.ellipse((3, 8, 5, 11), fill=EYE); dr.ellipse((10, 8, 12, 11), fill=EYE)
            dr.rectangle((7, 11, 8, 12), fill=(255, 190, 90))
            dr.rectangle((2, 12, 4, 13), fill=(255, 170, 190)); dr.rectangle((11, 12, 13, 13), fill=(255, 170, 190))
        elif key == "KUROMI":
            dr.rectangle((0, 0, 15, 7), fill=BLACK)
            dr.rectangle((0, 7, 15, 7), fill=PURPLE)
            dr.ellipse((6, 1, 9, 5), fill=HOT); dr.point((7, 3), fill=EYE); dr.point((8, 3), fill=EYE)
            dr.ellipse((3, 9, 5, 12), fill=EYE); dr.ellipse((10, 9, 12, 12), fill=EYE)
            dr.rectangle((7, 12, 8, 13), fill=(255, 150, 190))
        elif key == "CINNAMOROLL":
            dr.rectangle((0, 3, 2, 13), fill=(225, 240, 255)); dr.rectangle((13, 3, 15, 13), fill=(225, 240, 255))
            dr.ellipse((3, 6, 5, 9), fill=(70, 140, 230)); dr.ellipse((10, 6, 12, 9), fill=(70, 140, 230))
            dr.point((3, 6), fill=(255, 255, 255)); dr.point((10, 6), fill=(255, 255, 255))
            dr.rectangle((7, 9, 8, 10), fill=EYE)
            dr.rectangle((3, 11, 5, 12), fill=(255, 175, 195)); dr.rectangle((10, 11, 12, 12), fill=(255, 175, 195))
        elif key == "PURIN":
            dr.ellipse((2, 0, 13, 4), fill=BROWN); dr.point((12, 0), fill=DBROWN)
            dr.rectangle((0, 4, 2, 9), fill=BROWN); dr.rectangle((13, 4, 15, 9), fill=BROWN)
            dr.ellipse((4, 6, 6, 9), fill=EYE); dr.ellipse((9, 6, 11, 9), fill=EYE)
            dr.rectangle((7, 9, 8, 10), fill=DBROWN)
            dr.line((6, 12, 9, 12), fill=DBROWN)
        img.save("%s/%s_front.png" % (d, plush))


# ============================================================ ÍTEMS (16x16)

def item_textures():
    d = OUT + "/item"
    os.makedirs(d, exist_ok=True)

    def new():
        img = Image.new("RGBA", (16, 16), (0, 0, 0, 0))
        return img, ImageDraw.Draw(img)

    # tarta de manzana
    img, dr = new()
    dr.ellipse((0, 8, 15, 15), fill=(232, 232, 238))
    dr.ellipse((1, 4, 14, 12), fill=(212, 148, 78))
    dr.ellipse((3, 5, 12, 10), fill=(226, 168, 96))
    for (a, b, c, e) in [(3, 6, 10, 9), (5, 5, 12, 8), (3, 8, 8, 5)]:
        dr.line((a, b, c, e), fill=(165, 100, 45))
    dr.point((7, 7), fill=RED); dr.point((8, 7), fill=RED)
    img.save(d + "/kitty_apple_pie.png")

    # bizcocho de almendra
    img, dr = new()
    dr.rectangle((1, 13, 14, 14), fill=(232, 232, 238))
    dr.polygon([(2, 12), (13, 12), (13, 6), (2, 6)], fill=(246, 224, 165))
    dr.rectangle((2, 4, 13, 7), fill=(255, 165, 200))
    for (x, y) in [(4, 9), (7, 10), (10, 9), (5, 11), (11, 11)]:
        dr.point((x, y), fill=(205, 165, 105))
    dr.point((4, 4), fill=(255, 220, 235)); dr.point((9, 5), fill=(255, 220, 235))
    img.save(d + "/melody_almond_cake.png")

    # cupcake de Kuromi
    img, dr = new()
    dr.polygon([(4, 9), (12, 9), (11, 14), (5, 14)], fill=(118, 58, 170))
    for x in (6, 8, 10):
        dr.line((x, 9, x, 13), fill=(90, 40, 140))
    dr.ellipse((3, 4, 13, 10), fill=(58, 30, 84))
    dr.ellipse((5, 2, 11, 6), fill=(88, 48, 118))
    dr.rectangle((7, 6, 8, 7), fill=HOT); dr.point((7, 7), fill=EYE); dr.point((8, 7), fill=EYE)
    dr.point((8, 1), fill=HOT)
    img.save(d + "/kuromi_cupcake.png")

    # rollito de canela
    img, dr = new()
    dr.ellipse((1, 2, 14, 14), fill=(205, 140, 75))
    dr.ellipse((3, 4, 12, 12), outline=(150, 92, 42))
    dr.ellipse((5, 6, 10, 10), outline=(150, 92, 42))
    dr.rectangle((7, 7, 8, 8), fill=(150, 92, 42))
    for (x, y) in [(3, 3), (6, 2), (10, 3), (12, 6), (2, 7), (5, 12), (10, 12)]:
        dr.point((x, y), fill=(170, 218, 255))
    img.save(d + "/cinnamoroll_roll.png")

    # pudín de Purin
    img, dr = new()
    dr.ellipse((0, 12, 15, 15), fill=(232, 232, 238))
    dr.polygon([(4, 13), (12, 13), (13, 6), (3, 6)], fill=(250, 208, 70))
    dr.polygon([(3, 6), (13, 6), (12, 3), (4, 3)], fill=(150, 85, 30))
    for (x, y) in [(4, 7), (7, 8), (11, 7)]:
        dr.point((x, y), fill=(150, 85, 30))
    dr.ellipse((7, 0, 9, 2), fill=RED)
    img.save(d + "/purin_pudding.png")

    GOLD = (230, 190, 70)

    def chain(dr):
        dr.line((8, 0, 4, 4), fill=GOLD); dr.line((8, 0, 12, 4), fill=GOLD)
        dr.point((4, 5), fill=GOLD); dr.point((12, 5), fill=GOLD)

    # lazo de Hello Kitty
    img, dr = new(); chain(dr)
    dr.polygon([(2, 7), (8, 10), (2, 14)], fill=RED, outline=DRED)
    dr.polygon([(14, 7), (8, 10), (14, 14)], fill=RED, outline=DRED)
    dr.rectangle((7, 9, 9, 11), fill=DRED)
    img.save(d + "/kitty_ribbon.png")

    # amuleto de Melody (corazón rosa)
    img, dr = new(); chain(dr)
    dr.ellipse((2, 6, 8, 12), fill=(255, 150, 190)); dr.ellipse((8, 6, 14, 12), fill=(255, 150, 190))
    dr.polygon([(2, 10), (14, 10), (8, 15)], fill=(255, 150, 190))
    dr.point((4, 8), fill=(255, 235, 245)); dr.point((5, 8), fill=(255, 235, 245))
    img.save(d + "/melody_hood_charm.png")

    # amuleto de Kuromi (calavera)
    img, dr = new(); chain(dr)
    dr.ellipse((2, 6, 13, 15), fill=(38, 30, 50), outline=PURPLE)
    dr.ellipse((5, 8, 10, 12), fill=HOT); dr.rectangle((6, 11, 9, 13), fill=HOT)
    dr.point((6, 9), fill=EYE); dr.point((9, 9), fill=EYE)
    img.save(d + "/kuromi_skull_charm.png")

    # amuleto de Cinnamoroll (nube)
    img, dr = new(); chain(dr)
    dr.ellipse((1, 9, 8, 14), fill=(196, 228, 255)); dr.ellipse((5, 6, 12, 13), fill=(220, 238, 255))
    dr.ellipse((8, 9, 15, 14), fill=(196, 228, 255))
    dr.point((8, 8), fill=WHITE); dr.point((9, 8), fill=WHITE)
    img.save(d + "/cinnamoroll_cloud_charm.png")

    # amuleto de Purin (boina)
    img, dr = new(); chain(dr)
    dr.ellipse((3, 7, 13, 15), fill=YEL)
    dr.ellipse((2, 5, 12, 9), fill=BROWN); dr.point((7, 4), fill=DBROWN)
    dr.point((6, 11), fill=EYE); dr.point((10, 11), fill=EYE)
    img.save(d + "/purin_beret_charm.png")


def generate_all():
    for key, cid in [("KITTY", "hello_kitty"), ("MELODY", "my_melody"), ("KUROMI", "kuromi"),
                     ("CINNAMOROLL", "cinnamoroll"), ("PURIN", "purin")]:
        entity(key, cid)
    block_textures()
    item_textures()


if __name__ == "__main__":
    generate_all()
    print("texturas generadas")
