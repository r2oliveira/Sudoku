package br.r2oliveira.sudoku.tabuleiro;

import java.util.ArrayList;
import java.util.List;

/**
 * Célula do tabuleiro de Sudoku
 * 
 * @author Rodrigo Rodrigues de Oliveira
 */
public class CelulaSudoku {

	private Integer valor;
	private int linha;
	private int coluna;
	/**
	 * Lista de valores de possível preenchimento no estado atual do Tabuleiro caso o campo valor estiver vazio.
	 */
	private List<Integer> possibilidades;

	public CelulaSudoku(int linha, int coluna) {
		this.linha = linha;
		this.coluna = coluna;
		possibilidades = new ArrayList<Integer>();
	}

	public CelulaSudoku(int linha, int coluna, int valor) {
		this(linha, coluna);
		this.valor = valor;
	}

	public Integer getValor() {
		return valor;
	}

	public void setValor(Integer valor) {
		this.valor = valor;
	}

	public int getLinha() {
		return linha;
	}

	public int getColuna() {
		return coluna;
	}

	public int getQuantidadePossibilidades() {
		return possibilidades.size();
	}

	public List<Integer> getPossibidades() {
		return possibilidades;
	}

	public void initPossibidades() {
		possibilidades = new ArrayList<Integer>();
	}

	/**
	 * Adiciona um valor de possibilidade
	 * 
	 * @param valor
	 */
	public void adicionaPossibilidade(Integer valor) {
		if (!possibilidadeExiste(valor)) {
			possibilidades.add(valor);
		}
	}

	/**
	 * Remove um valor de possibilidade
	 * 
	 * @param valor
	 */
	public void removePossibilidade(Integer valor) {
		for (int i = 0; i < possibilidades.size(); i++) {
			if (possibilidades.get(i).equals(valor)) {
				possibilidades.remove(i);
			}
		}
	}

	/**
	 * Verifica se o valor é uma possibilidade já existente na lista
	 * 
	 * @param valor
	 * @return
	 */
	public boolean possibilidadeExiste(Integer valor) {
		for (Integer n : possibilidades) {
			if (n.equals(valor)) {
				return true;
			}
		}
		return false;
	}

	@Override
	protected CelulaSudoku clone() {
		CelulaSudoku casa = new CelulaSudoku(linha, coluna, valor);
		for (Integer possibilidade : this.possibilidades) {
			casa.getPossibidades().add(new Integer(possibilidade));
		}
		return casa;
	}
}
