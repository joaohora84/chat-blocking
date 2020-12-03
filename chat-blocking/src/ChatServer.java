
import java.io.IOException;
import java.net.ServerSocket;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

public class ChatServer {

	public static final int PORT = 4000;

	private ServerSocket serverSocket;
	private final List<ClientSocket> clientSocketList;

	public ChatServer() {
		clientSocketList = new LinkedList<>();
	}

	public static void main(String[] args) {

		final ChatServer server = new ChatServer();

		try {
			server.start();
		} catch (Exception e) {
			System.err.println("Erro ao iniciar servidor: " + e.getMessage());
		}

	}

	public void start() throws IOException {
		serverSocket = new ServerSocket(PORT);
		System.out.println("Servidor de chat iniciado. Endereço: " + serverSocket.getInetAddress().getHostAddress()
				+ " na porta: " + PORT);

		clientConnectionLoop();

	}

	private void clientConnectionLoop() {
		try {
			while (true) {
				System.out.println("Aguardando de novo cliente");

				final ClientSocket clientSocket;
				try {
					clientSocket = new ClientSocket(serverSocket.accept());
					System.out.println("Cliente " + clientSocket.getRemoteSocketAddress() + " conectado");
				} catch (Exception e) {
					System.err.println(
							"Erro no aceite da conexao do cliente. O servidor está possivelmente sobrecarregado.");
					System.err.println(e.getMessage());
					continue;
				}
				try {

					new Thread(() -> clientMessageLoop(clientSocket)).start();

					clientSocketList.add(clientSocket);
				} catch (OutOfMemoryError e) {
					System.err.println("Não foi possivel criar thread para o cliente.");
					System.err.println(e.getMessage());
					clientSocket.close();
				}
			}
		} finally {
			stop();
		}
	}

	private void clientMessageLoop(final ClientSocket clientSocket) {
		try {
			String msg;
			while ((msg = clientSocket.getMessage()) != null) {
				System.out
						.println("Mensagem recebida do cliente " + clientSocket.getRemoteSocketAddress() + " : " + msg);
				if ("sair".equalsIgnoreCase(msg)) {
					return;
				}
				sendMsgAll(clientSocket, msg);
			}
		} finally {
			clientSocket.close();
		}
	}

	private void sendMsgAll(final ClientSocket sender, final String msg) {
		final Iterator<ClientSocket> iterator = clientSocketList.iterator();
		int count = 0;

		while (iterator.hasNext()) {
			final ClientSocket client = iterator.next();
			if (!client.equals(sender)) {
				if (client.sendMsg(msg)) {
					count++;
				} else {
					iterator.remove();
				}

			}

		}
		System.out.println("Mensagem encaminhada para " + count + " clientes");
	}

	private void stop() {
		try {
			System.out.println("Finalizando servidor");
			serverSocket.close();
		} catch (Exception e) {
			System.out.println("Erro: " + e.getMessage());
		}
	}

}
