package MetropoleBot;


import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.UpdatesListener;
import com.pengrad.telegrambot.request.SendMessage;
import javafx.scene.control.TextArea;

import java.io.FileWriter;
import java.io.IOException;
import java.sql.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

class Bot
{
    /**
     * Bot criado para controlar acervo do instituto metropole digital
     *
     * @param log referencia para a area de log na interface
     * @param con conexao com o banco
     * @param bot bot criado com o token gerado pelo bot father e desenvolvido por pengrad
     * token de teste 927616899:AAFD419_B2phsx5iFtjM3hM17cvVkNTkO9Q
     * token de producao 1068564256:AAGsb9uiUmM37Vpnkrl7cQPKjVWrgKI-2NQ
     * @param locais locais do IMD
     * @param bens bens do IMD
     * @param categorias categorias dos bens
     * @param chatId id do chat atual
     * @param msg mensagem vinda do chat
     * @param now data que o bot recebe a acao
     */
    private TextArea log;
    private Connection con = null;
    private TelegramBot bot = new TelegramBot("927616899:AAFD419_B2phsx5iFtjM3hM17cvVkNTkO9Q");
    private Locais locais = new Locais();
    private Bens bens = new Bens();
    private Categorias categorias = new Categorias();
    private long chatId;
    private String msg;
    private DateTimeFormatter timeFormatter;
    private LocalDateTime now;


    //=== CONSTRUTOR ===\\


    Bot(TextArea log)
    {
        this.log = log;
        this.timeFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
    }


    //=== METODOS ===\\


    // Acao realizada apos pressionar o botao de start, inicia a conexao com o banco e logo apos o bot
    void start()
    {
        connect();

        if(con == null)
        {
            log.appendText("\n\n>>> Não foi possível connectar ao banco de dados! <<<");
            return;
        }

        listen();
    }

    // Faz o bot esperar por acoes dos usuarios
    private void listen()
    {
        // Registra atualizacoes
        bot.setUpdatesListener(updates -> {
            // Processa as atualizacoes
            updates.forEach(update -> {
                chatId = update.message().chat().id();
                msg = update.message().text();
                timeFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
                now = LocalDateTime.now();


                if(msg.contains("cadastrar localização"))
                {
                    addLocal();
                }
                else if(msg.contains("cadastrar categoria de bem"))
                {
                    addItemCat();
                }
                else if(msg.contains("cadastrar bem"))
                {
                    addItem();
                }
                else if(msg.contains("listar localizações"))
                {
                    listLocal();
                }
                else if(msg.contains("listar categorias"))
                {
                    listCat();
                }
                else if(msg.contains("listar bens de uma localização"))
                {
                    listItensFromLoc();
                }
                else if(msg.contains("buscar bem por código"))
                {
                    findItemByCode();
                }
                else if(msg.contains("buscar bem por nome"))
                {
                    findItemByName();
                }
                else if(msg.contains("buscar bem por descrição"))
                {
                    findItemByDesc();
                }
                else if(msg.contains("movimentar bem"))
                {
                    moveItem();
                }
                else if(msg.equals("gerar relatório"))
                {
                    generateReport();
                }
                else if(msg.contains("gerar relatório -arquivo"))
                {
                    generateReportInFile();
                }
                else if(msg.contains("Carregar dados"))
                {
                    // TODO
                }
                else if(msg.contains("Apagar localização"))
                {
                    deleteLocal();
                }
                else if(msg.contains("Apagar categoria")) ;
                else if(msg.contains("Apagar bem")) ;
                else if(msg.contains("-help"))
                {
                    help();
                }
                else
                    bot.execute(new SendMessage(chatId, "Comando inválido! digite -help para ver os possíveis comandos"));


                // Log
                // System.out.println("\n" + timeFormatter.format(now) + " - chatID: " + chatId + " - msg: " + msg);
                log.appendText("\n" + timeFormatter.format(now) + " - chatID: " + chatId + " - msg: " + msg);
            });

            // Retorna id da ultima atualizacao processada ou confirma todas
            return UpdatesListener.CONFIRMED_UPDATES_ALL;
        });
    }

    /**
     * Apaga a localizacao se ela nao estiver em uso
     */
    private void deleteLocal()
    {
        String localFlag = "-localização ";
        final int localIndex = msg.indexOf(localFlag) + localFlag.length();
        String local = msg.substring(localIndex);

        locais.delete(local);
    }

    /**
     * Gera um relatorio com os locais, categorias e os dados dos bens e o salva em um arquivo de nome especificado pelo usuario
     */
    private void generateReportInFile()
    {
        String fileFlag = "-arquivo ";
        final int fileIndex = msg.indexOf(fileFlag) + fileFlag.length();
        String fileName = msg.substring(fileIndex);
        FileWriter file;
        StringBuilder report;

        try
        {
            file = new FileWriter(fileName);
        }
        catch(IOException e)
        {
            bot.execute(new SendMessage(chatId, "Não foi possível abrir o arquivo!"));
            return;
        }

        try
        {
            report = bens.generateReport(con);
        }
        catch(SQLException e)
        {
            bot.execute(new SendMessage(chatId, "Não foi possível gerar o relatório!"));
            return;
        }

        try
        {
            file.append(report.toString());
        }
        catch(IOException e)
        {
            bot.execute(new SendMessage(chatId, "Não foi possível escrever no arquivo!"));
            return;
        }

        try
        {
            file.close();
        }
        catch(IOException e)
        {
            bot.execute(new SendMessage(chatId, "Não foi possível salvar o relatório!"));
            return;
        }
        bot.execute(new SendMessage(chatId, "Relatório gerado!"));
    }

    /**
     * Gera um relatório listando todos os bens agrupados por localizacao e depois por categoria e depois por nome
     * (agrupa primeiro por localização, depois por categoria e depois por nome) ordenados alfabeticamente
     */
    private void generateReport()
    {
        StringBuilder report;
        try
        {
            report = bens.generateReport(con);
        }
        catch(SQLException e)
        {
            bot.execute(new SendMessage(chatId, ">>> Não foi possível gerar o relatório! <<<"));
            return;
        }

        bot.execute(new SendMessage(chatId, report.toString()));
    }

    /**
     * Mostra para o usuario os possiveis comandos e seus argumentos
     */
    private void help()
    {
        /*TODO
        else if(msg.contains("gerar relatório -arquivo")) ;
        else if(msg.contains("Carregar dados")) ;
        else if(msg.contains("Apagar localização")) ;
        else if(msg.contains("Apagar categoria")) ;
        else if(msg.contains("Apagar bem")) ;
        */
        String comandos = "Comandos possíveis:\n" +
                "cadastrar localização -localização *** -descrição ***\n" +
                "cadastrar categoria de bem -código *** -nome *** -descrição ***\n" +
                "cadastrar bem -código *** -nome *** -descrição *** -localização *** -categoria ***\n" +
                "listar localizações\n" +
                "listar categorias\n" +
                "listar bens de uma localização -localização ***\n" +
                "buscar bem por código -código ***\n" +
                "buscar bem por nome -nome ***\n" +
                "buscar bem por descrição -descrição ***\n" +
                "movimentar bem -código *** -localização ***\n" +
                "gerar relatório\n";
        bot.execute(new SendMessage(chatId, comandos));
    }


    /**
     * Move um bem, identificado pelo seu codigo de um local salvo no banco, para outro, e trata as excecoes geradas
     */
    private void moveItem()
    {
        if(msg.contains("-código") && msg.contains("-localização"))
        {
            try
            {
                canMoveItem();
            }
            catch(SQLException e)
            {
                bot.execute(new SendMessage(chatId, e.getMessage()));
            }
        }
        else
            bot.execute(new SendMessage(chatId, "Erro ao movimentar bem " +
                    "(movimentar bem -código *** -localização ***)"));
    }

    /**
     * Move um bem de codigo dado pelo usuario, salvo no banco
     *
     * @throws SQLException tratamento basico de excecao
     */
    private void canMoveItem() throws SQLException
    {
        String codeFlag = "-código ";
        String localFlag = "-localização ";

        final int codeIndex = msg.indexOf(codeFlag) + codeFlag.length();
        final int localIndex = msg.indexOf(localFlag) + localFlag.length();

        String code = msg.substring(codeIndex, msg.indexOf(localFlag) - 1);
        String local = msg.substring(localIndex);

        if(bens.moveItem(con, code, local))
            bot.execute(new SendMessage(chatId, "Bem movido com sucesso"));
        else
            bot.execute(new SendMessage(chatId, "Erro ao mover o bem! Código ou localização inválido(s)!"));
    }


    /**
     * Lista os itens com a descricao dada pelo usuario salvos no banco e trata as excecoes geradas
     */
    private void findItemByDesc()
    {
        if(msg.contains("-descrição"))
        {
            try
            {
                canFindItemByDesc();
            }
            catch(SQLException e)
            {
                bot.execute(new SendMessage(chatId, e.getMessage()));
            }
        }
        else
            bot.execute(new SendMessage(chatId, "Erro ao buscar bem por descrição " +
                    "(buscar bem por descrição -descrição ***)"));
    }

    /**
     * Lista os itens com a descricao dada pelo usuario, salvos no banco
     *
     * @throws SQLException tratamento basico de excecao
     */
    private void canFindItemByDesc() throws SQLException
    {
        StringBuilder data = new StringBuilder();
        ArrayList<String> columnName = new ArrayList<>();
        String descFlag = "-descrição ";
        final int indexDesc = msg.indexOf(descFlag) + descFlag.length();
        String descricao = msg.substring(indexDesc);


        ResultSet result = bens.findByDesc(con, descricao);
        ResultSetMetaData metaData = result.getMetaData();

        int columnCount = metaData.getColumnCount();
        for(int i = 1; i <= columnCount; i++)
        {
            columnName.add(metaData.getColumnName(i));
        }

        if(result.next())
        {
            do
            {
                for(String s: columnName)
                {
                    data.append(s).append(" - ").append(result.getString(s)).append(", ");
                }
                data.append("\n");
            } while(result.next());

            bot.execute(new SendMessage(chatId, data.toString()));
        }
        else
            bot.execute(new SendMessage(chatId, "Não existem bens cadastrados com essa descrição"));
    }


    /**
     * Lista os itens com o nome dado pelo usuario, salvos no banco
     *
     * @throws SQLException tratamento basico de excecao
     */
    private void canFindItemByName() throws SQLException
    {
        StringBuilder data = new StringBuilder();
        ArrayList<String> columnName = new ArrayList<>();
        String nomeFlag = "-nome ";
        final int indexNome = msg.indexOf(nomeFlag) + nomeFlag.length();
        String nome = msg.substring(indexNome);


        ResultSet result = bens.findByName(con, nome);
        ResultSetMetaData metaData = result.getMetaData();

        int columnCount = metaData.getColumnCount();
        for(int i = 1; i <= columnCount; i++)
        {
            columnName.add(metaData.getColumnName(i));
        }

        if(result.next())
        {
            do
            {
                for(String s: columnName)
                {
                    data.append(s).append(" - ").append(result.getString(s)).append(", ");
                }
                data.append("\n");
            } while(result.next());

            bot.execute(new SendMessage(chatId, data.toString()));
        }
        else
            bot.execute(new SendMessage(chatId, "Não existem bens cadastrados com esse nome"));
    }

    /**
     * Lista os itens com o nome dado pelo usuario salvos no banco e trata as excecoes geradas
     */
    private void findItemByName()
    {
        if(msg.contains("-nome"))
        {
            try
            {
                canFindItemByName();
            }
            catch(SQLException e)
            {
                bot.execute(new SendMessage(chatId, e.getMessage()));
            }
        }
        else
            bot.execute(new SendMessage(chatId, "Erro ao buscar bem por nome " +
                    "(buscar bem por nome -nome ***)"));
    }

    /**
     * Lista os itens com o codigo dado pelo usuario, salvos no banco
     *
     * @throws SQLException tratamento basico de excecao
     */
    private void canFindItemByCode() throws SQLException
    {
        StringBuilder data = new StringBuilder();
        ArrayList<String> columnName = new ArrayList<>();
        String codigoFlag = "-código ";
        final int indexCodigo = msg.indexOf(codigoFlag) + codigoFlag.length();
        String codigo = msg.substring(indexCodigo);


        ResultSet result = bens.findByCode(con, codigo);
        ResultSetMetaData metaData = result.getMetaData();

        int columnCount = metaData.getColumnCount();
        for(int i = 1; i <= columnCount; i++)
        {
            columnName.add(metaData.getColumnName(i));
        }

        if(result.next())
        {
            do
            {
                for(String s: columnName)
                {
                    data.append(s).append(" - ").append(result.getString(s)).append(", ");
                }
                data.append("\n");
            } while(result.next());

            bot.execute(new SendMessage(chatId, data.toString()));
        }
        else
            bot.execute(new SendMessage(chatId, "Não existem bens cadastrados com esse código"));
    }

    /**
     * Lista os itens com o codigo dado pelo usuario salvos no banco e trata as excecoes geradas
     */
    private void findItemByCode()
    {
        if(msg.contains("-código"))
        {
            try
            {
                canFindItemByCode();
            }
            catch(SQLException e)
            {
                bot.execute(new SendMessage(chatId, e.getMessage()));
            }
        }
        else
            bot.execute(new SendMessage(chatId, "Erro ao buscar bem por código " +
                    "(buscar bem por código -código ***)"));
    }


    /**
     * Lista os itens de um local salvo no banco e trata as excecoes geradas
     */
    private void listItensFromLoc()
    {
        if(msg.contains("-localização"))
        {
            try
            {
                canListItensFromLoc();
            }
            catch(SQLException e)
            {
                bot.execute(new SendMessage(chatId, e.getMessage()));
            }
        }
        else
            bot.execute(new SendMessage(chatId, "Erro ao listar bens de uma localização " +
                    "(listar bens de uma localização -local ***)"));
    }

    /**
     * Lista os itens de um local, salvos no banco
     *
     * @throws SQLException tratamento basico de excecao
     */
    private void canListItensFromLoc() throws SQLException
    {
        StringBuilder itensFromLoc = new StringBuilder();
        String localFlag = "-localização ";
        final int indexLocal = msg.indexOf(localFlag) + localFlag.length();
        String local = msg.substring(indexLocal);


        ResultSet result = bens.listFromLoc(con, local);

        if(result.next())
        {
            do
            {
                itensFromLoc.append(result.getString("nome"));
                itensFromLoc.append("\n");
            } while(result.next());

            bot.execute(new SendMessage(chatId, itensFromLoc.toString()));
        }
        else
            bot.execute(new SendMessage(chatId, "Não existem bens cadastrados nessa localização"));
    }

    /**
     * List as categorias salvas no banco e trata as excecoes geradas
     */
    private void listCat()
    {
        try
        {
            canListCat();
        }
        catch(SQLException e)
        {
            bot.execute(new SendMessage(chatId, e.getMessage()));
        }
    }

    /**
     * Lista as categorias salvas no banco
     *
     * @throws SQLException tratamento basico de excecao
     */
    private void canListCat() throws SQLException
    {
        ResultSet result = categorias.list(con);
        StringBuilder cat = new StringBuilder();

        if(result.next())
        {
            do
            {
                cat.append(result.getString("nome"));
                cat.append("\n");
            } while(result.next());

            bot.execute(new SendMessage(chatId, cat.toString()));
        }
        else
            bot.execute(new SendMessage(chatId, "Não existem categorias cadastradas"));
    }


    /**
     * List os locais salvos no banco e trata as excecoes geradas
     */
    private void listLocal()
    {
        try
        {
            canListLocal();
        }
        catch(SQLException e)
        {
            bot.execute(new SendMessage(chatId, e.getMessage()));
        }
    }

    /**
     * Lista os locais salvos no banco
     *
     * @throws SQLException tratamento basico de excecao
     */
    private void canListLocal() throws SQLException
    {
        ResultSet result = locais.list(con);
        StringBuilder local = new StringBuilder();

        if(result.next())
        {
            do
            {
                local.append(result.getString("nome"));
                local.append("\n");
            } while(result.next());

            bot.execute(new SendMessage(chatId, local.toString()));
        }
        else
            bot.execute(new SendMessage(chatId, "Não existem localizações cadastradas"));
    }

    /**
     * Verifica se o usuario passou as informacoes corretas para o cadastro do bem, se sim tenta adicionar o local, se nao avisa que falta argumentos
     */
    private void addItem()
    {
        if(msg.contains("-código") && msg.contains("-nome") && msg.contains("-descrição") && msg.contains("-localização") && msg.contains("-categoria"))
        {
            try
            {
                if(canAddItem())
                    bot.execute(new SendMessage(chatId, "Cadastro realizado com sucesso!"));
                else
                    bot.execute(new SendMessage(chatId, "Não foi possível cadastrar o bem!"));
            }
            catch(SQLException e)
            {
                bot.execute(new SendMessage(chatId, ">>> " + e.getMessage() + " <<<"));
            }
        }
        else
            bot.execute(new SendMessage(chatId, "Erro ao cadastrar categoria de bem " +
                    "(cadastrar bem -código *** -nome *** -descrição *** -localização *** -categoria ***)"));
    }

    /**
     * Verifica se eh possivel adicionar o bem ao banco de dados
     *
     * @return true se conseguir cadastrar o bem e false caso contrario
     * @throws SQLException tratamento simples de excecao
     */
    private boolean canAddItem() throws SQLException
    {
        String codigoFlag = "-código ";
        String nomeFlag = "-nome ";
        String descricaoFlag = "-descrição ";
        String localFlag = "-localização ";
        String categoriaFlag = "-categoria ";

        final int indexCodigo = msg.indexOf(codigoFlag) + codigoFlag.length();
        final int indexNome = msg.indexOf(nomeFlag) + nomeFlag.length();
        final int indexDescricao = msg.indexOf(descricaoFlag) + descricaoFlag.length();
        final int indexLocal = msg.indexOf(localFlag) + localFlag.length();
        final int indexCategoria = msg.indexOf(categoriaFlag) + categoriaFlag.length();

        String codigo = msg.substring(indexCodigo, msg.indexOf(nomeFlag) - 1);
        String nome = msg.substring(indexNome, msg.indexOf(descricaoFlag) - 1);
        String descricao = msg.substring(indexDescricao, msg.indexOf(localFlag) - 1);
        String local = msg.substring(indexLocal, msg.indexOf(categoriaFlag) - 1);
        String categoria = msg.substring(indexCategoria);

        // Procura o local no banco de dados
        String idLocal = locais.find(local, con);

        // Se local existir
        if(idLocal != null)
        {
            // Procura categoria no banco de dados
            String idCategoria = categorias.find(categoria, con);

            // Se categoria existir
            if(idCategoria != null)
                return bens.cadastrar(new Bem(codigo, nome, descricao, local, categoria), idCategoria, idLocal, con);
            else
            {
                bot.execute(new SendMessage(chatId, "Categoria não cadastrada!"));
                return false;
            }
        }
        else
        {
            bot.execute(new SendMessage(chatId, "Local não cadastrado!"));
            return false;
        }
    }

    /**
     * Verifica se o usuario passou as informacoes corretas para o cadastro da categoria, se sim tenta adicionar o local, se nao avisa que falta argumentos
     */
    private void addItemCat()
    {
        if(msg.contains("-código") && msg.contains("-nome") && msg.contains("-descrição"))
        {
            try
            {
                if(canAddItemCat())
                    bot.execute(new SendMessage(chatId, "Cadastro realizado com sucesso!"));
                else
                    bot.execute(new SendMessage(chatId, "Categoria de bem já existe!"));
            }
            catch(SQLException e)
            {
                bot.execute(new SendMessage(chatId, ">>> " + e.getMessage() + " <<<"));
            }
        }
        else
            bot.execute(new SendMessage(chatId, "Erro ao cadastrar categoria de bem " +
                    "(cadastrar categoria de bem -código *** -nome *** -descrição ***)"));
    }

    /**
     * Verifica se a categoria do bem pode ser inserida no banco de dados
     *
     * @return true se conseguir cadastrar a categoria do bem e false caso nao
     * @throws SQLException tratamento basico de excecao
     */
    private boolean canAddItemCat() throws SQLException
    {
        String codigoFlag = "-código ";
        String nomeFlag = "-nome ";
        String descricaoFlag = "-descrição ";

        final int indexCodigo = msg.indexOf(codigoFlag) + codigoFlag.length();
        final int indexNome = msg.indexOf(nomeFlag) + nomeFlag.length();
        final int indexDescricao = msg.indexOf(descricaoFlag) + descricaoFlag.length();

        String codigo = msg.substring(indexCodigo, msg.indexOf(nomeFlag) - 1);
        String nome = msg.substring(indexNome, msg.indexOf(descricaoFlag) - 1);
        String descricao = msg.substring(indexDescricao);

        // Procura categoria no banco
        String idCategoria = categorias.find(nome, con);
        // Se categoria nao esta cadastrada
        if(idCategoria == null)
            return categorias.cadastrarCategoria(new Categoria(codigo, nome, descricao), con);
        else
            return false;
    }

    /**
     * Verifica se o usuario passou as informacoes corretas para o cadastro do local, se sim tenta adicionar o local, se nao avisa que falta argumentos
     */
    private void addLocal()
    {
        if(msg.contains("-localização") && msg.contains("-descrição"))
        {
            try
            {
                if(canAddLocal())
                    bot.execute(new SendMessage(chatId, "Cadastro realizado com sucesso!"));
                else
                    bot.execute(new SendMessage(chatId, "Localização já existe!"));
            }
            catch(SQLException e)
            {
                bot.execute(new SendMessage(chatId, ">>> " + e.getMessage() + " <<<"));
            }
        }
        else
            bot.execute(new SendMessage(chatId, "Erro ao cadastrar localização " +
                    "(cadastrar localização -localização *** -descrição ***)"));
    }

    /**
     * Verifica se eh possivel adicionar o local ao bando de dados
     *
     * @return true se conseguir cadastrar o local, false caso nao
     * @throws SQLException tratamento basico de excecao
     */
    private boolean canAddLocal() throws SQLException
    {
        String localFlag = "-localização ";
        String descricaoFlag = "-descrição ";

        final int indexLocal = msg.indexOf(localFlag) + localFlag.length();
        final int indexDescricao = msg.indexOf(descricaoFlag) + descricaoFlag.length();

        String local = msg.substring(indexLocal, msg.indexOf(descricaoFlag) - 1);
        String descricao = msg.substring(indexDescricao);

        // Procura local no banco
        String idLocal = locais.find(local, con);

        // Se local nao existe
        if(idLocal == null)
            return locais.cadastrarLocal(new Local(local, descricao), con);
        else
            return false;
    }


    /**
     * Conecta com o banco de dados
     */
    private void connect()
    {
        try
        {
            con = DriverManager.getConnection(
                    "jdbc:mysql://localhost:3306/imdPatrimonio", "root", "mysql");
        }
        catch(Exception e)
        {
            bot.execute(new SendMessage(chatId, ">>> " + e.getMessage() + " <<<"));
        }
    }
}