# Evidencia de funcionamiento - SGPI

## 1. Prueba automatizada (21 verificaciones)

Comando (requiere `db.properties` configurado y `sql/sgpi.sql` ejecutado):

```powershell
java -cp "target/classes;$env:USERPROFILE\.m2\repository\com\mysql\mysql-connector-j\8.4.0\mysql-connector-j-8.4.0.jar" com.elahorro.sgpi.pruebas.PruebasSistema
```

Salida esperada:

```text
Conectado a: u000000000_sgpi
[OK]    RF-01 - Login correcto
[OK]    RF-01 - Login incorrecto rechazado
[OK]    RF-02 - Solo admin elimina usuarios
[OK]    RF-03 - Password cifrada (no texto plano)
[OK]    RF-04 - CRUD productos (registrar y buscar)
[OK]    RF-05 - Busqueda de productos
[OK]    RF-06 - Alerta de stock minimo
[OK]    RF-07 - Gestion de categorias
[OK]    RF-08 - DNI unico por cliente
[OK]    RF-09 - Busqueda de cliente por nombre
[OK]    RF-10 - Pedido requiere cliente (RN-03)
[OK]    RF-11 - Pedido con multiples detalles
[OK]    RF-12 - Validacion de stock (RN-02)
[OK]    RF-13 - Calculo de subtotales y total
[OK]    RF-14 - Descuento maximo 20% sin autorizacion
[OK]    RF-15 - Pago con monto igual al total
[OK]    RF-17 - Descuento automatico de stock al vender
[OK]    RF-16 - Cancelacion de pedido repone stock
[OK]    RF-18 - Reporte de stock bajo
[OK]    RF-19 - Reporte de ventas por fecha
[OK]    RF-20 - Persistencia (recarga desde MySQL)

========================================
RESULTADO: 21 correctas, 0 fallidas.
========================================
```

> **Advertencia:** el runner limpia (TRUNCATE) las tablas de negocio. Use una
> base de datos de practica, no produccion.

## 2. Pruebas unitarias JUnit

```powershell
mvn test
```

Archivo: `src/test/java/com/elahorro/sgpi/ModeloTest.java`.

## 3. Evidencia de persistencia en MySQL

Ejemplo de consultas y su resultado tras ejecutar las pruebas:

```sql
SELECT codigo, nombre, stock, stock_minimo FROM productos ORDER BY codigo;
-- T001 | Samsung Galaxy A54 | 8 | 3
-- T002 | Laptop HP 15      | 1 | 5
-- T003 | Audifonos JBL     | 5 | 2

SELECT id, fecha, estado, total, cliente_id, vendedor_id, pago_id FROM pedidos;
-- 1 | 2026-09-23 | PAGADO    | 5098.80 | 1 | 2 | 1
-- 2 | 2026-09-23 | CANCELADO |  599.70 | 1 | 2 | NULL

SELECT id, nombre, username, password_hash, rol FROM usuarios;
-- 1 | Administrador | admin     | 240be5...720a9 | ADMINISTRADOR
-- 2 | Vendedor Uno  | vendedor1 | 7e6e1f...41ab9 | VENDEDOR
```

La contrasena se almacena cifrada (SHA-256), nunca en texto plano.

## 4. Como ejecutar la aplicacion

```powershell
$conn = "$env:USERPROFILE\.m2\repository\com\mysql\mysql-connector-j\8.4.0\mysql-connector-j-8.4.0.jar"
javac -encoding UTF-8 -cp $conn -d target/classes (Get-ChildItem -Recurse -Filter *.java src/main/java).FullName
Copy-Item src/main/resources/* target/classes/
java -cp "target/classes;$conn" com.elahorro.sgpi.Main
```

Credenciales por defecto: **admin / admin123**.

## 5. Evidencia de ejecucion por consola

La aplicacion es 100% por consola (no usa interfaz grafica). Ejemplo de sesion:

```text
==================================================
      TIENDA DE TECNOLOGIA - EL AHORRO
  Sistema de Gestion de Pedidos e Inventario
                   SGPI
==================================================

--- INICIAR SESION ---
Usuario: admin
Contrasena: admin123

Bienvenido, Administrador (Administrador)

==================================================
  MENU PRINCIPAL - admin (Administrador)
==================================================
1. Productos
2. Categorias
3. Clientes
4. Pedidos
5. Reportes
6. Usuarios
0. Cerrar sesion / Salir
Opcion:
```

Submenus disponibles: Productos, Categorias, Clientes, Pedidos, Reportes y
Usuarios (solo administrador). Cada accion valida las reglas de negocio e
informa el resultado en pantalla.
