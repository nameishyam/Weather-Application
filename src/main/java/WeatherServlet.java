import jakarta.servlet.ServletException;
import jakarta.servlet.http.*;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.*;
import java.util.*;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

public class WeatherServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		String apiKey = "a23ee117de065104ad8bf0c0526fc7e4";
		String city = request.getParameter("city");
		String apiUrl = "https://api.openweathermap.org/data/2.5/weather?q=" + city + "&appid=" + apiKey;
		@SuppressWarnings("deprecation")
		URL url = new URL(apiUrl);
		HttpURLConnection connection = (HttpURLConnection) url.openConnection();
		connection.setRequestMethod("GET");
		InputStream inputStream = connection.getInputStream();
		InputStreamReader reader = new InputStreamReader(inputStream);
		Scanner scanner = new Scanner(reader);
		StringBuilder responseContent = new StringBuilder();
		while (scanner.hasNext()) {
			responseContent.append(scanner.nextLine());
		}
		scanner.close();
		Gson gson = new Gson();
		JsonObject jsonObject = gson.fromJson(responseContent.toString(), JsonObject.class);
		long dateTimestamp = jsonObject.get("dt").getAsLong() * 1000;
		String date = new Date(dateTimestamp).toString();
		double temperatureKelvin = jsonObject.getAsJsonObject("main").get("temp").getAsDouble();
		int temperatureCelsius = (int) (temperatureKelvin - 273.15);
		int humidity = jsonObject.getAsJsonObject("main").get("humidity").getAsInt();
		double windSpeed = jsonObject.getAsJsonObject("wind").get("speed").getAsDouble();
		String weatherCondition = jsonObject.getAsJsonArray("weather").get(0).getAsJsonObject().get("main").getAsString();
		request.setAttribute("date", date);
		request.setAttribute("city", city);
		request.setAttribute("temperature", temperatureCelsius);
		request.setAttribute("weatherCondition", weatherCondition);
		request.setAttribute("humidity", humidity);
		request.setAttribute("windSpeed", windSpeed);
		request.setAttribute("weatherData", responseContent.toString());
		connection.disconnect();
		request.getRequestDispatcher("index.jsp").forward(request, response);
	}
}
