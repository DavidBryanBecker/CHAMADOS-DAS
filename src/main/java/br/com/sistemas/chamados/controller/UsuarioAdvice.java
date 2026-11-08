package br.com.sistemas.chamados.controller;

import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

@ControllerAdvice
public class UsuarioAdvice {

    @ModelAttribute("usuarioLogado")
    public String usuarioLogado(Authentication authentication) {
        return authentication != null ? authentication.getName() : null;
    }
}