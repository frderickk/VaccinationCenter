import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.ArrayList;

public class CentroVacunacion {
	
	private int capacidad;
	private String nombre;	
	private HashMap<Integer, Persona> inscriptos;
	private LinkedHashMap<Integer, Persona> turnos;
    private HashMap<Integer, String> vacunados;
    private LinkedList<Persona> prioridad; 
    private HeladeraVacunas heladeras;

          
	/**
	* Constructor del centro de vacunacion
	* recibe el nombre del centro y la capacidad de vacunación diaria.
	* Si la capacidad de vacunación no es positiva se debe generar una excepción.
	* Si el nombre no está definido, se debe generar una excepción.
	*/
	public CentroVacunacion(String nombreVacunatorio, int capacidadDiaria) {
		this.capacidad = capacidadDiaria;
		this.nombre = nombreVacunatorio;
		inscriptos = new HashMap<Integer, Persona>();
		turnos = new LinkedHashMap<Integer, Persona>();
		vacunados = new HashMap<Integer, String>();
		prioridad = new LinkedList<Persona>();
		heladeras = new HeladeraVacunas();
		if(capacidadDiaria <= 0) {
			throw new RuntimeException("El centro no tiene capacidad");
		}
		if(nombreVacunatorio == " ") {
			throw new RuntimeException("El centro necesita un nombre para el reconocimiento");
		}
	}

	
	/**
	* Solo se pueden ingresar los tipos de vacunas planteados en la 1ra parte.
	* Si el nombre de la vacuna no coincidiera con los especificados se debe generar
	* una excepción.
	* También se genera excepción si la cantidad es negativa.
	* La cantidad se debe
	* sumar al stock existente, tomando en cuenta las vacunas ya utilizadas.
	*/
	public void ingresarVacunas(String nombre, int cant, Fecha fechaDeEntrada) {
		heladeras.ingresarVacunas(nombre, cant, fechaDeEntrada);
	}
	
	
	/**
	* total de vacunas disponibles no vencidas sin distinción por tipo.
	*/
	public int vacunasDisponibles() {
		return heladeras.vacunasDisponibles();
	}
	
	
	/**
	* total de vacunas disponibles no vencidas que coincida con el nombre de
	* vacuna especificado.
	*/
	public int vacunasDisponibles(String nombre) {
		return heladeras.vacunasDisponibles(nombre);
	}
	
	
	/**
	* Se inscribe una persona en lista de espera.
	* Si la persona ya se encuentra inscripta o es menor de 18 años, se debe
	* generar una excepción.
	* Si la persona ya fue vacunada, también debe generar una excepción.
	*/
	public void inscribirPersona(int dni, Fecha fechaDeNacimiento, Boolean comorbilidad, Boolean trabajadorDeSalud) {
		if(inscriptos.containsKey(dni)) {
			throw new RuntimeException("Esta persona ya ha sido inscripta");
		}
		if(dni < 0000001 || dni > 99999999) {
			throw new RuntimeException("El dni de esta persona no es correcto");
		}
		 if (Fecha.diferenciaAnios(Fecha.hoy(), fechaDeNacimiento) < 18) {
			throw new RuntimeException("Esta persona es menor de edad");
		}
		 if(vacunados.containsKey(dni)) {
			 throw new RuntimeException("Esta persona ya ha sido vacunada");
		 }
		 else {
			 inscriptos.put(dni, new Persona(dni, fechaDeNacimiento, comorbilidad, trabajadorDeSalud));
			 definirPrioridad();
		 } 
	}
	
	
	/**
	 * Definimos la prioridad que el centro le da a las personas (se utiliza cuando inscribimos a estas)
	 */
	private void definirPrioridad() {
		for (int key : inscriptos.keySet()) {
			if(inscriptos.get(key).getTrabajadorDeSalud() == true) {
				inscriptos.get(key).setPrioridad(1);
			}
			else if(inscriptos.get(key).getComorbilidades() == true) {
				inscriptos.get(key).setPrioridad(3);
			}
			else if(inscriptos.get(key).edad() > 60) {
				inscriptos.get(key).setPrioridad(2);
			}
			else {
				inscriptos.get(key).setPrioridad(4);
			}
		}
	}

	
	/**
	* Devuelve una lista con los DNI de todos los inscriptos que no se vacunaron
	* y que no tienen turno asignado.
	* Si no quedan inscriptos sin vacunas debe devolver una lista vacía.
	*/
	List <Integer> listaDeEspera() {
		ArrayList<Integer> lista = new ArrayList<>();
		for (Integer p : inscriptos.keySet()) {
			lista.add(p);
		}
		return lista;
	}
	
	
	/**
	* Primero se verifica si hay turnos vencidos. En caso de haber turnos
	* vencidos, la persona que no asistió al turno debe ser borrada del sistema
	* y la vacuna reservada debe volver a estar disponible.
	*
	* Segundo, se deben verificar si hay vacunas vencidas y quitarlas del sistema.
	*
	* Por último, se procede a asignar los turnos a partir de la fecha inicial
	* recibida según lo especificado en la 1ra parte.
	* Cada vez que se registra un nuevo turno, la vacuna destinada a esa persona
	* dejará de estar disponible. Dado que estará reservada para ser aplicada
	* el día del turno.
	*/
	public void generarTurnos(Fecha fechaInicial) {
		removerPorfechaInvalida();
		// Utilizamos metodos de la Heladera para controlar el stock
		heladeras.moverVacunasVencidas();
		heladeras.quitarVacunaVencida();
		inscriptosOrdenados();
		// Si hay inscriptos con vacuna asignada
		while(prioridad.size() > 0) {
		asignarTurnos(fechaInicial);
			moverConTurnoAsignado();
		}
	}
	
	
	/**
	 * Primer paso para poder generarTurno()
	 * 		Verificamos si hay fechas validas caso contrario la elimnamos 
	 * 		y devolvemos la vacuna que habia sido asignada.
	 */
	private void removerPorfechaInvalida() {	
		Iterator<Map.Entry<Integer,Persona>> iterator=turnos.entrySet().iterator();
		while (iterator.hasNext()){
			Map.Entry<Integer,Persona> entry=iterator.next();
			if(Fecha.hoy().posterior(entry.getValue().getFecha())){
				heladeras.devolverVacuna(entry.getValue().getVacunaAsignada()); 
				iterator.remove();
			}
		}	
	}

	
	/**
	 * Segundo paso para generarTurno()
	 * 		Ordenamos a los inscriptos segun su prioridad.
	 */
	private void inscriptosOrdenados() {
		for (Persona p : inscriptos.values()) {	
			if(p.getPrioridad() == 1) {
				prioridad.addFirst(p);
			}
			else if(p.getPrioridad() == 2) {
				prioridad.add(p);
			}
			else if(p.getPrioridad() == 3) {
				prioridad.add(p);
			}
			else if(p.getPrioridad() == 4) {
				prioridad.add(p);
			}
		}
	}
		
	
	/**
	 * Tercer paso para generarTurno()
	 * 		Asignamos el turno para cada inscripto chequeando las fechas.
	 */
	private void asignarTurnos (Fecha fechaInicial) {
		Fecha f = new Fecha(fechaInicial);
		int cap = this.capacidad;
		chequearFecha(f);		
		for (int i = 0; i<prioridad.size(); i++) {
			if(cap > 0) {
				prioridad.get(i).setFecha(f);
				prioridad.get(i).setVacunaAsignada(heladeras.asignarVacunaDisponibles(prioridad.get(i).edad()));
				cap--;
			}
		}
	}

	
	/**
	 * Cuarto paso para generarTurno()
	 * 		Finalmente movemos a la persona de inscriptos al diccionario de turnos
	 */
	private void moverConTurnoAsignado() {
		Iterator<Persona> iterator=prioridad.iterator();
		while (iterator.hasNext()){
		Persona entry=iterator.next();
		if(entry.getFecha() != null){
			turnos.put(entry.getDni(), entry);
			iterator.remove();
			}
		}
		Iterator<Map.Entry<Integer,Persona>> iterator1=inscriptos.entrySet().iterator();
		while (iterator1.hasNext()){
			Map.Entry<Integer,Persona> entry=iterator1.next();
			if(entry.getValue().getFecha() != null){
				iterator1.remove();
			}
		}
	}
	

	/**
	 * Metodo que se utiliza en asignarTurno() para comparar la fecha
	 * del parametro con la fecha de hoy.
	 */
	public Fecha chequearFecha(Fecha fecha) {
		if(Fecha.hoy().posterior(fecha)) {
			throw new RuntimeException("No es una fecha válida");
		}
		for (int i = 0; i < turnos.size(); i++) {
			if(cantidadDeTurnosPorDia(fecha) == this.capacidad) {		
				fecha.avanzarUnDia();
			}
		}
		return new Fecha(fecha);
	}
	
	
	/**
	 * Metodo que utiliza chequearFecha() donde recorre 
	 * las fechas del diccionario de turnos.
	 */
	public int cantidadDeTurnosPorDia(Fecha fecha) {
		int turnosPorDia = 0;
		for (Integer key : turnos.keySet()) {
			if(turnos.get(key).getFecha().equals(fecha)) {
				turnosPorDia++;
			}
		}
		return turnosPorDia;
	}
	
	
	/**
	* Devuelve una lista con los dni de las personas que tienen turno asignado
	* para la fecha pasada por parámetro.
	* Si no hay turnos asignados para ese día, se debe devolver una lista vacía.
	* La cantidad de turnos no puede exceder la capacidad por día de la ungs.
	*/
	List<Integer> turnosConFecha(Fecha fecha) {
		Fecha f = new Fecha(fecha);
		ArrayList<Integer> lista = new ArrayList<>();
		for (Integer p : turnos.keySet()) {
			if(turnos.get(p).getFecha().equals(f))
			lista.add(p);
		}
		return lista;
	}
	
	
	/**
	* Dado el DNI de la persona y la fecha de vacunación
	* se valida que esté inscripto y que tenga turno para ese dia.
	* - Si tiene turno y está inscripto se debe registrar la persona como
	* vacunada y la vacuna se quita del depósito.
	* - Si no está inscripto o no tiene turno ese día, se genera una Excepcion.
	*/	
	public void vacunarInscripto(Integer dni, Fecha fechaVacunacion) {	
		if(turnos.get(dni) == null) {
			throw new RuntimeException("No está inscripto");
		}	
		else if(!turnos.get(dni).getFecha().equals(fechaVacunacion)) {
			throw new RuntimeException("La fecha de vacunación no corresponde con el día de hoy");
		}
		else  {
			heladeras.aplicarVacuna(turnos.get(dni).getVacunaAsignada());
			vacunados.put(dni,turnos.get(dni).getVacunaAsignada());
			heladeras.quitarVacunaAplicada(turnos.get(dni).getVacunaAsignada());
		}
	}
	
	
	/**
	* Devuelve un Diccionario donde
	* - la clave es el dni de las personas vacunadas
	* - Y, el valor es el nombre de la vacuna aplicada.
	*/
	Map<Integer, String> reporteVacunacion() {
		return vacunados;
	}
	
	
	/**
	* Devuelve en O(1) un Diccionario:
	* - clave: nombre de la vacuna
	* - valor: cantidad de vacunas vencidas conocidas hasta el momento.
	*/
	Map<String, Integer> reporteVacunasVencidas() {
		return heladeras.reporteVacunasVencidas();
	}
	
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("                      ***********************************\n");
		sb.append("                      -----------------------------------\n");
		sb.append("                           Centro de vacunacion " + nombre + "" + "\n");
		sb.append("                       Capacidad de vacunacion diaria: [" + capacidad + "]\n");
		sb.append("                      -----------------------------------\n");
		sb.append("                      ***********************************\n\n");
		sb.append("\n                      --------------Turnos---------------\n\n");
		for (Persona persona : turnos.values()) {
			sb.append("DNI n*: " + persona.getDni() + " con prioridad " + persona.getPrioridad() + " tiene turno para el dia: " + persona.getFecha() + " y se aplica la vacuna: " + persona.getVacunaAsignada() + "\n");
		}
		sb.append(heladeras);
		return sb.toString();
	}
}
