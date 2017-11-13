package thefusionera.com.wifimoduletest;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

/**
 * Created by Neeraj Athalye on 27-Aug-17.
 */

public interface RetrofitService {

    @GET("/{parameter_name}={parameter_value}")
    Call<ResponseBody> getDeviceStatus(@Path("parameter_name") String parameterName, @Path("parameter_value") String parameterValue);

    @GET("/")
    Call<ResponseBody> getGeneralStatus();

    @GET("/{parameter_name}={parameter_intensity}")
    Call<ResponseBody> getDeviceIntensity(@Path("parameter_name") String parameterName, @Path("parameter_intensity") String parameterIntensity);




}
