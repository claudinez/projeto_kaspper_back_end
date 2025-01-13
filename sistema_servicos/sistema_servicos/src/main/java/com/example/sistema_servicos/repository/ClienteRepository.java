package com.example.sistema_servicos.repository;

import com.example.sistema_servicos.model.Cliente;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;



@Repository
public interface ClienteRepository extends JpaRepository<Cliente, Long> {
    // Métodos customizados podem ser adicionados aqui, se necessário
}
