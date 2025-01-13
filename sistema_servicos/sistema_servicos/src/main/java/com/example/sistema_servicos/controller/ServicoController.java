package com.example.sistema_servicos.controller;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.server.ResponseStatusException;

import com.example.sistema_servicos.model.Cliente;
import com.example.sistema_servicos.model.Servico;
import com.example.sistema_servicos.repository.ClienteRepository;
import com.example.sistema_servicos.repository.ServicoRepository;

@Controller
@RequestMapping("/servicos")
public class ServicoController {

    @Autowired
    private ServicoRepository servicoRepository;

    @Autowired
    private ClienteRepository clienteRepository;

    @GetMapping
    public String listarServicos(Model model) {
        model.addAttribute("servicos", servicoRepository.findAll());
        return "servicos/lista";
    }

    @GetMapping("/novo")
    public String novoServico(@RequestParam Long clienteId, Model model) {
        Cliente cliente = clienteRepository.findById(clienteId)
                .orElseThrow(() -> new IllegalArgumentException("Cliente não encontrado: " + clienteId));
        Servico servico = new Servico();
        servico.setCliente(cliente);

        model.addAttribute("servico", servico);
        return "servicos/formulario";
    }

    @PostMapping("/salvar")
    public String salvarServico(@ModelAttribute Servico servico) {
        servicoRepository.save(servico);
        return "redirect:/servicos";
    }
    
    @GetMapping("/editar/{id}")
    public String editarServico(@PathVariable("id") Long id, Model model) {
        Optional<Servico> servicoOpt = servicoRepository.findById(id);  // Usando o servicoRepository para buscar pelo ID
        if (servicoOpt.isPresent()) {
            Servico servico = servicoOpt.get();
            model.addAttribute("servico", servico);
            model.addAttribute("cliente", servico.getCliente());  // Passando o cliente associado ao serviço
            return "servicos/formulario"; // Nome do template HTML de edição, certifique-se de que o arquivo editar-servico.html existe
        } else {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Serviço não encontrado");
        }
    }
    
    @GetMapping("/deletar/{id}")
    public String deletarServico(@PathVariable("id") Long id) {
        Optional<Servico> servicoOpt = servicoRepository.findById(id); // Procurando o serviço pelo ID
        if (servicoOpt.isPresent()) {
            Servico servico = servicoOpt.get();
            servicoRepository.delete(servico); // Deletando o serviço
            return "redirect:/servicos"; // Redireciona para a lista de serviços após a exclusão
        } else {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Serviço não encontrado");
        }
    }
}
