import java.io.IOException;
import java.net.ServerSocket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ServerHTTP {
    public static void main(String[] args) throws IOException {
        int port = 8080;

        if (args.length > 0) { 
            port = Integer.parseInt(args[0]);
        }

        ServerSocket server = new ServerSocket(port);
        ExecutorService pool = Executors.newFixedThreadPool(10);
        
        while (true) {
            // cria uma nova thread para cada nova solicitação de conexão
            pool.execute(new Connection(server.accept(), port));
        }
    }
}
