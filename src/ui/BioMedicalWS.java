package ui;

import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.GenericEntity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.google.gson.Gson;

import dao.OLAP;


@Path("/biomedicalDa")
public class BioMedicalWS {
	//Sets the path to base URL + /biomedicalDA
	OLAP ob = new OLAP();
	

	  // This method is called if TEXT_PLAIN is request
	  @GET
	  @Produces(MediaType.TEXT_PLAIN)
	  public String sayPlainTextHello() {
	    return "Hello Jersey";
	  }

	  // This method is called if XML is request
	  @GET
	  @Path("/he1")
	  @Produces(MediaType.TEXT_XML)
	  public String sayXMLHello() {
	    return "<?xml version=\"1.0\"?>" + "<hello> Hello Jersey" + "</hello>";
	  }

	  // This method is called if HTML is request
	  @GET
	  @Produces(MediaType.TEXT_HTML)
	  public String sayHtmlHello() {
	    return "<html> " + "<title>" + "Hello Jersey" + "</title>"
	        + "<body><h1>" + "Hello Jersey" + "</body></h1>" + "</html> ";
	  }
	  
	  @GET
	  @Path("/diseaseUUIDExpression")
	  @Produces(MediaType.TEXT_PLAIN)
	  public String getDiseaseUUIDExpression()
	  {
		  Gson g = new Gson();
	    return g.toJson(ob.diseaseUUIDExpression());
	  }
	  
	  @GET
	  @Path("/diseaseGoidExpression")
	  @Produces(MediaType.TEXT_PLAIN)
	  public String getDiseaseGoidExpression()
	  {
	    Gson g = new Gson();
	    return g.toJson(ob.diseaseUUIDExpression());
	  }
	  
	  @GET
	  @Path("/diseaseUUIDExpressionRollup")
	  @Produces(MediaType.TEXT_PLAIN)
	  public String getDiseaseUUIDExpressionRollup()
	  {
		  Gson g = new Gson();
	    return g.toJson(ob.diseaseUUIDExpression());
	  }
	  
	  @GET
	  @Path("/diseaseGoidExpressionRollup")
	  @Produces(MediaType.TEXT_PLAIN)
	  public String getDiseaseGoidExpressionRollup()
	  {
	    Gson g = new Gson();
	    return g.toJson(ob.diseaseUUIDExpression());
	  }
	  
	  @GET
	  @Path("/hej")
	  @Produces(MediaType.TEXT_PLAIN)
	  public String getPersons()
	  {
		  final List<Person> persons = new ArrayList<Person>();
	    persons.add(new Person(1, "John Smith"));
	    persons.add(new Person(2, "Jane Smith"));
	    
	    
	    Gson g = new Gson();
	    return g.toJson(ob.testQuery());
//	    return Response.ok(persons).entity(new GenericEntity<List<Person>>(persons) {}).build();
	  }

}

class Person{
	int id ; String name;
	public Person(int id, String name){
		this.id = id; this.name = name;
		
	}
}
