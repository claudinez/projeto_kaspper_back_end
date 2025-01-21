package com.example.sistema_servicos.controller;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.propertyeditors.CustomNumberEditor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;
import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;


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
    
    @Autowired
    private EmailService emailService;  // Correção: Injeção do EmailService
    
    
    @InitBinder
    public void initBinder(WebDataBinder binder) {
        DecimalFormat decimalFormat = new DecimalFormat("#,##0.00", new DecimalFormatSymbols(new Locale("pt", "BR")));
        decimalFormat.setParseBigDecimal(true);
        binder.registerCustomEditor(BigDecimal.class, new CustomNumberEditor(BigDecimal.class, decimalFormat, true));
    }
       
    @GetMapping("/lista")
    public String listarServicos(Model model) {
    	 // Obter todos os serviços
        model.addAttribute("servicos", servicoRepository.findAll());
        
     // Retorna a view (a página HTML que exibirá os dados)
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
	    	calcularEAtualizarValorTotal(servico); // Garante que o valor total seja atualizado corretamente
	        servicoRepository.save(servico);
	        return "redirect:/servicos/lista";
	    }

	    private void calcularEAtualizarValorTotal(Servico servico) {
	        if (servico.getValorHora() != null && servico.getDiaTrabalhado() > 0 && servico.getHoraDia() > 0) {
	            BigDecimal valorTotal = servico.getValorHora()
	                .multiply(BigDecimal.valueOf(servico.getDiaTrabalhado()))
	                .multiply(BigDecimal.valueOf(servico.getHoraDia()));

	            servico.setValorTotalProjeto(valorTotal);
	        } else {
	            servico.setValorTotalProjeto(BigDecimal.ZERO);
	        }
	    }
    
	    @GetMapping("/editar/{id}")
	    public String editarServico(@PathVariable("id") Long id, Model model) {
	        Optional<Servico> servicoOpt = servicoRepository.findById(id);  // Usando o servicoRepository para buscar pelo ID
	        if (servicoOpt.isPresent()) {
	            Servico servico = servicoOpt.get();
	            model.addAttribute("servico", servico);
	            model.addAttribute("cliente", servico.getCliente());  // Passando o cliente associado ao serviço
	            model.addAttribute("isEditing", true);  // Adiciona o atributo que indica que está em modo de edição
	            return "servicos/formulario"; // Nome do template HTML de edição, certifique-se de que o arquivo editar-servico.html existe
	        } else {
	            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Serviço não encontrado");
	        }
	    }
    
    @GetMapping("/visualizar/{id}")
    public String visualizarServico(@PathVariable("id") Long id, Model model) {
        Optional<Servico> servicoOpt = servicoRepository.findById(id);
        if (servicoOpt.isPresent()) {
            Servico servico = servicoOpt.get();
            model.addAttribute("servico", servico);
            model.addAttribute("isEditing", false);  // Modo de visualização
            return "servicos/formulario";  // Nome da página de visualização
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
    
    @PostMapping("/enviarRelatorio/{clienteId}")
    public ResponseEntity<String> enviarRelatorio(@PathVariable Long clienteId) {
        try {
            Optional<Cliente> clienteOpt = clienteRepository.findById(clienteId);

            if (clienteOpt.isPresent()) {
                Cliente cliente = clienteOpt.get();
                List<Servico> servicos = servicoRepository.findByClienteId(clienteId);

                if (servicos.isEmpty()) {
                    return ResponseEntity.status(404).body("Nenhum serviço encontrado para o cliente.");
                }

                StringBuilder tabelaHtml = new StringBuilder();
                tabelaHtml.append("<h1>Relatório de Serviços</h1>")
                        .append("<p>Olá ").append(cliente.getNome()).append(",</p>")
                        .append("<p>Segue a lista de serviços associados a você:</p>")
                        .append("<table border='1' style='border-collapse: collapse; width: 100%;'>")
                        .append("<thead><tr><th>Descrição</th><th>Linguagem</th><th>Valor Total</th></tr></thead><tbody>");

                for (Servico servico : servicos) {
                    tabelaHtml.append("<tr>")
                            .append("<td>").append(servico.getDescricao()).append("</td>")
                            .append("<td>").append(servico.getLinguagem()).append("</td>")
                            .append("<td>").append(String.format("R$ %.2f", servico.getValorTotalProjeto())).append("</td>")
                            .append("</tr>");
                }

                tabelaHtml.append("</tbody></table>")
                        .append("<p>Obrigado por utilizar nossos serviços!</p>");

                // Enviar o e-mail HTML
                emailService.enviarEmailHtml(cliente.getEmail(), "Relatório de Serviços", tabelaHtml.toString());

                return ResponseEntity.ok("Relatório enviado com sucesso para " + cliente.getEmail());
            } else {
                return ResponseEntity.status(404).body("Cliente não encontrado.");
            }
        } catch (RuntimeException e) {
            return ResponseEntity.status(500).body("Erro ao enviar o e-mail: " + e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Erro interno: " + e.getMessage());
        }
    }
}