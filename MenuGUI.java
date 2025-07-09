import javax.swing.*;

import java.awt.Color;
import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDType0Font;
import java.io.InputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
public class MenuGUI extends JFrame {
    static final String JDBC_DRIVER = "com.mysql.cj.jdbc.Driver";
    static final String DB_URL = "jdbc:mysql://localhost/MySQL";
    static final String USER = "root";
    static final String PASS = "1a2w3d4r";

    public MenuGUI() {
        setTitle("Menu");
        setSize(400, 300);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JPanel panel = new JPanel();
        panel.setLayout(null);

        JButton emitirNotaButton = new JButton("Emitir Nota");
        emitirNotaButton.setBounds(50, 30, 120, 30);
        panel.add(emitirNotaButton);

        JButton cadastrarMotoristaButton = new JButton("Cadastrar Motorista");
        cadastrarMotoristaButton.setBounds(200, 30, 150, 30);
        panel.add(cadastrarMotoristaButton);

        JButton buscarMotoristaButton = new JButton("Buscar Motorista");
        buscarMotoristaButton.setBounds(50, 80, 120, 30);
        panel.add(buscarMotoristaButton);

        JButton buscarNotaButton = new JButton("Buscar Nota");
        buscarNotaButton.setBounds(200, 80, 120, 30);
        panel.add(buscarNotaButton);


        add(panel);

        emitirNotaButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                emitirNota();
            }
        });

        cadastrarMotoristaButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                cadastrarMotorista();
            }
        });

        buscarMotoristaButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                buscarMotorista();
            }
        });

        buscarNotaButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                buscarNota();
            }
        });

    }

  

    public static void emitirNota() {
        JFrame frame = new JFrame();
        frame.setSize(400, 400);
        frame.setTitle("Buscar Motorista que deseja adicionar a nota");
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        JPanel panel = new JPanel();
        panel.setLayout(null);

        // JLabel e JTextField para inserir o nome do motorista a ser buscado
        JLabel lblNome = new JLabel("Nome:");
        lblNome.setBounds(20, 20, 80, 25);
        panel.add(lblNome);

        JTextField txtNome = new JTextField();
        txtNome.setBounds(100, 20, 250, 25);
        panel.add(txtNome);

        // JButton para realizar a busca
        JButton btnBuscar = new JButton("Buscar");
        btnBuscar.setBounds(150, 50, 100, 25);
        panel.add(btnBuscar);

        // Painel para exibir os resultados da busca
        JPanel resultadosPanel = new JPanel();
        resultadosPanel.setLayout(new BoxLayout(resultadosPanel, BoxLayout.Y_AXIS));
        resultadosPanel.setBounds(20, 90, 360, 230);

        // JScrollPane para os resultados da busca
        JScrollPane scrollPane = new JScrollPane(resultadosPanel);
        scrollPane.setBounds(20, 90, 360, 230);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS); // Barra de rolagem sempre visível
        panel.add(scrollPane);

        // Ação do botão buscar
        btnBuscar.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String nome = txtNome.getText();

                // Realizar a busca no banco de dados
                try (Connection conn = DriverManager.getConnection(DB_URL, USER, PASS)) {
                    String query = "SELECT * FROM motorista WHERE nome LIKE ?";
                    PreparedStatement statement = conn.prepareStatement(query);
                    statement.setString(1, "%" + nome + "%");
                    ResultSet rs = statement.executeQuery();

                    // Limpar resultados anteriores
                    resultadosPanel.removeAll();

                    // Exibir os resultados na JTextArea
                    while (rs.next()) {
                    	int id = rs.getInt("id");
                        String nomeMotorista = rs.getString("nome");
                        String cpfMotorista = rs.getString("cpf");
                        String celularMotorista = rs.getString("celular");

                        // Adicionar um painel para cada resultado da busca
                        JPanel motoristaPanel = new JPanel();
                        motoristaPanel.setBorder(BorderFactory.createLineBorder(Color.BLACK));
                        motoristaPanel.setPreferredSize(new Dimension(340, 60));
                        motoristaPanel.setLayout(new GridLayout(3, 1)); // Layout com 3 linhas e 1 coluna

                        // Adicionar labels para cada informação do motorista
                        JLabel nomeLabel = new JLabel("Nome: " + nomeMotorista);
                        JLabel cpfLabel = new JLabel("CPF: " + cpfMotorista);
                        JLabel celularLabel = new JLabel("Celular: " + celularMotorista);

                        // Adicionar as labels ao painel
                        motoristaPanel.add(nomeLabel);
                        motoristaPanel.add(cpfLabel);
                        motoristaPanel.add(celularLabel);

                        // Adicionar evento de clique ao painel do motorista
                        motoristaPanel.addMouseListener(new MouseAdapter() {
                            public void mouseClicked(MouseEvent e) {
                                if (e.getClickCount() == 1) { // Verifica se foi um clique simples
                                    emitirNota2(nomeMotorista, cpfMotorista, celularMotorista);
                                }
                            }
                        });

                        resultadosPanel.add(motoristaPanel);
                    }

                    // Atualizar a interface para mostrar os novos resultados
                    resultadosPanel.revalidate();
                    resultadosPanel.repaint();
                } catch (SQLException ex) {
                    JOptionPane.showMessageDialog(null, "Erro ao buscar motorista: " + ex.getMessage());
                }
            }
        });

        frame.add(panel);
        frame.setVisible(true);
    }
    public static void emitirNota2(String nome, String cpf, String celular) {
        JFrame frame = new JFrame();
        frame.setSize(600, 600);
        frame.setTitle("Emitir Nota");
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        JPanel panel = new JPanel();
        panel.setLayout(null);

        // Componentes para coletar informações da nota
        JLabel lblNomeComprador = new JLabel("Nome do Comprador:");
        lblNomeComprador.setBounds(20, 20, 150, 25);
        panel.add(lblNomeComprador);

        JTextField txtNomeComprador = new JTextField();
        txtNomeComprador.setBounds(200, 20, 150, 25);
        panel.add(txtNomeComprador);

        JLabel lblProdutor = new JLabel("Produtor:");
        lblProdutor.setBounds(20, 50, 150, 25);
        panel.add(lblProdutor);

        JTextField txtProdutor = new JTextField();
        txtProdutor.setBounds(200, 50, 150, 25);
        panel.add(txtProdutor);

        JLabel lblData = new JLabel("Data:");
        lblData.setBounds(20, 80, 150, 25);
        panel.add(lblData);

        JTextField txtData = new JTextField();
        txtData.setBounds(200, 80, 150, 25);
        panel.add(txtData);

        JLabel lblANTT = new JLabel("ANTT:");
        lblANTT.setBounds(20, 110, 150, 25);
        panel.add(lblANTT);

        JTextField txtANTT = new JTextField();
        txtANTT.setBounds(200, 110, 150, 25);
        panel.add(txtANTT);

        JLabel lblPlaca = new JLabel("Placa:");
        lblPlaca.setBounds(20, 140, 150, 25);
        panel.add(lblPlaca);

        JTextField txtPlaca = new JTextField();
        txtPlaca.setBounds(200, 140, 150, 25);
        panel.add(txtPlaca);

        JLabel lblRenavam = new JLabel("Renavam:");
        lblRenavam.setBounds(20, 170, 150, 25);
        panel.add(lblRenavam);

        JTextField txtRenavam = new JTextField();
        txtRenavam.setBounds(200, 170, 150, 25);
        panel.add(txtRenavam);

        JLabel lblProprietario = new JLabel("Proprietário:");
        lblProprietario.setBounds(20, 200, 150, 25);
        panel.add(lblProprietario);

        JTextField txtProprietario = new JTextField();
        txtProprietario.setBounds(200, 200, 150, 25);
        panel.add(txtProprietario);

        JLabel lblTipoDocumento = new JLabel("Documento do Proprietário:");
        lblTipoDocumento.setBounds(20, 230, 180, 25);
        panel.add(lblTipoDocumento);

        String[] tiposDocumento = {"CPF", "CNPJ"};
        JComboBox<String> comboTipoDocumento = new JComboBox<>(tiposDocumento);
        comboTipoDocumento.setBounds(200, 230, 70, 25);
        panel.add(comboTipoDocumento);

        JTextField txtDocumentoProprietario = new JTextField();
        txtDocumentoProprietario.setBounds(280, 230, 120, 25);
        panel.add(txtDocumentoProprietario);

        JLabel lblModelo = new JLabel("Modelo do Caminhão:");
        lblModelo.setBounds(20, 260, 150, 25);
        panel.add(lblModelo);

        JTextField txtModelo = new JTextField();
        txtModelo.setBounds(200, 260, 150, 25);
        panel.add(txtModelo);

        JLabel lblCor = new JLabel("Cor:");
        lblCor.setBounds(20, 290, 150, 25);
        panel.add(lblCor);

        JTextField txtCor = new JTextField();
        txtCor.setBounds(200, 290, 150, 25);
        panel.add(txtCor);

        JLabel lblCidade = new JLabel("Cidade:");
        lblCidade.setBounds(20, 320, 150, 25);
        panel.add(lblCidade);

        JTextField txtCidade = new JTextField();
        txtCidade.setBounds(200, 320, 150, 25);
        panel.add(txtCidade);

        JLabel lblValorFrete = new JLabel("Valor do Frete:");
        lblValorFrete.setBounds(20, 350, 150, 25);
        panel.add(lblValorFrete);

        JTextField txtValorFrete = new JTextField();
        txtValorFrete.setBounds(200, 350, 150, 25);
        panel.add(txtValorFrete);

        JLabel lblDestino = new JLabel("Destino:");
        lblDestino.setBounds(20, 380, 150, 25);
        panel.add(lblDestino);

        JTextField txtDestino = new JTextField();
        txtDestino.setBounds(200, 380, 150, 25);
        panel.add(txtDestino);

        JLabel lblComissao = new JLabel("Comissão:");
        lblComissao.setBounds(20, 410, 150, 25);
        panel.add(lblComissao);

        JTextField txtComissao = new JTextField();
        txtComissao.setBounds(200, 410, 150, 25);
        panel.add(txtComissao);

        JLabel lblDespesas = new JLabel("Despesas:");
        lblDespesas.setBounds(20, 440, 150, 25);
        panel.add(lblDespesas);

        JTextField txtDespesas = new JTextField();
        txtDespesas.setBounds(200, 440, 150, 25);
        panel.add(txtDespesas);

        JLabel lblObservacoes = new JLabel("Observações:");
        lblObservacoes.setBounds(20, 470, 150, 25);
        panel.add(lblObservacoes);

        JTextField txtObservacoes = new JTextField();
        txtObservacoes.setBounds(200, 470, 150, 25);
        panel.add(txtObservacoes);

        // Botão para salvar a nota
        JButton btnSalvar = new JButton("Salvar");
        btnSalvar.setBounds(250, 510, 100, 30);
        panel.add(btnSalvar);

        // Ação do botão de salvar
        btnSalvar.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                // Obter os valores dos campos
                String nomeComprador = txtNomeComprador.getText();
                String produtor = txtProdutor.getText();
                String data = txtData.getText();
                String antt = txtANTT.getText();
                String placa = txtPlaca.getText();
                String renavam = txtRenavam.getText();
                String proprietario = txtProprietario.getText();
                String tipoDocumento = (String) comboTipoDocumento.getSelectedItem();
                String documentoProprietario = txtDocumentoProprietario.getText();
                String modelo = txtModelo.getText();
                String cor = txtCor.getText();
                String cidade = txtCidade.getText();
                String valorFrete = txtValorFrete.getText();
                String destino = txtDestino.getText();
                String comissao = txtComissao.getText();
                String despesas = txtDespesas.getText();
                String observacoes = txtObservacoes.getText();
                // Obtenha os outros valores dos campos de entrada conforme necessário

                // Salvar no banco de dados
                try (Connection conn = DriverManager.getConnection(DB_URL, USER, PASS)) {
                    String query = "INSERT INTO notas (nome_comprador, produtor, data, antt, motorista_nome, motorista_cpf, motorista_celular, placa, renavam, proprietario,tipo_documento, documento_proprietario, modelo, cor, cidade, valor_frete, destino, "
                    		+ "comissao, despesas, observacoes) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
                    PreparedStatement statement = conn.prepareStatement(query);
                    statement.setString(1, nomeComprador);
                    statement.setString(2, produtor);
                    statement.setString(3, data);
                    statement.setString(4, antt);
                    statement.setString(5, nome);
                    statement.setString(6, cpf);
                    statement.setString(7, celular);
                    statement.setString(8, placa);
                    statement.setString(9, renavam);
                    statement.setString(10, proprietario);
                    statement.setString(11, tipoDocumento);
                    statement.setString(12, documentoProprietario);
                    statement.setString(13, modelo);
                    statement.setString(14, cor);
                    statement.setString(15, cidade);
                    statement.setString(16, valorFrete);
                    statement.setString(17, destino);
                    statement.setString(18, comissao);
                    statement.setString(19, despesas);
                    statement.setString(20, observacoes);
                    int rowsInserted = statement.executeUpdate();
                    gerarPDF(nomeComprador, produtor, data, antt, nome, cpf, celular, placa, renavam, proprietario, tipoDocumento, documentoProprietario, modelo, cor, cidade, valorFrete, destino, comissao, despesas,observacoes);
                    if (rowsInserted > 0) {
                        JOptionPane.showMessageDialog(null, "Nota emitida com sucesso!");
                    } else {
                        JOptionPane.showMessageDialog(null, "Falha ao emitir a nota.");
                    }
                } catch (SQLException ex) {
                    JOptionPane.showMessageDialog(null, "Erro ao emitir a nota: " + ex.getMessage());
                }
            }
        });

        frame.add(panel);
        frame.setVisible(true);
    }

    public static void cadastrarMotorista() {
        JFrame frame = new JFrame();
        frame.setSize(400, 300);
        frame.setTitle("Cadastrar Motorista");
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        JPanel panel = new JPanel();
        panel.setLayout(null);

        // JLabels
        JLabel lblNome = new JLabel("Nome:");
        lblNome.setBounds(20, 20, 80, 25);
        panel.add(lblNome);

        JLabel lblCPF = new JLabel("CPF:");
        lblCPF.setBounds(20, 50, 80, 25);
        panel.add(lblCPF);

        JLabel lblCelular = new JLabel("Celular:");
        lblCelular.setBounds(20, 80, 80, 25);
        panel.add(lblCelular);

        // JTextFields
        JTextField txtNome = new JTextField();
        txtNome.setBounds(100, 20, 250, 25);
        panel.add(txtNome);

        JTextField txtCPF = new JTextField();
        txtCPF.setBounds(100, 50, 250, 25);
        panel.add(txtCPF);

        JTextField txtCelular = new JTextField();
        txtCelular.setBounds(100, 80, 250, 25);
        panel.add(txtCelular);

        // JButton
        JButton btnCadastrar = new JButton("Cadastrar");
        btnCadastrar.setBounds(150, 120, 100, 25);
        panel.add(btnCadastrar);

        // Ação do botão cadastrar
        btnCadastrar.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                // Obter os dados inseridos pelo usuário
                String nome = txtNome.getText();
                String cpf = txtCPF.getText();
                String celular = txtCelular.getText();

                // Realizar a inserção no banco de dados
                try (Connection conn = DriverManager.getConnection(DB_URL, USER, PASS)) {
                    String query = "INSERT INTO motorista (nome, cpf, celular) VALUES (?, ?, ?)";
                    PreparedStatement statement = conn.prepareStatement(query);
                    statement.setString(1, nome);
                    statement.setString(2, cpf);
                    statement.setString(3, celular);
                    statement.executeUpdate();
                    JOptionPane.showMessageDialog(null, "Motorista cadastrado com sucesso!");
                } catch (SQLException ex) {
                    JOptionPane.showMessageDialog(null, "Erro ao cadastrar motorista: " + ex.getMessage());
                }
            }
        });

        frame.add(panel);
        frame.setVisible(true);
    }

    public static void buscarMotorista() {
        JFrame frame = new JFrame();
        frame.setSize(400, 400);
        frame.setTitle("Buscar Motorista");
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        JPanel panel = new JPanel();
        panel.setLayout(null);

        // JLabel e JTextField para inserir o nome do motorista a ser buscado
        JLabel lblNome = new JLabel("Nome:");
        lblNome.setBounds(20, 20, 80, 25);
        panel.add(lblNome);

        JTextField txtNome = new JTextField();
        txtNome.setBounds(100, 20, 250, 25);
        panel.add(txtNome);

        // JButton para realizar a busca
        JButton btnBuscar = new JButton("Buscar");
        btnBuscar.setBounds(150, 50, 100, 25);
        panel.add(btnBuscar);

        // Painel para exibir os resultados da busca
        JPanel resultadosPanel = new JPanel();
        resultadosPanel.setLayout(new BoxLayout(resultadosPanel, BoxLayout.Y_AXIS));
        resultadosPanel.setBounds(20, 90, 360, 230);

        // JScrollPane para os resultados da busca
        JScrollPane scrollPane = new JScrollPane(resultadosPanel);
        scrollPane.setBounds(20, 90, 360, 230);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS); // Barra de rolagem sempre visível
        panel.add(scrollPane);

        // Ação do botão buscar
        btnBuscar.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String nome = txtNome.getText();

                // Realizar a busca no banco de dados
                try (Connection conn = DriverManager.getConnection(DB_URL, USER, PASS)) {
                    String query = "SELECT * FROM motorista WHERE nome LIKE ?";
                    PreparedStatement statement = conn.prepareStatement(query);
                    statement.setString(1, "%" + nome + "%");
                    ResultSet rs = statement.executeQuery();

                    // Limpar resultados anteriores
                    resultadosPanel.removeAll();

                    // Exibir os resultados na JTextArea
                    while (rs.next()) {
                    	int id = rs.getInt("id");
                        String nomeMotorista = rs.getString("nome");
                        String cpfMotorista = rs.getString("cpf");
                        String celularMotorista = rs.getString("celular");

                        // Adicionar um painel para cada resultado da busca
                        JPanel motoristaPanel = new JPanel();
                        motoristaPanel.setBorder(BorderFactory.createLineBorder(Color.BLACK));
                        motoristaPanel.setPreferredSize(new Dimension(340, 60));
                        motoristaPanel.setLayout(new GridLayout(3, 1)); // Layout com 3 linhas e 1 coluna

                        // Adicionar labels para cada informação do motorista
                        JLabel nomeLabel = new JLabel("Nome: " + nomeMotorista);
                        JLabel cpfLabel = new JLabel("CPF: " + cpfMotorista);
                        JLabel celularLabel = new JLabel("Celular: " + celularMotorista);

                        // Adicionar as labels ao painel
                        motoristaPanel.add(nomeLabel);
                        motoristaPanel.add(cpfLabel);
                        motoristaPanel.add(celularLabel);

                        // Adicionar evento de clique ao painel do motorista
                        motoristaPanel.addMouseListener(new MouseAdapter() {
                            public void mouseClicked(MouseEvent e) {
                                if (e.getClickCount() == 1) { // Verifica se foi um clique simples
                                    mostrarOpcoesEditarExcluir1(nomeMotorista, cpfMotorista, celularMotorista, id);
                                }
                            }
                        });

                        resultadosPanel.add(motoristaPanel);
                    }

                    // Atualizar a interface para mostrar os novos resultados
                    resultadosPanel.revalidate();
                    resultadosPanel.repaint();
                } catch (SQLException ex) {
                    JOptionPane.showMessageDialog(null, "Erro ao buscar motorista: " + ex.getMessage());
                }
            }
        });

        frame.add(panel);
        frame.setVisible(true);
    }


    public static void mostrarOpcoesEditarExcluir1(String nome, String cpf, String celular, int id) {
        JFrame frame = new JFrame("Opções");
        frame.setSize(400, 250);
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        JPanel panel = new JPanel();
        panel.setLayout(null);

        // JLabels para mostrar as informações do motorista selecionado
        JLabel lblNome = new JLabel("Nome: " + nome);
        lblNome.setBounds(20, 10, 260, 25);
        panel.add(lblNome);

        JLabel lblCPF = new JLabel("CPF: " + cpf);
        lblCPF.setBounds(20, 35, 160, 25);
        panel.add(lblCPF);

        JLabel lblCelular = new JLabel("Celular: " + celular);
        lblCelular.setBounds(20, 60, 160, 25);
        panel.add(lblCelular);

        JButton btnEditar = new JButton("Editar");
        btnEditar.setBounds(20, 90, 80, 25);
        panel.add(btnEditar);

        JButton btnExcluir = new JButton("Excluir");
        btnExcluir.setBounds(100, 90, 80, 25);
        panel.add(btnExcluir);

        btnEditar.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Criar uma nova janela para editar as informações do motorista
                JFrame editarFrame = new JFrame("Editar Motorista");
                editarFrame.setSize(300, 200);
                editarFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

                JPanel editarPanel = new JPanel();
                editarPanel.setLayout(new GridLayout(4, 2));

                JLabel lblNome = new JLabel("Novo Nome:");
                JTextField txtNovoNome = new JTextField();
                JLabel lblCPF = new JLabel("Novo CPF:");
                JTextField txtNovoCPF = new JTextField();
                JLabel lblCelular = new JLabel("Novo Celular:");
                JTextField txtNovoCelular = new JTextField();

                editarPanel.add(lblNome);
                editarPanel.add(txtNovoNome);
                editarPanel.add(lblCPF);
                editarPanel.add(txtNovoCPF);
                editarPanel.add(lblCelular);
                editarPanel.add(txtNovoCelular);

                JButton btnSalvar = new JButton("Salvar");
                btnSalvar.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        // Obter os novos valores
                        String novoNome = txtNovoNome.getText();
                        String novoCPF = txtNovoCPF.getText();
                        String novoCelular = txtNovoCelular.getText();

                        // Atualizar os dados no banco de dados
                        try (Connection conn = DriverManager.getConnection(DB_URL, USER, PASS)) {
                            String query = "UPDATE motorista SET nome = ?, cpf = ?, celular = ? WHERE id = ?";
                            PreparedStatement statement = conn.prepareStatement(query);
                            statement.setString(1, novoNome);
                            statement.setString(2, novoCPF);
                            statement.setString(3, novoCelular);
                            statement.setInt(4, id); // Usando o nome original como condição de atualização
                            int rowsUpdated = statement.executeUpdate();

                            if (rowsUpdated > 0) {
                                JOptionPane.showMessageDialog(null, "Dados do motorista atualizados com sucesso!");
                            } else {
                                JOptionPane.showMessageDialog(null, "Falha ao atualizar os dados do motorista.");
                            }

                            // Fechar a janela de edição após a conclusão
                            editarFrame.dispose();
                        } catch (SQLException ex) {
                            JOptionPane.showMessageDialog(null, "Erro ao atualizar os dados do motorista: " + ex.getMessage());
                        }
                    }
                });

                editarPanel.add(btnSalvar);

                editarFrame.add(editarPanel);
                editarFrame.setVisible(true);
            }
        });

        btnExcluir.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int opcao = JOptionPane.showConfirmDialog(null, "Tem certeza que deseja excluir este motorista?", "Confirmar exclusão", JOptionPane.YES_NO_OPTION);
                if (opcao == JOptionPane.YES_OPTION) {
                    // Confirmado pelo usuário, proceder com a exclusão
                    try (Connection conn = DriverManager.getConnection(DB_URL, USER, PASS)) {
                        String query = "DELETE FROM motorista WHERE id = ?";
                        PreparedStatement statement = conn.prepareStatement(query);
                        statement.setInt(1, id); 
                        int rowsDeleted = statement.executeUpdate();

                        if (rowsDeleted > 0) {
                            JOptionPane.showMessageDialog(null, "Motorista excluído com sucesso!");
                        } else {
                            JOptionPane.showMessageDialog(null, "Falha ao excluir o motorista.");
                        }

                        // Fechar a janela após a exclusão
                        frame.dispose();
                    } catch (SQLException ex) {
                        JOptionPane.showMessageDialog(null, "Erro ao excluir o motorista: " + ex.getMessage());
                    }
                }
            }
        });
        frame.add(panel);
        frame.setVisible(true);
    }

    public static void buscarNota() {
        JFrame frame = new JFrame();
        frame.setSize(500, 500);
        frame.setTitle("Buscar Notas");
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        JPanel panel = new JPanel();
        panel.setLayout(null);

        // JLabel e JTextField para inserir o nome do motorista a ser buscado
        JLabel lblNomeMotorista = new JLabel("Nome do Motorista:");
        lblNomeMotorista.setBounds(20, 20, 150, 25);
        panel.add(lblNomeMotorista);

        JTextField txtNomeMotorista = new JTextField();
        txtNomeMotorista.setBounds(150, 20, 200, 25);
        panel.add(txtNomeMotorista);

        // JButton para realizar a busca
        JButton btnBuscar = new JButton("Buscar");
        btnBuscar.setBounds(150, 50, 100, 25);
        panel.add(btnBuscar);

        // Painel para exibir os resultados da busca
        JPanel resultadosPanel = new JPanel();
        resultadosPanel.setLayout(new BoxLayout(resultadosPanel, BoxLayout.Y_AXIS));
        resultadosPanel.setBounds(20, 90, 460, 350); // Ajuste o tamanho do painel de resultados

        // JScrollPane para os resultados da busca
        JScrollPane scrollPane = new JScrollPane(resultadosPanel);
        scrollPane.setBounds(20, 90, 460, 350); // Ajuste o tamanho do JScrollPane
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        panel.add(scrollPane);

        // Ação do botão buscar
        btnBuscar.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String nomeMotorista = txtNomeMotorista.getText();

                // Realizar a busca no banco de dados
                try (Connection conn = DriverManager.getConnection(DB_URL, USER, PASS)) {
                    String query = "SELECT * FROM notas WHERE motorista_nome LIKE ?";
                    PreparedStatement statement = conn.prepareStatement(query);
                    statement.setString(1, "%" + nomeMotorista + "%");
                    ResultSet rs = statement.executeQuery();

                    // Limpar resultados anteriores
                    resultadosPanel.removeAll();

                    // Exibir os resultados na JTextArea
                    while (rs.next()) {
                        int id = rs.getInt("id");
                        String nomeComprador = rs.getString("nome_comprador");
                        String produtor = rs.getString("produtor");
                        String data = rs.getString("data");
                        String antt = rs.getString("antt");
                        String nome = rs.getString("motorista_nome");
                        String cpf = rs.getString("motorista_cpf");
                        String celular = rs.getString("motorista_celular");
                        String placa = rs.getString("placa");
                        String renavam = rs.getString("renavam");
                        String proprietario = rs.getString("proprietario");
                        String tipoDocumento = rs.getString("tipo_documento");
                        String documentoProprietario = rs.getString("documento_proprietario");
                        String modelo = rs.getString("modelo");
                        String cor = rs.getString("cor");
                        String cidade = rs.getString("cidade");
                        String valorFrete = rs.getString("valor_frete");
                        String destino = rs.getString("destino");
                        String comissao = rs.getString("comissao");
                        String despesas = rs.getString("despesas");
                        String observacoes = rs.getString("observacoes");

                        // Adicionar um painel para cada resultado da busca
                        JPanel notaPanel = new JPanel();
                        notaPanel.setBorder(BorderFactory.createLineBorder(Color.BLACK));
                        notaPanel.setPreferredSize(new Dimension(440, 250)); // Ajuste as dimensões do painel da nota
                        notaPanel.setLayout(new GridLayout(19, 1)); // Layout com 19 linhas e 1 coluna

                        // Adicionar labels para cada informação da nota
                        JLabel nomeLabel = new JLabel("Nome do Comprador: " + nomeComprador); // Use o nome pesquisado, não o do banco de dados
                        JLabel produtorLabel = new JLabel("Produtor: " + produtor);
                        JLabel quantidadeFrutaLabel = new JLabel("Data: " + data);
                        JLabel mediaLabel = new JLabel("ANTT: " + antt);
                        JLabel nomeMotoristaLabel = new JLabel("Nome do motorista: " + nome);
                        JLabel cpfLabel = new JLabel("CPF motorista: " + cpf);
                        JLabel celularLabel = new JLabel("Celular motorista: " + celular);
                        JLabel placaLabel = new JLabel("Placa: " + placa);
                        JLabel renavamLabel = new JLabel("Renavam: " + renavam);
                        JLabel proprietarioLabel = new JLabel("Proprietário: " + proprietario);
                        JLabel proprietarioCPFLabel = new JLabel(tipoDocumento + " do Proprietário: " + documentoProprietario);
                        JLabel modeloLabel = new JLabel("Modelo do Caminhão: " + modelo);
                        JLabel corLabel = new JLabel("Cor: " + cor);
                        JLabel cidadeLabel = new JLabel("Cidade: " + cidade);
                        JLabel valorFreteLabel = new JLabel("Valor do Frete: " + valorFrete);
                        JLabel destinoLabel = new JLabel("Destino: " + destino);
                        JLabel comissaoLabel = new JLabel("Comissão: " + comissao);
                        JLabel despesasLabel = new JLabel("Despesas: " + despesas);
                        JLabel observacoesLabel = new JLabel("Observações: " + observacoes);

                        // Adicionar as labels ao painel
                        notaPanel.add(nomeLabel);
                        notaPanel.add(produtorLabel);
                        notaPanel.add(quantidadeFrutaLabel);
                        notaPanel.add(mediaLabel);
                        notaPanel.add(nomeMotoristaLabel);
                        notaPanel.add(cpfLabel);
                        notaPanel.add(celularLabel);
                        notaPanel.add(placaLabel);
                        notaPanel.add(renavamLabel);
                        notaPanel.add(proprietarioLabel);
                        notaPanel.add(proprietarioCPFLabel);
                        notaPanel.add(modeloLabel);
                        notaPanel.add(corLabel);
                        notaPanel.add(cidadeLabel);
                        notaPanel.add(valorFreteLabel);
                        notaPanel.add(destinoLabel);
                        notaPanel.add(comissaoLabel);
                        notaPanel.add(despesasLabel);
                        notaPanel.add(observacoesLabel);
                        notaPanel.addMouseListener(new MouseAdapter() {
                            public void mouseClicked(MouseEvent e) {
                                if (e.getClickCount() == 1) { // Verifica se foi um clique simples
                                	mostrarOpcoesEditarExcluirNota(id, nomeComprador, produtor, data, antt,
                                            nome, cpf, celular, placa, renavam, proprietario, tipoDocumento,documentoProprietario, modelo, cor, cidade,
                                            valorFrete, destino, comissao, despesas, observacoes);
                                }
                            }
                        });
                        resultadosPanel.add(notaPanel);
                    }

                    // Atualizar a interface para mostrar os novos resultados
                    resultadosPanel.revalidate();
                    resultadosPanel.repaint();
                } catch (SQLException ex) {
                    JOptionPane.showMessageDialog(null, "Erro ao buscar notas: " + ex.getMessage());
                }
            }
        });

        frame.add(panel);
        frame.setVisible(true);
    }
    public static void mostrarOpcoesEditarExcluirNota(int id, String nomeComprador, String produtor, String data, String antt,
            String motorista_nome, String motorista_cpf, String motorista_celular, String placa, String renavam, String proprietario, String tipoDocumento, String documentoProprietario, String modelo, String cor, String cidade,
            String valorFrete, String destino, String comissao, String despesas, String obs) {
        JFrame frame = new JFrame("Opções");
        frame.setSize(400, 580);
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        JPanel panel = new JPanel();
        panel.setLayout(null);

        // JLabels para mostrar as informações da nota selecionada
        JLabel lblNomeComprador = new JLabel("Nome do Comprador: " + nomeComprador);
        lblNomeComprador.setBounds(20, 10, 300, 25);
        panel.add(lblNomeComprador);

        JLabel lblProdutor = new JLabel("Produtor: " + produtor);
        lblProdutor.setBounds(20, 35, 300, 25);
        panel.add(lblProdutor);

        JLabel lblQuantidadeFruta = new JLabel("Data: " + data);
        lblQuantidadeFruta.setBounds(20, 60, 300, 25);
        panel.add(lblQuantidadeFruta);
        JLabel lblMedia = new JLabel("ANTT: " + antt);
        lblMedia.setBounds(20, 85, 300, 25);
        panel.add(lblMedia);

        JLabel lblMotoristaNome = new JLabel("Nome do Motorista: " + motorista_nome);
        lblMotoristaNome.setBounds(20, 110, 300, 25);
        panel.add(lblMotoristaNome);

        JLabel lblMotoristaCPF = new JLabel("CPF do Motorista: " + motorista_cpf);
        lblMotoristaCPF.setBounds(20, 135, 300, 25);
        panel.add(lblMotoristaCPF);

        JLabel lblMotoristaCelular = new JLabel("Celular do Motorista: " + motorista_celular);
        lblMotoristaCelular.setBounds(20, 160, 300, 25);
        panel.add(lblMotoristaCelular);

        JLabel lblPlaca = new JLabel("Placa: " + placa);
        lblPlaca.setBounds(20, 185, 300, 25);
        panel.add(lblPlaca);

        JLabel lblRenavam = new JLabel("Renavam: " + renavam);
        lblRenavam.setBounds(20, 210, 300, 25);
        panel.add(lblRenavam);

        JLabel lblProprietario = new JLabel("Proprietário: " + proprietario);
        lblProprietario.setBounds(20, 235, 300, 25);
        panel.add(lblProprietario);

        JLabel lblCPFProprietario = new JLabel(tipoDocumento + " do Proprietário: " + documentoProprietario);
        lblCPFProprietario.setBounds(20, 260, 300, 25);
        panel.add(lblCPFProprietario);

        JLabel lblModelo = new JLabel("Modelo do Caminhão: " + modelo);
        lblModelo.setBounds(20, 285, 300, 25);
        panel.add(lblModelo);

        JLabel lblCor = new JLabel("Cor: " + cor);
        lblCor.setBounds(20, 310, 300, 25);
        panel.add(lblCor);

        JLabel lblCidade = new JLabel("Cidade: " + cidade);
        lblCidade.setBounds(20, 335, 300, 25);
        panel.add(lblCidade);

        JLabel lblValorFrete = new JLabel("Valor do Frete: " + valorFrete);
        lblValorFrete.setBounds(20, 360, 300, 25);
        panel.add(lblValorFrete);

        JLabel lblDestino = new JLabel("Destino: " + destino);
        lblDestino.setBounds(20, 385, 300, 25);
        panel.add(lblDestino);

        JLabel lblComissao = new JLabel("Comissão: " + comissao);
        lblComissao.setBounds(20, 410, 300, 25);
        panel.add(lblComissao);

        JLabel lblDespesas = new JLabel("Despesas: " + despesas);
        lblDespesas.setBounds(20, 435, 300, 25);
        panel.add(lblDespesas);

        JLabel lblObs = new JLabel("Observações: " + obs);
        lblObs.setBounds(20, 460, 300, 25);
        panel.add(lblObs);

        // Adicione outros JLabels para os detalhes da nota aqui ...

        JButton btnEditar = new JButton("Editar");
        btnEditar.setBounds(20, 490, 80, 25);
        panel.add(btnEditar);

        JButton btnExcluir = new JButton("Excluir");
        btnExcluir.setBounds(110, 490, 80, 25);
        panel.add(btnExcluir);

        JButton btnGerarPDF = new JButton("Gerar PDF");
        btnGerarPDF.setBounds(200, 490, 120, 25);
        panel.add(btnGerarPDF);

        btnEditar.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Abrir uma janela de edição da nota
                JFrame editarFrame = new JFrame("Editar Nota");
                editarFrame.setSize(600, 400);
                editarFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

                // Painel para conter os componentes de edição da nota
                JPanel editarPanel = new JPanel();
                editarPanel.setLayout(new GridLayout(0, 2));

                // Campos de texto para permitir a edição dos valores da nota
                JTextField txtNomeComprador = new JTextField(nomeComprador);
                JTextField txtProdutor = new JTextField(produtor);
                JTextField txtQuantidadeFruta = new JTextField(data);
                JTextField txtMedia = new JTextField(antt);
                JTextField txtNomeMotorista = new JTextField(motorista_nome);
                JTextField txtCPF = new JTextField(motorista_cpf);
                JTextField txtCelular = new JTextField(motorista_celular);
                JTextField txtPlaca = new JTextField(placa);
                JTextField txtRenavam = new JTextField(renavam);
                JTextField txtProprietario = new JTextField(proprietario);
                JTextField txtCPFProprietario = new JTextField(documentoProprietario);
                JTextField txtModelo = new JTextField(modelo);
                JTextField txtCor = new JTextField(cor);
                JTextField txtCidade = new JTextField(cidade);
                JTextField txtValorFrete = new JTextField(valorFrete);
                JTextField txtDestino = new JTextField(destino);
                JTextField txtComissao = new JTextField(comissao);
                JTextField txtDespesas = new JTextField(despesas);
                JTextField txtObservacoes = new JTextField(obs);
                // Adicione aqui os outros campos que você deseja editar

                // Rótulos para os campos de edição
                JLabel lblNomeComprador = new JLabel("Nome do Comprador:");
                JLabel lblProdutor = new JLabel("Produtor:");
                JLabel lblQuantidadeFruta = new JLabel("Data:");
                JLabel lblMedia = new JLabel("ANTT:");
                JLabel lblNomeMotorista = new JLabel("Nome do Motorista:");
                JLabel lblCPF = new JLabel("CPF:");
                JLabel lblCelular = new JLabel("Celular:");
                JLabel lblPlaca = new JLabel("Placa:");
                JLabel lblRenavam = new JLabel("Renavam:");
                JLabel lblProprietario = new JLabel("Proprietário:");
                JLabel lblCPFProprietario = new JLabel("Documento do Proprietário:");
                JLabel lblModelo = new JLabel("Modelo do Caminhão:");
                JLabel lblCor = new JLabel("Cor:");
                JLabel lblCidade = new JLabel("Cidade:");
                JLabel lblValorFrete = new JLabel("Valor do Frete:");
                JLabel lblDestino = new JLabel("Destino:");
                JLabel lblComissao = new JLabel("Comissão:");
                JLabel lblDespesas = new JLabel("Despesas:");
                JLabel lblObservacoes = new JLabel("Observações:");
                // Adicione aqui os outros rótulos para os campos

                // Adicionando os componentes ao painel de edição
                editarPanel.add(lblNomeComprador);
                editarPanel.add(txtNomeComprador);
                editarPanel.add(lblProdutor);
                editarPanel.add(txtProdutor);
                editarPanel.add(lblQuantidadeFruta);
                editarPanel.add(txtQuantidadeFruta);
                editarPanel.add(lblMedia);
                editarPanel.add(txtMedia);
                editarPanel.add(lblNomeMotorista);
                editarPanel.add(txtNomeMotorista);
                editarPanel.add(lblCPF);
                editarPanel.add(txtCPF);
                editarPanel.add(lblCelular);
                editarPanel.add(txtCelular);
                editarPanel.add(lblPlaca);
                editarPanel.add(txtPlaca);
                editarPanel.add(lblRenavam);
                editarPanel.add(txtRenavam);
                editarPanel.add(lblProprietario);
                editarPanel.add(txtProprietario);
                editarPanel.add(lblCPFProprietario);
                editarPanel.add(txtCPFProprietario);
                editarPanel.add(lblModelo);
                editarPanel.add(txtModelo);
                editarPanel.add(lblCor);
                editarPanel.add(txtCor);
                editarPanel.add(lblCidade);
                editarPanel.add(txtCidade);
                editarPanel.add(lblValorFrete);
                editarPanel.add(txtValorFrete);
                editarPanel.add(lblDestino);
                editarPanel.add(txtDestino);
                editarPanel.add(lblComissao);
                editarPanel.add(txtComissao);
                editarPanel.add(lblDespesas);
                editarPanel.add(txtDespesas);
                editarPanel.add(lblObservacoes);
                editarPanel.add(txtObservacoes);
                // Adicione aqui os outros componentes ao painel de edição

                // Botão para salvar as alterações
                JButton btnSalvar = new JButton("Salvar");
                btnSalvar.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        // Obter os novos valores dos campos de texto
                        String novoNomeComprador = txtNomeComprador.getText();
                        String novoProdutor = txtProdutor.getText();
                        String novaQuantidadeFruta = txtQuantidadeFruta.getText();
                        String novaMedia = txtMedia.getText();
                        String novoNomeMotorista = txtNomeMotorista.getText();
                        String novoCPF = txtCPF.getText();
                        String novoCelular = txtCelular.getText();
                        String novaPlaca = txtPlaca.getText();
                        String novoRenavam = txtRenavam.getText();
                        String novoProprietario = txtProprietario.getText();
                        String novoCPFProprietario = txtCPFProprietario.getText();
                        String novoModelo = txtModelo.getText();
                        String novaCor = txtCor.getText();
                        String novaCidade = txtCidade.getText();
                        String novoValorFrete = txtValorFrete.getText();
                        String novoDestino = txtDestino.getText();
                        String novaComissao = txtComissao.getText();
                        String novasDespesas = txtDespesas.getText();
                        String novasObservacoes = txtObservacoes.getText();
                        // Obtenha aqui os outros novos valores dos campos

                        // Atualizar os valores da nota no banco de dados ou em algum objeto de dados
                        // Implemente aqui a lógica para atualizar os valores da nota
                        // Atualizar os valores da nota no banco de dados
                        try (Connection conn = DriverManager.getConnection(DB_URL, USER, PASS)) {
                            String query = "UPDATE notas SET nome_comprador=?, produtor=?, data=?, antt=?, motorista_nome=?, motorista_cpf=?, " +
                                    "motorista_celular=?, placa=?, renavam=?, proprietario=?, documento_proprietario=?, modelo=?, cor=?, cidade=?, valor_frete=?, " +
                                    "destino=?, comissao=?, despesas=?, observacoes=? WHERE id=?";
                            PreparedStatement statement = conn.prepareStatement(query);
                            statement.setString(1, novoNomeComprador);
                            statement.setString(2, novoProdutor);
                            statement.setString(3, novaQuantidadeFruta);
                            statement.setString(4, novaMedia);
                            statement.setString(5, novoNomeMotorista);
                            statement.setString(6, novoCPF);
                            statement.setString(7, novoCelular);
                            statement.setString(8, novaPlaca);
                            statement.setString(9, novoRenavam);
                            statement.setString(10, novoProprietario);
                            statement.setString(11, novoCPFProprietario);
                            statement.setString(12, novoModelo);
                            statement.setString(13, novaCor);
                            statement.setString(14, novaCidade);
                            statement.setString(15, novoValorFrete);
                            statement.setString(16, novoDestino);
                            statement.setString(17, novaComissao);
                            statement.setString(18, novasDespesas);
                            statement.setString(19, novasObservacoes);
                            statement.setInt(20, id); // Supondo que 'id' seja o identificador único da nota no banco de dados

                            int rowsUpdated = statement.executeUpdate();

                            if (rowsUpdated > 0) {
                                JOptionPane.showMessageDialog(null, "Nota atualizada com sucesso!");
                            } else {
                                JOptionPane.showMessageDialog(null, "Falha ao atualizar a nota.");
                            }

                            // Fechar a conexão e a janela de edição após salvar as alterações
                            conn.close();
                            editarFrame.dispose();
                        } catch (SQLException ex) {
                            JOptionPane.showMessageDialog(null, "Erro ao atualizar a nota: " + ex.getMessage());
                        }
                        // Fechar a janela de edição após salvar as alterações
                        editarFrame.dispose();
                    }
                });

                // Adicionando o botão de salvar ao painel de edição
                editarPanel.add(btnSalvar);

                // Adicionando o painel de edição ao quadro de edição
                editarFrame.add(editarPanel);
                editarFrame.setVisible(true);
            }
        });

        btnExcluir.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int opcao = JOptionPane.showConfirmDialog(null, "Tem certeza que deseja excluir esta nota?", "Confirmar exclusão", JOptionPane.YES_NO_OPTION);
                if (opcao == JOptionPane.YES_OPTION) {
                    // Confirmado pelo usuário, proceder com a exclusão
                    try (Connection conn = DriverManager.getConnection(DB_URL, USER, PASS)) {
                        String query = "DELETE FROM notas WHERE id = ?";
                        PreparedStatement statement = conn.prepareStatement(query);
                        statement.setInt(1, id);
                        int rowsDeleted = statement.executeUpdate();

                        if (rowsDeleted > 0) {
                            JOptionPane.showMessageDialog(null, "Nota excluída com sucesso!");
                        } else {
                            JOptionPane.showMessageDialog(null, "Falha ao excluir a nota.");
                        }

                        // Fechar a janela após a exclusão
                        frame.dispose();
                    } catch (SQLException ex) {
                        JOptionPane.showMessageDialog(null, "Erro ao excluir a nota: " + ex.getMessage());
                    }
                }
            }
        });

        btnGerarPDF.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                gerarPDF(nomeComprador, produtor, data, antt,
                        motorista_nome, motorista_cpf, motorista_celular, placa, renavam, tipoDocumento, proprietario, documentoProprietario, modelo, cor, cidade,
                        valorFrete, destino, comissao, despesas, obs);
            }
        });

        frame.add(panel);
        frame.setVisible(true);
    }
    public static void gerarPDF(String nomeComprador, String produtor, String data, String antt,
            String motorista_nome, String motorista_cpf, String motorista_celular, String placa, String renavam, String proprietario,String tipoDocumento, String documentoProprietario, String modelo, String cor, String cidade,
            String valorFrete, String destino, String comissao, String despesas, String obs) {
        PDType0Font fonteTitulo = null;
        PDDocument document = new PDDocument();
        try (InputStream inputStream = new FileInputStream("static\\OpenSans_Condensed-Bold.ttf")) {
            fonteTitulo = PDType0Font.load(document, inputStream);
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Verificar se a fonte foi carregada com sucesso
        if (fonteTitulo == null) {
            System.err.println("Erro ao carregar a fonte TrueType.");
            return;
        }

     // Tamanho do texto
        int tamanhoTitulo = 16;
        int tamanhoConteudo = 12;
        int tamanhoTitulo1 = 36;
        // Crie uma nova página
        PDPage page = new PDPage();
        document.addPage(page);
        PDRectangle pageSize = PDRectangle.A4;

        try (PDPageContentStream contentStream = new PDPageContentStream(document, page)) {
        	// Definir as dimensões do retângulo maior
        	float rectX = 50;
        	float rectY = 350;
        	float rectWidth = 520;
        	float rectHeight = 350; // Ajuste a altura para aumentar o retângulo para baixo

        	// Desenhar o retângulo maior em torno das informações
        	contentStream.addRect(rectX, rectY, rectWidth, rectHeight);
        	contentStream.stroke();

        	// Escrever o título grande "AGÊNCIA DE CARGA PESADA WEDER"
        	contentStream.beginText();
        	contentStream.setFont(fonteTitulo, tamanhoTitulo1);
        	String titulo = "WC AGÊNCIA DE CARGAS";
        	float tituloWidth = fonteTitulo.getStringWidth(titulo) / 1000f * tamanhoTitulo1;
        	float tituloX = (pageSize.getWidth() - tituloWidth) / 2; // Calcular a posição X centralizada
        	float tituloY = pageSize.getHeight() - 100; // Ajustar conforme necessário
        	contentStream.newLineAtOffset(tituloX, tituloY);
        	contentStream.showText(titulo);
        	contentStream.endText();
        	// Escrever os números de contato
        	contentStream.beginText();
        	contentStream.setFont(fonteTitulo, tamanhoConteudo);
        	contentStream.newLineAtOffset(200, 725);
        	contentStream.showText(" Weder (63) 98115-7009 | (62) 99254-8897");
        	contentStream.newLineAtOffset(0, -20);
        	contentStream.showText(" Kadu    (63) 99919-3438 | (62) 99926-5235");
        	contentStream.endText();

        	// Escrever o título "ROMANEIO DE CARREGAMENTO"
        	contentStream.beginText();
        	contentStream.setFont(fonteTitulo, tamanhoTitulo);
        	contentStream.newLineAtOffset((pageSize.getWidth() - fonteTitulo.getStringWidth("ROMANEIO DE CARREGAMENTO") / 1000f * tamanhoTitulo) / 2, 680);
        	contentStream.showText("ROMANEIO DE CARREGAMENTO");
        	contentStream.endText();

        	// Desenhar uma linha horizontal abaixo do título do romaneio
        	contentStream.moveTo(50, 670);
        	contentStream.lineTo(570, 670);
        	contentStream.stroke();

        	// Definir as dimensões dos retângulos para cada par de informações
        	float rect1X = 60; // Ajuste para posicionar dentro do retângulo maior
        	float rect2X = 360; // Ajuste para posicionar dentro do retângulo maior
        	rectY = 620; // Posição inicial do retângulo menor

        	// Informações na primeira coluna
        	contentStream.beginText();
        	contentStream.setFont(fonteTitulo, tamanhoConteudo);
        	contentStream.newLineAtOffset(rect1X + 5, rectY + 15);
        	contentStream.showText("Nome do Comprador: " + nomeComprador);
        	contentStream.newLineAtOffset(0, -30); // Ajuste para separar as linhas
        	contentStream.showText("data: " + data);
        	contentStream.newLineAtOffset(0, -30); // Ajuste para separar as linhas
        	contentStream.showText("Motorista: " + motorista_nome);
        	contentStream.newLineAtOffset(0, -30); // Ajuste para separar as linhas
        	contentStream.showText("CPF do Motorista: " + motorista_cpf);
        	contentStream.newLineAtOffset(0, -30); // Ajuste para separar as linhas
        	contentStream.showText((tipoDocumento.equals("CNPJ") ? "CNPJ do Proprietário: " : "CPF do Proprietário: ") + documentoProprietario);
        	contentStream.newLineAtOffset(0, -30); // Ajuste para separar as linhas
        	contentStream.showText("Celular do Motorista: " + motorista_celular);
        	contentStream.newLineAtOffset(0, -30); // Ajuste para separar as linhas
        	contentStream.showText("Cidade: " + cidade);
        	contentStream.newLineAtOffset(0, -30); // Ajuste para separar as linhas
        	contentStream.showText("Destino: " + destino);
        	contentStream.newLineAtOffset(0, -30); // Ajuste para separar as linhas
        	contentStream.showText("Despesas: " + despesas);
        	contentStream.newLineAtOffset(0, -30); // Ajuste para separar as linhas
        	contentStream.showText("Observações: " + obs);
        	contentStream.endText();

        	// Informações na segunda coluna
        	contentStream.beginText();
        	contentStream.setFont(fonteTitulo, tamanhoConteudo);
        	contentStream.newLineAtOffset(rect2X + 5, rectY + 15);
        	contentStream.showText("Produtor: " + produtor);
        	contentStream.newLineAtOffset(0, -30); // Ajuste para separar as linhas
        	contentStream.showText("ANTT: " + antt);
        	contentStream.newLineAtOffset(0, -30); // Ajuste para separar as linhas
        	contentStream.showText("Modelo do Caminhão: " + modelo);
        	contentStream.newLineAtOffset(0, -30); // Ajuste para separar as linhas
        	contentStream.showText("Placa do Veículo: " + placa);
        	contentStream.newLineAtOffset(0, -30); // Ajuste para separar as linhas
        	contentStream.showText("Renavam: " + renavam);
        	contentStream.newLineAtOffset(0, -30); // Ajuste para separar as linhas
        	contentStream.showText("Cor do Veículo: " + cor);
        	contentStream.newLineAtOffset(0, -30); // Ajuste para separar as linhas
        	contentStream.showText("Valor do Frete: " + valorFrete);
        	contentStream.newLineAtOffset(0, -30); // Ajuste para separar as linhas
        	contentStream.showText("Comissão: " + comissao);

        	contentStream.endText();

        } catch (IOException e) {
            e.printStackTrace();
        }
        // Criar um diretório temporário para salvar o arquivo PDF
        String diretorioTemporario = System.getProperty("java.io.tmpdir");
        String formato = "dd-MM-yyyy HH.mm.ss";
        Date dataAtual = new Date();
        SimpleDateFormat dateFormat = new SimpleDateFormat(formato);
        String dataFormatada = dateFormat.format(dataAtual);
        String nomeArquivo = motorista_nome + "_" + dataFormatada + ".pdf";
        // Caminho completo para o arquivo a ser salvo no diretório temporário
        String caminhoCompleto = diretorioTemporario + File.separator + nomeArquivo;

        // Salvar o documento no caminho especificado
        try {
            document.save(caminhoCompleto);
            System.out.println("Documento salvo em: " + caminhoCompleto);
            abrirPDF(caminhoCompleto);
        } catch (IOException e) {
            e.printStackTrace();
        }
        
    }
    private static void abrirPDF(String caminhoPDF) {
        try {
            File file = new File(caminhoPDF);
            Desktop.getDesktop().open(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        MenuGUI menu = new MenuGUI();
        menu.setVisible(true);
    }
}

