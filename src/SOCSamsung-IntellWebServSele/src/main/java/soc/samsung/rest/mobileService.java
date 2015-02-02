package soc.samsung.rest;


import javax.ws.rs.*;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import soc.samsung.dto.*;
import soc.samsung.context.UserContext;
import soc.samsung.discovery.ServicesData;
import soc.samsung.po.serviceTrustPO;

@Path("/")
public class mobileService {

	private HashMap<String, List<Point>> verifyPoints;
	private HashMap<String, HashMap<String, List<Double>>> underEvaluation;
	private List<serviceTrustPO> serviceTrust;
	private UserContext context;
	
	public mobileService() {
		State state = State.getInstance();
		verifyPoints = state.verifyPoints;
		underEvaluation = state.underEvaluation;
		serviceTrust = state.serviceTrust;
		context = state.context;
	}
	

    @POST
    @Consumes("application/json")
    @Produces("application/json")
    @Path("/register")
    public Behavior registerStreet(StreetRegistration street) {
        String streetName = street.getStreetName();
        Behavior behavior = new Behavior();
        if (verifyPoints.containsKey(streetName)) {
        	System.out.println("**** A user registered for " + streetName + " ******");
        	System.out.println("**** System has started service Verification ******");
        	behavior.setBehavior("evaluate");
        	behavior.setVerificationPoints(verifyPoints.get(streetName));
        } else {
        	System.out.println("**** No previous data found for " + streetName + ", Sampling road ******");
        	behavior.setBehavior("sample");
        	behavior.setVerificationPoints(null);
        }
        return behavior;
    }

    
    @POST
    @Consumes("application/json")
    @Produces("application/json")
    @Path("/recommendation")
    public Recommendation recommendation(Evaluation evaluation) {
        System.out.println("**** Recommendation Requested ******");
        
        String streetName = evaluation.getStreetName();


        /* Find the item with the maximum trust value */
        double max_trust = 0;
        serviceTrustPO max_item = serviceTrust.get(0);

        for (serviceTrustPO item : serviceTrust) {
            if (item.getServiceTrustValue() >= max_trust) {
                max_trust = item.getServiceTrustValue();
                max_item = item;
            }
        }

        Recommendation recommend =  new Recommendation();

        /* Manually reconstruct the segment */
        StreetSegment segment = new StreetSegment();
        Point a = new Point(evaluation.getStartlong(), evaluation.getStartlat());
        Point b = new Point(evaluation.getEndlong(), evaluation.getEndlat());
        segment.setPointA(a);
        segment.setPointB(b);

        System.out.println("**** Recommendation Provided: " + max_item.getServiceName() + " ******");

        ServicesData resultData = new ServicesData();
        resultData.getServiceData(max_item, segment, context);

        recommend.setRecommendedURI(resultData.getServiceUrl());
        recommend.setServiceName(max_item.getServiceName());
        return recommend;
    }

    @POST
    @Consumes("application/json")
    @Path("/evaluate")
    public Response evaluate(Evaluation evaluation) {

        StreetSegment segment = new StreetSegment();
        Point a = new Point(evaluation.getStartlong(), evaluation.getStartlat());
        Point b = new Point(evaluation.getEndlong(), evaluation.getEndlat());
        segment.setPointA(a);
        segment.setPointB(b);

         


    	String streetName = evaluation.getStreetName();
    	Double duration = (double) (evaluation.getMilliseconds()/1000) / 60 ; // minutes
        System.out.println("**** Received evaluation for " + streetName + " *****");
    	System.out.println("-- The segment was measured at " + evaluation.getMilliseconds() + " ms --");

        /* insert evaluation in data structure */
    	if (!underEvaluation.containsKey(streetName)) {
    		underEvaluation.put(streetName, new HashMap<String, List<Double>>());
    	}
		HashMap<String, List<Double>> map = underEvaluation.get(streetName);

		List<Double> list = new ArrayList<Double>();

    	for (serviceTrustPO service : serviceTrust) {
            ServicesData resultData = new ServicesData();
            resultData.getServiceData(service, segment, context);
    		list.add(resultData.getDurationForSegment());
    	}

        System.out.println("Services responded with the following times:");
        System.out.println(list);
		map.put(segment.toString(), list);

    	/* Evaluation Logic */
    	list = underEvaluation.get(streetName).get(segment.toString());
        if (list != null) {
            System.out.println(list);

            /* Service Evaluation */
            for (int i=0; i < list.size(); i++) {
                serviceTrust.get(i).setServiceTrustValue(((100 - Math.abs(duration - list.get(i))) + (serviceTrust.get(i).getServiceTrustValue())) /2 );
            }
            System.out.println("Service trust updated: (Bing, Google, MapQuest)");
            for (serviceTrustPO item : serviceTrust) {
                System.out.println(item.getServiceName() + " " + Double.toString(item.getServiceTrustValue()));
            }

            return ok();
        } else {
            System.out.println("WARN: no service data was found for this segment");
            return ok();
        }
    }

    @POST
    @Consumes("application/json")
    @Path("/streetsample")
    public Response submitPoint(StreetSample sample) {
      	String street = sample.getStreetName();
    	System.out.println("*** New Street Sample received for " + street + " *****");
    	Point samplePoint = sample.getSample();
        System.out.println(" Long: " + Double.toString(samplePoint.getLongitude()) + " Lat: " + Double.toString(samplePoint.getLatitude()));
    	if (verifyPoints.containsKey(street)) {
    		verifyPoints.get(street).add(samplePoint);
    	} else {
    		ArrayList<Point> newList = new ArrayList<Point>();
    		newList.add(samplePoint);
    		verifyPoints.put(street, newList);
    	}
        return ok();
    }

    protected Response ok(Object obj) {
        return Response.status(Status.OK).entity(obj).build();
    }

    protected Response ok() {
        return Response.status(Status.OK).build();
    }
}
