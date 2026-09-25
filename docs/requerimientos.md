# SGPI - Requerimientos prioritarios y criterios de aceptacion

Sistema de Gestion de Pedidos e Inventario para la tienda de tecnologia
**"El Ahorro"**.
Curso: Tecnicas de Programacion Orientada a Objetos - UPN.

Esta matriz documenta los **20 requerimientos funcionales prioritarios** de la
primera version funcional, su criterio de aceptacion y la evidencia de su
funcionamiento (prueba automatizada o interfaz).

Evidencia automatizada: clase `com.elahorro.sgpi.pruebas.PruebasSistema`
(ejecutable con `java -cp target/classes com.elahorro.sgpi.pruebas.PruebasSistema`).
Su salida se conserva en `docs/evidencia.md`.

| # | Requerimiento | Criterio de aceptacion | Evidencia |
|---|---------------|------------------------|-----------|
| RF-01 | Inicio de sesion con usuario y contrasena | Con credenciales validas se abre el menu; con invalidas se muestra error | Prueba `RF-01` + `LoginFrame` |
| RF-02 | Gestion de usuarios (solo Administrador) | Un usuario Vendedor no puede eliminar ni crear usuarios | Prueba `RF-02` + submenu Usuarios |
| RF-03 | Contrasena cifrada | La contrasena almacenada no coincide con el texto plano (SHA-256) | Prueba `RF-03` + `Cifrador` |
| RF-04 | CRUD de productos | El producto registrado se puede consultar y persiste en CSV | Prueba `RF-04` + submenu Productos |
| RF-05 | Busqueda de productos por nombre/codigo | La busqueda devuelve las coincidencias | Prueba `RF-05` |
| RF-06 | Alerta de stock minimo | Producto con `stock <= stockMinimo` aparece como alerta | Prueba `RF-06` |
| RF-07 | Gestion de categorias | Se registran categorias y se asignan a productos | Prueba `RF-07` + submenu Categorias |
| RF-08 | DNI unico por cliente | Un DNI repetido es rechazado | Prueba `RF-08` |
| RF-09 | Busqueda de clientes | Busqueda por nombre y por DNI | Prueba `RF-09` |
| RF-10 | Pedido con cliente obligatorio (RN-03) | No se crea un pedido sin cliente | Prueba `RF-10` |
| RF-11 | Pedido con multiples detalles | Un pedido admite 2 o mas productos | Prueba `RF-11` |
| RF-12 | Validacion de stock (RN-02) | No se permite vender mas stock del disponible | Prueba `RF-12` |
| RF-13 | Calculo de subtotales y total | `total = suma(cantidad * precio)` con redondeo a 2 decimales | Prueba `RF-13` |
| RF-14 | Descuento maximo 20% sin autorizacion (RN-06) | Un descuento mayor a 20% sin autorizacion es rechazado | Prueba `RF-14` |
| RF-15 | Registro de pago | El monto del pago debe ser igual al total del pedido | Prueba `RF-15` |
| RF-16 | Cancelacion de pedido | Al cancelar se repone el stock de los productos | Prueba `RF-16` |
| RF-17 | Descuento automatico de stock | Al agregar un producto al pedido, el stock disminuye | Prueba `RF-17` |
| RF-18 | Reporte de stock bajo | Lista los productos bajo el minimo y exporta CSV | Prueba `RF-18` + submenu Reportes |
| RF-19 | Reporte de ventas por fecha | Filtra pedidos PAGADOS por rango y suma el total | Prueba `RF-19` |
| RF-20 | Exportacion a CSV | Se generan archivos CSV legibles de stock bajo y ventas | Prueba `RF-20` |

## Reglas de negocio verificadas

| Codigo | Regla | Implementacion |
|--------|-------|----------------|
| RN-02 | No se registra pedido sin stock suficiente | `Producto.reducirStock()` / `Pedido.agregarDetalle()` |
| RN-03 | Todo pedido requiere cliente | Constructor de `Pedido` |
| RN-04 | Precio mayor a 0 | `Producto.setPrecio()` |
| RN-05 | Inmutabilidad del precio de venta | `DetallePedido.precioUnitario` final |
| RN-06 | Descuento maximo 20% sin autorizacion | `Pedido.setDescuento()` |
| RN-07 | Solo el Administrador elimina productos/usuarios | `ProductoService.eliminar()` / `UsuarioService.eliminar()` |

## Estructura de paquetes

```
com.elahorro.sgpi
├── Main.java              -> lanzador de la interfaz de consola
├── modelo/                -> clases del dominio + enums
├── repositorio/           -> persistencia en archivos CSV
├── servicio/              -> logica de negocio (Sistema, *Service)
├── vista/                 -> interfaz por consola
├── util/                  -> Validador, Cifrador, CsvUtil
└── pruebas/               -> PruebasSistema (evidencia)
```

## Persistencia (archivos CSV en `datos/`)

| Archivo | Contenido |
|---------|-----------|
| `categorias.csv` | id; nombre |
| `productos.csv` | id; codigo; nombre; precio; stock; stockMinimo; categoriaId |
| `clientes.csv` | id; nombre; dni; telefono; direccion |
| `usuarios.csv` | id; nombre; username; passwordHash; rol |
| `pedidos.csv` | id; fecha; estado; descuento; total; clienteDni; vendedorUsername; pagoId |
| `detalles.csv` | pedidoId; detalleId; productoId; cantidad; precioUnitario; subtotal |
| `pagos.csv` | id; fecha; monto; metodo |

## Credenciales por defecto

- Usuario: `admin` - Contrasena: `admin123` - Rol: Administrador
