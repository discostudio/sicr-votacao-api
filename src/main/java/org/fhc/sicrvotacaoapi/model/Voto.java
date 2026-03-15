package org.fhc.sicrvotacaoapi.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(
    uniqueConstraints = @UniqueConstraint(columnNames = {"pauta_id", "associado_cpf"}),
    indexes = { @Index(name = "idx_sessao_valor", columnList = "sessao_id, valor") }
)
@Getter
@NoArgsConstructor
public class Voto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "sessao_id", nullable = false)
    private SessaoVotacao sessao;

    // Para garantir via banco a restrição de um voto por pauta
    @ManyToOne
    @JoinColumn(name = "pauta_id", nullable = false)
    private Pauta pauta;

    @Column(name = "associado_cpf", nullable = false, length = 11)
    private String associadoCpf;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private VotoValor valor;

    private LocalDateTime criadoEm;

    public Voto(SessaoVotacao sessao, Pauta pauta, String associadoCpf, VotoValor valor) {
        this.sessao = sessao;
        this.pauta = pauta;
        this.associadoCpf = associadoCpf;
        this.valor = valor;
        this.criadoEm = LocalDateTime.now();
    }
}
