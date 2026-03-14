package org.fhc.sicrvotacaoapi.exception;

public class SessaoJaAbertaException extends RuntimeException {

    public SessaoJaAbertaException(Long pautaId) {
        super("Sessão já aberta para a pauta com id " + pautaId);
    }

    public SessaoJaAbertaException(String message) {
        super(message);
    }
}
