
public class Persona //implements Comparable<Persona> 
{
	//fecha de vacunacion depende la implementacion
	private String nombre, apellido;
	private boolean trabajadorDeSalud, comorbilidades, vacunado;
	private char prioridad;
	private String vacunaAsignada;
	private Fecha fechaDeTurno, fechaDeNacimiento;
	
	
	//Constructor para Persona
	public Persona(Fecha fecha2, Boolean salud, Boolean comorbilidad) {
		this.fechaDeNacimiento = fecha2;
		this.trabajadorDeSalud = salud;
		this.comorbilidades = comorbilidad;
	}

	public Fecha getFechaDeNacimiento() {
		return fechaDeNacimiento;
	}

	public void setFechaDeNacimiento(Fecha fechaDeNacimiento) {
		this.fechaDeNacimiento = fechaDeNacimiento;
	}

	public boolean isVacunado() {
		return vacunado;
	}

	public void setVacunado(boolean vacunado) {
		this.vacunado = vacunado;
	}

	public boolean getTrabajadorDeSalud() {
		return trabajadorDeSalud;
	}

	public void setTrabajadorDeSalud(boolean trabajadorDeSalud) {
		this.trabajadorDeSalud = trabajadorDeSalud;
	}

	public boolean getComorbilidades() {
		return comorbilidades;
	}

	public void setComorbilidades(boolean comorbilidades) {
		this.comorbilidades = comorbilidades;
	}

	public char getPrioridad() {
		return prioridad;
	}

	public void setPrioridad(char prioridad) {
		this.prioridad = prioridad;
	}
	
	public String getVacunaAsignada() {
		return vacunaAsignada;
	}

	public void setVacunaAsignada(String vacunaAsignada) {
		this.vacunaAsignada = vacunaAsignada;
	}
	
	public Fecha getFecha() {
		return fechaDeTurno;
	}

	public void setFecha(Fecha fecha) {
		this.fechaDeTurno = fecha;
	}
	
//	boolean esMenorDe60() {
//		if (this.fechaDeNacimiento < 60) {
//			return true;
//		}
//		return false;
//	}
	
//	void asignarVacuna(Integer dni) {
//		if(esMenorDe60()) {
//			System.out.println("Pfizer o Sputnik");
//		}
//		else {
//			System.out.println("Cualquiera");
//		}
//	}
	
//	Vacunas verVacunaAsignada() {
//		return vacunaAsignada.get(dni);
//	}
	
//	public int compareTo(Persona o) {
//		return o.getDni()-this.getDni();
//	}


	@Override
	public String toString() {
		return "" + nombre + " " + apellido  + ", "  + " años";
	}
	
}
