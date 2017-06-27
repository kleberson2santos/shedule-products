package com.bokine.agendamento.repository.filter;

import java.io.Serializable;

/**
 * @author kleber
 *
 */
public class ClienteFilter implements Serializable {

	private static final long serialVersionUID = 1L;

	private String nome;
	private String email;
	private String cpf;
	private String celular;

	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getCpf() {
		return cpf;
	}

	public void setCpf(String cpf) {
		this.cpf = cpf;
	}

	public String getCelular() {
		return celular;
	}

	public void setCelular(String celular) {
		this.celular = celular;
	}

	@Override
	public String toString() {
		return "ClienteFilter [nome=" + nome + ", email=" + email + ", cpf=" + cpf +", celular=" + celular+  "]";
	}

	

}