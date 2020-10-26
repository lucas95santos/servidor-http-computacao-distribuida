import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.Map;

public class CGI {
    private Socket socket;
    private String script;
    private String query;

    public CGI(Socket socket, String script, String query) {
        this.socket = socket;
        this.script = script;
        this.query = query;
    }

    public void execute() throws IOException {
        ProcessBuilder processBuilder = new ProcessBuilder("./cgi-bin" + this.script);
        Map<String, String> env = processBuilder.environment();

        env.put("QUERY_STRING", this.query);
		
		Process proc = processBuilder.start();
		
		InputStream is = proc.getInputStream();
		InputStreamReader isr = new InputStreamReader(is);
        BufferedReader br = new BufferedReader(isr);
        
        DataOutputStream outToServer = new DataOutputStream(this.socket.getOutputStream());
		
		String line;
		while ( (line = br.readLine()) != null) {
            outToServer.writeBytes(line);
        }
    
		br.close();
    }
}
