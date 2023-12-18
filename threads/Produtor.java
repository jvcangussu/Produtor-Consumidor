/* ***************************************************************
* Autor............: Joao Vitor Cangussu B Oliveira
* Matricula........: 202210559
* Inicio...........: 20/10/2023
* Ultima alteracao.: 29/10/2023
* Nome.............: Produtor
* Funcao...........: Descreve a thread produtora e o que ela executa
*************************************************************** */
package threads;

import control.BackgroundScreenController;
import control.SemaphoresControl;
import javafx.application.Platform;
import javafx.scene.control.Slider;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class Produtor extends Thread {
  //Atributos
  private ImageView imagem;//imagem do dono
  private boolean pausado;////flag que indica se o usuario pausou o produtor
  private boolean finalizado;//flag que indica se o usuario reiniciou o produtor
  private int animacao;//variavel auxiliar para controlar a animacao de andar do menino
  private Slider velocidade;//slider que o usuario controla a velocidade de producao e movimento

  //Construtor
  public Produtor(ImageView imagem, Slider sliderVelocidade){
    //atribui os elementos que a thread vai controlar
    this.imagem = imagem;
    this.velocidade = sliderVelocidade;
    //configura a imagem inicial e sua posicao inicial
    imagem.setImage(new Image("/img/person/person1.png"));
    imagem.setLayoutX(637);
    imagem.setLayoutY(70);
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

          //INICIO REGIAO CRITICA
          SemaphoresControl.vazio.acquire();//down no semaforo de posicoes vazias
          SemaphoresControl.mutex.acquire();//down no semaforo que controla acesso ao buffer
          if(finalizado){//testa se nao foi finalizada
            break;
          }//fim if
          produzir();//animacao de producao da thread
          if(finalizado){//testa se nao foi finalizada
            break;
          }//fim if
          SemaphoresControl.mutex.release();//up no semaforo que controla acesso ao buffer
          SemaphoresControl.cheio.release();//up no semaforo de posicoes cheias
          //FIM REGIAO CRITICA

          if(finalizado){//testa se nao foi finalizada
            break;
          }//fim if
          irPegarRacao();//anda de volta ate o saco de racao
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
  * Metodo: produzir
  * Funcao: Descreve uma producao da thread e a animacao de colocar no buffer
  * Parametros: Sem parametros
  * Retorno: void
  *************************************************************** */
  public void produzir(){
    try{
      checarPausado();
      sleep(10 * (101 - (int)velocidade.getValue()));//determina o tempo que a thread ficara produzindo
      checarPausado();
      BackgroundScreenController.dogFoods.get(6 - SemaphoresControl.vazio.availablePermits()).setVisible(true);//torna a imagem da ultima comida visivel
    } catch(Exception e){
      e.printStackTrace();
    }//fim try catch
  }//fim produzir

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
  * Funcao: Descreve a animacao do dono indo ate a mesa
  * Parametros: Sem parametros
  * Retorno: void
  *************************************************************** */
  public void irParaMesa(){
    while(!finalizado){//se o usuario resetar a animacao eh parada para a thread ser finalizada
      if(!pausado){//so executa se a thread nao tiver pausada
        Platform.runLater(()->imagem.setLayoutY(imagem.getLayoutY() + 1));//incrementa a posicao Y da imagem
        animacao = valorAnimacao();//recebe o caso que sera escolhido dependendo da posicao da imagem
        switch(animacao){
          case 0://primeira imagem da animacao
            Platform.runLater(()->imagem.setImage(new Image("/img/person/person1.png")));
            break;
          case 1://segunda imagem da animacao
            Platform.runLater(()->imagem.setImage(new Image("/img/person/person2.png")));
            break;
          case 2://terceira imagem da animacao
            Platform.runLater(()->imagem.setImage(new Image("/img/person/person3.png")));
            break;
          case 3://quarta imagem da animacao
            Platform.runLater(()->imagem.setImage(new Image("/img/person/person4.png")));
            break;
          case 4://quinta imagem da animacao
            Platform.runLater(()->imagem.setImage(new Image("/img/person/person5.png")));
            break;
        }//fim switch
        //dorme alguns ms para que ocorra o efeito da animacao e a velocidade seja controlada
        try{
          sleep(45 - (5 + (int)velocidade.getValue()/3));//formula que controla a velocidade de acordo com o valor do slider manipulado pelo usuario
        } catch(Exception e){
          System.out.println(e.getMessage());
        }//fim try catch
        //se chegar na mesa o while eh interrompido e a animacao acaba
        if(imagem.getLayoutY() >= 397){
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
  }//fim irParaMesa

  /* ***************************************************************
  * Metodo: irPegarRacao
  * Funcao: Descreve a animacao do dono voltando ate o saco de racao
  * Parametros: Sem parametros
  * Retorno: void
  *************************************************************** */
  public void irPegarRacao(){
    while(!finalizado){//se o usuario resetar a animacao eh parada para a thread ser finalizada
      if(!pausado){//so executa se a thread nao tiver pausada
        Platform.runLater(()->imagem.setLayoutY(imagem.getLayoutY() - 1));//decrementa um da posicao Y da imagem
        animacao = valorAnimacao();
        switch(animacao){
          case 0://primeira imagem da animacao
            Platform.runLater(()->imagem.setImage(new Image("/img/person_reverse/person1.png")));
            break;
          case 1://segunda imagem da animacao
            Platform.runLater(()->imagem.setImage(new Image("/img/person_reverse/person2.png")));
            break;
          case 2://terceira imagem da animacao
            Platform.runLater(()->imagem.setImage(new Image("/img/person_reverse/person3.png")));
            break;
          case 3://quarta imagem da animacao
            Platform.runLater(()->imagem.setImage(new Image("/img/person_reverse/person4.png")));
            break;
          case 4://quinta imagem da animacao
            Platform.runLater(()->imagem.setImage(new Image("/img/person_reverse/person5.png")));
            break;
        }//fim switch
        //dorme alguns ms para que ocorra o efeito da animacao e a velocidade seja controlada
        try{
          sleep(45 - (5 + (int)velocidade.getValue()/3));//formula que controla a velocidade de acordo com o valor do slider manipulado pelo usuario
        } catch(Exception e){
          System.out.println(e.getMessage());
        }//fim try catch
        //se chegar na racao a animacao acaba
        if(imagem.getLayoutY() <= 70){
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
  }//fim irPegarRacao

  /* ***************************************************************
  * Metodo: valorAnimacao
  * Funcao: controla de quantos em quantos pixels a animacao sera trocada por meio de mapeamento
  * Parametros: Sem parametros
  * Retorno: void
  *************************************************************** */
  public int valorAnimacao(){
    //como tem cinco imagens diferentes serao necessarios cinco casos diferentes
    //por isso o valor eh dividido de forma que a cada seis pixels seja um caso diferente
    double valor = (int)imagem.getLayoutY() % 30;
    if(valor >= 0 && valor < 6){//primeiro caso
      return 0;
    } else if(valor >= 6 && valor < 12){//segundo caso
      return 1;
    } else if(valor >= 12 && valor < 18){//terceiro caso
      return 2;
    } else if(valor >= 18 && valor < 24){//quarto caso
      return 3;
    } else {//quinto caso
      return 4;
    }//fim if else
  }//fim valorAnimacao
}//fim classe Produtor
