package com.algaworks.pedidovenda.repository;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.TemporalType;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.From;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateUtils;

import com.algaworks.pedidovenda.model.Agendamento;
import com.algaworks.pedidovenda.model.Cliente;
import com.algaworks.pedidovenda.model.NotaFiscal;
import com.algaworks.pedidovenda.model.Usuario;
import com.algaworks.pedidovenda.model.vo.DataQuantidade;
import com.algaworks.pedidovenda.repository.filter.AgendamentoFilter;
import com.algaworks.pedidovenda.util.jpa.Transactional;

public class Agendamentos implements Serializable {

	private static final long serialVersionUID = 1L;

	@Inject
	private EntityManager manager;

	@Transactional
	public Agendamento guardar(Agendamento agendamento) {
		return manager.merge(agendamento);
	}
	
	private List<Predicate> criarPredicatesParaFiltro(AgendamentoFilter filtro, Root<Agendamento> agendamentoRoot, 
			From<?, ?> clienteJoin) {
		CriteriaBuilder builder = manager.getCriteriaBuilder();
		List<Predicate> predicates = new ArrayList<>();
		
		if (filtro.getNumeroDe() != null) {
			predicates.add(builder.greaterThanOrEqualTo(agendamentoRoot.get("id"), filtro.getNumeroDe()));
		}

		if (filtro.getNumeroAte() != null) {
			predicates.add(builder.lessThanOrEqualTo(agendamentoRoot.get("id"), filtro.getNumeroAte()));
		}
		if (filtro.getDataCriacaoDe() != null) {
			predicates.add(builder.greaterThanOrEqualTo(agendamentoRoot.get("dataCriacao"), filtro.getDataCriacaoDe()));
		}
		
		if (filtro.getDataCriacaoAte() != null) {
			predicates.add(builder.lessThanOrEqualTo(agendamentoRoot.get("dataCriacao"), filtro.getDataCriacaoAte()));
		}
		
		if (filtro.getDataMontagemDe() != null) {
			predicates.add(builder.greaterThanOrEqualTo(agendamentoRoot.get("dataMontagem"), filtro.getDataMontagemDe()));
		}
		
		if (filtro.getDataMontagemAte() != null) {
			predicates.add(builder.lessThanOrEqualTo(agendamentoRoot.get("dataMontagem"), filtro.getDataMontagemAte()));
		}
		
		if (StringUtils.isNotBlank(filtro.getNomeCliente())) {
			predicates.add(builder.like(clienteJoin.get("nome"), "%" + filtro.getNomeCliente() + "%"));
		}
		
		if (filtro.getStatuses() != null && filtro.getStatuses().length > 0) {
			predicates.add(agendamentoRoot.get("status").in(Arrays.asList(filtro.getStatuses())));
		}
		
		return predicates;
	}
	
	
	
	public List<Agendamento> filtrados(AgendamentoFilter filtro) {
		From<?, ?> orderByFromEntity = null;
		
		CriteriaBuilder builder = manager.getCriteriaBuilder();
		CriteriaQuery<Agendamento> criteriaQuery = builder.createQuery(Agendamento.class);
		
		Root<Agendamento> agendamentoRoot = criteriaQuery.from(Agendamento.class);
		From<?, ?> clienteJoin = (From<?, ?>) agendamentoRoot.fetch("cliente", JoinType.INNER);
//		From<?, ?> vendedorJoin = (From<?, ?>) agendamentoRoot.fetch("vendedor", JoinType.INNER);
		
		List<Predicate> predicates = criarPredicatesParaFiltro(filtro, agendamentoRoot, clienteJoin);
		
		criteriaQuery.select(agendamentoRoot);
		criteriaQuery.where(predicates.toArray(new Predicate[0]));
		
		if (filtro.getPropriedadeOrdenacao() != null) {
			String nomePropriedadeOrdenacao = filtro.getPropriedadeOrdenacao();
			orderByFromEntity = agendamentoRoot;
			
			if (filtro.getPropriedadeOrdenacao().contains(".")) {
				nomePropriedadeOrdenacao = nomePropriedadeOrdenacao.substring(
					filtro.getPropriedadeOrdenacao().indexOf(".") + 1);
			}
			
			if (filtro.getPropriedadeOrdenacao().startsWith("cliente.")) {
				orderByFromEntity = clienteJoin;
			}
			
			if (filtro.isAscendente() && filtro.getPropriedadeOrdenacao() != null) {
				criteriaQuery.orderBy(builder.asc(orderByFromEntity.get(nomePropriedadeOrdenacao)));
			} else if (filtro.getPropriedadeOrdenacao() != null) {
				criteriaQuery.orderBy(builder.desc(orderByFromEntity.get(nomePropriedadeOrdenacao)));
			}
		}
		
		TypedQuery<Agendamento> query = manager.createQuery(criteriaQuery);
		
		query.setFirstResult(filtro.getPrimeiroRegistro());
		query.setMaxResults(filtro.getQuantidadeRegistros());
		
		return query.getResultList();
	}
	
	public int quantidadeFiltrados(AgendamentoFilter filtro) {
		CriteriaBuilder builder = manager.getCriteriaBuilder();
		CriteriaQuery<Long> criteriaQuery = builder.createQuery(Long.class);
		
		Root<Agendamento> agendamentoRoot = criteriaQuery.from(Agendamento.class);
		
		Join<Agendamento, Cliente> clienteJoin = agendamentoRoot.join("cliente", JoinType.INNER);

		List<Predicate> predicates = criarPredicatesParaFiltro(filtro, agendamentoRoot, clienteJoin);
		
		criteriaQuery.select(builder.count(agendamentoRoot));
		criteriaQuery.where(predicates.toArray(new Predicate[0]));
		
		TypedQuery<Long> query = manager.createQuery(criteriaQuery);
		
		return query.getSingleResult().intValue();
	}

	public Agendamento porId(Long id) {
		return this.manager.find(Agendamento.class, id);
	}

	public Map<Date, Long> valoresPorData(Integer numeroDeDias, Usuario criadoPor) {
		numeroDeDias -= 1;
		
		Calendar dataInicial = Calendar.getInstance();
		dataInicial = DateUtils.truncate(dataInicial, Calendar.DAY_OF_MONTH);
		dataInicial.add(Calendar.DAY_OF_MONTH, numeroDeDias * -1);
		
		Map<Date, Long> resultado = criarMapaVazio(numeroDeDias, dataInicial);
		
		String jpql = "select new com.algaworks.pedidovenda.model.vo.DataQuantidade(date(a.dataCriacao), count(a)) "
				+ "from Agendamento a where a.dataCriacao >= :dataInicial ";
		
		if (criadoPor != null) {
			jpql += "and a.usuario = :usuario ";
		}
		
		jpql += "group by date(dataCriacao)";
		
		TypedQuery<DataQuantidade> query = manager.createQuery(jpql, DataQuantidade.class);
		
		query.setParameter("dataInicial", dataInicial.getTime());
		
		if (criadoPor != null) {
			query.setParameter("usuario", criadoPor);
		}
		
		List<DataQuantidade> quantidadePorData = query.getResultList();
		
		for (DataQuantidade dataValor : quantidadePorData) {
			resultado.put(dataValor.getData(), dataValor.getValor());
		}
		
		return resultado;
	}

	private Map<Date, Long> criarMapaVazio(Integer numeroDeDias, Calendar dataInicial) {
		dataInicial = (Calendar) dataInicial.clone();
		Map<Date, Long> mapaInicial = new TreeMap<>();

		for (int i = 0; i <= numeroDeDias; i++) {
			mapaInicial.put(dataInicial.getTime(), 0L);
			dataInicial.add(Calendar.DAY_OF_MONTH, 1);
		}
		
		return mapaInicial;
	}

	public List<Agendamento> todos() {		
		return manager.createQuery("from Agendamento",Agendamento.class).getResultList();
	}
	
	public List<Agendamento> filtradosEntre(Date inicio, Date fim) {
		TypedQuery<Agendamento> query = manager.createQuery("SELECT a FROM Agendamento a "
				+ "where a.dataMontagem between :inicio AND :fim", Agendamento.class)
				.setParameter("inicio", inicio, TemporalType.DATE)
                .setParameter("fim", fim, TemporalType.DATE);
		List<Agendamento> results = query.getResultList();
		
		return results;
	}

	public List<NotaFiscal> buscarNotas(Agendamento agendamento) {
		return manager.createQuery("from NotaFiscal n where n.agendamento = :agendamento",NotaFiscal.class)
				.setParameter("agendamento", agendamento)
				.getResultList();
	}

}