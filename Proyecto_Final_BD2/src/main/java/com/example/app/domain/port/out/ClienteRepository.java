package com.example.app.domain.port.out;

import com.example.app.domain.model.Cliente;
import java.util.List;
import java.util.Optional;

public interface ClienteRepository {
    Cliente save(Cliente cliente);
    Optional<Cliente> findById(Integer id);
    List<Cliente> findAll();
    void deleteById(Integer id);
    Optional<Cliente> findByDocumento(String documento);
    Cliente update(Cliente cliente);   // agrega método update
}
