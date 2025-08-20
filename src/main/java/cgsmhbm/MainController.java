package cgsmhbm;


import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.prefs.Preferences;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import java.time.format.DateTimeFormatter;





public class MainController {
	
    private static final Path CAMINHO_XLSX_PADRAO =
            Paths.get(System.getProperty("user.home"), "Documents")
                    .resolve("CGSMH")
                    .resolve("equipamentos.xlsx");
    private static final String PREFS_KEY = "ultimo_excel";
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");

	
	@FXML private TableView<Equipment> tabelaEquipamentos;
	@FXML private TableColumn<Equipment, String> colGLPI;
	@FXML private TableColumn<Equipment, String> colPatrimonio;
        @FXML private TableColumn<Equipment, String> colNumeroSerie;
        @FXML private TableColumn<Equipment, String> colDescricaoMarcaModelo;
        @FXML private TableColumn<Equipment, String> colDefeito;
	@FXML private TableColumn<Equipment, String> colBatalhao;
		@FXML private TableColumn<Equipment, String> colChamadoEmpresa;
		@FXML private TableColumn<Equipment, EquipmentStatus> colStatus;
    @FXML private TextField campoBusca;
    @FXML private ComboBox<String> comboFiltro;
    @FXML private Button btnEditar;
    @FXML private Button btnRemover;
    @FXML private Label labelInfo;


    private ObservableList<Equipment> equipamentos = FXCollections.observableArrayList();
    private FilteredList<Equipment> equipamentosFiltrados;

    @FXML
    private static int compareAlphaNumeric(String a, String b) {
    	   if (a == null && b == null) return 0;
    	    if (a == null) return -1;
    	    if (b == null) return 1;

    	    String sa = a.trim(), sb = b.trim();
    	    int ia = 0, ib = 0, na = sa.length(), nb = sb.length();

    	    while (ia < na && ib < nb) {
    	        char ca = sa.charAt(ia), cb = sb.charAt(ib);

    	        boolean da = Character.isDigit(ca);
    	        boolean db = Character.isDigit(cb);

    	        if (da && db) {
    	            // lê blocos numéricos completos
    	            int za = ia; while (za < na && sa.charAt(za) == '0') za++;
    	            int zb = ib; while (zb < nb && sb.charAt(zb) == '0') zb++;

    	            int ea = za; while (ea < na && Character.isDigit(sa.charAt(ea))) ea++;
    	            int eb = zb; while (eb < nb && Character.isDigit(sb.charAt(eb))) eb++;

    	            int lenA = ea - za;
    	            int lenB = eb - zb;

    	            // compara pelo comprimento (evita BigInteger)
    	            if (lenA != lenB) return Integer.compare(lenA, lenB);

    	            // mesmo comprimento: compara lexicograficamente a parte sem zeros
    	            int cmp = sa.regionMatches(za, sb, zb, lenA) ? 0
    	                      : sa.substring(za, ea).compareTo(sb.substring(zb, eb));
    	            if (cmp != 0) return cmp;

    	            // números iguais: desempata por número de zeros à esquerda (menos zeros < mais zeros)
    	            int zerosA = za - ia, zerosB = zb - ib;
    	            if (zerosA != zerosB) return Integer.compare(zerosA, zerosB);

    	            ia = ea; ib = eb; // avança após o bloco numérico
    	            continue;
    	        }

    	        // se um é dígito e o outro não, decide ordem (aqui: não-dígito < dígito)
    	        if (da != db) return da ? 1 : -1;

    	        // ambos não-numéricos: compara case-insensitive
    	        int cmp = Character.compare(Character.toLowerCase(ca), Character.toLowerCase(cb));
    	        if (cmp != 0) return cmp;

    	        ia++; ib++;
    	    }
    	    // prefixo comum: menor comprimento vem primeiro
    	    return Integer.compare(na - ia, nb - ib);
    	}

    @FXML 
    public void initialize() {
        criarPastaPadraoSeNecessario();
        colGLPI.setCellValueFactory(new PropertyValueFactory<>("GLPI"));
        colPatrimonio.setCellValueFactory(new PropertyValueFactory<>("patrimonio"));
        colNumeroSerie.setCellValueFactory(new PropertyValueFactory<>("numeroSerie"));
        colDescricaoMarcaModelo.setCellValueFactory(new PropertyValueFactory<>("descricaoMarcaModelo"));
        colDefeito.setCellValueFactory(new PropertyValueFactory<>("defeito"));
        colBatalhao.setCellValueFactory(new PropertyValueFactory<>("batalhao"));
        colChamadoEmpresa.setCellValueFactory(new PropertyValueFactory<>("chamadoEmpresa"));
        colStatus.setCellValueFactory(new PropertyValueFactory<>("status"));
        colGLPI.setComparator(MainController::compareAlphaNumeric);
        colPatrimonio.setComparator(MainController::compareAlphaNumeric);
        colNumeroSerie.setComparator(MainController::compareAlphaNumeric);
        colDescricaoMarcaModelo.setComparator(MainController::compareAlphaNumeric);
        colDefeito.setComparator(MainController::compareAlphaNumeric);
        colBatalhao.setComparator(MainController::compareAlphaNumeric);
        colChamadoEmpresa.setComparator(MainController::compareAlphaNumeric);

        equipamentosFiltrados = new FilteredList<>(equipamentos, e -> true);
        SortedList<Equipment> equipamentosOrdenados = new SortedList<>(equipamentosFiltrados);
        equipamentosOrdenados.comparatorProperty().bind(tabelaEquipamentos.comparatorProperty());
        tabelaEquipamentos.setItems(equipamentosOrdenados);

        comboFiltro.setItems(FXCollections.observableArrayList(
                "GLPI",
                "Patrimônio",
                "Nº de Série",
                "Cod. Chamado Empresa"
        ));
        comboFiltro.getSelectionModel().selectFirst();
        
        tabelaEquipamentos.getSelectionModel().selectedItemProperty().addListener((obs, oldSel, newSel) -> {
            if (newSel != null) {
                labelInfo.setText(String.format("Criado em: %s | Última modificação: %s", newSel.getCriadoEm(), newSel.getAtualizadoEm()));
            } else {
                labelInfo.setText("");
            }
        });

        
        var selectedStatus = javafx.beans.binding.Bindings.select(
                tabelaEquipamentos.getSelectionModel().selectedItemProperty(),
                "status"
        );

        var isFechado = javafx.beans.binding.Bindings.createBooleanBinding(
                () -> EquipmentStatus.FECHADO.equals(selectedStatus.getValue()),

                selectedStatus
        );

        btnEditar.disableProperty().bind(isFechado);
        btnRemover.disableProperty().bind(isFechado); 
        
        carregarEquipamentosDoExcel(obterCaminhoUltimoArquivo());
    }

    @FXML
    private void onBuscar() {
        String filtro = comboFiltro.getValue();
        String termo = campoBusca.getText();

        if (termo == null || termo.isBlank()) {
            equipamentosFiltrados.setPredicate(e -> true);
            return;
        }

        String termoLower = termo.toLowerCase();

        equipamentosFiltrados.setPredicate(eq -> {
            if (filtro == null) {
                return true;
            }
            return switch (filtro) {
                case "GLPI" -> eq.getGLPI() != null && eq.getGLPI().toLowerCase().contains(termoLower);
                case "Patrimônio" -> eq.getPatrimonio() != null && eq.getPatrimonio().toLowerCase().contains(termoLower);
                case "Nº de Série" -> eq.getNumeroSerie() != null && eq.getNumeroSerie().toLowerCase().contains(termoLower);
                case "Cod. Chamado Empresa" -> eq.getChamadoEmpresa() != null && eq.getChamadoEmpresa().toLowerCase().contains(termoLower);
                default -> true;
            };
        });
    }

    
    private void criarPastaPadraoSeNecessario() {
        Path arquivo = CAMINHO_XLSX_PADRAO;
        Path pasta = arquivo.getParent();
        try {
            Files.createDirectories(pasta);
            if (Files.notExists(arquivo)) {
                try (Workbook workbook = new XSSFWorkbook()) {
                    Sheet sheet = workbook.createSheet("Equipamentos");
                    Row header = sheet.createRow(0);
                    String[] headers = {"GLPI","Patrimonio","NumeroSerie","DescricaoMarcaModelo","Defeito","Batalhao","ChamadoEmpresa","Status","CriadoEm","AtualizadoEm"};
                    for (int i = 0; i < headers.length; i++) {
                        header.createCell(i).setCellValue(headers[i]);
                    }
                    try (OutputStream fos = Files.newOutputStream(arquivo)) {
                        workbook.write(fos);
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
            }
        }
    
    
    @FXML
    public void onAdicionar() {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/AddEquipmentView.fxml"));
            Parent root = fxmlLoader.load();

            AdicionarEquipamentoController controller = fxmlLoader.getController();
            
            Stage stage = new Stage();
            stage.setTitle("Adicionar Equipamento");
            stage.setScene(new Scene(root));
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.showAndWait();
            Equipment novo = controller.getEquipamentoSalvo();
            if (novo != null) {
                tabelaEquipamentos.getSelectionModel().select(novo);
                labelInfo.setText(String.format("Criado em: %s | Última modificação: %s", novo.getCriadoEm(), novo.getAtualizadoEm()));
                equipamentos.add(novo);

                salvarEquipamentosEmExcel(obterCaminhoUltimoArquivo());

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    @FXML
    private void onSobre() {
    	String conteudo = "CGSMH — Controle de Garantias da Seção de Manutenção de Hardware\n"
    					+ "Sistema para controle e gestão de garantias de equipamentos de informática da Seção Manutenção de Hardware da Brigada Militar.\n\n"
    					+ "Contato suporte: cmtec-smh@bm.rs.gov.br\n"
    					+ "Autor: Christian Heil Salsa\n"
    					+ "© 2025 Seção Manutenção de Hardware da Brigada Militar\n"
    					+ "Versão 1.3.2\n"
    					+ "Sistema de uso interno. Proibida a distribuição sem autorização.";
    	javafx.scene.control.Alert alerta = new javafx.scene.control.Alert(javafx.scene.control.Alert.AlertType.INFORMATION);
    	alerta.setTitle("Sobre");
    	alerta.setHeaderText("Sobre o sistema");
    	alerta.setContentText(conteudo);

    	javafx.scene.control.TextArea area = new javafx.scene.control.TextArea(conteudo);
    	area.setWrapText(true);
    	area.setEditable(false);
    	area.setMaxWidth(Double.MAX_VALUE);
    	area.setMaxHeight(Double.MAX_VALUE);
    	alerta.getDialogPane().setContent(area);
    	
    	alerta.showAndWait();
    }

    @FXML
    private void onRemover() {
    	 Equipment selecionado = tabelaEquipamentos.getSelectionModel().getSelectedItem();
         if (selecionado == null) {
                 javafx.scene.control.Alert alerta = new javafx.scene.control.Alert(javafx.scene.control.Alert.AlertType.WARNING);
                 alerta.setTitle("Remover");
                 alerta.setHeaderText(null);
                 alerta.setContentText("Selecione um equipamento para remover.");
                 alerta.showAndWait();
                 return;
          }
         if (EquipmentStatus.FECHADO.equals(selecionado.getStatus())) {
                 javafx.scene.control.Alert alerta = new javafx.scene.control.Alert(javafx.scene.control.Alert.AlertType.WARNING);
                 alerta.setTitle("Chamado fechado");
                 alerta.setHeaderText(null);
                 alerta.setContentText("O chamado está fechado!");
                 alerta.showAndWait();
                 return;
         }
         javafx.scene.control.Alert confirmacao = new javafx.scene.control.Alert(javafx.scene.control.Alert.AlertType.CONFIRMATION);
    	confirmacao.setTitle("Confirmação");
    	confirmacao.setHeaderText("Remover equipamento");
    	confirmacao.setContentText("Tem certeza que deseja remover o equipamento selecionado?");
    	
    	java.util.Optional<javafx.scene.control.ButtonType> resposta = confirmacao.showAndWait();
    	if (resposta.isPresent() && resposta.get() == javafx.scene.control.ButtonType.OK) {
    		equipamentos.remove(selecionado);
            salvarEquipamentosEmExcel(obterCaminhoUltimoArquivo());


    	}
    	
    }
    
    @FXML
    private void onMudarStatus() {
        Equipment selecionado = tabelaEquipamentos.getSelectionModel().getSelectedItem();

        if (selecionado == null) {
            javafx.scene.control.Alert alerta = new javafx.scene.control.Alert(javafx.scene.control.Alert.AlertType.WARNING);
            alerta.setTitle("Mudar Status");
            alerta.setHeaderText(null);
            alerta.setContentText("Selecione um equipamento para mudar o Status!");
            alerta.showAndWait();
            return;
        }

        // Pega o status atual ou null
        EquipmentStatus statusAtual = selecionado.getStatus();

        // Cria o diálogo já com os valores do enum
        javafx.scene.control.ChoiceDialog<EquipmentStatus> dialog = new javafx.scene.control.ChoiceDialog<>(
                statusAtual, EquipmentStatus.values());
        dialog.setTitle("Mudar Status");
        dialog.setHeaderText("Alterar o status do equipamento selecionado");
        dialog.setContentText("Escolha o novo status");

        dialog.showAndWait().ifPresent(novoStatus -> {
            selecionado.setStatus(novoStatus);
            selecionado.setAtualizadoEm(java.time.LocalDateTime.now().format(FORMATTER));
            labelInfo.setText("Criado em: %s | Última modificação: %s"
                    .formatted(selecionado.getCriadoEm(), selecionado.getAtualizadoEm()));

            tabelaEquipamentos.refresh();
            salvarEquipamentosEmExcel(obterCaminhoUltimoArquivo());
        });
    }

    @FXML 
    private void onEditar() {
        Equipment selecionado = tabelaEquipamentos.getSelectionModel().getSelectedItem();
        if (selecionado == null) {
                javafx.scene.control.Alert alerta = new javafx.scene.control.Alert(javafx.scene.control.Alert.AlertType.WARNING);
                alerta.setTitle("Editar");
                alerta.setHeaderText("Editar informações");
                alerta.setContentText("Selecione um equipamento para ser editado!");
                alerta.showAndWait();
                return;
        }
        if (EquipmentStatus.FECHADO.equals(selecionado.getStatus())) {
                javafx.scene.control.Alert alerta = new javafx.scene.control.Alert(javafx.scene.control.Alert.AlertType.WARNING);
                alerta.setTitle("Chamado fechado");
                alerta.setHeaderText(null);
                alerta.setContentText("O chamado está fechado!");
                alerta.showAndWait();
                return;
        }
        try {
                FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/AddEquipmentView.fxml"));
                Parent root = fxmlLoader.load();
		
		AdicionarEquipamentoController controller = fxmlLoader.getController();
		controller.preencherCampos(selecionado);
		
		Stage stage = new Stage();
		stage.setTitle("Editar Equipamento");
		stage.setScene(new Scene(root));
		stage.initModality(Modality.APPLICATION_MODAL);
		stage.showAndWait();
		
		Equipment editado = controller.getEquipamentoSalvo();
        if(editado != null) {
            selecionado.setGLPI(editado.getGLPI());
            selecionado.setPatrimonio(editado.getPatrimonio());
            selecionado.setNumeroSerie(editado.getNumeroSerie());
            selecionado.setDescricaoMarcaModelo(editado.getDescricaoMarcaModelo());
            selecionado.setDefeito(editado.getDefeito());
            selecionado.setBatalhao(editado.getBatalhao());
            selecionado.setChamadoEmpresa(editado.getChamadoEmpresa());
            selecionado.setStatus(editado.getStatus());
            selecionado.setCriadoEm(editado.getCriadoEm());
            selecionado.setAtualizadoEm(editado.getAtualizadoEm());
            labelInfo.setText(String.format("Criado em: %s | Última modificação: %s", selecionado.getCriadoEm(), selecionado.getAtualizadoEm()));
            tabelaEquipamentos.refresh();
salvarEquipamentosEmExcel(obterCaminhoUltimoArquivo());

    }
	}catch (Exception e) {
		e.printStackTrace();
	}
}

    private void salvarEquipamentosEmExcel(Path caminhoArquivo) {
        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("Equipamentos");
            Row header = sheet.createRow(0);
            String[] headers = {"GLPI","Patrimonio","NumeroSerie","DescricaoMarcaModelo","Defeito","Batalhao","ChamadoEmpresa","Status","CriadoEm","AtualizadoEm"};
            for (int i = 0; i < headers.length; i++) {
                header.createCell(i).setCellValue(headers[i]);
            }

            int rowIndex = 1;

            for (Equipment eq : equipamentos) {
            	 Row row = sheet.createRow(rowIndex++);
                 row.createCell(0).setCellValue(eq.getGLPI());
                 row.createCell(1).setCellValue(eq.getPatrimonio());
                 row.createCell(2).setCellValue(eq.getNumeroSerie());
                 row.createCell(3).setCellValue(eq.getDescricaoMarcaModelo());
                 row.createCell(4).setCellValue(eq.getDefeito());
                 row.createCell(5).setCellValue(eq.getBatalhao());
                 row.createCell(6).setCellValue(eq.getChamadoEmpresa());
                 row.createCell(7).setCellValue(eq.getStatus() != null ? eq.getStatus().name() : "");
                 row.createCell(8).setCellValue(eq.getCriadoEm());
                 row.createCell(9).setCellValue(eq.getAtualizadoEm());
             }

            try (OutputStream fos = Files.newOutputStream(caminhoArquivo)) {
                 workbook.write(fos);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void onCarregarArquivo() {
    	  FileChooser fileChooser = new FileChooser();
          fileChooser.setTitle("Selecione a planilha");
          fileChooser.getExtensionFilters().add(
                          new FileChooser.ExtensionFilter("Planilhas Excel", "*.xlsx"));

          File arquivo = fileChooser.showOpenDialog(tabelaEquipamentos.getScene().getWindow());
          if (arquivo != null) {
              carregarEquipamentosDoExcel(arquivo.toPath());
              salvarCaminhoUltimoArquivo(arquivo.toPath());
          }
    }

    private void carregarEquipamentosDoExcel(Path caminhoArquivo) {
        equipamentos.clear();
        if (Files.notExists(caminhoArquivo)) return;
        try (InputStream fis = Files.newInputStream(caminhoArquivo);
                Workbook workbook = new XSSFWorkbook(fis)) {
            Sheet sheet = workbook.getSheetAt(0);
            for (int i = 1; i <= sheet.getLastRowNum(); i++) {
                Row row = sheet.getRow(i);
                if (row == null) continue;
                equipamentos.add(new Equipment(
                        getCellValue(row,0),
                        getCellValue(row,2),
                        getCellValue(row,1),
                        getCellValue(row,3),
                        getCellValue(row,4),
                        getCellValue(row,5),
                        getCellValue(row,6),
                        parseStatus(getCellValue(row,7)),
                        getCellValue(row,8),
                        getCellValue(row,9)
                   ));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private String getCellValue(Row row, int index) {
        Cell cell = row.getCell(index, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);
        cell.setCellType(CellType.STRING);
        return cell.getStringCellValue();
    }


    private EquipmentStatus parseStatus(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }
        try {
            return EquipmentStatus.valueOf(value);
        } catch (IllegalArgumentException e) {
            return null;
        }
    }

    
    private Path obterCaminhoUltimoArquivo() {
        Preferences prefs = Preferences.userNodeForPackage(MainController.class);
        return Paths.get(prefs.get(PREFS_KEY, CAMINHO_XLSX_PADRAO.toString()));
    }

    private void salvarCaminhoUltimoArquivo(Path caminho) {
        Preferences prefs = Preferences.userNodeForPackage(MainController.class);
        prefs.put(PREFS_KEY, caminho.toString());
    }

}