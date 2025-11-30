package com.example.app.infrastructure.jdbc;

import com.example.app.domain.model.Cliente;
import com.example.app.domain.port.out.ClienteRepository;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Properties;
import java.io.InputStream;
import java.io.IOException;

/**
 * Implementación JDBC de ClienteRepository.
 * Lee la configuración desde src/main/resources/config.properties:
 *
 * db.url=jdbc:mysql://localhost:3306/proyecto_final?serverTimezone=UTC
 * db.user=root
 * db.password=
 *
 * Coloca ese archivo exactamente en: src/main/resources/config.properties
 */
public class ClienteRepositoryJdbc implements ClienteRepository {

    private final String url;
    private final String user;
    private final String password;

    public ClienteRepositoryJdbc() {
        Properties prop = loadConfig();
        this.url = prop.getProperty("db.url");
        this.user = prop.getProperty("db.user");
        this.password = prop.getProperty("db.password");
        if (this.url == null || this.user == null) {
            throw new IllegalStateException("Properties db.url y db.user deben estar definidos en config.properties");
        }
    }

    private Properties loadConfig() {
        Properties prop = new Properties();
        try (InputStream in = getClass().getClassLoader().getResourceAsStream("config.properties")) {
            if (in == null) {
                throw new RuntimeException("Archivo config.properties no encontrado en classpath. " +
                        "Crea src/main/resources/config.properties con las propiedades db.url, db.user y db.password.");
            }
            prop.load(in);
            return prop;
        } catch (IOException e) {
            throw new RuntimeException("Error leyendo config.properties", e);
        }
    }

    private Connection getConnection() throws SQLException {
        // DriverManager usará el driver incluido por Maven (mysql-connector-j).
        return DriverManager.getConnection(url, user, password);
    }

    @Override
    public Cliente save(Cliente cliente) {
        String sql = "INSERT INTO clientes (nombre, documento, correo, telefono) VALUES (?, ?, ?, ?)";
        try (Connection c = getConnection();
             PreparedStatement ps = c.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, cliente.getNombre());
            ps.setString(2, cliente.getDocumento());
            ps.setString(3, cliente.getCorreo());
            ps.setString(4, cliente.getTelefono());

            int affected = ps.executeUpdate();
            if (affected == 0) {
                throw new RuntimeException("Insert falló, ninguna fila afectada.");
            }

            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    cliente.setId(rs.getInt(1));
                }
            }
            return cliente;
        } catch (SQLException e) {
            throw new RuntimeException("Error al insertar cliente: " + e.getMessage(), e);
        }
    }

    @Override
    public Cliente update(Cliente cliente) {
        if (cliente.getId() == null) throw new IllegalArgumentException("ID requerido para update");
        String sql = "UPDATE clientes SET nombre = ?, documento = ?, correo = ?, telefono = ? WHERE id = ?";
        try (Connection c = getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setString(1, cliente.getNombre());
            ps.setString(2, cliente.getDocumento());
            ps.setString(3, cliente.getCorreo());
            ps.setString(4, cliente.getTelefono());
            ps.setInt(5, cliente.getId());

            int affected = ps.executeUpdate();
            if (affected == 0) {
                throw new RuntimeException("Update no afectó filas (cliente id=" + cliente.getId() + " no existe?).");
            }
            return cliente;
        } catch (SQLException e) {
            throw new RuntimeException("Error al actualizar cliente: " + e.getMessage(), e);
        }
    }

    @Override
    public Optional<Cliente> findById(Integer id) {
        String sql = "SELECT id, nombre, documento, correo, telefono FROM clientes WHERE id = ?";
        try (Connection c = getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Cliente cli = new Cliente(
                            rs.getInt("id"),
                            rs.getString("nombre"),
                            rs.getString("documento"),
                            rs.getString("correo"),
                            rs.getString("telefono")
                    );
                    return Optional.of(cli);
                }
            }
            return Optional.empty();
        } catch (SQLException e) {
            throw new RuntimeException("Error al buscar cliente por id: " + e.getMessage(), e);
        }
    }

    @Override
    public List<Cliente> findAll() {
        String sql = "SELECT id, nombre, documento, correo, telefono FROM clientes ORDER BY creado_en DESC";
        List<Cliente> list = new ArrayList<>();
        try (Connection c = getConnection();
             PreparedStatement ps = c.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                Cliente cli = new Cliente(
                        rs.getInt("id"),
                        rs.getString("nombre"),
                        rs.getString("documento"),
                        rs.getString("correo"),
                        rs.getString("telefono")
                );
                list.add(cli);
            }
            return list;
        } catch (SQLException e) {
            throw new RuntimeException("Error al listar clientes: " + e.getMessage(), e);
        }
    }

    @Override
    public void deleteById(Integer id) {
        String sql = "DELETE FROM clientes WHERE id = ?";
        try (Connection c = getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setInt(1, id);
            int affected = ps.executeUpdate();
            if (affected == 0) {
                // no lanzar excepción si ya no existe, solo informar
                throw new RuntimeException("Eliminar no afectó filas (id=" + id + " posiblemente no existe).");
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error al eliminar cliente: " + e.getMessage(), e);
        }
    }

    @Override
    public Optional<Cliente> findByDocumento(String documento) {
        String sql = "SELECT id, nombre, documento, correo, telefono FROM clientes WHERE documento = ?";
        try (Connection c = getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setString(1, documento);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Cliente cli = new Cliente(
                            rs.getInt("id"),
                            rs.getString("nombre"),
                            rs.getString("documento"),
                            rs.getString("correo"),
                            rs.getString("telefono")
                    );
                    return Optional.of(cli);
                }
            }
            return Optional.empty();
        } catch (SQLException e) {
            throw new RuntimeException("Error al buscar cliente por documento: " + e.getMessage(), e);
        }
    }
}