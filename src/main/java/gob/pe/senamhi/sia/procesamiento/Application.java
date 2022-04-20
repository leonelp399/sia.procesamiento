package gob.pe.senamhi.sia.procesamiento;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
//import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;

import com.google.gson.Gson;

import gob.pe.senamhi.sia.procesamiento.Beans.Producto;


@SpringBootApplication
public class Application {

	private static final Logger LOGGER = LogManager.getLogger(Application.class);
	
	public static void main(String[] args) {
		SpringApplication.run(Application.class, args);
		try {
			List<Producto> list = getProductos();
			for(Producto p : list) {
				LOGGER.info("#######################");
				LOGGER.info("Producto ->"+ new Gson().toJson(p));
				generarLugaresAfectados(p.getEsquema(),p.getTabla());
				LOGGER.info("-----------------------");
				insertarImagen(p.getEsquema(),p.getTabla());
				LOGGER.info("#######################");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static List<Producto> getProductos() throws Exception{
		List<Producto> list = new ArrayList<Producto>();
		try {
			ResourceBundle rs = ResourceBundle.getBundle("config");
			Integer connect = Integer.parseInt(rs.getString("time.connect"));
			Integer read = Integer.parseInt(rs.getString("time.read"));
			RestTemplateBuilder builder = new RestTemplateBuilder();
			ResponseEntity<Producto[]> responseEntity = builder.setConnectTimeout(Duration.ofMillis(connect)).setReadTimeout(Duration.ofMillis(read)).build()
																.getForEntity(rs.getString("producto.service"), Producto[].class);
			 list = Arrays.asList(responseEntity.getBody());
		} catch (Exception e) {
			LOGGER.error("metodo Productos  ->" + e.getMessage());
			e.printStackTrace();
		}
		return list;
	}
	
	public static void generarLugaresAfectados(String esquema, String tabla) throws Exception{
		LOGGER.info("======== INICIO DE PROCESAMIENTO - LUGARES AFECTADOS ========");
		try {
			ResourceBundle rs = ResourceBundle.getBundle("config");
			Integer connect = Integer.parseInt(rs.getString("time.connect"));
			Integer read = Integer.parseInt(rs.getString("time.read"));
			Map<String, String> vars = new HashMap<String, String>();
			vars.put("esquema", esquema);
			vars.put("tabla", tabla);
			RestTemplateBuilder builder = new RestTemplateBuilder();
			builder.setConnectTimeout(Duration.ofMillis(connect)).setReadTimeout(Duration.ofMillis(read)).build()
																.getForEntity(rs.getString("lugares.afectados.service"), String.class,vars);
			LOGGER.info("EXICTO!");
		} catch (Exception e) {
//			e.printStackTrace();
//			LOGGER.error("metodo 2  ->" + e.getMessage());
			LOGGER.info("NO GENERO FICHA!");
		}
		LOGGER.info("======== FIN DE PROCESAMIENTO - LUGARES AFECTADOS ========");
	}
	
	public static void insertarImagen(String esquema, String tabla) throws Exception{
		LOGGER.info("======== INICIO DE PROCESAMIENTO - INSERTAR IMAGEN ========");
		try {
			ResourceBundle rs = ResourceBundle.getBundle("config");
			Integer connect = Integer.parseInt(rs.getString("time.connect"));
			Integer read = Integer.parseInt(rs.getString("time.read"));
			Map<String, String> vars = new HashMap<String, String>();
			vars.put("esquema", esquema);
			vars.put("tabla", tabla);
			RestTemplateBuilder builder = new RestTemplateBuilder();
			builder.setConnectTimeout(Duration.ofMillis(connect)).setReadTimeout(Duration.ofMillis(read)).build()
																.getForEntity(rs.getString("imagen.service"), String.class,vars);
			LOGGER.info("EXICTO!");
		} catch (Exception e) {
//			e.printStackTrace();
//			LOGGER.error("metodo 2  ->" + e.getMessage());
			LOGGER.info("NO REGISTRO IMAGEN!");
		}
		LOGGER.info("======== FIN DE PROCESAMIENTO - INSERTAR IMAGEN ========");
	}
	
	

}
