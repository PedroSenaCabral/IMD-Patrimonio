package lpii.bot;

import java.util.ArrayList;

public class Localizacao extends TipoBem {
    private ArrayList<String> salas;
    private String nomeSala;

    public Localizacao(ArrayList<String> salas, String nomeSala) {
        super();
        this.salas = salas;
        this.nomeSala = nomeSala;
    }

    public void setLocalizacao(ArrayList<String> salas) {
        this.salas = salas;
    }

    public ArrayList<String> getLocalizacao() {
        return salas;
    }
    /*
     * Metodo para cadastrar uma localizacao
     */
    public void cadastrarSala(String sala){
        salas.add(sala);
    }
    /*
     * Metodo para listar as localizacao cadastradas
     */
    public void listarSalas(){
        for(String sala : salas) {
            System.out.println(sala);
        }
    }
    /*
    * Metodo para exclusao de localizacao cadastrada
    */
    public void excluirSala(String nome){
        for(String sala : salas){
            if(nome.equals(this.nomeSala)){
                salas.remove(sala);
            }
            System.out.println("Item nao existe");
        }
    }
    /*
     * Metodo para trocar a localizacao cadastrada de um bem
     */
    public void movimentarBem(ArrayList<String> tipoBens, int Codigo, String novaLocalizacao){
        for(String sala : salas){
            if(novaLocalizacao.equals(this.nomeSala)){
                for(String tipobem : tipoBens) {
                    if (Codigo == this.getCodigo()) {
                        tipoBens.remove(tipobem);
                        tipoBens.add(novaLocalizacao);
                    }
                }
            }
        }
    }
    /*
     * Metodo para criar relatorio por localizacao em seguida por categoria
     * !!!Falta ordenacao alfabetica
     */
    public void relatorioLocalizacaoCategoria(){
        for(String sala : salas){
                System.out.println(sala);
                    this.relatorioCategoria();
                }
            }
        }
    }
}
