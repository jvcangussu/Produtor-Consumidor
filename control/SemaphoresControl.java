/* ***************************************************************
* Autor............: Joao Vitor Cangussu B Oliveira
* Matricula........: 202210559
* Inicio...........: 20/10/2023
* Ultima alteracao.: 28/10/2023
* Nome.............: SemaphoresControl
* Funcao...........: Controla e agrupa os semaforos necessarios para
resolver o problema
*************************************************************** */
package control;

import java.util.concurrent.Semaphore;

public class SemaphoresControl {
  //variavel que controla o tamanho maximo do buffer
  private final static int BUFFER_SIZE = 7;
  //mutex eh o semaforo que controla o acesso a regiao critica
  public static Semaphore mutex = new Semaphore(1);
  //vazio eh o semaforo que controla o numero de espaços vazios no buffer
  public static Semaphore vazio = new Semaphore(BUFFER_SIZE);
  //cheio eh o semaforo que controla o numero de espaços cheios no buffer
  public static Semaphore cheio = new Semaphore(0);
}