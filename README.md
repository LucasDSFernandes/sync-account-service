# sync-account-service
Micro service que simula uma atualizacao de conta apartir de uma planilha 

Para rodar o projeto, sigua as instruções:

    1 - Realize o git clone;
    2 - Realize mvn clean install para gerar a sync-account-service-0.0.1.jar em seu repositorio local
    3 - Execute o .jar como abaixo: 

### Comando para rodar um jar via CMD

  1 - Na pasta onde esta o .jar abra seu CMD e realize o comando
  #### java -jar .\sync-account-service-0.0.1.jar <CAMINHO_PLANILHA> 
Exemplo:

  ![image](https://user-images.githubusercontent.com/33759918/99292341-d9e9af80-281f-11eb-9535-446d3fb7ddb6.png)

<strong> Aguarde ao final da execução, onde aparesentara o tempo pecorrido pela aplicação. </strong>

  ![image](https://user-images.githubusercontent.com/33759918/99292538-1a492d80-2820-11eb-9f88-23d481f7709d.png)

### Caso deseje rodar local

    1 - Import o ms na Ide de sua preferencia.
    2 - Para rodar local existe uma planilha no caminho sync-account-service\src\main\resources\files, 
    pegar o caminho e trocar pelo parametro do imput dentro da class aplication do micro serviço.
    3 - Caso deseje apontar para a planilha do micro serviço basta adicionar no args o caminho apontado no profile de dev no VM Arguments: -Dspring.profiles.active=dev
	  Como exemplo: 
	   
	  PropertiesFile propertiesFile= applicationContext.getBean(PropertiesFile.class);
	  args[0]= System.getProperty("user.dir").concat(propertiesFile.getOutputFilePath());
      
      Obs.: Caso não coloque e queira testar local, dará um erro, que não encontrou a configuração {account.file.outputFilePath} .
  
   *Exemplo de como ficaria o metodo main:*
   
  ![image](https://user-images.githubusercontent.com/33759918/99461825-3e386c00-2911-11eb-91b6-c6674c4ae3ea.png)

    
### Utilização do strategy
    
     1 - Para caso no futuro precisar usar uma outra extenção de arquivo,  basta adicionar uma put no map com 
            Key -> extenção do arquivo 
            Value -> instancia da classe da extenção de arquivo 
     2 - Implementar regra na classe da extenção de arquivo 
     3 - A nova classe de implementação do service criado deve implementar a interface FileService.
     
     Exemplos:
     fileStrategyMap.put("xls", xlsServiceImpl );
     fileStrategyMap.put("xlsx", xlsxServiceImpl );
     fileStrategyMap.put("txt", txtServiceImpl );

### Coverage de classe de test
 Plugin Coverage do Eclipse Ide
 
  ![image](https://user-images.githubusercontent.com/33759918/99291750-0b15b000-281f-11eb-8cc7-512c689b1577.png)

 
