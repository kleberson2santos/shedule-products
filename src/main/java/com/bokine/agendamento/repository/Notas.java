package com.bokine.agendamento.repository;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.apache.commons.lang3.StringUtils;

import com.bokine.agendamento.model.Agendamento;
import com.bokine.agendamento.model.Cliente;
import com.bokine.agendamento.model.ItemMontagem;
import com.bokine.agendamento.model.NotaFiscal;
import com.bokine.agendamento.model.StatusItem;
import com.bokine.agendamento.util.jpa.Corporativo;

public class Notas implements Serializable{
	
	private static final long serialVersionUID = 1L;
	
	@Inject
	private EntityManager manager;
	
	@Inject
	@Corporativo
	private EntityManager managerCorporativo;

	ArrayList<NotaFiscal> notas;
	
	public List<NotaFiscal> buscarSaidasPorCliente(Cliente cliente) {
		System.out.println("Buscar saidasPorCliente no Firebird");
		notas = new ArrayList<NotaFiscal>();
		if(cliente!=null){
			Query q = managerCorporativo.createNativeQuery("select nf.COD_OPERACAO, nf.NOTA from nf inner join saidas s "
					+ "on s.SAIDA = nf.COD_OPERACAO where s.CLIENTE = :cliente")
					.setParameter("cliente", cliente.getCodigo().toString());
			@SuppressWarnings("unchecked")
			Collection<Object[]> results = q.getResultList();
			Iterator<Object[]> ite = results.iterator();
			while (ite.hasNext()) {
				Object[] elements = (Object[]) ite.next();
				notas.add(new NotaFiscal((Integer) elements[0],(Integer) elements[1]));
			}
		}
	return notas;
	}
	
	public List<NotaFiscal> buscarNotasAgendadas(Cliente filter) {
		System.out.println("Buscar notas agendadas no sistema mysql");
		CriteriaBuilder builder = manager.getCriteriaBuilder();
		CriteriaQuery<NotaFiscal> criteriaQuery = builder.createQuery(NotaFiscal.class);
		
		Root<NotaFiscal> notaRoot = criteriaQuery.from(NotaFiscal.class);
		Join<NotaFiscal, Agendamento> agendamentoJoin = notaRoot.join("agendamento", JoinType.INNER);
		Join<Agendamento, Cliente> clienteJoin = agendamentoJoin.join("cliente", JoinType.INNER);

		List<Predicate> predicates = new ArrayList<>();
		
		if (StringUtils.isNotBlank(filter.getNome())) {
			predicates.add(builder.equal(clienteJoin.get("nome"), filter.getNome()));
		}
		criteriaQuery.select(notaRoot);
		criteriaQuery.where(predicates.toArray(new Predicate[0]));
		
		TypedQuery<NotaFiscal> query = manager.createQuery(criteriaQuery);
		return query.getResultList();
	}
	
	public List<NotaFiscal> buscarNotasAgendadasECompletas(Cliente filter) {
		List<NotaFiscal> retorno = new ArrayList<NotaFiscal>();
		Query q = manager.createNativeQuery("select nf.nota as nota, nf.saida as saida " + 
				"from nota_fiscal nf " + 
				"inner join agendamento a on nf.agendamento_id=a.id " + 
				"inner join cliente c on a.cliente_id=c.id " + 
				"where c.nome like :nome" + 
				" and nf.nota not in(select distinct it.notaFiscal from item_montagem it where it.statusItem='CANCELADO')")
				.setParameter("nome", filter.getNome()+"%");
		@SuppressWarnings("unchecked")
		Collection<Object[]> results = q.getResultList();
		Iterator<Object[]> ite = results.iterator();
		while (ite.hasNext()) {
			Object[] elements = (Object[]) ite.next();
			NotaFiscal nf = new NotaFiscal((Integer) elements[1], (Integer) elements[0]);
			retorno.add(nf);
		}         
		return retorno;
	}
	
	public void exemplo() {
		CriteriaBuilder builder = manager.getCriteriaBuilder();
		CriteriaQuery<ItemMontagem> criteriaQuery = builder.createQuery(ItemMontagem.class);
		
		Root<ItemMontagem> item = criteriaQuery.from(ItemMontagem.class);
		
		criteriaQuery.select(item);
		List<Predicate> predicates = new ArrayList<>();
		predicates.add(builder.equal(item.get("statusItem"), StatusItem.CANCELADO));
		
		TypedQuery<ItemMontagem> query = manager.createQuery(criteriaQuery);
		List<ItemMontagem> itens = query.getResultList();
		
		for (ItemMontagem c : itens) {
			System.out.println(c.getNotaFiscal() + " - " + c.getStatusItem());
		}
	}
}
