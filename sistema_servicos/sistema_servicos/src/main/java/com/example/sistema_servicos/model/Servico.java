package com.example.sistema_servicos.model;

import jakarta.persistence.*;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonFormat;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.Locale;

import org.antlr.v4.runtime.misc.NotNull;

@Entity
@Table(name = "servicos")
public class Servico {


	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @JsonBackReference
    @ManyToOne
    @JoinColumn(name = "cliente_id", nullable = false)
    private Cliente cliente;

    @Column(nullable = false, length = 50)
    private String descricao;

    @Column(nullable = false, length = 100)
    private String linguagem;
    
    private BigDecimal valorHora;
    
    private Integer diaTrabalhado; // campo para calculo orcamento
    
    private Integer horaDia; // campo para calculo orcamento
    
    @Column(precision = 10, scale = 2)
    private BigDecimal valorTotalProjeto;
    
    
    @Transient
    private String valorHoraFormatado;

    
    @Column(length = 20)
    private String status;
    
    @Column(name = "data_solicitacao", nullable = false, updatable = false)
    private LocalDateTime dataSolicitacao;
    
    @PrePersist
    protected void onCreate() {
        this.dataSolicitacao = LocalDateTime.now();
        if (this.status == null) {
            this.status = "Em Análise";
        }
           
	 }

    // Getters e Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Cliente getCliente() {
        return cliente;
    }

    public void setCliente(Cliente cliente) {
        this.cliente = cliente;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public String getLinguagem() {
        return linguagem;
    }

    public void setLinguagem(String linguagem) {
        this.linguagem = linguagem;
    }
    
    public BigDecimal getValorHora() {
        return valorHora;
    }

    public void setValorHora(BigDecimal valorHora) {
        this.valorHora = valorHora;
    }

    
    public Integer getDiaTrabalhado() {
        return diaTrabalhado;
    }

    public void setDiaTrabalhado(Integer diaTrabalhado) {
        this.diaTrabalhado = diaTrabalhado;
    }
    
    public Integer getHoraDia() {
    return horaDia; // Aqui deve retornar 'horaDia'
    }

    public void setHoraDia(Integer horaDia) {
        this.horaDia = horaDia; // Aqui deve atribuir 'horaDia'
    }
    
	 // Método para calcular o valor total do projeto
	 public BigDecimal getValorTotalProjeto() {
	        return valorTotalProjeto;
	    }
	
	 public void setValorTotalProjeto(BigDecimal valorTotalProjeto) {
	    this.valorTotalProjeto = valorTotalProjeto;
	}

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public LocalDateTime getDataSolicitacao() {
        return dataSolicitacao;
    }
    
   
    }
