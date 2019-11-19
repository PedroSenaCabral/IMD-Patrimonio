package MetropoleBot;


import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

class Bens
{
    private ArrayList<Bem> bens;

    Bens()
    {
        bens = new ArrayList<>();
    }

    /**
     * @param bem         bem a ser cadastrado
     * @param idCategoria categoria a qual o bem pertence
     * @param idLocal     local ao qual o bem pertence
     * @param con         conexao com o banco
     * @return true se conseguir cadastrar o bem, false se nao
     * @throws SQLException tratamento basico de excecao
     */
    boolean cadastrar(Bem bem, String idCategoria, String idLocal, Connection con) throws SQLException
    {

        if(find(bem, con))
        {
            this.bens.add(bem);

            String sql = "INSERT INTO bens (codigo, nome, descricao, idLocalizacao, idCategoria) values(?, ?, ?, ?, ?)";

            PreparedStatement stmt = con.prepareStatement(sql);

            stmt.setString(1, bem.getCodigo());
            stmt.setString(2, bem.getNome());
            stmt.setString(3, bem.getDescricao());
            stmt.setString(4, idLocal);
            stmt.setString(5, idCategoria);

            stmt.executeUpdate();

            return true;
        }

        else
            return false;
    }

    /**
     * @param bem bem a ser procurado no banco
     * @param con conexao com o banco
     * @return retorna true se existe, falso se nao
     * @throws SQLException tratamento basico de excecao
     */
    private boolean find(Bem bem, Connection con) throws SQLException
    {
        String sql = "SELECT id FROM bens WHERE codigo = ?";

        PreparedStatement stmt = con.prepareStatement(sql);

        stmt.setString(1, bem.getCodigo());

        ResultSet result = stmt.executeQuery();

        return !result.next();
    }

    /**
     * Lista os bens de uma localizacao
     *
     * @param con   conexao com o banco
     * @param local local onde o bem esta
     * @return retorna uma lista de bens
     */
    ResultSet listFromLoc(Connection con, String local) throws SQLException
    {
        String sql = "SELECT bens.nome FROM bens INNER JOIN locais l on bens.idLocalizacao = l.id where l.nome = ?";

        PreparedStatement stmt = con.prepareStatement(sql);

        stmt.setString(1, local);

        return stmt.executeQuery();
    }

    /**
     * Procura bens por codigo
     *
     * @param con    conexao com o banco
     * @param codigo codigo do bem a ser procurado
     * @return retorna uma lista de bens com o codigo informado
     * @throws SQLException tratamento basico de excecao
     */
    ResultSet findByCode(Connection con, String codigo) throws SQLException
    {
        String sql = "SELECT * FROM bens where codigo = ?";

        PreparedStatement stmt = con.prepareStatement(sql);

        stmt.setString(1, codigo);

        return stmt.executeQuery();
    }

    /**
     * Procura bens por nome
     *
     * @param con  conexao com o banco
     * @param nome nome do bem a ser procurado
     * @return retorna uma lista de bens com o nome informado
     * @throws SQLException tratamento basico de excecao
     */
    ResultSet findByName(Connection con, String nome) throws SQLException
    {
        String sql = "SELECT * FROM bens where nome = ?";

        PreparedStatement stmt = con.prepareStatement(sql);

        stmt.setString(1, nome);

        return stmt.executeQuery();
    }

    ResultSet findByDesc(Connection con, String descricao) throws SQLException
    {
        String sql = "SELECT * FROM bens where descricao = ?";

        PreparedStatement stmt = con.prepareStatement(sql);

        stmt.setString(1, descricao);

        return stmt.executeQuery();
    }

    boolean moveItem(Connection con, String code, String local) throws SQLException
    {
        String sql = "UPDATE bens b SET b.idLocalizacao = (SELECT id FROM locais where locais.nome = ?) where b.codigo = ?";

        PreparedStatement stmt = con.prepareStatement(sql);

        stmt.setString(1, local);
        stmt.setString(2, code);

        return stmt.executeUpdate() > 0;
    }


    /**
     * Executa 3 querys, uma para pegar os locais ativos no banco, uma para pegar as categorias ativas no banco e a ultima para pegar os bens cadastrados
     *
     * @param con conexao com o banco
     * @return retorna um relatorio contendo os locais ativos, categorias ativas e os bens cadastrados
     * @throws SQLException tratamento basico de excecao
     */
    StringBuilder generateReport(Connection con) throws SQLException
    {
        StringBuilder report = new StringBuilder();

        String bensOrdered = "SELECT bens.codigo, bens.nome, bens.descricao, l.nome, l.descricao, c.nome, c.descricao\n" +
                "FROM bens INNER JOIN locais l on bens.idLocalizacao = l.id INNER JOIN categorias c on bens.idCategoria = c.id " +
                "ORDER BY l.nome, c.nome, bens.nome";

        PreparedStatement stmt = con.prepareStatement(bensOrdered);


        ResultSet result = stmt.executeQuery();

        report.append("\nBens cadastrados no banco:\n\n");

        while(result.next())
        {
            report.append("|código: ");
            report.append(result.getString("bens.codigo"));
            report.append(", ");
            report.append("nome: ");
            report.append(result.getString("bens.nome"));
            report.append(", ");
            report.append("descrição: ");
            report.append(result.getString("bens.descricao"));
            report.append("\n");
            report.append("localização: ");
            report.append(result.getString("l.nome"));
            report.append(", ");
            report.append("descrição localização: ");
            report.append(result.getString("l.descricao"));
            report.append("\n");
            report.append("categoria: ");
            report.append(result.getString("c.nome"));
            report.append(", ");
            report.append("descrição categoria: ");
            report.append(result.getString("c.descricao"));
            report.append(" | ");

            report.append("\n\n");
        }

        report.append("\n");

        return report;
    }
}
