package com.bokine.agendamento.service;

import java.io.Serializable;

import javax.inject.Inject;

import com.bokine.agendamento.model.Produto;
import com.bokine.agendamento.repository.Produtos;
import com.bokine.agendamento.util.jpa.Transactional;

public class CadastroProdutoService implements Serializable {

	private static final long serialVersionUID = 1L;

	@Inject
	private Produtos produtos;
	
		
	@Transactional
	public Produto salvar(Produto produto) throws NegocioException {
		Produto produtoExistente = produtos.porSku(produto.getSku());
		
		if (produtoExistente != null && !produtoExistente.equals(produto)) {
			throw new NegocioException("Já existe um produto com o SKU informado.");
		}
		
		return produtos.guardar(produto);
	}
	

	public Produto salvarAntes(Produto produto){
			Produto produtoAux = new Produto();
			Produto produtoExistente = produtos.porSku(produto.getSku());
			
			if (produtoExistente != null && !produtoExistente.equals(produto)) {
				return produtoExistente;
			}else{
				produtoAux = produtos.guardar(produto);
			}
			
			return produtoAux;
		}
	
}
