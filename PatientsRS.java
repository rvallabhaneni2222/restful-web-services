package drpatients;

import java.io.InputStream;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.DELETE;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.FormParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Context;
import javax.servlet.ServletContext;
import com.fasterxml.jackson.databind.ObjectMapper;

@Path("/")
public class PatientsRS {
    @Context 
    private ServletContext sctx;          // dependency injection
    private static PatientsList dlist; // set in populate()
	Patient xyz = new Patient();
    String[] parts;
    public PatientsRS() { }

    @GET
    @Path("/xml")
    @Produces({MediaType.APPLICATION_XML}) 
    public Response getXml() {
	checkContext();
	return Response.ok(dlist, "application/xml").build();
    }

    @GET
    @Path("/xml/{id: \\d+}")
    @Produces({MediaType.APPLICATION_XML}) // could use "application/xml" instead
    public Response getXml(@PathParam("id") int id) {
	checkContext();
	return toRequestedType(id, "application/xml");
    }

    @GET
    @Produces({MediaType.APPLICATION_JSON})
    @Path("/json")
    public Response getJson() {
	checkContext();
	return Response.ok(toJson(dlist), "application/json").build();
    }

    @GET    
    @Produces({MediaType.APPLICATION_JSON})
    @Path("/json/{id: \\d+}")
    public Response getJson(@PathParam("id") int id) {
	checkContext();
	return toRequestedType(id, "application/json");
    }

    @GET
    @Path("/plain")
    @Produces({MediaType.TEXT_PLAIN}) 
    public String getPlain() {
	checkContext();
	return dlist.toString();
    }

	 @GET    
    @Produces({MediaType.TEXT_PLAIN})
    @Path("/plain/{id: \\d+}")
    public String getPlain(@PathParam("id") int id) {
	checkContext();
	 xyz = dlist.find(id);
		return xyz.toString();
    }

	
    @POST
    @Produces({MediaType.TEXT_PLAIN})
    @Path("/create")
    public Response create(@FormParam("doctor") String doctor, 
			   @FormParam("first") String first,@FormParam("second") String second,@FormParam("third") String third) {
	checkContext();
	String msg = null;
	// Require both properties to create.
	if (doctor == null || first == null || second ==  null ||third == null) {
	    msg = "Property 'doctor' or 'first' or 'second' is missing.\n";
	    return Response.status(Response.Status.BAD_REQUEST).
		                                   entity(msg).
		                                   type(MediaType.TEXT_PLAIN).
		                                   build();
	}	    
	// Otherwise, create the Patient and add it to the collection.
	int id = addPatient(doctor, first,second,third);
	msg = "Patient " + id + " created: (doctor = " + doctor + " first = " + first + "second = " + second+"third = "+third +").\n";
	return Response.ok(msg, "text/plain").build();
    }

    @PUT
    @Produces({MediaType.TEXT_PLAIN})
    @Path("/update")
    public Response update(@FormParam("id") int id,
			   @FormParam("doctor") String doctor, 
			   @FormParam("first") String first,
                   @FormParam("second") String second,
						   @FormParam("third") String third) {
	checkContext();

	// Check that sufficient data are present to do an edit.
	String msg = null;
	if (doctor == null && first == null && second == null && third == null) 
	    msg = "Neither doctor nor first is given: nothing to edit.\n";

	Patient d = dlist.find(id);
	if (d == null)
	    msg = "There is no patient with ID " + id + "\n";

	if (msg != null)
	    return Response.status(Response.Status.BAD_REQUEST).
		                                   entity(msg).
		                                   type(MediaType.TEXT_PLAIN).
		                                   build();
	// Update.
	if (doctor != null) d.setDoctor(doctor);
	if (first != null) d.setFirst(first);
	if (second != null) d.setSecond(second);
	if (third != null) d.setThird(third);

msg = "Patient " + id + " has been updated.\n";
	return Response.ok(msg, "text/plain").build();
    }

    @DELETE
    @Produces({MediaType.TEXT_PLAIN})
    @Path("/delete/{id: \\d+}")
    public Response delete(@PathParam("id") int id) {
	checkContext();
	String msg = null;
	Patient d = dlist.find(id);
	if (d == null) {
	    msg = "There is no patient with ID " + id + ". Cannot delete.\n";
	    return Response.status(Response.Status.BAD_REQUEST).
		                                   entity(msg).
		                                   type(MediaType.TEXT_PLAIN).
		                                   build();
	}
	dlist.getPatients().remove(d);
	msg = "Patient " + id + " deleted.\n";

	return Response.ok(msg, "text/plain").build();
    }

    //** utilities
    private void checkContext() {
	if (dlist == null) populate();
    }

    private void populate() {
	dlist = new PatientsList();

	String filename = "/WEB-INF/data/drs.db";
	InputStream in = sctx.getResourceAsStream(filename);
	
	String filename2 = "/WEB-INF/data/patients.db";
	InputStream intwo = sctx.getResourceAsStream(filename2);
	
	
	// Read the data into the array of Patients. 
	if (in != null) {
	    try {
		BufferedReader reader = new BufferedReader(new InputStreamReader(in));
		BufferedReader reader2 = new BufferedReader(new InputStreamReader(intwo));
		int i = 0;int a = 0;
		String record = null;
		String  record2 =null;
		
		
		
		while ((record2 = reader2.readLine()) != null && (record = reader.readLine()) != null) {
		    String[] parts2 = record2.split("!");
		    parts = record.split("!");
		    addPatient(parts[0], parts2[0],parts2[1],parts2[2]);
		    
		}
		
	    }
		
	    catch (Exception e) { 
		throw new RuntimeException("I/O failed!"); 
	    }
	}
    }

    // Add a new patient to the list.
    private int addPatient(String doctor, String first , String second,String third) {
	int id = dlist.add(doctor, first, second,third);
	return id;
    }

    // Patient --> JSON document
    private String toJson(Patient patient) {
	String json = "If you see this, there's a problem.";
	try {
	    json = new ObjectMapper().writeValueAsString(patient);
	}
	catch(Exception e) { }
	return json;
    }

    // PatientsList --> JSON document
    private String toJson(PatientsList dlist) {
	String json = "If you see this, there's a problem.";
	try {
	    json = new ObjectMapper().writeValueAsString(dlist);
	}
	catch(Exception e) { }
	return json;
    }

    // Generate an HTTP error response or typed OK response.
    private Response toRequestedType(int id, String type) {
	Patient pl = dlist.find(id);
	if (pl == null) {
	    String msg = id + " is a bad ID.\n";
	    return Response.status(Response.Status.BAD_REQUEST).
		                                   entity(msg).
		                                   type(MediaType.TEXT_PLAIN).
		                                   build();
	}
	else if (type.contains("json"))
	    return Response.ok(toJson(pl), type).build();
	else
	    return Response.ok(pl, type).build(); // toXml is automatic
    }
}



