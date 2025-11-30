package com.example.app.application.service;

import com.example.app.domain.model.Cliente;
import com.example.app.domain.port.out.ClienteRepository;

import java.util.List;
import java.util.Optional;

public class ClienteService {
    private final ClienteRepository clienteRepository;

    public ClienteService(ClienteRepository clienteRepository) {
        this.clienteRepository = clienteRepository;
    }

    public Cliente crearCliente(Cliente c) {
        if (c.getDocumento() != null && clienteRepository.findByDocumento(c.getDocumento()).isPresent()) {
            throw new IllegalArgumentException("Documento ya registrado");
        }
        return clienteRepository.save(c);
    }

    public Cliente actualizarCliente(Cliente c) {
        if (c.getId() == null) throw new IllegalArgumentException("ID requerido para actualizar");
        return clienteRepository.update(c);
    }

    public Optional<Cliente> obtenerPorId(Integer id) {
        return clienteRepository.findById(id);
    }

    public List<Cliente> listarClientes() {
        return clienteRepository.findAll();
    }

    public void eliminarCliente(Integer id) {
        clienteRepository.deleteById(id);
    }
}
