package core;

public class Start {
	public static void main(String[] args) {
		Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {
			public void run() {
				if (SQL.is_connected) {
					SQL.gI().close();
					System.out.println("SERVER STOPPED!");
				}
			}
		}));
		ServerManager.gI().init();
	}
}
