"""Tabla de partes de los modelos. De aquí salen tanto ModelSpecs.java como las texturas, así no se desincronizan."""
import os

# Partes base (iguales para todos). (u, v, w, h, d)
BASE = {
    "head":      (0, 0, 10, 8, 8),
    "body":      (0, 16, 6, 6, 4),
    "right_arm": (20, 16, 2, 5, 2),
    "left_arm":  (28, 16, 2, 5, 2),
    "right_leg": (0, 26, 3, 4, 3),
    "left_leg":  (12, 26, 3, 4, 3),
}

# UVs de las partes extra (fijas por nombre)
UV = {"ear_l": (36, 0), "ear_r": (36, 13), "acc1": (0, 36), "acc2": (0, 50), "tail": (42, 50)}

def part(name, parent, box, offset, rot=(0, 0, 0)):
    x, y, z, w, h, d = box
    return dict(name=name, parent=parent, u=UV[name][0], v=UV[name][1], x=x, y=y, z=z, w=w, h=h, d=d,
                px=offset[0], py=offset[1], pz=offset[2], rx=rot[0], ry=rot[1], rz=rot[2])

PARTS = {
    "KITTY": [
        part("ear_l", "head", (-1.5, -3, -0.5, 3, 3, 1), (3.5, -8, 0), (0, 0, 0.2)),
        part("ear_r", "head", (-1.5, -3, -0.5, 3, 3, 1), (-3.5, -8, 0), (0, 0, -0.2)),
        part("acc1", "head", (-2, -1, -0.5, 4, 2, 1), (3.2, -7.0, -4.3), (0, 0, -0.35)),   # lazo
        part("acc2", "head", (-1, -1, -0.5, 2, 2, 1), (3.2, -7.0, -4.8), (0, 0, -0.35)),   # nudo del lazo
        part("tail", "body", (-1, -1, 0, 2, 2, 2), (0, 4.5, 2)),
    ],
    "MELODY": [
        part("ear_l", "head", (-1.5, -9, -0.5, 3, 9, 1), (2.5, -8.5, 1.0), (0, 0, 0.12)),
        part("ear_r", "head", (-1.5, -9, -0.5, 3, 9, 1), (-2.5, -8.5, 1.0), (0, 0, -0.12)),
        part("acc1", "head", (-5.5, -8.5, -4.5, 11, 4, 9), (0, 0, 0)),                    # capucha
        part("acc2", "head", (-1.5, -1.5, -0.5, 3, 3, 1), (3.8, -7.0, -4.8)),             # flor
        part("tail", "body", (-1, -1, 0, 2, 2, 2), (0, 4.5, 2)),
    ],
    "KUROMI": [
        part("ear_l", "head", (-1.5, -8, -0.5, 3, 8, 1), (3, -8.5, 0), (0, 0, 0.35)),
        part("ear_r", "head", (-1.5, -8, -0.5, 3, 8, 1), (-3, -8.5, 0), (0, 0, -0.35)),
        part("acc1", "head", (-5.5, -8.5, -4.5, 11, 4, 9), (0, 0, 0)),                    # capucha
        part("acc2", "head", (-2, -1.5, -0.5, 4, 3, 1), (0, -6.6, -4.9)),                 # calavera rosa
        part("tail", "body", (-0.5, 0, 0, 1, 4, 1), (0, 4, 2.2), (0.6, 0, 0)),
    ],
    "CINNAMOROLL": [
        part("ear_l", "head", (-1, 0, -2, 2, 9, 4), (5, -5, 0), (0, 0, -0.2)),
        part("ear_r", "head", (-1, 0, -2, 2, 9, 4), (-5, -5, 0), (0, 0, 0.2)),
        part("acc1", "head", (-1, -2, -1, 2, 2, 2), (0, -8, -1.5)),                       # mechón
        part("acc2", "head", (-0.5, -0.5, -0.5, 1, 1, 1), (0, -4, 0)),                    # (oculto)
        part("tail", "body", (-1.5, -1.5, 0, 3, 3, 3), (0, 4, 2)),
    ],
    "PURIN": [
        part("ear_l", "head", (-1, 0, -1.5, 2, 4, 3), (5, -7, 0), (0, 0, -0.3)),
        part("ear_r", "head", (-1, 0, -1.5, 2, 4, 3), (-5, -7, 0), (0, 0, 0.3)),
        part("acc1", "head", (-3.5, -1, -3.5, 7, 1, 7), (-0.5, -8.3, 0), (0, 0, 0.15)),   # boina
        part("acc2", "head", (-0.5, -1, -0.5, 1, 1, 1), (-0.3, -9.2, 0)),                 # botoncito de la boina
        part("tail", "body", (-1, -1, 0, 2, 2, 2), (0, 4.5, 2)),
    ],
}

def dims(char, name):
    if name in BASE:
        return BASE[name]
    for p in PARTS[char]:
        if p["name"] == name:
            return (p["u"], p["v"], p["w"], p["h"], p["d"])
    raise KeyError(name)

def f(v):
    return ("%sF" % repr(float(v)))

def write_model_specs(path):
    lines = []
    lines.append("package com.sanriofriends.client;")
    lines.append("")
    lines.append("import com.sanriofriends.SanrioCharacter;")
    lines.append("")
    lines.append("/** GENERADO por tools/parts.py. No editar a mano. */")
    lines.append("public final class ModelSpecs {")
    lines.append("    public record Spec(String name, String parent, int u, int v, float x, float y, float z, int w, int h, int d,")
    lines.append("                       float px, float py, float pz, float rx, float ry, float rz) {}")
    lines.append("")
    lines.append("    private ModelSpecs() {}")
    lines.append("")
    lines.append("    public static Spec[] of(SanrioCharacter c) {")
    lines.append("        return switch (c) {")
    for ch, plist in PARTS.items():
        lines.append("            case %s -> new Spec[]{" % ch)
        for i, p in enumerate(plist):
            comma = "," if i < len(plist) - 1 else ""
            lines.append('                    new Spec("%s", "%s", %d, %d, %s, %s, %s, %d, %d, %d, %s, %s, %s, %s, %s, %s)%s' % (
                p["name"], p["parent"], p["u"], p["v"], f(p["x"]), f(p["y"]), f(p["z"]), p["w"], p["h"], p["d"],
                f(p["px"]), f(p["py"]), f(p["pz"]), f(p["rx"]), f(p["ry"]), f(p["rz"]), comma))
        lines.append("            };")
    lines.append("        };")
    lines.append("    }")
    lines.append("}")
    os.makedirs(os.path.dirname(path), exist_ok=True)
    open(path, "w", encoding="utf-8").write("\n".join(lines) + "\n")

if __name__ == "__main__":
    write_model_specs("src/main/java/com/sanriofriends/client/ModelSpecs.java")
    print("ModelSpecs.java generado")
