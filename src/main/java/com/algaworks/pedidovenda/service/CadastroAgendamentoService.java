package com.algaworks.pedidovenda.service;

import java.io.Serializable;
import java.util.Date;

import javax.inject.Inject;

import com.algaworks.pedidovenda.model.Agendamento;
import com.algaworks.pedidovenda.model.StatusAgendamento;
import com.algaworks.pedidovenda.repository.Agendamentos;
import com.algaworks.pedidovenda.security.UsuarioLogado;
import com.algaworks.pedidovenda.security.UsuarioSistema;

public class CadastroAgendamentoService implements Serializable{

	private static final long serialVersionUID = 1L;

	@Inject
	@UsuarioLogado
	private UsuarioSistema usuarioLogado;
	
	@Inject
	private Agendamentos agendamentos;

	
//	@Transactional
//	public Cliente salvarClienteFirebird(Cliente cliente)throws NegocioException{
//		Cliente clienteExistente = new Cliente();
//		if(cliente.isNovo()){
//			
//			clienteExistente = clientes.getClienteExistenteFirebird(cliente);
//		}
//		
//		if (clienteExistente != null && !clienteExistente.equals(cliente)) {
//			throw new NegocioException("Já existe um cliente com o Cpf informado.");
//		}
//		
//		cliente.setTipo(TipoPessoa.FISICA);
//		cliente =  clientes.guardarCliente(cliente);
//		return cliente;
//	}
	
	public Agendamento salvar(Agendamento agendamento){
		if (agendamento.isNovo()) {
			agendamento.setDataCriacao(new Date());
			agendamento.setStatus(StatusAgendamento.AGENDADO);
			agendamento.setUsuario(this.usuarioLogado.getUsuario());
		}
		
		agendamento =  agendamentos.guardar(agendamento);
		return agendamento;
	}
}	
	//pedido.recalcularValorTotal();
	
//	if (pedido.isNaoAlteravel()) {
//		throw new NegocioException("Pedido não pode ser alterado no status "
//				+ pedido.getStatus().getDescricao() + ".");
//	}
//	
//	
//	if (pedido.isValorTotalNegativo()) {
//		throw new NegocioException("Valor total do pedido não pode ser negativo.");
//	}
//	

