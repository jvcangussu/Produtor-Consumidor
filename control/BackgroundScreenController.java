/* ***************************************************************
* Autor............: Joao Vitor Cangussu B Oliveira
* Matricula........: 202210559
* Inicio...........: 20/10/2023
* Ultima alteracao.: 28/10/2023
* Nome.............: BackgroundScreenController
* Funcao...........: Controla a tela principal do programa
*************************************************************** */
package control;

import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;
import java.util.concurrent.Semaphore;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Slider;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import threads.Consumidor;
import threads.Produtor;

public class BackgroundScreenController implements Initializable {

  //atributos usados no fxml

  @FXML
  private ImageView dog;//imagem do consumidor  
  @FXML
  private ImageView person;//imagem do produtor

  @FXML
  private Slider consumidorSpeedSlider;//slider que controla a velocidade do consumidor
  @FXML
  private Slider produtorSpeedSlider;//slider que controla a velocidade do produtor

  @FXML
  private ImageView produtorPauseResumeButton;//imagem botao de pause do produtor
  /* ***************************************************************
  * Metodo: produtorPausarOuDespausar
  * Funcao: Configura o botao de pause ou continue do produtor
  * Parametros: MouseEvent event - evento de quando o botao eh pressionado
  * Retorno: void
  *************************************************************** */
  @FXML
  void produtorPausarOuDespausar(MouseEvent event) {
    if(dono.pausarOuContinuar()){//pausarOuContinuar() retorna se o produtor esta pausado ou nao
      produtorPauseResumeButton.setImage(new Image("/img/resumeButton.png"));//se pausado o botao eh setado para o de continuar
    } else {
      produtorPauseResumeButton.setImage(new Image("/img/pauseButton.png"));//se despausado o botao eh setado para o de pausar
    }//fim if else
  }//fim produtorPausarOuDespausar

  @FXML
  private ImageView consumidorPauseResumeButton;//imagem botao de pause do consumidor
  /* ***************************************************************
  * Metodo: consumidorPausarOuDespausar
  * Funcao: Configura o botao de pause ou continue do consumidor
  * Parametros: MouseEvent event - evento de quando o botao eh pressionado
  * Retorno: void
  *************************************************************** */
  @FXML
  void consumidorPausarOuDespausar(MouseEvent event) {
    if(cachorro.pausarOuContinuar()){//pausarOuContinuar() retorna se o consumidor esta pausado ou nao
      consumidorPauseResumeButton.setImage(new Image("/img/resumeButton.png"));//se pausado o botao eh setado para o de continuar
    } else {
      consumidorPauseResumeButton.setImage(new Image("/img/pauseButton.png"));//se despausado o botao eh setado para o de pausar
    }//fim if else
  }//fim consumidorPausarOuDespausar

  @FXML
  private ImageView resetButton;//imagem botao de resetar o programa
  /* ***************************************************************
  * Metodo: resetAll
  * Funcao: Configura o botao de reset para setar as configuracoes iniciais
  * Parametros: MouseEvent event - evento de quando o botao eh pressionado
  * Retorno: void
  *************************************************************** */
  @FXML
  void resetAll(MouseEvent event) {    
    cachorro.finalizar();//inicia a finalizacao da thread antiga do consumidor
    dono.finalizar();//inicia a finalizacao da thread antiga do produtor
    consumidorSpeedSlider.setValue(50);//configura a velocidade inicial do consumidor para metade
    produtorSpeedSlider.setValue(50);//configura a velocidade inicial do produtor para metade
    cachorro = new Consumidor(dog, dogPoop, consumidorSpeedSlider);//cria uma nova instancia da thread do consumidor
    dono = new Produtor(person, produtorSpeedSlider);//cria uma nova instancia da thread do produtor
    SemaphoresControl.mutex = new Semaphore(1);//cria um novo semaforo mutex com a configuracao inicial
    SemaphoresControl.vazio = new Semaphore(7);//cria um novo semaforo vazio com a configuracao inicial
    SemaphoresControl.cheio = new Semaphore(0);//cria um novo semaforo cheio com a configuracao inicial
    for(ImageView dogFood : dogFoods){//para cada comida dentro da lista de comidas
      dogFood.setVisible(false);//seta a comida como nao visivel
    }//fim do for
    consumidorPauseResumeButton.setImage(new Image("/img/pauseButton.png"));//seta o botao do consumidor para o de pause
    produtorPauseResumeButton.setImage(new Image("/img/pauseButton.png"));//seta o botao do produtor para o de pause
    cachorro.start();//inicia os comandos da nova thread consumidor
    dono.start();//inicia os comandos da nova thread produtor
  }//fim resetAll

  @FXML
  private ImageView dogPoop;//imagem do coco do cachorro

  //imagens das sete comidas de cachorro
  @FXML
  private ImageView dogFood1;
  @FXML
  private ImageView dogFood2;
  @FXML
  private ImageView dogFood3;
  @FXML
  private ImageView dogFood4;
  @FXML
  private ImageView dogFood5;
  @FXML
  private ImageView dogFood6;
  @FXML
  private ImageView dogFood7;
  //ArrayList para guardar todas as comidas de cachorro
  public static ArrayList<ImageView> dogFoods = new ArrayList<ImageView>();

  @FXML
  private ImageView closeButton;//imagem do botao de fechar a tela
  /* ***************************************************************
  * Metodo: finishProgram
  * Funcao: Configura o botao de fechar para encerrar todo o programa
  * Parametros: MouseEvent event - evento de quando o botao eh pressionado
  * Retorno: void
  *************************************************************** */
  @FXML
  void finishProgram(MouseEvent event) {
    System.exit(0);//comando para encerrar o programa passando um status normal
  }//fim finishProgram

  //variaveis para referenciar o produtor e o consumidor
  Consumidor cachorro;
  Produtor dono;

  //metodo initialize para definir configuracoes iniciais da tela
  @Override
  public void initialize(URL location, ResourceBundle resources) {
    //adiciona todas as comidas de cachoror no arraylist criado
    dogFoods.add(dogFood1);
    dogFoods.add(dogFood2);
    dogFoods.add(dogFood3);
    dogFoods.add(dogFood4);
    dogFoods.add(dogFood5);
    dogFoods.add(dogFood6);
    dogFoods.add(dogFood7);  
    for(ImageView dogFood : dogFoods){//para cada comida no array de comidas
      dogFood.setVisible(false);//a imagem eh setada como nao visivel
    }
    consumidorSpeedSlider.setValue(50);//configura a velocidade inicial do consumidor para metade
    produtorSpeedSlider.setValue(50);//configura a velocidade inicial do produtor para metade
    dogPoop.setVisible(false);//seta o coco do cachorro como nao visivel
    cachorro = new Consumidor(dog, dogPoop, consumidorSpeedSlider);//cria uma nova instancia da thread consumidora
    dono = new Produtor(person, produtorSpeedSlider);//cria uma nova instancia da thread produtora
    cachorro.start();//inicia a thread consumidora
    dono.start();//inicia a thread produtora
  }//fim initialize
}//fim da classe