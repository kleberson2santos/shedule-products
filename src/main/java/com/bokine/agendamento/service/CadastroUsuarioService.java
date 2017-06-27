package com.bokine.agendamento.service;

import java.io.Serializable;
import java.util.List;

import javax.inject.Inject;

import com.bokine.agendamento.model.Grupo;
import com.bokine.agendamento.model.Usuario;
import com.bokine.agendamento.repository.Grupos;
import com.bokine.agendamento.repository.Usuarios;
import com.bokine.agendamento.util.jpa.Transactional;

public class CadastroUsuarioService implements Serializable {

	private static final long serialVersionUID = 1L;

	@Inject
	private Usuarios usuarios;
	
	@Inject
	private Grupos grupos;
	
	@Transactional
	public Usuario salvar(Usuario usuario) throws NegocioException {
		Usuario usuarioExistente = usuarios.porEmail(usuario.getEmail());
		
		if (usuarioExistente != null && !usuarioExistente.equals(usuario)) {
			throw new NegocioException("Já existe um usuario com as mesmas caracteristicas cadastrado.");
		}
		
		return usuarios.guardar(usuario);
	}

	public List<Grupo> grupos() {		
		return grupos.todos();
	}
	
}
