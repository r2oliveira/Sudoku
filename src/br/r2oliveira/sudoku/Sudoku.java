package br.r2oliveira.sudoku;

import java.util.Random;

import br.r2oliveira.sudoku.exception.SudokuException;
import br.r2oliveira.sudoku.tabuleiro.TabuleiroSudoku;

/**
 * Sudoku
 * 
 * @author Rodrigo Rodrigues de Oliveira
 */
public class Sudoku {

	private TabuleiroSudoku tabuleiroSudoku;
	private int[] valoresIniciais;

	private Random aleatorio;
	private int quantidadeJogadasAleatorias = 0;
	private int quantidadeGameOver = 0;

	/**
	 * Construtor.
	 * 
	 * @param texto no formato "0-0-0-0-0-0-1-0-6-8-0-0-   " Obs.: "0" é o valor de um campo vazio.
	 */
	public Sudoku(String s) {
		this.aleatorio = new Random();
		initTabuleiro(s);
	}

	/**
	 * Inicia a matriz com a String recebida.
	 * 
	 * @param texto no formato "0-0-0-0-0-0-1-0-6-8-0-0-   " Obs.: "0" é o valor de um campo vazio.
	 */
	private void initTabuleiro(String texto) {
		String[] lista = texto.split("-");
		int[] valores = new int[lista.length];
		for (int i = 0; i < lista.length; i++) {
			try {
				valores[i] = Integer.parseInt(lista[i]);
			} catch (Exception e) {
				throw new SudokuException("Texto inválido!\nTexto deve ter números sesperados por traço: \"0-1-3-5-0-4-9-...\" ");
			}
		}

		this.valoresIniciais = valores;
		int quantidadeCelulasLista = valores.length;
		int valorMaximo = (int) Math.sqrt(quantidadeCelulasLista);
		if (quantidadeCelulasLista != Math.pow(valorMaximo, 2)) {
			throw new SudokuException("Quantidade de células inválida.");
		}
		for (int i = 0; i < quantidadeCelulasLista; i++) {
			if (!(valores[i] >= 0 && valores[i] <= valorMaximo)) {
				throw new SudokuException("Encontrado número inválido ao carregar o tabuleiro. Os números devem estar entre 0 e " + valorMaximo);
			}
		}
		int tamanhoBloco = (int) Math.sqrt(valorMaximo);
		tabuleiroSudoku = new TabuleiroSudoku(tamanhoBloco);
		recarregaTabuleiroComValoresIniciais();
		tabuleiroSudoku.imprimeTabuleiro();
	}


	/**
	 * Recarrega os números no mesa com os valores de preenchimento inicial.
	 */
	public void recarregaTabuleiroComValoresIniciais() {
		int c = 0;
		for (int linha = 0; linha < tabuleiroSudoku.TAMANHO_TABULEIRO; linha++) {
			for (int coluna = 0; coluna < tabuleiroSudoku.TAMANHO_TABULEIRO; coluna++) {
				if (valoresIniciais == null || valoresIniciais[c] == 0) {
					tabuleiroSudoku.setValorCelula(linha, coluna, null);
				} else {
					tabuleiroSudoku.setValorCelula(linha, coluna, valoresIniciais[c]);
				}
				c++;
			}
		}
		tabuleiroSudoku.calculaPossibilidadesDePreenchimentoDasCelulasVazias();
	}

	/**
	 * Resolve todas as células do Sudoku.
	 */
	public void resolveSudoku() {
		long tempoInicio = System.currentTimeMillis();
		do {
			if (!resolveCelulaComApenasUmaPossibilidade()) {
				if (!resolveCelulaComPossibilidadeCalculadaDentroDoBloco()) {
					resolveCelulaAleatoria();
				}
			}
			if (tabuleiroSudoku.verificaTabuleiroGameOver()) {
				quantidadeGameOver++;
				recarregaTabuleiroComValoresIniciais();
			}
		} while (!tabuleiroSudoku.verificaTabuleiroCompletamenteResolvido());
		System.out.println("\n\n******\n FIM!\n******");
		tabuleiroSudoku.imprimeTabuleiro();
		System.out.println("\nQuantidade de jogadas aleatórias realizadas: " + quantidadeJogadasAleatorias);
		System.out.println("Quantidade de GAME OVERs atingidos: " + quantidadeGameOver);
		System.out.printf("Tempo total de resolução: %s(ms)", (System.currentTimeMillis() - tempoInicio));
	}

	/**
	 * Tenta resolver uma célula aleatória.
	 */
	private void resolveCelulaAleatoria() {
		int linha;
		int coluna;
		do {
			linha = aleatorio.nextInt(tabuleiroSudoku.TAMANHO_TABULEIRO);
			coluna = aleatorio.nextInt(tabuleiroSudoku.TAMANHO_TABULEIRO);
		} while (tabuleiroSudoku.getValorCelula(linha, coluna) != null);

		tabuleiroSudoku.setValorCelula(linha, coluna, tabuleiroSudoku.getPossibilidadesDaCelula(linha, coluna)
				.get(aleatorio.nextInt(tabuleiroSudoku.getPossibilidadesDaCelula(linha, coluna).size())));
		quantidadeJogadasAleatorias++;
	}

	/**
	 * Resolve uma célula que tenha apenas uma possibilidade de preenchimento.
	 * 
	 * @return Conseguiu resolver alguma célula?
	 */
	private boolean resolveCelulaComApenasUmaPossibilidade() {
		for (int linha = 0; linha < tabuleiroSudoku.TAMANHO_TABULEIRO; linha++) {
			for (int coluna = 0; coluna < tabuleiroSudoku.TAMANHO_TABULEIRO; coluna++) {
				if (tabuleiroSudoku.getPossibilidadesDaCelula(linha, coluna).size() == 1) {
					tabuleiroSudoku.setValorCelula(linha, coluna, tabuleiroSudoku.getPossibilidadesDaCelula(linha, coluna).get(0));
					return true;
				}
			}
		}
		return false;
	}

	/**
	 * Resolve uma Casa em que uma das possibilidades de resolução só existe nesta Casa dentre as Casas pertencentes ao mesmo Bloco.
	 * 
	 * @return Conseguiu resolver alguma Casa?
	 */
	private boolean resolveCelulaComPossibilidadeCalculadaDentroDoBloco() {
		// Matriz montada no formato: matrizSolucoesEmBloco[bloco][possibilidade]
		// [bloco]: Em qual Bloco pertence a célula analisada
		// [possibilidade]: Quantas vezes a possibilidade aparece dentro do Bloco [bloco]
		int[][] matrizSolucoesEmBloco = new int[tabuleiroSudoku.TAMANHO_TABULEIRO][tabuleiroSudoku.TAMANHO_TABULEIRO];

		int auxLinha;
		int auxColuna;
		for (int linha = 0; linha < tabuleiroSudoku.TAMANHO_TABULEIRO; linha++) {
			for (int coluna = 0; coluna < tabuleiroSudoku.TAMANHO_TABULEIRO; coluna++) {
				auxLinha = tabuleiroSudoku.TAMANHO_BLOCO * (linha / tabuleiroSudoku.TAMANHO_BLOCO);
				auxColuna = coluna / tabuleiroSudoku.TAMANHO_BLOCO;
				for (int possibilidade : tabuleiroSudoku.getPossibilidadesDaCelula(linha, coluna)) {
					matrizSolucoesEmBloco[auxLinha + auxColuna][possibilidade - 1]++;
				}
			}
		}
		for (int linha = 0; linha < tabuleiroSudoku.TAMANHO_TABULEIRO; linha++) {
			for (int coluna = 0; coluna < tabuleiroSudoku.TAMANHO_TABULEIRO; coluna++) {
				auxLinha = tabuleiroSudoku.TAMANHO_BLOCO * (linha / tabuleiroSudoku.TAMANHO_BLOCO);
				auxColuna = coluna / tabuleiroSudoku.TAMANHO_BLOCO;
				for (int possibilidade : tabuleiroSudoku.getPossibilidadesDaCelula(linha, coluna)) {
					if (matrizSolucoesEmBloco[auxLinha + auxColuna][possibilidade - 1] == 1) {
						tabuleiroSudoku.setValorCelula(linha, coluna, possibilidade);
						return true;
					}
				}
			}
		}
		return false;
	}
}
