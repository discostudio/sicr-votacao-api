package org.fhc.sicrvotacaoapi.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "sessao_votacao")
@Getter
@NoArgsConstructor
public class SessaoVotacao {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // uma sessão por pauta.
    @OneToOne
    @JoinColumn(name = "pauta_id", nullable = false, unique = true)
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
