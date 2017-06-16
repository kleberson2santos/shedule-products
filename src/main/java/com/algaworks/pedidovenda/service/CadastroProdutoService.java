package com.algaworks.pedidovenda.service;

import java.io.Serializable;

import javax.inject.Inject;

import com.algaworks.pedidovenda.model.Produto;
import com.algaworks.pedidovenda.repository.Produtos;
import com.algaworks.pedidovenda.util.jpa.Transactional;

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
			System.out.println("> SERVICE REGRAS SALVAR ANTES: "+produto);
			Produto produtoAux = new Produto();
			Produto produtoExistente = produtos.porSku(produto.getSku());
			
			if (produtoExistente != null && !produtoExistente.equals(produto)) {
				System.err.println("------------------------------------------------");
				System.err.println(">>> Já existe um produto com o SKU informado.");
				System.err.println("------------------------------------------------");
				return produtoExistente;
			}else{
				produtoAux = produtos.guardar(produto);
			}
			
			return produtoAux;
		}
	
}
