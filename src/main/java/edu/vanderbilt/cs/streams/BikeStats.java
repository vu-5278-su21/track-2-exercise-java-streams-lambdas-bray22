package edu.vanderbilt.cs.streams;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import edu.vanderbilt.cs.streams.BikeRide.DataFrame;
import edu.vanderbilt.cs.streams.BikeRide.LatLng;

public class BikeStats {

    private BikeRide ride;

    public BikeStats(BikeRide ride) {
        this.ride = ride;
    }

    /**
     * @ToDo:
     *
     * Create a stream of DataFrames representing the average of the
     * sliding windows generated from the given window size.
     *
     * For example, if a windowSize of 3 was provided, the BikeRide.DataFrames
     * would be fetched with the BikeRide.fusedFramesStream() method. These
     * frames would be divided into sliding windows of size 3 using the
     * StreamUtils.slidingWindow() method. Each sliding window would be a
     * list of 3 DataFrame objects. You would produce a new DataFrame for
     * each window by averaging the grade, altitude, velocity, and heart
     * rate for the 3 DataFrame objects.
     *
     * For each window, you should use the coordinate of the first DataFrame in the window
     * for the location.
     *
     * @param windowSize
     * @return
     */
    public Stream<BikeRide.DataFrame> averagedDataFrameStream(int windowSize){
    	List<DataFrame> dataFrameList = this.ride.fusedFramesStream().collect(Collectors.toList());
    	Stream<List<DataFrame>> streamList  = StreamUtils.slidingWindow(dataFrameList, windowSize);
    	List<DataFrame> dataFrames = new ArrayList<DataFrame>();
    	
    	streamList.forEach(arr -> {
    		double grade = 0.0, altitude = 0.0, velocity = 0.0, heartRate = 0.0;
    		for (int i=0; i<arr.size(); i++) {
    			grade += arr.get(i).grade;
    			altitude += arr.get(i).altitude;
    			velocity += arr.get(i).velocity;
    			heartRate += arr.get(i).heartRate;
    		}
    		
    		velocity = velocity/windowSize;
    		DecimalFormat df = new DecimalFormat("#.###");   
    		velocity = Double.valueOf(df.format(velocity));
    		
    	
    		DataFrame averageDataFrame = new BikeRide.DataFrame(
    				arr.get(0).coordinate, 
    				grade, 
    				altitude, 
    				velocity, 
    				heartRate);
    		
    		dataFrames.add(averageDataFrame);
    	});
    	
    	return dataFrames.stream();
    }

    // @ToDo:
    //
    // Determine the stream of unique locations that the
    // rider stopped. A location is unique if there are no
    // other stops at the same latitude / longitude.
    // The rider is stopped if velocity = 0.
    //
    // For the purposes of this assignment, you should use
    // LatLng.equals() to determine if two locations are
    // the same.
    //
    public Stream<LatLng> locationsOfStops() {
    	ArrayList<LatLng> coordinatesList = new ArrayList<LatLng>();
    	List<DataFrame> dataFrameList = this.ride.fusedFramesStream().filter(
    		dataFrame -> dataFrame.velocity == 0).collect(Collectors.toList()
    	);
    	
    	dataFrameList.forEach(dataFrame -> { coordinatesList.add(dataFrame.coordinate); });

    	for (int i=0; i<dataFrameList.size(); i++) {
    		for (int j=i+1; j<dataFrameList.size(); j++) { 
    			if (dataFrameList.get(i).coordinate.equals(dataFrameList.get(j).coordinate)) {
    				dataFrameList.remove(i);
    			}
    			
    		}
    	}
    	
        return coordinatesList.stream();
    }

}
