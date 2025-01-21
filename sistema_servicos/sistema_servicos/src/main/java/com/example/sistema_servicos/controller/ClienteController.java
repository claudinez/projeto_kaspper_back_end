package com.example.sistema_servicos.controller;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.sistema_servicos.model.Cliente;
import com.example.sistema_servicos.repository.ClienteRepository;


@Controller
@RequestMapping("/clientes")
public class ClienteController {

    @Autowired
    private ClienteRepository clienteRepository;

    @GetMapping("/novo")
    public String novoCliente(Model model) {
        model.addAttribute("cliente", new Cliente());
        return "clientes/formulario";
    } 

    @PostMapping("/salvar")
    public String salvarCliente(@ModelAttribute Cliente cliente, RedirectAttributes redirectAttributes) {
        Cliente clienteSalvo = clienteRepository.save(cliente);
        redirectAttributes.addFlashAttribute("cliente", clienteSalvo);
        return "redirect:/servicos/novo?clienteId=" + clienteSalvo.getId();
    }
    
    @GetMapping("/lista")
    public String listarClientes(Model model) {
        List<Cliente> clientes = clienteRepository.findAll(); // Busca clientes no banco
        model.addAttribute("clientes", clientes);
        return "clientes/lista"; // Deve existir um arquivo lista.html em templates/clientes/
    }
    
    @GetMapping("/editar/{id}")
    public String editarCliente(@PathVariable("id") Long id, Model model, RedirectAttributes redirectAttributes) {
        // Busca o cliente no banco
        Cliente cliente = clienteRepository.findById(id).orElse(null);
        
        if (cliente == null) {
            redirectAttributes.addFlashAttribute("erro", "Cliente não encontrado!");
            return "redirect:/clientes/lista";
        }
        
        model.addAttribute("cliente", cliente);
        return "clientes/formulario"; // O Thymeleaf deve ter um arquivo formulario.html
    }
}
