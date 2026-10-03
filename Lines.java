package com.sanriofriends.dialogue;

import com.sanriofriends.SanrioCharacter;

/** Frases de cada personaje. */
public final class Lines {
    private Lines() {}

    public static String[] greet(SanrioCharacter c) {
        return switch (c) {
            case KITTY -> new String[]{
                    "¡Hola! Soy Hello Kitty. ¡Qué alegría verte por aquí!",
                    "¡Oh, una visita! Pasa, pasa, mi casita siempre está lista para un amigo.",
                    "¡Hola, hola! Justo estaba pensando en hornear una tarta de manzana."};
            case MELODY -> new String[]{
                    "¡Hola, hola! Soy My Melody. ¡Qué ilusión que me visites!",
                    "¡Oh, qué sorpresa tan bonita! Bienvenido a mi casita del bosque.",
                    "¡Hola! Estaba recogiendo flores. ¿Quieres ayudarme?"};
            case KUROMI -> new String[]{
                    "¡Hmph! Soy Kuromi, la más rebelde de por aquí. ¿Qué miras?",
                    "¿Quién es? ¡Ah, eres tú! No es que me alegre de verte... ¡hmph!",
                    "¡Ja! Cuidado, que hoy estoy de humor travieso."};
            case CINNAMOROLL -> new String[]{
                    "¡Hola! Soy Cinnamoroll. ¡Bienvenido al Café Cinnamon!",
                    "¡Buenos días! Mis orejas me hacen volar, ¿lo sabías?",
                    "¡Oh, un cliente! Digo... ¡un amigo! Pasa, pasa."};
            case PURIN -> new String[]{
                    "¡Hola! Soy Purin. ¡Mmm, aquí huele a caramelo!",
                    "¡Oh, hola! Estaba echándome una siestecita al sol.",
                    "¡Hola, hola! ¿Has traído algo de comer? ¡Es broma! ...o no."};
        };
    }

    public static String[] greetFriend(SanrioCharacter c) {
        return switch (c) {
            case KITTY -> new String[]{
                    "¡Hola, amigo! Me encanta cuando vienes a verme.",
                    "¡Qué bien que has venido! Hoy es un día perfecto para estar juntos.",
                    "¡Mi amigo favorito! ¿Qué hacemos hoy?"};
            case MELODY -> new String[]{
                    "¡Mi querido amigo! ¡Qué alegría verte otra vez!",
                    "¡Oh, has venido! Prepararé algo rico para merendar.",
                    "¡Hola, hola! Contigo todo es más bonito."};
            case KUROMI -> new String[]{
                    "Vaya, otra vez tú... bueno, un poquito me alegra, ¿vale?",
                    "¡Eh, amigo! No se lo digas a nadie, pero eres de los buenos.",
                    "¿Vienes a hacer travesuras conmigo? ¡Me apunto!"};
            case CINNAMOROLL -> new String[]{
                    "¡Hola, amigo! ¡Qué contento estoy de verte!",
                    "¡Has vuelto! Voy a preparar algo dulce para los dos.",
                    "¡Hoy hace un día perfecto para volar contigo!"};
            case PURIN -> new String[]{
                    "¡Mi amigo! ¡Qué bien que vengas a verme!",
                    "¡Hola! Hoy es un día genial para comer pudín.",
                    "¡Contigo las siestas son todavía mejores!"};
        };
    }

    public static String[] chat(SanrioCharacter c) {
        return switch (c) {
            case KITTY -> new String[]{
                    "Mi comida favorita es la tarta de manzana. ¡Mmm!",
                    "Me encantan los lazos, pero los amigos todavía más.",
                    "Tengo una hermana gemela que se llama Mimmy. ¡Nos parecemos mucho!",
                    "Escribo en mi diario todas las cosas bonitas que me pasan."};
            case MELODY -> new String[]{
                    "Mi capucha rosa me la hizo mi abuela con mucho cariño.",
                    "Me encantan los pasteles de almendra... ¡y las flores!",
                    "Mi mejor amigo es Flat, un ratoncito muy simpático.",
                    "Kuromi dice que es mi rival, pero yo creo que en el fondo es buena persona."};
            case KUROMI -> new String[]{
                    "Yo no soy mala, ¡solo muy traviesa!",
                    "Mi compañero Baku siempre tiene hambre. ¡Come de todo!",
                    "Esa My Melody se cree muy perfecta. ¡Hmph!... aunque hace unas tartas buenísimas.",
                    "Me encanta el rosa y el negro. ¡Una combinación perfecta!"};
            case CINNAMOROLL -> new String[]{
                    "Nací en una nube y me encontró una chica muy amable llamada Chiffon.",
                    "Mis orejas son tan grandes que puedo volar con ellas.",
                    "A veces me da vergüenza hablar... ¡pero soy muy valiente!",
                    "En el café siempre huele a pan recién hecho. ¡Qué rico!"};
            case PURIN -> new String[]{
                    "Mi boina marrón no me la quito para nada.",
                    "Me encanta el pudín y echarme siestas al sol.",
                    "Mi mejor amigo es Muffin, un hámster muy simpático.",
                    "El caramelo es lo mejor del mundo. ¡No se discute!"};
        };
    }

    public static String thanks(SanrioCharacter c) {
        return switch (c) {
            case KITTY -> "¡Muchas gracias, eres un encanto!";
            case MELODY -> "¡Oh, qué detalle tan bonito! ¡Gracias, gracias!";
            case KUROMI -> "Hmph... bueno, gracias. No se lo cuentes a nadie.";
            case CINNAMOROLL -> "¡Gracias, gracias! ¡Eres muy amable!";
            case PURIN -> "¡Mmm, qué bien! ¡Muchas gracias!";
        };
    }

    public static String farewell(SanrioCharacter c) {
        return switch (c) {
            case KITTY -> "¡Vale! Vuelve cuando quieras, ¿sí?";
            case MELODY -> "¡Claro, sin prisa! Aquí estaré.";
            case KUROMI -> "Como quieras. Yo seguiré con mis cosas.";
            case CINNAMOROLL -> "¡De acuerdo! Aquí te espero.";
            case PURIN -> "¡Vale! Yo mientras me echo una siesta.";
        };
    }
}
