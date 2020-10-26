import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class Request {
    private String method;
    private String resource;
    private String protocol;
    private boolean keepAlive;
    private int limitTime;
    private Map<String, List<String>> headers;

    public Request() {
        this.keepAlive = true;
        this.limitTime = 3000;
    }

    // métodos auxiliares

    public String getProtocol() {
        return this.protocol;
    }

    public void setProtocol(String protocol) {
        this.protocol = protocol;
    }

    public String getResource() {
        return this.resource;
    }
    
    public void setResource(String resource) {
        this.resource = resource;
    }

    public String getMethod() {
        return this.method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public boolean getKeepAlive() {
        return this.keepAlive;
    }

    public void setKeepAlive(boolean keepAlive) {
        this.keepAlive = keepAlive;
    }

    public Map<String, List<String>> getHeaders() {
        return this.headers;
    }

    public int getLimitTime() {
        return limitTime;
    }

    public void setLimitTime(int limitTime) {
        this.limitTime = limitTime;
    }

    public void setHeaders(String key, String[] values) {
        if (this.headers == null) {
            this.headers = new TreeMap<>();
        }
        
        this.headers.put(key, Arrays.asList(values));
    }

    public Request read(InputStream input) throws IOException {
        BufferedReader buffer = new BufferedReader(new InputStreamReader(input));

        System.out.println("Request: ");

        String requestLine = buffer.readLine();
        String[] requestData = requestLine.split(" ");
        
        this.setMethod(requestData[0]);
        this.setResource(requestData[1]);
        this.setProtocol(requestData[2]);

        String headerData = buffer.readLine();
        //Enquanto a linha nao for nula e nao for vazia
        while (headerData != null && !headerData.isEmpty()) {
            System.out.println(headerData);

            String[] headerLine = headerData.split(":");
            this.setHeaders(headerLine[0], headerLine[1].trim().split(","));
            
            headerData = buffer.readLine();
        }

        if (this.getHeaders().containsKey("Connection")) {
            this.setKeepAlive(this.getHeaders().get("Connection").get(0).equals("keep-alive"));
        }

        return this;
    }
}
