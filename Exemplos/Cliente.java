import java.io.*;
import java.util.*;

public class Cliente {
    public static void main(String[] args) throws IOException {
        while (true) {
            //nessa string vai receber cgi-bin|prog?parametro1=valor1&parametro2=valor2 - fazer um parser para processar essa entrada e separar cada coisa
            String input;
            BufferedReader console = new BufferedReader (new InputStreamReader(System.in));
            input = console.readLine();

            // teste System.out.println("Input: " + input);
            
            //tratando a string recebida
            String inputCGI[] = input.split("\\|");
            String cgi = inputCGI[0];

            String inputProg[] = inputCGI[1].split("\\?");
            String programa = inputProg[0];

            String inputParam1[] = inputProg[1].split("\\=");
            String parametro1 = inputParam1[0];

            String valor1 = inputParam1[1];

            /*teste
            System.out.println("CGI: " + cgi);
            System.out.println("Prog: " + programa);
            System.out.println("Parametro: " + parametro1);
            System.out.println("Valor: " + valor1);*/

            //abrindo socket para servidor conectar
            Socket clientSocket = new Socket("localhost", 6789); 
		
		    DataOutputStream outToServer = new DataOutputStream(clientSocket.getOutputStream());
		
		    BufferedReader inFromServer = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));

            //criando processo
            ProcessBuilder pb = new ProcessBuilder(input);

            //passabdo parametros da string para as variaveis de ambiente
            Map<String, String> env = pb.enviroment();
            env.put("PARAM1", "PARAM1_VALUE");
            env.put("PARAM1", "PARAM1_VALUE");

            //startando processo - mas ainda não sei pra que vamos usar exatamente
            Process proc = pb.start();
        }
    }
}