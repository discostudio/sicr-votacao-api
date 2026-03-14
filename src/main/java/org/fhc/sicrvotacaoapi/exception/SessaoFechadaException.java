package org.fhc.sicrvotacaoapi.exception;

public class SessaoFechadaException extends RuntimeException{

    public SessaoFechadaException(Long sessaoId) {
        super("Sessão fechada: id=" + sessaoId + ". Não é possível registrar voto.");
    }
}
