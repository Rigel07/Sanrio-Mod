# Sanrio Friends

Mod de **Minecraft Forge 1.20.1** (Forge 47.3.0+, Java 17) hecho por fans.
**Hello Kitty, My Melody, Kuromi, Cinnamoroll y Purin** viven en casitas por el mundo. Habla con ellos, completa sus misiones, hazte su amigo y pídeles que te sigan.

> Proyecto **no oficial y sin ánimo de lucro**. Los personajes pertenecen a Sanrio Co., Ltd. Las texturas y los modelos son pixel art propio; no se incluye ningún material oficial.

## Los personajes y sus casas

| Personaje | Estructura | Dónde aparece | Peculiaridad |
|---|---|---|---|
| **Hello Kitty** | Casita rosa y blanca con un lazo gigante sobre la puerta, jardín de flores y valla | Casi cualquier bioma de tierra (la más frecuente) | |
| **My Melody** | Casa-seta rosa con puntitos blancos | Bosques, pradera, cerezos, llanuras | |
| **Kuromi** | Torre morada y negra con calaveras rosas | Bosque oscuro, taiga, badlands, montañas, desierto, nieve | Torre de dos plantas con escalera de mano |
| **Cinnamoroll** | Café azul y blanco con tejado de nubes | Llanuras, pradera, nieve, sabana, cerezos | **Vuela** con sus orejas |
| **Purin** | Casa con forma de flan con caramelo y cereza | Llanuras, pradera, sabana, desierto, badlands | |

Cada casa tiene cama, peluche, cofre con botín (dulces, esmeraldas, lámparas...), mesa con tarta y su personaje dentro. Los personajes **no se alejan de su casa** y son **inofensivos e inmortales**.

## Cómo se juega

1. Encuentra una casa y haz **clic derecho** a su dueño. Hablas con él en el chat y **eliges entre 3 opciones** (clic en el texto amarillo):
   - **Charlar un rato**: te cuenta cosas y, una vez al día, **te regala objetos de su personaje**.
   - **Misiones** (o «¿Necesitas ayuda con algo?»): te encarga cosas (recoger objetos o derrotar monstruos).
   - **¿Puedo quedarme en tu casa?**: te deja descansar: cura vida y hambre, **mueve tu punto de reaparición a su casa** y, si es de noche y estás solo, **pasa a la mañana**. De noche o si estás herido, **te lo ofrece él mismo**.
2. Al **completar la primera misión sois amigos**. Entonces aparece «**Pedirle algo...**» con: **¡Sígueme!**, **Quédate aquí quieto** y descansar en su casa.
3. Las misiones se repiten en ciclos de 3 con cantidades cada vez mayores. Las recompensas mejoran: dulces y esmeraldas → **peluche** → **amuleto** → lámparas estrella.
4. `/sanrio amigos` muestra tu amistad y misión activa con cada personaje.

## Objetos y bloques

- **Dulces** (uno por personaje, dan efectos): tarta de manzana (Suerte), bizcocho de almendra (Regeneración), cupcake de Kuromi (Fuerza), rollito de canela (Velocidad), pudín de Purin (Absorción). También se fabrican.
- **Amuletos** (uno por personaje): efecto pasivo mientras están en la barra rápida. Se consiguen como recompensa.
- **Peluches** (5, decorativos y fabricables con 8 lanas + un tinte), **bloque corazón rosa**, **bloque nube**, **bloque de pudín**, **calavera de Kuromi** y **lámpara estrella** (luz).
- **Huevos de aparición** de los cinco, en la pestaña creativa *Sanrio Friends*.

## Probarlo rápido (creativo / con trucos)

```text
/place structure sanriofriends:hello_kitty_house
/place structure sanriofriends:my_melody_house
/place structure sanriofriends:kuromi_tower
/place structure sanriofriends:cinnamoroll_cafe
/place structure sanriofriends:purin_pudding_house
/locate structure sanriofriends:hello_kitty_house
```

## Compilar

Con GitHub Actions: pestaña **Actions** → último *Build* → artefacto `sanrio-friends` → contiene el `.jar`. En local: JDK 17 y `gradle build`.

## Regenerar los recursos (opcional)

Las texturas, modelos, idiomas, recetas, datos de worldgen y las **estructuras (.nbt)** se generan con scripts de `tools/` (necesitan Python 3 y `pip install pillow`):

```bash
python3 tools/generate_all.py
```

Para cambiar un modelo (orejas, lazos...) edita `tools/parts.py`; para cambiar una casa, `tools/structures.py`. También puedes sustituir cualquier textura por tu propia versión editando los `.png` de `src/main/resources/assets/sanriofriends/textures/`.

## Licencia

Código bajo licencia MIT. Los personajes y marcas pertenecen a sus propietarios (Sanrio Co., Ltd.).
