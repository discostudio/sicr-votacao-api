package org.fhc.sicrvotacaoapi.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "sessao_votacao",
        indexes = {
                @Index(name = "idx_sessao_pauta_id", columnList = "pauta_id") }
)
@Getter
@NoArgsConstructor
public class SessaoVotacao {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // permitir mais de uma sessão por pauta (desde que sessão anterior já esteja finalizada)
    @ManyToOne
    @JoinColumn(name = "pauta_id", nullable = false)
    private Pauta pauta;

    private LocalDateTime criadoEm;

    private LocalDateTime inicio;
    private LocalDateTime fim;

    public SessaoVotacao(Pauta pauta, LocalDateTime inicio, LocalDateTime fim) {
        this.pauta = pauta;
        this.inicio = inicio;
        this.fim = fim;
        this.criadoEm = LocalDateTime.now();
    }

    public boolean isAberta() {
        LocalDateTime now = LocalDateTime.now();
        return now.isAfter(inicio) && now.isBefore(fim);
    }
}
