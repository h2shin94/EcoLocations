package uk.ac.cam.cl.foxtrot.ecolocations;

/**
 * Created by miteyan on 23/02/2017.
 */
import android.location.Location;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.maps.android.data.geojson.GeoJsonLayer;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.w3c.dom.Node;
import org.w3c.dom.Element;
import org.w3c.dom.Text;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import java.io.IOException;
import java.io.StringReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

public class FlickrUtils {

    private static final String APIKEY = "a36c06fbd6ccbe6bf7501defee9984fc";
//String testURL = "https://api.flickr.com/services/rest/?method=flickr.photos.search&api_key=906e25f0690cfbd53c7ddec7fede56f1&text=Coldhams+common&geo_context=&lat=52.205614&lon=0.150837radius=1&format=json&nojsoncallback=1&api_sig=1d50c2ed1d5959951679c461ede305b2";

    //get flickr xml url
    public static String getURL(String name, double lat, double lon, double radius) {
        name = URLEncoder.encode(name);
        return "https://api.flickr.com/services/rest/?method=flickr.photos.search&api_key=" +
                APIKEY + "&text=" + name + "&geo_context=&lat=" + lat + "&lon=" + lon + "&radius=" +
                radius + "&format=rest";
    }

    public static String getURL(EcoLocation loc) {
        return getURL(loc.getNAME(), loc.getLATITUDE(), loc.getLONGITUDE(), getRadius(loc));
    }

    private static double getRadius(EcoLocation loc) {
//        GeoJsonLayer layer = loc.getGeoJsonLayer();
//        if (layer == null) {
        double hackyRadius = getEstimatedRadius(loc.getGIS_AREA() + loc.getGIS_M_AREA());
        return Math.min(5, hackyRadius);
//        } else {
//            LatLngBounds bounds = layer.getBoundingBox();
//            Location startPoint = new Location("A");
//            startPoint.setLatitude(bounds.northeast.latitude);
//            startPoint.setLongitude(bounds.northeast.longitude);
//            Location endPoint = new Location("B");
//            endPoint.setLatitude(bounds.southwest.latitude);
//            endPoint.setLongitude(bounds.southwest.longitude);
//            return Math.min(startPoint.distanceTo(endPoint) / 1000, 5);
//        }
    }

    private static double getEstimatedRadius(double area) {
        return Math.sqrt(area / Math.PI);
    }

    public static String getImageURL(String farm, String server, String id, String secret) {
        return "https://farm" + farm + ".staticflickr.com/" + server + "/" + id + "_" + secret + ".jpg";
    }

    public static List<String> XMLParse(String flickrXml) {
        List<String> imageURLs = new ArrayList<>();
        String farm,id,secret,server;
        try {
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.parse(new InputSource(new StringReader(flickrXml)));
            doc.getDocumentElement().normalize();

            NodeList nList = doc.getElementsByTagName("photo");
            for (int temp = 0; temp < nList.getLength(); temp++) {
                Node nNode = nList.item(temp);
                if (nNode.getNodeType() == Node.ELEMENT_NODE) {
                    Element eElement = (Element) nNode;

                    farm = eElement.getAttribute("farm");
                    id = eElement.getAttribute("id");
                    secret = eElement.getAttribute("secret");
                    server = eElement.getAttribute("server");

                    imageURLs.add(getImageURL(farm,server,id,secret));
                }
            }
        } catch (ParserConfigurationException | SAXException | IOException e) {
            e.printStackTrace();
        }
        return imageURLs;
    }


    public static List<String> getImageURLs (String name, double lat, double lon, double radius) {
        return XMLParse(getURL(name,lat,lon,radius));
    }

    public static void main(String[] args) {
        //Inputs
        String name = "Coldhams+common";
        double lat = 52.205614;
        double lon = 0.150837;
        double radius = 0.5;

        List<String> urls = getImageURLs(name,lat,lon,radius);
        for (String u: urls) {
            System.out.println(u);
        }

    }
}
