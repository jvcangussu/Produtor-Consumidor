/* ***************************************************************
* Autor............: Joao Vitor Cangussu B Oliveira
* Matricula........: 202210559
* Inicio...........: 20/10/2023
* Ultima alteracao.: 29/10/2023
* Nome.............: Consumidor
* Funcao...........: Descreve a thread consumidora e o que ela executa
*************************************************************** */
package threads;

import control.BackgroundScreenController;
import control.SemaphoresControl;
import javafx.application.Platform;
import javafx.scene.control.Slider;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class Consumidor extends Thread {
  //Atributos
  private ImageView imagem;//imagem do cachorro
  private ImageView coco;//imagem do coco do cachorro
  private int animacao;//variavel auxiliar para controlar a animacao de andar do cachorro
  private boolean pausado;//flag que indica se o usuario pausou o consumidor
  private boolean finalizado;//flag que indica se o usuario reiniciou o consumidor
  private Slider velocidade;//slider que o usuario controla a velocidade de consumo e movimento

  //Construtor
  public Consumidor(ImageView imagem, ImageView coco, Slider sliderVelocidade){
    //atribui os elementos que a thread vai controlar
    this.imagem = imagem;
    this.velocidade = sliderVelocidade;
    this.coco = coco;
    //configura a imagem inicial e sua posicao inicial
    imagem.setImage(new Image("/img/dog/dog1.png"));
    imagem.setLayoutX(69);
    imagem.setLayoutY(442);
    //deixa o coco nao visivel
    coco.setVisible(false);
    //define as flags para o estagio inicial de nao pausado nem finalizado
    pausado = false;
    finalizado = false;
  }//fim do construtor

  //Metodos
  //metodo run da thread
  @Override
  public void run(){
    while(!finalizado){//executa ate a thread nao entrar em estado de finalizando
      if(!pausado){//so executa se a thread nao estiver pausada
        try{
          irParaMesa();//anda ate a mesa
          if(finalizado){//testa se nao foi finalizada
            break;
          }//fim if

          //INICO REGIAO CRITICA
          SemaphoresControl.cheio.acquire();//down no semaforo de posicoes cheias
          SemaphoresControl.mutex.acquire();//down no semaforo que controla acesso ao buffer
          if(finalizado){//testa se nao foi finalizada
            break;
          }//fim if
          consumir();//animacao de consumo da thread
          if(finalizado){//testa se nao foi finalizada
            break;
          }//fim if
          SemaphoresControl.mutex.release();//up no semaforo que controla acesso ao buffer
          SemaphoresControl.vazio.release();//up no semaforo de posicoes vazias
          //FIM REGIAO CRITICA

          if(finalizado){//testa se nao foi finalizada
            break;
          }//fim if
          voltarParaCasinha();//anda de volta para a casinha
        } catch(Exception e){
          e.printStackTrace();
        }//fim try catch
      } else {//se a thread estiver pausada
        try{
          sleep(1);//dorme ate despausar
        } catch(Exception e){
          e.printStackTrace();
        }//fim try catch
      }//fim if else
    }//fim while
  }//fim run

  /* ***************************************************************
  * Metodo: consumir
  * Funcao: Descreve um consumo da thread e a animacao de tirar do buffer
  * Parametros: Sem parametros
  * Retorno: void
  *************************************************************** */
  public void consumir(){
    try{
      checarPausado();
      sleep(20 * (101 - (int)velocidade.getValue()));//determina o tempo que a thread ficara consumindo
      checarPausado();
      BackgroundScreenController.dogFoods.get(0).setVisible(false);//torna a imagem da primeira comida nao visivel
      sleep(200);//tempo para realizar a animacao
      BackgroundScreenController.dogFoods.get(0).setVisible(true);//torna a imagem da primeira comida visivel
      BackgroundScreenController.dogFoods.get(SemaphoresControl.cheio.availablePermits()).setVisible(false);//torna a imagem da ultima comida nao visivel
    } catch(Exception e){
      e.printStackTrace();
    }//fim try catch
  }//fim consumir

  /* ***************************************************************
  * Metodo: pausarOuContinuar
  * Funcao: Alterna a thread entre estado de pausada ou nao
  * Parametros: Sem parametros
  * Retorno: boolean se a thread esta pausada ou nao apos execucao do metodo
  *************************************************************** */
  public boolean pausarOuContinuar(){
    if(!pausado){
      pausado = true;
    } else if(pausado){
      pausado = false;
    }//fim do if
    return pausado;
  }//fim pausarOuContinuar

  /* ***************************************************************
  * Metodo: finalizar
  * Funcao: Determinar que a thread deve finalizar sua execucao
  * Parametros: Sem parametros
  * Retorno: void
  *************************************************************** */
  public void finalizar(){
    finalizado = true;
  }//fim finalizar

  /* ***************************************************************
  * Metodo: checarPausado
  * Funcao: checa se a thread deve ser pausada e se positivo a thread dorme funcionando como uma espera ocupada
  * Parametros: Sem parametros
  * Retorno: void
  *************************************************************** */
  public void checarPausado(){
    while(pausado && !finalizado){//testa se a thread nao esta finalizada para que possa ser caso necessite enquanto esta pausada
      try{
        sleep(1);//thread dorme por 1 ms a cada checagem positiva
      } catch(Exception e){
        e.printStackTrace();
      }//fim try catch
    }//fim while
  }//fim checarPausado

  /* ***************************************************************
  * Metodo: irParaMesa
  * Funcao: Descreve a animacao do cachorro indo ate a mesa
  * Parametros: Sem parametros
  * Retorno: void
  *************************************************************** */
  public void irParaMesa(){
    while(!finalizado){//se o usuario resetar a animacao eh parada para a thread ser finalizada
      if(!pausado){//so executa se a thread nao tiver pausada
        Platform.runLater(()->imagem.setLayoutX(imagem.getLayoutX() + 1));//incrementa a posicao X da imagem
        animacao = valorAnimacao();//recebe o caso que sera escolhido dependendo da posicao da imagem
        switch(animacao){
          case 0://primeira imagem da animacao
            Platform.runLater(()->imagem.setImage(new Image("/img/dog/dog1.png")));
            break;
          case 1://segunda imagem da animacao
            Platform.runLater(()->imagem.setImage(new Image("/img/dog/dog2.png")));
            break;
          case 2://terceira imagem da animacao
            Platform.runLater(()->imagem.setImage(new Image("/img/dog/dog3.png")));
            break;
          case 3://quarta imagem da animacao
            Platform.runLater(()->imagem.setImage(new Image("/img/dog/dog4.png")));
            break;
          case 4://quinta imagem da animacao
            Platform.runLater(()->imagem.setImage(new Image("/img/dog/dog5.png")));
            break;
          case 5://sexta imagem da animacao
            Platform.runLater(()->imagem.setImage(new Image("/img/dog/dog6.png")));
            break;
        }//fim switch
        //dorme alguns ms para que ocorra o efeito da animacao e a velocidade seja controlada
        try{
          sleep(42 - (5 + (int)velocidade.getValue()/3));//formula que controla a velocidade de acordo com o valor do slider manipulado pelo usuario
        } catch(Exception e){
          System.out.println(e.getMessage());
        }//fim try catch
        //se chegar na mesa o while eh interrompido e a animacao acaba
        if(imagem.getLayoutX() >= 383){
          break;
        }//fim if
      } else {//se estiver pausado
        try{
          sleep(1);//a thread dorme
        } catch(Exception e){
          System.out.println(e.getMessage());
        }//fim try catch
      }//fim if else
    }//fim while
    //apos a animacao de andar a imagem do coco fica invisivel
    if(!finalizado){//testa se a thread nao esta em estado de finalizando
      Platform.runLater(()->coco.setVisible(false)); 
    }//fim if
  }//fim irParaMesa

  /* ***************************************************************
  * Metodo: voltarParaCasinha
  * Funcao: Descreve a animacao do cachorro voltando ate a casinha
  * Parametros: Sem parametros
  * Retorno: void
  *************************************************************** */
  public void voltarParaCasinha(){
    while(!finalizado){//se o usuario resetar a animacao eh parada para a thread ser finalizada
      if(!pausado){//so executa se a thread nao tiver pausada
        imagem.setLayoutX(imagem.getLayoutX() - 1);//decrementa um da posicao X da imagem
        animacao = valorAnimacao();//recebe o caso que sera escolhido dependendo da posicao da imagem
        switch(animacao){
          case 0://primeira imagem da animacao
            Platform.runLater(()->imagem.setImage(new Image("/img/dog_reverse/dog1.png")));
            break;
          case 1://segunda imagem da animacao
            Platform.runLater(()->imagem.setImage(new Image("/img/dog_reverse/dog2.png")));
            break;
          case 2://terceira imagem da animacao
            Platform.runLater(()->imagem.setImage(new Image("/img/dog_reverse/dog3.png")));
            break;
          case 3://quarta imagem da animacao
            Platform.runLater(()->imagem.setImage(new Image("/img/dog_reverse/dog4.png")));
            break;
          case 4://quinta imagem da animacao
            Platform.runLater(()->imagem.setImage(new Image("/img/dog_reverse/dog5.png")));
            break;
          case 5://sexta imagem da animacao
            Platform.runLater(()->imagem.setImage(new Image("/img/dog_reverse/dog6.png")));
            break;
        }//fim switch
        //dorme alguns ms para que ocorra o efeito da animacao e a velocidade seja controlada
        try{
          sleep(42 - (5 + (int)velocidade.getValue()/3));//formula que controla a velocidade de acordo com o valor do slider manipulado pelo usuario
        } catch(Exception e){
          System.out.println(e.getMessage());
        }//fim try catch
        //se chegar na casinha a animacao acaba
        if(imagem.getLayoutX() <= 80){
          break;
        }//fim if
      } else {
        try{
          sleep(1);//se tiver pausada a thread dorme
        } catch(Exception e){
          System.out.println(e.getMessage());
        }//fim try catch
      }//fim if else
    }//fim while
    //apos a animacao de andar a imagem do coco fica visivel
    if(!finalizado){//testa se a thread nao esta em estado de finalizando
      Platform.runLater(()->coco.setVisible(true)); 
    }//fim if
  }//fim voltarParaCasinha

  /* ***************************************************************
  * Metodo: valorAnimacao
  * Funcao: controla de quantos em quantos pixels a animacao sera trocada por meio de mapeamento
  * Parametros: Sem parametros
  * Retorno: void
  *************************************************************** */
  public int valorAnimacao(){
    //como tem seis imagens diferentes serao necessarios seis casos diferentes
    //por isso o valor eh dividido de forma que a cada seis pixels seja um caso diferente
    double valor = (int)imagem.getLayoutX() % 36;
    if(valor >= 0 && valor < 6){//primeiro caso
      return 0;
    } else if(valor >= 6 && valor < 12){//segundo caso
      return 1;
    } else if(valor >= 12 && valor < 18){//terceiro caso
      return 2;
    } else if(valor >= 18 && valor < 24){//quarto caso
      return 3;
    } else if(valor >= 24 && valor < 30){//quinto caso
      return 4;
    } else {//sexto caso
      return 5;
    }//fim if else
  }//fim valorAnimacao
}//fim classe Consumidor