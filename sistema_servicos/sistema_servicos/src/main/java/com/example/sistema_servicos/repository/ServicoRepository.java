package com.example.sistema_servicos.repository;

import java.util.List;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.sistema_servicos.model.Servico;



@Repository
public interface ServicoRepository extends JpaRepository<Servico, Long> {
    // Métodos customizados podem ser adicionados aqui, se necessário
	List<Servico> findByClienteId(Long clienteId);
}
