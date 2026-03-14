package org.fhc.sicrvotacaoapi.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "pauta")
@Getter
@NoArgsConstructor
public class Pauta {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nome;

    private String descricao;

    private LocalDateTime criadoEm;

    public Pauta(String nome, String descricao) {
        this.nome = nome;
        this.descricao = descricao;
        this.criadoEm = LocalDateTime.now();
    }
}
