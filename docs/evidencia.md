# Evidencia de funcionamiento - SGPI

## 1. Prueba automatizada (21 verificaciones)

Comando:

```powershell
java -cp target/classes com.elahorro.sgpi.pruebas.PruebasSistema
```

Salida:

```text
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
[OK]    RF-20 - Exportacion a CSV

========================================
RESULTADO: 21 correctas, 0 fallidas.
========================================
```

## 2. Pruebas unitarias JUnit (opcional)

Si dispone de Maven:

```powershell
mvn test
```

Archivo: `src/test/java/com/elahorro/sgpi/ModeloTest.java`.

## 3. Evidencia de persistencia en CSV (carpeta `datos/`)

`productos.csv`

```text
id;codigo;nombre;precio;stock;stockMinimo;categoriaId
1;T001;Samsung Galaxy A54;1299.90;8;3;1
2;T002;Laptop HP 15;2499.00;1;5;1
3;T003;Audifonos JBL;199.90;5;2;1
```

`pedidos.csv`

```text
id;fecha;estado;descuento;total;clienteDni;vendedorUsername;pagoId
1;2026-09-23;PAGADO;0.00;5098.80;12345678;vendedor1;1
2;2026-09-23;CANCELADO;0.00;599.70;12345678;vendedor1;
```

`detalles.csv`

```text
pedidoId;detalleId;productoId;cantidad;precioUnitario;subtotal
1;1;1;2;1299.90;2599.80
1;2;2;1;2499.00;2499.00
2;1;3;3;199.90;599.70
```

`usuarios.csv` (la contrasena se almacena cifrada, nunca en texto plano)

```text
id;nombre;username;passwordHash;rol
1;Administrador;admin;240be518fabd2724ddb6f04eeb1da5967448d7e831c08c8fa822809f74c720a9;ADMINISTRADOR
2;Vendedor Uno;vendedor1;7e6e1f7bd27296e97d8494fc51cb3d19355b26e062e7c09a163d954213f41ab9;VENDEDOR
```

## 4. Como ejecutar la aplicacion

```powershell
javac -encoding UTF-8 -d target/classes (Get-ChildItem -Recurse -Filter *.java src/main/java).FullName
java -cp target/classes com.elahorro.sgpi.Main
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

