package lpii.bot;

import java.util.ArrayList;

public class TipoBem extends Categoria{
    private ArrayList<String> tipoBens;
    private int codigo;
    private String nome;
    private String descricao;

    /*
     * Construtor de classe TipoBem
     */
    public TipoBem(ArrayList<String> tipoBem, int codigo, String nome, String descricao) {
        super();
        tipoBens = tipoBem;
        this.codigo = codigo;
        this.nome = nome;
        this.descricao = descricao;
    }

    public void setTipoBens(ArrayList<String> tipoBens) {
        this.tipoBens = tipoBens;
    }
        
    public ArrayList<String> getTipoBens() {
        return tipoBens;
    }
    /*
     * Cadastrar Tipo de bem
     */
    public void cadastrarTipoBem(String tipobem){
        tipoBens.add(tipobem);
    }
    /*
     * Listar os bens
     */
    public void listarTipobem(){
        for(String tipobem : tipoBens) {
            System.out.println(tipobem);
        }
    }

    public int getCodigo() {
        return codigo;
    }

    public String getNome() {
        return nome;
    }

    public String getDescricao() {
        return descricao;
    }
    /*
     * Buscar Tipo de bem por codigo
     */
    public void buscarBemCodigo(int Codigo){
        for(String tipobem : tipoBens) {
           if(Codigo == this.codigo)
            System.out.println(tipobem);
        }
        System.out.println("Item nao existe");
    }
    /*
     * Buscar Tipo de bem por nome
     */
    public void buscarBemNome(String Nome){
        for(String tipobem : tipoBens) {
           if(Nome.equals(this.nome)){
               System.out.println(tipobem);
           }
        }
        System.out.println("Item nao existe");
    }
    /*
     * Buscar Tipo de bem por descricao
     */
    public void buscarBemDescricao(String Descricao){
        for(String tipobem : tipoBens) {
            if(Descricao.equals(this.descricao)){
                System.out.println(tipobem);
            }
            System.out.println("Item nao existe");
        }
    }
    /*
     * Excuir Tipo de bem por nome
     */
    public void excluirTipoBem(String Nome){
        for(String tipobem : tipoBens){
            if(Nome.equals(this.nome)){
                tipoBens.remove(tipobem);
            }
            System.out.println("Item nao existe");
        }
    }
}
