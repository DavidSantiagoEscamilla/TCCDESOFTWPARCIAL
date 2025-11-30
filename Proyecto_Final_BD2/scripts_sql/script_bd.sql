-- Script de creación de la BD y tablas (Ejemplo para MySQL)
CREATE DATABASE IF NOT EXISTS proyecto_final;
USE proyecto_final;

-- Tabla ejemplo: clientes
CREATE TABLE IF NOT EXISTS clientes (
  id INT AUTO_INCREMENT PRIMARY KEY,
  nombre VARCHAR(100) NOT NULL,
  documento VARCHAR(50) UNIQUE NOT NULL,
  correo VARCHAR(100),
  telefono VARCHAR(50),
  creado_en TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Ejemplos CRUDL (Create, Read, Update, Delete, List)
-- Create
INSERT INTO clientes (nombre, documento, correo, telefono) VALUES ('Juan Perez', '123456', 'juan@example.com', '3001111111');

-- Read (by id)
SELECT * FROM clientes WHERE id = 1;

-- Update
UPDATE clientes SET correo = 'juan.perez@example.com' WHERE id = 1;

-- Delete
DELETE FROM clientes WHERE id = 1;

-- List (all)
SELECT * FROM clientes ORDER BY creado_en DESC;