package br.r2oliveira.sudoku.tabuleiro;

import java.util.List;

import br.r2oliveira.sudoku.exception.SudokuException;

/**
 * Tabuleiro de Sudoku
 * 
 * @author Rodrigo Rodrigues de Oliveira
 */
public class TabuleiroSudoku {

	private CelulaSudoku[][] celula;
	public final int TAMANHO_TABULEIRO;
	public final int TAMANHO_BLOCO;
	public final int VALOR_INTEIRO_MINIMO = 1;
	public final int VALOR_INTEIRO_MAXIMO;

	public TabuleiroSudoku() {
		this(3);
	}

	public TabuleiroSudoku(int tamanhoBloco) {
		if (tamanhoBloco <= 0) {
			throw new SudokuException("Tamanho do bloco deve ser maior que zero. O Sudoku padrão possui bloco de tamanho 3");
		}
		this.TAMANHO_BLOCO = tamanhoBloco;
		this.TAMANHO_TABULEIRO = this.VALOR_INTEIRO_MAXIMO = (int) Math.pow(tamanhoBloco, 2);
		initCelulas();
	}

	/**
	 * Inicia as células do tabuleiro.
	 */
	private void initCelulas() {
		celula = new CelulaSudoku[TAMANHO_TABULEIRO][TAMANHO_TABULEIRO];
		for (int linha = 0; linha < TAMANHO_TABULEIRO; linha++) {
			for (int coluna = 0; coluna < TAMANHO_TABULEIRO; coluna++) {
				celula[linha][coluna] = new CelulaSudoku(linha, coluna);
			}
		}
	}

	public Integer getValorCelula(int linha, int coluna) {
		return celula[linha][coluna].getValor();
	}

	/**
	 * Define o valor da célula.
	 * 
	 * @param linha
	 * @param coluna
	 * @param valor
	 */
	public void setValorCelula(int linha, int coluna, Integer valor) {
		if (valor != null && (valor < VALOR_INTEIRO_MINIMO || valor > VALOR_INTEIRO_MAXIMO)) {
			celula[linha][coluna].setValor(null);
		}
		celula[linha][coluna].setValor(valor);
		calculaPossibilidadesDaCelulaPelasVizinhancas(celula[linha][coluna]);
		retiraPossibilidadeNasListasDeCelulasVizinhas(celula[linha][coluna]);
	}

	public List<Integer> getPossibilidadesDaCelula(int linha, int coluna) {
		return celula[linha][coluna].getPossibidades();
	}

	/**
	 * Retorna a quantidade de células vazias.
	 */
	public int getQuantidadeCelulasVazias() {
		int c = 0;
		for (int linha = 0; linha < TAMANHO_TABULEIRO; linha++) {
			for (int coluna = 0; coluna < TAMANHO_TABULEIRO; coluna++) {
				if (celula[linha][coluna].getValor() == null) {
					c++;
				}
			}
		}
		return c;
	}

	/**
	 * Define as possibilidades de preenchimento de todas as células do tabuleiro.
	 */
	public void calculaPossibilidadesDePreenchimentoDasCelulasVazias() {
		for (int linha = 0; linha < TAMANHO_TABULEIRO; linha++) {
			for (int coluna = 0; coluna < TAMANHO_TABULEIRO; coluna++) {
				calculaPossibilidadesDaCelulaPelasVizinhancas(celula[linha][coluna]);
			}
		}
	}

	/**
	 * Define as possibilidades de preenchimento de uma célula do tabuleiro.
	 * 
	 * @param celula
	 */
	private void calculaPossibilidadesDaCelulaPelasVizinhancas(CelulaSudoku celula) {
		// Inicializa lista de possibilidades.
		celula.initPossibidades();

		// Se a célula já estiver preenchida então não há o que fazer.
		if (celula.getValor() != null) {
			return;
		}

		// 1: Adiciona todo o conjunto de valores entre mínimo e máximo na lista de possibilidades da célula.
		for (int valor = VALOR_INTEIRO_MINIMO; valor <= TAMANHO_TABULEIRO; valor++) {
			celula.adicionaPossibilidade(valor);
		}

		// 2: Percorre todas as linhas do Tabuleiro na mesma coluna da célula e retira da lista de possibilidades os outros valores já existentes.
		for (int linha = 0; linha < TAMANHO_TABULEIRO; linha++) {
			if (this.celula[linha][celula.getColuna()].getValor() != null) {
				celula.removePossibilidade(this.celula[linha][celula.getColuna()].getValor());
			}
		}

		// 3: Percorre todas as colunas do Tabuleiro na mesma linha da célula e retira da lista de possibilidades os outros valores já existentes.
		for (int coluna = 0; coluna < TAMANHO_TABULEIRO; coluna++) {
			if (this.celula[celula.getLinha()][coluna].getValor() != null) {
				celula.removePossibilidade(this.celula[celula.getLinha()][coluna].getValor());
			}
		}

		// 4: Percorre todo o bloco da célula e retira da lista de possibilidades os outros valores já existentes.
		for (int linha = 0; linha < TAMANHO_TABULEIRO; linha++) {
			for (int coluna = 0; coluna < TAMANHO_TABULEIRO; coluna++) {
				if (celula.getLinha() / TAMANHO_BLOCO == linha / TAMANHO_BLOCO
						&& celula.getColuna() / TAMANHO_BLOCO == coluna / TAMANHO_BLOCO && this.celula[linha][coluna].getValor() != null) {
					celula.removePossibilidade(this.celula[linha][coluna].getValor());
				}
			}
		}
	}

	/**
	 * Retira da lista de possibilidade da vizinhança o valor da célula.
	 * 
	 * @param celula
	 */
	private void retiraPossibilidadeNasListasDeCelulasVizinhas(CelulaSudoku celula) {
		for (int linha = 0; linha < TAMANHO_TABULEIRO; linha++) {
			if (this.celula[linha][celula.getColuna()].getValor() == null) {
				this.celula[linha][celula.getColuna()].removePossibilidade(celula.getValor());
			}
		}
		for (int coluna = 0; coluna < TAMANHO_TABULEIRO; coluna++) {
			if (this.celula[celula.getLinha()][coluna].getValor() == null) {
				this.celula[celula.getLinha()][coluna].removePossibilidade(celula.getValor());
			}
		}
		for (int linha = 0; linha < TAMANHO_TABULEIRO; linha++) {
			for (int coluna = 0; coluna < TAMANHO_TABULEIRO; coluna++) {
				if (celula.getLinha() / TAMANHO_BLOCO == linha / TAMANHO_BLOCO
						&& celula.getColuna() / TAMANHO_BLOCO == coluna / TAMANHO_BLOCO && this.celula[linha][coluna].getValor() == null) {
					this.celula[linha][coluna].removePossibilidade(celula.getValor());
				}
			}
		}
	}

	/**
	 * Verifica se o Jogo está completamente resolvido.
	 * 
	 * @return Jogo está completamente resolvido?
	 */
	public boolean verificaTabuleiroCompletamenteResolvido() {
		for (int linha = 0; linha < TAMANHO_TABULEIRO; linha++) {
			for (int coluna = 0; coluna < TAMANHO_TABULEIRO; coluna++) {
				if (celula[linha][coluna].getValor() == null) {
					return false;
				}
			}
		}
		return true;
	}

	/**
	 * Verifica se o Tabuleiro está em estado de Game Over.
	 * 
	 * @return Game Over?
	 */
	public boolean verificaTabuleiroGameOver() {
		for (int linha = 0; linha < TAMANHO_TABULEIRO; linha++) {
			for (int coluna = 0; coluna < TAMANHO_TABULEIRO; coluna++) {
				if (celula[linha][coluna].getValor() == null && celula[linha][coluna].getPossibidades().isEmpty()) {
					return true;
				}
			}
		}
		return false;
	}

	/**
	 * Imprime o Tabuleiro no estado atual.
	 */
	public void imprimeTabuleiro() {
		String linhaTracejada = "";
		for (int i = 0; i < TAMANHO_TABULEIRO; i++) {
			linhaTracejada += "---";
		}
		for (int i = 0; i < TAMANHO_BLOCO; i++) {
			linhaTracejada += "-";
		}
		for (int linha = 0; linha < TAMANHO_TABULEIRO; linha++) {
			String traco = linha != 0 && linha % TAMANHO_BLOCO == 0 ? linhaTracejada + "\n" : "";
			System.out.printf("\n%s", traco);
			for (int coluna = 0; coluna < TAMANHO_TABULEIRO; coluna++) {
				String barra = coluna != 0 && coluna % TAMANHO_BLOCO == 0 ? " |" : " ";
				System.out.printf("%s %s", barra, celula[linha][coluna].getValor() == null ? "_" : celula[linha][coluna].getValor());
			}
		}
		System.out.println();
	}

	/**
	 * Imprime a matriz com todas as possibilidades de preenchimento nas células.
	 */
	public void imprimeTabuleiroComPossibilidades() {
		String linhaTracejada = "";
		for (int i = 0; i < TAMANHO_TABULEIRO; i++) {
			linhaTracejada += "------------";
		}
		for (int i = 0; i < TAMANHO_BLOCO; i++) {
			linhaTracejada += "-";
		}
		for (int linha = 0; linha < TAMANHO_TABULEIRO; linha++) {
			String traco = linha != 0 && linha % TAMANHO_BLOCO == 0 ? linhaTracejada + "\n" : "";
			System.out.printf("\n%s", traco);
			for (int coluna = 0; coluna < TAMANHO_TABULEIRO; coluna++) {
				String barra = coluna != 0 && coluna % TAMANHO_BLOCO == 0 ? "|" : "";
				System.out.printf("%s", barra);

				System.out.print("[");
				for (Integer inteiro : celula[linha][coluna].getPossibidades()) {
					System.out.printf("%d", inteiro);
				}
				System.out.print("]");

				for (int i = celula[linha][coluna].getPossibidades().size(); i <= TAMANHO_TABULEIRO; i++) {
					System.out.print(" ");
				}
			}

			System.out.println();
			for (int coluna = 0; coluna < TAMANHO_TABULEIRO; coluna++) {
				String barra = coluna != 0 && coluna % TAMANHO_BLOCO == 0 ? "|" : "";
				System.out.printf("%s", barra);
				System.out.printf("%s           ", celula[linha][coluna].getValor() == null ? "_" : celula[linha][coluna].getValor());
			}
			System.out.println();
			for (int coluna = 0; coluna < TAMANHO_TABULEIRO; coluna++) {
				String barra = coluna != 0 && coluna % TAMANHO_BLOCO == 0 ? "|" : "";
				System.out.printf("%s            ", barra);
			}
		}
		System.out.println();
	}

	@Override
	public TabuleiroSudoku clone() {
		TabuleiroSudoku novoTabuleiroSudoku = new TabuleiroSudoku(TAMANHO_BLOCO);
		for (int linha = 0; linha < TAMANHO_TABULEIRO; linha++) {
			for (int coluna = 0; coluna < TAMANHO_TABULEIRO; coluna++) {
				novoTabuleiroSudoku.setValorCelula(linha, coluna, celula[linha][coluna].getValor());
			}
		}
		return novoTabuleiroSudoku;
	}
}
