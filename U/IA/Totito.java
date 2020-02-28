import java.util.Scanner;
import java.util.Arrays;
import java.util.Random;
import java.util.List;
import java.util.ArrayList;
import java.util.function.Consumer;
import java.util.Map;
import java.util.HashMap;
import java.util.stream.Collectors;
import java.util.Comparator;

public class Totito {

	private static Scanner sn = new Scanner(System.in);
	private static Random r = new Random();
	private static List<LogGanador> winnersHistory = new ArrayList<>();
	private static Consumer<Object> print = x -> System.out.println(x);

	public static void main(String... args) {

		print.accept("Bienvenido a totito machine learning");
		print.accept("Ingresa el valor que deseas utilizar X / O");
		String signoUsuario = sn.nextLine().toUpperCase();
		print.accept("Haz escogido " + signoUsuario);
		String signoMaquina = signoUsuario.equals("X") ? "O" : "X";
		int posicion = 0;
		String volverAJugar = "N";

		do {
			String[] values = { " ", " ", " ", " ", " ", " ", " ", " ", " " };
			pintarTotito(values);
			String turno = generaRandom(1, 2) == 1 ? "M" : "U";
			for (int x = 0; x < values.length; x++) {
				String poS = "";
				if (turno.equals("U")) {
					do {
						print.accept("Ingresa la posicion donde deseas colocar 1-9");
						poS = sn.nextLine();
					} while (!esEntero(poS) || (Integer.parseInt(poS) < 1 || Integer.parseInt(poS) > 9)
							|| !values[Integer.parseInt(poS) - 1].trim().equals(""));
					posicion = Integer.parseInt(poS);
					values[posicion - 1] = signoUsuario;
				} else {
					print.accept("Es mi turno :)");
					do {
						posicion = getBestOption(signoMaquina, signoUsuario, values);
						// print.accept(posicion);
					} while (!values[posicion - 1].trim().equals(""));
					values[posicion - 1] = signoMaquina;
				}
				pintarTotito(values);
				if (haGanado(values)) {
					if (turno.equals("U")) {
						print.accept("Ohhh nooo he perdido :(, felicidades!!!");
						agregarLogGanador(values, signoUsuario);
					} else {
						print.accept("Ohhh SIIIII te he derrotado :)");
						agregarLogGanador(values, signoMaquina);
					}
					break;
				}
				turno = turno.equals("U") ? "M" : "U";
			}
			print.accept("He aprendido un poco de nuestro juego ^^)");
			print.accept("Te gustaria volver a jugar? Y/N ");
			volverAJugar = sn.nextLine();
		} while (volverAJugar.equalsIgnoreCase("Y"));
	}

	private static int getBestOption(String charMachine, String charUser, String[] values) {
		String[] routeMachine = setGeneralFlow(charMachine, values);
		String[] routeUser = setGeneralFlow(charUser, values);

		if (winnersHistory.size() == 0) {
			return generaRandom(1, 9);
		} else {
			if (Arrays.asList(routeUser).stream().filter(v -> v.equals("W")).findFirst().orElse(null) != null) {

				List<LogGanador> winnersTemmp = winnersHistory.stream().filter(w -> {
					for (int i = 0; i < w.getValores().length; i++) {
						if (w.getValores()[i].equals(routeUser[i]) && routeUser[i].equals("W")) {
							return true;
						}
					}
					return false;
				}).collect(Collectors.toList());
				//System.out.println(winnersTemmp);
				winnersTemmp.sort((h1, h2) -> {
					double p1 = 0d, p2 = 0d;
					for (int po = 0; po < routeUser.length; po++) {
						if (routeUser[po].equals("W") && routeUser[po].equals(h1.getValores()[po])) {
							p1++;
						}
						if (routeUser[po].equals("W") && routeUser[po].equals(h2.getValores()[po])) {
							p2++;
						}
					}
					p1 = p1 / h1.getValores().length;
					p2 = p2 / h2.getValores().length;
					return (int) ((p2 - p1) * 100) + (h2.getCantidad() - h1.getCantidad()) ;
				});
				//System.out.println(winnersTemmp);
				//LogGanador logPivot = winnersTemmp.stream().findFirst().orElse(null);
				int posicion = generaRandom(1, 9);
				for(LogGanador logPivot : winnersTemmp){
					if (logPivot != null) {
						for (int x = 0; x < logPivot.getValores().length; x++) {
							if (logPivot.getValores()[x].equals("W") && routeUser[x].equals(" ")) {
								String[] testWinner = routeUser.clone();
								testWinner[x] = "W";
								if(haGanado(testWinner)){
									return x + 1;
								}
								posicion = x + 1;
							}
						}
						
					}
				}
				return posicion;
			}
			return generaRandom(1, 9);
		}
	}

	/*private static void posibleRutaGanadora(String... values){

		

		0,1,2
		3,4,5
		6,7,8

		0,3,6
		1,4,7
		2,5,8

		0,4,8
		2,5,6	


	}*/

	private static String[] setGeneralFlow(String character, String[] values) {
		String[] internalValues = new String[9];
		for (int i = 0; i < values.length; i++) {
			if (values[i].equalsIgnoreCase(character)) {
				internalValues[i] = "W";
			} else if (!values[i].equalsIgnoreCase(" ")) {
				internalValues[i] = "L";
			} else {
				internalValues[i] = " ";
			}
		}
		return internalValues;
	}

	private static void pintarTotito(String... items) {
		System.out.println();
		print.accept("1 " + items[0] + " |2 " + items[1] + " |3 " + items[2] + " ");
		print.accept("----|----|----");
		print.accept("4 " + items[3] + " |5 " + items[4] + " |6 " + items[5] + " ");
		print.accept("----|----|----");
		print.accept("7 " + items[6] + " |8 " + items[7] + " |9 " + items[8] + " ");
		System.out.println();
	}

	private static boolean haGanado(String... items) {
		return (
		// Horizontales
		items[0].trim().equals(items[1].trim()) && items[0].trim().equals(items[2].trim())
				&& !(items[0] + items[1] + items[2]).trim().equals(""))
				|| (items[3].trim().equals(items[4].trim()) && items[3].trim().equals(items[5].trim())
						&& !(items[3] + items[4] + items[5]).trim().equals(""))
				|| (items[6].trim().equals(items[7].trim()) && items[6].trim().equals(items[8].trim())
						&& !(items[6] + items[7] + items[8]).trim().equals(""))
				// Verticales
				|| (items[0].trim().equals(items[3].trim()) && items[0].trim().equals(items[6].trim())
						&& !(items[0] + items[3] + items[6]).trim().equals(""))
				|| (items[1].trim().equals(items[4].trim()) && items[1].trim().equals(items[7].trim())
						&& !(items[1] + items[4] + items[7]).trim().equals(""))
				|| (items[2].trim().equals(items[5].trim()) && items[2].trim().equals(items[8].trim())
						&& !(items[2] + items[5] + items[8]).trim().equals(""))
				// Diagonal
				|| (items[0].trim().equals(items[4].trim()) && items[0].trim().equals(items[8].trim())
						&& !(items[0] + items[4] + items[8]).trim().equals(""))
				|| (items[2].trim().equals(items[4].trim()) && items[2].trim().equals(items[6].trim())
						&& !(items[2] + items[4] + items[6]).trim().equals(""));
	}

	private static boolean esEntero(String value) {
		try {
			Integer.parseInt(value);
			return true;
		} catch (Exception e) {
			print.accept("Valor " + value + " no es entero no me quieras timar");
			return false;
		}
	}

	private static int generaRandom(int inicio, int fin) {
		int random = r.nextInt(fin - inicio + 1) + inicio;
		return random;
	}

	private static void agregarLogGanador(String[] valores, String signoGanador) {
		for (int i = 0; i < valores.length; i++) {
			if (valores[i].equalsIgnoreCase(signoGanador)) {
				valores[i] = "W";
			} else {
				valores[i] = "L";
			}
		}
		LogGanador logGanador = new LogGanador();
		logGanador.setValores(valores);
		logGanador.setCantidad(1);

		LogGanador oldLog = winnersHistory.stream()
				.filter(l -> Arrays.toString(l.getValores()).equalsIgnoreCase(Arrays.toString(valores))).findFirst()
				.orElse(null);

		if (oldLog != null) {
			oldLog.setCantidad(oldLog.getCantidad() + 1);
		} else {
			winnersHistory.add(logGanador);
		}
	}
}

class LogGanador {

	LogGanador() {
	}

	LogGanador(String[] valores, int cantidad) {
		this.valores = valores;
		this.cantidad = cantidad;
	};

	private String[] valores;
	private int cantidad;

	public void setCantidad(int cantidad) {
		this.cantidad = cantidad;
	}

	public int getCantidad() {
		return this.cantidad;
	}

	public void setValores(String[] valores) {
		this.valores = valores;
	}

	public String[] getValores() {
		return this.valores;
	}

	public String toString() {
		return Arrays.toString(valores) + " - " + cantidad;
	}
}
