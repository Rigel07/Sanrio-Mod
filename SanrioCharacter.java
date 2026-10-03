package com.sanriofriends;

/** Los cinco personajes y sus datos básicos. */
public enum SanrioCharacter {
    KITTY("hello_kitty", "Hello Kitty", 0xFF8FB8, 1.35F,
            "kitty_apple_pie", "kitty_ribbon", "kitty_plush", 0xFFFFFF, 0xE8334F,
            "Un lazo de la suerte: te da Suerte mientras lo llevas en la barra rápida."),
    MELODY("my_melody", "My Melody", 0xFFA6CF, 1.45F,
            "melody_almond_cake", "melody_hood_charm", "melody_plush", 0xFFB6D0, 0xFFFFFF,
            "Un amuleto con cariño: te da Regeneración mientras lo llevas en la barra rápida."),
    KUROMI("kuromi", "Kuromi", 0xB57BFF, 1.10F,
            "kuromi_cupcake", "kuromi_skull_charm", "kuromi_plush", 0x2A2433, 0xFF69B4,
            "Un amuleto rebelde: te da Fuerza mientras lo llevas en la barra rápida."),
    CINNAMOROLL("cinnamoroll", "Cinnamoroll", 0x8EC9FF, 1.40F,
            "cinnamoroll_roll", "cinnamoroll_cloud_charm", "cinnamoroll_plush", 0xFFFFFF, 0x8EC9FF,
            "Un amuleto de nube: te da Caída lenta mientras lo llevas en la barra rápida."),
    PURIN("purin", "Purin", 0xFFD84D, 1.00F,
            "purin_pudding", "purin_beret_charm", "purin_plush", 0xF7D263, 0x7D4E2D,
            "Un amuleto dulce: te da Absorción mientras lo llevas en la barra rápida.");

    public final String id;          // id de entidad y de textura
    public final String displayName;
    public final int color;          // color del chat
    public final float pitch;        // tono de su "voz"
    public final String foodId;
    public final String charmId;
    public final String plushId;
    public final int eggBg;
    public final int eggFg;
    public final String charmTip;

    SanrioCharacter(String id, String displayName, int color, float pitch, String foodId, String charmId,
                    String plushId, int eggBg, int eggFg, String charmTip) {
        this.id = id;
        this.displayName = displayName;
        this.color = color;
        this.pitch = pitch;
        this.foodId = foodId;
        this.charmId = charmId;
        this.plushId = plushId;
        this.eggBg = eggBg;
        this.eggFg = eggFg;
        this.charmTip = charmTip;
    }

    public static SanrioCharacter byId(String id) {
        for (SanrioCharacter c : values()) {
            if (c.id.equals(id)) return c;
        }
        return null;
    }
}
