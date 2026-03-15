package org.fhc.sicrvotacaoapi.utils;

import org.fhc.sicrvotacaoapi.model.Pauta;
import org.fhc.sicrvotacaoapi.model.SessaoVotacao;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDateTime;

public class TestEntityFactory {

    public static Pauta criarPauta(Long id, String nome) {
        Pauta pauta = new Pauta(nome, "Descrição da pauta " + nome);
        ReflectionTestUtils.setField(pauta, "id", id);
        return pauta;
    }

    public static SessaoVotacao criarSessao(Pauta pauta, int duracaoMinutos) {
        SessaoVotacao sessao = new SessaoVotacao(pauta, LocalDateTime.now(), LocalDateTime.now().plusMinutes(duracaoMinutos));
        ReflectionTestUtils.setField(sessao, "id", 1L);
        return sessao;
    }
}
