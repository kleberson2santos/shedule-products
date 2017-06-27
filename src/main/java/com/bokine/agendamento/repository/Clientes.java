package com.bokine.agendamento.repository;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceException;
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
import com.bokine.agendamento.model.Categoria;
import com.bokine.agendamento.model.Cliente;
import com.bokine.agendamento.model.Endereco;
import com.bokine.agendamento.model.ItemMontagem;
import com.bokine.agendamento.model.NotaFiscal;
import com.bokine.agendamento.model.Produto;
import com.bokine.agendamento.service.CadastroProdutoService;
import com.bokine.agendamento.service.NegocioException;
import com.bokine.agendamento.util.jpa.Corporativo;
import com.bokine.agendamento.util.jpa.Transactional;

public class Clientes implements Serializable {

	private static final long serialVersionUID = 1L;
	
	@Inject
	@Corporativo
	private EntityManager managerCorporativo;
	
	@Inject
	private EntityManager manager;
	
	@Inject
	private CadastroProdutoService cadastroProdutoService;
	

	ArrayList<String> nomes = new ArrayList<String> ();
	ArrayList<String> cpfs = new ArrayList<String> ();
	ArrayList<NotaFiscal> notas = new ArrayList<NotaFiscal>();
	


	public List<String> porNomeFirebird(String parametro) {
		String nome,cpf = new String("");
		nomes.clear();
		cpfs.clear();
		Query q = managerCorporativo
				.createNativeQuery("select distinct c.nome,c.cpf from CLIENTES c "
						+ "inner join ENDERECOS_CADASTRO e on c.GERADOR = e.GERADOR "
						+ "inner join SAIDAS s on s.CLIENTE=c.CLIENTE  "
						+ "where c.CLIENTE NOT IN (31) and s.EVENTO in (10,30) and c.cpf is not null and upper(c.NOME) like :nome ")
				.setParameter("nome", parametro.toUpperCase() + "%");
		@SuppressWarnings("unchecked")
		Collection<Object[]> results = q.getResultList();
		Iterator<Object[]> ite = results.iterator();
		while (ite.hasNext()) {
			Object[] elements = (Object[]) ite.next();
				nome = (String) elements[0];
				nomes.add(nome);
				cpf = (String) elements[1];
				cpfs.add(cpf);
		}         
        return nomes;		
	}
	
	public List<String> porCpfFirebird(String parametro) {
		nomes.clear();
		cpfs.clear();
		
		String nome,cpf = new String("");
		if (parametro != null) {
			parametro = removerCaracteresDoCPF(parametro);
			if(parametro.length()>3){
				parametro = inserirCaracteresDoCPF(parametro);
			}
			Query q = managerCorporativo
					.createNativeQuery("select distinct c.nome,c.cpf from CLIENTES c "
							+ "inner join ENDERECOS_CADASTRO e on c.GERADOR = e.GERADOR "
							+ "inner join SAIDAS s on s.CLIENTE=c.CLIENTE  "
							+ "where c.CLIENTE NOT IN (31) and s.EVENTO in (10,30) and c.cpf like :cpf ")
					.setParameter("cpf", parametro+"%");
			@SuppressWarnings("unchecked")
			Collection<Object[]> results = q.getResultList();
			Iterator<Object[]> ite = results.iterator();
			while (ite.hasNext()) {
				Object[] elements = (Object[]) ite.next();
					nome = (String) elements[0];
					nomes.add(nome);
					cpf = (String) elements[1];
					cpfs.add(cpf); 
			}         
	        return cpfs;
		}else{
			return new ArrayList<>();
		}
	}
	
	public List<Cliente> buscaClientesPorNome(String parametro) {
		String nome,cpf = new String("");
		
		List<Cliente> filtrados = new ArrayList<Cliente>();
		Query q = managerCorporativo
				.createNativeQuery("select c.nome,c.cpf from clientes c where upper(c.NOME) like :nome and c.cpf is not null")
				.setParameter("nome", parametro.toUpperCase() + "%");
		@SuppressWarnings("unchecked")
		Collection<Object[]> results = q.getResultList();
		Iterator<Object[]> ite = results.iterator();
		while (ite.hasNext()) {
			Cliente c = new Cliente();
			Object[] elements = (Object[]) ite.next();
				nome = (String) elements[0];
				nomes.add(nome);
				cpf = (String) elements[1];
				cpfs.add(cpf);
				
				c.setNome(nome);
				c.setDocumentoReceitaFederal(cpf);				
				filtrados.add(c);
		}         
        return filtrados;		
	}
	
	private String inserirCaracteresDoCPF(String str){	
		return formataCpf(str);
	}
	
	private String formataCpf(String str) {
		if(str.length()>=3 && str.length()<6){
			return str.replaceAll("(\\d{3})", "$1.");
		}else{
				if(str.length()>=6 && str.length()<9){
					return str.replaceAll("(\\d{3})(\\d{3})", "$1.$2.");
				}else{
					if(str.length()>=9 && str.length()<11){
						return str.replaceAll("(\\d{3})(\\d{3})(\\d{3})", "$1.$2.$3-");
					}else{
						if(str.length()>=10 && str.length()<12){
							return str.replaceAll("(\\d{3})(\\d{3})(\\d{3})(\\d{2})", "$1.$2.$3-$4");
						}else{
							return str.substring(0,11);
						}
					}
						
				}
		}
	}

	private String removerCaracteresDoCPF(String str) {
		if (str != null) {
			return str.replaceAll("\\D","");
		}
		return null;
	}
		
	
//	public Agendamento guardar(Agendamento agendamento) {
//		return this.manager.merge(agendamento);
//	}
//	
	
	public Cliente guardar(Cliente cliente) {
		return this.manager.merge(cliente);
	}
	
	public List<NotaFiscal> buscarSaidasPorCliente(Cliente cliente) {
		notas= new ArrayList<NotaFiscal>();
		if(cliente!=null){
			Query q = managerCorporativo.createNativeQuery("select nf.COD_OPERACAO, nf.NOTA from nf inner join saidas s "
					+ "on s.SAIDA = nf.COD_OPERACAO where s.CLIENTE = :cliente")
					.setParameter("cliente", cliente.getCodigo().toString());
			
			List<NotaFiscal> saidasAgendadas = this.buscarSaidasSistema(cliente);
			
			@SuppressWarnings("unchecked")
			Collection<Object[]> results = q.getResultList();
			Iterator<Object[]> ite = results.iterator();
			while (ite.hasNext()) {
				Object[] elements = (Object[]) ite.next();
				boolean existeSaida = false;
				for (NotaFiscal nf : saidasAgendadas) {
					if(nf.getSaida().equals((Integer) elements[0]))
						existeSaida=true;
				}
				if(existeSaida){
				}else{
					notas.add(new NotaFiscal((Integer) elements[0],(Integer) elements[1]));
				}
			}
		}
	return notas;
	}

	
	public List<ItemMontagem> buscarProdutosPorSaidasSelecionadas(ArrayList<Integer> notas){
		//String notaTemp = new String();
		for (Integer i : notas) {
			System.out.println(i);
		}
		Categoria categoria = new Categoria();
		categoria.setId(23L);
		List<ItemMontagem> itens = new ArrayList<ItemMontagem>();
		if (notas.isEmpty())
			return itens;
		String sql = "select P.DESCRICAO1, P.DESCRICAO2, P.COD_PRODUTO, PE.COD_OPERACAO, PE.NOTA "
				+ "from PRODUTOS P inner join PRODUTOS_EVENTOS PE on P.PRODUTO = PE.PRODUTO where P.CATEGORIA = 23 "
				+ "and PE.COD_OPERACAO IN "+ sqlFormatedList(notas);
		Query q = managerCorporativo.createNativeQuery(sql);
		@SuppressWarnings("unchecked")
		Collection<Object[]> results = q.getResultList();
		Iterator<Object[]> ite = results.iterator();
		while (ite.hasNext()) {
			Object[] elements = (Object[]) ite.next();			
			Produto produto = new Produto();
			produto.setCategoria(categoria);
			produto.setNome((String) elements[0]);
			produto.setRotulo((String) elements[1]);
			produto.setCodigo((String) elements[2]);
			produto.setNota(new NotaFiscal((Integer) elements[3], (Integer) elements[4]));
			//notaTemp = produto.getNota().getNota().toString();
			produto.setQuantidadeEstoque(1);
			produto.setSku("MM"+0+0+((String) elements[2]).replaceAll("[^0-9]", ""));
			System.out.println("produto SKU TRANSFORMADO: "+produto.getSku());
			produto.setValorUnitario(new BigDecimal("0.00"));
			
			produto = cadastroProdutoService.salvarAntes(produto);
			produto.setNota(new NotaFiscal((Integer) elements[3], (Integer) elements[4]));
			if(produto.getId()!=null){
				ItemMontagem item = new ItemMontagem(produto);
				item.setNotaFiscal(produto.getNota().getNota().toString());
				itens.add(item);
			}
		}
		return itens;
	}
	
	private String sqlFormatedList(List<Integer> list){
		
		 StringBuilder sb = new StringBuilder();
		 sb.append("(");
		 for (Integer i : list){
		   sb.append(i+",");
		 }
		 sb.deleteCharAt(sb.length() -1);
		 sb.append(")");
		 return sb.toString();
		}
	
	public Cliente buscaClienteFirebird(Cliente cliente) {
		System.out.println("BUSCANDO NO BANCO DE DADOS FIREBIRD"+ cliente);
		Cliente clienteAux = new Cliente();
		if(cliente.getDocumentoReceitaFederal() != null ){
			Query q = managerCorporativo.createNativeQuery("select c.cpf, c.nome, c.e_mail, c.cel, c.cliente,"
					+ " e.logradouro, e.dicas_endereco, e.cidade, e.estado, e.cep"
					+ " from CLIENTES c inner join ENDERECOS_CADASTRO e on c.GERADOR = e.GERADOR"
					+ " inner join SAIDAS s on s.CLIENTE=c.CLIENTE "
					+ " inner join nf on s.SAIDA=nf.COD_OPERACAO  where c.CLIENTE NOT IN (31) and c.cpf = ?1");
			q.setParameter(1, cliente.getDocumentoReceitaFederal());
			@SuppressWarnings("unchecked")
			Collection<Object[]> results = q.getResultList();
			Iterator<Object[]> ite = results.iterator();
			while (ite.hasNext()) {
				Object[] elements = (Object[]) ite.next();
					clienteAux.setDocumentoReceitaFederal((String) elements[0]);
					
					clienteAux.setNome((String) elements[1]);
					
					clienteAux.setEmail((String) elements[2]);
					
					clienteAux.setTelefone((String) elements[3]);
					
					clienteAux.setCodigo((Integer) elements[4]);
					
					clienteAux.setEndereco(new Endereco((String) elements[5], (String) elements[6], (String) elements[7], 
							(String) elements[8], (String) elements[9]));
			}         
		}else{
			if(cliente.getNome()!=null){
				Query q = managerCorporativo.createNativeQuery("select c.cpf, c.nome, c.e_mail , c.cel, c.cliente, e.logradouro, e.dicas_endereco, e.cidade, e.estado, e.cep"
					+ " from CLIENTES c inner join ENDERECOS_CADASTRO e on c.GERADOR = e.GERADOR"
					+ " inner join SAIDAS s on s.CLIENTE=c.CLIENTE "
					+ " inner join nf on s.SAIDA=nf.COD_OPERACAO  where c.CLIENTE NOT IN (31) and  c.nome like :nome")
				.setParameter("nome", cliente.getNome()+"%");
				@SuppressWarnings("unchecked")
				Collection<Object[]> results = q.getResultList();
				Iterator<Object[]> ite = results.iterator();
				while (ite.hasNext()) {
					Object[] elements = (Object[]) ite.next();
						clienteAux.setDocumentoReceitaFederal((String) elements[0]);
						
						clienteAux.setNome((String) elements[1]);
						
						clienteAux.setEmail((String) elements[2]);
						
						clienteAux.setTelefone((String) elements[3]);
						
						clienteAux.setCodigo((Integer) elements[4]);
						
						clienteAux.setEndereco(new Endereco((String) elements[5], (String) elements[6], (String) elements[7], 
								(String) elements[8], (String) elements[9]));
				}         
			}else{
				if(cliente.getEmail()!=null){
					Query q = managerCorporativo.createNativeQuery("select c.cpf, c.nome, c.e_mail, c.cel, c.cliente, e.logradouro, e.dicas_endereco, e.cidade, e.estado, e.cep"
							+ " from CLIENTES c inner join ENDERECOS_CADASTRO e on c.GERADOR = e.GERADOR"
							+ " inner join SAIDAS s on s.CLIENTE=c.CLIENTE "
							+ " inner join nf on s.SAIDA=nf.COD_OPERACAO  where c.CLIENTE NOT IN (31) and c.e_mail = ?1 ");
					q.setParameter(1, cliente.getEmail());
					@SuppressWarnings("unchecked")
					Collection<Object[]> results = q.getResultList();
					Iterator<Object[]> ite = results.iterator();
					while (ite.hasNext()) {
						Object[] elements = (Object[]) ite.next();
							clienteAux.setDocumentoReceitaFederal((String) elements[0]);
							
							clienteAux.setNome((String) elements[1]);
							
							clienteAux.setEmail((String) elements[2]);
							
							clienteAux.setTelefone((String) elements[3]);
							
							clienteAux.setCodigo((Integer) elements[4]);
							
							clienteAux.setEndereco(new Endereco((String) elements[5], (String) elements[6], (String) elements[7], 
									(String) elements[8], (String) elements[9]));
					}         
				}
			}
		}    
		return clienteAux;
	}
	
	public Cliente porId(Long id) {
		return this.manager.find(Cliente.class, id);
	}
	
	public Cliente porCpf(String cpf){
		if(cpf==null){
			System.err.println("O CPF TA NULL");
		}
		try{
			return this.manager.createQuery("from Cliente where upper(documentoReceitaFederal) like :cpf", Cliente.class)
					.setParameter("cpf", cpf.toUpperCase() + "%").getSingleResult();
		}catch (NoResultException e) {
		}
		return null;
	}
	public Cliente porNome(String nome){
		if(nome==null){
			System.err.println("O NOME TA NULL");
		}
		try{
			return this.manager.createQuery("from Cliente where upper(nome) like :nome", Cliente.class)
					.setParameter("nome", nome.toUpperCase() + "%").getSingleResult();
		}catch (NoResultException e) {
		}
		return null;
	}
	
	

	public List<NotaFiscal> buscarSaidasSistema(Cliente filter) {
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

	@Transactional
	public void remover(ItemMontagem itemMontagem) throws NegocioException {
		System.out.println("REMOVENDO ITEM");
		try {
			itemMontagem = manager.find(ItemMontagem.class, itemMontagem.getId());
			manager.remove(itemMontagem);
			manager.flush();
		} catch (PersistenceException e) {
			throw new NegocioException("Item não pode ser excluído.");
		}
	}

	public List<Cliente> porNomeTodos(String nome) {
		return this.manager.createQuery("from Cliente " +
				"where upper(nome) like :nome", Cliente.class)
				.setParameter("nome", nome.toUpperCase() + "%")
				.getResultList();
	}
}