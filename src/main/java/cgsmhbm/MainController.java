package cgsmhbm;


import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Modality;
import javafx.stage.Stage;
import java.io.*;
import java.util.prefs.Preferences;
import javafx.stage.FileChooser;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;




public class MainController {
	
    private static final String CAMINHO_XLSX_PADRAO = System.getProperty("user.home")
            + File.separator + "Documents"
            + File.separator + "CGSMH"
            + File.separator + "equipamentos.xlsx";
    private static final String PREFS_KEY = "ultimo_excel";
	
	@FXML private TableView<Equipment> tabelaEquipamentos;
	@FXML private TableColumn<Equipment, String> colGLPI;
	@FXML private TableColumn<Equipment, String> colPatrimonio;
        @FXML private TableColumn<Equipment, String> colNumeroSerie;
        @FXML private TableColumn<Equipment, String> colDescricaoMarcaModelo;
        @FXML private TableColumn<Equipment, String> colDefeito;
	@FXML private TableColumn<Equipment, String> colBatalhao;
	@FXML private TableColumn<Equipment, String> colChamadoEmpresa;
	@FXML private TableColumn<Equipment, String> colStatus;
    @FXML private TextField campoBusca;

    private ObservableList<Equipment> equipamentos = FXCollections.observableArrayList();

    

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
        tabelaEquipamentos.setItems(equipamentos);
        
        
        
        carregarEquipamentosDoExcel(obterCaminhoUltimoArquivo());
    }

    
    
    private void criarPastaPadraoSeNecessario() {
        File pasta = new File(System.getProperty("user.home") + File.separator + "Documents" + File.separator + "CGSMH");
        if (!pasta.exists()) {
            pasta.mkdirs();
        }
        File arquivo = new File(CAMINHO_XLSX_PADRAO);
        if (!arquivo.exists()) {
        	try (Workbook workbook = new XSSFWorkbook()) {
                Sheet sheet = workbook.createSheet("Equipamentos");
                Row header = sheet.createRow(0);
                String[] headers = {"GLPI","Patrimonio","NumeroSerie","DescricaoMarcaModelo","Defeito","Batalhao","ChamadoEmpresa","Status"};
                for (int i = 0; i < headers.length; i++) {
                    header.createCell(i).setCellValue(headers[i]);
                }
                try (FileOutputStream fos = new FileOutputStream(arquivo)) {
                    workbook.write(fos);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
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
    					+ "Versão 1.0.1\n"
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
    	javafx.scene.control.Alert confirmacao = new javafx.scene.control.Alert(javafx.scene.control.Alert.AlertType.CONFIRMATION);
    	confirmacao.setTitle("Confirmação");
    	confirmacao.setHeaderText("Remover equipamento");
    	confirmacao.setContentText("Tem certeza que deseja remover o equipamento selecionado?");
    	
    	java.util.Optional<javafx.scene.control.ButtonType> resposta = confirmacao.showAndWait();
    	if (resposta.isPresent() && resposta.get() == javafx.scene.control.ButtonType.OK) {
    		tabelaEquipamentos.getItems().remove(selecionado);
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
    	javafx.scene.control.ChoiceDialog<String> dialog = new javafx.scene.control.ChoiceDialog<String>(
    			selecionado.getStatus(),"Aguardando abertura" , "Garantia acionada", "QAP - Aguardando devolução", "Fechado");
    	dialog.setTitle("Mudar Status");
    	dialog.setHeaderText("Alterar o status do equipamento selecionado");
    	dialog.setContentText("Escolha o novo status");
    	
    	java.util.Optional<String> resultado = dialog.showAndWait();
    	resultado.ifPresent(novoStatus -> {
    		selecionado.setStatus(novoStatus);
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
			tabelaEquipamentos.refresh();
            salvarEquipamentosEmExcel(obterCaminhoUltimoArquivo());

		}
	}catch (Exception e) {
		e.printStackTrace();
	}
}

    private void salvarEquipamentosEmExcel(String caminhoArquivo) {
        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("Equipamentos");
            Row header = sheet.createRow(0);
            String[] headers = {"GLPI","Patrimonio","NumeroSerie","DescricaoMarcaModelo","Defeito","Batalhao","ChamadoEmpresa","Status"};
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
                 row.createCell(7).setCellValue(eq.getStatus());
             }

             try (FileOutputStream fos = new FileOutputStream(caminhoArquivo)) {
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
          carregarEquipamentosDoExcel(arquivo.getAbsolutePath());
          salvarCaminhoUltimoArquivo(arquivo.getAbsolutePath());
          }
    }

    private void carregarEquipamentosDoExcel(String caminhoArquivo) {
        equipamentos.clear();
        File file = new File(caminhoArquivo);
        if (!file.exists()) return;
        try (FileInputStream fis = new FileInputStream(file);
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
                           getCellValue(row,7)
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


    private String obterCaminhoUltimoArquivo() {
    	Preferences prefs = Preferences.userNodeForPackage(MainController.class);
        return prefs.get(PREFS_KEY, CAMINHO_XLSX_PADRAO);
    }
    
    private void salvarCaminhoUltimoArquivo(String caminho) {
    	Preferences prefs = Preferences.userNodeForPackage(MainController.class);
    	prefs.put(PREFS_KEY, caminho);
    }

}