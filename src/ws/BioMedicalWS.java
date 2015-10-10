package ws;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.GenericEntity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.google.gson.Gson;

import dao.OLAP;
import dao.StatisticalAnalysis;


@Path("/biomedicalDa")
public class BioMedicalWS {
	//Sets the path to base URL + /biomedicalDA
	OLAP ob = new OLAP();
	StatisticalAnalysis sa = new StatisticalAnalysis();
	

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
	  @Path("/numOfPatientsWithDiseasesByDescription")
	  @Produces(MediaType.TEXT_PLAIN)
	  public String numOfPatientsWithDiseasesByDescription(@QueryParam("description") String description){
		  Gson g = new Gson();
		    return g.toJson(ob.numOfPatientsWithDiseasesByDescription(description));
	  }
	  @GET
	  @Path("/numOfPatientsWithDiseasesByName")
	  @Produces(MediaType.TEXT_PLAIN)
	  public String numOfPatientsWithDiseasesByName(@QueryParam("name") String name){
		  Gson g = new Gson();
		    return g.toJson(ob.numOfPatientsWithDiseasesByName(name));
	  }
	  @GET
	  @Path("/numOfPatientsWithDiseasesByType")
	  @Produces(MediaType.TEXT_PLAIN)
	  public String numOfPatientsWithDiseasesByType(@QueryParam("type") String type){
		  Gson g = new Gson();
		    return g.toJson(ob.numOfPatientsWithDiseasesByType(type));
	  }
	  
	  @GET
	  @Path("/listOfDrugsAppliedToPatientsWith")
	  @Produces(MediaType.TEXT_PLAIN)
	  public String listOfDrugsAppliedToPatientsWith(@QueryParam("diseaseDescription") String diseaseDescription){
		  Gson g = new Gson();
		    return g.toJson(ob.listOfDrugsAppliedToPatientsWith(diseaseDescription));
	  }
	  
	  @GET
	  @Path("/listMRNAExpressions")
	  @Produces(MediaType.TEXT_PLAIN)
	  public String listMRNAExpressions(@QueryParam("diseaseName") String diseaseName,
			  @QueryParam("clusterId") String clusterId,
			  @QueryParam("muId") String muId){
		  Gson g = new Gson();
		    return g.toJson(ob.listMRNAExpressions(diseaseName,clusterId,muId));
	  }
	  
	  @GET
	  @Path("/runTtestOneVsAll")
	  @Produces(MediaType.TEXT_PLAIN)
	  public String runTtestOneVsAll(@QueryParam("diseaseName") String diseaseName,
			  @QueryParam("goId") int goId){
		  Gson g = new Gson();
		    return g.toJson(sa.runTtestOneVsAll(diseaseName,goId));
	  }
	  
	  @GET
	  @Path("/runFtest")
	  @Produces(MediaType.TEXT_PLAIN)
	  public String runFtest(@QueryParam("diseaseName1") String diseaseName1,
			  @QueryParam("diseaseName2") String diseaseName2,
			  @QueryParam("diseaseName3") String diseaseName3,
			  @QueryParam("diseaseName4") String diseaseName4,
			  @QueryParam("goId") int goId){
		  Gson g = new Gson();
		    return g.toJson(sa.runFtest(new ArrayList<String>(Arrays.asList(diseaseName1,diseaseName2,diseaseName3,diseaseName4)),goId));
	  }
	  
	  @GET
	  @Path("/calculateAveragePearsonCorrelation1")
	  @Produces(MediaType.TEXT_PLAIN)
	  public String calculateAveragePearsonCorrelation1(@QueryParam("diseaseName") String diseaseName, @QueryParam("goId") int goId){
		  Gson g = new Gson();
		    return g.toJson(sa.calculateAveragePearsonCorrelation(diseaseName,goId));
	  }
	  
	  @GET
	  @Path("/calculateAveragePearsonCorrelation2")
	  @Produces(MediaType.TEXT_PLAIN)
	  public String calculateAveragePearsonCorrelation2(@QueryParam("diseaseName1") String diseaseName1,
			  @QueryParam("diseaseName2") String diseaseName2, @QueryParam("goId") int goId){
		  Gson g = new Gson();
		    return g.toJson(sa.calculateAveragePearsonCorrelation(diseaseName1,diseaseName2 ,goId));
	  }
	  
	  
	  
	  
}


