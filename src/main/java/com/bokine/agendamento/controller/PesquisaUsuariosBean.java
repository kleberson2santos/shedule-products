package com.bokine.agendamento.controller;

import java.io.Serializable;
import java.util.List;

import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;

import com.bokine.agendamento.model.Usuario;
import com.bokine.agendamento.repository.Usuarios;
import com.bokine.agendamento.repository.filter.UsuarioFilter;
import com.bokine.agendamento.service.NegocioException;
import com.bokine.agendamento.util.jsf.FacesUtil;

@Named
@ViewScoped
public class PesquisaUsuariosBean  implements Serializable{
	
	private static final long serialVersionUID = 1L;
	
	@Inject
	private Usuarios usuarios;
	
	private UsuarioFilter filtro;
	private Usuario usuarioSelecionado;	
	private List<Usuario> usuariosFiltrados;	

	public PesquisaUsuariosBean() {
		filtro = new UsuarioFilter();
	}
	public void pesquisar(){
		usuariosFiltrados = usuarios.filtrados(filtro);
	}
	
	public void excluir(){
		try {
			usuarios.remover(usuarioSelecionado);
			usuariosFiltrados.remove(usuarioSelecionado);
			
			FacesUtil.addInfoMessage("Usuario " + usuarioSelecionado.getNome() 
					+ " excluído com sucesso.");
		} catch (NegocioException ne) {
			FacesUtil.addErrorMessage(ne.getMessage());
		}
	}
	
	public List<Usuario> getusuariosFiltrados() {
		return usuariosFiltrados;
	}

	public UsuarioFilter getFiltro() {
		return filtro;
	}
	
	public Usuario getUsuarioSelecionado() {
		return usuarioSelecionado;
	}
	
	public void setUsuarioSelecionado(Usuario usuarioSelecionado) {
		this.usuarioSelecionado = usuarioSelecionado;
	}

}