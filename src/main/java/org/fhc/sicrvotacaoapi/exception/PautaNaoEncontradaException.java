package org.fhc.sicrvotacaoapi.exception;

public class PautaNaoEncontradaException extends RuntimeException {
    public PautaNaoEncontradaException(Long pautaId) {
        super("Pauta não encontrada com id " + pautaId);
    }

    public PautaNaoEncontradaException(String message) {
        super(message);
    }
}
