package org.fhc.sicrvotacaoapi.exception;

public class SessaoNaoEncontradaException extends RuntimeException {

    public SessaoNaoEncontradaException(Long sessaoId) {
        super("Sessão não encontrada: id=" + sessaoId);
    }
}
