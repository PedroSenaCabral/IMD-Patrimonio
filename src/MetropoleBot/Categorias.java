package MetropoleBot;

import java.util.ArrayList;

class Categoria
{

    private ArrayList<String> categorias;
    private String nomeCategoria;

    /*
     * Construtor de categorias
     */
    Categoria(ArrayList<String> categorias)
    {
        this.categorias = categorias;
    }

    Categoria()
    {
        this.categorias = new ArrayList<>();
    }

    public void setCategorias(ArrayList<String> categorias)
    {
        this.categorias = categorias;
    }

    void setCategoria(String categoria)
    {
        this.nomeCategoria = categoria;
    }

    public ArrayList<String> getCategorias()
    {
        return categorias;
    }

    /*
     * Cria novas categorias
     */
    public void cadastrarCategoria(String categoria)
    {
        categorias.add(categoria);
    }

    /*
     * Lista categorias
     */
    public void listarCategorias()
    {
        for(String categoria: categorias)
        {
            System.out.println(categoria);
        }
    }

    /*
     * Exclui categorias por nome
     */
    public void excluirCategoria(String nome)
    {
        for(String categoria: categorias)
        {
            if(nome.equals(this.nomeCategoria))
            {
                categorias.remove(categoria);
            }
            System.out.println("Item nao existe");
        }
    }

    /*
     * Relatorio de categorias
     */
    void relatorioCategoria()
    {
        for(String categoria: categorias)
        {
            System.out.println(categoria);
        }
    }
}
