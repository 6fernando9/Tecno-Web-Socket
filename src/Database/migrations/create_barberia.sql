-- Migration: create_barberia.sql
-- Esquema: BARBERÍA
-- PostgreSQL compatible

-- Enums (usamos types para mayor claridad)
CREATE TYPE user_role AS ENUM ('propietario', 'barbero', 'secretaria', 'cliente');
CREATE TYPE movimiento_tipo AS ENUM ('ingreso', 'salida_venta', 'ajuste');
CREATE TYPE cita_estado AS ENUM ('pendiente_pago_adelanto', 'confirmada', 'completada', 'cancelada');
CREATE TYPE pago_estado AS ENUM ('pendiente_adelanto', 'pendiente_saldo', 'pagado');
CREATE TYPE metodo_pago AS ENUM ('contado', 'tarjeta', 'transferencia', 'credito_fiado');

-- Tabla Usuarios
CREATE TABLE IF NOT EXISTS Usuarios (
  id SERIAL PRIMARY KEY,
  nombre VARCHAR(255),
  apellido VARCHAR(255),
  email VARCHAR(320) UNIQUE NOT NULL,
  telefono VARCHAR(50),
  password_hash VARCHAR(255),
  rol user_role NOT NULL,
  fecha_registro TIMESTAMP WITH TIME ZONE DEFAULT now()
);

-- Horarios de barberos (recurrentes por día de semana)
CREATE TABLE IF NOT EXISTS Horarios_Barberos (
  id SERIAL PRIMARY KEY,
  barbero_id INT NOT NULL REFERENCES Usuarios(id) ON DELETE CASCADE,
  dia_semana INT NOT NULL,
  hora_inicio TIME,
  hora_fin TIME
);

-- Excepciones al horario (fechas concretas)
CREATE TABLE IF NOT EXISTS Excepciones_Horario (
  id SERIAL PRIMARY KEY,
  barbero_id INT NOT NULL REFERENCES Usuarios(id) ON DELETE CASCADE,
  fecha DATE NOT NULL,
  es_disponible BOOLEAN DEFAULT FALSE,
  hora_inicio TIME,
  hora_fin TIME
);

-- Servicios
CREATE TABLE IF NOT EXISTS Servicios (
  id SERIAL PRIMARY KEY,
  nombre VARCHAR(255),
  descripcion TEXT,
  precio DECIMAL(10,2),
  duracion_estimada_min INT
);

-- Productos
CREATE TABLE IF NOT EXISTS Productos (
  id SERIAL PRIMARY KEY,
  nombre VARCHAR(255),
  descripcion TEXT,
  precio_venta DECIMAL(10,2),
  stock_actual INT DEFAULT 0,
  stock_minimo INT DEFAULT 0
);

-- Movimientos de inventario
CREATE TABLE IF NOT EXISTS Movimientos_Inventario (
  id SERIAL PRIMARY KEY,
  producto_id INT NOT NULL REFERENCES Productos(id) ON DELETE RESTRICT,
  usuario_id INT NOT NULL REFERENCES Usuarios(id) ON DELETE SET NULL,
  tipo_movimiento movimiento_tipo,
  cantidad INT,
  fecha TIMESTAMP WITH TIME ZONE DEFAULT now(),
  motivo VARCHAR(512)
);

-- Citas
CREATE TABLE IF NOT EXISTS Citas (
  id SERIAL PRIMARY KEY,
  cliente_id INT NOT NULL REFERENCES Usuarios(id) ON DELETE CASCADE,
  barbero_id INT NOT NULL REFERENCES Usuarios(id) ON DELETE CASCADE,
  fecha_hora_inicio TIMESTAMP WITH TIME ZONE NOT NULL,
  fecha_hora_fin TIMESTAMP WITH TIME ZONE,
  pago_inicial NUMERIC(10,2),
  estado cita_estado NOT NULL
);

-- Pivote Cita <-> Servicios
CREATE TABLE IF NOT EXISTS Cita_Servicios (
  id SERIAL PRIMARY KEY,
  cita_id INT NOT NULL REFERENCES Citas(id) ON DELETE CASCADE,
  servicio_id INT NOT NULL REFERENCES Servicios(id) ON DELETE RESTRICT,
  precio_cobrado DECIMAL(10,2)
);

-- Ventas (pagos)
CREATE TABLE IF NOT EXISTS Ventas (
  id SERIAL PRIMARY KEY,
  cita_id INT REFERENCES Citas(id) ON DELETE SET NULL,
  cliente_id INT NOT NULL REFERENCES Usuarios(id) ON DELETE CASCADE,
  usuario_id INT NOT NULL REFERENCES Usuarios(id) ON DELETE SET NULL,
  fecha_hora TIMESTAMP WITH TIME ZONE DEFAULT now(),
  monto_total DECIMAL(10,2),
  estado_pago pago_estado NOT NULL
);

-- Pivote Venta <-> Productos
CREATE TABLE IF NOT EXISTS Venta_Productos (
  id SERIAL PRIMARY KEY,
  venta_id INT NOT NULL REFERENCES Ventas(id) ON DELETE CASCADE,
  producto_id INT NOT NULL REFERENCES Productos(id) ON DELETE RESTRICT,
  cantidad INT,
  precio_unitario_venta DECIMAL(10,2)
);

-- Detalle de pagos
CREATE TABLE IF NOT EXISTS Detalle_Pagos (
  id SERIAL PRIMARY KEY,
  venta_id INT NOT NULL REFERENCES Ventas(id) ON DELETE CASCADE,
  metodo_pago metodo_pago,
  monto DECIMAL(10,2)
);

-- Índices recomendados
CREATE INDEX IF NOT EXISTS idx_usuarios_email ON Usuarios(email);
CREATE INDEX IF NOT EXISTS idx_citas_cliente ON Citas(cliente_id);
CREATE INDEX IF NOT EXISTS idx_citas_barbero ON Citas(barbero_id);

-- Nota: ejecutar este script con una cuenta con privilegios suficientes para crear types y tablas.
