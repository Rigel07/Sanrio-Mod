"""Regenera TODOS los recursos generados (texturas, modelos, idiomas, recetas, worldgen, estructuras y ModelSpecs.java).
Uso (desde la raíz del repositorio):  python3 tools/generate_all.py     (necesita Pillow: pip install pillow)
"""
import os
import sys

sys.path.insert(0, os.path.dirname(os.path.abspath(__file__)))
import parts, textures, data, structures

parts.write_model_specs("src/main/java/com/sanriofriends/client/ModelSpecs.java")
textures.generate_all()
data.generate_all()
structures.generate_all()
print("Todo generado.")
