package com.algaworks.pedidovenda.controller;

import java.io.Serializable;
import java.util.List;

import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;

import com.algaworks.pedidovenda.model.Grupo;
import com.algaworks.pedidovenda.model.Usuario;
import com.algaworks.pedidovenda.service.CadastroUsuarioService;
import com.algaworks.pedidovenda.service.NegocioException;
import com.algaworks.pedidovenda.util.jsf.FacesUtil;

@Named
@ViewScoped
public class CadastroUsuarioBean implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	@Inject
	private CadastroUsuarioService cadastroUsuarioService;

	private Usuario usuario;
	private List<Grupo> grupos;
	private Grupo grupoSelecionado;

	public CadastroUsuarioBean() {
		limpar();
	}
	
	public void inicializar() {
		if (this.usuario == null) {
			limpar();
		}
		grupos = cadastroUsuarioService.grupos();
//		grupos.forEach(f -> {
//			System.out.println(f.getNome());
//		});

	}
	
	private void limpar(){
		usuario = new Usuario();
		grupoSelecionado = new Grupo();
	}

	public void salvar() {
		try {
			this.usuario = cadastroUsuarioService.salvar(this.usuario);
			limpar();
			FacesUtil.addInfoMessage("Usuário salvo com sucesso");
		} catch (NegocioException ne) {
			FacesUtil.addErrorMessage(ne.getMessage());
		}
	}

	public Usuario getUsuario() {
		return usuario;
	}

	public void setUsuario(Usuario usuario) {
		this.usuario = usuario;
	}
	
	public List<Grupo> getGrupos() {
		return grupos;
	}
	
	public Grupo getGrupoSelecionado() {
		return grupoSelecionado;
	}

	public void setGrupoSelecionado(Grupo grupoSelecionado) {
		this.grupoSelecionado = grupoSelecionado;
	}

	public boolean isEditando(){
		return this.usuario.getId() != null;
	}
	
	public void excluirGrupo(){		
		usuario.getGrupos().remove(grupoSelecionado);
		FacesUtil.addInfoMessage("Grupo excluido com sucesso!");
	}
	
	public void adicionarGrupo(){
		if (usuario.getGrupos().contains(grupoSelecionado)){
			FacesUtil.addWarningMessage("Esse grupo já foi adicionado.");
		}else{
			this.usuario.getGrupos().add(grupoSelecionado);
		}
	}
}