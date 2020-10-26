import java.io.File;
import java.io.IOException;
import java.util.logging.Logger;
import java.util.Date;
import java.util.logging.Level;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.nio.file.Files;

public class Connection implements Runnable {
    private Socket socket;
    private int port;
    private boolean connected;

    public Connection(Socket socket, int port) {
        this.socket = socket;
        this.port = port;
    }

    public void run() {
        this.connected = true;
        System.out.println("Client " + this.socket.getInetAddress() + " is connected");

        while (this.connected) {
            try {
                Request request = new Request();
                request.read(this.socket.getInputStream());

                Response response;

                // verificando se a conexão é permanente e setando um tempo limite
                if (request.getKeepAlive()) {
                    this.socket.setKeepAlive(true);
                    this.socket.setSoTimeout(request.getLimitTime());
                } else {
                    socket.setSoTimeout(300);
                }

                if (request.getResource().contains("/cgi-bin")) {
                    String[] resourceData = request.getResource().split("\\?");

                    String script = resourceData[0].replaceFirst("/cgi-bin", "");
                    String query = resourceData[1];

                    File file = new File("./" + resourceData[0]);

                    if (file.exists()) {
                        try {
                            CGI cgi = new CGI(this.socket, script, query);
                            cgi.execute();
                        } catch (IOException exception) {
                            System.out.println(exception);
                        }
                    } else {
                        System.out.println("File Not Found");
                    }

                    continue;
                }

                // setando arquivo padrão
                if (request.getResource().equals("/")) {
                    request.setResource("/index.html");
                }

                String fileName = request.getResource();

                if (!request.getResource().startsWith(".")) {
                    fileName = request.getResource().replaceFirst("/", "./files/");
                }

                File file = new File(fileName);

                response = new Response(request.getProtocol(), 200, "Document follows");

                byte[] content;

                // verificando se é diretório
                if (file.isDirectory()) {
                    content = response.getDirectoryStructure(file).getBytes();
                } else {
                    if (!file.exists()) {
                        response = new Response(request.getProtocol(), 404, "File Not Found");
                        file = new File("./files/error.html");
                    }

                    content = Files.readAllBytes(file.toPath());
                }

                response.setContent(content);

                String formattedDate = DateFormat.getDate(new Date());
                String locationString = "http://localhost:" + this.port;

                // setando os headers da resposta
                response.setHeaders("Location", locationString);
                response.setHeaders("Date", formattedDate);
                response.setHeaders("Server-Type", "FACOM-CD-2020/1.0");

                if (request.getResource().endsWith(".html"))
                    response.setHeaders("Content-Type", "text/html");
                
                if (request.getResource().endsWith(".jpg") || request.getResource().endsWith(".jpeg"))
                    response.setHeaders("Content-Type", "image/jpeg");

                if (request.getResource().endsWith(".gif"))
                    response.setHeaders("Content-Type", "image/gif");

                if (request.getResource().endsWith(".txt"))
                    response.setHeaders("Content-Type", "text/plain");

                response.setHeaders("Content-Length", response.getContentSize());

                response.setOutput(this.socket.getOutputStream());
                response.send();
            } catch (IOException exception) {
                if (exception instanceof SocketTimeoutException) {
                    try {
                        this.connected = false;
                        this.socket.close();
                    } catch (IOException secondException) {
                        System.out.println(secondException);
                    }
                }
            }
        }
    }
}
