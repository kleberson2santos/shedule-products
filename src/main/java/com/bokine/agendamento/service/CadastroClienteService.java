package com.bokine.agendamento.service;

import java.io.Serializable;

import javax.inject.Inject;
import javax.persistence.NoResultException;
import javax.persistence.NonUniqueResultException;

import com.bokine.agendamento.model.Cliente;
import com.bokine.agendamento.model.TipoPessoa;
import com.bokine.agendamento.repository.Clientes;
import com.bokine.agendamento.util.jpa.Transactional;

public class CadastroClienteService implements Serializable {

	private static final long serialVersionUID = 1L;

	@Inject
	private Clientes clientes;
	
	
	public Cliente buscaCliente(Cliente clienteFilter) {
		System.out.println("Busca Cliente Service");
		Cliente cliente = new Cliente();		
		//SE TIVER CPF PROCURA NO MYSQL
		// BUSCAR SEMPRE NO FIREBIRD
		if(!clienteFilter.getDocumentoReceitaFederal().isEmpty()){
			System.out.println("VAMOS BUSCAR NO MYSQL PELO CPF ELE NAO ESTA nullo ");
			try{
				cliente = clientes.porCpf(clienteFilter.getDocumentoReceitaFederal());
			}catch (NonUniqueResultException e) {
				System.err.println("Tem mais de um CPF encontrado: "+e);
			}
			catch (NoResultException n) {
				System.err.println("A busca nao retornou registro: "+n);
			}
			if(!cliente.isNovo()){
				System.out.println("Nao e cliente novo, tem ID retorna ele: ");
				System.out.println(">>: "+cliente);
				return cliente;
			}else{
				//SE NAO ACHAR PROCURA NO FIREBIRD
				System.out.println("Ja que nao encontrou no MysQL procura e salva o do Firebird ");
				cliente = buscaClienteESalvaFirebird(clienteFilter);
			}        
		}else{
			if(!clienteFilter.getNome().isEmpty()){
				System.out.println("VAMOS buscar pelo NOME ele nao esta Nullo ");
				cliente = buscaClienteESalvaFirebird(clienteFilter);
			} 
		}
		return cliente;
	}

	private Cliente buscaClienteESalvaFirebird(Cliente cliente){
		Cliente clienteAux =  new Cliente();
		clienteAux = clientes.buscaClienteFirebird(cliente);
		System.out.println("RETORNOU "+ clienteAux);

		cliente = salvar(clienteAux);

		return cliente;
		
	}

	@Transactional
	public Cliente salvar(Cliente cliente){
		cliente.setTipo(TipoPessoa.FISICA);
		cliente =  clientes.guardar(cliente);
		return cliente;
	}
	
	
}
