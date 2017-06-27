package com.bokine.agendamento.repository;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceException;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Fetch;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.apache.commons.lang3.StringUtils;

import com.bokine.agendamento.model.Categoria;
import com.bokine.agendamento.model.Produto;
import com.bokine.agendamento.repository.filter.ProdutoFilter;
import com.bokine.agendamento.service.NegocioException;
import com.bokine.agendamento.util.jpa.Transactional;

public class Produtos implements Serializable {

	private static final long serialVersionUID = 1L;

	@Inject
	private EntityManager manager;

	@Transactional
	public Produto guardar(Produto produto) {
		System.out.println("> REPOSITORY GUARDAR PRODUTO : " +produto);
		return manager.merge(produto);
	}
	
	
	public Produto guardarAntes(Produto produto) {
		System.out.println("> REPOSITORY GUARDAR ANTES O PRODUTO : " +produto);
		Produto produtoRetornado = new Produto();
		EntityTransaction trx = manager.getTransaction();
		System.out.println("> TRANSACAO ATIVA?  : " +trx.isActive());
		trx.begin();
		
			try {
				manager.merge(produto);
				manager.flush();
				
				
				System.out.println(" BUSCA POR SKU: "+this.porSku(produto.getSku()));
			} catch (Exception e) {
				System.out.println(">>Houve um erro ao GUARDAR o objeto. "+e);
			}
		trx.commit();
		return produtoRetornado;
	}


	
	@Transactional
	public void remover(Produto produto) throws NegocioException {
		try {
			produto = porId(produto.getId());
			manager.remove(produto);
			manager.flush();
		} catch (PersistenceException e) {
			throw new NegocioException("Produto não pode ser excluído.");
		}
	}

	public Produto porSku(String sku) {
		try {
			return manager.createQuery("from Produto where upper(sku) = :sku", Produto.class)
				.setParameter("sku", sku.toUpperCase())
				.getSingleResult();
		} catch (NoResultException e) {
			return null;
		}
	}
	
	public List<Produto> filtrados(ProdutoFilter filtro) {
		CriteriaBuilder builder = manager.getCriteriaBuilder();
		CriteriaQuery<Produto> criteriaQuery = builder.createQuery(Produto.class);
		List<Predicate> predicates = new ArrayList<>();
		
		Root<Produto> produtoRoot = criteriaQuery.from(Produto.class);
		Fetch<Produto, Categoria> categoriaJoin = produtoRoot.fetch("categoria", JoinType.INNER);
		categoriaJoin.fetch("categoriaPai", JoinType.INNER);
		
		if (StringUtils.isNotBlank(filtro.getSku())) {
			predicates.add(builder.equal(produtoRoot.get("sku"), filtro.getSku()));
		}
		
		if (StringUtils.isNotBlank(filtro.getNome())) {
			predicates.add(builder.like(builder.lower(produtoRoot.get("nome")), 
					"%" + filtro.getNome().toLowerCase() + "%"));
		}
		
		criteriaQuery.select(produtoRoot);
		criteriaQuery.where(predicates.toArray(new Predicate[0]));
		criteriaQuery.orderBy(builder.asc(produtoRoot.get("nome")));
		
		TypedQuery<Produto> query = manager.createQuery(criteriaQuery);
		return query.getResultList();
	}
	
	public Produto porId(Long id) {
		System.out.println("CHAMOU O BUSCA POR ID");
		return manager.find(Produto.class, id);
	}

	public List<Produto> porNome(String nome) {
		return this.manager.createQuery("from Produto where upper(nome) like :nome", Produto.class)
				.setParameter("nome", nome.toUpperCase() + "%").getResultList();
	}
	
}