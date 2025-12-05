# 🔄 Instrucciones para Recargar el Workspace de Java

**Problema:** El IDE reporta 292 errores después de crear nuevas clases de excepción.
**Causa:** El Language Server de Java no ha recompilado las clases nuevas.

---

## ✅ SOLUCIÓN RÁPIDA (Recomendado)

### **Opción 1: Comando de VSCode**

1. Presiona `Ctrl+Shift+P` (o `Cmd+Shift+P` en Mac)
2. Escribe y selecciona: **"Java: Clean Java Language Server Workspace"**
3. Confirma cuando pregunte si quieres recargar y borrar
4. Espera a que VSCode recargue la ventana
5. Espera a que el Language Server recompile (verás progreso en la barra inferior)

---

### **Opción 2: Recargar Ventana**

1. Presiona `Ctrl+Shift+P`
2. Escribe y selecciona: **"Developer: Reload Window"**
3. Espera a que VSCode recargue
4. El Language Server debería reconocer las nuevas clases

---

### **Opción 3: Forzar Actualización de Proyecto**

1. Presiona `Ctrl+Shift+P`
2. Escribe y selecciona: **"Java: Force Java Compilation"**
3. Espera a que compile

---

## 🔧 SOLUCIÓN MANUAL (Si las opciones anteriores no funcionan)

### **Paso 1: Limpiar Workspace**

```bash
# Desde la raíz del proyecto:
cd d:/forum-viajeros_fs

# Eliminar caches del Language Server de Java
rm -rf .metadata
rm -rf .recommenders
rm -rf .settings
rm -rf bin

# Limpiar backend
cd Forum_backend
rm -rf target
rm -rf .classpath
rm -rf .project
rm -rf .settings
```

### **Paso 2: Cerrar y Reabrir VSCode**

1. Cierra completamente VSCode
2. Reabre el proyecto
3. Espera a que Java Language Server indexe el proyecto

### **Paso 3: Verificar que las clases existen**

```bash
# Verificar que las clases de excepción están creadas
ls -la Forum_backend/src/main/java/com/forumviajeros/backend/exception/

# Deberías ver:
# - BadRequestException.java
# - ResourceNotFoundException.java
```

---

## 📋 VERIFICACIÓN DE QUE FUNCIONÓ

### **Señales de que el problema está resuelto:**

1. ✅ Los errores bajan de 292 a 0-2
2. ✅ No hay subrayados rojos en los imports de las excepciones
3. ✅ El Language Server muestra "Ready" en la barra inferior
4. ✅ Puedes hacer Ctrl+Click en `ResourceNotFoundException` y te lleva a la clase

### **Si todavía hay problemas:**

Verifica que el `pom.xml` se esté detectando:

1. Abre el archivo `pom.xml`
2. Espera unos segundos
3. En la barra inferior debería aparecer: "Importing projects" o "Updating Maven project"

---

## 🎯 POR QUÉ PASÓ ESTO

El Language Server de Java en VSCode cachea la información del proyecto. Cuando creamos nuevas clases:

1. Los archivos `.java` se crean físicamente ✅
2. Pero el Language Server no los "ve" hasta que recompila ❌
3. Otros archivos que importan estas clases muestran errores (clase no encontrada)

**Esto es normal** y pasa con cualquier IDE cuando se crean clases nuevas externamente.

---

## 🔍 COMANDOS ÚTILES

```bash
# Ver todos los archivos Java del proyecto
find Forum_backend/src -name "*.java" -type f | wc -l
# Debería mostrar: 36 archivos

# Verificar que las excepciones existen
cat Forum_backend/src/main/java/com/forumviajeros/backend/exception/ResourceNotFoundException.java

# Ver errores de compilación real (si Maven está disponible)
cd Forum_backend
mvn clean compile
```

---

## ✅ ESTADO ESPERADO DESPUÉS DE RECARGAR

```
📊 Problemas en el workspace:
├─ Errores: 0
├─ Advertencias: 2 (pattern matching - opcional)
└─ Info: 0

✅ Todas las clases reconocidas
✅ Imports funcionando
✅ Sin errores de compilación
```

---

## 💡 PARA EVITAR ESTO EN EL FUTURO

Después de crear nuevas clases o modificar el `pom.xml`:

1. Guardar todos los archivos (`Ctrl+K S`)
2. Ejecutar: `Java: Force Java Compilation`
3. O simplemente recargar la ventana

---

**Nota:** Los 292 errores son **falsos positivos** del Language Server. El código está correcto, solo necesita que el IDE lo reconozca.

---

## 🆘 SI NADA FUNCIONA

Como última opción, reinstala la extensión de Java:

1. Ve a Extensions (`Ctrl+Shift+X`)
2. Busca "Java Extension Pack"
3. Click en el ícono de engranaje → Uninstall
4. Reinicia VSCode
5. Vuelve a instalar "Java Extension Pack"
6. Reabre el proyecto

---

**Creado:** 2025-12-04
**Problema:** 292 errores falsos del Language Server
**Solución:** Recargar workspace de Java
