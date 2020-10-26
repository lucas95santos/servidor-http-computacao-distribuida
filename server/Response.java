import java.io.IOException;
import java.io.File;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class Response {

    private String protocol;
    private int code;
    private String message;
    private byte[] content;
    private Map<String, List<String>> headers;
    private OutputStream output;

    public Response() {

    }

    public Response(String protocol, int code, String message) {
        this.protocol = protocol;
        this.code = code;
        this.message = message;
    }

    // métodos auxiliares

    public void setContent(byte[] content) {
        this.content = content;
    }

    public String getContentSize() {
        return Integer.toString(this.content.length);
    }

    public void setOutput(OutputStream output) {
        this.output = output;
    }

    public void setHeaders(String chave, String... valores) {
        if (this.headers == null) {
            this.headers = new TreeMap<>();
        }

        this.headers.put(chave, Arrays.asList(valores));
    }

    public String getDirectoryStructure(File file) {
        String[] names = file.list();

        String directoryStructure = "<html xmlns=\"http://www.w3.org/1999/xhtml\" xml:lang=\"en\" lang=\"en\">\r\n"
                + "<head>\r\n" + "<title>Linux/kernel/ - Linux Cross Reference - Free Electrons</title>\r\n"
                + "</head>\r\n" + "<body>\r\n";

        for (int i = 0; i < names.length; i++) {
            directoryStructure += String.format("<td><a href=\"%s\">%s</a></td>\n", names[i], names[i]);
        }

        directoryStructure += "</body>\r\n";

        return directoryStructure;
    }

    public void send() throws IOException {
        this.output.write(this.buildHeader());
        this.output.write(this.content);
        this.output.flush();
    }

    private byte[] buildHeader() {
        return this.toString().getBytes();
    }

    public String toString() {
        StringBuilder str = new StringBuilder();

        str.append(this.protocol).append(" ").append(this.code).append(" ").append(this.message).append("\r\n");
        for (Map.Entry<String, List<String>> entry : this.headers.entrySet()) {
            str.append(entry.getKey());
            String formattedString = Arrays.toString(entry.getValue().toArray()).replace("[", "").replace("]", "");
            str.append(": ").append(formattedString).append("\r\n");
        }
        str.append("\r\n");

        return str.toString();
    }
}