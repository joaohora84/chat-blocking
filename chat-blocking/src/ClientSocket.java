import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.SocketAddress;

public class ClientSocket {

	private final Socket socket;
	private final BufferedReader in;
	private final PrintWriter out;

	public ClientSocket(final Socket socket) throws IOException {
		this.socket = socket;
		this.in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
		this.out = new PrintWriter(socket.getOutputStream(), true);
	}

	public boolean sendMsg(String msg) {
		out.println(msg);
		return !out.checkError();
	}

	public String getMessage() {
		try {
			return in.readLine();
		} catch (Exception e) {
			return null;
		}
	}

	public void close() {
		try {
			in.close();
			out.close();
			socket.close();
		} catch (Exception e) {
			System.err.println("Erro: " + e.getMessage());
		}
	}

	public SocketAddress getRemoteSocketAddress() {
		return socket.getRemoteSocketAddress();
	}

	public boolean isOpen() {
		return !socket.isClosed();
	}

}
