package br.com.sistemas.chamados.exception;

public class ChamadoFechadoException extends RegraNegocioException {

    public ChamadoFechadoException(String mensagem) {
        super(mensagem);
    }
}