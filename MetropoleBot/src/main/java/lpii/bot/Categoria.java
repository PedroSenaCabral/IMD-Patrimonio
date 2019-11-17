package lpii.bot;

import java.util.ArrayList;

abstract class Categoria {

    private ArrayList<String> categorias;
    private String nomeCategoria;
    /*
     * Construtor de categorias
     */
    public Categoria(ArrayList<String> categorias) {
        this.categorias = categorias;
    }

    public void setCategorias(ArrayList<String> categorias) {
        this.categorias = categorias;
    }

    public ArrayList<String> getCategorias() {
        return categorias;
    }
    /*
     * Cria novas categorias
     */
    public void cadastrarCategoria(String categoria){
        categorias.add(categoria);
    }
    /*
     * Lista categorias
     */
    public void listarCategorias(){
        for(String categoria : categorias) {
            System.out.println(categoria);
        }
    }
    /*
     * Exclui categorias por nome
     */
    public void excluirCategoria(String nome){
        for(String categoria : categorias){
            if(nome.equals(this.nomeCategoria)){
                categorias.remove(categoria);
            }
            System.out.println("Item nao existe");
        }
    }
    /*
     * Relatorio de categorias
     */
    public void relatorioCategoria(){
        for(String categoria : categorias){
            System.out.println(categoria);
        }
    }
}
